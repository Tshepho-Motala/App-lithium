package lithium.service.user.client.objects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PubSubAccountCreate implements PubSubObj {
    private PubSubEventType eventType;
    private String firstName;
    private String lastName;
    private String DOB;
    private String domain;
    private String registrationDate;
    private String userName;
    private String cellphoneNumber;
    private String accountId;
    private String bonusCode;
    private Boolean isEmailValidated;
    private Boolean isCellNumberValidated;
    private Boolean isAddressVerified;
}
