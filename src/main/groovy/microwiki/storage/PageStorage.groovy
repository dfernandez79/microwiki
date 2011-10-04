package microwiki.storage

import microwiki.WikiPage

public interface PageStorage {
    WikiPage pageAt(String path) throws PageNotFoundException

    void removePageAt(String path) throws PageNotFoundException, RemovePageException

    void savePage(String path, String contents) throws SavePageException
}