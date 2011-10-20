package microwiki.servlets

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import microwiki.TempDirectory
import microwiki.Templates
import microwiki.pages.Page
import microwiki.pages.PageDisplayContext
import microwiki.pages.WritablePageProvider
import microwiki.pages.markdown.MarkdownPageProvider

class PageServletSpecification extends spock.lang.Specification {
    private static final URI NOT_FOUND_URI = new URI('notfound.md')
    private Page helloPage
    private WritablePageProvider pageProvider
    private HttpServletResponse response
    private StringWriter responseOutput
    private Templates templates
    private PageServlet servlet
    private static File tempDirectory

    def setupSpec() {
        tempDirectory = TempDirectory.create()
        createHelloPage()
    }

    def createHelloPage() {
        new File(tempDirectory, 'hello.md').text = 'hello'
    }

    def cleanupSpec() {
        tempDirectory.deleteDir()
    }

    def setup() {
        pageProvider = new MarkdownPageProvider(tempDirectory, 'UTF-8')
        helloPage = pageProvider.pageFor('hello.md')

        response = Mock()
        response.contentType = 'text/html'
        response.characterEncoding = 'UTF-8'
        responseOutput = new StringWriter()
        response.writer >> { new PrintWriter(responseOutput) }
        response.reset() >> { responseOutput.buffer.length = 0 }

        templates = new Templates()
        servlet = new PageServlet(pageProvider, templates)
    }

    private HttpServletRequest requestFor(String path, Map<String, String> parameters = null) {
        HttpServletRequest req = Mock()
        req.servletPath >> path
        if (parameters != null) {
            req.getParameter(_ as String) >> { parameters.get(it.get(0)) }
        }
        req
    }

    def "When method is GET display the page"() {
        setup:
        HttpServletRequest request = requestFor('/hello.md')

        when:
        servlet.doGet(request, response)

        then:
        responseOutput.toString() == templates.display.applyWith(context(helloPage)).toString()
    }

    PageDisplayContext context(Page page) {
        new PageDisplayContext(page, false)
    }

    def "When method is GET and the ?edit parameter is specified, display the edit page"() {
        setup:
        HttpServletRequest request = requestFor('/hello.md', [edit: ''])

        when:
        servlet.doGet(request, response)

        then:
        responseOutput.toString() == templates.edit.applyWith(context(helloPage)).toString()
    }

    def "When method is POST and contents is specified, write the page contents"() {
        setup:
        HttpServletRequest request = requestFor('/hello.md', [contents: 'New content'])

        when:
        servlet.doPost(request, response)

        then:
        helloPage.source.toString() == 'New content'

        cleanup:
        createHelloPage()
    }

    def "When method is POST, contents is specified and page doesn't exists, write the page contents"() {
        setup:
        HttpServletRequest request = requestFor('/' + NOT_FOUND_URI.path, [contents: 'New content'])

        when:
        servlet.doPost(request, response)

        then:
        pageProvider.pageFor(NOT_FOUND_URI).source.toString() == 'New content'

        cleanup:
        new File(tempDirectory, NOT_FOUND_URI.path).delete()
    }

    def "If the file is not found, display the edit page with the new file action"() {
        setup:
        HttpServletRequest request = requestFor('/' + NOT_FOUND_URI.path)

        when:
        servlet.doGet(request, response)

        then:
        responseOutput.toString() == templates.create.applyWith(context(pageProvider.newPageSampleFor(NOT_FOUND_URI))).toString()
    }

    def "If the file is not found and the ?edit parameter is specified, display the edit page with the new file action"() {
        setup:
        HttpServletRequest request = requestFor('/' + NOT_FOUND_URI.path, [edit: ''])

        when:
        servlet.doGet(request, response)

        then:
        responseOutput.toString() == templates.create.applyWith(context(pageProvider.newPageSampleFor(NOT_FOUND_URI))).toString()
    }
}
