package com.payments.creditcards;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface CreditCardTxnHistorySlidingWindow {

  int SLIDING_WINDOW = 24;
  ChronoUnit SLIDING_WINDOW_UNIT = ChronoUnit.HOURS;

  default Long timeDiff(LocalDateTime start, LocalDateTime end) {
    return SLIDING_WINDOW_UNIT.between(start, end);
  }

  default boolean isRangeInSlidingWindow(LocalDateTime start, LocalDateTime end) {
    return timeDiff(start, end) <= SLIDING_WINDOW;
  }

}
