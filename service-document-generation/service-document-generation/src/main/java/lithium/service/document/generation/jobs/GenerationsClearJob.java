package lithium.service.document.generation.jobs;

import com.google.common.collect.ImmutableList;
import lithium.service.document.generation.client.enums.DocumentGenerationStatus;
import lithium.service.document.generation.config.DocumentGenerationConfigurationProperties;
import lithium.service.document.generation.data.entities.DocumentFile;
import lithium.service.document.generation.data.entities.DocumentGeneration;
import lithium.service.document.generation.data.repositories.DocumentFileRepository;
import lithium.service.document.generation.data.repositories.DocumentGenerationRepository;
import lithium.service.document.generation.data.repositories.RequestParametersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GenerationsClearJob {
    @Autowired
    private DocumentFileRepository documentFileRepository;

    @Autowired
    private DocumentGenerationRepository documentGenerationRepository;
    @Autowired
    private DocumentGenerationConfigurationProperties properties;

    @Autowired
    private RequestParametersRepository repository;

    private final ImmutableList<Integer> CLEANABLE_STATUSES = ImmutableList.of(
            DocumentGenerationStatus.CREATED.getValue(),
            DocumentGenerationStatus.FAILED.getValue(),
            DocumentGenerationStatus.CANCELED.getValue()
    );


    @Scheduled(cron = "${lithium.service.document.generation.cron:0 0 */1 * * *}")
    @Transactional
    @Modifying
    public void process() {
        long clearDelay = properties.getClearDelayMillis();
        Date before = new Date(System.currentTimeMillis() - clearDelay);
        clearFiles(before);
        clearGenerations(before);
    }

    private void clearGenerations(Date before) {
        List<DocumentGeneration> generations = documentGenerationRepository.findByStatusInAndCreatedDateBefore(CLEANABLE_STATUSES, before);
        log.info("Document generations clear job started and found: " + generations.size() + " generations for deleting");
        deleteGenerationData(generations);
    }

    private void clearFiles(Date before) {
        List<DocumentFile> files = documentFileRepository.deleteDocumentFileByCreatedDateBefore(before);

        log.info("Document generations clear job started and found: " + files.size() + " files for deleting");

        List<DocumentGeneration> generations = files.stream()
                .map(DocumentFile::getReference)
                .map(Long::valueOf)
                .map(documentGenerationRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        deleteGenerationData(generations);

        log.debug("Deleted document generations files: " + files.stream()
                .map(DocumentFile::getReference)
                .collect(Collectors.joining(","))
        );
    }

    private void deleteGenerationData(List<DocumentGeneration> content) {
        content.forEach(documentGeneration -> repository.deleteAll(documentGeneration.getParameters()));
        documentGenerationRepository.deleteAll(content);
        log.debug(content.size() + " generations deleted by cleanJob");
    }
}
