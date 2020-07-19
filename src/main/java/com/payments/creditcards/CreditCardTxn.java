package com.payments.creditcards;

import java.time.LocalDateTime;

public class CreditCardTxn {
  private LocalDateTime CCTimestamp;
  private Double CCAmountSpent;

  public CreditCardTxn(LocalDateTime timestamp, Double amountSpent) {
    CCTimestamp = timestamp;
    CCAmountSpent = amountSpent;
  }

  public LocalDateTime getCCTimestamp() { return CCTimestamp; }
  public Double getCCAmountSpent() { return CCAmountSpent; }
}
