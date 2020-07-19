package com.payments;

import com.payments.creditcards.CreditCardsProcessor;

public class Main {

  public static void main(String[] args) {

    if (args.length != 2) {
      System.err.println("Incorrect input args: " +
          args.length +
          "\nEnter 2 arguments: threshold filePath");
      return;
    }

    Double threshold = Double.parseDouble(args[0]);
    String filePath = args[1];

    CreditCardsProcessor creditCardsProcessor = new CreditCardsProcessor(threshold);

    creditCardsProcessor.processInputFile(filePath);

    System.out.println("\nLIST OF FRAUDULENT CREDIT CARDS HASH:");
    creditCardsProcessor.getFraudulentCreditCardsHashList().forEach(System.out::println);
  }
}
