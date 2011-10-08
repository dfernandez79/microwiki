package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.Templates
import microwiki.pages.Page
import microwiki.pages.PageProvider
import microwiki.pages.PageSourceNotFoundException
import microwiki.pages.PageTemplate

abstract class AbstractPageServlet extends HttpServlet {
    private static final int RESPONSE_BUFFER_SIZE = 64 * 1024
    protected final PageProvider pageProvider
    protected final Templates templates

    AbstractPageServlet(PageProvider pageProvider, Templates templates) {
        this.pageProvider = pageProvider
        this.templates = templates
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        URI pageURI = pagePathFrom(req)
        resp.bufferSize = RESPONSE_BUFFER_SIZE

        try {
            render(pageFor(pageURI), templateFor(req), resp)
        } catch (PageSourceNotFoundException e) {
            resp.reset()
            pageSourceNotFound(pageURI, resp)
        }
    }

    protected abstract void pageSourceNotFound(URI pageURI, HttpServletResponse resp)

    protected def render(Page page, PageTemplate template, HttpServletResponse resp) {
        resp.contentType = 'text/html'
        resp.characterEncoding = page.encoding
        template.applyTo(page).writeTo(resp.writer)
    }

    private Page pageFor(URI pageURI) {
        return pageProvider.pageFor(pageURI)
    }

    protected abstract PageTemplate templateFor(HttpServletRequest req)

    protected URI pagePathFrom(HttpServletRequest req) {
        return new URI(req.servletPath.substring(1))
    }
}
