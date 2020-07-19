package com.payments.creditcards;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreditCardTxnHistory extends CreditCardTxnHistorySlider {

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
      System.err.println("Error adding transaction to Credit Card:" + exception.getMessage());
      exception.printStackTrace();
    } catch (Exception exception) {
      System.err.println("Error adding transaction to Credit Card:" + exception.getMessage());
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
      System.err.println(e.getMessage());
      e.printStackTrace();
      return null;
    }
  }

  public boolean isNewCreditCardTxnValid(LocalDateTime timestamp, Double amount,
      Double creditCardThreshold) {
    Double newCreditCardTotalSpend;

    try {

      if (isNewCreditCard() ||
          isRangeInSlidingWindow(getStartCreditCardTxn().getCCTimestamp(), timestamp)) {

        newCreditCardTotalSpend = sumAmountInWindow(amount);

      } else {
        newCreditCardTotalSpend =
            sumAmountInWindow(amount) - getStartCreditCardTxn().getCCAmountSpent();
      }
      return isAmountInThreshold(newCreditCardTotalSpend, creditCardThreshold);

    } catch (IndexOutOfBoundsException | NullPointerException exception) {
      System.err.println("Error adding transaction to Credit Card:" + exception.getMessage());
      exception.printStackTrace();
      return false;
    }
  }

  private boolean isAmountInThreshold(Double amount, Double creditCardThreshold) {
    return amount <= creditCardThreshold;
  }

  public void updateCreditCardSlidingWindow(CreditCardTxn startTxn, CreditCardTxn newTxn)
      throws Exception {

    // First transaction in card
    if (isNewCreditCard()) {
      setStartCreditCardSlidingWindowIndex(getStartCreditCardSlidingWindowIndex() + 1);

    } else {

      // If new transaction moves the sliding window, update the start index and deduct the amount
      // Do this until we find the new starting transaction in the sliding window range
      Integer curIdx = getStartCreditCardSlidingWindowIndex();
      CreditCardTxn curTxn = startTxn;

      while (curTxn != newTxn && !isRangeInSlidingWindow(curTxn.getCCTimestamp(),
          newTxn.getCCTimestamp())) {

        setCreditCardTotalSpentInSlidingWindow(
            getCreditCardTotalSpentInSlidingWindow() - curTxn.getCCAmountSpent());

        setStartCreditCardSlidingWindowIndex(curIdx + 1);

        curIdx++;
        try {
          curTxn = getCreditCardTxnList().get(curIdx);
        } catch (IndexOutOfBoundsException e) {
          throw new Exception(
              "Error encountered in updating sliding window: Reached end of the transaction list");
        }
      }
    }

    // New transaction amount is included in the Sliding Window
    setCreditCardTotalSpentInSlidingWindow(
        getCreditCardTotalSpentInSlidingWindow() + newTxn.getCCAmountSpent());
  }
}
