package microwiki.config.dsl

import microwiki.config.SearchConfig

abstract class ConfigScript extends Script {
    void server(Closure closure) {
        binding.builder.server.with closure
    }

    void templates(Closure closure) {
        binding.builder.templates.with closure
    }

    void search(Closure closure) {
        binding.builder.search.with closure
    }
}
