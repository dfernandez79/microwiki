package microwiki.servlets

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.servlets.view.Templates
import microwiki.pages.PageProvider
import microwiki.servlets.view.ViewTemplate

class ReadonlyPageServlet extends AbstractPageServlet {
    ReadonlyPageServlet(PageProvider pageProvider, Templates templates) {
        super(pageProvider, templates)
    }

    @Override
    protected void pageSourceNotFound(URI pageURI, HttpServletResponse resp) {
        resp.status = HttpServletResponse.SC_NOT_FOUND
    }

    @Override
    protected ViewTemplate templateFor(HttpServletRequest req) {
        templates.read
    }
}
