package microwiki.storage.impl

import microwiki.storage.PageStorageException

class InvalidPageNameException extends PageStorageException {
    def InvalidPageNameException(String pageName, String message) {
        super(pageName, message)
    }
}
