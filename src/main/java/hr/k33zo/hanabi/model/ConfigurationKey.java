package hr.k33zo.hanabi.model;

public enum ConfigurationKey {

    HOST("host"),
    SERVER_PORT("server.port"),
    CLIENT_PORT("client.port"),
    RANDOM_PORT_HINT("random.port.hint"),
    RMI_PORT("rmi.port");

    private String keyName;

    private ConfigurationKey(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }
}
