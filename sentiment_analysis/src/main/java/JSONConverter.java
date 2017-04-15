import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;

/**
 * Created by zhangshiqiu on 2017/3/22.
 *
 * Based on the code from Internet.
 * I changed it a little bit, to make it cooperate with my code.
 *
 */
public class JSONConverter {
    public static void main(String[] args) throws IOException, JSONException {
        JSONArray jsonArray = new JSONArray();

        // args[0] : path to your output, e.g. part-r-00000
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        String line = reader.readLine();

        // args[1] :  path you want to store result.json
        FileWriter fileWriter = new FileWriter(args[1]);

        BufferedWriter writer = new BufferedWriter(fileWriter);

        while (line != null) {
            JSONObject article = new JSONObject();
            String[] title_emotion_count = line.split("\t");
            if (title_emotion_count.length != 3){
                line = reader.readLine();
                continue;
            }
            JSONObject emotionList = new JSONObject();

            emotionList.put(title_emotion_count[1], title_emotion_count[2]);
            article.put("title", title_emotion_count[0]);
            for (int i = 0; i < 2; i++) {
                line = reader.readLine();
                title_emotion_count = line.split("\t");
                emotionList.put(title_emotion_count[1], title_emotion_count[2]);
            }
            article.put("data", emotionList);
            jsonArray.put(article);

            line = reader.readLine();
        }

        writer.write(jsonArray.toString());

        reader.close();
        writer.close();
    }
}
