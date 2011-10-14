package microwiki.pages

interface PageProvider {
    Page pageFor(URI uri)

    Page pageFor(String relativePath)
}
