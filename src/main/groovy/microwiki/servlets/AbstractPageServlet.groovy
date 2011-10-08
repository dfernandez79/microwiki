package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.pages.Page
import microwiki.pages.PageProvider
import microwiki.pages.PageSourceNotFoundException
import microwiki.pages.PageTemplate

abstract class AbstractPageServlet extends HttpServlet {
    protected final PageProvider pageProvider
    protected final Map<String, PageTemplate> templates

    AbstractPageServlet(PageProvider pageProvider, Map<String, PageTemplate> templates) {
        this.pageProvider = pageProvider
        this.templates = templates
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        URI pageURI = pagePathFrom(req)
        resp.bufferSize = 64 * 1024

        try {
            render(pageFor(pageURI), templateFor(req), resp)
        } catch (PageSourceNotFoundException e) {
            resp.resetBuffer()
            pageSourceNotFound(pageURI, resp)
        }
    }

    protected abstract void pageSourceNotFound(URI pageURI, HttpServletResponse resp)

    protected def render(Page page, PageTemplate template, HttpServletResponse resp) {
        resp.characterEncoding = page.encoding
        template.applyTo(page).writeTo(resp.writer)
    }

    private Page pageFor(URI pageURI) {
        return pageProvider.pageFor(pageURI)
    }

    protected abstract PageTemplate templateFor(HttpServletRequest req)

    protected URI pagePathFrom(HttpServletRequest req) {
        def resource = req.servletContext.getResource(req.servletPath)
        if (resource != null) {
            return resource.toURI()
        } else {
            return new URI(req.servletPath.substring(1))
        }
    }
}
