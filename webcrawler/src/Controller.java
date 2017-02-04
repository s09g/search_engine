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
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "data/crawl";
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        String seedUrl = "http://www.viterbi.usc.edu/";
        controller.addSeed(seedUrl);
        controller.start(MyCrawler.class, numberOfCrawlers);
    }
}
