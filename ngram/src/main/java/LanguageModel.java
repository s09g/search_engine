import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by zhangshiqiu on 2017/1/26.
 */
public class LanguageModel {
    public static class LanguageModelMapper extends Mapper<LongWritable, Text, Text, Text>{
        int threshold;
        @Override
        public void setup(Context context){
            Configuration configuration = context.getConfiguration();
            threshold = configuration.getInt("threshold", 20);
        }


        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
            String line = value.toString().trim();

            String[] wordsCount = line.split("\t");
            if (wordsCount.length < 2){
                return;
            }

            int count = Integer.parseInt(wordsCount[1]);

            if (count < threshold){
                return;
            }


            String[] words = wordsCount[0].split("\\s+");
            int len = words.length;

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < len - 1; i++){
                builder.append(words[i]);
                builder.append(" ");
            }
            String starting_phrase = builder.toString().trim();
            String following_word = words[len - 1];

            String outputKey = starting_phrase;
            String outputValue = following_word + "=" + count;

            context.write(new Text(outputKey), new Text(outputValue));
        }
    }

    public static class LanguageModelReducer extends Reducer<Text, Text, DBOutputWritable, NullWritable>{
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{

        }
    }
}
