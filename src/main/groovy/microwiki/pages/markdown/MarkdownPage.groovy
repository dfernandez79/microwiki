package microwiki.pages.markdown

import microwiki.pages.Page
import microwiki.pages.PageSourceNotFoundException
import org.pegdown.PegDownProcessor
import org.pegdown.ToHtmlSerializer
import org.pegdown.ast.HeaderNode
import org.pegdown.ast.Node
import org.pegdown.ast.RootNode
import org.pegdown.ast.TextNode

class MarkdownPage implements Page {
    final URI uri
    final String encoding
    final Writable source
    final Writable html

    @Lazy(soft = true) RootNode document = { new PegDownProcessor().parseMarkdown(source.toString().toCharArray()) }()
    @Lazy String title = {
        Node firstHeaderNode = document.children.find { it instanceof HeaderNode }
        TextNode headerText = (TextNode) firstHeaderNode?.children?.find { it instanceof TextNode }
        headerText?.text ?: uri.path
    }()

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
        new ToHtmlSerializer().toHtml(document);
    }
}
