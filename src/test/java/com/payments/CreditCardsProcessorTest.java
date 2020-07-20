package com.payments;
import static org.assertj.core.api.Assertions.assertThat;

import com.payments.creditcards.CreditCard;
import com.payments.creditcards.CreditCardsProcessor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.Test;

public class CreditCardsProcessorTest {

  @Test
  public void processCreditCardTxn_testNewCreditCard() {
    Double threshold = 150.0;
    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";
    LocalDateTime timestamp = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;

    CreditCardsProcessor processor = new CreditCardsProcessor(threshold);
    processor.processCreditCardTxn(hash, timestamp, amount);

    assertThat(processor.getFraudulentCreditCardsHashList()).isEmpty();
    assertThat(processor.getCreditCardFromHash(hash).getCcHash()).isEqualTo(hash);
  }

  @Test
  public void getCreditCard_testNewCC() {
    Double threshold = 150.0;
    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";
    LocalDateTime timestamp = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;

    CreditCardsProcessor processor = new CreditCardsProcessor(threshold);
    CreditCard cc = processor.getCreditCard(hash, timestamp, amount);
    assertThat(processor.getFraudulentCreditCardsHashList()).isEmpty();
    assertThat(processor.getCreditCardFromHash(hash).getCcHash()).isEqualTo(hash);
    assertThat(cc.getCcHash()).isEqualTo(hash);
  }

  @Test
  public void getCreditCard_testExistingCC() {
    Double threshold = 150.0;
    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";
    LocalDateTime timestamp = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount = 10.0;

    CreditCardsProcessor processor = new CreditCardsProcessor(threshold);
    CreditCard creditCard = new CreditCard(hash);
    processor.updateProcessorNewCreditCard(creditCard);

    CreditCard cc = processor.getCreditCard(hash, timestamp, amount);
    assertThat(processor.getFraudulentCreditCardsHashList()).isEmpty();
    assertThat(processor.getCreditCardFromHash(hash).getCcHash()).isEqualTo(hash);
    assertThat(cc.getCcHash()).isEqualTo(hash);
    assertThat(cc).isEqualTo(creditCard);
  }

  @Test
  public void updCreditCardAsInvalid_test() {
    Double threshold = 150.0;
    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";
    CreditCardsProcessor processor = new CreditCardsProcessor(threshold);
    CreditCard creditCard = new CreditCard(hash);

    processor.updCreditCardAsInvalid(creditCard);
    assertThat(processor.getFraudulentCreditCardsHashList().contains(hash)).isEqualTo(true);
  }

  @Test
  public void processCreditCardTxn_testValidTxnsPerCardInWindow() {
    Double threshold = 150.0;

    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";

    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount1 = 10.0;

    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-29T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount2 = 10.0;

    CreditCardsProcessor processor = new CreditCardsProcessor(threshold);
    processor.processCreditCardTxn(hash, timestamp1, amount1);
    processor.processCreditCardTxn(hash, timestamp2, amount2);

    assertThat(processor.getFraudulentCreditCardsHashList().isEmpty()).isEqualTo(true);
    assertThat(processor.getTotalCreditCardsNum()).isEqualTo(1);
  }

  @Test
  public void processCreditCardTxn_testValidTxnsPerCardNotInWindow() {
    Double threshold = 150.0;

    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";

    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-28T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount1 = 10.0;

    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-30T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount2 = 10.0;

    CreditCardsProcessor processor = new CreditCardsProcessor(threshold);
    processor.processCreditCardTxn(hash, timestamp1, amount1);
    processor.processCreditCardTxn(hash, timestamp2, amount2);

    assertThat(processor.getFraudulentCreditCardsHashList().isEmpty()).isEqualTo(true);
    assertThat(processor.getTotalCreditCardsNum()).isEqualTo(1);
  }

  @Test
  public void processCreditCardTxn_testInValidTxnsPerCardInWindow() {
    Double threshold = 15.0;

    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";

    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-28T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount1 = 10.0;

    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-28T23:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount2 = 10.0;

    CreditCardsProcessor processor = new CreditCardsProcessor(threshold);
    processor.processCreditCardTxn(hash, timestamp1, amount1);
    processor.processCreditCardTxn(hash, timestamp2, amount2);

    assertThat(processor.getFraudulentCreditCardsHashList().isEmpty()).isEqualTo(false);
    assertThat(processor.getTotalCreditCardsNum()).isEqualTo(1);
    assertThat(processor.getFraudulentCreditCardsHashList().contains(hash)).isEqualTo(true);
  }

