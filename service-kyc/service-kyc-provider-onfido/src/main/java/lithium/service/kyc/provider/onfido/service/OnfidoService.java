package lithium.service.kyc.provider.onfido.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfido.Onfido;
import com.onfido.api.FileDownload;
import com.onfido.exceptions.OnfidoException;
import com.onfido.models.Check;
import com.onfido.models.Document;
import com.onfido.models.DocumentId;
import com.onfido.models.Report;
import com.onfido.webhooks.WebhookEventObject;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.document.client.DocumentClientService;
import lithium.service.document.client.objects.DocumentInfo;
import lithium.service.document.client.objects.DocumentPurpose;
import lithium.service.document.client.objects.DocumentRequest;
import lithium.service.document.client.objects.enums.DocumentReviewStatus;
import lithium.service.document.client.objects.mail.DwhNotificationPlaceholdersBuilder;
import lithium.service.document.client.objects.mail.DwhTemplate;
import lithium.service.document.client.objects.mail.MailRequest;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.objects.VendorData;
import lithium.service.kyc.provider.objects.VendorDataUtil;
import lithium.service.kyc.provider.onfido.config.ProviderConfig;
import lithium.service.kyc.provider.onfido.entitites.CheckStatus;
import lithium.service.kyc.provider.onfido.entitites.OnfidoCheck;
import lithium.service.kyc.provider.onfido.exceptions.Status400DisabledOnfidoReportException;
import lithium.service.kyc.provider.onfido.exceptions.Status411FailOnfidoServiceException;
import lithium.service.kyc.provider.onfido.exceptions.Status412NotFoundApplicantException;
import lithium.service.kyc.provider.onfido.exceptions.Status413RetrieveDocumentException;
import lithium.service.kyc.provider.onfido.exceptions.Status414NotFoundOnfidoCheckException;
import lithium.service.kyc.provider.onfido.exceptions.Status416IncompleteCheckException;
import lithium.service.kyc.provider.onfido.inbox.OnfidoNotificationService;
import lithium.service.kyc.provider.onfido.objects.OnfidoAttemptCheck;
import lithium.service.kyc.provider.onfido.objects.StatusResponse;
import lithium.service.kyc.provider.onfido.parser.BreakdownParser;
import lithium.service.kyc.provider.onfido.parser.PropertyParser;
import lithium.service.kyc.provider.onfido.repositories.OnfidoCheckRepository;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.user.client.enums.BiometricsStatus;
import lithium.service.user.client.enums.Status;
import lithium.service.user.client.enums.StatusReason;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.client.objects.UserBiometricsStatusUpdate;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static lithium.service.kyc.provider.objects.VendorDataUtil.ADDRESS;
import static lithium.service.kyc.provider.objects.VendorDataUtil.APPLICANT_ID;
import static lithium.service.kyc.provider.objects.VendorDataUtil.CHECK_ID;
import static lithium.service.kyc.provider.objects.VendorDataUtil.CHECK_SUMMARY;
import static lithium.service.kyc.provider.objects.VendorDataUtil.CREATED_AT;
import static lithium.service.kyc.provider.objects.VendorDataUtil.DATE_OF_BIRTH;
import static lithium.service.kyc.provider.objects.VendorDataUtil.DATE_OF_EXPIRY;
import static lithium.service.kyc.provider.objects.VendorDataUtil.DOCUMENT_NUMBER;
import static lithium.service.kyc.provider.objects.VendorDataUtil.DOCUMENT_REPORT;
import static lithium.service.kyc.provider.objects.VendorDataUtil.DOCUMENT_TYPE;
import static lithium.service.kyc.provider.objects.VendorDataUtil.FIRST_NAME;
import static lithium.service.kyc.provider.objects.VendorDataUtil.ISSUING_COUNTRY;
import static lithium.service.kyc.provider.objects.VendorDataUtil.ISSUING_DATE;
import static lithium.service.kyc.provider.objects.VendorDataUtil.LAST_NAME;
import static lithium.service.kyc.provider.objects.VendorDataUtil.PLACE_OF_BIRTH;
import static lithium.service.kyc.provider.objects.VendorDataUtil.UPDATED_AT;

