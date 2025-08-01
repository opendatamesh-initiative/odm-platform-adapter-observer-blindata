# Configuration Guide

This guide covers all configuration parameters for the Blindata Observer, including authentication, event handling, and
validation settings.

## Table of Contents

- [Authentication](configuration/authentication.md) - Configure authentication methods for Blindata Platform
- [Event Handling](configuration/event-handling.md) - Configure event subscriptions and processing actions
- [Validator](configuration/validator.md) - Configure data product validation policies
- [Blindata Configurations](configuration/blindata-configurations.md) - Configure metadata extraction, data product management, and issue management settings
- [ODM Platform Configurations](configuration/odm-platform-configurations.md) - Configure connections to Open Data Mesh microservices (Policy, Registry, Notification)

## Overview

The Blindata Observer configuration is organized into several key areas, each handling a specific aspect of the
integration between Open Data Mesh and Blindata platforms. Click on any section in the table of contents above to view
detailed configuration options and examples.

### Configuration Sections Explained

- **Authentication**: Defines how the observer connects to the Blindata Platform, supporting API key and OAuth2
  authentication methods for secure communication.

- **Event Handling**: Configures how the observer responds to Open Data Mesh platform events, including event type
  subscriptions, content filtering, and the actions to perform for each event.

- **Validator**: Sets up automatic data product validation policies that ensure compliance and quality standards before
  data products are created in the platform.

- **Blindata Configurations**: Defines how the observer extracts metadata from data product descriptors, manages data products and their assets, and handles issue management policies. Includes settings for system name extraction, asset cleanup, and asynchronous processing.

- **ODM Platform Configurations**: Configures the connections to Open Data Mesh microservices including the Policy Service (for governance validation), Registry Service (for data product metadata), and Notification Service (for event subscriptions and processing).

## Passing configuration to your application

When using Docker, you can configure the observer using environment variables:

```bash
# Basic configuration
BLINDATA_URL=https://app.blindata.io
BLINDATA_USER=your-username
BLINDATA_PASSWORD=your-password
BLINDATA_TENANT_UUID=your-tenant-uuid

# ODM services
ODM_POLICY_SERVICE_ADDRESS=http://localhost:8005
ODM_REGISTRY_SERVICE_ADDRESS=http://localhost:8001
ODM_NOTIFICATION_SERVICE_ADDRESS=http://localhost:8006
```

**Note**: If you want to override the default configuration (in addition to the basic ones), you can pass the env
variable `SPRING_APPLICATION_JSON` with the complete json containing all the
configurations.