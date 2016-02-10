package zxb.zweibo.ui.fragment.view;

import java.util.List;

import zxb.zweibo.bean.FavoriteItem;
import zxb.zweibo.bean.FavoriteJson;

/**
 * Created by rex on 15-8-28.
 */
public interface IFavorites {
    void response(List<FavoriteItem> favList);
}
