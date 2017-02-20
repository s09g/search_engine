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
    static Writer fetch, visit, urls;
    static String domain ="http://www.latimes.com/";
    private final static Pattern filters = Pattern.compile(
            ".*(\\.(txt|css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg" +
                    "|ram|m4v|rm|smil|wmv|swf|wma|zip|rar|gz|xml))$");

    private static final Pattern requirePatterns = Pattern.compile(
            ".*(\\.(html|doc|pdf|bmp|gif|jpe?g|png|tiff?))$");

    static Builder builder1, builder2, builder3;


    synchronized static Writer getFetch(){
        if (fetch == null){
            try {
                fetch = new Writer(new PrintWriter(new File("./data/fetch_LATimes.csv")));
            } catch (Exception e){

            }
        }
        return fetch;
    }

    synchronized static Writer getVisit(){
        if (visit == null){
            try {
                visit = new Writer(new PrintWriter(new File("./data/visit_LATimes.csv")));
            } catch (Exception e){

            }
        }
        return visit;
    }

    synchronized static Writer getUrls(){
        if (urls == null){
            try {
                urls = new Writer(new PrintWriter(new File("./data/urls_LATimes.csv")));
            } catch (Exception e){

            }
        }
        return urls;
    }

    synchronized static Builder get1(){
        if (builder1 == null){
            try {
                builder1 = new Builder(new StringBuffer());
            } catch (Exception e){

            }
        }
        return builder1;
    }

    synchronized static Builder get2(){
        if (builder2 == null){
            try {
                builder2 = new Builder(new StringBuffer());
            } catch (Exception e){

            }
        }
        return builder2;
    }
    synchronized static Builder get3(){
        if (builder3 == null){
            try {
                builder3 = new Builder(new StringBuffer());
            } catch (Exception e){

            }
        }
        return builder3;
    }

    public void onStart(){
        fetch = MyCrawler.getFetch();
        visit = MyCrawler.getVisit();
        urls = MyCrawler.getUrls();
        builder1 = MyCrawler.get1();
        builder2 = MyCrawler.get2();
        builder3 = MyCrawler.get3();
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

        String contentType = referringPage.getContentType();
        if (contentType.contains("text/html")
                || contentType.contains("image")
                || contentType.contains("application/pdf")){
            return true;
        }

        return requirePatterns.matcher(href).matches();
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
