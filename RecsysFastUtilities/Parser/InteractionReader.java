package reebo_cecere.Parser;

import reebo_cecere.Elements.Interaction;

import java.io.*;
import java.util.*;

/**
 * @author Andrea Riboni
 */
public class InteractionReader {

    /**
     * Reads the interactions file and inverts 0s with 1s
     * @param input interaction file
     * @param output output file
     * @throws IOException
     */
    public static void invertZeroOnes(File input, File output) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(input));
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        String csv_line = null;
        try {
            while ((csv_line = br.readLine()) != null) {
                String[] parameters = csv_line.split(",");
                //creating this interaction
                Interaction interaction = new Interaction();
                boolean watched = Integer.parseInt(parameters[parameters.length-1]) == 0;

                for(int i = 0; i < parameters.length - 1; i++){
                    bw.append(parameters[i]);
                    bw.append(",");
                }
                bw.append(watched ? "1" : "0");
                bw.newLine();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        br.close();
        bw.close();
    }

    /**
     * Parse the interaction file to retrieve a Java list of Interactions
     * @return interactions
     * @throws FileNotFoundException
     */
    public static List<Interaction> getInteractions(File input) throws FileNotFoundException {
        System.out.println("Reading interactions");
        //file reader
        BufferedReader br = new BufferedReader(new FileReader(input));

        String csv_line = null;

        //model representation of the interactions and impressions
        List<Interaction> interactions = new ArrayList<>();

        //reading line by line
        try {
            while ((csv_line = br.readLine()) != null) {
                String[] parameters = csv_line.split(",");
                //creating this interaction
                Interaction interaction = new Interaction();
                int iduser = Integer.parseInt(parameters[0]);
                int iditem = Integer.parseInt(parameters[1]);
                boolean watched = Integer.parseInt(parameters[parameters.length-1]) == 0;
                List<Integer> impressions = new ArrayList<>();
                for(int i = 2; i < parameters.length - 1; i++){
                    if(!parameters[i].isEmpty())
                        impressions.add(Integer.parseInt(parameters[i].replace("\"", "")));
                }
                interaction.setIduser(iduser);
                interaction.setIditem(iditem);
                interaction.setWatched(watched);
                interaction.setClicked(!watched);
                interaction.setImpressions(impressions);

                //adding this interaction to our "database"
                interactions.add(interaction);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Interactions correctly read");
        return interactions;
    }


    /**
     * Gets the number of unique users from the interaction list
     * @param interactions interaction list
     * @return number of users
     */
    public static int getNofUsers(List<Interaction> interactions){
        TreeSet<Integer> users_list = new TreeSet<>();
        for(Interaction interaction : interactions){
            int iduser = interaction.getIduser();
            if(!users_list.contains(iduser)){
                users_list.add(iduser);
            }
        }
        return users_list.size();
    }

    public static void printColdUsers(List<Interaction> interactions){
        int last_user = 0;
        for(Interaction interaction : interactions){
            int iduser = interaction.getIduser();
            if(iduser == last_user){
                continue;
            }
            if(iduser == last_user + 1){
                last_user = iduser;
                continue;
            }
            System.out.println(last_user);
        }
    }
}
