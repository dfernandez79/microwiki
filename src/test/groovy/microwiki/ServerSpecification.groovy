package microwiki

import org.eclipse.jetty.util.resource.Resource
import spock.lang.Timeout

class ServerSpecification extends spock.lang.Specification {
    private static Server server
    private static File tempDirectory

    def setupSpec() {
        tempDirectory = TempDirectory.create()
        server = new Server(Resource.newResource(tempDirectory))
        server.start()
    }

    def cleanupSpec() {
        server.stop()
        tempDirectory.deleteDir()
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
        contentOf('http://localhost:9999/hello.md') == htmlFor(mdFile)
    }

    private String htmlFor(File file) {
        server.templates.display.applyTo(server.pageProvider.pageFor(tempDirectory.toURI().relativize(file.toURI())))
    }

    def "Display command line args help if the --help parameter is given"() {
        when:
        def output = captureOutputOf { Server.main('--help') }

        then:
        output == captureOutputOf { Server.showCommandLineHelp() }
    }

    private String captureOutputOf(Closure closure) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        PrintStream newOut = new PrintStream(out)
        PrintStream oldOut = System.out
        System.setOut(newOut)
        try {
            closure.run()
        } finally {
            System.setOut(oldOut)
        }
        return out.toString()
    }
}
