package microwiki.cli

import microwiki.Server

import microwiki.pages.PageTemplate
import microwiki.pages.TemplateAdapter
import microwiki.pages.Templates

class ConfigBuilder {
    final ServerConfigBuilder server = new ServerConfigBuilder()
    final SearchConfigBuilder search = new SearchConfigBuilder()
    final TemplatesBuilder templates = new TemplatesBuilder()

    Config build() {
        new Config(server.build(), search.build(), templates.build())
    }

    public static class ServerConfigBuilder {
        int port = Server.DEFAULT_PORT
        boolean readOnly = false
        String encoding = Server.DEFAULT_ENCODING

        ServerConfig build() {
            new ServerConfig(port, encoding, readOnly)
        }
    }

    public static class SearchConfigBuilder {
        boolean enabled = true

        SearchConfig build() {
            new SearchConfig(enabled)
        }
    }

    public static class TemplatesBuilder {
        def display = Templates.DEFAULT_DISPLAY_TEMPLATE
        def edit = Templates.DEFAULT_EDIT_TEMPLATE
        def create = Templates.DEFAULT_CREATE_TEMPLATE
        def read = Templates.DEFAULT_READ_TEMPLATE

        Templates build() {
            new Templates(
                    display: template(display),
                    edit: template(edit),
                    create: template(create),
                    read: template(read))
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
