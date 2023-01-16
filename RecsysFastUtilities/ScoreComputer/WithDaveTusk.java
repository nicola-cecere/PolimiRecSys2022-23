package reebo_cecere.ScoreComputer;

import reebo_cecere.Elements.Interaction;
import reebo_cecere.Elements.Item;
import reebo_cecere.Elements.UserItem;
import reebo_cecere.Enums.Score;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrea Riboni
 * @author Luca Petracca
 * @author Davide Zanutto
 */
public class WithDaveTusk extends ScoreComputer{
    public WithDaveTusk(List<Interaction> interactions, List<Item> items) {
        super(interactions, items);
    }

    private List<UserItem> getWatched(){
        //first we separate each episode by type for each user
        List<UserItem> useritem_to_watched = new ArrayList<>();
        for(Interaction interaction : interactions){
            int id_user = interaction.getIduser();
            int id_item = interaction.getIditem();
            //Item item = searchItem(items, id_item);
            //int len = item.getLength();
            //if(len == 0) continue;
            int views = 0;
            UserItem ui = new UserItem(id_user, id_item);

            if(useritem_to_watched.isEmpty()){
                if(interaction.isWatched()){
                    ui.addScore(1);
                } else {
                    ui.addClick(1);
                }
                useritem_to_watched.add(ui);
            } else {
                UserItem last = useritem_to_watched.get(useritem_to_watched.size() - 1);
                if(last.equals(ui)){
                    if(interaction.isWatched()){
                        last.addScore(1);
                    } else {
                        last.addClick(1);
                    }
                } else {
                    if(interaction.isWatched()){
                        ui.addScore(1);
                    } else {
                        ui.addClick(1);
                    }
                    useritem_to_watched.add(ui);
                }
            }
        }

        int count = 0;
        for(UserItem ui : useritem_to_watched){
            int nof_watched = (int)ui.getScore();
            int nof_click = (int)ui.getClicked();
            float score = 0;
            if(nof_watched == 0){
                if(nof_click >= 4) {
                    score = 0.55f;
                } else {
                    score = 0.45f;
                }
            } else {
                Item item = searchItem(items, ui.getItem_id());
                int len = item.getLength();
                if(len == 0){
                    if(nof_watched < 5) {
                        score = 0.6f;
                    } else if(nof_watched >= 5 && nof_watched < 10) {
                        score = 0.725f;
                    } else {
                        score = 0.85f;
                    }
                } else {
                    float perc = nof_watched / len;
                    if(perc <= 0.25){
                        score = 0.7f;
                    } else if(perc > 0.25 && perc <= 0.5) {
                        score = 0.8f;
                    } else if(perc > 0.5 && perc <= 0.75) {
                        score = 0.9f;
                    } else {
                        score = 1;
                    }
                }
            }
            ui.setScore(score);
            if(++count < 100)
                System.out.println("user\t" + ui.getUser_id() + "\titem\t" + ui.getItem_id()+"\trating\t" + score);
        }
        return useritem_to_watched;
    }

    @Override
    public void computeAndSaveNewScores(File out) throws IOException {

        List<UserItem> ui_data = getWatched();

        System.out.println("Computing new scores");
        boolean verbose = false;
        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        bw.append("user_id,item_id,rating");
        bw.newLine();

        for(UserItem ui : ui_data){
            StringBuilder sb = new StringBuilder();
            sb.append(ui.getUser_id()).append(",").append(ui.getItem_id()).append(",").append(ui.getScore()).append("\n");
            bw.append(sb.toString());
        }

        bw.close();
    }
}
