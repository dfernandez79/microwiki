package microwiki.pages.markdown

import microwiki.TempDirectory
import microwiki.search.PageSearchStrategy
import microwiki.search.SearchResult
import microwiki.search.SearchResults

class MarkdownPageProviderSpecification extends spock.lang.Specification {
    MarkdownPageProvider provider
    File tempDir
    private static final MOCK_SEARCH_RESULTS = new SearchResults('', null, 0, Collections.<SearchResult> emptyList())

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

    def "When a search strategy is not provided, the search returns no results -does not throw exception"() {
        expect:
        !provider.searchSupported
        provider.search('text').total == 0
    }

    def "Search is delegated to the search strategy"() {
        setup:
        PageSearchStrategy mockSearchStrategy = Mock()
        mockSearchStrategy.searchSupported >> true
        mockSearchStrategy.search(_, _) >> MOCK_SEARCH_RESULTS
        def providerWithSearch = new MarkdownPageProvider(tempDir, 'UTF-8', mockSearchStrategy)

        expect:
        providerWithSearch.searchSupported
        providerWithSearch.search('text') == MOCK_SEARCH_RESULTS
    }

    def "All the pages available can using the eachPage method"() {
        when:
        def pageURIs = new HashSet<URI>();
        4.times { n ->
            def uri = 'test$n.md'.toURI()
            provider.writePage(uri) { out -> out.write 'Test $n'}
            pageURIs << uri
        }
        def pages = new HashSet<URI>()
        provider.eachPage { pages << it.uri }

        then:
        pageURIs == pages
    }
}
