package microwiki.search

import microwiki.pages.PageProvider

interface PageSearchStrategyFactory {
    PageSearchStrategy createFor(PageProvider provider)
}
