package microwiki.config

import microwiki.Server
import microwiki.TempDirectory

import microwiki.pages.Page

import microwiki.servlets.view.Templates

class ConfigSpecification extends spock.lang.Specification {
    def "Server port can be configured with the server section"() {
        when:
        def config = configWith('server { port = 8787 }')

        then:
        config.server.port == 8787
    }

    def "Server readonly mode can be configured with the server section"() {
        when:
        def config = configWith('server { readOnly = true }')

        then:
        config.server.readOnly
    }

    def "By default server is not in readonly mode"() {
        when:
        def config = configWith('')

        then:
        !config.server.readOnly
    }

    def "Default server encoding is UTF-8"() {
        when:
        def config = configWith('')

        then:
        config.server.encoding == 'UTF-8'
    }

    def "Templates can be configured with the templates section"() {
        setup:
        File tempDir = TempDirectory.create()
        File tempFile = new File(tempDir, 'template.html')
        tempFile.text = '${page.html}'

        expect:
        configTemplateAndDisplay(tempFile, template) == "$template".toString()

        cleanup:
        tempDir?.deleteDir()

        where:
        template << ['display', 'edit', 'create', 'read']
    }

    def "Default configuration"() {
        when:
        def config = new Config()

        then:
        config.server.port == Server.DEFAULT_PORT
        config.server.encoding == Server.DEFAULT_ENCODING
        !config.server.readOnly
        config.templates.display == Templates.DEFAULT_DISPLAY_TEMPLATE
        config.templates.create == Templates.DEFAULT_CREATE_TEMPLATE
        config.templates.edit == Templates.DEFAULT_EDIT_TEMPLATE
        config.templates.read == Templates.DEFAULT_READ_TEMPLATE
    }

    def "Search is enabled by default"() {
        expect:
        new Config().search.enabled
    }

    def "toString shows the configuration options - non-relevant defaults are hidden"() {
        when:
        def config = new Config()

        then:
        config.toString() == """Port: ${config.server.port}
Encoding: ${config.server.encoding}
""".toString()
    }

    def "toString shows the configuration options - non defaults are displayed"() {
        setup:
        File tempDir = TempDirectory.create()
        File tempFile = new File(tempDir, 'template.html')
        tempFile.text = '${page.html}'

        when:
        def config = configWith("""
        server { port = 6767; readOnly = true; encoding = 'ISO-8859-1'}
        templates { edit = '${tempFile.absolutePath}'; read = '${tempFile.absolutePath}' }
        search { enabled = false }""")

        then:
        config.toString() == """Port: ${config.server.port}
Encoding: ${config.server.encoding}
Read Only Mode
Search is disabled
Templates:
    Edit: ${config.templates.edit}
    Read: ${config.templates.read}
""".toString()

        cleanup:
        tempDir?.deleteDir()
    }

    def "Specify the search index storage method"() {
        when:
        def config = configWith('search { indexStorageMethod = memory }')

        then:
        config.search.indexStorageMethod == SearchConfig.MEMORY_INDEX_STORAGE
    }

    def "Specify the search index storage using filesystem and a directory"() {
        when:
        def config = configWith('search { indexStorageMethod = filesystem(\'index\') }')

        then:
        config.search.indexStorageMethod == [directory: new File('index')]
    }

    def "By default use the file system to store the search index"() {
        expect:
        new Config().search.indexStorageMethod == SearchConfig.DEFAULT_FILESYSTEM_STORAGE
    }
    
    def "By default the file system method uses the .microwiki-index relative directory"() {
        expect:
        new Config().search.indexStorageMethod.directory == new File('.microwiki-index')
    }

    private String configTemplateAndDisplay(File tempFile, String t) {
        def out = new StringWriter()
        configWith("templates { $t = '$tempFile' }").templates."$t".applyWith(contextFor(t)).writeTo(out)
        out.toString()
    }

    private configWith(String contents) {
        Config.readFrom(new StringReader(contents))
    }

    private Map contextFor(String template) {
        Page page = Mock()
        page.html >> {out -> out.write template}.asWritable()
        [page: page, searchSupported: false]
    }
}
