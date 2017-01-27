import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

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
        int topK;

        @Override
        public void setup(Context context){
            Configuration configuration = context.getConfiguration();
            topK = configuration.getInt("topK", 5);
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
            TreeMap<Integer, List<String>> treeMap = new TreeMap<>(Collections.<Integer>reverseOrder());

            for (Text text : values){
                String[] texts = text.toString().trim().split("=");
                String word = texts[0].trim();
                int count = Integer.parseInt(texts[1].trim());

                if (!treeMap.containsKey(count)){
                    treeMap.put(count, new ArrayList<String>());
                }
                treeMap.get(count).add(word);
            }

            Iterator<Integer> iterator = treeMap.keySet().iterator();
            for (int j = 0; iterator.hasNext() && j < topK; ){
                int keyCount = iterator.next();
                List<String> words = treeMap.get(keyCount);
                for (String following_word: words){
                    context.write(new DBOutputWritable(key.toString(), following_word, keyCount),
                                                        NullWritable.get());
                    j++;
                }
            }
        }
    }
}
