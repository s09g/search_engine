/**
 * Definition of Document:
 * class Document {
 *     public int id;
 *     public String content;
 * }
 */
public class InvertedIndex {
    /**
     * @param docs a list of documents
     * @return an inverted index
     */
    public Map<String, List<Integer>> invertedIndex(List<Document> docs) {
        Map<String, List<Integer>> map = new HashMap<>();
        
        for (Document doc : docs){
            int id = doc.id;
            String[] words = doc.content.split("\\s+");
            for (String word : words){
                if (!map.containsKey(word)){
                    map.put(word, new ArrayList<Integer>());
                }
                if (map.get(word).contains(id)){
                    continue;
                }
                map.get(word).add(id);
            }
        }
        return map;
    }
}