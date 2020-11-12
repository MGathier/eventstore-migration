# Event Store Migration

Migrates an Axon Server Event Store from one location to another. This may be a different Axon Server istance, 
or it may be another context within the same Axon Server installation. 

## Usage

Uses an application.properties file to configure the source and target Axon Server information.

Required properties:

- migration.source.servers - comma separated list of source nodes (use hostname:port if port is other than 8124) 
- migration.target.servers - comma separated list of target nodes (use hostname:port if port is other than 8124)

Optional properties: 

- migration.source.context - the context from which to read the events (uses "default" if not set) 
- migration.source.token - access token for the source event store 
- migration.source.tls-enabled - use transport level security for the source event store
- migration.target.context - the context to which to write the events  (uses "default" if not set)
- migration.target.token - access token for the target event store 
- migration.target.tls-enabled - use transport level security for the target event store
- migration.read-buffer-size - the buffer size for reading events from the source (default value 1000)
- migration.write-batch-size - the number of events to send to the target in one request (default value 50)
 
 
