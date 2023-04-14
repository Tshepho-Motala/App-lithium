package lithium.service.pushmsg.client.objects;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PushMsgTemplate implements Serializable {
	private static final long serialVersionUID = -7926915071763803208L;
	
	private Long id;
	private int version;
	private String lang;
	private String name;
	private PushMsgTemplateRevision edit;
	private Date editStartedOn;
	private User editBy;
	private PushMsgTemplateRevision current;
	private Domain domain;
	private Boolean enabled;
}