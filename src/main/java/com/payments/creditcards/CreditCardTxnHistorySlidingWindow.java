package com.payments.creditcards;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CreditCardTxnHistorySlidingWindow {

  int SLIDING_WINDOW = 24;
  ChronoUnit SLIDING_WINDOW_UNIT = ChronoUnit.HOURS;

  public Long timeDiff(LocalDateTime start, LocalDateTime end) {
    return SLIDING_WINDOW_UNIT.between(start, end);
  }

  public boolean isRangeInSlidingWindow(LocalDateTime start, LocalDateTime end) {
    return timeDiff(start, end) <= SLIDING_WINDOW;
  }

}
