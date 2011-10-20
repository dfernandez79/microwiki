package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.Templates
import microwiki.pages.*

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

    protected render(Page page, PageTemplate template, HttpServletResponse resp) {
        resp.contentType = 'text/html'
        resp.characterEncoding = page.encoding
        template.applyWith(displayContextFor(page)).writeTo(resp.writer)
    }

    protected PageDisplayContext displayContextFor(Page page) {
        new PageDisplayContext(page, pageProvider.searchSupported)
    }

    private Page pageFor(URI pageURI) {
        pageProvider.pageFor(pageURI)
    }

    protected abstract void pageSourceNotFound(URI pageURI, HttpServletResponse resp)

    protected abstract PageTemplate templateFor(HttpServletRequest req)

    protected URI pagePathFrom(HttpServletRequest req) {
        new URI(req.servletPath.substring(1))
    }
}
