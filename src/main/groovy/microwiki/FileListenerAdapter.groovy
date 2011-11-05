package microwiki

import microwiki.pages.PageChangeListener
import org.apache.commons.vfs2.FileChangeEvent
import org.apache.commons.vfs2.FileListener
import microwiki.pages.markdown.MarkdownPageProvider

class FileListenerAdapter implements FileListener {
    private final PageChangeListener pageChangeListener

    FileListenerAdapter(PageChangeListener pageChangeListener) {
        this.pageChangeListener = pageChangeListener
    }

    @Override
    void fileCreated(FileChangeEvent event) {
        pageChangeListener.creationOfPageIdentifiedBy(event.file.name.URI.toURI())
    }

    @Override
    void fileDeleted(FileChangeEvent event) {
        pageChangeListener.removalOfPageIdentifiedBy(event.file.name.URI.toURI())
    }

    @Override
    void fileChanged(FileChangeEvent event) {
        pageChangeListener.updateOfPageIdentifiedBy(event.file.name.URI.toURI())
    }
}
