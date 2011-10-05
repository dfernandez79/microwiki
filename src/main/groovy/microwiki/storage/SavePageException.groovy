package microwiki.storage

class SavePageException extends PageStorageException {
     SavePageException(String pageName, Throwable cause) {
        super(pageName, "The page '$pageName' cannot be saved" , cause)
    }
}
