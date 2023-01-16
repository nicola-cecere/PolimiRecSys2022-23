package reebo_cecere.ScoreComputer;

import reebo_cecere.Elements.Interaction;
import reebo_cecere.Elements.Item;
import reebo_cecere.Elements.Recommendation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WithEnriched extends ScoreComputer {
    public WithEnriched(List<Interaction> interactions) {
        super(interactions, null);
    }

    @Override
    public void computeAndSaveNewScores(File out) throws IOException {
        System.out.println("Computing new scores");
        boolean verbose = false;
        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        bw.append("user_id,item_id,rating");
        bw.newLine();
        Interaction last_action = null;
        for(Interaction interaction : interactions){
            //print("\nStudying interaction: " + interaction, verbose);
            float new_score = 0; //new score starts at 0
            //getting basic data
            int user_id = interaction.getIduser();
            int item_id = interaction.getIditem();
            //is this interaction part of the previous?
            if(last_action != null && last_action.getIduser() == user_id && last_action.getIditem() == item_id){ //same user-item couple -> adding the score
                new_score += last_action.getScore();
                print("This was just like the old interaction. Recovered score is " + new_score, verbose);
            } else {
                print("This seems to be a new interaction: " + user_id + "/" + item_id, verbose);
                if(last_action != null){ //user-item couple has changed: saving the old one
                    StringBuilder sb = new StringBuilder();
                    sb.append(last_action.getIduser()).append(",").append(last_action.getIditem()).append(",").append(last_action.getScore()).append("\n");
                    bw.append(sb);
                    print("I just saved the old one: " + sb.toString().replace("\n", ""), verbose);
                }
                last_action = interaction;
                print("Setting this as last_action: " + last_action, verbose);
            }

            //computing this interaction's bonus
            new_score++;

            //Score is computed: updating last action's score
            if(interaction.isFake()) new_score = 0;
            last_action.setScore(new_score);
            print("New computed score is " + new_score, verbose);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(last_action.getIduser()).append(",").append(last_action.getIditem()).append(",").append(last_action.getScore()).append("\n");
        bw.append(sb);
        print("I just saved the last one: " + sb.toString().replace("\n", ""), verbose);
        bw.close();
    }
}
