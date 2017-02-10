import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.text.DecimalFormat;
/**
 * Created by zhangshiqiu on 2017/2/5.
 */
public class UnitSum {
    public static class PassMapper extends Mapper<Object, Text, Text, DoubleWritable> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            //input format: toPage\t unitMultiplication
            //target: pass to reducer
            String[] subPageRank = value.toString().split("\t");
            double subRank = Double.parseDouble(subPageRank[1].trim());
            context.write(new Text(subPageRank[0]), new DoubleWritable(subRank));
        }
    }

    public static class BetaMapper extends Mapper<Object, Text, Text, DoubleWritable> {
        float beta;

        @Override
        public void setup(Context context) {
            Configuration configuration = context.getConfiguration();
            beta = configuration.getFloat("beta", 0.2f);
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            //input format: Page\t PageRank
            //target: write to reducer
            String[] pageRank = value.toString().split("\t");
            double betaRank = Double.parseDouble(pageRank[1]) * beta;
            context.write(new Text(pageRank[0]), new DoubleWritable(betaRank));

        }
    }

    public static class SumReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
        @Override
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {

           //input key = toPage value = <unitMultiplication>
            //target: sum!

            double sum = 0;
            for (DoubleWritable value : values) {
                sum += value.get();
            }

            DecimalFormat decimalFormat = new DecimalFormat("#.0000000");
            sum = Double.valueOf(decimalFormat.format(sum));
            context.write(key, new DoubleWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        conf.setFloat("beta", Float.parseFloat(args[3]));

        Job job = Job.getInstance(conf);
        job.setJarByClass(UnitSum.class);

//        job.setMapperClass(PassMapper.class);
        ChainMapper.addMapper(job, PassMapper.class, Object.class, Text.class, Text.class, DoubleWritable.class, conf);
        ChainMapper.addMapper(job, BetaMapper.class, Text.class, DoubleWritable.class, Text.class, DoubleWritable.class, conf);

        job.setReducerClass(SumReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

//        FileInputFormat.addInputPath(job, new Path(args[0]));
        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, PassMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, BetaMapper.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.waitForCompletion(true);
    }
}
