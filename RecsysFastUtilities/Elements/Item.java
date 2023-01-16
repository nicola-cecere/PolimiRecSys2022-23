package reebo_cecere.Elements;

/**
 * @author Andrea Riboni
 */
public class Item {
    private int iditem;
    private int length;
    private int type;

    public int getIditem() {
        return iditem;
    }

    public void setIditem(int iditem) {
        this.iditem = iditem;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ITEM: ").append(iditem).append(", LENGTH: ").append(length).append(", TYPE: ").append(type);
        return sb.toString();
    }
}