@Service
@Slf4j
@AllArgsConstructor
public class OnfidoService extends OnfidoBaseService {
    private static final String GUID = "guid=";
    private static final String VENDOR_CLEAR_STATUS = "clear";
    private static final List<String> DOCUMENT_DECISION_REPORTS = List.of("document", "document_with_address_information", "document_with_driving_licence_information");
    private static final List<String> ADDRESS_DECISION_REPORTS = List.of("proof_of_address");
    private static final List<String> FACIAL_SIMILARITY = List.of("facial_similarity_photo", "facial_similarity_video");
    private final OnfidoApplicantService applicantService;
    private final UserApiInternalClientService userApiInternalClientService;
    private final LimitInternalSystemService limits;
    private final DocumentClientService documentServiceClient;
    private final OnfidoChangelogService changelogService;
    private final OnfidoCheckRepository onfidoCheckRepository;
    private final ObjectMapper mapper;
    private final AddressMatchService addressMatchService;
    private final OnfidoNotificationService notificationService;

    public StatusResponse submitCheck(String guid, String domainName, String reportType, String[] documentIds)
            throws
            Status400DisabledOnfidoReportException,
            Status411FailOnfidoServiceException,
            Status512ProviderNotConfiguredException,
            Status500InternalServerErrorException,
            Status412NotFoundApplicantException, Status416IncompleteCheckException {
        OnfidoAttemptCheck.OnfidoAttemptCheckBuilder attemptCheckBuilder = OnfidoAttemptCheck.builder()
                .userGuid(guid)
                .domainName(domainName);
        boolean logAttempt = true;
        try {

            ProviderConfig config = getConfig(domainName);
            Onfido onfido = getOnfidoClient(config);

            reportValidation(config, reportType);

            String applicantId = applicantService.getApplicantIdByGuid(guid);
            attemptCheckBuilder
                    .applicantId(applicantId)
                    .reportNames(List.of(reportType));

            Optional<OnfidoCheck> activeCheck = getActiveCheck(guid);
            if (activeCheck.isPresent()) {
                OnfidoCheck onfidoCheck = activeCheck.get();
                if (!reportType.equals(onfidoCheck.getReportType())) {
                    log.warn("There is other type incomplete check (" + guid + ", " + applicantId + ")");
                    throw new Status416IncompleteCheckException("There is other type incomplete check for requested applicant");
                }
                logAttempt = false;
                return StatusResponse.mapStatus(activeCheck.get().getStatus());
            }

            Check check = doCheck(onfido, config.getWebhookIds(), applicantId, reportType, documentIds, guid);

            onfidoCheckRepository.save(OnfidoCheck.builder()
                    .checkId(check.getId())
                    .applicantId(applicantId)
                    .status(CheckStatus.INITIATED)
                    .reportType(reportType)
                    .build());

            String message = "Applicant check committed (" + check.getResultsUri() + "," + guid + ")";
            log.info(message);
            attemptCheckBuilder
                    .status(CheckStatus.INITIATED)
                    .checkId(check.getId())
                    .comment(message);
            if (isFacialSimilarity(reportType)) {
                updateBiometricsStatus(guid, BiometricsStatus.PENDING.name(), "Onfido check submitted for " + reportType);
            }
            return StatusResponse.mapStatus(CheckStatus.INITIATED);

        } catch (Exception e) {
            String message = "Applicant check(" + guid + ") commit fail due " + e.getMessage();
            log.warn(message);
            attemptCheckBuilder
                    .comment(message);
            throw e;
        } finally {
            if (logAttempt) {
                changelogService.registerInitialAttempt(attemptCheckBuilder.build());
            }
        }
    }

    private static void reportValidation(ProviderConfig config, String reportType) throws Status400DisabledOnfidoReportException {
        if (!config.getReportNames().contains(reportType)) {
            throw new Status400DisabledOnfidoReportException("Report " + reportType + " is disabled or absent");
        }
    }

