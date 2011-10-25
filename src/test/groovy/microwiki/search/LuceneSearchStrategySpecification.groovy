package microwiki.search

import microwiki.TempDirectory
import microwiki.search.lucene.LuceneSearchStrategy
import org.apache.commons.vfs2.FileChangeEvent
import org.apache.commons.vfs2.FileListener
import org.apache.commons.vfs2.VFS

class LuceneSearchStrategySpecification extends spock.lang.Specification {
    private static File tempDirectory
    private File testFile
    private LuceneSearchStrategy strategy

    def setupSpec() {
        tempDirectory = TempDirectory.create()
    }

    def cleanupSpec() {
        tempDirectory?.deleteDir()
    }

    def setup() {
        testFile = new File(tempDirectory, 'test.md')
        testFile.text = 'test'
        strategy = new LuceneSearchStrategy(tempDirectory, ~/.*\.md/)
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

    def "Can listen for file changes"() {
        expect:
        FileListener.isAssignableFrom(LuceneSearchStrategy)
    }

    def "Changes in files updates the search index"() {
        when:
        testFile.text = 'new content'
        strategy.fileChanged(new FileChangeEvent(VFS.manager.toFileObject(testFile)))

        then:
        strategy.search('test', SearchResultsDisplayOptions.defaultOptions()).retrievedResults.empty
    }

    def "Removed files are removed from the search index"() {
        when:
        testFile.delete()
        strategy.fileDeleted(new FileChangeEvent(VFS.manager.toFileObject(testFile)))

        then:
        strategy.search('test', SearchResultsDisplayOptions.defaultOptions()).retrievedResults.empty
    }

    def "New files are indexed"() {
        when:
        File newFile = new File(tempDirectory, 'new.md')
        newFile.text = 'hello world'
        strategy.fileCreated(new FileChangeEvent(VFS.manager.toFileObject(newFile)))
        def results = strategy.search('hello', SearchResultsDisplayOptions.defaultOptions())

        then:
        !results.retrievedResults.empty
        results.retrievedResults.size() == 1
        results.retrievedResults[0].uri == new URI('new.md')
    }
}
