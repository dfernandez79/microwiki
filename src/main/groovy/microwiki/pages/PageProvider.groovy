package microwiki.pages

import microwiki.search.SearchResults
import microwiki.search.SearchResultsDisplayOptions

interface PageProvider {
    Page pageFor(URI uri)

    Page pageFor(String relativePath)

    boolean isSearchSupported()

    SearchResults search(String text)

    SearchResults search(String text, SearchResultsDisplayOptions options)
}
