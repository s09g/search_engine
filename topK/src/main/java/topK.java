import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
/**
 * Created by zhangshiqiu on 2017/2/8.
 */
public class topK {
    public static class topKMapper extends Mapper<Object, Document, Text, IntWritable> {
        public void map(Object key, Document value, Context context) throws IOException, InterruptedException {
            for (String word : value.content.split("\\s+")){
                if (word.trim().length() > 0) {
                    return;
                }
                context.write(new Text(word.trim()), new IntWritable(1));
            }
        }
    }

//    public static class topKReducer extends Reducer<Text, IntWritable>
}