  @Test
  public void processCreditCardTxn_testInValidTxnsPerCardNotInWindow() {
    Double threshold = 5.0;

    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";

    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-28T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount1 = 10.0;

    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-30T23:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount2 = 10.0;

    CreditCardsProcessor processor = new CreditCardsProcessor(threshold);
    processor.processCreditCardTxn(hash, timestamp1, amount1);
    processor.processCreditCardTxn(hash, timestamp2, amount2);

    assertThat(processor.getFraudulentCreditCardsHashList().isEmpty()).isEqualTo(false);
    assertThat(processor.getTotalCreditCardsNum()).isEqualTo(1);
    assertThat(processor.getFraudulentCreditCardsHashList().contains(hash)).isEqualTo(true);
  }

  @Test
  public void processCreditCardTxn_testInValidTxnsMultiCards() {
    Double threshold = 5.0;

    String hash1 = "10d7ce2f43e35fa57d1bbf8b1e2";
    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-28T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount1 = 10.0;

    String hash2 = "10d7ce2f43e35fa57d1bbf8b1e3";
    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-30T23:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount2 = 10.0;

    CreditCardsProcessor processor = new CreditCardsProcessor(threshold);
    processor.processCreditCardTxn(hash1, timestamp1, amount1);
    processor.processCreditCardTxn(hash2, timestamp2, amount2);

    assertThat(processor.getFraudulentCreditCardsHashList().isEmpty()).isEqualTo(false);
    assertThat(processor.getTotalCreditCardsNum()).isEqualTo(2);
    assertThat(processor.getFraudulentCreditCardsHashList().contains(hash1)).isEqualTo(true);
    assertThat(processor.getFraudulentCreditCardsHashList().contains(hash2)).isEqualTo(true);
  }

  @Test
  public void processCreditCardTxn_testInValidTxnsSameCard() {
    Double threshold = 5.0;

    String hash = "10d7ce2f43e35fa57d1bbf8b1e2";
    LocalDateTime timestamp1 = LocalDateTime.parse("2014-04-28T13:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount1 = 10.0;

    LocalDateTime timestamp2 = LocalDateTime.parse("2014-04-30T23:15:54",
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    Double amount2 = 10.0;

    CreditCardsProcessor processor = new CreditCardsProcessor(threshold);
    processor.processCreditCardTxn(hash, timestamp1, amount1);
    processor.processCreditCardTxn(hash, timestamp2, amount2);

    assertThat(processor.getFraudulentCreditCardsHashList().isEmpty()).isEqualTo(false);
    assertThat(processor.getTotalCreditCardsNum()).isEqualTo(1);
    assertThat(processor.getFraudulentCreditCardsHashList().contains(hash)).isEqualTo(true);
  }

  @Test
  public void processCreditCardTxnAllString_test() {
    String line1 = "10d7ce2f43e35fa57d1bbf8b1e3, 2014-04-29T13:15:54, 100.00";
    String line2 = "10d7ce2f43e35fa57d1bbf8b1e4, 2014-04-29T13:15:54";
    String line3 = "10d7ce2f43e35fa57d1bbf8b1e5,2014-04-29T13:15:53,x";
    String delimitator = ",";

    CreditCardsProcessor creditCardsProcessor = new CreditCardsProcessor(100.0);

    System.out.println("Test1: Correct CSV format.");
    creditCardsProcessor.processCreditCardTxnAllString(line1,delimitator);
    assertThat(creditCardsProcessor.getCreditCardFromHash("10d7ce2f43e35fa57d1bbf8b1e3").isCreditCardFraudulent()).isEqualTo(true);

    System.out.println("Test2: Incorrect CSV format. Expecting error message.");
    creditCardsProcessor.processCreditCardTxnAllString(line2,delimitator);
    assertThat(creditCardsProcessor.getCreditCardFromHash("10d7ce2f43e35fa57d1bbf8b1e4")).isEqualTo(null);

    System.out.println("Test3: Incorrect input format in CSV. Expecting error message.");
    creditCardsProcessor.processCreditCardTxnAllString(line3,delimitator);
    assertThat(creditCardsProcessor.getCreditCardFromHash("10d7ce2f43e35fa57d1bbf8b1e5")).isEqualTo(null);
  }
}
