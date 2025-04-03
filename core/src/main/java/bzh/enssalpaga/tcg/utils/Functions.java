package bzh.enssalpaga.tcg.utils;

import bzh.enssalpaga.tcg.handlers.Card;

public class Functions {
    public static String getPath(int rarity, int id){
        return "cards/" + id + "r" + rarity + ".png";
    }
}
