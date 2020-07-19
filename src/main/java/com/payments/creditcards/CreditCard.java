package com.payments.creditcards;

import java.time.LocalDateTime;

public class CreditCard {

  private String CCHash;
  private CreditCardTxnHistory CCTxnHistory;
  private boolean CCValid;

  public CreditCard(String hash) {
    CCHash = hash;
    CCTxnHistory = new CreditCardTxnHistory();
    CCValid = true;
  }

  public String getCCHash() {
    return CCHash;
  }

  public CreditCardTxnHistory getCCTxnHistory() { return CCTxnHistory; }

  public boolean isCreditCardValid() { return CCValid; }

  public void setCreditCardInvalid() { this.CCValid = false; }

  public void addNewCreditCardTxn(LocalDateTime timestamp, Double amount) {
    CCTxnHistory.addCreditCardTxn(new CreditCardTxn(timestamp, amount));
  }

  public boolean isCreditCardTxnValid(LocalDateTime timestamp, Double amount,
      Double creditCardThreshold) {
    try {

      return isCreditCardValid() &&
          CCTxnHistory.isNewCreditCardTxnValid(timestamp, amount, creditCardThreshold);

    } catch (NullPointerException exception) {
      System.err.println("Error checking for credit card validity: " + exception.getMessage());
      exception.printStackTrace();
      return false;
    }
  }
}