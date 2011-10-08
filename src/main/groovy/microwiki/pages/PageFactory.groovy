package microwiki.pages

import microwiki.pages.Page

interface PageFactory {
    Page createPage(URL url)
}
