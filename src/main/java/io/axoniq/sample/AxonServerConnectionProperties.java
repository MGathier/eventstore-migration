package io.axoniq.sample;

import io.axoniq.axonserver.connector.impl.ServerAddress;

/**
 * @author Marc Gathier
 */
public class AxonServerConnectionProperties {

    private String[] servers;
    private String token = "";
    private String context = "default";
    private boolean tlsEnabled;

    public void setServers(String[] servers) {
        this.servers = servers;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean tlsEnabled() {
        return tlsEnabled;
    }

    public void setTlsEnabled(boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    public ServerAddress[] serverAddresses() {
        ServerAddress[] addresses = new ServerAddress[servers.length];
        int idx = 0;
        for (String server : servers) {
            String[] hostnamePort = server.split(":", 2);
            if (hostnamePort.length == 1) {
                addresses[idx] = new ServerAddress(server);
            } else {
                addresses[idx] = new ServerAddress(hostnamePort[0], Integer.parseInt(hostnamePort[1]));
            }
            idx++;
        }
        return addresses;
    }

    public String token() {
        return token;
    }

    public String context() {
        return context;
    }
}