    private static Check doCheck(Onfido onfido, String[] webhookIds, String applicantId, String reportType, String[] documentIds, String userGuid) throws Status411FailOnfidoServiceException {
        try {
            Check.Request checkRequest = Check.request()
                    .applicantId(applicantId)
                    .reportNames(List.of(reportType))
                    .webhookIds(webhookIds)
                    .documentIds(documentIds)
                    .tags(GUID + userGuid);
            return onfido.check.create(checkRequest);
        } catch (OnfidoException e) {
            log.error("Can't do check (" + applicantId + ", " + userGuid + ") due " + e.getMessage(), e);
            throw new Status411FailOnfidoServiceException("Can't do check due " + e.getMessage());
        }
    }
    public Optional<OnfidoCheck> getLastCheck(String guid, String requestedReportType) throws Status412NotFoundApplicantException {
        String applicantId = applicantService.getApplicantIdByGuid(guid);
        return onfidoCheckRepository.findFirstByApplicantIdAndReportTypeOrderByIdDesc(applicantId, requestedReportType);
    }

    private Optional<OnfidoCheck> getActiveCheck(String guid) throws Status412NotFoundApplicantException {
        String applicantId = applicantService.getApplicantIdByGuid(guid);
        return onfidoCheckRepository.findFirstByApplicantIdAndStatusInOrderByIdDesc(applicantId, List.of(CheckStatus.INITIATED, CheckStatus.PROCESSING));
    }

    public boolean haveIncompleteCheck(String guid) {
        try {
            return getActiveCheck(guid).isPresent();
        } catch (Status412NotFoundApplicantException e) {
            log.warn("Related applicant not found (" + guid + ")");
            return false;
        }
    }

    public void handleCheckComplete(String domainName, WebhookEventObject webhookEventObject) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException, OnfidoException, Status413RetrieveDocumentException, Status412NotFoundApplicantException, Status414NotFoundOnfidoCheckException {
        String checkId = webhookEventObject.getId();
        String completedAtIso8601 = Optional.ofNullable(webhookEventObject.getCompletedAtIso8601())
                .orElse(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(OffsetDateTime.now()));

        OnfidoCheck onfidoCheck = onfidoCheckRepository.findByCheckId(checkId)
                .orElseThrow(() -> new Status414NotFoundOnfidoCheckException("Can't find Onfido check " + checkId));
        User user = applicantService.getUserByApplicantId(onfidoCheck.getApplicantId());

        OnfidoAttemptCheck.OnfidoAttemptCheckBuilder attemptCheckBuilder = OnfidoAttemptCheck.builder()
                .userGuid(user.guid())
                .domainName(domainName)
                .checkId(checkId)
                .applicantId(onfidoCheck.getApplicantId());
        try {

            if (CheckStatus.INITIATED != onfidoCheck.getStatus()) {
                String message = "Related Onfido check is not in expected state: " + onfidoCheck;
                log.warn(message);
                attemptCheckBuilder.comment(message);
                return;
            }

            onfidoCheck.setStatus(CheckStatus.PROCESSING);
            onfidoCheckRepository.save(onfidoCheck);
            log.info("Start processing Onfido check (" + checkId + ")");

            ProviderConfig config = getConfig(domainName);
            Onfido onfido = getOnfidoClient(config);

            Check check = onfido.check.find(checkId);
            log.info("Found check (" + checkId + ", " + check.getTags() + "): " + check.getResult() + ", " + check.getResultsUri());
            List<Report> reports = onfido.report.list(checkId);

            KycSuccessVerificationResponse verificationResponse = extractFieldsFromReports(user, reports, check.getApplicantId(), completedAtIso8601);

            if (isFacialSimilarityResponse(verificationResponse)) {
                resolveBiometricStatus(user, checkId, verificationResponse);
            } else {
                resolveVerificationStatus(user, config, onfido, reports, checkId, verificationResponse);
            }

            log.info(verificationResponse.getResultMessageText());
            onfidoCheck.setStatus(CheckStatus.COMPLETE);
            onfidoCheckRepository.save(onfidoCheck);

            attemptCheckBuilder
                    .reportNames(reports.stream().map(Report::getName).toList())
                    .resultsUri(check.getResultsUri())
                    .documentReports(reports.stream().map(this::toJson).toList())
                    .status(CheckStatus.COMPLETE)
                    .comment(verificationResponse.getResultMessageText())
                    .response(verificationResponse);

        } catch (Exception e) {
            onfidoCheck.setStatus(CheckStatus.FAIL);
            onfidoCheckRepository.save(onfidoCheck);
            String message = "User(" + user.getGuid() + ") verification via Onfido fail due " + e.getMessage();
            log.warn(message);
            attemptCheckBuilder
                    .status(CheckStatus.FAIL)
                    .comment(message);
            throw e;
        } finally {
            changelogService.registerFinishAttempt(onfidoCheck.getKycVerificationResultId(), attemptCheckBuilder.build());
        }
    }

