import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhangshiqiu on 2017/3/5.
 */
public class IndexReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
        Set<Integer> set = new HashSet<Integer>();
        for (IntWritable value : values) {
            int val = value.get();
            if (set.contains(value.get())){
                continue;
            }
            set.add(val);
            context.write(key, value);
        }
    }
}