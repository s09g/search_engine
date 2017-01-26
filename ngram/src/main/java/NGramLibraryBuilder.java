/**
 * Created by zhangshiqiu on 2017/1/26.
 */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class NGramLibraryBuilder {
    public static class NGramMapper extends Mapper <LongWritable, Text, Text, IntWritable>{
        int noGram;

        @Override
        public void setup(Context context){
            Configuration conf = context.getConfiguration();
            noGram = conf.getInt("noGram", 5);
        }

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString().trim().toLowerCase().replaceAll("[^a-z]", " ");//把非alphabetic的字符替换成空格
            String[] words = line.split("\\s+");
            if (words.length < 2){
                return;
            }

            StringBuilder builder;
             for (int i = 0; i < words.length; i++) {
                builder = new StringBuilder();
                builder.append(words[i]);
                for (int j = 1; i + j < words.length && j < noGram; j++) {
                    builder.append(" ");
                    builder.append(words[j+i]);
                    context.write(new Text(builder.toString()), new IntWritable(1));
                }
            }
        }
    }

    public static class NGramReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reducer(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value: values) {
                sum += value.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }
}
