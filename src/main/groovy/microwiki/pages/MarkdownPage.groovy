package microwiki.pages

import org.pegdown.PegDownProcessor

class MarkdownPage implements Page {
    private final URL url
    private final String charset

    public static PageFactory factoryUsing(final String charset) {
        {URL url  -> new MarkdownPage(url, charset) } as PageFactory
    }

    MarkdownPage(URL url, String charset) {
        this.url = url
        this.charset = charset
    }

    @Override
    Writable getSource() {
        return ({ Writer out -> out.write url.text }).asWritable()
    }

    @Override
    Writable getHtml() {
        return ({ Writer out -> out.write htmlFromMarkdown() }).asWritable()
    }

    private def htmlFromMarkdown() {
        new PegDownProcessor().markdownToHtml(getSource().toString())
    }

    @Override
    URL getUrl() {
        return url
    }
}
