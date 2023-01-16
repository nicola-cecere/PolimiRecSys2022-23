package reebo_cecere.Producer;

import reebo_cecere.Elements.Interaction;
import reebo_cecere.Elements.Item;
import reebo_cecere.Parser.InteractionReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UserDescriptor {

    public static void createUserDescriptions(File output, List<Item> items, List<Interaction> interactions, boolean discard_id) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        if(!discard_id) {
            bw.append(
                    "iduser,"
            );
        }
        bw.append(
                "avg_len,interactions,perc_1,perc_2,perc_3,perc_4,perc_5,perc_6,perc_7\n"
        );
        Map<Integer, float[]> percentages = InteractionCreator.getPercentageWatchedByType(interactions, items);
        Map<Integer, Double> nof_interactions = InteractionCreator.getNofInteractions(interactions, true);
        Map<Integer, Double> average_lengths = InteractionCreator.getAverageLength(interactions, items, true);
        final int N_USERS = InteractionReader.getNofUsers(interactions);
        for(int i = 0; i < N_USERS; i++){
            StringBuilder sb = new StringBuilder();
            if(!discard_id) {
                //id
                sb.append(i).append(",");
            }
            //average seen items length
            sb.append(average_lengths.get(i)).append(",");
            //#interactions
            sb.append(nof_interactions.get(i)).append(",");
            //percentages per type
            float[] type_perc = percentages.get(i);
            for(int o = 0; o < type_perc.length - 1; o++){
                sb.append(type_perc[o]).append(",");
            }
            sb.append(type_perc[type_perc.length - 1]).append("\n");
            bw.append(sb.toString());
            if(i % 1000 == 1){
                System.out.println("Done writing " + i + " users");
            }
        }
        bw.close();
    }
}
