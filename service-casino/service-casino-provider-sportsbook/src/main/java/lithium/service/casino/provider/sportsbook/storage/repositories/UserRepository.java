package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.sportsbook.storage.entities.User;

public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {
}
