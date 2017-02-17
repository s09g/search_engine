import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class LanguageModel {
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {

		int threashold;

		@Override
		public void setup(Context context) {
			Configuration configuration = context.getConfiguration();
			threashold = configuration.getInt("threashold", 20);
		}


		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			if((value == null) || (value.toString().trim()).length() == 0) {
				return;
			}
			//this is cool\t20
			String line = value.toString().trim();

			String[] wordsPlusCount = line.split("\t");
			if(wordsPlusCount.length < 2) {
				return;
			}

			String[] words = wordsPlusCount[0].split("\\s+");

			int count = Integer.valueOf(wordsPlusCount[1]);
			if (count < threashold){
			    return;
      }

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < words.length - 1; i++) {
			    builder.append(words[i]).append(" ");
      }
			String outputKey = builder.toString().trim();

            //this is --> cool = 20
			builder.delete(0, builder.length());
			builder.append(words[words.length-1]).append("=").append(count);
      String outputValue = builder.toString().trim();

      context.write(new Text(outputKey), new Text(outputValue));
		}
	}

	public static class Reduce extends Reducer<Text, Text, DBOutputWritable, NullWritable> {

		int n;
		@Override
		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			n = conf.getInt("n", 5);
		}

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // key: XXXX
            // value : YY=ZZ
		    TreeMap<Integer, List<String>> treeMap = new TreeMap<Integer, List<String>>(Collections.<Integer>reverseOrder());
            for (Text value : values){
                // YY = ZZ
                String[] resultPair = value.toString().trim().split("=");
                if (resultPair.length < 2){
                    return;
                }
                String word = resultPair[0];
                int count = Integer.valueOf(resultPair[1].trim());
                if (!treeMap.containsKey(count)){
                    treeMap.put(count, new ArrayList<String>());
                }
                treeMap.get(count).add(word);
            }

            Iterator<Integer> iterator = treeMap.keySet().iterator();
            int sequence = 1;
            while (iterator.hasNext()){
                int count = iterator.next();
                for (String word: treeMap.get(count)) {
                    if (sequence > n){
                        return;
                    }
                    context.write(new DBOutputWritable(key.toString(), word, count), NullWritable.get());
                    sequence++;
                }
            }

		}
	}
}