    private void resolveVerificationStatus(User user, ProviderConfig config, Onfido onfido, List<Report> reports, String checkId, KycSuccessVerificationResponse verificationResponse) throws Status500InternalServerErrorException {
        List<DocumentInfo> documentsInfo = retrieveDocumentsFromOnfidoToDocumentsService(onfido, user, reports);

        boolean documentPassed = isClear(verificationResponse.getDocumentDecision());
        boolean addressPassed = isClear(verificationResponse.getAddressDecision());

        DwhTemplate template = null;

        if (!documentPassed && canFirstNameMatch(verificationResponse, config)) {
            documentPassed = matchFirstNameManually(verificationResponse, user);
        }
        if (!documentPassed) {
            template = DwhTemplate.UPLOADED_DOCUMENT_TEMPLATE;
        }

        if (documentPassed && !addressPassed && canAddressMatch(verificationResponse, config, user.getGuid())) {
            if (config.isMatchDocumentAddress()) {
                addressPassed = matchAddressManually(verificationResponse.getAddress(), user);
                if (!addressPassed) {
                    template = DwhTemplate.MATCH_DOCUMENT_ADDRESS_MANUAL_TEMPLATE;
                }
            } else {
                template = DwhTemplate.UPLOADED_DOCUMENT_TEMPLATE;
            }
        }

        boolean userUnderAged = checkIsUserUnderAged(verificationResponse);

        boolean completeVerified = resolveAndUpdateUserStatus(documentPassed, addressPassed, userUnderAged, user);
        notifyDwhWithDocuments(completeVerified, documentsInfo, user, template, verificationResponse.getAddress());

        String message = "User verification via Onfido complete: User (" + user.getGuid() + ", " + checkId +
                "). Document passed: " + documentPassed + ". Address passed: " + addressPassed;

        verificationResponse.setSuccess(documentPassed && addressPassed);
        verificationResponse.setResultMessageText(message);
    }

    private void notifyDwhWithDocuments(boolean completeVerified, List<DocumentInfo> documentsInfo, User user, DwhTemplate template, String parsedAddress) {
        if (completeVerified || isNull(template)) {
            return;
        }
        if (documentsInfo.isEmpty()) {
            log.error("Can't send DWH notification due no documents passed (" + user.getGuid() + ")");
            return;
        }
        sendDwhNotification(user.getGuid(), documentsInfo, template, parsedAddress);
    }

    private boolean canFirstNameMatch(KycSuccessVerificationResponse verificationResponse, ProviderConfig config) {
        if (!config.isMatchFirstName()) {
            return false;
        }
        if (!isReportCautionStatus(verificationResponse)) {
            return false;
        }

        Optional<Map<String, String>> vendorData = getRelatedVendorData(verificationResponse);
        boolean considerFirstName = vendorData.map(map -> map.get("dataComparison.first_name"))
                .filter("consider"::equals)
                .isPresent();
        boolean clearLastName = vendorData.map(map -> map.get("dataComparison.last_name"))
                .filter(VENDOR_CLEAR_STATUS::equals)
                .isPresent();
        boolean clearDob = vendorData.map(map -> map.get("dataComparison.date_of_birth"))
                .filter(VENDOR_CLEAR_STATUS::equals)
                .isPresent();

        return considerFirstName && clearLastName && clearDob;
    }

