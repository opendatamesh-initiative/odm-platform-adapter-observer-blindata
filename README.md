# Open Data Mesh Observer Blindata

<div align="center">

![Blindata Observer](https://img.shields.io/badge/Open%20Data%20Mesh-Observer%20Blindata-blue?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTEyIDJMMiA3TDEyIDEyTDIyIDdMMTIgMloiIHN0cm9rZT0iY3VycmVudENvbG9yIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIvPgo8cGF0aCBkPSJNMjIgMTdMMTIgMjJMMiAxN0wxMiAxMkwyMiAxN1oiIHN0cm9rZT0iY3VycmVudENvbG9yIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIvPgo8cGF0aCBkPSJNMjIgMTJMMTIgMTdMMiAxMkwxMiA3TDIyIDEyWiIgc3Ryb2tlPSJjdXJyZW50Q29sb3IiIHN0cm9rZS13aWR0aD0iMiIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBzdHJva2UtbGluZWpvaW49InJvdW5kIi8+Cjwvc3ZnPgo=)

**Observer adapter for [blindata.io](https://blindata.io/)**

[![Java](https://img.shields.io/badge/Java-11+-orange?style=flat-square&logo=java)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.8.6+-red?style=flat-square&logo=apache-maven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-green?style=flat-square)](LICENSE)
[![Documentation](https://img.shields.io/badge/Documentation-Complete-blue?style=flat-square&logo=read-the-docs)](docs/README.md)

</div>

---

## 🎯 What is the Blindata Observer?

The Blindata Observer is an intelligent adapter for the Open Data Mesh platform that integrates
with [blindata.io](https://blindata.io/).

Blindata is a SaaS platform that leverages Data Governance and Compliance to empower your Data Management projects. The
purpose of this adapter is to keep the data catalog within Blindata constantly updated. Upon the occurrence of a
creation, deletion, or modification of a dataproduct, Blindata is immediately and automatically notified to ensure that
its catalog remains aligned.
---

## Quick Start

### Option 1: Using Maven (Local Development)

#### 1. Clone and Build

```bash
git clone https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata.git
cd odm-platform-adapter-observer-blindata
mvn clean install -DskipTests
```

#### 2. Configure

Create `application.yml` with your Blindata settings:

```yaml
blindata:
  url: https://app.blindata.io
  user: your-username
  password: your-password
  tenantUUID: your-tenant-uuid
```

For detailed configuration options, see [Configuration Guide](docs/configuration.md).

#### 3. Run

```bash
mvn spring-boot:run
```

### Option 2: Using Docker Image

#### 1. Pull and Run

```bash
# Pull the image from Docker Hub
docker pull opendatamesh/observer-blindata:latest

# Run with environment variables
docker run -d \
  -p 8080:8080 \
  -e BLINDATA_URL=https://app.blindata.io \
  -e BLINDATA_USER=your-username \
  -e BLINDATA_PASSWORD=your-password \
  -e BLINDATA_TENANT_UUID=your-tenant-uuid \
  --name blindata-observer \
  opendatamesh/observer-blindata:latest
```

#### 2. Or Use Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  blindata-observer:
    image: opendatamesh/observer-blindata:latest
    ports:
      - "8080:8080"
    environment:
      - BLINDATA_URL=https://app.blindata.io
      - BLINDATA_USER=your-username
      - BLINDATA_PASSWORD=your-password
      - BLINDATA_TENANT_UUID=your-tenant-uuid
    restart: unless-stopped
```

Then run:

```bash
docker-compose up -d
```

**Note**: If you want to override the default configuration (in addition to the basic ones), you can pass the env
variable `SPRING_APPLICATION_JSON` with the complete json containing all the
configurations.


---

## 📚 Documentation Hub

|        **Category**         | **Description**                                           |            **Links**            |
|:---------------------------:|:----------------------------------------------------------|:-------------------------------:|
|    ⚙️ **Configurations**    | Setup guides, environment variables, and advanced options | [ Guide](docs/configuration.md) |
|  🗺️ **Metadata Mapping**   | Metadata mapping between ODM and Blindata platforms       |    [ Guide](docs/mapping.md)    |
| ✅ **Descriptor Validation** | Data Product Descriptor validation rules                  |  [ Guide](docs/validation.md)   |

### 🎯 Quick Navigation

<details>

- [Authentication Setup](docs/configuration/authentication.md) - Configure secure access to Blindata
- [Blindata Configurations](docs/configuration/blindata-configurations.md) - Platform-specific settings
- [Event Handling](docs/configuration/event-handling.md) - Configure event processing
- [ODM Platform Configurations](docs/configuration/odm-platform-configurations.md) - Open Data Mesh settings
- [Validator Configuration](docs/configuration/validator.md) - Data validation setup

</details>

---

## 🤝 Contributing

We welcome contributions!

### Development Setup

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

---

## 🆘 Support

- 📖 **[Documentation](docs/README.md)** - Complete guides and examples
- 🐛 **[Issues](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/issues)** - Report bugs
  and request features
- 💬 **[Discussions](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/discussions)** -
  Ask questions and share ideas
- 📧 **[Blindata Support](https://help.blindata.io/)** - Official Blindata documentation

