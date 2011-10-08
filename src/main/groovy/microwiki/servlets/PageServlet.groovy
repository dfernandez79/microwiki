package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.pages.Page
import microwiki.pages.PageFactory
import microwiki.pages.PageTemplate

class PageServlet extends HttpServlet {
    private final PageFactory pageFactory
    private final PageTemplate displayTemplate
    private final PageTemplate editTemplate

    PageServlet(PageFactory pageFactory, PageTemplate displayTemplate, PageTemplate editTemplate) {
        this.pageFactory = pageFactory
        this.displayTemplate = displayTemplate
        this.editTemplate = editTemplate
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        displayTemplate.applyTo(pageFrom(req)).writeTo(resp.writer)
    }

    private Page pageFrom(HttpServletRequest req) {
        pageFactory.createPage(req.servletContext.getResource(req.servletPath))
    }
}
