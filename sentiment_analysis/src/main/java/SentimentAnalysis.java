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
import org.apache.hadoop.util.hash.Hash;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

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
    public static class ArticleSplit extends Mapper<Object, Text, Text, Text> {
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
            Text filename = new Text(fileName);

            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken().trim().toLowerCase();
                if (emotinDict.containsKey(word)){
                    context.write(filename, new Text(emotinDict.get(word)));
                }
            }
        }
    }

    public static class SentimentContentCollection extends Reducer<Text, Text, Text, Text>{
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            HashMap<String, Integer> map = new HashMap<>();
            for (Text value: values) {
                String emotion = value.toString();
                int count = map.getOrDefault(emotion, 0) + 1;
                map.put(emotion, count);
            }
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, Integer> entry : map.entrySet()){
                String emotion = entry.getKey();
                int count = entry.getValue();
                builder.append(emotion).append("\t").append(count);
                context.write(key, new Text(builder.toString()));
                builder.setLength(0);
            }
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
