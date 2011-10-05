package microwiki.page.markdown

import microwiki.page.Page
import org.pegdown.PegDownProcessor

class MarkdownPage implements Page {
    private final String source

    MarkdownPage(String source) {
        this.source = source
    }

    @Override
    Writable getSource() {
        return WritableAdapter.adapt(source)
    }

    @Override
    Writable getHtml() {
        return WritableAdapter.adapt(new PegDownProcessor().markdownToHtml(source))
    }
}
