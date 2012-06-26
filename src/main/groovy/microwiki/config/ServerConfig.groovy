package microwiki.config

import groovy.transform.Immutable
import microwiki.Server

@Immutable
class ServerConfig {
    static final int DEFAULT_PORT = 9999
    static final String DEFAULT_ENCODING = 'UTF-8'

    int port
    String encoding
    boolean readOnly
    boolean aliases

    static ServerConfig getDefault() {
        new ServerConfig(DEFAULT_PORT, DEFAULT_ENCODING, false, false)
    }
}
