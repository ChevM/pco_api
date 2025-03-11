# PCO Lyrics Viewer

A Spring Boot application that integrates with the Planning Center Online (PCO) Services API to display song lyrics for worship services.

## Features

- Browse service types from your PCO account
- View plans for each service type
- Display songs with lyrics for each plan
- Clean, responsive UI for easy viewing on any device

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Planning Center Online account with API access

## Configuration

Before running the application, you need to configure your PCO API credentials:

1. Copy the template configuration files:
   ```bash
   cp src/main/resources/application.properties.template src/main/resources/application.properties
   cp src/test/resources/application-test.properties.template src/test/resources/application-test.properties
   ```

2. Edit the properties files and add your PCO API credentials:
   ```properties
   pco.api.app-id=your-app-id
   pco.api.secret=your-secret
   ```

You can obtain these credentials by creating an application in the PCO Developer portal: https://api.planningcenteronline.com/oauth/applications

## Running the Application

### Using Gradle

```bash
./gradlew bootRun
```

### Using Java

```bash
./gradlew build
java -jar build/libs/pco-lyrics-viewer-0.0.1-SNAPSHOT.jar
```

The application will be available at: http://localhost:8080/pco-lyrics-viewer/

## API Endpoints

The application exposes the following REST endpoints:

- `GET /api/pco/service-types` - Get all service types
- `GET /api/pco/service-types/{serviceTypeId}/plans` - Get plans for a specific service type
- `GET /api/pco/plans/{planId}/songs` - Get songs with lyrics for a specific plan

## Technologies Used

- Spring Boot
- Spring WebFlux (WebClient)
- Thymeleaf
- Bootstrap 5
- Resilience4j
- Lombok

## License

This project is licensed under the MIT License - see the LICENSE file for details. 