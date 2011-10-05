package microwiki

import microwiki.storage.impl.FilesystemPageStorage

class ServerSpecification  extends spock.lang.Specification {
    private static Server server
    private static TestResources resources
    private static Microwiki wiki

    def setupSpec() {
         resources = new TestResources().file('index.md', 'Main')
         wiki = new Microwiki(storage: new FilesystemPageStorage(resources.tempDirectory))

         server = new Server(wiki, 9999)
         server.start()
    }

    def cleanupSpec() {
        server.stop()
        server = null

        resources.cleanup()
        resources = null
    }

    def "serve the CSS for wiki pages"() {
        expect:
            'http://localhost:9999/style.css'.toURL().text == getClass().getResource('static/style.css').text
    }

    def "serve a wiki page"() {
        expect:
            'http://localhost:9999/index'.toURL().text == wiki.htmlToDisplay('index').toString()
    }
}
