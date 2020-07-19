package com.payments;

import com.payments.creditcards.CreditCardsProcessor;
import com.payments.io.Logger;

public class Main implements Logger {

  public static void main(String[] args) {

    if (args.length != 2) {
      Logger.printErrorCommandLine("Incorrect input args: " +
          args.length +
          "\nEnter 2 arguments: threshold filePath");
      return;
    }

    Double threshold = Double.parseDouble(args[0]);
    String filePath = args[1];

    CreditCardsProcessor creditCardsProcessor = new CreditCardsProcessor(threshold);
    creditCardsProcessor.printFraudulentCreditCards(filePath);
  }
}
