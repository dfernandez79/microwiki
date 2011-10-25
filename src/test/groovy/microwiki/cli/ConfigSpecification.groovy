package microwiki.cli

import microwiki.TempDirectory
import microwiki.pages.Page
import microwiki.pages.PageDisplayContext

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

    String configTemplateAndDisplay(File tempFile, String t) {
        def out = new StringWriter()
        configWith("templates { $t = '$tempFile' }").templates."$t".applyWith(contextFor(t)).writeTo(out)
        out.toString()
    }

    def configWith(String contents) {
        Config.readFrom(new StringReader(contents))
    }

    def PageDisplayContext contextFor(String template) {
        Page page = Mock()
        page.html >> {out -> out.write template}.asWritable()
        new PageDisplayContext(page, false)
    }
}
