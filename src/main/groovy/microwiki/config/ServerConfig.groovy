package microwiki.config

import groovy.transform.Immutable
import microwiki.Server

@Immutable
class ServerConfig {
    int port
    String encoding
    boolean readOnly
    boolean aliases

    static ServerConfig getDefault() {
        new ServerConfig(Server.DEFAULT_PORT, Server.DEFAULT_ENCODING, false, false)
    }
}
