import java.io.*;

import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
/**
 * Created by zhangshiqiu on 2017/4/17.
 */
import org.xml.sax.SAXException;

public class HtmlParse {
    private static Writer writer;
    public static void main(final String[] args) throws Exception {
        String webPageDirectoryPath = "/Users/zhangshiqiu/572/crawelData/";
        File bigTxt = new File("/Users/zhangshiqiu/572/big.txt");
        writer = new FileWriter(bigTxt);

        File webPageDirecrory = new File(webPageDirectoryPath);
        for(File webpage : webPageDirecrory.listFiles()){
            parse(webpage);
        }

        writer.close();
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
            String words = handler.toString().replaceAll("\\s+|[^A-Za-z]+"," ");
            writer.append(words).append(" ");
        }
    }
}
