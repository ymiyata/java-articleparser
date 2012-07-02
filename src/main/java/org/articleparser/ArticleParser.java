/*
 * Java port of Arc90's readability code
 */

package org.articleparser;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArticleParser {

    private static final Pattern NEGATIVE_REGEX =
        Pattern.compile(
            "combx|comment|contact|foot|footer|footnote|link|media|meta|promo|related|scroll|" + 
            "shoutbox|sponsor|tags|widget",
            Pattern.CASE_INSENSITIVE
        );

    private static final Pattern POSITIVE_REGEX =
        Pattern.compile(
            "article|body|content|entry|hentry|page|pagination|post|text",
            Pattern.CASE_INSENSITIVE
        );

    private String url;
    private Document soup;
    private Article article;

    public ArticleParser(String url) throws IOException {
        this.url = url;
        try {
            soup = Jsoup.connect(url).get();
        } catch(IOException e) {
            e.fillInStackTrace();
            throw e;
        }
    }

    public String getUrl() {
        return url;
    }

    public Article parse() {
        if (this.article == null) {
            this.article = parse(false);
            return this.article;
        } else {
            return this.article;
        }
    }

    private Article parse(boolean preserveUnlikelyElement) {
        String header = parseHeader();
        RawDocument document = new RawDocument(soup);
        document.clean(preserveUnlikelyElement);
        
        Elements paragraphs = document.getDocument().select("p");
        HashMap<Element, Integer> candidates = new HashMap<Element, Integer>();
        
        for (Element p : paragraphs) {
            Element parent = p.parent();
            if (p.text().length() < 30) continue;
            
            int score = 0;
            if (parent != null) {
                if (parent.hasAttr("class")) {
                    if (regexMatch(NEGATIVE_REGEX, parent.className())) score -= 30;
                    if (regexMatch(POSITIVE_REGEX, parent.className())) score += 30;
                }
                if (parent.hasAttr("id")) {
                    if (regexMatch(NEGATIVE_REGEX, parent.id())) score -= 30;
                    if (regexMatch(POSITIVE_REGEX, parent.id())) score += 30;
                }
                score += Math.min(Math.floor(p.text().length()/50), 6);
            }
            Integer parentScore = (Integer)candidates.get(parent);
            candidates.put(parent, 
                    new Integer((parentScore == null) ? score : score + parentScore.intValue()));
        }
        
        int bestScore = 0;
        Element bestElement = null;
        for (Map.Entry<Element, Integer> candidate : candidates.entrySet()) {
            if (bestElement == null || bestScore < candidate.getValue()) {
                bestScore = candidate.getValue();
                bestElement = candidate.getKey();
            }
        }
        
        Article article = createArticle(header, bestElement);
        if (article.getSnippet().length() == 0) {
            // Attempt again, but this time preserve unlikely candidate
            // If that fails use body as the last resort
            if (!preserveUnlikelyElement) {
                article = parse(true);
            } else {
                Elements body = document.getDocument().select("body");
                if (body.size() > 0)
                    article = createArticle(header, body.get(0));
            }
        }
        return article;
    }

    private String parseHeader() {
        Elements headerCandidate = soup.select("h2");
        if (headerCandidate.size() == 1) {
            return headerCandidate.get(0).text();
        }
        headerCandidate = soup.select("h1");
        if (!headerCandidate.isEmpty()) {
            return headerCandidate.get(0).text();
        }
        headerCandidate = soup.select("title");
        if (!headerCandidate.isEmpty()) {
            return headerCandidate.get(0).text();
        }
        return "";
    }

    private Article createArticle(String title, Element element) {
        String article = "";
        String articleSnippet = "";
        String imageSrc = "";
        if (element != null) {
            article = element.html();
            articleSnippet = element.text();
            Elements imageTags = element.select("img");
            if (!imageTags.isEmpty()) {
                imageSrc = imageTags.get(0).attr("src");
            }
        }
        return new Article(title, this.getUrl(), articleSnippet, article,
                null, "", imageSrc, "");
 
    }

    private boolean regexMatch(Pattern regex, String target) {
        return regex.matcher(target).find();
    }
}
