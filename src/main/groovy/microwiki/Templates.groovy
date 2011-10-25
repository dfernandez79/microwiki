package microwiki

import microwiki.pages.PageTemplate
import microwiki.pages.TemplateAdapter

class Templates {
    static PageTemplate DEFAULT_DISPLAY_TEMPLATE = template('display.html', [readonly: true])
    static PageTemplate DEFAULT_EDIT_TEMPLATE = template('edit.html')
    static PageTemplate DEFAULT_CREATE_TEMPLATE = DEFAULT_EDIT_TEMPLATE
    static PageTemplate DEFAULT_READ_TEMPLATE = template('display.html', [readonly: true])

    public final PageTemplate display
    public final PageTemplate edit
    public final PageTemplate create
    public final PageTemplate read

    private static template(String resource, Map templateSpecificContext) {
        TemplateAdapter.using(this.getResource("templates/$resource"), templateSpecificContext)
    }
    private static template(String resource) {
        template(resource, Collections.emptyMap())
    }

    Templates() {
        this([:])
    }

    Templates(Map<String, PageTemplate> config) {
        this.display = config.display ?: DEFAULT_DISPLAY_TEMPLATE
        this.edit = config.edit ?: DEFAULT_EDIT_TEMPLATE
        this.create = config.create ?: DEFAULT_CREATE_TEMPLATE
        this.read = config.read ?: DEFAULT_READ_TEMPLATE
    }
}
