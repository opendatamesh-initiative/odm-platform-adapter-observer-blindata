Feature: Validator Error Codes Tagging

  As a user observing the validation process
  I want all validation warning messages to include a specific unique tag
  So that I can easily identify the error code and find its description in the documentation

  Scenario: Validator logs a standard warning message with a tag
    Given the validator is evaluating a data product
    When the validator encounters an issue (e.g., "Avro root schema must be a RECORD")
    Then the validator's logger should record a warning message
    And the warning message must start with a unique tag in the format "[#<id>]" (e.g., "[#7]")
    And the tag should correspond to an entry in the validator error documentation

  Scenario: Validator logs multiple distinct warnings
    Given the validator is evaluating a data product
    When the validator encounters multiple different issues
    Then the validator's logger should record a separate warning message for each issue
    And every recorded message must include its corresponding unique tag