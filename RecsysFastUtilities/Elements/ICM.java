package reebo_cecere.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Andrea Riboni
 */
public class ICM {
    //items are the rows, attributes are the columns
    private int[][] icm;
    private final int LENGTH = 0, TYPE = 1;

    public ICM(int nof_items, int nof_attributes){
        icm = new int[nof_items][nof_attributes];
    }

    /**
     * Fills the ICM using the item list
     * @param items item list
     */
    public void fill(List<Item> items){
        for(int i = 0; i < items.size(); i++){
            icm[i][LENGTH] = items.get(i).getLength();
            icm[i][TYPE] = items.get(i).getType();
        }
        System.out.println("ICM has been filled");
    }

    public int[][] getIcm() {
        return icm;
    }

    /**
     * Saves the dense ICM as CSV into a file
     * @param f output file
     * @throws IOException
     */
    public void saveTo(File f) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        for(int[] row : icm){
            for(int i = 0; i < row.length; i++){
                bw.append(row[i] + "");
                if(i != row.length - 1) bw.append(",");
            }
            bw.newLine();
        }
        bw.close();
    }

    /**
     * Saves the dense ICM as CSV into a file
     * @param items item list
     * @param f output file
     * @throws IOException
     */
    public static void saveDetailed(List<Item> items, File f) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.append("iditem,length,type");
        bw.newLine();
        for(int i = 0; i < items.size(); i++){
            StringBuilder sb = new StringBuilder();
            Item it = items.get(i);
            sb.append(it.getIditem()).append(",").append(it.getLength()).append(",").append(it.getType());
            bw.append(sb.toString());
            bw.newLine();
        }
        bw.close();
        System.out.println("ICM has been filled");
    }

}
