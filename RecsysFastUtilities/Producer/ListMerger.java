package reebo_cecere.Producer;

import reebo_cecere.Elements.Item;
import reebo_cecere.Elements.Recommendation;
import reebo_cecere.Parser.SubmissionReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities to merge submission files
 * @author Andrea Riboni
 */
public class ListMerger {
    //can be extended to many lists
    private List<Recommendation> recs1, recs2;
    private List<Recommendation>[] recs;
    private final static int REC_LEN = 10;

    /**
     * Initialize the submission merger
     * @param path1 submission 1 file
     * @param path2 submission 2 file
     */
    public ListMerger(String path1, String path2){
        recs1 = parseSubmission(new File(path1));
        recs2 = parseSubmission(new File(path2));
    }

    private List<Recommendation> parseSubmission(File f){
        return SubmissionReader.parseSubmission(f);
    }

    /**
     * Perform round-robin of two lists
     * @return new list of recommendations
     */
    public List<Recommendation> doRoundRobin(){
        List<Recommendation> final_rec = new ArrayList<>();
        for(int o = 0; o < recs1.size(); o++) {
            Recommendation rec1 = recs1.get(o);
            Recommendation rec2 = recs2.get(o);
            Recommendation merge = new Recommendation();
            merge.setUserId(rec1.getUserId());
            //Adding items in round-robin-fashion
            int added = 0;
            int index_1 = 0, index_2 = 0;
            while (added < REC_LEN) {
                //System.out.println("added: " + added);
                int r1 = rec1.getItems().get(index_1);
                int r2 = rec2.getItems().get(index_2);
                //System.out.println("index1: " + index_1);
                //System.out.println("index2: " + index_2);
                //System.out.println("r1: " + r1);
                //System.out.println("r2: " + r2);
                if(!merge.getItems().contains(r1)) {
                    merge.addItem(r1);
                    added++;
                    //System.out.println("added r1");
                }
                index_1++;
                if(!merge.getItems().contains(r2) && added < 10){
                    merge.addItem(r2);
                    //System.out.println("added r2");
                    added++;
                }
                index_2++;
                //System.out.println();
            }
            final_rec.add(merge);
        }
        return final_rec;
    }

    /**
     * Saves the recommendations as a file
     * @param recommendations recommendation list
     * @param out output file
     * @throws IOException
     */
    public static void save(List<Recommendation> recommendations, File out) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        bw.append("user_id,item_list\n");
        for (Recommendation rec : recommendations) {
            bw.append(String.valueOf(rec.getUserId())).append(",");
            for (int o = 0; o < rec.getItems().size() - 1; o++) {
                bw.append(rec.getItems().get(o) + "").append(" ");
            }
            bw.append(rec.getItems().get(rec.getItems().size() - 1) + "");
            bw.newLine();
        }
        bw.close();
    }
}
