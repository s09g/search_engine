import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException{
        String inputDir = args[0];
        String nGramLib = args[1];
        String numberOfNGram = args[2];
        String threshold = args[3];
        String numberOfFollowingWords = args[4];


        // job1
        Configuration conf1 = new Configuration();
        conf1.set("textinputformat.record.delimiter", ".");
        conf1.set("noGram", numberOfNGram);


//        Job job1 = Job.getInstance()
    }
}