    private void resolveBiometricStatus(User user, String checkId, KycSuccessVerificationResponse verificationResponse) throws Status500InternalServerErrorException {
        boolean facialSimilarityPassed = isClear(verificationResponse.getBiometricValidation());
        String biometricStatus = facialSimilarityPassed ? BiometricsStatus.PASSED.name() : BiometricsStatus.FAILED.name();

        String message = "User verification via Onfido complete: User (" + user.getGuid() + ", " + checkId +
                "). Document facial similarity " + biometricStatus;

        verificationResponse.setSuccess(facialSimilarityPassed);
        verificationResponse.setResultMessageText(message);

        updateBiometricsStatus(user.getGuid(), biometricStatus, message);

        if (facialSimilarityPassed) {
            notificationService.sendBiometricValidationPassedNotification(user.getGuid());
        } else {
            sendDwhNotification(user.getGuid(), Collections.emptyList(), DwhTemplate.MATCH_FACIAL_SIMILARITY_TEMPLATE, null);
        }
    }

    private void updateBiometricsStatus(String guid, String biometricsStatus, String message) throws Status500InternalServerErrorException {
        try {
            userApiInternalClientService.updateBiometricsStatus(
                    UserBiometricsStatusUpdate
                            .builder()
                            .userGuid(guid)
                            .biometricsStatus(biometricsStatus)
                            .comment(message)
                            .authorGuid(guid)
                            .build());
        } catch (Exception ex) {
            String exMessage = "Cant update BiometricsStatus for user = " + guid + " to '" + biometricsStatus + "'";
            log.error(exMessage, ex);
            throw new Status500InternalServerErrorException(exMessage);
        }
    }

    private static boolean isFacialSimilarityResponse(KycSuccessVerificationResponse verificationResponse) {
        return verificationResponse.getVendorsData().stream()
                .anyMatch(vendorData -> isFacialSimilarity(vendorData.getName()));
    }

    private static boolean isFacialSimilarity(String reportName) {
        return FACIAL_SIMILARITY.contains(reportName);
    }

    private static boolean checkIsUserUnderAged(KycSuccessVerificationResponse verificationResponse) {
        boolean documentReportRejected = getDocumentReport(verificationResponse.getVendorsData())
                .filter("rejected"::equalsIgnoreCase)
                .isPresent();
        boolean minAcceptedAgeFailed = getRelatedVendorData(verificationResponse).map(map -> map.get("ageValidation.minimum_accepted_age"))
                .filter("consider"::equals)
                .isPresent();
        return documentReportRejected && minAcceptedAgeFailed;
    }

    private static boolean isReportCautionStatus(KycSuccessVerificationResponse verificationResponse) {
        return getDocumentReport(verificationResponse.getVendorsData())
                .filter("caution"::equalsIgnoreCase)
                .isPresent();
    }

    private static Optional<String> getIssuingCountry(List<VendorData> vendorData) {
        return VendorDataUtil.findData(ISSUING_COUNTRY, vendorData);
    }

    private static Optional<String> getDocumentReport(List<VendorData> vendorData) {
        return VendorDataUtil.findData(DOCUMENT_REPORT, vendorData);
    }

    private static boolean canAddressMatch(KycSuccessVerificationResponse verificationResponse, ProviderConfig config, String guid) {
        boolean addressExtracted = ofNullable(verificationResponse.getAddress())
                .filter(not(String::isEmpty))
                .filter(s -> !"Not available".equalsIgnoreCase(s))
                .isPresent();
        if (!addressExtracted) {
            log.info(guid + ", address not extracted");
            return false;
        }

        Optional<String> issuingCountryOptional = getIssuingCountry(verificationResponse.getVendorsData());
        if (issuingCountryOptional.isEmpty()) {
            log.warn(guid + ", Can't find issuing country of document");
            return false;
        }

        List<String> supportedIssuingCountries = config.getSupportedIssuingCountries();
        if (nonNull(supportedIssuingCountries) && !supportedIssuingCountries.isEmpty()) {
            String issuingCountry = issuingCountryOptional.get();
            if (supportedIssuingCountries.stream()
                    .noneMatch(issuingCountry::equalsIgnoreCase)) {
                log.info(guid + ", not able to match address due issuing country(" + issuingCountry + ") not supported");
                return false;
            }
        }
        return true;
    }

