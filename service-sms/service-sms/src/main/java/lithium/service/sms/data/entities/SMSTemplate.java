package lithium.service.sms.data.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
	@Index(name="idx_tpl_all", columnList="name, lang, domain_id", unique=true)
})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class SMSTemplate implements Serializable {
	private static final long serialVersionUID = -7926915071763803208L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	
	@Column(nullable=false)
	private String lang;
	
	@Column(nullable=false)
	private String name;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private SMSTemplateRevision edit;
	
	@Column(nullable=true)
	private Date editStartedOn;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private User editBy;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private SMSTemplateRevision current;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=false)
	private Boolean enabled;

    @Column(name="updated_on")
    private Date updatedOn;
	
	@PrePersist
	private void prePersist() {
		if (enabled == null) enabled = true;
	}
}