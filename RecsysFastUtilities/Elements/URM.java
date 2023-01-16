package reebo_cecere.Elements;

import java.util.List;

public class URM {
    private float[][] matrix;
    private int n_user, n_item;

    public URM(int n_users, int n_items){
        this.n_user = n_users;
        this.n_item = n_items;
        matrix = new float[n_users][n_items];
    }

    public void fill(List<Interaction> interactions){
        for(Interaction interaction : interactions){
            int user = interaction.getIduser();
            int item = interaction.getIditem();
            matrix[user][item] = 1;
        }
    }

    public int getNUsers(){
        return n_user;
    }

    public int getNItems(){
        return n_item;
    }
}
