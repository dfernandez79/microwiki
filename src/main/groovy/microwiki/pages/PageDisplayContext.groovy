package microwiki.pages

class PageDisplayContext {
    Page page
    boolean searchSupported

    PageDisplayContext(Page page, boolean searchSupported) {
        this.page = page
        this.searchSupported = searchSupported
    }

    Map asMap() {
        [page: page, searchSupported: searchSupported]
    }
}
