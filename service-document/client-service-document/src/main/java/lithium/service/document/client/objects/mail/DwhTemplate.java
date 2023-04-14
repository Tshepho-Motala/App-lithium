package lithium.service.document.client.objects.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DwhTemplate {
    UPLOADED_DOCUMENT_TEMPLATE("uploaded.document.dwh"),
    MATCH_DOCUMENT_ADDRESS_MANUAL_TEMPLATE("match.address.dwh"),
    MATCH_FACIAL_SIMILARITY_TEMPLATE("match.facial_similarity.dwh");

    @Getter
    private String templateName;
}
