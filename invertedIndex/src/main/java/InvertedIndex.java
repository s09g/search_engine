import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
 * Created by zhangshiqiu on 2017/2/7.
 */
public class InvertedIndex {
    public static class DocumentMapper extends Mapper<Object, Document, Text, IntWritable> {
        @Override
        public void map (Object key, Document value, Context context) throws IOException, InterruptedException{
            int id = value.id;
            String[] words = value.content.trim().split("\\s+");
            for (String word : words){
                context.write(new Text(word), new IntWritable(id));
            }
        }
    }

    public static class IndexReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
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

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        job.setJarByClass(InvertedIndex.class);
        job.setMapperClass(DocumentMapper.class);
        job.setReducerClass(IndexReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}
