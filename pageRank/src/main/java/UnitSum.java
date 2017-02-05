import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by zhangshiqiu on 2017/2/5.
 */
public class UnitSum {
    public static class PassMapper extends Mapper <Object, Text, Text, DoubleWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            //tag\tsubPR
            String[] pageSubRank = value.toString().trim().split("\t");
            String tag = pageSubRank[0].trim();
            double subRank = Double.parseDouble(pageSubRank[1].trim());
            context.write(new Text(tag), new DoubleWritable(subRank));
        }
    }

    public static class SumReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable>{
        @Override
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            double sum = 0;
            for (DoubleWritable val : values){
                sum += val.get();
            }

            DecimalFormat decimalFormat = new DecimalFormat("#.00000");
            sum = Double.valueOf(decimalFormat.format(sum));

            context.write(key, new DoubleWritable(sum));
        }

        public static void main(String[] args) throws Exception{
            Configuration conf = new Configuration();

            Job job = Job.getInstance(conf);

            job.setJarByClass(UnitSum.class);
            job.setMapperClass(PassMapper.class);
            job.setReducerClass(SumReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(DoubleWritable.class);

            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));
            job.waitForCompletion(true);

        }
    }

}
