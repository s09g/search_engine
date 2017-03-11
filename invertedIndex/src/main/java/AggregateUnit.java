import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by zhangshiqiu on 2017/3/11.
 */
public class AggregateUnit {
    public static class UnitMapper extends Mapper<Object, Text, Text, Text> {
        @Override
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] word_doc_time = value.toString().trim().split("\t");
            String[] word_docID = word_doc_time[0].trim().split(":");
            String word = word_docID[0];
            String docID = word_docID[1];
            String time = word_doc_time[1];
            context.write(new Text(word), new Text(docID + ":" + time));
        }
    }
    public static class UnitReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            StringBuffer buffer = new StringBuffer();
            for (Text value : values) {
                buffer.append(value.toString()).append(" ");
            }
            context.write(key, new Text(buffer.toString().trim()));
        }
    }

}
