Feature: BALANCE-HighLevel

  Scenario: Success
    Given a lobby BALANCE request is WELL-FORMED and VALID
    And the specified PLAYER exists

    When the request is processed
    """
    {
      "sid":"6168fd11-7e67-4883-a828-fc33949d74bf",
      "userId":"livescore_uk/123456789",
      "game": null,
      "currency":"EUR",
      "uuid":"ce186440-ed92-11e3-ac10-0800200c9a66"
    }
    """

    Then a balance response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "status":"OK",
      "balance":999.35,
      "bonus":0.00,
      "uuid":"ce186440-ed92-11e3-ac10-0800200c9a66"
    }
    """

  # ---------------------------------------------------------------------------------------
  Scenario: Success in-game call
    Given an in-game BALANCE request is WELL-FORMED and VALID
    And the specified PLAYER exists
    """
    {
      "sid": "6168fd11-7e67-4883-a828-fc33949d74bf",
      "userId": "livescore_uk/123456789",
      "game": {
          "id": null,
          "type": "blackjack",
          "details": {
              "table": {
                  "id": "aaabbbcccdddeee111",
                  "vid": "aaabbbcccdddeee111"
              }
          }
      },
      "currency": "EUR",
      "uuid": "ce186440-ed92-11e3-ac10-0800200c9a66"
    }
    """

    Then a balance response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "status":"OK",
      "balance":999.35,
      "bonus":0.00,
      "uuid":"ce186440-ed92-11e3-ac10-0800200c9a66"
    }
    """