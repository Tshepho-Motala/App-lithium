package lithium.service.cashier.enums;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Slf4j
public class CardTypeTest {
	
	@Test
	public void testDetection() {
		try {
			assertEquals(CardType.VISA, CardType.detect("4000056655665556"));
			log.debug("4000056655665556 == "+CardType.VISA);
			assertEquals(CardType.VISA, CardType.detect("4242424242424242"));
			log.debug("4242424242424242 == "+CardType.VISA);
			assertEquals(CardType.VISA, CardType.detect("4012001036275556"));
			log.debug("4012001036275556 == "+CardType.VISA);
			
			assertEquals(CardType.MASTERCARD, CardType.detect("5105105105105100"));
			log.debug("5105105105105100 == "+CardType.MASTERCARD);
			assertEquals(CardType.MASTERCARD, CardType.detect("5200828282828210"));
			log.debug("5200828282828210 == "+CardType.MASTERCARD);
			assertEquals(CardType.MASTERCARD, CardType.detect("5555555555554444"));
			log.debug("5555555555554444 == "+CardType.MASTERCARD);
			
			assertEquals(CardType.AMERICAN_EXPRESS, CardType.detect("371449635398431"));
			log.debug("371449635398431 == "+CardType.AMERICAN_EXPRESS);
			assertEquals(CardType.AMERICAN_EXPRESS, CardType.detect("378282246310005"));
			log.debug("378282246310005 == "+CardType.AMERICAN_EXPRESS);
			
			assertEquals(CardType.DISCOVER, CardType.detect("6011000990139424"));
			log.debug("6011000990139424 == "+CardType.DISCOVER);
			assertEquals(CardType.DISCOVER, CardType.detect("6011111111111117"));
			log.debug("6011111111111117 == "+CardType.DISCOVER);
			
			assertEquals(CardType.DINERS_CLUB, CardType.detect("30569309025904"));
			log.debug("30569309025904 == "+CardType.DINERS_CLUB);
			assertEquals(CardType.DINERS_CLUB, CardType.detect("38520000023237"));
			log.debug("38520000023237 == "+CardType.DINERS_CLUB);
			
			assertEquals(CardType.JCB, CardType.detect("3530111333300000"));
			log.debug("3530111333300000 == "+CardType.JCB);
			assertEquals(CardType.JCB, CardType.detect("3566002020360505"));
			log.debug("3566002020360505 == "+CardType.JCB);
			
			assertEquals(CardType.UNKNOWN, CardType.detect("0000000000000000"));
			log.debug("0000000000000000 == "+CardType.UNKNOWN);
			
			log.debug("Valid : "+CardType.isValid("6011000990139424"));
			log.debug("Valid : "+CardType.isValid("371449635398431"));
		} catch (AssertionError e) {
			log.error(e.getMessage(), e);
		}
	}
}
