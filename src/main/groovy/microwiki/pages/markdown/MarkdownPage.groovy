package microwiki.pages.markdown

import microwiki.pages.Page
import microwiki.pages.PageSourceNotFoundException
import org.pegdown.PegDownProcessor

class MarkdownPage implements Page {
    private final def sourceData
    private final URI uri
    private final String encoding

    MarkdownPage(URI uri, sourceData, String encoding) {
        this.uri = uri
        this.sourceData = sourceData
        this.encoding = encoding
    }

    @Override
    Writable getSource() {
        return deferredWriteOf { sourceData.getText(encoding) }
    }

    @Override
    Writable getHtml() {
        return deferredWriteOf { htmlFromMarkdown() }
    }

    private Writable deferredWriteOf(Closure cl) {
        ({ Writer out ->
            try {
                out.write cl.call()
            } catch (FileNotFoundException e) {
                throw new PageSourceNotFoundException(e)
            }
        }).asWritable()
    }

    private def htmlFromMarkdown() {
        new PegDownProcessor().markdownToHtml(getSource().toString())
    }

    @Override
    URI getUri() {
        return uri
    }

    @Override
    String getEncoding() {
        return encoding
    }
}
