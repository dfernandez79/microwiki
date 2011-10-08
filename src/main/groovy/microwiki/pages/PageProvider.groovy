package microwiki.pages

interface PageProvider {
    Page pageFor(URI uri)

    Page pageFor(String relativePath)

    Page newPageSampleFor(URI uri)

    public <T> T writePage(URI uri, Closure<T> closure)
}
