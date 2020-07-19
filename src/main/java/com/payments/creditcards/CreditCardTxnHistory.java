package com.payments.creditcards;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreditCardTxnHistory extends CreditCardTxnHistorySlider {

  private List<CreditCardTxn> creditCardTxnList;

  public CreditCardTxnHistory() {
    creditCardTxnList = new ArrayList<CreditCardTxn>();
  }

  public List<CreditCardTxn> getCreditCardTxnList() {
    return creditCardTxnList;
  }

  public void addCreditCardTxn(CreditCardTxn newTxn) {
    try {
      creditCardTxnList.add(newTxn);
      updateCreditCardSlidingWindow(getStartCreditCardTxn(), newTxn);
    } catch (NullPointerException exception) {
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

  public boolean isNewCreditCardTxnValid(LocalDateTime timestamp, Double amount, Double creditCardThreshold) {
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
}
