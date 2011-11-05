package microwiki.search

import microwiki.pages.Page

enum NullPageSearchStrategy implements PageSearchStrategy {
    INSTANCE

    @Override
    SearchResults search(String query, SearchResultsDisplayOptions options) {
        new SearchResults(query, options, 0, Collections.<SearchResult> emptyList())
    }

    @Override
    boolean isSearchSupported() {
        false
    }

    @Override
    void removalOfPageIdentifiedBy(URI uri) {
    }

    @Override
    void creationOf(Page page) {
    }

    @Override
    void updateOf(Page page) {
    }
}
