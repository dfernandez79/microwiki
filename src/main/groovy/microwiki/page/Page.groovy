package microwiki.page

public interface Page {
    Writable getSource()

    Writable getHtml()
}