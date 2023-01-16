package reebo_cecere.Producer;

import reebo_cecere.Elements.Interaction;
import reebo_cecere.Elements.Item;
import reebo_cecere.Elements.UserItem;
import reebo_cecere.Parser.InteractionReader;
import reebo_cecere.ScoreComputer.ScoreComputer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Performs operation on the interactions
 * @author Andrea Riboni
 */
public class InteractionCreator {
    private List<Interaction> interactions;
    private List<Item> items;

    public InteractionCreator(List<Interaction> interactions, List<Item> items) {
        this.interactions = interactions;
        this.items = items;
    }

    /**
     * Compute converted explicit ratings and save them into a file
     * @param sc algorithm to compute the scores
     * @param out output file
     * @throws IOException
     */
    public void computeAndSaveNewScores(ScoreComputer sc, File out) throws IOException {
        sc.computeAndSaveNewScores(out);
    }

    /**
     * Adds "negative" (0) interactions for cold items
     */
    public void addColdItemsInteractions(){
        int max_item_id = items.get(items.size() - 1).getIditem();
        System.out.println("Max item id is: " + max_item_id);
        int max_interacted_item = 24506; //from kaggle
        for(int i = max_interacted_item + 1; i <= max_item_id; i++){
            int interacting_user = (int)(41628 * Math.random());
            int interacting_item = i;
            Interaction fake_int = new Interaction();
            fake_int.setIditem(i);
            fake_int.setIduser(interacting_user);
            fake_int.setScore(0);
            fake_int.setFake();
            interactions.add(fake_int);
        }
    }

    public List<Interaction> addNegativeInteractions(int negative_ratio, int max_item){
        //index true interactions for a fast access
        Map<Integer, TreeSet<Interaction>> interactions = mapUserInteractions(this.interactions);
        for(Integer user : interactions.keySet())
            for(Interaction interaction : interactions.get(user))
                interaction.setScore(1);
        //generate random interactions not present in real interactions
        for(Integer user_id : interactions.keySet()){
            TreeSet<Interaction> interacted_items = interactions.get(user_id);
            int len = interacted_items.size();
            for(int x = 0; x < negative_ratio; x++) {
                for (int i = 0; i < len; i++) { //add a new item
                    boolean contained;
                    int item_id = 0;
                    do {
                        item_id = (int) (Math.random() * max_item);
                        int finalItem_id = item_id;
                        contained = interacted_items.stream().anyMatch(in -> in.getIditem() == finalItem_id);
                    } while (contained);
                    Interaction new_interaction = new Interaction();
                    new_interaction.setIditem(item_id);
                    new_interaction.setIduser(user_id);
                    new_interaction.setWatched(false);
                    new_interaction.setClicked(false);
                    new_interaction.setScore(-1f);
                    new_interaction.setFake();
                    interacted_items.add(new_interaction);
                }
            }
        }
        //get the interaction list
        List<Interaction> interaction_list = new ArrayList<>();
        for(Integer user_id : interactions.keySet()){
            TreeSet<Interaction> interacted_items = interactions.get(user_id);
            interaction_list.addAll(interacted_items);
        }
        return interaction_list;
    }



    /**
     * Get the number of interactions for a user
     * @param user user
     * @param interactions interactions
     * @return times user appears as an interaction
     */
    public static int getNofInteractions(int user, List<Interaction> interactions){
        return (int) interactions.stream().filter(in -> in.getIduser() == user).count();
    }

    public static float getAvgLengthForUser(int user, List<Interaction> interactions, List<Item> items){
        return (float)interactions.stream().filter(in -> in.getIduser() == user).flatMapToInt(f_in -> IntStream.of(searchItem(items, f_in.getIditem()).getLength())).average().getAsDouble();
    }

    /**
     * Search for an item within the item list
     * @param items item list (ordered by id since it's using binary search)
     * @param target_id item id to search
     * @return (possibly) found item or null
     */
    public static Item searchItem(List<Item> items, int target_id) {
        return searchItemRecursive(items, 0, items.size() - 1, target_id);
    }

    private static Item searchItemRecursive(List<Item> items, int l, int r, int target_id){
        if (r >= l) {
            int mid = l + (r - l) / 2;

            // If the element is present at the
            // middle itself
            if (items.get(mid).getIditem() == target_id)
                return items.get(mid);

            // If element is smaller than mid, then
            // it can only be present in left subarray
            if (items.get(mid).getIditem() > target_id)
                return searchItemRecursive(items, l, mid - 1, target_id);

            // Else the element can only be present
            // in right subarray
            return searchItemRecursive(items, mid + 1, r, target_id);
        }

        // We reach here when element is not present
        // in array
        return null;
    }

    public static Map<Integer, Double> getNofInteractions(List<Interaction> interactions, boolean normalize){
        //first we separate each episode by type for each user
        Map<Integer, Double> user_to_interactions = new HashMap<>();
        for(Interaction interaction : interactions){
            int id_user = interaction.getIduser();
            int id_item = interaction.getIditem();
            double nof_interactions = user_to_interactions.getOrDefault(id_user, 0d);
            nof_interactions++;
            user_to_interactions.put(id_user, nof_interactions);
        }
        if(normalize){
            Map<Integer, Double> user_to_interactions_norm = new HashMap<>();
            for(int user : user_to_interactions.keySet()){
                double norm = user_to_interactions.get(user);
                norm = normalized_sigmoid_for_interaction(norm);
                user_to_interactions_norm.put(user, norm);
            }
            return user_to_interactions_norm;
        }
        return user_to_interactions;
    }

