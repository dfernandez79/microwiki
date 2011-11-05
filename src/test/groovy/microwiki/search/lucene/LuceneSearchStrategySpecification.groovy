package microwiki.search.lucene

import microwiki.TempDirectory
import microwiki.pages.WritablePageProvider
import microwiki.pages.markdown.MarkdownPageProvider
import microwiki.search.SearchResultsDisplayOptions

class LuceneSearchStrategySpecification extends spock.lang.Specification {
    private static File tempDirectory
    private File testFile
    private LuceneSearchStrategy strategy
    private WritablePageProvider provider

    def setupSpec() {
        tempDirectory = TempDirectory.create()
    }

    def cleanupSpec() {
        tempDirectory?.deleteDir()
    }

    def setup() {
        testFile = new File(tempDirectory, 'test.md')
        testFile.text = '''Test Page
-----------

This is a test page'''

        provider = new MarkdownPageProvider(tempDirectory, 'UTF-8')
        strategy = new LuceneSearchStrategy(provider)
    }

    def cleanup() {
        testFile.delete()
    }

    def "Search for text in an existing file"() {
        when:
        def results = strategy.search('test', SearchResultsDisplayOptions.defaultOptions())

        then:
        !results.retrievedResults.empty
        results.retrievedResults.size() == 1
        results.retrievedResults[0].uri == new URI('test.md')
    }

    def "Changes in pages updates the search index"() {
        when:
        provider.writePage('test.md'.toURI()) { out -> out.write 'hello' }
        strategy.updateOf(provider.pageFor('test.md'.toURI()))

        then:
        strategy.search('test', SearchResultsDisplayOptions.defaultOptions()).retrievedResults.empty
        strategy.search('hello', SearchResultsDisplayOptions.defaultOptions()).retrievedResults.size() == 1
    }

    def "Removed pages are removed from the search index"() {
        when:
        testFile.delete()
        strategy.removalOfPageIdentifiedBy('test.md'.toURI())

        then:
        strategy.search('test', SearchResultsDisplayOptions.defaultOptions()).retrievedResults.empty
    }

    def "New pages are indexed"() {
        when:
        provider.writePage('new.md'.toURI()) { out -> out.write 'hello world' }
        strategy.creationOf(provider.pageFor('new.md'.toURI()))
        def results = strategy.search('hello', SearchResultsDisplayOptions.defaultOptions())

        then:
        !results.retrievedResults.empty
        results.retrievedResults.size() == 1
        results.retrievedResults[0].uri == new URI('new.md')
    }

    def "Search result contains highlight fragments"() {
        when:
        def results = strategy.search('test', SearchResultsDisplayOptions.defaultOptions())

        then:
        !results.retrievedResults.empty
        results.retrievedResults.size() == 1
        results.retrievedResults[0].highlights.size() == 1
        results.retrievedResults[0].highlights == ['''<B>Test</B> Page
-----------

This is a <B>test</B> page''']
    }
}
