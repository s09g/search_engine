import java.io.*;

import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.SAXException;

/**
 * Created by zhangshiqiu on 2017/4/25.
 */

public class ContentExtraction {
    private static Writer writer;
    public static void main(final String[] args) throws Exception {
        String webPageDirectoryPath = "/Users/zhangshiqiu/572/crawelData/";
        String contentFileDirectoryPath = "/Users/zhangshiqiu/572/contentFiles/";
        File webPageDirecrory = new File(webPageDirectoryPath);

        for(File webpage : webPageDirecrory.listFiles()){
            String contentFile = webpage.getName().replace(".html",".txt");
            writer = new BufferedWriter(new FileWriter(contentFileDirectoryPath + contentFile));
            parse(webpage);
            writer.flush();
            writer.close();
        }
    }

    private static void parse(File webpage) throws IOException,SAXException, TikaException{
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(webpage);
        ParseContext pcontext = new ParseContext();

        HtmlParser htmlparser = new HtmlParser();
        htmlparser.parse(inputstream, handler, metadata, pcontext);

        LanguageIdentifier identifier = new LanguageIdentifier(handler.toString());
        if (identifier.getLanguage().equals("en")){
            String content = handler.toString().replaceAll("\\s+"," ");

            writer.write(content);
        }
    }
}

