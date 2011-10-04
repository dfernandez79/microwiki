package microwiki

import org.pegdown.ast.Visitor
import org.pegdown.ast.AbbreviationNode
import org.pegdown.ast.AutoLinkNode
import org.pegdown.ast.BlockQuoteNode
import org.pegdown.ast.BulletListNode
import org.pegdown.ast.CodeNode
import org.pegdown.ast.DefinitionListNode
import org.pegdown.ast.DefinitionNode
import org.pegdown.ast.DefinitionTermNode
import org.pegdown.ast.EmphNode
import org.pegdown.ast.ExpLinkNode
import org.pegdown.ast.HeaderNode
import org.pegdown.ast.HtmlBlockNode
import org.pegdown.ast.InlineHtmlNode
import org.pegdown.ast.ListItemNode
import org.pegdown.ast.MailLinkNode
import org.pegdown.ast.OrderedListNode
import org.pegdown.ast.ParaNode
import org.pegdown.ast.QuotedNode
import org.pegdown.ast.ReferenceNode
import org.pegdown.ast.RefLinkNode
import org.pegdown.ast.RootNode
import org.pegdown.ast.SimpleNode
import org.pegdown.ast.SpecialTextNode
import org.pegdown.ast.StrongNode
import org.pegdown.ast.TableBodyNode
import org.pegdown.ast.TableCellNode
import org.pegdown.ast.TableColumnNode
import org.pegdown.ast.TableHeaderNode
import org.pegdown.ast.TableNode
import org.pegdown.ast.TableRowNode
import org.pegdown.ast.VerbatimNode
import org.pegdown.ast.TextNode
import org.pegdown.ast.SuperNode
import org.pegdown.ast.Node
import org.pegdown.PegDownProcessor

class MarkdownHtmlWriter implements Visitor {
    private PrintWriter out

    static MarkdownHtmlWriter on(Writer out) {
        return new MarkdownHtmlWriter(out.newPrintWriter())
    }

   MarkdownHtmlWriter(PrintWriter out) {
       this.out = out
   }

    @Override
    void visit(AbbreviationNode node) {
        println node
    }

    @Override
    void visit(AutoLinkNode node) {
        println node
    }

    @Override
    void visit(BlockQuoteNode node) {
        println node
    }

    @Override
    void visit(BulletListNode node) {
        println node
    }

    @Override
    void visit(CodeNode node) {
        println node
    }

    @Override
    void visit(DefinitionListNode node) {
        println node
    }

    @Override
    void visit(DefinitionNode node) {
        println node
    }

    @Override
    void visit(DefinitionTermNode node) {
        println node
    }

    @Override
    void visit(EmphNode node) {
        println node
    }

    @Override
    void visit(ExpLinkNode node) {
        println node
    }

    @Override
    void visit(HeaderNode node) {
        println node
    }

    @Override
    void visit(HtmlBlockNode node) {
        println node
    }

    @Override
    void visit(InlineHtmlNode node) {
        println node
    }

    @Override
    void visit(ListItemNode node) {
        println node
    }

    @Override
    void visit(MailLinkNode node) {
        println node
    }

    @Override
    void visit(OrderedListNode node) {
        println node
    }

    @Override
    void visit(ParaNode node) {
        tag('p') { visitChildrenOf(node) }
    }

    private def visitChildrenOf(SuperNode node) {
        node.getChildren()*.accept(this)
    }

    @Override
    void visit(QuotedNode node) {
        println node
    }

    @Override
    void visit(ReferenceNode node) {
        println node
    }

    @Override
    void visit(RefLinkNode node) {
        println node
    }

    @Override
    void visit(RootNode node) {
        visitChildrenOf node
    }

    @Override
    void visit(SimpleNode node) {
        println node
    }

    @Override
    void visit(SpecialTextNode node) {
        println node
    }

    @Override
    void visit(StrongNode node) {
        println node
    }

    @Override
    void visit(TableBodyNode node) {
        println node
    }

    @Override
    void visit(TableCellNode node) {
        println node
    }

    @Override
    void visit(TableColumnNode node) {
        println node
    }

    @Override
    void visit(TableHeaderNode node) {
        println node
    }

    @Override
    void visit(TableNode node) {
        println node
    }

    @Override
    void visit(TableRowNode node) {
        println node
    }

    @Override
    void visit(VerbatimNode node) {
        println node
    }

    @Override
    void visit(TextNode node) {
        out.write node.text
    }

    @Override
    void visit(SuperNode node) {
        visitChildrenOf node
    }

    @Override
    void visit(Node node) {
        println node
    }

    void write(WikiPage page) {
        out.print '<!doctype html><html><body>'
        parse(page).accept(this)
        out.print '</body></html>'
    }

    private void tag(String tagName, Closure block) {
        out.print "<$tagName>"
        block()
        out.print "</$tagName>"
    }

     private  RootNode parse(WikiPage page) {
        return  new PegDownProcessor().parseMarkdown(page.source.toCharArray())
    }
}
