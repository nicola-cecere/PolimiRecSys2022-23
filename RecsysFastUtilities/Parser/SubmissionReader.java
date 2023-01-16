package reebo_cecere.Parser;

import reebo_cecere.Elements.Item;
import reebo_cecere.Elements.Recommendation;

import java.io.*;
import java.util.*;

public class SubmissionReader {
    public static List<Recommendation> parseSubmission(File f){
        List<Recommendation> recommendations = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                //regex to remove multiple spaces
                line = line.replaceAll(" +", " ");
                line = line.replaceAll(", ", ",");
                String[] params = line.split(",");
                int user_id = Integer.parseInt(params[0]);
                String[] str_items = params[1].split(" ");
                List<Integer> items = new ArrayList<>();
                for (String str_item : str_items) {
                    items.add(Integer.parseInt(str_item));
                }
                Recommendation rec = new Recommendation();
                rec.setUserId(user_id);
                rec.setItems(items);
                recommendations.add(rec);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recommendations;
    }

    public static Map<Integer, Recommendation> submissionToMap(List<Recommendation> recs){
        Map<Integer, Recommendation> hmap = new HashMap<>();
        for(Recommendation r : recs){
            hmap.put(r.getUserId(), r);
        }
        return hmap;
    }

    public static List<Integer> getTargetUsers(File users) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(users));
        String line = br.readLine();
        List<Integer> user_list = new ArrayList<>();
        while((line = br.readLine()) != null) {
            user_list.add(Integer.parseInt(line));
        }
        br.close();
        return user_list;
    }

    private static int getRandomItem(int n_item){
        return (int)(Math.random() * n_item);
    }

    public static void randomize(List<Recommendation> sub, List<Item> items){
        int n_items = items.size();
        for(Recommendation recommendation : sub){
            List<Integer> rec_items = recommendation.getItems();
            List<Integer> new_rec_items = new ArrayList<>();
            for(int i = 0; i < rec_items.size(); i++){
                if(Math.random() < 0.006 * i){ //randomize
                    new_rec_items.add(getRandomItem(n_items));
                } else { //keep equal
                    new_rec_items.add(rec_items.get(i));
                }
            }
            recommendation.setItems(new_rec_items);
        }
    }

    public static int compareSubs(List<Recommendation> s1, List<Recommendation> s2){
        int different = 0;
        for(int i = 0; i < s1.size(); i++){
            Recommendation r1 = s1.get(i);
            Recommendation r2 = s2.get(i);
            for(int o = 0; o < r1.getItems().size(); o++){
                if(!Objects.equals(r1.getItems().get(o), r2.getItems().get(o))){
                    //System.out.println(r1.getItems().get(0) + " vs " + r2.getItems().get(o));
                    different++;
                }
            }
        }
        return different;
    }
}
