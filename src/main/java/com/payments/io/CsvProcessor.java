package com.payments.io;

import com.google.common.io.Resources;
import com.payments.creditcards.CreditCardsProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public interface CsvProcessor {

  public static String delimitator = ",";

  default void processInputFile(String inputFilePath) {
    List<String[]> inputList = null;
    try {
      InputStream inputFS;
      if (inputFilePath.isEmpty()) {
        inputFS = new FileInputStream(new File(Resources.getResource("creditcards.csv").getFile()));
      } else {
        File inputF = new File(inputFilePath);
        inputFS = new FileInputStream(inputF);
      }

      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
      String line = null;

      // Processing line by line in case long file
      while ((line = br.readLine()) != null) {
        processInputLine(line);
      }

      br.close();

    } catch (IOException exception) {
      System.err.println("Error reading the given file: " + exception.getMessage());
      exception.printStackTrace();
    }
  }

  public void processInputLine(String line);
}
