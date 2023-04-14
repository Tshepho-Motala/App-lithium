package lithium.service.casino.provider.betsoft.data.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"result", "code", "description", "bonusId"})
public class CheckBonusResponse extends Response {
	@XmlElement(name="BONUSID")
	private Integer bonusId;
	
	public CheckBonusResponse(String code, String result) {
		super(code, result);
	}
}