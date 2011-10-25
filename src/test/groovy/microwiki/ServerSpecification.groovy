package microwiki

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.util.resource.Resource
import spock.lang.Timeout

class ServerSpecification extends spock.lang.Specification {
    private static Server server
    private static File tempDirectory

    def setupSpec() {
        tempDirectory = TempDirectory.create()
        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
                resp.writer.print 'Hello From Servlet'
            }
        }
        server = new Server(Resource.newResource(tempDirectory), servlet, Server.DEFAULT_PORT)
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
}
