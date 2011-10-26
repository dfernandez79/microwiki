package microwiki.cli

class ConfigurationScriptException extends RuntimeException {
    def ConfigurationScriptException(Throwable cause) {
        super('The configuration script failed to load, see the error cause for details', cause)
    }
}