    private static boolean matchFirstNameManually(KycSuccessVerificationResponse verificationResponse, User user) {
        String extractedFirstName =getRelatedVendorData(verificationResponse).map(map -> map.get("firstName"))
                .orElse("");
        boolean match = extractedFirstName.toLowerCase().contains(user.getFirstName().toLowerCase());
        if (match) {
            log.info(user.getGuid() + " first name matched");
        } else {
            log.info(user.getGuid() + " first name matching failed");
        }
        return match;
    }

    private static Optional<Map<String, String>> getRelatedVendorData(KycSuccessVerificationResponse verificationResponse) {
        return verificationResponse.getVendorsData().stream()
                .filter(vendorData -> DOCUMENT_DECISION_REPORTS.contains(vendorData.getName()))
                .findFirst()
                .map(VendorData::getData);
    }

    private boolean matchAddressManually(String extractedAddress, User user) {
        if (isNull(user.getResidentialAddress())) {
            log.warn("User " + user.getGuid() + " not defined residential address");
            return false;
        }
        lithium.service.user.client.objects.Address residentialAddress = user.getResidentialAddress();

        boolean match = addressMatchService.matchWithSymbolsIgnore(user.guid(), extractedAddress,
                residentialAddress.getAddressLine1(), residentialAddress.getCity(), residentialAddress.getPostalCode(),
                "\s", "\\.", "-", ",");
        if (match) {
            log.info(user.getGuid() + " address matched");
        } else {
            log.info(user.getGuid() + " address matching failed");
        }
        return match;
    }

    private void sendDwhNotification(String userGuid, List<DocumentInfo> documentsInfo, DwhTemplate template, String parsedAddress) {
        try {
            User user = userApiInternalClientService.getUserByGuid(userGuid);
            String domainName = user.getDomain().getName();
            DwhNotificationPlaceholdersBuilder placeholders = new DwhNotificationPlaceholdersBuilder()
                    .setDomainName(domainName)
                    .setPlayerGuid(user.getGuid())
                    .setPlayerLink("/#/dashboard/players/" + domainName + "/" + user.getId() + "/summary")
                    .setAccountStatus(user.getStatus().getName())
                    .setVerificationStatus(limits.getVerificationStatusCode(user.getVerificationStatus()))
                    .setAgeVerified(Boolean.TRUE.equals(user.getAgeVerified()) ? "Yes" : "No")
                    .setAddressVerified(Boolean.TRUE.equals(user.getAddressVerified()) ? "Yes" : "No")
                    .setBiometricStatus(user.getBiometricsStatus().getValue())
                    .setResidentialAddress(Optional.ofNullable(user.getResidentialAddress())
                            .map(lithium.service.user.client.objects.Address::toOneLinerFull)
                            .orElse(null))
                    .setDocumentAddress(parsedAddress);

            if (documentsInfo.size() > 0) {
                DocumentInfo documentInfo1 = documentsInfo.get(0);
                placeholders
                        .setFileName1(documentInfo1.getFileName())
                        .setFileLink1(documentInfo1.getFileLink())
                        .setFileTimestamp1(documentInfo1.getUploadDate().toString())
                        .setDocumentType(documentInfo1.getDocumentType());
            }
            if (documentsInfo.size() > 1) {
                DocumentInfo documentInfo2 = documentsInfo.get(1);
                placeholders
                        .setFileName2(documentInfo2.getFileName())
                        .setFileLink2(documentInfo2.getFileLink())
                        .setFileTimestamp2(documentInfo2.getUploadDate().toString());
            }
            documentServiceClient.sendDwhNotification(MailRequest.builder()
                    .domainName(domainName)
                    .userGuid(user.getGuid())
                    .template(template)
                    .placeholders(placeholders.build())
                    .build());
        } catch (Throwable e) {
            log.error("Can't notify DWH about uploaded documents (" + userGuid + ") due " + e.getMessage(), e);
        }
    }

