package microwiki.storage

class PageNotFoundException extends RuntimeException {
    public final String path
    PageNotFoundException(String path, Throwable cause) {
        super("Page '$path' not found" , cause)
        this.path = path
    }
}
