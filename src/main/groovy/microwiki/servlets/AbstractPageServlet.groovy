package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.pages.Page
import microwiki.pages.PageProvider
import microwiki.pages.PageSourceNotFoundException
import microwiki.servlets.view.Templates
import microwiki.servlets.view.ViewTemplate

abstract class AbstractPageServlet extends HttpServlet {
    private static final int RESPONSE_BUFFER_SIZE = 64 * 1024
    protected final PageProvider pageProvider
    protected final boolean searchEnabled

    protected AbstractPageServlet(PageProvider pageProvider, boolean searchEnabled) {
        this.pageProvider = pageProvider
        this.searchEnabled = searchEnabled
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
                searchEnabled: searchEnabled).writeTo(resp.writer)
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
