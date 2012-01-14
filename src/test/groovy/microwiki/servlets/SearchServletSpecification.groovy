package microwiki.servlets

import microwiki.pages.PageProvider

import javax.servlet.http.HttpServletResponse
import microwiki.servlets.view.Templates
import microwiki.servlets.view.TemplateAdapter
import javax.servlet.http.HttpServletRequest
import microwiki.search.PageSearchIndex
import microwiki.search.SearchResultsDisplayOptions

class SearchServletSpecification extends ServletSpecification {
    private PageSearchIndex searchIndex
    private SearchServlet servlet

    def setup() {
        searchIndex = Mock()
        servlet = new SearchServlet(searchIndex, new Templates(search: TemplateAdapter.using('search template')))
    }

    def "A search POST executes the search in the search index"() {
        when:
        servlet.doPost(searchRequest(), response)

        then:
        1 * searchIndex.search('test', SearchResultsDisplayOptions.default)
    }

    def "A search GET executes the search in the search index"() {
        when:
        servlet.doGet(searchRequest(), response)

        then:
        1 * searchIndex.search('test', SearchResultsDisplayOptions.default)
    }

    HttpServletRequest searchRequest() {
        requestFor('/search', [q: 'test'])
    }

    def "The search results are displayed using the search template"() {
        when:
        servlet.doPost(searchRequest(), response)

        then:
        responseOutput.toString() == 'search template'
    }
}
