package com.payments.creditcards;

public abstract class CreditCardTxnHistorySlider implements CreditCardTxnHistorySlidingWindow {

  private static final Integer NEW_CARD = -1;
  private Double creditCardTotalSpentInSlidingWindow = 0.0;
  private Integer startCreditCardSlidingWindowIndex = NEW_CARD;

  public Double getCreditCardTotalSpentInSlidingWindow() { return creditCardTotalSpentInSlidingWindow; }
  public void setCreditCardTotalSpentInSlidingWindow(Double amount) { creditCardTotalSpentInSlidingWindow = amount; }

  public boolean isNewCreditCard() { return startCreditCardSlidingWindowIndex.equals(NEW_CARD); }

  public Integer getStartCreditCardSlidingWindowIndex() { return startCreditCardSlidingWindowIndex; }

  public void setStartCreditCardSlidingWindowIndex(Integer index) { startCreditCardSlidingWindowIndex = index; }

  public Double sumAmountInWindow(Double amount) {
    return getCreditCardTotalSpentInSlidingWindow() + amount;
  }

  public void updateCreditCardSlidingWindow(CreditCardTxn startTxn, CreditCardTxn newTxn) {

    // First transaction in card
    if (isNewCreditCard()) {
      startCreditCardSlidingWindowIndex++;

    } else if (!isRangeInSlidingWindow(startTxn.getCCTimestamp(), newTxn.getCCTimestamp())) {
      // If new transaction moves the sliding window, update the start index and deduct the amount
      creditCardTotalSpentInSlidingWindow -= startTxn.getCCAmountSpent();
      startCreditCardSlidingWindowIndex++;
    }

    // New transaction amount is included in the Sliding Window
    creditCardTotalSpentInSlidingWindow += newTxn.getCCAmountSpent();
  }
}
