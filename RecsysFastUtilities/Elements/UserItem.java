package reebo_cecere.Elements;

/**
 * User-Item tuple
 * @author Andrea Riboni
 */
public class UserItem implements Comparable<UserItem> {
    private int user_id, item_id;
    private float score;
    private int click = 0;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public UserItem(int user_id, int item_id) {
        this.user_id = user_id;
        this.item_id = item_id;
        score = 0;
    }

    public boolean equals(UserItem ui){
        return ui.item_id == item_id && ui.user_id == user_id;
    }

    public UserItem copy(){
        return new UserItem(user_id, item_id);
    }

    public void addScore(float value){
        score += value;
    }

    public void setScore(float value){
        score = value;
    }

    public float getScore(){
        return score;
    }

    public String toString(){
        return user_id + "/" + item_id;
    }

    public void addClick(int i) {
        click += i;
    }

    public int getClicked() {
        return click;
    }

    @Override
    public int compareTo(UserItem o) {
        int compared_user = Integer.compare(user_id, o.user_id);
        if(compared_user == 0){
            return Integer.compare(item_id, o.item_id);
        }
        return compared_user;
    }
}
