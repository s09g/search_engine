import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.lib.db.DBConfiguration;
import org.apache.hadoop.mapred.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException{
        // inputDir outputDir
        // noGram threshold topK
        String inputDir = args[0];
        String outputDir = args[1];
        String noGram = args[2];
        String threshold = args[3];
        String topK = args[4];


        // job1
        Configuration configuration1 = new Configuration();
        configuration1.set("textinputformat.record.delimiter", ".");
        configuration1.set("noGram", noGram);

        Job job1 = Job.getInstance();
        job1.setJobName("NGram");
        job1.setJarByClass(Driver.class);

        job1.setMapperClass(NGramLibraryBuilder.NGramMapper.class);
        job1.setReducerClass(NGramLibraryBuilder.NGramReducer.class);

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);

        job1.setInputFormatClass(TextInputFormat.class);
        job1.setOutputFormatClass(TextOutputFormat.class);

        TextInputFormat.setInputPaths(job1, new Path(inputDir));
        TextOutputFormat.setOutputPath(job1, new Path(outputDir));


        job1.waitForCompletion(true);

        //job2
        Configuration configuration2 = new Configuration();
        configuration2.set("threshold", threshold);
        configuration2.set("topK", topK);


        DBConfiguration.configureDB(configuration2,
                "com.mysql.jdbc.Drive",
                "jdbc:mysql://192.168.0.112:8889/test",
                "root",
                "root");

        Job job2 = Job.getInstance();

        job2.setJobName("LanguageModel");
        job2.setJarByClass(Driver.class);

        job2.setMapperClass(LanguageModel.LanguageModelMapper.class);
        job2.setReducerClass(LanguageModel.LanguageModelReducer.class);

        job2.addArchiveToClassPath(new Path("/mysql/mysql-connector-java-5.1.39-bin.jar"));

        // Mapper的output Key, Value 与 Reducer 的 Key, Value 不吻合
        //设置 Mapper的output Key, Value
        job2.setMapOutputKeyClass(Text.class);
        job2.setMapOutputKeyClass(Text.class);
        //默认  设置 reducer的 K,V
        job2.setOutputKeyClass(DBOutputWritable.class);
        job2.setOutputValueClass(NullWritable.class);

        job2.setInputFormatClass(TextInputFormat.class);
        job2.setOutputFormatClass(DBOutputFormat.class);

        DBOutputFormat.setOutput(job2,
                "output",
                new String[] {"starting_phrase", "following_word", "count"});

        TextInputFormat.setInputPaths(job2, new Path(outputDir));


        job2.waitForCompletion(true);
    }
}
