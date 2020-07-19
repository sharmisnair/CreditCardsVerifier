package com.payments.io;

import java.util.List;

public interface Logger {
  static void printCommandLine(List<String> list) {
    list.forEach(System.out::println);
  }

  static void printCommandLine(String str) {
    System.out.println(str);
  }

  static void printErrorCommandLine(String str) {
    System.err.println(str);
  }
}
