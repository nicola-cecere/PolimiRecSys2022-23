package reebo_cecere.ScoreComputer;

import reebo_cecere.Elements.Interaction;
import reebo_cecere.Elements.Item;
import reebo_cecere.Elements.Recommendation;
import reebo_cecere.Parser.InteractionReader;
import reebo_cecere.Producer.InteractionCreator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Algorithm wrapper class to compute simulated explicit ratings
 * @author Andrea Riboni
 */
public abstract class ScoreComputer {
    protected float MAX_SCORE_THRESHOLD = 10;
    protected List<Interaction> interactions;
    protected List<Item> items;

    /**
     * Initialization
     * @param interactions interaction list
     * @param items item list
     */
    public ScoreComputer(List<Interaction> interactions, List<Item> items) {
        this.interactions = interactions;
        this.items = items;
    }

    /**
     * Compute new scores and save them into a file
     * @param out output file
     * @throws IOException
     */
    public abstract void computeAndSaveNewScores(File out) throws IOException;

    protected void print(String str, boolean verbose){
        if(verbose) System.out.println(str);
    }

    /**
     * Search for an item within the item list
     * @param items item list (ordered by id since it's using binary search)
     * @param target_id item id to search
     * @return (possibly) found item or null
     */
    protected Item searchItem(List<Item> items, int target_id) {
        return InteractionCreator.searchItem(items, target_id);
    }

    /**
     * Sets a threshold for the score
     * @param v threshold
     */
    public void setThreshold(float v){
        MAX_SCORE_THRESHOLD = v;
    }

    public static List<Interaction> merge(List<Interaction> interactions, List<Recommendation> recommendations){
        Map<Integer, TreeSet<Interaction>> sorted_interactions = InteractionCreator.mapUserInteractions(interactions);
        for(Recommendation rec : recommendations){
            int user = rec.getUserId();
            for(int item : rec.getItems()){
                Interaction interaction = new Interaction();
                interaction.setIduser(user);
                interaction.setIditem(item);
                interaction.setWatched(true);
                TreeSet<Interaction> this_user_interactions = sorted_interactions.get(user);
                this_user_interactions.add(interaction);
            }
        }
        List<Interaction> merged_interactions = new ArrayList<>();
        for(int user : sorted_interactions.keySet()){
            TreeSet<Interaction> this_user_interactions = sorted_interactions.get(user);
            for(Interaction interaction : this_user_interactions){
                merged_interactions.add(interaction);
            }
        }
        return merged_interactions;
    }
}
