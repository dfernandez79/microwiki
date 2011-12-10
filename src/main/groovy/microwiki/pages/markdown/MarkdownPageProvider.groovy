package microwiki.pages.markdown

import groovy.io.FileType
import java.util.regex.Pattern
import microwiki.pages.Page
import microwiki.pages.WritablePageProvider

// TODO 10. Finish the listing servlet
// TODO 11. Change fonts
// TODO 12. Add hyphenation
// TODO 13. Add jQuery animation for quickref
// TODO 14. Welcome page
// TODO 15. Document code and design (javadoc / wiki)
// TODO 16. Release 1
// TODO 17. Add UML support
// TODO 18. Add Side Bar support (aka TOC)
// TODO 19. Script to generate PDF from TOC
class MarkdownPageProvider implements WritablePageProvider {
    private static final Pattern FILE_PATTERN = ~/.*\.md/
    private final URI docRootURI

    final String encoding
    final File docRoot

    MarkdownPageProvider(File docRoot, String encoding) {
        this.docRoot = docRoot
        this.docRootURI = docRoot.toURI()
        this.encoding = encoding
    }

    @Override
    <T> T writePage(URI uri, Closure<T> closure) {
        new File(docRootURI.resolve(uri)).withWriter closure
    }

    @Override
    Page pageFor(String relativePath) {
        pageFor(new URI(relativePath))
    }

    @Override
    Page pageFor(URI uri) {
        assertRelativeToDocroot(uri)
        createPage(relativize(uri))
    }

    @Override
    void eachPage(Closure closure) {
        docRoot.eachFileMatch(FileType.FILES, FILE_PATTERN) { File file ->
            closure.call pageFor(relativize(file.toURI()))
        }
    }

    private void assertRelativeToDocroot(URI uri) {
        if (uri.isAbsolute() && docRootURI.relativize(uri).isAbsolute()) {
            throw new IllegalArgumentException("Only URIs relative to $docRootURI are allowed")
        }
    }

    private URI relativize(URI uri) {
        return docRootURI.relativize(uri)
    }


    private Page createPage(URI relativeURI) {
        new MarkdownPage(relativeURI, docRootURI.resolve(relativeURI).toURL(), encoding)
    }

    @Override
    Page newPageSampleFor(URI uri) {
        new MarkdownPage(relativize(uri), getClass().getResource('/microwiki/templates/newpage.md'), 'UTF-8')
    }
}
