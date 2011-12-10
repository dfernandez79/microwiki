package microwiki.pages

interface PageProvider {
    Page pageFor(URI uri)

    Page pageFor(String relativePath)

    void eachPage(Closure closure)
}
