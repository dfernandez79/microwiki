package microwiki.cli

import microwiki.Templates
import org.codehaus.groovy.control.CompilerConfiguration

class Config {
    final ServerConfig server
    final Templates templates

    static Config readFrom(Reader reader) {
        def binding = new Binding(builder: new ConfigBuilder())
        def shell = new GroovyShell(this.classLoader, binding, useConfigScriptBaseClass())

        shell.evaluate(reader)

        binding.builder.build()
    }

    Config(ServerConfig serverConfig, Templates templates) {
        this.server = serverConfig
        this.templates = templates
    }

    static CompilerConfiguration useConfigScriptBaseClass() {
        def configuration = new CompilerConfiguration()
        configuration.scriptBaseClass = ConfigScript.canonicalName
        return configuration
    }
}
