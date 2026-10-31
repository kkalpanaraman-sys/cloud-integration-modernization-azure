# Cloud Integration Modernisation POC | Azure Integration Services

Portfolio starter: Spring Boot Order API → Azure Logic Apps → Azure Service Bus → Java Azure Function → Destination API.

## Contents
- `order-api/`: Spring Boot REST endpoint with validation and correlation ID
- `destination-api/`: simple downstream REST endpoint
- `azure-function/`: Java Service Bus-triggered Function forwarding messages
- `logic-app/workflow-definition.json`: workflow starter; configure the Service Bus connector in the Azure portal
- `infra/main.bicep`: creates a Service Bus namespace and `orders` queue
- `docs/architecture.md`: architecture and design notes



## Run locally
Requires Java 21 and Maven 3.9+.

Terminal 1:
```bash
cd order-api
mvn spring-boot:run
```
Terminal 2:
```bash
cd destination-api
mvn spring-boot:run
```
Test the order API:
```bash
curl -i -X POST http://localhost:8080/orders -H "Content-Type: application/json" -H "X-Correlation-ID: demo-001" -d '{"orderId":"ORD-1001","customerId":"CUST-101","amount":249.99,"currency":"EUR"}'
```
The API returns HTTP 202 after validation. It does not publish to Service Bus by itself; the Azure Logic App is the intended orchestration component.

## Deploy basic Service Bus resources
Review Azure pricing and naming/region availability first.
```bash
az login
az group create --name rg-integration-poc --location northeurope
az deployment group create --resource-group rg-integration-poc --template-file infra/main.bicep --parameters namespaceName='<globally-unique-namespace-name>'
```
Delete resources when finished if not needed:
```bash
az group delete --name rg-integration-poc --yes --no-wait
```

## Configure Azure
1. Create a Logic App and import/adapt `logic-app/workflow-definition.json`.
2. Configure its HTTP trigger and Service Bus connector action in the Azure portal; send the prepared JSON to queue `orders`.
3. Deploy the Java Function and configure app settings: `ORDER_QUEUE_NAME=orders`, `DESTINATION_API_URL=<reachable destination API URL>`, and Service Bus connection/authentication.
4. For production-like practice, prefer managed identity/RBAC over long-lived connection strings where supported. Never commit credentials, local settings, or secrets.
5. Monitor Logic App run history, Function logs/Application Insights, Service Bus active/dead-letter message counts.

`azure-function/local.settings.example.json` is a template only. Copy it to `local.settings.json`, fill in local values, and do not commit the real settings file.

## CV wording (only after building/testing)
**Personal Portfolio POC — Cloud Integration Modernisation with Azure Integration Services**
- Built a Spring Boot REST API and designed an integration flow using Logic Apps, Service Bus, and an Azure Function.
- Demonstrated message validation, asynchronous processing, correlation IDs, and downstream REST delivery.
- Documented retries/dead-letter handling, secure configuration, and observability considerations.

