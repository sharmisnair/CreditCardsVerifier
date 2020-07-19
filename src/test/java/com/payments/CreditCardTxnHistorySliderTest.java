package com.payments;
import static org.assertj.core.api.Assertions.assertThat;

import com.payments.creditcards.CreditCardTxn;
import com.payments.creditcards.CreditCardTxnHistorySlider;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.Test;

public class CreditCardTxnHistorySliderTest extends CreditCardTxnHistorySlider {
  @Test
  public void updateCreditCardSlidingWindow_testNewCreditCard() {
    LocalDateTime timestamp = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;
    CreditCardTxn newTxn = new CreditCardTxn(timestamp, amount);

    updateCreditCardSlidingWindow(null, newTxn);
    assertThat(getStartCreditCardSlidingWindowIndex()).isEqualTo(0);
    assertThat(getCreditCardTotalSpentInSlidingWindow()).isEqualTo(newTxn.getCCAmountSpent());
  }

  @Test
  public void updateCreditCardSlidingWindow_testExistingCardNoSliding() {

    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-29T20:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;
    CreditCardTxn startTxn = new CreditCardTxn(timestamp1, amount);
    CreditCardTxn newTxn = new CreditCardTxn(timestamp2, amount);

    setStartCreditCardSlidingWindowIndex(1);
    setCreditCardTotalSpentInSlidingWindow(amount);

    updateCreditCardSlidingWindow(startTxn, newTxn);

    assertThat(getStartCreditCardSlidingWindowIndex()).isEqualTo(1);
    assertThat(getCreditCardTotalSpentInSlidingWindow()).isEqualTo(20);
  }

  @Test
  public void updateCreditCardSlidingWindow_testExistingCardAndSliding() {

    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-30T20:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;

    CreditCardTxn startTxn = new CreditCardTxn(timestamp1, amount);
    CreditCardTxn newTxn = new CreditCardTxn(timestamp2, amount);

    setStartCreditCardSlidingWindowIndex(1);
    setCreditCardTotalSpentInSlidingWindow(amount);

    updateCreditCardSlidingWindow(startTxn, newTxn);

    assertThat(getStartCreditCardSlidingWindowIndex()).isEqualTo(2);
    assertThat(getCreditCardTotalSpentInSlidingWindow()).isEqualTo(10);
  }
}
