import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangshiqiu on 2017/3/12.
 */
public class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        for (Text value : values) {
            String docID = value.toString().trim();
            int times = map.containsKey(docID) ? map.get(docID) : 0;
            map.put(docID, times + 1);
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String docID = entry.getKey();
            int times = entry.getValue();
            builder.append(docID).append(':').append(times).append("\t");
        }

        context.write(key, new Text(builder.toString().trim()));
    }
}