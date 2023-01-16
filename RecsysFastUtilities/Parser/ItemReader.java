package reebo_cecere.Parser;

import reebo_cecere.Elements.Item;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Item data parser
 * @author Andrea Riboni
 */
public class ItemReader {

    /**
     * Parses the item data and gets the item list
     * @param maintain_fakes not every item is described in these files. Set this attribute to TRUE to create filler items with unknown (0) attributes. Set this attribute to FALSE to avoid creating fake placeholder items
     * @return item list
     * @throws FileNotFoundException
     */
    public static List<Item> getItems(boolean maintain_fakes) throws FileNotFoundException {
        System.out.println("Reading items");
        //file reader
        BufferedReader i_len = new BufferedReader(new FileReader("data/data_ICM_length.csv"));
        BufferedReader i_type = new BufferedReader(new FileReader("data/data_ICM_type.csv"));

        String csv_line = null;

        //model representation of the items
        List<Item> items = new ArrayList<>();

        int max_item_id = 0;

        //reading line by line the lengths
        Map<Integer, Integer> iditem_to_length = new HashMap<>();
        try {
            while ((csv_line = i_len.readLine()) != null) {
                String[] parameters = csv_line.split(",");

                int iditem = 0, length = 0;
                try {
                    iditem = Integer.parseInt(parameters[0]);
                    length = Integer.parseInt(parameters[2]);
                } catch (NumberFormatException ex){
                    continue;
                }

                if(iditem > max_item_id) max_item_id = iditem;

                //adding a correspondence between the id and the length
                iditem_to_length.put(iditem, length);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        //reading line by line the types
        Map<Integer, Integer> iditem_to_type = new HashMap<>();
        try {
            while ((csv_line = i_type.readLine()) != null) {
                String[] parameters = csv_line.split(",");

                int iditem = 0, type = 0;
                try {
                    iditem = Integer.parseInt(parameters[0]);
                    type = Integer.parseInt(parameters[1]);
                } catch (NumberFormatException ex){
                    continue;
                }

                if(iditem > max_item_id) max_item_id = iditem;

                //adding a correspondence between the id and the type
                iditem_to_type.put(iditem, type);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        //Merging length and types
        System.out.println("Found " + iditem_to_length.keySet().size() + " lengths");
        System.out.println("Found " + iditem_to_type.keySet().size() + " types");

        for(int id = 0; id <= max_item_id; id++){
            Item item = new Item();
            item.setIditem(id);
            item.setType(iditem_to_type.getOrDefault(id, 0));
            item.setLength(iditem_to_length.getOrDefault(id, 0));
            boolean real_item = maintain_fakes;
            if(iditem_to_length.containsKey(id)){
                item.setLength(iditem_to_length.get(id));
                real_item = true;
            }
            if(iditem_to_type.containsKey(id)){
                item.setType(iditem_to_type.get(id));
                real_item = true;
            }
            if(real_item){
                items.add(item);
            }
        }

        System.out.println("Items correctly read");
        return items;
    }

    /**
     * Creates a hashmap (key: unique item id, value: item object) for efficient indexing
     * @param items item list
     * @return item hash map
     */
    public static Map<Integer, Item> toHashMap(List<Item> items){
        Map<Integer, Item> items_info = new HashMap<>();
        for(Item item : items){
            items_info.put(item.getIditem(), item);
        }
        return items_info;
    }
}
