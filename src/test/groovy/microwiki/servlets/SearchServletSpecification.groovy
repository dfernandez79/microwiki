package microwiki.servlets

import microwiki.pages.PageProvider
import com.sun.deploy.net.HttpResponse
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse
import microwiki.pages.Templates
import microwiki.pages.TemplateAdapter
import javax.servlet.http.HttpServletRequest

class SearchServletSpecification extends ServletSpecification {
    private PageProvider provider

    def setup() {
        provider = Mock()
    }

    def "A search POST executes the search in the provider"() {
        setup:
        provider.searchSupported >> true
        def servlet = new SearchServlet(provider, new Templates())

        when:
        servlet.doPost(searchRequest(), response)

        then:
        1 * provider.search('test')
    }

    def "A search GET executes the search in the provider"() {
        setup:
        provider.searchSupported >> true
        def servlet = new SearchServlet(provider, new Templates())

        when:
        servlet.doGet(searchRequest(), response)

        then:
        1 * provider.search('test')
    }

    HttpServletRequest searchRequest() {
        return requestFor('/search', [q: 'test'])
    }

    def "When search is not supported generates an error response"() {
        setup:
        provider.searchSupported >> false
        def servlet = new SearchServlet(provider, new Templates())

        when:
        servlet.doPost(searchRequest(), response)

        then:
        response.getStatus() ==  HttpServletResponse.SC_FORBIDDEN
    }

    def "The search results are displayed using the search template"() {
        setup:
        provider.searchSupported >> true
        def servlet = new SearchServlet(provider, new Templates(search: TemplateAdapter.using('search template')))

        when:
        servlet.doPost(searchRequest(), response)

        then:
        responseOutput.toString() == 'search template'
    }
}
