package microwiki.servlets.view

class Templates {
    static final ViewTemplate DEFAULT_DISPLAY_TEMPLATE = template('display.html', [readonly: false])
    static final ViewTemplate DEFAULT_EDIT_TEMPLATE = template('edit.html')
    static final ViewTemplate DEFAULT_CREATE_TEMPLATE = DEFAULT_EDIT_TEMPLATE
    static final ViewTemplate DEFAULT_READ_TEMPLATE = template('display.html', [readonly: true])
    static final ViewTemplate DEFAULT_SEARCH_TEMPLATE = template('search.html')
    static final ViewTemplate DEFAULT_DIRECTORY_LISTING_TEMPLATE = template('directory-listing.html')

    public final ViewTemplate display
    public final ViewTemplate edit
    public final ViewTemplate create
    public final ViewTemplate read
    public final ViewTemplate search
    public final ViewTemplate directoryListing

    private static template(String resource, Map templateSpecificContext) {
        TemplateAdapter.using(this.getResource("/microwiki/templates/$resource"), templateSpecificContext)
    }

    private static template(String resource) {
        template(resource, Collections.emptyMap())
    }

    Templates() {
        this([:])
    }

    Templates(Map<String, ViewTemplate> config) {
        display = config.display ?: DEFAULT_DISPLAY_TEMPLATE
        edit = config.edit ?: DEFAULT_EDIT_TEMPLATE
        create = config.create ?: DEFAULT_CREATE_TEMPLATE
        read = config.read ?: DEFAULT_READ_TEMPLATE
        search = config.search ?: DEFAULT_SEARCH_TEMPLATE
        directoryListing = config.directoryListing ?: DEFAULT_DIRECTORY_LISTING_TEMPLATE
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
