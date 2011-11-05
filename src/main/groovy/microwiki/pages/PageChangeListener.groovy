package microwiki.pages

interface PageChangeListener {
    void creationOfPageIdentifiedBy(URI uri)

    void updateOfPageIdentifiedBy(URI uri)

    void removalOfPageIdentifiedBy(URI uri)
}
