package microwiki.storage

class PageNotFoundException extends PageStorageException {
    PageNotFoundException(String pageName, Throwable cause) {
        super(pageName, "Page '$pageName' not found" , cause)
    }
}
