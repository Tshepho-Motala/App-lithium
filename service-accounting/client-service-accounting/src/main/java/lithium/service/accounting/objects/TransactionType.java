package lithium.service.accounting.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long id;
	int version;
	private String code;
	
}
