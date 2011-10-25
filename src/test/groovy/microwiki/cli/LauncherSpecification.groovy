package microwiki.cli

import microwiki.cli.Launcher
import microwiki.Server
import microwiki.TempDirectory

class LauncherSpecification extends spock.lang.Specification {
    def "Display command line args help if the --help parameter is given"() {
        when:
        def server = null
        def output = captureOutputOf {
            server = new Launcher({uri->}, '--help').startServer()
        }

        then:
        server == null
        output.startsWith('usage: uwiki [options] <docroot>') // TODO compare with complete usage

        cleanup:
        server?.stop()
    }

    private String captureOutputOf(Closure closure) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        PrintStream newOut = new PrintStream(out)
        PrintStream oldOut = System.out
        System.out = newOut
        try {
            closure.run()
        } finally {
            System.out = oldOut
        }
        out.toString()
    }

    def "When microwiki is started show the README.md in the current directory"() {
        setup:
        File tempDir = createTempDirAndFileNamed('README.md')
        URI displayed = null
        Server server = new Launcher({ URI uri -> displayed = uri }, tempDir.absolutePath).startServer()

        expect:
        displayed == new URI("http://localhost:${Server.DEFAULT_PORT}/README.md")

        cleanup:
        server?.stop()
        tempDir?.deleteDir()
    }


    def createTempDirAndFileNamed(String file) {
        File tempDir = TempDirectory.create()
        new File(tempDir, file).text = 'Hello'
        tempDir
    }

    def "Launch the browser pointing to the correct port"() {
        setup:
        File tempDir = createTempDirAndFileNamed('README.md')
        URI displayed = null
        Server server = new Launcher(
                { URI uri -> displayed = uri },
                '--port', '9898', tempDir.absolutePath).startServer()

        expect:
        displayed == new URI("http://localhost:9898/README.md")

        cleanup:
        server?.stop()
        tempDir?.deleteDir()
    }

    def "If no README.md is found look for an index.md"() {
        setup:
        File tempDir =  createTempDirAndFileNamed('index.md')

        URI displayed = null
        Server server = new Launcher(
                { URI uri -> displayed = uri }, tempDir.absolutePath).startServer()

        expect:
        displayed == new URI("http://localhost:${Server.DEFAULT_PORT}/index.md")

        cleanup:
        server?.stop()
        tempDir?.deleteDir()
    }

    def "If no README.md or index.md in current directory, look for a docs directory"() {
        when:
        File tempDir = TempDirectory.create()
        def docRoot = new File(tempDir, 'docs')
        docRoot.mkdir()
        new File(docRoot, 'README.md').text  = 'Hello'
        def server = null
        def output = captureOutputOf {
            server = new Launcher({uri->}, tempDir.absolutePath).startServer()
        }

        then:
        output.contains("Document root: $docRoot")

        cleanup:
        server?.stop()
        tempDir?.deleteDir()
    }

    def "When the document root is invalid show an error"() {
        when:
        def server = new Launcher({uri->}, '/invalid/docroot').startServer()

        then:
        IllegalArgumentException e = thrown()
        e.message == '/invalid/docroot is not an existing or readable directory'

        cleanup:
        server?.stop()
    }

    def "main method catches IllegalArgumentExceptions and shows them as errors into the console"() {
        when:
        def output = captureOutputOf { Launcher.main('/invalid/docroot')  }

        then:
        output.readLines()[0] == 'ERROR: /invalid/docroot is not an existing or readable directory'
    }

    def "When the config file is invalid show an error"() {
        // TODO
    }

    def "Detect if the configuration file is present in the current directory and use it by default"() {
        // TODO
    }
}
