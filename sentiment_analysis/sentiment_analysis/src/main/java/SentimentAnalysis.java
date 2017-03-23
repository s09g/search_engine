import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by zhangshiqiu on 2017/3/22.
 */
public class SentimentAnalysis {
    public static class ArticleSplit extends Mapper<Object, Text, Text, IntWritable> {
        private HashMap<String, String> emotinDict = new HashMap<>();

        @Override
        public void setup(Context context) throws IOException {
            Configuration configuration = new Configuration();
            String dict = configuration.get("dict", "");

            BufferedReader reader = new BufferedReader((new FileReader(dict)));
            String line = reader.readLine();

            while (line != null) {
                String[] word_emotion = line.split("\t");
                emotinDict.put(word_emotion[0].trim().toLowerCase(), word_emotion[1].trim());
                line = reader.readLine();
            }
            reader.close();
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
            String line = value.toString().trim();
            StringTokenizer tokenizer = new StringTokenizer(line);
            StringBuilder builder = new StringBuilder();
            Text outputKey = new Text();
            IntWritable one = new IntWritable(1);

            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken().trim().toLowerCase();
                if (emotinDict.containsKey(word)){
                    builder.append(fileName).append("\t").append(word);
                    outputKey.set(builder.toString());
                    context.write(outputKey, one);
                    builder.setLength(0);
                }
            }
        }
    }

    public static class SentimentContentCollection extends Reducer<Text, IntWritable, Text, IntWritable>{
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value: values) {
                sum += value.get();
            }

            context.write(key, new IntWritable(sum));
        }
    }


    // args[0] : input path
    // args[1] : output path
    // args[2] : path of emotion dictionary.
    //           In this case is emotionCategory.txt, which is powered by the instructor.
    public static void main(String[] args) throws Exception{
        Configuration configuration = new Configuration();

        // args[2] : path of emotion dictionary.
        configuration.set("dict", args[2]);

        Job job = Job.getInstance(configuration);
        job.setJarByClass(SentimentAnalysis.class);

        job.setMapperClass(ArticleSplit.class);
        job.setReducerClass(SentimentContentCollection.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // args[0] : input path
        // args[1] : output path
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}
