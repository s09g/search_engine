import java.io.PrintWriter;

/**
 * Created by zhangshiqiu on 2017/2/20.
 */
public class Writer {
    PrintWriter writer;
    public Writer(PrintWriter writer){
        this.writer = writer;
    }

    public synchronized void write(String line){
        writer.write(line);
    }

    public synchronized void close(){
        writer.close();
    }

}
