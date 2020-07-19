# CreditCardsVerifier
CreditCardsVerifier is a Java project to detect fraudulent credit cards given a list of credit card transactions.

## Problem description

A credit card transaction comprises the following elements.
* hashed credit card number
* timestamp - of format year-month-dayThour:minute:second
* amount - of format dollars.cents

Transactions are received as input csv file, one transaction per line. eg:
```
10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00
```
The file passed to the app will contain a sequence of transactions in chronological order.
A credit card is identified as fraudulent if the sum of amounts for a unique hashed credit card number over a 24-hour sliding window period exceeds the price threshold.
The app should print out the hashed credit card numbers that have been identified as
fraudulent.

## Assumptions

* The file can be large and so processing to be done line by line rather than reading the entire contents of file into memory.

## How to run the app

### Pre-requisites

* Maven

### Step by step instructions

Run the below commands from terminal after cloning this repository:
1. mvn package
2. Run the below command to execute the program: 
```java -cp target/CreditCardsVerifier-1.0.jar com.payments.Main 150 src/main/resources/creditcards.csv```

Two arguments are passed to the com.payments.Main program:
* Threshold value (150 in this example)
* Path to CSV file with transactions (src/main/resources/creditcards.csv in this example)

## Solution

### Algorithm Overview

*Sliding window technique* used in the program optimises for time with a **time complexity of O(n)** to process a transaction and detect if it is fraudulent.

This involves storing transactions per credit card and tracking the window of transactions that are within the 24 hour window.

* *creditCardTotalSpentInSlidingWindow*: The total sum of all transaction amounts for the given credit card in the past 24 hours as you increment the start index
* *startCreditCardSlidingWindowIndex*: Start index to the transaction of the given credit card which marks the first transaction in the past 24 hours

As new transactions come in, it would be one of the two following cases :
#### Case 1: Timestamp is within the 24 hours of the timestamp of starting transaction
Update:
* No change to start index (aka startCreditCardSlidingWindowIndex)
* creditCardTotalSpentInSlidingWindow = creditCardTotalSpentInSlidingWindow + amount in transaction

#### Case 2: Timestamp is past 24 hours of the timestamp of starting transaction
Move through the transaction linked list and update below until we find the starting transaction
* Increment startCreditCardSlidingWindowIndex until we find the transaction marking the starting transaction in the past 24 hours window
* Subtract the amount of old txn from creditCardTotalSpentInSlidingWindow as you increment the start index
Finally, creditCardTotalSpentInSlidingWindow = creditCardTotalSpentInSlidingWindow + amount in new transaction

### Design Overview

UML Diagram of Classes and relationships
![UML Diagram](https://github.com/sharmisnair/CreditCardsVerifier/blob/master/src/main/java/CreditCardsVerifierUMLDiagram.png)
