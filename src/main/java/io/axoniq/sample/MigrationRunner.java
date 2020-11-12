package io.axoniq.sample;

import io.axoniq.axonserver.connector.AxonServerConnectionFactory;
import io.axoniq.axonserver.connector.event.EventChannel;
import io.axoniq.axonserver.connector.event.EventStream;
import io.axoniq.axonserver.grpc.event.Event;
import io.axoniq.axonserver.grpc.event.EventWithToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class MigrationRunner implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(MigrationRunner.class);
    private final MigrationProperties migrationProperties;
    private final ApplicationContext context;

    public MigrationRunner(MigrationProperties migrationProperties,
                           ApplicationContext context) {
        this.migrationProperties = migrationProperties;
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        if (migrationProperties.source() == null || migrationProperties.source().serverAddresses().length == 0) {
            throw new RuntimeException("No source addresses configured");
        }
        if (migrationProperties.target() == null || migrationProperties.target().serverAddresses().length == 0) {
            throw new RuntimeException("No target addresses configured");
        }
        AxonServerConnectionFactory sourceConnectionFactory = axonServerConnection(migrationProperties.source());
        AxonServerConnectionFactory targetConnectionFactory = axonServerConnection(migrationProperties.target());

        try {
            EventChannel sourceEventChannel = sourceConnectionFactory.connect(migrationProperties.source().context())
                                                                     .eventChannel();
            EventChannel targetEventChannel = targetConnectionFactory.connect(migrationProperties.target().context())
                                                                     .eventChannel();

            long last = targetEventChannel.getLastToken().get();
            Event[] events = new Event[migrationProperties.writeBatchSize()];
            int idx = 0;
            logger.info("Starting to read events from source at {}", last);
            try (EventStream sourceStream = sourceEventChannel.openStream(last, migrationProperties.readBufferSize())) {
                EventWithToken event;
                do {
                    event = sourceStream.nextIfAvailable(10, TimeUnit.SECONDS);
                    if (event != null) {
                        events[idx] = event.getEvent();
                        idx++;
                        if (idx == events.length) {
                            targetEventChannel.appendEvents(events).get();
                            idx = 0;
                        }
                        last = event.getToken();
                        if (logger.isDebugEnabled() && last % 1000 == 0) {
                            logger.debug("Reading events from source, last token = {}", last);
                        }
                    }
                } while (event != null);
                if (idx > 0) {
                    targetEventChannel.appendEvents(Arrays.copyOfRange(events, 0, idx));
                }
            }

            logger.info("Stopped reading events from source, last token = {}", last);
        } finally {
            sourceConnectionFactory.shutdown();
            targetConnectionFactory.shutdown();
        }
        SpringApplication.exit(context);
    }

    private AxonServerConnectionFactory axonServerConnection(AxonServerConnectionProperties source) {
        AxonServerConnectionFactory.Builder sourceConnectionFactoryBuilder =
                AxonServerConnectionFactory.forClient("migrationApplication", UUID.randomUUID().toString())
                                           .routingServers(source.serverAddresses())
                                           .token(source.token());
        if (source.tlsEnabled()) {
            sourceConnectionFactoryBuilder.useTransportSecurity();
        }
        return sourceConnectionFactoryBuilder.build();
    }
}