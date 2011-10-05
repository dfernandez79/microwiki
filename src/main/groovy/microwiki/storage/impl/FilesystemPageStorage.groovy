package microwiki.storage.impl

import microwiki.page.Page
import microwiki.page.markdown.MarkdownPage
import microwiki.storage.PageNotFoundException
import microwiki.storage.PageStorage
import microwiki.storage.RemovePageException
import microwiki.storage.SavePageException

class FilesystemPageStorage implements PageStorage {
    private File root

    FilesystemPageStorage(File root) {
        assertValidRoot(root)
        this.root = root;
    }

    private static void assertValidRoot(File file) {
        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalArgumentException("The file $file is not a directory")
        }
    }

    @Override
    Page pageNamed(String pageName) throws PageNotFoundException {
        try {
            return new MarkdownPage(fileFor(pageName).text)
        }  catch (FileNotFoundException e) {
            throw new PageNotFoundException(pageName, e)
        }
    }

    private File fileFor(String pageName) {
        return new SimplePageName(pageName).toFile(root,  'md')
    }

    void removePageNamed(String pageName) throws PageNotFoundException, RemovePageException {
        File file = fileFor(pageName)
        if (!file.exists()) {
            throw new PageNotFoundException(pageName, new FileNotFoundException(file.absolutePath))
        }
        if (!file.delete()) {
            throw new RemovePageException(pageName)
        }
    }

    void savePage(String pageName, String contents) throws SavePageException {
        try {
            fileFor(pageName).text = contents
        } catch (IOException e) {
            throw new SavePageException(pageName, e)
        }
    }
}
