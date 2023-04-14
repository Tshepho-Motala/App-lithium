package lithium.service.cashier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.repositories.TransactionStatusRepository;

@Service
public class TransactionStatusService {
	
	@Autowired TransactionStatusRepository repo;

	public TransactionStatus findOrCreate(String code, boolean active) {
		TransactionStatus status = repo.findByCode(code);
		if (status == null) status = repo.save(TransactionStatus.builder().code(code).active(active).build());
		return status;
	}
}
