package lithium.service.notifications.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.notifications.data.entities.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {
	User findByGuid(String guid);
}
