package microwiki.search.lucene

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory
import org.apache.lucene.util.Version

class LuceneFactory {
    private Version version

    LuceneFactory(Version version) {
        this.version = version
    }

    def <V> V withIndexWriterForCreationOn(Directory directory, Closure<V> closure) {
        withIndexWriter(directory, OpenMode.CREATE, closure)
    }

    def <V> V withIndexWriter(Directory directory, OpenMode mode, Closure<V> closure) {
        ensureClose(new IndexWriter(directory, newIndexWriterConfig(mode)), closure)
    }

    IndexWriterConfig newIndexWriterConfig(OpenMode openMode) {
        IndexWriterConfig config = new IndexWriterConfig(version, newStandardAnalyzer())
        config.openMode = openMode
        config
    }

    StandardAnalyzer newStandardAnalyzer() {
        new StandardAnalyzer(version)
    }

    QueryParser newParserFor(String fieldName) {
        new QueryParser(version, 'contents', newStandardAnalyzer())
    }

    def <V> V withReadOnlySearcherOn(Directory directory, Closure<V> closure) {
        ensureClose(new IndexSearcher(directory, true), closure)
    }

    def <V> V ensureClose(Closeable closeable, Closure<V> closure) {
        try {
            return closure.call(closeable)
        } finally {
            closeable.close()
        }
    }

    def <V> V withIndexWriterForAppendOn(Directory directory, Closure<V> closure) {
        withIndexWriter(directory, OpenMode.APPEND, closure)
    }
}
