package microwiki.storage.impl

import microwiki.storage.PageStorage
import microwiki.storage.PageNotFoundException
import microwiki.storage.RemovePageException
import microwiki.storage.SavePageException
import microwiki.WikiPage

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
    WikiPage pageAt(String path) throws PageNotFoundException {
        try {
            def file = fileFor(path)
            return new WikiPage(file.toURI(), file.text)
        }  catch (FileNotFoundException e) {
            throw new PageNotFoundException(path, e)
        }
    }

    private File fileFor(String path) {
        return new File(root, path + '.md')
    }

    void removePageAt(String path) throws PageNotFoundException, RemovePageException {
        File file = fileFor(path)
        if (!file.exists()) {
            throw new PageNotFoundException(path, new FileNotFoundException(file.absolutePath))
        }
        if (!file.delete()) {
            throw new RemovePageException()
        }
    }

    void savePage(String path, String contents) throws SavePageException {
        fileFor(path).text = contents
    }
}
