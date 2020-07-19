package com.payments;

import static org.assertj.core.api.Assertions.assertThat;

import com.payments.creditcards.CreditCard;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.Test;

public class CreditCardTest {

  @Test
  public void isCreditCardTxnValid_testInvalidCard() {

    LocalDateTime timestamp = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;
    Double threshold = 150.0;

    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";
    CreditCard cc = new CreditCard(hash);

    cc.setCreditCardInvalid();
    assertThat(cc.isCreditCardTxnValid(timestamp, amount, threshold)).isEqualTo(false);
  }

  @Test
  public void isCreditCardTxnValid_testValidTxn() {

    LocalDateTime timestamp = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;
    Double threshold = 150.0;

    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";
    CreditCard cc = new CreditCard(hash);

    assertThat(cc.isCreditCardTxnValid(timestamp, amount, threshold)).isEqualTo(true);
  }

  @Test
  public void isCreditCardTxnValid_testInvalidTxn() {

    LocalDateTime timestamp = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 200.0;
    Double threshold = 150.0;

    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";
    CreditCard cc = new CreditCard(hash);

    assertThat(cc.isCreditCardTxnValid(timestamp, amount, threshold)).isEqualTo(false);
  }

  @Test
  public void setCreditCardInvalid_testInvalid() {
    CreditCard cc = new CreditCard("10d7ce2f43e35fa57d1bbf8b1e2");

    cc.setCreditCardInvalid();

    assertThat(cc.isCreditCardValid()).isEqualTo(false);
  }

  @Test
  public void setCreditCardInvalid_testValid() {
    CreditCard cc = new CreditCard("10d7ce2f43e35fa57d1bbf8b1e2");

    assertThat(cc.isCreditCardValid()).isEqualTo(true);
  }
}
