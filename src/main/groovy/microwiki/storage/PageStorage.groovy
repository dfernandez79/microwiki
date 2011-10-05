package microwiki.storage

import microwiki.page.Page

public interface PageStorage {
    Page pageNamed(String pageName) throws PageNotFoundException

    void removePageNamed(String pageName) throws PageNotFoundException, RemovePageException

    void savePage(String pageName, String contents) throws SavePageException
}