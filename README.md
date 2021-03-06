Java ArticleParser 
========
Java ArticleParser is a java library to extract the primary content of a webpage. It is a Java port of the arc90's readability project.

Dependency
--------
Java ArticleParser uses jsoup (https://github.com/jhy/jsoup) to parse html.

Example
--------
```java
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.articleparser.Article;
import org.articleparser.ArticleParser;

public class HelloArticleParser {
    public static void main(String[] args) {
        try {
            ArticleParser parser = new ArticleParser("http://en.wikipedia.org/wiki/Github");
            Article article = parser.parse();
            String articleHTML = article.getHTML();
            BufferedWriter out = new BufferedWriter(new FileWriter("parsed.html"));
            try {
                out.write(articleHTML);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
