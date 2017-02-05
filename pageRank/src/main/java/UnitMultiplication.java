import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshiqiu on 2017/2/5.
 */
public class UnitMultiplication {
    public static class TransitionMapper extends Mapper<Object, Text, Text, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // a\tb,c,d
            // key = a
            // value = to = prob
            String line = value.toString().trim();
            String[] fromTo = line.split("\t");
            if (fromTo.length < 2 || fromTo[1].trim().length() == 0 ) {
                return;
            }

            String from = fromTo[0];
            String[] to = fromTo[1].split(",");
            for (String curt : to){
                context.write(new Text(from), new Text(curt+"="+(double)1/to.length));
            }

        }
    }

    public static class PRMapper extends Mapper<Object, Text, Text, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            //  page pageRank0
            //  a	0.25
            String[] pr = value.toString().trim().split("\t");
            context.write(new Text(pr[0].trim()), new Text(pr[1].trim()));
        }
    }

    public static class MultiplicationReducer extends Reducer <Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // key      :  1
            // value    : <2 = 1/4, 7=1/4, 1/6012 >
            List<String> transitionCell = new ArrayList<String>();
            double prCell = 0;

            for (Text val : values){
                String value = val.toString().trim();
                if (value.contains("=")){
                    transitionCell.add(value);
                } else {
                    prCell = Double.parseDouble(value);
                }
            }

            for (String cell : transitionCell){
                String[] transitionTuple = cell.split("=");
                String outputKey = transitionTuple[0];
                double probability = Double.parseDouble(transitionTuple[1]);
                String outputValue = String.valueOf(probability * prCell);
                context.write(new Text(outputKey), new Text(outputValue));
            }
        }
    }

    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(UnitMultiplication.class);


        ChainMapper.addMapper(job, TransitionMapper.class, Object.class, Text.class, Text.class, Text.class, conf);
        ChainMapper.addMapper(job, PRMapper.class, Object.class, Text.class, Text.class, Text.class, conf);

        job.setReducerClass(MultiplicationReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // arg[0] = relation.txt
        // arg[1] = pr.txt

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, TransitionMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, PRMapper.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.waitForCompletion(true);

    }
}
