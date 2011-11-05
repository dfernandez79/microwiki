package microwiki.search

import microwiki.pages.Page

interface PageSearchStrategy {
    boolean isSearchSupported()

    SearchResults search(String query, SearchResultsDisplayOptions options)

    void creationOf(Page page)

    void updateOf(Page page)

    void removalOfPageIdentifiedBy(URI uri)
}
