package microwiki.search

enum NullPageSearchStrategy implements PageSearchStrategy {
    INSTANCE;

    @Override
    SearchResults search(String text) {
        return SearchResults.empty()
    }

    @Override
    boolean isSearchSupported() {
        return false
    }
}
