package com.imjustdoom.justdoomapi.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class HtmlUtil {

    public static String convertMarkdownToHtml(String text) {
        text = text.replaceAll("script", "error style=\"display:none;\"");
        Parser parser = Parser.builder().build();
        Node document = parser.parse(text);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
