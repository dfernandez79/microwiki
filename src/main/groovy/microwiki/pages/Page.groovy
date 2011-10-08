package microwiki.pages

public interface Page {
    URI getUri()

    Writable getSource()

    Writable getHtml()

    String getEncoding()
}