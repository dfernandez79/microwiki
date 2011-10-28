package microwiki.pages.markdown

import microwiki.pages.Page
import microwiki.pages.PageSourceNotFoundException
import org.pegdown.PegDownProcessor

class MarkdownPage implements Page {
    final URI uri
    final String encoding
    final Writable source
    final Writable html

    MarkdownPage(URI uri, sourceData, String encoding) {
        this.uri = uri
        this.encoding = encoding
        this.source = deferredWriteOf { sourceData.getText(encoding) }
        this.html = deferredWriteOf { htmlFromMarkdown() }
    }

    private Writable deferredWriteOf(Closure<String> cl) {
        ({ Writer out ->
            try {
                out.write cl.call()
            } catch (FileNotFoundException e) {
                throw new PageSourceNotFoundException(e)
            }
        }).asWritable()
    }

    private String htmlFromMarkdown() {
        new PegDownProcessor().markdownToHtml(source.toString())
    }
}
