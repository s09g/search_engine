import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by zhangshiqiu on 2017/3/11.
 */
public class InvertedIndex {

    public static class InvertedIndexMapper extends Mapper<Object, Text, Text, Text> {
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

    public static class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
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

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: Inverted Index <inout path> <output path>");
            System.exit(-1);
        }

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        job.setJarByClass(InvertedIndex.class);
        job.setJobName("Inverted Index");

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(InvertedIndexMapper.class);
        job.setReducerClass(InvertedIndexReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.waitForCompletion(true);
    }
}