package microwiki.search

import groovy.transform.Immutable

@Immutable final class SearchResults {
    SearchResultsDisplayOptions options
    int total
    List<SearchResult> retrievedResults
}
