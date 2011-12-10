package microwiki.search.lucene

import microwiki.TempDirectory
import microwiki.pages.WritablePageProvider
import microwiki.pages.markdown.MarkdownPageProvider
import microwiki.search.SearchResultsDisplayOptions

class LucenePageSearchIndexSpecification extends spock.lang.Specification {
    private static File tempDirectory
    private File testFile
    private LucenePageSearchIndex searchIndex
    private WritablePageProvider provider

    def setupSpec() {
        tempDirectory = TempDirectory.create()
    }

    def cleanupSpec() {
        tempDirectory?.deleteDir()
    }

    def setup() {
        testFile = new File(tempDirectory, 'file.md')
        testFile.text = '''Test Page
-----------

This is a test page'''

        provider = new MarkdownPageProvider(tempDirectory, 'UTF-8')
        searchIndex = new LucenePageSearchIndex(provider)
    }

    def cleanup() {
        testFile.delete()
    }

    def "Search for text in an existing file"() {
        when:
        def results = searchIndex.search('test', SearchResultsDisplayOptions.default)

        then:
        !results.retrievedResults.empty
        results.retrievedResults.size() == 1
        results.retrievedResults[0].uri == new URI('file.md')
    }

    def "Changes in pages updates the search index"() {
        when:
        provider.writePage('file.md'.toURI()) { out -> out.write 'hello' }
        searchIndex.updateOfPageIdentifiedBy('file.md'.toURI())

        then:
        searchIndex.search('test', SearchResultsDisplayOptions.default).retrievedResults.empty
        searchIndex.search('hello', SearchResultsDisplayOptions.default).retrievedResults.size() == 1
    }

    def "Removed pages are removed from the search index"() {
        when:
        testFile.delete()
        searchIndex.removalOfPageIdentifiedBy('file.md'.toURI())

        then:
        searchIndex.search('test', SearchResultsDisplayOptions.default).retrievedResults.empty
    }

    def "New pages are indexed"() {
        when:
        provider.writePage('new.md'.toURI()) { out -> out.write 'hello world' }
        searchIndex.creationOfPageIdentifiedBy('new.md'.toURI())
        def results = searchIndex.search('hello', SearchResultsDisplayOptions.default)

        then:
        !results.retrievedResults.empty
        results.retrievedResults.size() == 1
        results.retrievedResults[0].uri == new URI('new.md')
    }

    def "Search result contains highlight fragments"() {
        when:
        def results = searchIndex.search('test', SearchResultsDisplayOptions.default)

        then:
        !results.retrievedResults.empty
        results.retrievedResults.size() == 1
        results.retrievedResults[0].highlights.size() == 1
        results.retrievedResults[0].highlights == ['''<B>Test</B> Page
-----------

This is a <B>test</B> page''']
    }

    def "The results reports the query used to obtain them"() {
        when:
        def results = searchIndex.search('test', SearchResultsDisplayOptions.default)

        then:
        results.textToSearch == 'test'
    }

    def "The file name is also included in search"() {
        when:
        def results = searchIndex.search('file', SearchResultsDisplayOptions.default)

        then:
        !results.retrievedResults.empty
        results.retrievedResults.size() == 1
        results.retrievedResults[0].uri == new URI('file.md')
    }

    def "The search result includes the page title"() {
        when:
        def results = searchIndex.search('file', SearchResultsDisplayOptions.default)

        then:
        !results.retrievedResults.empty
        results.retrievedResults.size() == 1
        results.retrievedResults[0].title == 'Test Page'
    }
}
