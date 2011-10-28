package microwiki.config.dsl

import microwiki.Server
import microwiki.config.Config
import microwiki.config.SearchConfig
import microwiki.config.ServerConfig
import microwiki.pages.PageTemplate
import microwiki.pages.TemplateAdapter
import microwiki.pages.Templates
import org.codehaus.groovy.control.CompilerConfiguration

class ConfigBuilder {
    static final String CONFIG_SCRIPT_EXAMPLE = """// This is a comment
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

    final ServerConfigBuilder server = new ServerConfigBuilder()
    final SearchConfigBuilder search = new SearchConfigBuilder()
    final TemplatesBuilder templates = new TemplatesBuilder()

    Config build() {
        new Config(server.build(), search.build(), templates.build())
    }

    void disablePageEditing() {
        server.readOnly = true
    }

    PageTemplate inlineTemplate(String source) {
        TemplateAdapter.using(source)
    }

    ConfigBuilder applyToScriptOn(Reader reader) throws ConfigScriptException {
        def binding = new Binding(builder: this)

        try {
            new GroovyShell(this.class.classLoader,
                    binding,
                    useConfigScriptBaseClass()).evaluate(reader)
        } catch (e) {
            throw new ConfigScriptException(e)
        }

        this
    }

    private CompilerConfiguration useConfigScriptBaseClass() {
        def configuration = new CompilerConfiguration()
        configuration.scriptBaseClass = ConfigScript.canonicalName
        configuration
    }

    static class ServerConfigBuilder {
        int port = Server.DEFAULT_PORT
        String encoding = Server.DEFAULT_ENCODING
        boolean readOnly = false

        ServerConfig build() {
            new ServerConfig(port, encoding, readOnly)
        }
    }

    static class SearchConfigBuilder {
        boolean enabled = true

        SearchConfig build() {
            new SearchConfig(enabled)
        }
    }

    static class TemplatesBuilder {
        def display = Templates.DEFAULT_DISPLAY_TEMPLATE
        def edit = Templates.DEFAULT_EDIT_TEMPLATE
        def create = Templates.DEFAULT_CREATE_TEMPLATE
        def read = Templates.DEFAULT_READ_TEMPLATE
        def search = Templates.DEFAULT_SEARCH_TEMPLATE

        Templates build() {
            new Templates(
                    display: template(display),
                    edit: template(edit),
                    create: template(create),
                    read: template(read),
                    search: template(search))
        }

        private PageTemplate template(source) {
            if (source instanceof PageTemplate) {
                source
            } else if (source instanceof String) {
                TemplateAdapter.using(new File(source))
            } else {
                TemplateAdapter.using(source)
            }
        }
    }
}
