package lithium.service.cashier.client.internal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum TransactionProcessingCode {
	HOLD_PENDING_WITHDRAWALS("on-hold"),
	RE_PROCESS_ON_HOLD_WITHDRAWALS("reprocess on-hold"),
	APPROVE_WITHDRAWALS("approved"),
	CANCEL("canceled");
	
	@Getter
	private String code;
}
