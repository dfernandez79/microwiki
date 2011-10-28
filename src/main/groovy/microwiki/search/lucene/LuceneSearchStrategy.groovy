package microwiki.search.lucene

import groovy.io.FileType
import java.util.regex.Pattern
import microwiki.search.PageSearchStrategy
import microwiki.search.SearchResult
import microwiki.search.SearchResults
import microwiki.search.SearchResultsDisplayOptions
import org.apache.commons.vfs2.FileChangeEvent
import org.apache.commons.vfs2.FileListener
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.Field.Index
import org.apache.lucene.document.Field.Store
import org.apache.lucene.index.FieldInfo.IndexOptions
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version

class LuceneSearchStrategy implements PageSearchStrategy, FileListener {
    private final File docRoot
    private final URI docRootURI
    private final Directory searchIndexDirectory
    private final LuceneFactory luceneFactory
    private final Pattern fileNamePattern

    LuceneSearchStrategy(File docRoot, Pattern fileNamePattern) {
        this(docRoot, fileNamePattern, new RAMDirectory(), new LuceneFactory(Version.LUCENE_34))
    }

    LuceneSearchStrategy(File docRoot, Pattern fileNamePattern, Directory searchIndexDirectory, LuceneFactory luceneFactory) {
        this.docRoot = docRoot
        docRootURI = docRoot.toURI()
        this.searchIndexDirectory = searchIndexDirectory
        this.luceneFactory = luceneFactory
        this.fileNamePattern = fileNamePattern
        createSearchIndex()
    }

    @Override
    void fileCreated(FileChangeEvent event) {
        luceneFactory.withIndexWriterForAppendOn(searchIndexDirectory) { IndexWriter idxWriter ->
            idxWriter.addDocument(luceneDocumentFor(event.file.URL.toURI()))
        }
    }

    @Override
    void fileDeleted(FileChangeEvent event) {
        luceneFactory.withIndexWriterForAppendOn(searchIndexDirectory) { IndexWriter idxWriter ->
            idxWriter.deleteDocuments(new Term('uri', docRootURI.relativize(event.file.URL.toURI()).toString()))
        }
    }

    @Override
    void fileChanged(FileChangeEvent event) {
        updateIndexWith(luceneDocumentFor(event.file.URL.toURI()))
    }

    private void updateIndexWith(Document document) {
        luceneFactory.withIndexWriterForAppendOn(searchIndexDirectory) { IndexWriter idxWriter ->
            idxWriter.updateDocument(new Term('uri', document.get('uri')), document)
        }
    }

    @Override
    boolean isSearchSupported() {
        true
    }

    @Override
    SearchResults search(String query, SearchResultsDisplayOptions options) {
        luceneFactory.withReadOnlySearcherOn(searchIndexDirectory) { IndexSearcher searcher ->
            createSearchResults(searcher, luceneFactory.newParserFor('contents').parse(query), options)
        }
    }

    SearchResults createSearchResults(IndexSearcher searcher, Query query, SearchResultsDisplayOptions options) {
        TopDocs topDocs = searcher.search(query, options.maxNumberOfResultsToRetrieve)
        new SearchResults(
                options,
                topDocs.totalHits,
                topDocs.scoreDocs.collect { ScoreDoc sc -> createResult(searcher.doc(sc.doc)) })
    }

    private SearchResult createResult(Document document) {
        new SearchResult(new URI(document.get('uri')))
    }

    private void createSearchIndex() {
        luceneFactory.withIndexWriterForCreationOn(searchIndexDirectory) { IndexWriter idxWriter ->
            docRoot.eachFileMatch(FileType.FILES, fileNamePattern) { File file ->
                idxWriter.addDocument(luceneDocumentFor(file.toURI()))
            }
        }
    }

    private Document luceneDocumentFor(URI uri) {
        Document doc = new Document()
        doc.add(new Field('contents', uri.toURL().newReader()))
        doc.add(uriFieldFor(uri))
        doc
    }

    Field uriFieldFor(URI uri) {
        def uriField = new Field('uri',
                docRootURI.relativize(uri).toString(),
                Store.YES,
                Index.NOT_ANALYZED_NO_NORMS)
        uriField.indexOptions = IndexOptions.DOCS_ONLY
        uriField
    }
}
