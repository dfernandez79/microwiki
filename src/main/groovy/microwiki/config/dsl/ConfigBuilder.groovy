package microwiki.config.dsl

import microwiki.config.Config
import microwiki.config.SearchConfig
import microwiki.config.ServerConfig
import microwiki.servlets.view.TemplateAdapter
import microwiki.servlets.view.Templates
import microwiki.servlets.view.ViewTemplate
import org.codehaus.groovy.control.CompilerConfiguration

class ConfigBuilder {
    static final String CONFIG_SCRIPT_EXAMPLE = """// This is a comment
// config files are Groovy code, you can use any Java/Groovy class available
server {
    port = 9999  // by default if ${ServerConfig.DEFAULT_PORT}
    readOnly = false // by default is false
    encoding = 'UTF-8' // by default is ${ServerConfig.DEFAULT_ENCODING}
    aliases = false // by default is false
}
search {
    enabled = true // by default is true
    indexStorageMethod = filesystem('.microwiki-index') // other option is memory
}
templates {
    display = 'displ.html' // Strings represents file names in the current directory
    edit = new File('edit.html) // you can use files
    read = new StringReader('hello') // readers...
    create = new URL('http//mytemplate.com/template') // or urls

    // You can use debug(source) to indicate that the template must be realoded each time:
    // display = debug('dipl.html')
    // Templates can also contain specific parameters that can be passed using template(source, context)
    // display = template('displ.html', [myParam:'hello',other:true])
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

    ViewTemplate inlineTemplate(String source) {
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
        int port = ServerConfig.DEFAULT_PORT
        String encoding = ServerConfig.DEFAULT_ENCODING
        boolean readOnly = false
        boolean aliases = false

        ServerConfig build() {
            new ServerConfig(port, encoding, readOnly, aliases)
        }
    }

    static class SearchConfigBuilder {
        boolean enabled = true
        Map indexStorageMethod = SearchConfig.DEFAULT_FILESYSTEM_STORAGE

        final Map memory = SearchConfig.MEMORY_INDEX_STORAGE
        final Map filesystem = SearchConfig.DEFAULT_FILESYSTEM_STORAGE

        Map filesystem(String path) {
            filesystem(new File(path))
        }

        Map filesystem(File path) {
            [directory: path]
        }

        SearchConfig build() {
            new SearchConfig(enabled, indexStorageMethod)
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

        ViewTemplate debug(source, Map context = [:]) {
            new ViewTemplate() {
                @Override
                Writable applyWith(Map ctx) {
                    template(source, context).applyWith(ctx)
                }

                @Override
                String toString() {
                    "DEBUG MODE [$source]"
                }
            }
        }

        private static ViewTemplate template(source, Map context = [:]) {
            if (source instanceof ViewTemplate) {
                source
            } else if (source instanceof String) {
                TemplateAdapter.using(new File(source), context)
            } else {
                TemplateAdapter.using(source, context)
            }
        }
    }
}
