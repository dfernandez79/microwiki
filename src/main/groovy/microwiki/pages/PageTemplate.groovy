package microwiki.pages

interface PageTemplate {
    Writable applyTo(Page page)
}
