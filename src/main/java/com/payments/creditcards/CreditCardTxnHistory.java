package com.payments.creditcards;

import com.payments.io.Logger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreditCardTxnHistory extends CreditCardTxnHistorySlider implements Logger {

  private List<CreditCardTxn> creditCardTxnList;

  public CreditCardTxnHistory() {
    creditCardTxnList = new ArrayList<CreditCardTxn>();
  }

  public List<CreditCardTxn> getCreditCardTxnList() {
    return creditCardTxnList;
  }

  public void setCreditCardTxnList(List<CreditCardTxn> source) {
    creditCardTxnList = source.stream().collect(Collectors.toList());
  }

  public void addCreditCardTxn(CreditCardTxn newTxn) {
    try {
      creditCardTxnList.add(newTxn);
      updateCreditCardSlidingWindow(getStartCreditCardTxn(), newTxn);
    } catch (NullPointerException exception) {
      Logger.printErrorCommandLine("Error adding transaction to Credit Card:" + exception.getMessage());
      exception.printStackTrace();
    } catch (Exception exception) {
      Logger.printErrorCommandLine("Error adding transaction to Credit Card:" + exception.getMessage());
      exception.printStackTrace();
    }
  }

  public CreditCardTxn getStartCreditCardTxn() {

    if (isNewCreditCard()) {
      return null;
    }

    try {

      return creditCardTxnList.get(getStartCreditCardSlidingWindowIndex());

    } catch (IndexOutOfBoundsException e) {
      Logger.printErrorCommandLine(e.getMessage());
      e.printStackTrace();
      return null;
    }
  }

  public boolean isNewCreditCardTxnValid(LocalDateTime timestamp, Double amount,
      Double creditCardThreshold) {

    try {
      Double newCreditCardTotalSpend = findNewTotalSpendInWindow(timestamp, amount);

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

  public Integer findNewStartIndex(CreditCardTxn startTxn, CreditCardTxn newTxn) throws Exception {

    Integer startIdx = getStartCreditCardSlidingWindowIndex();

    if (isNewCreditCard()) {
      startIdx = startIdx + 1;
      return startIdx;
    } else {

      //If new transaction in window, No changes to start index
      if (isRangeInSlidingWindow(startTxn.getCCTimestamp(), newTxn.getCCTimestamp())) {
        return startIdx;
      }

      // If new transaction moves the sliding window, update the start index and deduct the amount
      // Do this until we find the new starting transaction in the sliding window range
      Integer curIdx = startIdx;

      while (curIdx < getCreditCardTxnList().size()) {

        // Not in range, update start index
        if (isRangeInSlidingWindow(getCreditCardTxnList().get(curIdx).getCCTimestamp(),
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
  public Double findNewTotalSpendInWindow(LocalDateTime timestamp, Double amount) throws Exception {

    Double newCreditCardTotalSpend;

    if (isNewCreditCard() ||
        isRangeInSlidingWindow(getStartCreditCardTxn().getCCTimestamp(), timestamp)) {

      newCreditCardTotalSpend = sumAmountInWindow(amount);

    } else {

      CreditCardTxn startTxn = getStartCreditCardTxn();
      CreditCardTxn newTxn = new CreditCardTxn(timestamp, amount);
      Integer newStartIdx = findNewStartIndex(startTxn, newTxn);

      Integer curIdx = getStartCreditCardSlidingWindowIndex();
      newCreditCardTotalSpend = getCreditCardTotalSpentInSlidingWindow();
      CreditCardTxn curTxn = startTxn;

      while (curIdx < creditCardTxnList.size() &&
          curIdx <= newStartIdx) {
        newCreditCardTotalSpend = newCreditCardTotalSpend - curTxn.getCCAmountSpent();
        curIdx++;
      }
      newCreditCardTotalSpend = newCreditCardTotalSpend + amount;
    }

    return newCreditCardTotalSpend;
  }

  public void updateCreditCardSlidingWindow(CreditCardTxn startTxn, CreditCardTxn newTxn) {

    try {
      Integer newStartIdx = findNewStartIndex(startTxn, newTxn);

      Double newAmount = getCreditCardTotalSpentInSlidingWindow() + newTxn.getCCAmountSpent();
      Integer curIdx = getStartCreditCardSlidingWindowIndex();

      // If no changes to starting index of window, just sum the new txn amount
      if (curIdx.equals(newStartIdx)) {
        setCreditCardTotalSpentInSlidingWindow(newAmount);
        return;
      }

      if (!isNewCreditCard()) {
        // Loop through transactions in the list that are no longer in sliding window and deduct amount
        while (curIdx < getCreditCardTxnList().size() &&
            curIdx < newStartIdx) {

          CreditCardTxn curTxn = creditCardTxnList.get(curIdx);
          newAmount = newAmount - curTxn.getCCAmountSpent();
          curIdx++;

        }
      }

      setCreditCardTotalSpentInSlidingWindow(newAmount);
      setStartCreditCardSlidingWindowIndex(newStartIdx);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
