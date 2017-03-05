import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by zhangshiqiu on 2017/2/20.
 */
public class Writer {
    PrintWriter writer;
    public Writer( String path){
        try {
//            FileChannel fc = new FileInputStream(path).getChannel();
//            ByteBuffer buffer = ByteBuffer.allocate(1024);
            this.writer = new PrintWriter(path);
        } catch (FileNotFoundException e){

        }
    }

    public synchronized void write(String line){
        writer.write(line);
    }

    public synchronized void close(){
        writer.close();
    }

}
