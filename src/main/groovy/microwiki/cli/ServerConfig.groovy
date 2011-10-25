package microwiki.cli

import groovy.transform.Immutable

@Immutable
class ServerConfig {
    int port
    String encoding
    boolean readOnly
}
