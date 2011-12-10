package microwiki.servlets

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.search.PageSearchIndex
import microwiki.search.SearchResults
import microwiki.search.SearchResultsDisplayOptions
import microwiki.servlets.view.Templates

class SearchServlet extends HttpServlet {
    private PageSearchIndex searchIndex
    private SearchResultsDisplayOptions displayOptions
    private Templates templates

    public SearchServlet(PageSearchIndex searchIndex, Templates templates) {
        this(searchIndex, SearchResultsDisplayOptions.default, templates)
    }

    public SearchServlet(PageSearchIndex searchIndex, SearchResultsDisplayOptions displayOptions, Templates templates) {
        this.searchIndex = searchIndex
        this.displayOptions = displayOptions
        this.templates = templates
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doGet(req, resp)
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String query = req.getParameter('q')
        if (query == null || query.trim().empty) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 'A search query must be specified')
            return
        }

        SearchResults results = searchIndex.search(query, displayOptions)
        resp.contentType = 'text/html'
        templates.search.applyWith(results: results).writeTo(resp.writer)
    }
}
