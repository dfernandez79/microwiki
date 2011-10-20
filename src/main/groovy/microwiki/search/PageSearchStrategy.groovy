package microwiki.search

interface PageSearchStrategy {
    boolean isSearchSupported()

    SearchResults search(String text, SearchResultsDisplayOptions options)
}
