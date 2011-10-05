package microwiki.storage

abstract class PageStorageException extends RuntimeException {
    private final String pageName

    PageStorageException(String pageName, String message) {
        this(pageName, message, null)
    }

    PageStorageException(String pageName, String message, Throwable cause) {
        super(message, cause)
        this.pageName = pageName
    }

    String getPageName() {
        return pageName
    }
}
