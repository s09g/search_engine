import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by zhangshiqiu on 2017/3/12.
 */
public class InvertedIndexMapper extends Mapper<Object, Text, Text, Text> {
    private Text word = new Text();
    @Override
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        String[] id_content = value.toString().trim().split("\t");
        Text docID = new Text(id_content[0]);
        StringTokenizer tokenizer = new StringTokenizer(id_content[1]);


        while (tokenizer.hasMoreTokens()) {
            word.set(tokenizer.nextToken());
            context.write(word, docID);
        }
    }
}