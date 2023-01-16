package reebo_cecere.ScoreComputer;

import reebo_cecere.Elements.Interaction;
import reebo_cecere.Elements.Item;
import reebo_cecere.Enums.Score;
import reebo_cecere.Producer.InteractionCreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrea Riboni
 * Like WithKnownLength but includes a bonus for the type
 * If a user has watched 50% type-1 items, whenever he watches a type-1 item it gets a +0.5 bonus
 */
public class WithKnownTanHTypeAndLength extends ScoreComputer{
    public WithKnownTanHTypeAndLength(List<Interaction> interactions, List<Item> items) {
        super(interactions, items);
    }

    private Map<Integer, float[]> getWatchedByType(){
        return InteractionCreator.getPercentageWatchedByType(interactions, items);
    }

    @Override
    public void computeAndSaveNewScores(File out) throws IOException {
        //Getting the percentage for each type seen
        Map<Integer, float[]> user_to_percent = getWatchedByType();
        System.out.println("Computing new scores");
        boolean verbose = false;
        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        bw.append("user_id,item_id,rating");
        bw.newLine();
        Interaction last_action = null;
        float nof_episodes = 0;
        int nof_rewatch = 0;
        float max = 0;
        for(Interaction interaction : interactions){
            print("\nStudying interaction: " + interaction, verbose);
            float new_score = 0; //new score starts at 0
            //getting basic data
            int user_id = interaction.getIduser();
            int item_id = interaction.getIditem();
            boolean watched = interaction.isWatched();
            //if(watched) nof_rewatch++;
            interaction.setWatched(false);
            interaction.setClicked(false);
            //is this interaction part of the previous?
            if(last_action != null && last_action.getIduser() == user_id && last_action.getIditem() == item_id){ //same user-item couple -> adding the score
                new_score += last_action.getScore();
                print("This was just like the old interaction. Recovered score is " + new_score, verbose);
            } else {
                //checking how many episodes this series has
                nof_episodes = searchItem(items, item_id).getLength();
                print("This seems to be a new interaction: " + user_id + "/" + item_id, verbose);
                if(last_action != null){ //user-item couple has changed: saving the old one
                    StringBuilder sb = new StringBuilder();
                    sb.append(last_action.getIduser()).append(",").append(last_action.getIditem()).append(",").append(tanh(last_action.getScore(), 0.15f)).append("\n");
                    bw.append(sb);
                    print("I just saved the old one: " + sb.toString().replace("\n", ""), verbose);
                }
                last_action = interaction;
                print("Setting this as last_action: " + last_action, verbose);
            }

            Item item = searchItem(items, item_id);
            float bonus = 0;
            if(item.getType() != 0){
                bonus = user_to_percent.get(user_id)[item.getType() - 1];
            }

            //computing this interaction's bonus
            if(last_action.getIduser() == user_id && last_action.getIditem() == item_id){ //not the first time this couple appears
                print("This user-item couple already exists - watched: " + last_action.isWatched() + ", clicked: " + last_action.isClicked(), verbose);
                if(watched && !last_action.isWatched()){ //first time this user watches this item
                    print("I've watched this item and it was clicked. Setting as watched", verbose);
                    new_score += Score.FIRST_TIME_WATCHED.getValue();
                    last_action.setWatched(true);
                } else if(watched) { //user is rewatching this item
                    print("I'm rewatching this item", verbose);
                    float divisor = nof_episodes;
                    if(divisor != 0){
                        new_score += Score.REWATCHED.getValue() / divisor;
                    }
                } else if(!watched && !last_action.isClicked()){ //first time this user clickes this item
                    print("I've not watched this item and it was watched. Setting as clicked", verbose);
                    new_score += Score.FIRST_TIME_CLICKED.getValue();
                    last_action.setClicked(true);
                } else if(!watched){ //user is reclicking this item
                    float divisor = nof_episodes == 0 ? 1 : nof_episodes;
                    new_score += Score.RECLICKED.getValue() / divisor;
                    print("I've reclicked this item", verbose);
                }
                new_score += bonus;
            } else { //first time this couple appears
                print("First time this user-item couple appears", verbose);
                if(watched){
                    print("And i'm watching it", verbose);
                    new_score += Score.FIRST_TIME_WATCHED.getValue();
                    last_action.setWatched(true);
                } else {
                    print("And i'm clicking it", verbose);
                    new_score += Score.FIRST_TIME_CLICKED.getValue();
                    last_action.setClicked(true);
                }
                new_score += bonus;
            }

            //computing the length bonus

            //Score is computed: updating last action's score
            last_action.setScore(new_score);
            print("New computed score is " + new_score, verbose);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(last_action.getIduser()).append(",").append(last_action.getIditem()).append(",").append(tanh(last_action.getScore(), 0.15f)).append("\n");
        bw.append(sb);
        print("I just saved the last one: " + sb.toString().replace("\n", ""), verbose);
        bw.close();
    }

    private float tanh(float x, float a){
        return (float) Math.tanh(a*x);
    }
}
