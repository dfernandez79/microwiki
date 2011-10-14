package microwiki

import microwiki.pages.PageTemplate
import microwiki.pages.TemplateAdapter

class Templates {
    static PageTemplate DEFAULT_DISPLAY_TEMPLATE = template('display.html')
    static PageTemplate DEFAULT_EDIT_TEMPLATE = template('edit.html')
    static PageTemplate DEFAULT_CREATE_TEMPLATE = template('edit.html')
    static PageTemplate DEFAULT_READ_TEMPLATE = template('read.html')

    public final PageTemplate display
    public final PageTemplate edit
    public final PageTemplate create
    public final PageTemplate read

    private static template(String resource) {
        TemplateAdapter.using(this.getResource("templates/$resource"))
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
