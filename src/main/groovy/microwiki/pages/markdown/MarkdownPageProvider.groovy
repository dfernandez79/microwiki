package microwiki.pages.markdown

import microwiki.pages.Page
import microwiki.pages.PageProvider

class MarkdownPageProvider implements PageProvider {
    private final String encoding
    private final URI docRoot

    MarkdownPageProvider(File docRoot, String encoding) {
        this.docRoot = docRoot.toURI()
        this.encoding = encoding
    }

    @Override
    Page pageFor(URI uri) {
        if (uri.isAbsolute() && docRoot.relativize(uri).isAbsolute()) {
            throw new IllegalArgumentException("Only URIs relative $docRoot are allowed")
        }
        return new MarkdownPage(docRoot.relativize(uri), docRoot.resolve(uri).toURL(), encoding)
    }

    @Override
    Page newPageSampleFor(URI uri) {
        return new MarkdownPage(docRoot.relativize(uri), getClass().getResource('/microwiki/templates/newpage.md'), 'UTF-8')
    }

    @Override
    public <T> T writePage(URI uri, Closure<T> closure) {
        new File(docRoot.resolve(uri)).withWriter closure
    }

    @Override
    Page pageFor(String relativePath) {
        return pageFor(new URI(relativePath))
    }
}
