package microwiki.cli

import groovy.transform.Immutable

@Immutable
class SearchConfig {
    boolean enabled

    boolean isDisabled() {
        !enabled
    }

    static SearchConfig getDefault() {
        new SearchConfig(true)
    }
}
