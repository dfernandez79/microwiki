package microwiki.pages.markdown

import microwiki.TempDirectory
import microwiki.search.PageSearchStrategy
import microwiki.search.SearchResults

class MarkdownPageProviderSpecification extends spock.lang.Specification {
    MarkdownPageProvider provider
    File tempDir
    private static final MOCK_SEARCH_RESULTS = SearchResults.empty()

    def setup() {
        tempDir = TempDirectory.create()
        provider = new MarkdownPageProvider(tempDir, 'UTF-8')
    }

    def cleanup() {
        tempDir.deleteDir()
    }

    def "pageFor(uri) only accepts URIs relative to the docRoot"() {
        when:
        provider.pageFor(new URI("file://absolute.md"))

        then:
        IllegalArgumentException e = thrown()
        e.message == "Only URIs relative ${tempDir.toURI()} are allowed"
    }

    def "When a search strategy is not provided, the search returns empty results -not exception"() {
        expect:
        !provider.searchSupported
        provider.search('text').empty
    }

    def "Search is delegated to the search strategy"() {
        setup:
        PageSearchStrategy mockSearchStrategy = Mock()
        mockSearchStrategy.searchSupported >> true
        mockSearchStrategy.search(_) >> MOCK_SEARCH_RESULTS
        def providerWithSearch = new MarkdownPageProvider(tempDir, 'UTF-8', mockSearchStrategy)

        expect:
        providerWithSearch.searchSupported
        providerWithSearch.search('text') == MOCK_SEARCH_RESULTS
    }
}
