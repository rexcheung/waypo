package zxb.zweibo.bean;

import java.util.List;

/**
 * Created by rex on 15-8-28.
 */
public class FavoriteJson {

    /**
     * favorites : []
     * total_number : 212
     */
    public List<FavoriteItem> favorites;
    public int total_number;

    public List<FavoriteItem> getFavorites() {
        return favorites;
    }

    public int getTotal_number() {
        return total_number;
    }
}
