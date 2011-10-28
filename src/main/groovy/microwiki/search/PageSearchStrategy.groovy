package microwiki.search

interface PageSearchStrategy {
    boolean isSearchSupported()

    SearchResults search(String query, SearchResultsDisplayOptions options)
}
