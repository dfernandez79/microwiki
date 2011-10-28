package microwiki.pages

interface PageTemplate {
    Writable applyWith(Map context)
}
