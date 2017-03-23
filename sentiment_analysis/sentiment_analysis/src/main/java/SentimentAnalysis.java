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

/**
 * Created by zhangshiqiu on 2017/3/22.
 */
public class SentimentAnalysis {
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
