import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangshiqiu on 2017/2/3.
 */
public class Controller {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "data/crawl";
        int numberOfCrawlers = 1;
//        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        config.setPolitenessDelay(200);
        config.setMaxDepthOfCrawling(16);
        config.setMaxPagesToFetch(2000);

        config.setIncludeBinaryContentInCrawling(true);
        config.setResumableCrawling(false);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        String urlForLA_Times = "http://www.latimes.com/";
        controller.addSeed(urlForLA_Times);
        controller.start(MyCrawler.class, numberOfCrawlers);
    }
}
