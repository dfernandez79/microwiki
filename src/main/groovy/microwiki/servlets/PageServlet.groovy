package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.pages.Page
import microwiki.pages.PageFactory
import microwiki.pages.PageTemplate

class PageServlet extends HttpServlet {
     private PageTemplate template
     private PageFactory pageFactory

    PageServlet(PageFactory pageFactory, PageTemplate template) {
        this.pageFactory = pageFactory
        this.template = template
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        template.applyTo(pageFrom(req)).writeTo(resp.writer)
    }

    private Page pageFrom(HttpServletRequest req) {
        pageFactory.createPage(req.servletContext.getResource(req.servletPath))
    }
}
