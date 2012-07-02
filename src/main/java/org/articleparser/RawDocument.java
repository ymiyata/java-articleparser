package org.articleparser;

import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

class RawDocument { 
    private static final Pattern UNLIKELY_REGEX =
        Pattern.compile(
            "combx|comment|disqus|foot|header|menu|meta|nav|rss|shoutbox|sidebar|sponsor|profile",
            Pattern.CASE_INSENSITIVE
        );

    private static final Pattern MAYBE_REGEX =
        Pattern.compile("and|article|body|column|main", Pattern.CASE_INSENSITIVE);

    private static final Pattern BODY_MATCH_REGEX = 
        Pattern.compile("body", Pattern.CASE_INSENSITIVE);

    private static final String DIV_CHECK_STRING =
        "a, blockquote, dl, div, img, ol, p, pre, table, ul";

    Document soup;

    public RawDocument(Document soup) {
        this.soup = soup;
    }

    public Document getDocument() {
        return soup;
    }

    public void clean(boolean preserveUnlikelyElement) {
        deleteTags("script, link[href], style, form, object, h1, iframe, li");
        deleteH2Header();
        fixLink();
        if (!preserveUnlikelyElement) {
            deleteUnlikelyElement();
        }
        convertDivToParagragh();
    }

    private boolean isUnlikelyElement(Element element) {
        String classesAndIds = element.className() + element.id();
        return regexMatch(UNLIKELY_REGEX, classesAndIds) &&
            !regexMatch(MAYBE_REGEX, classesAndIds) &&
            !regexMatch(BODY_MATCH_REGEX, element.tagName());
    }

    private void deleteUnlikelyElement() {
        if (soup != null) {
            for (Element element : soup.select("*")) {
                if (isUnlikelyElement(element))
                    element.remove();
            }
        }
    }

    private void deleteH2Header() {
        if (soup != null) {
            Elements h2 = soup.select("h2");
            if (h2.size() == 1)
                h2.remove();
        }
    }

    // Convert all DIV with no nested elements to paragraph
    private void convertDivToParagragh() {
        String nonNestedDivSelector = "div:not(div:has(" + DIV_CHECK_STRING + "))";
        for (Element element : soup.select(nonNestedDivSelector)) {
            // If there is no significant nested element replace with <p> tag
            Element pTag = new Element(Tag.valueOf("p"), soup.baseUri()).html(element.html());
            element.replaceWith(pTag);
        }
    }
    
    // Fix href's and src's URL so that it contains the full path instead of a relative path
    private void fixLink() {
        if (soup != null) {
            String linkSelector = "img[src~=(?i)/[^.]*\\.(png|jpe?g|gif|bmp)], a[href^=/]";
            for (Element link : soup.select(linkSelector)) {
                if (link.hasAttr("src"))  link.attr("src", link.absUrl("src"));
                if (link.hasAttr("href")) link.attr("href", link.absUrl("href"));
            }
        }
    }

    private void deleteTags(String tag) {
        if (soup != null) {
            soup.select(tag).remove();
        }
    }

    private boolean regexMatch(Pattern regex, String target) {
        return regex.matcher(target).find();
    }
}
