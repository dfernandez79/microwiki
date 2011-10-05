package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.Microwiki

class PageDisplayServlet extends HttpServlet {
    private Microwiki wiki

    PageDisplayServlet(Microwiki wiki) {
        this.wiki = wiki
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        wiki.htmlToDisplay(req.pathInfo?.substring(1)).writeTo(resp.getWriter())
    }
}
