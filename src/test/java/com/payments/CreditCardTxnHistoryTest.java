package com.payments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.payments.creditcards.CreditCardTxn;
import com.payments.creditcards.CreditCardTxnHistory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

  @Test
  public void updateCreditCardSlidingWindow_testNewCreditCard() throws Exception {
    LocalDateTime timestamp = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;
    CreditCardTxn newTxn = new CreditCardTxn(timestamp, amount);
    CreditCardTxnHistory txnHistory = new CreditCardTxnHistory();

    txnHistory.updateCreditCardSlidingWindow(null, newTxn);
    assertThat(txnHistory.getStartCreditCardSlidingWindowIndex()).isEqualTo(0);
    assertThat(txnHistory.getCreditCardTotalSpentInSlidingWindow()).isEqualTo(newTxn.getCCAmountSpent());
  }

  @Test
  public void updateCreditCardSlidingWindow_testExistingCardNoSliding() throws Exception {

    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-29T20:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;
    CreditCardTxn startTxn = new CreditCardTxn(timestamp1, amount);
    CreditCardTxn newTxn = new CreditCardTxn(timestamp2, amount);
    CreditCardTxnHistory txnHistory = new CreditCardTxnHistory();

    txnHistory.setStartCreditCardSlidingWindowIndex(1);
    txnHistory.setCreditCardTotalSpentInSlidingWindow(amount);

    txnHistory.updateCreditCardSlidingWindow(startTxn, newTxn);

    assertThat(txnHistory.getStartCreditCardSlidingWindowIndex()).isEqualTo(1);
    assertThat(txnHistory.getCreditCardTotalSpentInSlidingWindow()).isEqualTo(20);
  }

  @Test
  public void updateCreditCardSlidingWindow_testExistingCardAndSliding() throws Exception {

    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-30T20:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;

    CreditCardTxn startTxn = new CreditCardTxn(timestamp1, amount);
    CreditCardTxn newTxn = new CreditCardTxn(timestamp2, amount);
    CreditCardTxnHistory txnHistory = new CreditCardTxnHistory();

    List<CreditCardTxn> txnList = new ArrayList<>();
    txnList.add(startTxn);
    txnList.add(newTxn);
    txnHistory.setCreditCardTxnList(txnList);
    txnHistory.setStartCreditCardSlidingWindowIndex(0);
    txnHistory.setCreditCardTotalSpentInSlidingWindow(amount);

    txnHistory.updateCreditCardSlidingWindow(startTxn, newTxn);

    assertThat(txnHistory.getStartCreditCardSlidingWindowIndex()).isEqualTo(1);
    assertThat(txnHistory.getCreditCardTotalSpentInSlidingWindow()).isEqualTo(10);
  }

  @Test
  public void updateCreditCardSlidingWindow_testExistingCardAndIncrementalSliding()
      throws Exception {

    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-28T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-29T20:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    LocalDateTime timestamp3 = LocalDateTime.parse("2014-04-30T21:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    Double amount1 = 10.0;
    Double amount2 = 5.0;
    Double amount3 = 2.0;

    CreditCardTxn startTxn = new CreditCardTxn(timestamp1, amount1);
    CreditCardTxn Txn2 = new CreditCardTxn(timestamp2, amount2);
    CreditCardTxn newTxn = new CreditCardTxn(timestamp3, amount3);

    CreditCardTxnHistory txnHistory = new CreditCardTxnHistory();
    List<CreditCardTxn> txnList = new ArrayList<>();
    txnList.add(startTxn);
    txnList.add(Txn2);
    txnList.add(newTxn);
    txnHistory.setCreditCardTxnList(txnList);

    txnHistory.setStartCreditCardSlidingWindowIndex(0);
    txnHistory.setCreditCardTotalSpentInSlidingWindow(15.0);

    txnHistory.updateCreditCardSlidingWindow(startTxn, newTxn);

    assertThat(txnHistory.getStartCreditCardSlidingWindowIndex()).isEqualTo(2);
    assertThat(txnHistory.getCreditCardTotalSpentInSlidingWindow()).isEqualTo(2.0);
  }
}
