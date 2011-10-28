package microwiki.config

import microwiki.Server
import microwiki.config.dsl.ConfigBuilder
import microwiki.config.dsl.ConfigScriptException
import microwiki.pages.Templates

class Config {
    final ServerConfig server
    final SearchConfig search
    final Templates templates

    static Config readFrom(Reader reader) throws ConfigScriptException {
        new ConfigBuilder().applyToScriptOn(reader).build()
    }

    Config() {
        this(ServerConfig.default, SearchConfig.default, new Templates())
    }

    Config(ServerConfig serverConfig, SearchConfig searchConfig, Templates templates) {
        this.server = serverConfig
        this.search = searchConfig
        this.templates = templates
    }

    @Override
    String toString() {
        StringWriter str = new StringWriter()
        def out = new PrintWriter(str)
        out.println "Port: ${server.port}"
        out.println "Encoding: ${server.encoding}"
        if (server.readOnly) { out.println 'Read Only Mode' }
        if (search.disabled) { out.println 'Search is disabled' }
        if (templates.anyRedefined) { out.print templates }
        str.toString()
    }
}