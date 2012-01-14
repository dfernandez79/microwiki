package microwiki.search.lucene

import microwiki.pages.Page
import microwiki.pages.PageProvider
import microwiki.search.PageSearchIndex
import microwiki.search.SearchResult
import microwiki.search.SearchResults
import microwiki.search.SearchResultsDisplayOptions
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.Field.Index
import org.apache.lucene.document.Field.Store
import org.apache.lucene.document.Field.TermVector
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.Term
import org.apache.lucene.index.TermPositionVector
import org.apache.lucene.queryParser.standard.QueryParserUtil
import org.apache.lucene.search.highlight.Highlighter
import org.apache.lucene.search.highlight.QueryScorer
import org.apache.lucene.search.highlight.SimpleSpanFragmenter
import org.apache.lucene.search.highlight.TokenSources
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import org.apache.lucene.search.*

class LucenePageSearchIndex implements PageSearchIndex {
    private final Directory searchIndexDirectory
    private final LuceneFactory luceneFactory
    private final PageProvider pageProvider

    LucenePageSearchIndex(PageProvider pageProvider) {
        this(pageProvider, new RAMDirectory())
    }

    LucenePageSearchIndex(PageProvider pageProvider, Directory searchIndexDirectory) {
        this(pageProvider, searchIndexDirectory, new LuceneFactory(Version.LUCENE_35))
    }

    LucenePageSearchIndex(PageProvider pageProvider, Directory searchIndexDirectory, LuceneFactory luceneFactory) {
        this.searchIndexDirectory = searchIndexDirectory
        this.luceneFactory = luceneFactory
        this.pageProvider = pageProvider
        initializeSearchIndex()
    }

    private void initializeSearchIndex() {
        luceneFactory.withIndexWriterForCreationOn(searchIndexDirectory) { IndexWriter idxWriter ->
            pageProvider.eachPage { Page page -> idxWriter.addDocument(luceneDocumentFor(page)) }
        }
    }

    @Override
    SearchResults search(String textToSearch, SearchResultsDisplayOptions options) {
        luceneFactory.withReadOnlySearcherOn(searchIndexDirectory) { IndexSearcher searcher ->
            createSearchResults(searcher, textToSearch, options)
        }
    }

    SearchResults createSearchResults(IndexSearcher searcher, String textToSearch, SearchResultsDisplayOptions options) {
        Query query = QueryParserUtil.parse(
                [textToSearch, "$textToSearch*"] as String[],
                ['contents', 'uri'] as String[],
                [BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD] as BooleanClause.Occur[],
                luceneFactory.newStandardAnalyzer())

        TopDocs topDocs = searcher.search(query, options.maxNumberOfResultsToRetrieve)
        Highlighter highlighter = contentHighlighterFor(query)

        new SearchResults(
                textToSearch,
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
        QueryScorer scorer = new QueryScorer(query, 'contents')
        Highlighter highlighter = new Highlighter(scorer)
        highlighter.textFragmenter = new SimpleSpanFragmenter(scorer)
        highlighter
    }

    private SearchResult createResult(Document document, List<String> highlights) {
        new SearchResult(document.get('title'), new URI(document.get('uri')), highlights)
    }

    private Document luceneDocumentFor(URI uri) {
        luceneDocumentFor(pageProvider.pageFor(uri))
    }

    private Document luceneDocumentFor(Page page) {
        Document doc = new Document()
        doc.add(titleFieldFor(page))
        doc.add(contentsFieldFor(page))
        doc.add(uriFieldFor(page.uri))
        doc
    }

    private Field titleFieldFor(Page page) {
        new Field('title',
                page.title,
                Store.YES,
                Index.NOT_ANALYZED_NO_NORMS)
    }

    private Field contentsFieldFor(Page page) {
        new Field('contents',
                page.source.toString(),
                Store.YES,
                Index.ANALYZED,
                TermVector.WITH_POSITIONS_OFFSETS)
    }

    private Field uriFieldFor(URI uri) {
        new Field('uri',
                uri.toString(),
                Store.YES,
                Index.ANALYZED)
    }

    @Override
    void creationOfPageIdentifiedBy(URI uri) {
        luceneFactory.withIndexWriterForAppendOn(searchIndexDirectory) { IndexWriter idxWriter ->
            idxWriter.addDocument(luceneDocumentFor(uri))
        }
    }

    @Override
    void updateOfPageIdentifiedBy(URI uri) {
        luceneFactory.withIndexWriterForAppendOn(searchIndexDirectory) { IndexWriter idxWriter ->
            Document document = luceneDocumentFor(uri)
            idxWriter.updateDocument(new Term('uri', document.get('uri')), document)
        }
    }

    @Override
    void removalOfPageIdentifiedBy(URI uri) {
        luceneFactory.withIndexWriterForAppendOn(searchIndexDirectory) { IndexWriter idxWriter ->
            idxWriter.deleteDocuments(new Term('uri', uri.toString()))
        }
    }
}
