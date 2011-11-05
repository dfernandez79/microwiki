package microwiki.search

import groovy.transform.Immutable

@Immutable final class SearchResults {
    String searchQuery
    SearchResultsDisplayOptions options
    int total
    List<SearchResult> retrievedResults
}
