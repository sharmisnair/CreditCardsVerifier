package com.payments;

import static org.assertj.core.api.Assertions.assertThat;

import com.payments.creditcards.CreditCardTxn;
import com.payments.creditcards.CreditCardTxnHistory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.Test;

public class CreditCardTxnHistoryTest {

  @Test
  public void addCreditCardTxn_test() {

    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-29T23:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;

    CreditCardTxnHistory txnHistory = new CreditCardTxnHistory();
    CreditCardTxn txn1 = new CreditCardTxn(timestamp1, amount);
    CreditCardTxn txn2 = new CreditCardTxn(timestamp1, amount);
    txnHistory.addCreditCardTxn(txn1);
    txnHistory.addCreditCardTxn(txn2);

    assertThat(txnHistory.getCreditCardTxnList().size()).isEqualTo(2);
    assertThat(txnHistory.getCreditCardTxnList().contains(txn1)).isEqualTo(true);
    assertThat(txnHistory.getCreditCardTxnList().contains(txn2)).isEqualTo(true);
  }

  @Test
  public void isNewCreditCardTxnValid_testNewCardTxnInThreshold() {
    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;
    Double threshold = 20.0;

    CreditCardTxnHistory txnHistory = new CreditCardTxnHistory();

    boolean response = txnHistory.isNewCreditCardTxnValid(timestamp1, amount, threshold);
    assertThat(response).isEqualTo(true);
  }

  @Test
  public void isNewCreditCardTxnValid_testExistingCardTxnInWindowInThreshold() {
    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-29T23:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount1 = 5.0;
    Double amount2 = 10.0;
    Double threshold = 20.0;

    CreditCardTxnHistory txnHistory = new CreditCardTxnHistory();

    // Mock adding a starting txn in window
    CreditCardTxn txn = new CreditCardTxn(timestamp1, 5.0);
    txnHistory.getCreditCardTxnList().add(txn);
    txnHistory.setCreditCardTotalSpentInSlidingWindow(amount1);
    txnHistory.setStartCreditCardSlidingWindowIndex(0);

    boolean response = txnHistory.isNewCreditCardTxnValid(timestamp2, amount2, threshold);
    assertThat(response).isEqualTo(true);
  }

  @Test
  public void isNewCreditCardTxnValid_testExistingCardTxnInWindowNotInThreshold() {
    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-29T23:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount1 = 5.0;
    Double amount2 = 20.0;
    Double threshold = 20.0;

    CreditCardTxnHistory txnHistory = new CreditCardTxnHistory();

    // Mock adding a starting txn in window
    CreditCardTxn txn = new CreditCardTxn(timestamp1, 5.0);
    txnHistory.getCreditCardTxnList().add(txn);
    txnHistory.setCreditCardTotalSpentInSlidingWindow(amount1);
    txnHistory.setStartCreditCardSlidingWindowIndex(0);

    boolean response = txnHistory.isNewCreditCardTxnValid(timestamp2, amount2, threshold);
    assertThat(response).isEqualTo(false);
  }

  @Test
  public void isNewCreditCardTxnValid_testExistingCardTxnNotInWindowInThreshold() {
    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-30T23:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount1 = 5.0;
    Double amount2 = 10.0;
    Double threshold = 20.0;

    CreditCardTxnHistory txnHistory = new CreditCardTxnHistory();

    // Mock adding a starting txn in window
    CreditCardTxn txn = new CreditCardTxn(timestamp1, amount1);
    txnHistory.getCreditCardTxnList().add(txn);
    txnHistory.setCreditCardTotalSpentInSlidingWindow(amount1);
    txnHistory.setStartCreditCardSlidingWindowIndex(0);

    boolean response = txnHistory.isNewCreditCardTxnValid(timestamp2, amount2, threshold);
    assertThat(response).isEqualTo(true);
  }

  @Test
  public void isNewCreditCardTxnValid_testExistingCardTxnNotInWindowNotInThreshold() {
    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-30T23:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount1 = 15.0;
    Double amount2 = 25.0;
    Double threshold = 20.0;

    CreditCardTxnHistory txnHistory = new CreditCardTxnHistory();

    // Mock adding a starting txn in window
    CreditCardTxn txn = new CreditCardTxn(timestamp1, amount1);
    txnHistory.getCreditCardTxnList().add(txn);
    txnHistory.setCreditCardTotalSpentInSlidingWindow(amount1);
    txnHistory.setStartCreditCardSlidingWindowIndex(0);

    boolean response = txnHistory.isNewCreditCardTxnValid(timestamp2, amount2, threshold);
    assertThat(response).isEqualTo(false);
  }
}
