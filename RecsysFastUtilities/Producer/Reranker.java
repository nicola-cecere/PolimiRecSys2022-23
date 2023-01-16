package reebo_cecere.Producer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Reranker {
    //todo
    public static float[][] parseScoresMatrix(File scores_matrix, int rows, int cols) throws IOException {
        float[][] scores = new float[rows][cols];
        BufferedReader br = new BufferedReader(new FileReader(scores_matrix));
        String line = null;
        int count = 0;
        while ((line = br.readLine()) != null){
            String[] values = line.split(",");
            float[] f_values = new float[values.length];
            for(int i = 0; i < values.length; i++){
                f_values[i] = Float.parseFloat(values[i]);
            }
            scores[count] = f_values;
            count++;
        }
        return scores;
    }
}
