package microwiki

import org.pegdown.ast.RootNode
import org.pegdown.PegDownProcessor

class WikiPage {
    public final String source
    public final URI uri

    WikiPage(URI uri, String source) {
        this.uri = uri
        this.source = source
    }
}
