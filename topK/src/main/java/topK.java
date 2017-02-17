import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by zhangshiqiu on 2017/2/8.
 */
class wordNode {
    Text word;
    int frequency;
    public wordNode(Text word, int frequency){
        this.word = word;
        this.frequency = frequency;
    }
}
public class topK {

    public static class Map extends Mapper<Text, Document, Text, IntWritable>{
        @Override
        public void map(Text key, Document value, Context context)
                throws IOException, InterruptedException {
            for (String word : value.content.split("\\s+")){
                if (word.length() > 0) {
                    context.write(new Text(word), new IntWritable(1));
                }
            }
        }
    }

    public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable>{
        private PriorityQueue<wordNode> minHeap;
        private int k;

        private Comparator<wordNode> wordCmp = new Comparator<wordNode>(){
            public int compare(wordNode a, wordNode b){
                if (a.frequency == b.frequency){
                    return b.word.compareTo(a.word);
                }
                return a.frequency - b.frequency;
            }
        };

        @Override
        public void setup(Context context) {
            Configuration configuration = new Configuration();
            k = configuration.getInt("k", 10);
            minHeap = new PriorityQueue<>(k, wordCmp);
        }

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException{
            int sum = 0;
            for (IntWritable value : values) {
                sum += value.get();
            }

            wordNode node = new wordNode(key, sum);
            if (minHeap.size() < k){
                minHeap.add(node);
            } else {
                if (wordCmp.compare(minHeap.peek(), node) < 0 ){
                    minHeap.poll();
                    minHeap.add(node);
                }
            }
        }

        @Override
        public void cleanup(Context context) throws IOException, InterruptedException {
            wordNode[] nodeList = new wordNode[k];
            for (int i = k - 1; i >= 0; i--){
                nodeList[i] = minHeap.poll();
            }

            for (wordNode n: nodeList){
                context.write(n.word, new IntWritable(n.frequency));
            }
        }
    }

    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();

        conf.setInt("k", Integer.parseInt(args[2]));

        Job job = Job.getInstance(conf);
        job.setJarByClass(topK.class);

        job.setMapperClass(Map.class);

        job.setReducerClass(Reduce.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));

        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);
    }
}
