package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.Profile;
import lithium.service.cashier.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	User findByGuid(String userGuid);
	Page<User> findByProfile(Profile profile, Pageable pageable);
}