    public static Map<Integer, float[]> getPercentageWatchedByType(List<Interaction> interactions, List<Item> items){
        //first we separate each episode by type for each user
        Map<Integer, int[]> user_to_types = new HashMap<>();
        for(Interaction interaction : interactions){
            int id_user = interaction.getIduser();
            int id_item = interaction.getIditem();
            Item item = searchItem(items, id_item);
            int type = item.getType();
            if(type == 0) continue;
            int[] views;
            if(user_to_types.containsKey(id_user)){ //just update the views
                views = user_to_types.get(id_user);
            } else { //create the views
                views = new int[7];
            }
            views[type-1]++;
            user_to_types.put(id_user, views);
        }
        //Then we determine the percentage
        //We compute the total
        Map<Integer, Integer> total_views = new HashMap<>();
        for(int user : user_to_types.keySet()){
            int[] views = user_to_types.get(user);
            int total_interaction = 0;
            for(int v : views){
                total_interaction += v;
            }
            total_views.put(user, total_interaction);
        }
        //And we compute the percentage (bonus) for each type
        Map<Integer, float[]> user_to_percentage = new HashMap<>();
        for(int user : user_to_types.keySet()){
            int tot_view_count = total_views.get(user);
            int[] views = user_to_types.get(user);
            float[] percentage = new float[views.length];
            for(int i = 0; i < views.length; i++){
                percentage[i] = (views[i]+0.0f) / tot_view_count;
            }
            user_to_percentage.put(user, percentage);
        }
        return user_to_percentage;
    }

    private static double normalized_sigmoid_for_length(double x) {
        return (1/( 1 + Math.pow(Math.E,(-0.3*(x-8)))));
    }

    private static double normalized_sigmoid_for_interaction(double x) {
        return (1/( 1 + Math.pow(Math.E,(-0.005*(x-500)))));
    }

    public static Map<Integer, Double> getAverageLength(List<Interaction> interactions, List<Item> items, boolean normalize){
        //extract unique user-item features
        TreeSet<UserItem> user_to_items = new TreeSet<>();
        for(Interaction interaction : interactions){
            int id_user = interaction.getIduser();
            int id_item = interaction.getIditem();
            UserItem ui = new UserItem(id_user, id_item);
            user_to_items.add(ui);
        }
        //Then determine the sum of the lengths
        Map<Integer, List<Double>> user_to_len = new HashMap<>();
        for(UserItem ui : user_to_items){
            double len = searchItem(items, ui.getItem_id()).getLength() + 0.0d;
            if(normalize){
                len = normalized_sigmoid_for_length(len);
            }
            List<Double> lengths = user_to_len.getOrDefault(ui.getUser_id(), new ArrayList<>());
            lengths.add(len);
            user_to_len.put(ui.getUser_id(), lengths);
        }
        //And compute the average
        Map<Integer, Double> user_to_avglen = new HashMap<>();
        for(int user : user_to_len.keySet()){
            double avg = 0;
            for(double x : user_to_len.get(user)){
                avg += x;
            }
            avg /= user_to_len.get(user).size();
            user_to_avglen.put(user, avg);
        }
        return user_to_avglen;
    }

    public static List<Interaction> impressionsToInteractions(List<Interaction> interactions, boolean only_impressions){
        TreeSet<Interaction> sorted_interactions = new TreeSet<>();
        for(Interaction i : interactions){
            if(!only_impressions) {
                sorted_interactions.add(i);
            } else { //add it as a 0 interaction to maintain the shape
                Interaction i0 = new Interaction();
                i0.setFake();
                i0.setIditem(i.getIditem());
                i0.setIduser(i.getIduser());
                sorted_interactions.add(i0);
            }
            for(int impression : i.getImpressions()){
                Interaction imp_int = new Interaction();
                imp_int.setIduser(i.getIduser());
                imp_int.setIditem(impression);
                sorted_interactions.add(imp_int);
            }
        }
        return new ArrayList<>(sorted_interactions);
    }

    public static Map<Integer, TreeSet<Interaction>> mapUserInteractions(List<Interaction> interactions){
        Map<Integer, TreeSet<Interaction>> mapped_interactions = new HashMap<>();
        for(Interaction interaction : interactions){
            int user = interaction.getIduser();
            if(mapped_interactions.containsKey(user)){
                TreeSet<Interaction> user_int = mapped_interactions.get(user);
                user_int.add(interaction);
            } else {
                TreeSet<Interaction> new_user_int = new TreeSet<>();
                new_user_int.add(interaction);
                mapped_interactions.put(user, new_user_int);
            }
        }
        return mapped_interactions;
    }

    public static void saveInteractions(List<Interaction> interactions, File output) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.append("user_id,item_id,impressions,rating\n");
        for(Interaction in : interactions){
            StringBuilder sb = new StringBuilder();
            //sb.append(in.getIduser()).append(",").append(in.getIditem()).append(",").append(in.impressionsToString()).append(",").append(in.getScore()).append("\n");
            sb.append(in.getIduser()).append(",").append(in.getIditem()).append(",").append(in.getScore()).append("\n");
            bw.append(sb.toString());
        }
        bw.close();
    }
}
