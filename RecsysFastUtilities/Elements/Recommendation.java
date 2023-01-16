package reebo_cecere.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Recommended items for a particular user
 * @author Andrea Riboni
 */
public class Recommendation {
    private int user_id;
    private List<Integer> items;
    private List<Double> scores;

    public Recommendation(){
        items = new ArrayList<>();
        scores = new ArrayList<>();
    }

    public void setUserId(int user_id){
        this.user_id = user_id;
    }

    public void setItems(List<Integer> items){
        this.items = items;
    }

    public void addItem(int item){
        items.add(item);
    }

    public List<Integer> getItems(){
        return items;
    }

    public int getUserId(){
        return user_id;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
    }

    public void addItem(int item, double score){
        items.add(item);
        scores.add(score);
    }

    public int[] getBestItems(int cutoff){
        if(scores.size() == 0){
            int max_score = items.size();
            for(int item : items){
                scores.add(max_score + 0d);
                max_score--;
            }
        }
        int[] indices = maxKIndex(scores, cutoff);
        int[] ret_items = new int[cutoff];
        for(int i = 0; i < cutoff; i++){
            ret_items[i] = items.get(indices[i]);
        }
        return ret_items;
    }

    /**
     * Return the indexes correspond to the top-k largest in an array.
     */
    private int[] maxKIndex(List<Double> array, int top_k) {
        double[] max = new double[top_k];
        int[] maxIndex = new int[top_k];
        Arrays.fill(max, Double.NEGATIVE_INFINITY);
        Arrays.fill(maxIndex, -1);

        top: for(int i = 0; i < array.size(); i++) {
            for(int j = 0; j < top_k; j++) {
                if(array.get(i) > max[j]) {
                    for(int x = top_k - 1; x > j; x--) {
                        maxIndex[x] = maxIndex[x-1]; max[x] = max[x-1];
                    }
                    maxIndex[j] = i; max[j] = array.get(i);
                    continue top;
                }
            }
        }
        return maxIndex;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("user:\t").append(user_id).append("\n");
        if(scores.size() == items.size())
            for(int i = 0; i < items.size(); i++){
                sb.append("item:\t").append(items.get(i)).append("\tscore:\t").append(scores.get(i)).append("\n");
            }
        else for(int i = 0; i < items.size(); i++){
            sb.append("item:\t").append(items.get(i)).append("\n");
        }
        return sb.toString();
    }
}
