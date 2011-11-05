package microwiki.pages.markdown

import groovy.io.FileType
import java.util.regex.Pattern
import microwiki.pages.Page
import microwiki.pages.PageChangeListener
import microwiki.pages.WritablePageProvider
import microwiki.search.*

class MarkdownPageProvider implements WritablePageProvider, PageChangeListener {
    private static final Pattern FILE_PATTERN = ~/.*\.md/
    private final URI docRootURI
    private final PageSearchStrategy pageSearchStrategy

    final String encoding
    final File docRoot

    MarkdownPageProvider(File docRoot, String encoding) {
        this(docRoot, encoding, { NullPageSearchStrategy.INSTANCE } as PageSearchStrategyFactory)
    }

    MarkdownPageProvider(File docRoot, String encoding, PageSearchStrategyFactory searchStrategyFactory) {
        this.docRoot = docRoot
        this.docRootURI = docRoot.toURI()
        this.encoding = encoding
        this.pageSearchStrategy = searchStrategyFactory.createFor(this)
    }

    @Override
    <T> T writePage(URI uri, Closure<T> closure) {
        new File(docRootURI.resolve(uri)).withWriter closure
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
    SearchResults search(String query) {
        search(query, SearchResultsDisplayOptions.defaultOptions())
    }

    @Override
    SearchResults search(String query, SearchResultsDisplayOptions options) {
        pageSearchStrategy.search(query, options)
    }

    @Override
    Page pageFor(URI uri) {
        assertRelativeToDocroot(uri)
        createPage(relativize(uri))
    }

    @Override
    void eachPage(Closure closure) {
        docRoot.eachFileMatch(FileType.FILES, FILE_PATTERN) { File file ->
            closure.call pageFor(relativize(file.toURI()))
        }
    }


    private void assertRelativeToDocroot(URI uri) {
        if (uri.isAbsolute() && docRootURI.relativize(uri).isAbsolute()) {
            throw new IllegalArgumentException("Only URIs relative to $docRootURI are allowed")
        }
    }

    private URI relativize(URI uri) {
        return docRootURI.relativize(uri)
    }


    @Override
    void creationOfPageIdentifiedBy(URI uri) {
        pageSearchStrategy.creationOf(pageFor(uri))
    }

    @Override
    void updateOfPageIdentifiedBy(URI uri) {
        pageSearchStrategy.updateOf(pageFor(uri))
    }

    @Override
    void removalOfPageIdentifiedBy(URI uri) {
        pageSearchStrategy.removalOfPageIdentifiedBy(uri)
    }

    private Page createPage(URI relativeURI) {
        new MarkdownPage(relativeURI, docRootURI.resolve(relativeURI).toURL(), encoding)
    }

    @Override
    Page newPageSampleFor(URI uri) {
        new MarkdownPage(relativize(uri), getClass().getResource('/microwiki/templates/newpage.md'), 'UTF-8')
    }
}
