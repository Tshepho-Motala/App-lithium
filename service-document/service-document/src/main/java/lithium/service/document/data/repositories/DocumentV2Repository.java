package lithium.service.document.data.repositories;

import lithium.service.document.data.entities.DocumentV2;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentV2Repository extends PagingAndSortingRepository<DocumentV2, Long> {
    Optional<DocumentV2> findById(long id);
    List<DocumentV2> findAllByOwnerGuidAndSensitiveFalseAndDeletedFalse(String guid);
    List<DocumentV2> findAllByOwnerGuidAndDeletedFalse(String guid);
}
