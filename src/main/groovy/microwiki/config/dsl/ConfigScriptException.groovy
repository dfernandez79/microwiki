package microwiki.config.dsl

class ConfigScriptException extends RuntimeException {
    def ConfigScriptException(Throwable cause) {
        super('The configuration script failed to load, see the error cause for details', cause)
    }
}
