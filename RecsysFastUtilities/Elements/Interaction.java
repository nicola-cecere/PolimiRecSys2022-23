package reebo_cecere.Elements;

import java.util.List;

/**
 * Some type of interaction between a user and an item
 * @author Andrea Riboni
 */
public class Interaction implements Comparable<Interaction> {
    private int iduser, iditem;
    private boolean watched, clicked;
    private float score;
    private boolean isFake = false;
    private List<Integer> impressions;

    /**
     * Sometimes is useful to create fake interactions
     */
    public void setFake(){
        isFake = true;
    }

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    public int getIditem() {
        return iditem;
    }

    public void setIditem(int iditem) {
        this.iditem = iditem;
    }

    public boolean isWatched() {
        return watched;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public List<Integer> getImpressions() {
        return impressions;
    }

    public void setImpressions(List<Integer> impressions) {
        this.impressions = impressions;
    }

    public void addImpression(int impression){
        this.impressions.add(impression);
    }

    public String impressionsToString(){
        if(impressions == null || impressions.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        for(int i = 0; i < impressions.size() - 1; i++){
            sb.append(i).append(",");
        }
        sb.append(impressions.get(impressions.size() - 1));
        sb.append("\"");
        return sb.toString();
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("USER: ").append(iduser).append(", ITEM: ").append(iditem).append(", WATCHED: ").append(watched).append(", IMPRESSIONS: ").append(impressionsToString());
        return sb.toString();
    }

    public boolean isFake() {
        return isFake;
    }

    @Override
    public int compareTo(Interaction i) {
        if(iduser < i.getIduser()) return -1;
        if(iduser == i.getIduser()){
            return Integer.compare(iditem, i.getIditem());
        }
        return 1;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Interaction){
            Interaction oi = (Interaction)o;
            return oi.iditem == iditem && oi.iduser == iduser;
        }
        return false;
    }
}
