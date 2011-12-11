package microwiki.servlets

import javax.servlet.http.HttpServletRequest
import microwiki.TempDirectory
import microwiki.pages.Page
import microwiki.servlets.view.Templates
import microwiki.pages.WritablePageProvider
import microwiki.pages.markdown.MarkdownPageProvider

class PageServletSpecification extends ServletSpecification {
    private static final URI NOT_FOUND_URI = new URI('notfound.md')
    private Page helloPage
    private WritablePageProvider pageProvider

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
        tempDirectory?.deleteDir()
    }

    @Override
    def setup() {
        pageProvider = new MarkdownPageProvider(tempDirectory, 'UTF-8')
        helloPage = pageProvider.pageFor('hello.md')
        templates = new Templates()
        servlet = new PageServlet(pageProvider, false, templates)
    }

    def "When method is GET display the page"() {
        when:
        servlet.doGet(requestFor('/hello.md'), response)

        then:
        responseOutput.toString() == templates.display.applyWith(context(helloPage)).toString()
    }

    Map context(Page page) {
        [page: page, searchEnabled: false]
    }

    def "When method is GET and the ?edit parameter is specified, display the edit page"() {
        when:
        servlet.doGet(requestFor('/hello.md', [edit: '']), response)

        then:
        responseOutput.toString() == templates.edit.applyWith(context(helloPage)).toString()
    }

    def "When method is POST and contents is specified, write the page contents"() {
        when:
        servlet.doPost(requestFor('/hello.md', [contents: 'New content']), response)

        then:
        helloPage.source.toString() == 'New content'

        cleanup:
        createHelloPage()
    }

    def "When method is POST, contents is specified and page doesn't exists, write the page contents"() {
        when:
        servlet.doPost(requestFor('/' + NOT_FOUND_URI.path, [contents: 'New content']), response)

        then:
        pageProvider.pageFor(NOT_FOUND_URI).source.toString() == 'New content'

        cleanup:
        new File(tempDirectory, NOT_FOUND_URI.path).delete()
    }

    def "If the file is not found, display the edit page with the new file action"() {
        when:
        servlet.doGet(requestFor('/' + NOT_FOUND_URI.path), response)

        then:
        responseOutput.toString() == templates.create.applyWith(context(pageProvider.newPageSampleFor(NOT_FOUND_URI))).toString()
    }

    def "If the file is not found and the ?edit parameter is specified, display the edit page with the new file action"() {
        when:
        servlet.doGet(requestFor('/' + NOT_FOUND_URI.path, [edit: '']), response)

        then:
        responseOutput.toString() == templates.create.applyWith(context(pageProvider.newPageSampleFor(NOT_FOUND_URI))).toString()
    }
}
