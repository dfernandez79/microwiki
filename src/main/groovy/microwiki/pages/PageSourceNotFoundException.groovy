package microwiki.pages

class PageSourceNotFoundException extends RuntimeException {
    def PageSourceNotFoundException(Throwable cause) {
        super(cause)
    }

    def PageSourceNotFoundException() {
        super()
    }

    def PageSourceNotFoundException(String message) {
        super(message)
    }

    def PageSourceNotFoundException(String message, Throwable cause) {
        super(message, cause)
    }
}
