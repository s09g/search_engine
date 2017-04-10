import com.opencsv.CSVReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by zhangshiqiu on 2017/4/6.
 */
public class ExtractLinks {
    public static void main(String[] args) throws Exception {
        String mapFilePath = "/Users/zhangshiqiu/572/mapLATimesDataFile.csv";
        File mapFile = new File(mapFilePath);
        CSVReader csvReader = new CSVReader(new FileReader(mapFile));
        HashMap<String, String> map = new HashMap<String, String>();
        String [] line;
        while ((line = csvReader.readNext()) != null) {
            map.put(line[1], line[0]);
        }

        String webPageDirectoryPath = "/Users/zhangshiqiu/572/crawelData/";
        File edgeFile = new File("/Users/zhangshiqiu/572/edgeList.txt");
        Writer writer = new FileWriter(edgeFile);
        File webPageDirecrory = new File(webPageDirectoryPath);
        StringBuilder builder = new StringBuilder();

        for(File webpage : webPageDirecrory.listFiles()){
            String src = webpage.toString();
            Document doc = Jsoup.parse(webpage, "UTF-8", "http://www.latimes.com/");
            Elements links = doc.select("a[href]");
            for (Element link : links){
                String dest = link.attr("href").trim();

                if (!map.containsKey(dest)){
                    continue;
                }

                dest = map.get(dest);

                builder.setLength(0);
                builder.append(src)
                        .append(' ')
                        .append(webPageDirectoryPath)
                        .append(dest)
                        .append('\n');
                writer.write(builder.toString());
            }
        }
    }
}
