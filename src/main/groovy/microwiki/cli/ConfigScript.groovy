package microwiki.cli

abstract class ConfigScript extends Script {
    void server(Closure closure) {
        binding.builder.server.with closure
    }

    void templates(Closure closure) {
        binding.builder.templates.with closure
    }
}
