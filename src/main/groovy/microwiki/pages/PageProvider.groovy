package microwiki.pages

import microwiki.search.SearchResults

interface PageProvider {
    Page pageFor(URI uri)

    Page pageFor(String relativePath)

    boolean isSearchSupported()

    SearchResults search(String text)
}
