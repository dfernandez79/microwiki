package microwiki

import groovy.text.Template
import microwiki.page.Page
import microwiki.storage.PageStorage
import groovy.text.GStringTemplateEngine

class Microwiki {
    private final PageStorage storage
    private final Map<String, Template> templates
    private final String defaultPageName


    public static final Map<String, Template> DEFAULT_TEMPLATES = new HashMap<String, Template>()
    static {
        GStringTemplateEngine engine = new GStringTemplateEngine()
        DEFAULT_TEMPLATES.display = engine.createTemplate(Microwiki.getResource('templates/display.html'))
    }

    Microwiki(Map config) {
        this(config.storage,
                config.templates ?: DEFAULT_TEMPLATES,
                config.defaultPageName ?: 'index')
    }

    Microwiki(PageStorage storage, Map<String, Template> templates, String defaultPageName) {
        if (storage == null) { throw new IllegalArgumentException( 'The wiki storage cannot be null') }
        this.storage = storage
        this.templates = templates
        this.defaultPageName = defaultPageName
    }

    String getDefaultPageName() {
        return defaultPageName
    }

    Writable htmlToDisplay(String pageName) {
        if (pageName == null || pageName.isEmpty()) {
            pageName = defaultPageName
        }
        templates.display.make([
                pageName: pageName,
                page: storage.pageNamed(pageName)])
    }
}
