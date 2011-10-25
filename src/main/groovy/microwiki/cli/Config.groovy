package microwiki.cli

import org.codehaus.groovy.control.CompilerConfiguration
import microwiki.pages.Templates
import microwiki.Server

class Config {
    static final String EXAMPLE = """// This is a comment
// config files are Groovy code, you can use any Java/Groovy class available
server {
    port = 9999  // by default if ${Server.DEFAULT_PORT}
    readOnly = false // by default is false
    encoding = 'UTF-8' // by default is ${Server.DEFAULT_ENCODING}
}
search {
    enabled = true // by default is true
}
templates {
    display = 'displ.html' // Strings represents file names in the current directory
    edit = new File('edit.html) // you can use files
    read = new StringReader('hello') // readers...
    create = new URL('http//mytemplate.com/template') // or urls
}
"""

    final ServerConfig server
    final SearchConfig search
    final Templates templates

    static Config readFrom(Reader reader) {
        def binding = new Binding(builder: new ConfigBuilder())

        new GroovyShell(this.classLoader,
                binding,
                useConfigScriptBaseClass()).evaluate(reader)

        binding.builder.build()
    }

    Config() {
        this(ServerConfig.default, SearchConfig.default, new Templates())
    }

    Config(ServerConfig serverConfig, SearchConfig searchConfig, Templates templates) {
        this.server = serverConfig
        this.search = searchConfig
        this.templates = templates
    }

    static CompilerConfiguration useConfigScriptBaseClass() {
        def configuration = new CompilerConfiguration()
        configuration.scriptBaseClass = ConfigScript.canonicalName
        return configuration
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
