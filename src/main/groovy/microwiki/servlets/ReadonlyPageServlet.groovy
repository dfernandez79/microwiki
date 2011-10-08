package microwiki.servlets

import microwiki.pages.PageProvider
import microwiki.pages.PageTemplate
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest

class ReadonlyPageServlet extends AbstractPageServlet {
    ReadonlyPageServlet(PageProvider pageProvider, Map<String, PageTemplate> templates) {
        super(pageProvider, templates)
    }

    @Override
    protected void pageSourceNotFound(URI pageURI, HttpServletResponse resp) {
        resp.status = HttpServletResponse.SC_NOT_FOUND
    }

    @Override
    protected PageTemplate templateFor(HttpServletRequest req) {
        return templates.read
    }
}
