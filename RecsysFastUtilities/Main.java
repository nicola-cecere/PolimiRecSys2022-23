package reebo_cecere;

import reebo_cecere.Elements.Interaction;
import reebo_cecere.Elements.Item;
import reebo_cecere.Elements.Recommendation;
import reebo_cecere.Parser.InteractionReader;
import reebo_cecere.Parser.ItemReader;
import reebo_cecere.Parser.SubmissionReader;
import reebo_cecere.Parser.XGBoostReader;
import reebo_cecere.Producer.InteractionCreator;
import reebo_cecere.Producer.ListMerger;
import reebo_cecere.ScoreComputer.*;

import java.io.*;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        List<Interaction> interactions;
        List<Item> items;
        if(args != null && args.length > 0){
            switch (args[0]) {
                case "Sort_XGBoost" ->
                        XGBoostReader.sortXGBoostPredictions(
                                new File("./data/xgboost/XGBoost_Xtrain.csv"),
                                new File("./data/xgboost/predicts.csv"),
                                new File("./data/xgboost/sub.csv")
                        );
                case "Perform_RoundRobin" -> {
                    ListMerger lm = new ListMerger(
                            "./data/sub_0.csv",
                            "./data/sub_1.csv"
                    );
                    List<Recommendation> rr_list = lm.doRoundRobin();
                    ListMerger.save(rr_list, new File("./data/SubRR.csv"));
                }
                case "AddRandomNegativeInteractions" -> {
                    interactions = InteractionReader.getInteractions(new File("./data/interactions_and_impressions_noheader.csv"));
                    items = ItemReader.getItems(true);
                    InteractionCreator ic = new InteractionCreator(interactions, items);
                    List<Interaction> interactions_for_FM = ic.addNegativeInteractions(2, 24507);
                    InteractionCreator.saveInteractions(interactions_for_FM, new File("./data/RatingsWithNegative.csv"));
                }
                case "CreateURM" -> {
                    interactions = InteractionReader.getInteractions(new File("./data/interactions_and_impressions_noheader.csv"));
                    items = ItemReader.getItems(true);
                    ScoreComputer sc = new WithBinaryValues(interactions, items);
                    InteractionCreator ic = new InteractionCreator(interactions, items);
                    ic.computeAndSaveNewScores(sc, new File("./data/RatingsRealViews.csv"));
                }
                case "EnrichInteractions" -> {
                    interactions = InteractionReader.getInteractions(new File("./data/interactions_and_impressions_noheader.csv"));
                    List<Recommendation> submission = SubmissionReader.parseSubmission(new File("./data/best.csv"));
                    items = ItemReader.getItems(true);
                    interactions = ScoreComputer.merge(interactions, submission);
                    ScoreComputer sc = new WithBinaryValues(interactions, items);
                    InteractionCreator ic = new InteractionCreator(interactions, items);
                    ic.computeAndSaveNewScores(sc, new File("./data/EnrichedInteractions.csv"));
                }
                case "CompareSub" -> {
                    List<Recommendation> sub1 = SubmissionReader.parseSubmission(new File("./data/sub1.csv"));
                    List<Recommendation> sub2 = SubmissionReader.parseSubmission(new File("./data/sub2.csv"));
                    int diff_val = SubmissionReader.compareSubs(sub1, sub2);
                    float tot = 41116 * 10;
                    tot = diff_val / tot;
                    System.out.println(diff_val + " different values (" + tot + "% of total values)");
                }
            }
        }
    }
}
