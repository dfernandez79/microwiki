package microwiki.pages.markdown

import microwiki.TempDirectory
import microwiki.search.PageSearchIndex
import microwiki.search.SearchResult
import microwiki.search.SearchResults

class MarkdownPageProviderSpecification extends spock.lang.Specification {
    MarkdownPageProvider provider
    File tempDir

    def setup() {
        tempDir = TempDirectory.create()
        provider = new MarkdownPageProvider(tempDir, 'UTF-8')
    }

    def cleanup() {
        tempDir?.deleteDir()
    }

    def "pageFor(uri) only accepts URIs relative to the docRoot"() {
        when:
        provider.pageFor(new URI('file://absolute.md'))

        then:
        IllegalArgumentException e = thrown()
        e.message == "Only URIs relative to ${tempDir.toURI()} are allowed"
    }

    def "All the pages available can using the eachPage method"() {
        when:
        def pageURIs = [] as HashSet
        4.times { n ->
            def uri = 'test$n.md'.toURI()
            provider.writePage(uri) { out -> out.write 'Test $n'}
            pageURIs << uri
        }
        def pages = [] as HashSet
        provider.eachPage { pages << it.uri }

        then:
        pageURIs == pages
    }
}
