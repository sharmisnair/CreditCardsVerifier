package com.payments.creditcards;

import com.payments.io.Logger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreditCardTxnHistory implements Logger {

  private List<CreditCardTxn> creditCardTxnList;
  CreditCardTxnHistorySlider txnHistorySlider;
  CreditCardTxnHistorySlidingWindow txnHistorySlidingWindow;

  public CreditCardTxnHistory() {
    creditCardTxnList = new ArrayList<CreditCardTxn>();
    txnHistorySlidingWindow = new CreditCardTxnHistorySlidingWindow();
    txnHistorySlider = new CreditCardTxnHistorySlider();
  }

  public List<CreditCardTxn> getCreditCardTxnList() {
    return creditCardTxnList;
  }

  public void setCreditCardTxnList(List<CreditCardTxn> source) {
    creditCardTxnList = source.stream().collect(Collectors.toList());
  }

  public CreditCardTxnHistorySlider getCreditCardTxnHistorySlider() {
    return txnHistorySlider;
  }

  public void addCreditCardTxn(CreditCardTxn newTxn) {
    try {
      creditCardTxnList.add(newTxn);
      updateCreditCardSlidingWindow(getStartCreditCardTxn(), newTxn);

    } catch (NullPointerException exception) {
      Logger.printErrorCommandLine(
          "Error adding transaction to Credit Card:" + exception.getMessage());
      exception.printStackTrace();
    } catch (Exception exception) {
      Logger.printErrorCommandLine(
          "Error adding transaction to Credit Card:" + exception.getMessage());
      exception.printStackTrace();
    }
  }

  public CreditCardTxn getStartCreditCardTxn() {

    if (txnHistorySlider.isNewCreditCard()) {
      return null;
    }

    try {

      return creditCardTxnList.get(txnHistorySlider.getStartCreditCardSlidingWindowIndex());

    } catch (IndexOutOfBoundsException e) {
      Logger.printErrorCommandLine(e.getMessage());
      e.printStackTrace();
      return null;
    }
  }

  public boolean isNewCreditCardTxnValid(LocalDateTime timestamp, Double amount,
      Double creditCardThreshold) {

    try {

      Integer newStartIdx = findNewStartIndex(getStartCreditCardTxn(), new CreditCardTxn(timestamp, amount));
      Double newCreditCardTotalSpend = findNewTotalSpendInWindow(timestamp, amount, newStartIdx);

      return isAmountInThreshold(newCreditCardTotalSpend, creditCardThreshold);

    } catch (Exception e) {
      Logger.printErrorCommandLine("Error checking new credit card txn.");
      e.printStackTrace();
      return false;
    }

  }

  private boolean isAmountInThreshold(Double amount, Double creditCardThreshold) {
    return amount <= creditCardThreshold;
  }

  /*
  Core Algorithm for sliding window technique to find the new starting Index of window:
  As new transactions come in, it would be one of the two following cases :
  #### Case 1: Timestamp is within the 24 hours of the timestamp of starting transaction
  * No change to start index (aka startCreditCardSlidingWindowIndex)
  * creditCardTotalSpentInSlidingWindow = creditCardTotalSpentInSlidingWindow + amount in transaction

  #### Case 2: Timestamp is past 24 hours of the timestamp of starting transaction
  Move through the CreditCardTxnList linked list and update below until we find the starting transaction
  * Increment startCreditCardSlidingWindowIndex until we find the transaction marking the starting transaction in the past 24 hours window
  * creditCardTotalSpentInSlidingWindow = creditCardTotalSpentInSlidingWindow + amount in transaction
  */

  public void updateCreditCardSlidingWindow(CreditCardTxn startTxn, CreditCardTxn newTxn) {
    try {

      Integer newStartIdx = findNewStartIndex(startTxn, newTxn);
      Double newAmount = findNewTotalSpendInWindow(newTxn.getCCTimestamp(),
          newTxn.getCCAmountSpent(), newStartIdx);
      txnHistorySlider.setCreditCardSlidingWindow(newStartIdx, newAmount);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Integer findNewStartIndex(CreditCardTxn startTxn, CreditCardTxn newTxn) throws Exception {

    Integer startIdx = txnHistorySlider.getStartCreditCardSlidingWindowIndex();

    if (txnHistorySlider.isNewCreditCard()) {
      startIdx = startIdx + 1;
      return startIdx;
    } else {

      //If new transaction in window, No changes to start index
      if (txnHistorySlidingWindow
          .isRangeInSlidingWindow(startTxn.getCCTimestamp(), newTxn.getCCTimestamp())) {
        return startIdx;
      }

      // If new transaction moves the sliding window, update the start index and deduct the amount
      // Do this until we find the new starting transaction in the sliding window range
      Integer curIdx = startIdx;

      while (curIdx < getCreditCardTxnList().size()) {

        // Not in range, update start index
        if (txnHistorySlidingWindow
            .isRangeInSlidingWindow(getCreditCardTxnList().get(curIdx).getCCTimestamp(),
                newTxn.getCCTimestamp())) {
          startIdx = curIdx;
          break;
        }
        curIdx++;
      }

      // Reached end of CreditCardTxnList linked list and none in the time window range
      // so the new transaction that is not yet added to list will be new start index
      if (curIdx.equals(getCreditCardTxnList().size())) {

        startIdx = getCreditCardTxnList().size();
      }
    }
    return startIdx;
  }

  /*
   * When a new transaction with timestamp/amount is being added, calculate the new total spend
   */
  private Double findNewTotalSpendInWindow(LocalDateTime timestamp, Double amount,
      Integer newStartIdx) throws Exception {

    Double newCreditCardTotalSpend;

    if (txnHistorySlider.isNewCreditCard() ||
        txnHistorySlidingWindow.isRangeInSlidingWindow(getStartCreditCardTxn().getCCTimestamp(), timestamp)) {

      newCreditCardTotalSpend = txnHistorySlider.getCreditCardTotalSpentInSlidingWindow() + amount;

    } else {

      Integer curIdx = txnHistorySlider.getStartCreditCardSlidingWindowIndex();
      newCreditCardTotalSpend = txnHistorySlider.getCreditCardTotalSpentInSlidingWindow();
      CreditCardTxn curTxn;

      while (curIdx < creditCardTxnList.size() &&
          curIdx != newStartIdx) {
        curTxn = getCreditCardTxnList().get(curIdx);
        newCreditCardTotalSpend = newCreditCardTotalSpend - curTxn.getCCAmountSpent();
        curIdx++;

      }
      newCreditCardTotalSpend = newCreditCardTotalSpend + amount;
    }

    return newCreditCardTotalSpend;
  }
}
