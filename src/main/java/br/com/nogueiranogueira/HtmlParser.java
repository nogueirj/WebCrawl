package br.com.nogueiranogueira;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HtmlParser {
    public Set<String> getUrls(String url) throws IOException{
        Set<String> links = new HashSet<>();
        Document document = Jsoup.connect(url).data("query", "Java")
                .userAgent("Mozilla")
                //.cookie("auth", "token")
                .timeout(9000)
                .get();

        Elements elements = document.select("a[href]");
        for (Element element : elements){
            links.add(element.attr("href"));
        }
        return links;
    }
}
