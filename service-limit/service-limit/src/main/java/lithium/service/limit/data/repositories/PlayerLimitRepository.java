package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.PlayerLimit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;

public interface PlayerLimitRepository extends PagingAndSortingRepository<PlayerLimit, Long> {
//	@Cacheable(cacheNames="lithium.service.limit.playergran", key="#root.args[0] + #root.args[1] + #root.args[2]", unless="#result == null")
	PlayerLimit findByPlayerGuidAndGranularityAndType(String playerGuid, int granularity, int type);
	Page<PlayerLimit> findByType(Integer type, Pageable pageRequest);
	Page<PlayerLimit> findByTypeAndGranularity(Integer type, int granularity, Pageable pageRequest);
	
//	List<PlayerLimit> findByPlayerGuid(String playerGuid);
//
//	List<PlayerLimit> findByDomainName(String domainName);
	
	@Modifying
	@Transactional
//	@CacheEvict(cacheNames="lithium.service.limit.playergran", key="#root.args[0] + #root.args[1] + #root.args[2]")
	void deleteByPlayerGuidAndGranularityAndType(String playerGuid, int granularity, int type);
	
	@Override
//	@CacheEvict(cacheNames="lithium.service.limit.playergran", key="#result.getPlayerGuid() + #result.getGranularity() + #result.getType()")
	<S extends PlayerLimit> S save(S arg0);
	
	Page<PlayerLimit> findByDomainNameAndGranularityAndType(String domainName, int granularity, int type, Pageable pageRequest);


}
