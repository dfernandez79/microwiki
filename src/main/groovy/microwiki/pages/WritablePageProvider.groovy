package microwiki.pages

public interface WritablePageProvider extends PageProvider {
    Page newPageSampleFor(URI uri)

    public <T> T writePage(URI uri, Closure<T> closure)
}