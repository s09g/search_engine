import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class NGramLibraryBuilder {



    public static class NGramMapper extends Mapper <LongWritable, Text, Text, IntWritable>{
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
        //input : read sentences
        //   I love big     n = 3
        //  I love --> 1
        //    love big  --> 1
        // I love big  --> 1
        }
    }

    public static class NGramReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reducer(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {

        }
    }
}
