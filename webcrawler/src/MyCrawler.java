import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;
import java.util.regex.Pattern;
/**
 * Created by zhangshiqiu on 2017/2/3.
 */
public class MyCrawler extends WebCrawler {
    private PrintWriter fetch, visit, urls;
    private String domain ="http://www.latimes.com/";
    private final static Pattern filters = Pattern.compile(
            ".*(\\.(txt|css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg" +
                    "|ram|m4v|rm|smil|wmv|swf|wma|zip|rar|gz|xml))$");

    private static final Pattern requirePatterns = Pattern.compile(
            ".*(\\.(html|doc|pdf|bmp|gif|jpe?g|png|tiff?))$");

    StringBuffer builder1, builder2, builder3;
    @Override
    public void onStart(){
        try {
            fetch = new PrintWriter(new File("./data/fetch_LATimes.csv"));
            visit = new PrintWriter(new File("./data/visit_LATimes.csv"));
            urls = new PrintWriter(new File("./data/urls_LATimes.csv"));
            builder1 = new StringBuffer();
            builder2 = new StringBuffer();
            builder3 = new StringBuffer();

            builder3.append("\"").append(domain).append("\"").append(",")
                    .append("OK").append("\n");
            urls.write(builder3.toString());
            builder3.delete(0, builder3.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeforeExit(){
        fetch.close();
        visit.close();
        urls.close();
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (!href.startsWith(domain) || filters.matcher(href).matches()){
            return false;
        }

        return requirePatterns.matcher(href).matches() ||
                referringPage.getContentType().contains("text/html");
    }

    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        String url = webUrl.getURL();
        task1(url, statusCode);
    }

    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            task2(page);
        }
    }

    private void task1(String url, int statusCode) {
        builder1.append("\"").append(url).append("\"").append(",")
                .append(statusCode).append("\n");
        fetch.write(builder1.toString());
        builder1.setLength(0);
    }
    private void task2(Page page) {
        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        String url = page.getWebURL().getURL();
        int size = page.getContentData().length;
        String contentType = page.getContentType();

        Set<WebURL> links = htmlParseData.getOutgoingUrls();
        int numOfOutlinks = links.size();

        builder2.append("\"").append(url).append("\"").append(",")
                .append(size).append(",")
                .append(numOfOutlinks).append(",")
                .append("\"").append(contentType).append("\"").append("\n");
        visit.write(builder2.toString());
        builder2.setLength(0);
        task3(links);
    }
    private void task3(Set<WebURL> links) {
        for (WebURL link : links) {
            String url = link.getURL();
            String indicator = url.startsWith(domain) ? "OK" : "N_OK";
            builder3.append("\"").append(url).append("\"").append(",")
                    .append(indicator).append("\n");
            urls.write(builder3.toString());
            builder3.setLength(0);
        }
    }
}
