Feature: Send Payment
  As a user
  I want to send payments to recipients
  So that I can transfer money securely

  Scenario: Successfully send a payment with valid details
    Given a user wants to send a payment
    When they enter recipient email "recipient@example.com"
    And they enter amount "100" in currency "USD"
    And they submit the payment
    Then the payment should be processed successfully
    And the transaction should be saved with status "COMPLETED"

  Scenario: Fail to send payment with invalid email
    Given a user wants to send a payment
    When they enter recipient email "invalid-email"
    And they enter amount "100" in currency "USD"
    And they submit the payment
    Then the payment should fail with error "Invalid email format"

  Scenario: Fail to send payment with zero amount
    Given a user wants to send a payment
    When they enter recipient email "test@example.com"
    And they enter amount "0" in currency "USD"
    And they submit the payment
    Then the payment should fail with error "Amount must be greater than zero"

  Scenario: Fail to send payment with negative amount
    Given a user wants to send a payment
    When they enter recipient email "test@example.com"
    And they enter amount "-100" in currency "USD"
    And they submit the payment
    Then the payment should fail with error "Amount must be greater than zero"

  Scenario: Successfully send payment with different currencies
    Given a user wants to send a payment
    When they enter recipient email "euro@example.com"
    And they enter amount "250" in currency "EUR"
    And they submit the payment
    Then the payment should be processed successfully
    And the transaction should have currency "EUR"

  Scenario: Handle API failure gracefully
    Given a user wants to send a payment
    And the remote API is unavailable
    When they enter recipient email "test@example.com"
    And they enter amount "100" in currency "USD"
    And they submit the payment
    Then the payment should fail with error containing "Network"
    And the transaction should be saved with status "FAILED"

  Scenario: Handle validation error from API
    Given a user wants to send a payment
    And the remote API will return validation errors
    When they enter recipient email "test@example.com"
    And they enter amount "100" in currency "USD"
    And they submit the payment
    Then the payment should fail with error containing "Validation failed"
    And the transaction should be saved with status "FAILED"
