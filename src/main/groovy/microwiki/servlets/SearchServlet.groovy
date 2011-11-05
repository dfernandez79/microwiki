package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.pages.PageProvider
import microwiki.servlets.view.Templates
import microwiki.search.SearchResults

class SearchServlet extends HttpServlet {
    private PageProvider pageProvider
    private Templates templates

    public SearchServlet(PageProvider pageProvider, Templates templates) {
        this.pageProvider = pageProvider
        this.templates = templates
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doGet(req, resp)
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (!pageProvider.searchSupported) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, 'The page search functionality is not available')
            return
        }

        String query = req.getParameter('q')
        if (query == null || query.trim().empty) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 'A search query must be specified')
            return
        }

        SearchResults results = pageProvider.search(query)
        resp.contentType = 'text/html'
        templates.search.applyWith(results: results).writeTo(resp.writer)
    }
}
