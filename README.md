# Aadhaar Parser API (Java Spring Boot)

REST API to decode Aadhaar QR from an image and parse the embedded XML securely.

## Features
- Upload image (.jpg, .jpeg, .png) and decode QR using ZXing
- Parse Aadhaar XML with secure XML settings (prevents XXE and entity expansion)
- Plain-text XML parsing endpoint
- Configurable multipart size limits
- Health probes via Spring Boot Actuator
- Containerized build with multi-stage Dockerfile

## Build
Requires Java 17 and Maven 3.9+
- Build: mvn -DskipTests package
- Output JAR: target/aadhar-parser-api-0.0.1-SNAPSHOT.jar

## Run
- Local: java -jar target/aadhar-parser-api-0.0.1-SNAPSHOT.jar
- Server: listens on port 8080
- Health: GET http://localhost:8080/actuator/health

## API
- POST /api/v1/aadhaar/parse-image
  - Content-Type: multipart/form-data
  - Part name: file
  - Response: { xml: string, data: { ...attributes } }

- POST /api/v1/aadhaar/parse-xml
  - Content-Type: text/plain
  - Body: XML string

## PowerShell examples
- Image upload:
  Invoke-RestMethod -Uri "http://localhost:8080/api/v1/aadhaar/parse-image" -Method Post -Form @{ file = Get-Item .\sample.jpg }

- XML parse:
  $xml = Get-Content .\aadhaar.xml -Raw
  Invoke-RestMethod -Uri "http://localhost:8080/api/v1/aadhaar/parse-xml" -Method Post -Body $xml -ContentType 'text/plain'

## Docker
- Build: docker build -t aadhar-parser-api:latest .
- Run: docker run --rm -p 8080:8080 aadhar-parser-api:latest

## Security Notes
- Secure XML: FEATURE_SECURE_PROCESSING enabled; external DTD/schema access disabled; entity expansion disabled
- Multipart limits: default 5MB (src/main/resources/application.yml)
- Consider reducing logs to avoid sensitive data exposure in production

## Project Layout
- Spring Boot API under src/
- Original proof-of-concept in aadhar-parser-in-java/ (not used by API)
