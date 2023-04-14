package lithium.service.casino.provider.sportsbook.context.objects;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency implements Serializable {

  private static final long serialVersionUID = 1L;
  private long id;
  private String code;
}