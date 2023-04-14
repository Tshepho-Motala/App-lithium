package lithium.service.mail.client.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lithium.service.mail.client.objects.SystemEmailData;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SystemMailStream {
	@Autowired SystemMailStreamOutputQueue channel;
	
	public void process(SystemEmailData systemEmailData) {
		try {
			channel.channel().send(MessageBuilder.<SystemEmailData>withPayload(systemEmailData).build());
		} catch (RuntimeException re) {
			log.error(re.getMessage(), re);
		}
	}
}