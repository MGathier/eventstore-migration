package io.axoniq.sample;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * @author Marc Gathier
 */
@ConfigurationProperties(prefix = "migration")
@Component
public class MigrationProperties {

    @NestedConfigurationProperty
    private AxonServerConnectionProperties source;
    @NestedConfigurationProperty
    private AxonServerConnectionProperties target;
    private int readBufferSize = 1000;
    private int writeBatchSize = 50;

    public AxonServerConnectionProperties source() {
        return source;
    }

    public void setSource(AxonServerConnectionProperties source) {
        this.source = source;
    }

    public AxonServerConnectionProperties target() {
        return target;
    }

    public void setTarget(AxonServerConnectionProperties target) {
        this.target = target;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    public void setWriteBatchSize(int writeBatchSize) {
        this.writeBatchSize = writeBatchSize;
    }

    public int readBufferSize() {
        return readBufferSize;
    }

    public int writeBatchSize() {
        return writeBatchSize;
    }
}
