import java.util.HashMap;

/**
 * Created by zhangshiqiu on 2017/4/18.
 */
public class Document {
    private HashMap<String, Integer> wordCount;
    private int size;
    public Document(String docName, String content){
        wordCount = new HashMap<>();
        String[] words = content.split("\\W");
        size = words.length;
        int times;
        for (String word : words){
            times = wordCount.getOrDefault(word, 0);
            wordCount.put(word, times + 1);
        }
    }

    public boolean contains(String word){
        return wordCount.containsKey(word);
    }

    public int count(String word){
        return wordCount.getOrDefault(word, 0);
    }

    public int size(){
        return size;
    }


}
