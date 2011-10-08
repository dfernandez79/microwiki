package microwiki.pages

public interface Page {
    URL getUrl()

    Writable getSource()

    Writable getHtml()
}