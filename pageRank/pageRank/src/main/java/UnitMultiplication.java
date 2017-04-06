import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
/**
 * Created by zhangshiqiu on 2017/2/5.
 */
public class UnitMultiplication {

    public static class TransitionMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            //input format: fromPage\t toPage1,toPage2,toPage3
            //target: build transition matrix unit -> fromPage\t toPage=probability
            String[] fromTo = value.toString().trim().split("\t");
            if (fromTo.length < 2 || fromTo[1].trim().length() == 0){
                return;
            }
            String from = fromTo[0];
            String[] tos = fromTo[1].split(",");
            for (String to : tos){
                context.write(new Text(from), new Text(to + "=" + (double)1.0/tos.length ));
            }
        }
    }

    public static class PRMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            //input format: Page\t PageRank
            //target: write to reducer
            String[] pageRank = value.toString().trim().split("\t");
            context.write(new Text(pageRank[1]), new Text(pageRank[1]));
        }
    }

    public static class MultiplicationReducer extends Reducer<Text, Text, Text, Text> {
        float beta;

        @Override
        public void setup(Context context){
            Configuration configuration = context.getConfiguration();
            beta = configuration.getFloat("beta", 0.2f);
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            //input key = fromPage value=<toPage=probability..., pageRank>
            //target: get the unit multiplication
            ArrayList<String> transitionList = new ArrayList<String>();
            double prValue = 0;
            for (Text value : values){
                String val = value.toString().trim();
                if (val.contains("=")){
                    transitionList.add(val);
                } else {
                    prValue = Double.parseDouble(val);
                }
            }

            for (String transitionUnit : transitionList){
                // unit = toPage=possibility
                // output  <toPage, possibility * prOfFromPage>
                String[] transition = transitionUnit.split("=");
                String outputKey = transition[0].trim();
                double relation = Double.parseDouble(transition[1]) * prValue * (1 - beta);
                // we need to write the middle value into HDFS, that's why we use string here
                String outPutValue = String.valueOf(relation);
                context.write(new Text(outputKey), new Text(outPutValue));
            }

        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(UnitMultiplication.class);

        // chain two mapper classes
        ChainMapper.addMapper(job, TransitionMapper.class, Object.class, Text.class, Text.class, Text.class, conf);
        ChainMapper.addMapper(job, PRMapper.class, Object.class, Text.class, Text.class, Text.class, conf);

        job.setReducerClass(MultiplicationReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, TransitionMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, PRMapper.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.waitForCompletion(true);
    }
}
