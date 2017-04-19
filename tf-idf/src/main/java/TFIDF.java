import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangshiqiu on 2017/4/18.
 */
public class TFIDF {
    private HashMap<String, Document> corpus;
    public TFIDF(){
        corpus = new HashMap<>();
    }

    private boolean contains(String docName){
        return corpus.containsKey(docName);
    }

    public void addHTMLPage(File webpage) throws Exception{
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(webpage);
        ParseContext pcontext = new ParseContext();

        HtmlParser htmlparser = new HtmlParser();
        htmlparser.parse(inputstream, handler, metadata, pcontext);
        this.addDocument(metadata.get("title"), handler.toString());
    }

    public void addDocument(String docName, String content){
        Document document = new Document(docName, content);
        corpus.put(docName, document);
    }

    public HashMap<String, Double> tfidfSimilarity(String[] words){
        HashMap<String, Double> tfidfSim = new HashMap<>();
        for (String docName : corpus.keySet()){
            tfidfSim.put(docName, 0.0);
        }

        for (String word: words){
            tf_idf(word, tfidfSim);
        }
        return tfidfSim;
    }


    private void tf_idf(String word, HashMap<String, Double> tfidfSim){
        int df = 1;
        for (Map.Entry<String, Document> entry : corpus.entrySet()) {
            Document doc = entry.getValue();
            if (doc.contains(word)){
                df += 1;
            }
        }

        double idf = Math.log(corpus.size() * 1.0 / df);

        for (Map.Entry<String, Document> entry : corpus.entrySet()) {
            String docName = entry.getKey();
            Document doc = entry.getValue();
            double tf = doc.count(word) * 1.0 / doc.size();

            double tfidfSum = tfidfSim.get(docName) + tf * idf;
            tfidfSim.put(docName, tfidfSum);
        }
    }
}
