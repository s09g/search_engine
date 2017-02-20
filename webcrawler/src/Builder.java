/**
 * Created by zhangshiqiu on 2017/2/20.
 */
public class Builder {
    StringBuffer builder;
    public Builder(StringBuffer builder){
        this.builder = builder;
    }

    public synchronized Builder append(CharSequence s){
        builder.append(s);
        return this;
    }

    public synchronized Builder append(int s){
        builder.append(s);
        return this;
    }

    public synchronized void setLength(int n){
        builder.setLength(n);
    }

    public synchronized String toString(){
        return builder.toString();
    }
}
