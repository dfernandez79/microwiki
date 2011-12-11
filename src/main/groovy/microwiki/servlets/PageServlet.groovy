package microwiki.servlets

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.pages.WritablePageProvider
import microwiki.servlets.view.Templates
import microwiki.servlets.view.ViewTemplate

class PageServlet extends AbstractPageServlet {
    private Templates templates

    PageServlet(WritablePageProvider pageProvider, boolean searchEnabled, Templates templates) {
        super(pageProvider, searchEnabled)
        this.templates = templates
    }

    @Override
    protected ViewTemplate templateFor(HttpServletRequest req) {
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
