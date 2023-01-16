package reebo_cecere.Producer;

import java.io.*;

/**
 * Perform operations on the ICM
 * @author Andrea Riboni
 */
public class ICMCreator {

    /**
     * Remove cold items from the item list
     * @param in item data file
     * @param out output file
     * @throws IOException
     */
    public void truncateICM(File in, File out) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(in));
        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        String line = null;
        int last = -1;
        line = br.readLine(); //header
        bw.append("item_id,feature_id,data\n"); //data always 1
        while ((line = br.readLine()) != null) {
            String[] attributes = line.split(",");
            int this_id = Integer.parseInt(attributes[0]);
            if(this_id > 24506){ //cold item
                break;
            }
            bw.append(line);
            bw.newLine();
        }
        bw.close();
        br.close();
    }
}
