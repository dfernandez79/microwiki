package microwiki.cli

import groovy.transform.Immutable
import microwiki.Server

@Immutable
class ServerConfig {
    int port
    String encoding
    boolean readOnly

    static ServerConfig getDefault() {
        new ServerConfig(Server.DEFAULT_PORT, Server.DEFAULT_ENCODING, false)
    }
}
