package microwiki.search.lucene

import microwiki.pages.Page
import microwiki.pages.PageProvider
import microwiki.search.PageSearchStrategy
import microwiki.search.SearchResult
import microwiki.search.SearchResults
import microwiki.search.SearchResultsDisplayOptions
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.Field.Index
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.Field.TermVector
import org.apache.lucene.index.FieldInfo.IndexOptions
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.Term
import org.apache.lucene.index.TermPositionVector
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import org.apache.lucene.search.highlight.*

class LuceneSearchStrategy implements PageSearchStrategy {
    private final Directory searchIndexDirectory
    private final LuceneFactory luceneFactory

    final boolean searchSupported = true

    LuceneSearchStrategy() {
        this(new RAMDirectory())
    }

    LuceneSearchStrategy(Directory searchIndexDirectory) {
        this(searchIndexDirectory, new LuceneFactory(Version.LUCENE_34))
    }

    LuceneSearchStrategy(Directory searchIndexDirectory, LuceneFactory luceneFactory) {
        this.searchIndexDirectory = searchIndexDirectory
        this.luceneFactory = luceneFactory
    }

    @Override
    SearchResults search(String query, SearchResultsDisplayOptions options) {
        luceneFactory.withReadOnlySearcherOn(searchIndexDirectory) { IndexSearcher searcher ->
            createSearchResults(searcher, query, options)
        }
    }

    SearchResults createSearchResults(IndexSearcher searcher, String searchQuery, SearchResultsDisplayOptions options) {
        Query query = luceneFactory.newParserFor('contents').parse(searchQuery)
        TopDocs topDocs = searcher.search(query, options.maxNumberOfResultsToRetrieve)
        Highlighter highlighter = contentHighlighterFor(query)

        new SearchResults(
                searchQuery,
                options,
                topDocs.totalHits,
                topDocs.scoreDocs.collect { ScoreDoc sc -> createSearchResult(searcher, sc, highlighter) })
    }

    private SearchResult createSearchResult(IndexSearcher searcher, ScoreDoc sc, Highlighter highlighter) {
        final doc = searcher.doc(sc.doc)
        final TermPositionVector termPositionVector = (TermPositionVector) searcher.indexReader.getTermFreqVector(sc.doc, 'contents')
        createResult(doc, resultHighlights(doc.get('contents'), highlighter, termPositionVector))
    }

    private List<String> resultHighlights(String contents, Highlighter highlighter, TermPositionVector termPosVector) {
        Arrays.asList(highlighter.getBestFragments(
                TokenSources.getTokenStream(termPosVector),
                contents, 5))
    }

    private Highlighter contentHighlighterFor(Query query) {
        Scorer scorer = new QueryScorer(query, 'contents')
        Highlighter highlighter = new Highlighter(scorer)
        highlighter.textFragmenter = new SimpleSpanFragmenter(scorer)
        highlighter
    }

    private SearchResult createResult(Document document, List<String> highlights) {
        new SearchResult(new URI(document.get('uri')), highlights)
    }

    private Document luceneDocumentFor(Page page) {
        Document doc = new Document()
        doc.add(contentsFieldFor(page))
        doc.add(uriFieldFor(page.uri))
        doc
    }

    private Field contentsFieldFor(Page page) {
        return new Field('contents',
                page.source.toString(),
                Store.YES,
                Index.ANALYZED,
                TermVector.WITH_POSITIONS_OFFSETS)
    }

    private Field uriFieldFor(URI uri) {
        def uriField = new Field('uri',
                uri.toString(),
                Store.YES,
                Index.NOT_ANALYZED_NO_NORMS)
        uriField.indexOptions = IndexOptions.DOCS_ONLY
        uriField
    }

    @Override
    void creationOf(Page newPage) {
        luceneFactory.withIndexWriterForAppendOn(searchIndexDirectory) { IndexWriter idxWriter ->
            idxWriter.addDocument(luceneDocumentFor(newPage))
        }
    }

    @Override
    void updateOf(Page page) {
        luceneFactory.withIndexWriterForAppendOn(searchIndexDirectory) { IndexWriter idxWriter ->
            idxWriter.updateDocument(new Term('uri', luceneDocumentFor(page).get('uri')), luceneDocumentFor(page))
        }
    }

    @Override
    void removalOfPageIdentifiedBy(URI uri) {
        luceneFactory.withIndexWriterForAppendOn(searchIndexDirectory) { IndexWriter idxWriter ->
            idxWriter.deleteDocuments(new Term('uri', uri.toString()))
        }
    }

    def createSearchIndexWithPagesFrom(PageProvider provider) {
        luceneFactory.withIndexWriterForCreationOn(searchIndexDirectory) { IndexWriter idxWriter ->
            provider.eachPage { page -> idxWriter.addDocument(luceneDocumentFor(page)) }
        }
    }
}
