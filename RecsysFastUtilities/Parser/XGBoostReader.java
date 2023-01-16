package reebo_cecere.Parser;

import reebo_cecere.Elements.Interaction;
import reebo_cecere.Elements.Recommendation;
import reebo_cecere.Elements.UserItem;
import reebo_cecere.Producer.InteractionCreator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class XGBoostReader {

    private static List<UserItem> parseXTrain(File x_train) throws IOException {
        //parse first file
        BufferedReader br = new BufferedReader(new FileReader(x_train));
        String csv_line = br.readLine();
        List<UserItem> user_item_list = new ArrayList<>();

        while((csv_line = br.readLine()) != null){
            String[] params = csv_line.split(",");
            int item_id = Integer.parseInt(params[1]);
            int user_id = Integer.parseInt(params[2]);
            UserItem ui = new UserItem(user_id, item_id);
            user_item_list.add(ui);
        }
        return user_item_list;
    }

    private static List<Double> parsePrediction(File predictions) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(predictions));
        String csv_line;
        List<Double> scores = new ArrayList<>();

        while((csv_line = br.readLine()) != null){
            double score = Double.parseDouble(csv_line);
            scores.add(score);
        }
        return scores;
    }

    private static List<Recommendation> mergeScoresWithRecommendations(List<Double> scores, List<UserItem> user_item_list){
        List<Recommendation> recommendations = new ArrayList<>();
        for(int i = 0; i < user_item_list.size(); i++){
            int user_id = user_item_list.get(i).getUser_id();
            int item_id = user_item_list.get(i).getItem_id();
            double score = scores.get(i);

            //update last recommendation or create a new one
            Recommendation rec;
            if(recommendations.isEmpty() || recommendations.get(recommendations.size() - 1).getUserId() != user_id){
                rec = new Recommendation();
                rec.setUserId(user_id);
                rec.addItem(item_id, score);
                recommendations.add(rec);
            } else {
                rec = recommendations.get(recommendations.size() - 1);
                rec.addItem(item_id, score);
            }
        }
        return recommendations;
    }

    private static Recommendation[] createTrueRecommendations(List<Recommendation> recommendations, int cutoff){
        Recommendation[] true_rec = new Recommendation[recommendations.size()];
        for(int i = 0; i < recommendations.size(); i++){
            Recommendation rec = new Recommendation();
            rec.setUserId(recommendations.get(i).getUserId());
            int[] best = recommendations.get(i).getBestItems(cutoff);
            for(int b : best) {
                rec.addItem(b);
            }
            true_rec[i] = rec;
        }
        return true_rec;
    }

    public static void sortXGBoostPredictions(File x_train, File predictions, File out) throws IOException {
        //parse first file
        List<UserItem> user_item_list = parseXTrain(x_train);

        //parse second file
        List<Double> scores = parsePrediction(predictions);

        //merge the scores for each user: each user is associated with *cutoff* items
        List<Recommendation> recommendations = mergeScoresWithRecommendations(scores, user_item_list);

        //create true recommendations (cutoff, ranked)
        int cutoff = recommendations.get(0).getItems().size();

        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        bw.append("user_id,item_list\n");
        for (Recommendation rec : recommendations) {
            bw.append(String.valueOf(rec.getUserId())).append(",");
            List<Integer> sorted_items = Arrays.stream(rec.getBestItems(cutoff)).boxed().collect(Collectors.toList());
            rec.setItems(sorted_items);
            for (int i = 0; i < cutoff - 1; i++) {
                bw.append(String.valueOf(rec.getItems().get(i))).append(" ");
            }
            bw.append(String.valueOf(rec.getItems().get(cutoff - 1))).append("\n");
        }
        bw.close();
    }

    public static void createUnseenSubmission(File target_users, File sub, File out, File best_sub, int cutoff, List<Interaction> interactions) throws IOException {
        //parse predictions
        List<Recommendation> recommendations = SubmissionReader.parseSubmission(sub);
        int xgb_cutoff = recommendations.get(0).getItems().size();
        Recommendation[] xgb_rec = createTrueRecommendations(recommendations, xgb_cutoff);

        //load test users
        List<Integer> sub_users = SubmissionReader.getTargetUsers(target_users);

        //load best submission
        List<Recommendation> best_recs = SubmissionReader.parseSubmission(best_sub);
        Map<Integer, Recommendation> best_recs_map = SubmissionReader.submissionToMap(best_recs);

        //make interactions look-up more efficient
        Map<Integer, TreeSet<Interaction>> mapped_interactions = InteractionCreator.mapUserInteractions(interactions);

        //generate submissions
        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        bw.append("user_id,item_list\n");

        for (Integer user : sub_users) {
            Recommendation rec = xgb_rec[user];
            List<Integer> recommended_items_all = rec.getItems();
            List<Integer> recommended_items_unseen = new ArrayList<>();

            //remove seen
            for(Interaction interaction : mapped_interactions.get(user)){
                //todo: remove them and replace them with unseen items of the best submission
                if(!recommended_items_all.contains(interaction.getIditem())){
                    recommended_items_unseen.add(interaction.getIditem());
                }
            }
            if(recommended_items_unseen.size() < 10){
                System.err.println("Not enough recommendations");
                //we replace this recommendations with our best's
                recommended_items_unseen = best_recs_map.get(user).getItems();
            }

            //print to file
            bw.append(String.valueOf(rec.getUserId())).append(",");
            for (int i = 0; i < cutoff - 1; i++) {
                bw.append(String.valueOf(recommended_items_unseen.get(i))).append(" ");
            }
            bw.append(String.valueOf(rec.getItems().get(cutoff - 1))).append("\n");
        }
        bw.close();
    }
}
