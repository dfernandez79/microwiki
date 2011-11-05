package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.servlets.view.Templates
import microwiki.pages.*
import microwiki.servlets.view.ViewTemplate

abstract class AbstractPageServlet extends HttpServlet {
    private static final int RESPONSE_BUFFER_SIZE = 64 * 1024
    protected final PageProvider pageProvider
    protected final Templates templates

    protected AbstractPageServlet(PageProvider pageProvider, Templates templates) {
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

    protected render(Page page, ViewTemplate template, HttpServletResponse resp) {
        resp.contentType = 'text/html'
        resp.characterEncoding = page.encoding
        template.applyWith(
                page: page,
                searchSupported: pageProvider.searchSupported).writeTo(resp.writer)
    }


    private Page pageFor(URI pageURI) {
        pageProvider.pageFor(pageURI)
    }

    protected abstract void pageSourceNotFound(URI pageURI, HttpServletResponse resp)

    protected abstract ViewTemplate templateFor(HttpServletRequest req)

    protected URI pagePathFrom(HttpServletRequest req) {
        new URI(req.servletPath.substring(1))
    }
}
