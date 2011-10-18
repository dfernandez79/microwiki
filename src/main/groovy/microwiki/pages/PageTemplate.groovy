package microwiki.pages

interface PageTemplate {
    Writable applyWith(PageDisplayContext context)
}
