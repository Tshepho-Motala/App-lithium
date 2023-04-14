package lithium.service.casino.provider.evolution.model.request.authentication;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Player {
    private String id;
    private Boolean update;
    private String firstName;
    private String lastName;
    private String nickname;
    private String country;
    private String language;
    private String currency;
    private PlayerSession session;
    private PlayerGroup group;

}
