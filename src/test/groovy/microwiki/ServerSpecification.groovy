package microwiki

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import spock.lang.Timeout

import microwiki.config.dsl.ConfigBuilder

class ServerSpecification extends spock.lang.Specification {
    private static Server server
    private static File tempDirectory

    def setupSpec() {
        tempDirectory = TempDirectory.create()
        server = new Server(
                tempDirectory,
                new ConfigBuilder().with {
                    disablePageEditing()
                    templates.read = inlineTemplate('Hello From Servlet')
                    templates.search = inlineTemplate('Search results')
                    build()
                })
        server.start()
    }

    def cleanupSpec() {
        server?.stop()
        tempDirectory?.deleteDir()
    }

    @Timeout(3)
    def "When style.css is not found in the base directory, the one in /microwiki/static classpath is used"() {
        expect:
        contentOf('http://localhost:9999/style.css') == staticResource('style.css')
    }

    private String staticResource(String resource) {
        getClass().getResource("/microwiki/static/$resource").text
    }

    private String contentOf(String url) {
        url.toURL().text
    }

    @Timeout(3)
    def "When style.css is found in the base directory, that one is used first"() {
        setup:
        new File(tempDirectory, 'style.css').text = '.test-style {}'

        expect:
        contentOf('http://localhost:9999/style.css') == '.test-style {}'
    }

    @Timeout(3)
    def "Non .md files are read from the base directory"() {
        setup:
        new File(tempDirectory, 'other.txt').text = 'Hello'

        expect:
        contentOf('http://localhost:9999/other.txt') == 'Hello'
    }

    @Timeout(3)
    def ".md files are displayed using the servlet"() {
        setup:
        def mdFile = new File(tempDirectory, 'hello.md')
        mdFile.text = 'Hello'

        expect:
        contentOf('http://localhost:9999/hello.md') == 'Hello From Servlet'
    }

    def "When search is enabled, register the search servlet"() {
        expect:
        server.config.search.enabled
        contentOf('http://localhost:9999/search?q=test') == 'Search results'
    }
}
