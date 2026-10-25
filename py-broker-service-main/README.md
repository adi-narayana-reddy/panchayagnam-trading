# py-broker-service
## How to Build & Run

# Build JAR
cd py-broker-service
mvn clean package -DskipTests

# Run
java -jar target/py-broker-service-0.0.1.jar

# Or with custom API key
java -jar target/py-broker-service-0.0.1.jar --smartapi.key=YOUR_KEY