    private KycSuccessVerificationResponse extractFieldsFromReports(User user, List<Report> reports, String applicantId, String completedAt) {
        KycSuccessVerificationResponse.KycSuccessVerificationResponseBuilder verificationResponseBuilder = KycSuccessVerificationResponse.builder();
        List<VendorData> vendorsData = new ArrayList<>();
        for (Report report : reports) {
            if (DOCUMENT_DECISION_REPORTS.contains(report.getName())) {
                verificationResponseBuilder.documentDecision(report.getResult());
            }
            if (ADDRESS_DECISION_REPORTS.contains(report.getName())) {
                verificationResponseBuilder.addressDecision(report.getResult());
            }
            if (isFacialSimilarity(report.getName())) {
                verificationResponseBuilder.biometricValidation(report.getResult());
            }
            boolean documentPassed = isClear(report.getResult());
            if (!documentPassed) {
                log.warn("Report (" + user.guid() + ", " + report.getId() + ") is not clear:\n" + toJson(report));
            }

            Map<String, String> data = new HashMap<>();

            data.put(APPLICANT_ID, applicantId);
            data.put(CREATED_AT, report.getCreatedAt().toString());
            data.put(UPDATED_AT, completedAt);
            data.put(CHECK_SUMMARY, report.getResult());
            data.put(DOCUMENT_REPORT, report.getSubResult());
            data.put(CHECK_ID, report.getCheckId());

            BreakdownParser breakdownParser = BreakdownParser.of(data, report.getBreakdown());
            breakdownParser.extract("data_comparison", "dataComparison",
                    "date_of_birth", "last_name", "first_name", "address");
            breakdownParser.extract("age_validation", "ageValidation",
                    "minimum_accepted_age");
            breakdownParser.extract("visual_authenticity", "visualAuthenticity",
                    "spoofing_detection", "liveness_detected");
            breakdownParser.extract("image_integrity", "imageIntegrity",
                    "face_detected", "source_integrity");
            breakdownParser.extract("face_comparison", "faceComparison",
                    "face_match");
            breakdownParser.extract("issuing_authority", "issuingAuthority",
                    "nfc_passive_authentication", "nfc_active_authentication");

            PropertyParser propertyParser = PropertyParser.of(data, report.getProperties());
            String firstName = propertyParser.extract("first_name", FIRST_NAME);
            String lastName = propertyParser.extract("last_name", LAST_NAME);
            verificationResponseBuilder.lastName(firstName);
            if (nonNull(firstName) && nonNull(lastName)) {
                verificationResponseBuilder.lastName(firstName + " " + lastName);
            }
            verificationResponseBuilder.dob(propertyParser.extract("date_of_birth", DATE_OF_BIRTH));
            verificationResponseBuilder.countryOfBirth(propertyParser.extract("place_of_birth", PLACE_OF_BIRTH));
            verificationResponseBuilder.address(propertyParser.extract("address", ADDRESS));
            propertyParser.extract("issuing_country", ISSUING_COUNTRY);
            propertyParser.extract("issuing_date", ISSUING_DATE);
            propertyParser.extract("date_of_expiry", DATE_OF_EXPIRY);
            propertyParser.extract("document_type", DOCUMENT_TYPE);
            propertyParser.extractDocumentNumbers(DOCUMENT_NUMBER);

            Optional.ofNullable(data.get(DOCUMENT_NUMBER))
                    .ifPresent(verificationResponseBuilder::methodTypeUid);

            VendorData vendorData = VendorData.builder()
                    .name(report.getName())
                    .data(data)
                    .build();
            vendorsData.add(vendorData);
        }
        verificationResponseBuilder.vendorsData(vendorsData);
        return verificationResponseBuilder.build();
    }

    private static boolean isClear(String result) {
        return VENDOR_CLEAR_STATUS.equals(result);
    }

