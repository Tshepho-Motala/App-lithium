package lithium.service.accounting.provider.internal.stream;

import lithium.service.accounting.objects.AuxLabelStreamData;
import lithium.service.accounting.provider.internal.conditional.NotReadOnlyConditional;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.services.QueueRateLimiter;
import lithium.service.accounting.provider.internal.services.TransactionService;
import lithium.stream.IDeadLetterQueueHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Data
@EnableBinding(AuxLabelQueueSink.class)
@Conditional(NotReadOnlyConditional.class)
public class AuxLabelQueueProcessor implements IDeadLetterQueueHandler {
	@Autowired TransactionService transactionService;
	@Autowired ModelMapper mapper;
	@Autowired RabbitTemplate rabbitTemplate;
	@Autowired QueueRateLimiter queueRateLimiter;

	/**
	 * This is the amount of times that it will be republished to the original queue, before it is retired to the parkingLot.
	 * ie. Lets assume Msg A will always fail. And maxAttempts is 2 and dlqRetries is 1. Msg A will enter the queue, be retried 2 times, then
	 * enters DLQ for the first time. DLQ republishes it back to original queue. It is retried 2 times. Enters DLQ again, but is then sent to
	 * the parking lot, to be handled with manual intervention.
	 */
	@Value("${lithium.service.accounting.provider.internal.dlqRetries:3}")
	private int maxDlqRetries;
	private final String parkingLotQueueName = AuxLabelQueueSink.PARKING_LOT;

	/**
	 * Very specific queue that will hopefully never contain anything, but it is better that it ends up here than the current implementation sending
	 * it to the great big black hole, never to be seen again..
	 * This queue will be monitored (I hope), and if anything ever ends up here, then we will need to investigate, and probably manually update the DB.
	 */
	@Override
	@Bean
	@Qualifier("auxLabelQueueParkingLot")
	public Queue parkingLotQueue() {
		return new Queue(AuxLabelQueueSink.PARKING_LOT);
	}

	/**
	 * This is the DLQ setup for 'aux-label-accounting-queue', messsages will be retried 5 times in the normal queue, after-which it is moved to the DLQ
	 * automatically. This queue will move it back to the normal queue 3 times. After the third attempt, it will be moved to the parkingLot.
	 * As mentioned above, this hopefully never happens, because this will require manual intervention.
	 * @param failedMessage
	 * @throws Exception
	 */
	@Override
	@RabbitListener(queues = AuxLabelQueueSink.DLQ)
	public void dlqHandle(Message failedMessage) {
		IDeadLetterQueueHandler.super.dlqHandle(failedMessage);
	}

	@StreamListener(AuxLabelQueueSink.INPUT)
	private void handle(AuxLabelStreamData entry) {
		queueRateLimiter.limitQueueRateIfApplicable(AuxLabelQueueSink.ORIGINAL_QUEUE);

		try {
			log.info("Received an AuxLabel from the queue for processing: " + entry);

			List<LabelValue> lvList = new ArrayList<>();
			entry.getLabelValueList().forEach(lv -> {
				lvList.add(mapper.map(lv, LabelValue.class));
			});

			transactionService.summarizeAdditionalTransactionLabels(entry.getTransactionId(), lvList);

			log.debug("Completed an AuxLabel from the queue: " + entry);
		} catch (Exception e) {
			log.error("Error handling attempt for msg: "+entry, e);
			throw new RuntimeException(e);
		}
	}
}