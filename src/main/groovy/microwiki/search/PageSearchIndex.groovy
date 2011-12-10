package microwiki.search

import microwiki.pages.PageChangeListener

interface PageSearchIndex extends PageChangeListener {
    SearchResults search(String textToSearch, SearchResultsDisplayOptions options)
}