    private boolean resolveAndUpdateUserStatus(boolean documentPassed, boolean addressPassed, boolean userUnderAged, User user) throws Status500InternalServerErrorException {
        try {
            boolean completelyVerified = false;
            String userGuid = user.guid();

            UserVerificationStatusUpdate.UserVerificationStatusUpdateBuilder statusUpdateBuilder = UserVerificationStatusUpdate.builder()
                    .userId(user.getId())
                    .comment("Based on the verification of documents by the Onfido service ")
                    .userGuid(userGuid);

            boolean isAddressVerified = userApiInternalClientService.getUserByGuid(userGuid).getAddressVerified();

            if (userUnderAged) {
                userApiInternalClientService.editUserVerificationStatus(
                        statusUpdateBuilder
                                .statusId(VerificationStatus.UNDERAGED.getId())
                                .comment("Document received indicating person is under 18")
                                .build());
                userApiInternalClientService.changeAccountStatus(
                        UserAccountStatusUpdate.builder()
                                .userGuid(user.guid())
                                .statusName(Status.BLOCKED.statusName())
                                .statusReasonName(StatusReason.OTHER.statusReasonName())
                                .comment("Document received indicating person is under 18")
                                .build());
                completelyVerified = false;
                log.info("User " + userGuid + " blocked due provided document received indicating person is under 18");
            } else if (documentPassed && addressPassed) {
                userApiInternalClientService.editUserVerificationStatus(
                        statusUpdateBuilder
                                .statusId(VerificationStatus.EXTERNALLY_VERIFIED.getId())
                                .ageVerified(true)
                                .addressVerified(true)
                                .build());
                completelyVerified = true;
                log.info("User " + userGuid + " age and address verified");
            } else if (documentPassed) {
                statusUpdateBuilder
                        .statusId(VerificationStatus.AGE_ONLY_VERIFIED.getId())
                        .ageVerified(true);
                if (isAddressVerified) {
                    statusUpdateBuilder.statusId(VerificationStatus.EXTERNALLY_VERIFIED.getId());
                    completelyVerified = true;
                }
                userApiInternalClientService.editUserVerificationStatus(statusUpdateBuilder.build());
                log.info("User " + userGuid + " age verified");
            } else if (addressPassed) {
                statusUpdateBuilder
                        .addressVerified(true);
                if (isAddressVerified) {
                    statusUpdateBuilder.statusId(VerificationStatus.EXTERNALLY_VERIFIED.getId());
                    completelyVerified = true;
                }
                userApiInternalClientService.editUserVerificationStatus(statusUpdateBuilder.build());
                log.info("User " + userGuid + " address verified");
            }
            return completelyVerified;
        } catch (Throwable e) {
            log.error("Can't update verification data due " + e.getMessage(), e);
            throw new Status500InternalServerErrorException("Can't update verification data due " + e.getMessage(), e);
        }
    }

    private List<DocumentInfo> retrieveDocumentsFromOnfidoToDocumentsService(Onfido onfido, User user, List<Report> reports) {
        String guid = user.guid();
        try {
            List<DocumentInfo> uploadedDocuments = new ArrayList<>();
            for (Report report : reports) {
                for (DocumentId documentId : report.getDocuments()) {
                    Document document = onfido.document.find(documentId.getId());
                    FileDownload fileDownload = onfido.document.download(documentId.getId());

                    String fileName = document.getFileName();
                    byte[] fileContent = fileDownload.content;
                    String mimeType = fileDownload.contentType;
                    String documentType = document.getType();
                    String domainName = user.getDomain().getName();
                    DocumentRequest request = DocumentRequest.builder()
                            .fileName(fileName)
                            .content(fileContent)
                            .mimeType(mimeType)
                            .docPage(1)
                            .documentPurpose(DocumentPurpose.INTERNAL)
                            .documentType(documentType)
                            .domainName(domainName)
                            .userGuid(guid)
                            .reviewStatus(isClear(report.getResult()) ? DocumentReviewStatus.VALID : DocumentReviewStatus.INVALID)
                            .build();
                    DocumentInfo documentInfo = documentServiceClient.createAndUploadDocument(request).getData();
                    uploadedDocuments.add(documentInfo);
                    log.info("User " + guid + " document uploaded: " + documentInfo.getFileName());
                }
            }
            return uploadedDocuments;
        } catch (Exception e) {
            log.error("Can't retrieve documents (" + guid + ") from Onfido to documents service due " + e.getMessage(), e);
            return Collections.emptyList();
        }

    }

    public String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("can't parse object to json, " + object.toString() + " , exception " + e.getMessage());
            return "";
        }
    }
}
