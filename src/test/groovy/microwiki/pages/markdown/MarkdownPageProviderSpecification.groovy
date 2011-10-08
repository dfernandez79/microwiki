package microwiki.pages.markdown

import microwiki.TempDirectory
import microwiki.pages.PageProvider

class MarkdownPageProviderSpecification extends spock.lang.Specification {
    def "pageFor(uri) only accepts URIs relative to the docRoot"() {
        setup:
        File tempDir = TempDirectory.create()
        PageProvider provider = new MarkdownPageProvider(tempDir, 'UTF-8')

        when:
        provider.pageFor(new URI("file://absolute.md"))

        then:
        IllegalArgumentException e = thrown()
        e.message == "Only URIs relative ${tempDir.toURI()} are allowed"

        cleanup:
        tempDir.deleteDir()
    }
}
