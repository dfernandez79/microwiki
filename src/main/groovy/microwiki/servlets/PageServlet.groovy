package microwiki.servlets

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.Templates
import microwiki.pages.PageProvider
import microwiki.pages.PageTemplate

class PageServlet extends AbstractPageServlet {
    PageServlet(PageProvider pageProvider, Templates templates) {
        super(pageProvider, templates)
    }

    @Override
    protected PageTemplate templateFor(HttpServletRequest req) {
        if (req.getParameter('edit') != null) {
            templates.edit
        } else {
            templates.display
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String contents = req.getParameter('contents')
        if (contents != null) {
            pageProvider.writePage(pagePathFrom(req)) { Writer out -> out.write contents }
        }
        doGet(req, resp)
    }

    @Override
    protected void pageSourceNotFound(URI pageURI, HttpServletResponse resp) {
        render(pageProvider.newPageSampleFor(pageURI), templates.create, resp)
    }
}
