package microwiki.pages.markdown

import microwiki.pages.PageProvider
import microwiki.pages.Page

class MarkdownPageProvider implements PageProvider {
    private final String encoding
    private final URI docRoot

    MarkdownPageProvider(File docRoot, String encoding) {
        this.docRoot = docRoot.toURI()
        this.encoding = encoding
    }

    @Override
    Page pageFor(URI uri) {
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
}
