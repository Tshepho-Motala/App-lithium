package lithium.service.casino.client.objects;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lithium.service.document.generation.client.objects.CsvContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CasinoBetHistoryCsv implements CsvContent {
    private String id;

    @CsvBindByName(column = "Bet Status")
    @CsvBindByPosition(position = 3)
    private String complete;
    @CsvBindByName(column = "Game Provider")
    @CsvBindByPosition(position = 9)
    private String providerName;
    @CsvBindByName(column = "Game Name")
    @CsvBindByPosition(position = 1)
    private String gameName;
    @CsvBindByName(column = "Game Supplier")
    @CsvBindByPosition(position = 2)
    private String gameSupplier;
    @CsvBindByName(column = "Bet Date")
    @CsvBindByPosition(position = 0)
    private String createdDate;
    @CsvBindByName(column = "Return")
    @CsvBindByPosition(position = 5)
    private String roundReturnsTotal;
    @CsvBindByName(column = "Stake")
    @CsvBindByPosition(position = 4)
    private String betAmount;
    @CsvBindByName(column = "Bet Round GUID")
    @CsvBindByPosition(position = 8)
    private String betRoundGuid;
    @CsvBindByName(column = "Round Status")
    @CsvBindByPosition(position = 6)
    private String betRoundStatus;
    @CsvBindByName(column = "Bet Settled Date")
    @CsvBindByPosition(position = 7)
    private String betSettledDate;
    @CsvBindByName(column = "Domain Name")
    @CsvBindByPosition(position = 10)
    private String domainName;
    @CsvBindByName(column = "User ID")
    @CsvBindByPosition(position = 11)
    private Long userId;
    @CsvBindByName(column = "User GUID")
    @CsvBindByPosition(position = 12)
    private String userGuid;
    @CsvBindByName(column = "User Name")
    @CsvBindByPosition(position = 13)
    private String userName;
    @CsvBindByName(column = "Currency Code")
    @CsvBindByPosition(position = 14)
    private String currencyCode;
}
