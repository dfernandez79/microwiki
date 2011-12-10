package microwiki.search

import groovy.transform.Immutable

@Immutable final class SearchResultsDisplayOptions {
    static final int DEFAULT_PAGE_SIZE = 10

    int maxNumberOfResultsToRetrieve

    static SearchResultsDisplayOptions getDefault() {
        new SearchResultsDisplayOptions(DEFAULT_PAGE_SIZE)
    }
}
