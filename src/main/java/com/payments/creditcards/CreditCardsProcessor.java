package com.payments.creditcards;

import com.payments.io.CsvProcessor;

import com.payments.io.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreditCardsProcessor implements CsvProcessor, Logger {

  private final Double creditCardThreshold;

  private HashMap<String, CreditCard> creditCardsMap;

  private List<String> fraudulentCreditCardsHashList;

  public CreditCardsProcessor(Double threshold) {

    creditCardThreshold = threshold;
    creditCardsMap = new HashMap<String, CreditCard>();
    fraudulentCreditCardsHashList = new ArrayList<String>();
  }

  public Integer getTotalCreditCardsNum() { return creditCardsMap.size(); }

  public List<String> getFraudulentCreditCardsHashList() {
    return fraudulentCreditCardsHashList;
  }

  public void processCreditCardTxnAllString(String creditCardTxnString, String delimiter) {

    String[] creditCardTxnDetails = creditCardTxnString.split(delimiter);

    if (creditCardTxnDetails.length != 3) {

      Logger.printErrorCommandLine("Could not process the credit card txn due to lack of information: "
          + Arrays.toString(creditCardTxnDetails));
      return;
    }

    try {

      // Convert Strings into meaningful data structures to process the card : processCreditCardTxn
      String hash = creditCardTxnDetails[0].trim();
      LocalDateTime timestamp = LocalDateTime.parse(creditCardTxnDetails[1].trim(),
          DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      Double amount = Double.parseDouble(creditCardTxnDetails[2].trim());

      processCreditCardTxn(hash, timestamp, amount);

    } catch (Exception exception) {
      Logger.printErrorCommandLine("Error parsing inputs for credit card txn :" + creditCardTxnString);
      exception.printStackTrace();
    }

  }

  public void processCreditCardTxn(String hash, LocalDateTime timestamp, Double amount) {

    CreditCard creditCard = getCreditCard(hash, timestamp, amount);

    if (creditCard == null) {
      Logger.printErrorCommandLine("Could not process the credit card txn: " +
          " Hash" + hash +
          ", timestamp: " + timestamp +
          ", amount: " + amount);
      return;
    }

    if (!creditCard.isCreditCardTxnValid(timestamp, amount, creditCardThreshold)) {

      updCreditCardAsInvalid(creditCard);
    } else {
      // Add a new credit card transaction
      creditCard.addNewCreditCardTxn(timestamp, amount);
    }
  }

  public CreditCard getCreditCard(String hash, LocalDateTime timestamp, Double amount) {

    CreditCard creditCard = null;
    try {

      // Check if we know this credit card already
      if (creditCardsMap.containsKey(hash)) {
        creditCard = creditCardsMap.get(hash);

      } else {
        // Add a new credit card
        creditCard = new CreditCard(hash);
        updateProcessorNewCreditCard(creditCard);
      }

    } catch (NullPointerException | IndexOutOfBoundsException | DateTimeParseException exception) {
      Logger.printErrorCommandLine("Error retrieving Credit Card:" + exception.getMessage());
      exception.printStackTrace();
    }

    return creditCard;
  }

  public void updateProcessorNewCreditCard(CreditCard creditCard) {
    String hash = creditCard.getCcHash();
    creditCardsMap.put(hash, creditCard);
  }

  public CreditCard getCreditCardFromHash(String hash) {
    return creditCardsMap.get(hash);
  }

  public void updCreditCardAsInvalid(CreditCard creditCard) {

    if (!creditCard.isCreditCardFraudulent()) return;
    
    creditCard.setCreditCardInvalid();
    fraudulentCreditCardsHashList.add(creditCard.getCcHash());
  }

  public void processInputLine(String line) {
    processCreditCardTxnAllString(line, delimiter);
  }

  public void printFraudulentCreditCards(String filePath) {
    processInputFile(filePath);
    List<String> fraudulentCreditCardsList = getFraudulentCreditCardsHashList();
    Logger.printCommandLine("\nLIST OF FRAUDULENT CREDIT CARDS HASH:");
    Logger.printCommandLine(fraudulentCreditCardsList);
  }
}