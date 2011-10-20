package microwiki.pages

interface Page {
    URI getUri()

    Writable getSource()

    Writable getHtml()

    String getEncoding()
}