package microwiki.servlets.view

interface ViewTemplate {
    Writable applyWith(Map context)
}
