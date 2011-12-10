package microwiki.search

import groovy.transform.Immutable

@Immutable final class SearchResults {
    String textToSearch
    SearchResultsDisplayOptions options
    int total
    List<SearchResult> retrievedResults
}
