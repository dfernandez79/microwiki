package microwiki.storage

class RemovePageException extends PageStorageException {
    RemovePageException(String pageName) {
        super(pageName, "The page '$pageName' cannot be removed")
    }
}
