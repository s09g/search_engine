import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by zhangshiqiu on 2017/3/5.
 */
public class DocumentMapper extends Mapper<Object, Document, Text, IntWritable> {
    @Override
    public void map (Object key, Document value, Context context) throws IOException, InterruptedException{
        int id = value.id;
        String[] words = value.content.trim().split("\\s+");
        for (String word : words){
            context.write(new Text(word), new IntWritable(id));
        }
    }
}