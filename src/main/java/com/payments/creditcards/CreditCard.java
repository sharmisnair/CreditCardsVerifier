package com.payments.creditcards;

import com.payments.io.Logger;
import java.time.LocalDateTime;

public class CreditCard implements Logger {

  private String ccHash;
  private CreditCardTxnHistory ccTxnHistory;
  private boolean ccFraudulent;

  public CreditCard(String hash) {
    ccHash = hash;
    ccTxnHistory = new CreditCardTxnHistory();
    ccFraudulent = true;
  }

  public String getCcHash() {
    return ccHash;
  }

  public CreditCardTxnHistory getCcTxnHistory() { return ccTxnHistory; }

  public boolean isCreditCardFraudulent() { return ccFraudulent; }

  public void setCreditCardInvalid() { this.ccFraudulent = false; }

  public void addNewCreditCardTxn(LocalDateTime timestamp, Double amount) {
    ccTxnHistory.addCreditCardTxn(new CreditCardTxn(timestamp, amount));
  }

  public boolean isCreditCardTxnValid(LocalDateTime timestamp, Double amount,
      Double creditCardThreshold) {
    try {

      return isCreditCardFraudulent() &&
          ccTxnHistory.isNewCreditCardTxnValid(timestamp, amount, creditCardThreshold);

    } catch (Exception exception) {
      Logger.printErrorCommandLine("Error checking for credit card validity: " + exception.getMessage());
      exception.printStackTrace();
      return false;
    }
  }
}