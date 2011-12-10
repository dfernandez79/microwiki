package microwiki.config

import groovy.transform.Immutable

@Immutable
class SearchConfig {
    public static final Map MEMORY_INDEX_STORAGE = Collections.emptyMap()
    public static final Map DEFAULT_FILESYSTEM_STORAGE = [directory:  new File('.microwiki-index')]

    boolean enabled
    Map indexStorageMethod

    boolean isDisabled() {
        !enabled
    }

    static SearchConfig getDefault() {
        new SearchConfig(true, DEFAULT_FILESYSTEM_STORAGE)
    }
}
