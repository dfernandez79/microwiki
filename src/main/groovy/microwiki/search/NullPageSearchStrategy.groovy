package microwiki.search

enum NullPageSearchStrategy implements PageSearchStrategy {
    INSTANCE

    @Override
    SearchResults search(String query, SearchResultsDisplayOptions options) {
        new SearchResults(options, 0, Collections.<SearchResult> emptyList())
    }

    @Override
    boolean isSearchSupported() {
        false
    }
}
