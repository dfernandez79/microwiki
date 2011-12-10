package microwiki.search

import groovy.transform.Immutable

@Immutable final class SearchResult {
    String title
    URI uri
    List<String> highlights
}