@description('Globally unique Service Bus namespace name')
param namespaceName string
param location string = resourceGroup().location

resource sb 'Microsoft.ServiceBus/namespaces@2022-10-01-preview' = {
  name: namespaceName
  location: location
  sku: { name: 'Standard', tier: 'Standard' }
  properties: { minimumTlsVersion: '1.2', publicNetworkAccess: 'Enabled' }
}
resource queue 'Microsoft.ServiceBus/namespaces/queues@2022-10-01-preview' = {
  parent: sb
  name: 'orders'
  properties: {
    lockDuration: 'PT1M'
    maxDeliveryCount: 5
    deadLetteringOnMessageExpiration: true
    defaultMessageTimeToLive: 'P1D'
    requiresDuplicateDetection: false
    requiresSession: false
    enablePartitioning: false
  }
}
output serviceBusNamespace string = sb.name
output queueName string = queue.name
