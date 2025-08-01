# Validation Guide

The Blindata Observer includes a feature that automatically generates a Policy within the Policy Service, which is
evaluated before creating a DataProduct or DataProductVersion.

## Overview

The validation feature ensures that data products meet compliance requirements before they are created. It verifies that
the data product contains all necessary elements required by Blindata and can be uploaded without issues.

## Configuration

For configuration go [here](./configuration/validator.md)

## How It Works

### 1. Policy Generation

The validation system automatically generates a policy within the Policy Service during application startup. This process is handled by the `ValidatorPolicySubscriber` class which:

- **Creates Policy Engine**: If not already existing, creates a policy engine with the configured name
- **Creates Policy**: Generates a policy resource with the following characteristics:
  - **Name**: Configurable via `blindata.validator.policy.name`
  - **Blocking**: Can be configured as blocking or non-blocking via `blindata.validator.policy.blocking`
  - **Evaluation Events**: Triggers on both `DATA_PRODUCT_CREATION` and `DATA_PRODUCT_VERSION_CREATION` events
  - **Engine URL**: Points to the Blindata Observer's validation endpoint

The policy is automatically registered with the ODM Policy Service and will be evaluated whenever data products are created or updated.

### 2. Policy Evaluation

The validation process is triggered automatically when:

- **Before Data Product Creation**: When a new data product is being created in the ODM platform
- **Before Data Product Version Creation**: When a new version of an existing data product is being created

The evaluation process:

1. **Receives Policy Request**: The Blindata Observer receives a policy evaluation request from the ODM Policy Service
2. **Simulates Upload Process**: Runs a dry-run of the complete data product upload process including:
   - Data product creation/update
   - Port and asset extraction and validation
   - Quality checks extraction and validation
3. **Collects Warnings**: All validation warnings are collected during the simulation
4. **Returns Result**: The policy evaluation result indicates whether the data product passes validation

If any validation warnings are detected during the dry-run, the policy evaluation fails and prevents the data product creation (if configured as blocking).

## Validation Checks

The validation system performs comprehensive checks across multiple areas of the data product. All validation issues are logged as warnings and can cause the validation to fail. Here are the main validation categories:

### Data Product Information Validation

- **Missing Data Product Info**: Validates that the data product information object exists
- **Missing Fully Qualified Name**: Ensures the data product has a valid fully qualified name
- **Missing Domain**: Verifies that the data product has a domain specified
- **Missing Name**: Checks that the data product has a name (will attempt to extract from FQN if missing)
- **Missing Version**: Validates version information (defaults to "0.0.0" if missing)
- **Invalid Product Type**: Ensures product type is a textual value when specified in additional properties

### User and Responsibility Validation

- **User Not Found**: Validates that the data product owner exists in Blindata
- **Role Not Found**: Ensures the default stewardship role exists in Blindata
- **Missing Owner ID**: Checks that the owner identifier is properly defined

### Interface Components Validation

- **Missing Interface Components**: Validates that the data product has at least one type of port defined
- **Missing Port Information**: Ensures ports have required fields:
  - Port identifier
  - Port name
  - Port version
  - Port type

### Port Assets Validation

- **Missing Port Identifier**: Validates that port assets have a port identifier
- **Missing Physical Entities**: Ensures port assets have physical entities defined
- **Missing System Information**: Validates system information on port assets:
  - System object exists
  - System name is specified
- **Missing Physical Entity Names**: Ensures physical entities have names
- **Missing Physical Field Names**: Validates that physical fields have names

### Quality Checks Validation

- **Missing Quality Check Code**: Ensures quality checks have a code defined
- **Invalid Quality Object**: Validates quality object structure and properties
- **Reference Quality Checks**: Handles quality checks that are references to other definitions
- **Missing Issue Policy Information**: Validates issue policies have required fields:
  - Policy name
  - Policy type
- **Unsupported Policy Types**: Checks that policy types are supported
- **Quality Upload Errors**: Captures any errors during quality check upload process

### Schema and Standard Definition Validation

- **Malformed Port Schema**: Validates port schema definitions are properly formatted
- **Missing Schema Format**: Ensures AsyncAPI messages have proper schema format
- **Unsupported Schema Types**: Validates schema types are supported (e.g., Avro RECORD types)
- **Invalid Regex Patterns**: Validates additional properties regex patterns are valid
- **Missing Schema Information**: Ensures schemas have required information

### Semantic Linking Validation

- **Missing Namespace**: Validates default namespace identifier is provided
- **Namespace Not Found**: Ensures the specified namespace exists in Blindata
- **Missing Data Category**: Validates default data category name is provided
- **Data Category Not Found**: Ensures the specified data category exists
- **Semantic Link Resolution**: Validates semantic link paths can be resolved

### Additional Properties Validation

- **Invalid Regex for Additional Properties**: Validates regex patterns used for extracting additional properties
- **Duplicate Dependencies**: Checks for conflicting dependency specifications (x-dependsOn vs dependsOn)

### Marketplace and Access Request Validation

- **Missing Access Request Identifier**: Validates access request has an identifier
- **Missing Provider Information**: Ensures marketplace access requests have provider details
- **Missing Provider Ports**: Validates provider has ports defined

### Error Handling and Processing

- **Use Case Execution Errors**: Captures internal use case execution errors
- **Client Communication Errors**: Handles communication errors with Blindata
- **JSON Processing Errors**: Manages JSON serialization/deserialization errors
- **Pattern Syntax Errors**: Validates regex pattern syntax

All these validation checks work together to ensure that data products meet the required standards before being created or updated in the Blindata system. The validation can be configured as either blocking (preventing creation on failure) or non-blocking (allowing creation but logging issues).

