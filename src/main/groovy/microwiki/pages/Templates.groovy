package microwiki.pages

class Templates {
    static final PageTemplate DEFAULT_DISPLAY_TEMPLATE = template('display.html', [readonly: false])
    static final PageTemplate DEFAULT_EDIT_TEMPLATE = template('edit.html')
    static final PageTemplate DEFAULT_CREATE_TEMPLATE = DEFAULT_EDIT_TEMPLATE
    static final PageTemplate DEFAULT_READ_TEMPLATE = template('display.html', [readonly: true])
    static final PageTemplate DEFAULT_SEARCH_TEMPLATE = template('search.html')

    public final PageTemplate display
    public final PageTemplate edit
    public final PageTemplate create
    public final PageTemplate read
    public final PageTemplate search

    private static template(String resource, Map templateSpecificContext) {
        TemplateAdapter.using(this.getResource("/microwiki/templates/$resource"), templateSpecificContext)
    }

    private static template(String resource) {
        template(resource, Collections.emptyMap())
    }

    Templates() {
        this([:])
    }

    Templates(Map<String, PageTemplate> config) {
        display = config.display ?: DEFAULT_DISPLAY_TEMPLATE
        edit = config.edit ?: DEFAULT_EDIT_TEMPLATE
        create = config.create ?: DEFAULT_CREATE_TEMPLATE
        read = config.read ?: DEFAULT_READ_TEMPLATE
        search = config.search ?: DEFAULT_SEARCH_TEMPLATE
    }

    boolean isAnyRedefined() {
        displayRedefined || editRedefined || createRedefined || readRedefined
    }

    boolean isReadRedefined() {
        read != DEFAULT_READ_TEMPLATE
    }

    boolean isCreateRedefined() {
        create != DEFAULT_CREATE_TEMPLATE
    }

    boolean isEditRedefined() {
        edit != DEFAULT_EDIT_TEMPLATE
    }

    boolean isDisplayRedefined() {
        display != DEFAULT_DISPLAY_TEMPLATE
    }

    @Override
    String toString() {
        StringWriter str = new StringWriter()
        def out = new PrintWriter(str)
        out.println 'Templates:'
        if (displayRedefined) { out.println "    Display: $display" }
        if (editRedefined) { out.println "    Edit: $edit" }
        if (createRedefined) { out.println "    Create: $create" }
        if (readRedefined) { out.println "    Read: $read" }
        str.toString()
    }
}
