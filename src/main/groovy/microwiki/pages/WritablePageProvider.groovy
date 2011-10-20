package microwiki.pages

interface WritablePageProvider extends PageProvider {
    Page newPageSampleFor(URI uri)

    def <T> T writePage(URI uri, Closure<T> closure)
}