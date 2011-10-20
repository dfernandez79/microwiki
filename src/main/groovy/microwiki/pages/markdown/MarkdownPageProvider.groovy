package microwiki.pages.markdown

import microwiki.pages.Page
import microwiki.pages.WritablePageProvider
import microwiki.search.NullPageSearchStrategy
import microwiki.search.PageSearchStrategy
import microwiki.search.SearchResults
import microwiki.search.SearchResultsDisplayOptions

class MarkdownPageProvider implements WritablePageProvider {
    private final String encoding
    private final URI docRoot
    private final PageSearchStrategy pageSearchStrategy

    MarkdownPageProvider(File docRoot, String encoding) {
        this(docRoot, encoding, NullPageSearchStrategy.INSTANCE)
    }

    MarkdownPageProvider(File docRoot, String encoding, PageSearchStrategy searchStrategy) {
        this.docRoot = docRoot.toURI()
        this.encoding = encoding
        this.pageSearchStrategy = searchStrategy
    }

    @Override
    Page pageFor(URI uri) {
        if (uri.isAbsolute() && docRoot.relativize(uri).isAbsolute()) {
            throw new IllegalArgumentException("Only URIs relative $docRoot are allowed")
        }
        new MarkdownPage(docRoot.relativize(uri), docRoot.resolve(uri).toURL(), encoding)
    }

    @Override
    Page newPageSampleFor(URI uri) {
        new MarkdownPage(docRoot.relativize(uri), getClass().getResource('/microwiki/templates/newpage.md'), 'UTF-8')
    }

    @Override
    <T> T writePage(URI uri, Closure<T> closure) {
        new File(docRoot.resolve(uri)).withWriter closure
    }

    @Override
    Page pageFor(String relativePath) {
        pageFor(new URI(relativePath))
    }

    @Override
    boolean isSearchSupported() {
        pageSearchStrategy.searchSupported
    }

    @Override
    SearchResults search(String text) {
        search(text, SearchResultsDisplayOptions.defaultOptions())
    }

    @Override
    SearchResults search(String text, SearchResultsDisplayOptions options) {
        pageSearchStrategy.search(text, options)
    }
}
