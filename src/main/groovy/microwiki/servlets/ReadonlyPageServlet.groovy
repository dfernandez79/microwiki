package microwiki.servlets

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.pages.Templates
import microwiki.pages.PageProvider
import microwiki.pages.PageTemplate

class ReadonlyPageServlet extends AbstractPageServlet {
    ReadonlyPageServlet(PageProvider pageProvider, Templates templates) {
        super(pageProvider, templates)
    }

    @Override
    protected void pageSourceNotFound(URI pageURI, HttpServletResponse resp) {
        resp.status = HttpServletResponse.SC_NOT_FOUND
    }

    @Override
    protected PageTemplate templateFor(HttpServletRequest req) {
        templates.read
    }
}
