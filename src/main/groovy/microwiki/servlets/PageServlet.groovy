package microwiki.servlets

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.Templates
import microwiki.pages.PageTemplate
import microwiki.pages.WritablePageProvider

class PageServlet extends AbstractPageServlet {
    PageServlet(WritablePageProvider pageProvider, Templates templates) {
        super(pageProvider, templates)
    }

    @Override
    protected PageTemplate templateFor(HttpServletRequest req) {
        (req.getParameter('edit') != null) ? templates.edit : templates.display
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String contents = req.getParameter('contents')
        if (contents != null) {
            ((WritablePageProvider) pageProvider).writePage(pagePathFrom(req)) { Writer out -> out.write contents }
        }
        doGet(req, resp)
    }

    @Override
    protected void pageSourceNotFound(URI pageURI, HttpServletResponse resp) {
        render(((WritablePageProvider) pageProvider).newPageSampleFor(pageURI), templates.create, resp)
    }
}
