package bzh.enssalpaga.tcg.handlers;

import java.util.ArrayList;
import java.util.List;

import bzh.enssalpaga.tcg.utils.Constants;

public class CollectionManager {
    private static int[][] collection;
    private static int[][] trade;
    private static List<Card> deck;
    public static void setCollection(String collection_string, String deck_string){
        collection = stringToCollection(collection_string);

        String[] cards = deck_string.split(";");
        deck = new ArrayList<>();
        for(int i = 0; i < cards.length/2; i++){
            int card = Integer.parseInt(cards[i*2]);
            int rarity = Integer.parseInt(cards[i*2+1]);
            if(card != -1){
                addDeck(rarity, card);
            }
        }
    }

    public static void resetTrade(){
        trade = new int[Constants.RARITY_NUMBER][];
        for(int i = 0; i<Constants.RARITY_NUMBER; i++){
            trade[i] = new int[Constants.CARD_NUMBER];
            for(int j = 0; j<Constants.CARD_NUMBER; j++){
                trade[i][j] = 0;
            }
        }
    }

    public static void addTrade(int rarity, int id){
        trade[rarity][id]++;
    }

    public static void removeTrade(int rarity, int id){
        trade[rarity][id]--;
    }

    public static int getTrade(int rarity, int id){
        return trade[rarity][id];
    }

    public static String getTrade(){
        String trade_string = "";
        for(int i = 0; i<Constants.RARITY_NUMBER; i++){
            for(int j = 0; j<Constants.CARD_NUMBER; j++){
                trade_string += trade[i][j] + ";";
            }
        }
        return trade_string;
    }

    public static int getQuantityWithoutTrade(int rarity, int id){
        return getQuantity(rarity, id) - trade[rarity][id];
    }

    public static boolean addDeck(int rarity, int id){
        if(deckIsFull()){
            return false;
        }else{
            deck.add(new Card(rarity, id));
            return true;
        }
    }

    public static void removeDeck(int i){
        deck.remove(i);
    }

    public static Card getDeck(int i){
        if(deck.size() > i){
            return deck.get(i);
        }else{
            return null;
        }
    }

    public static String getDeck(){
        String deck_string = "";
        for(Card card : deck){
            deck_string += card.getId() + ";" + card.getRarity() + ";";
        }
        return deck_string;
    }

    public static boolean deckIsFull(){
        return deck.size() == Constants.DECK_SIZE;
    }

    public static void add(int rarity, int card){
        collection[rarity][card]++;
    }

    public static int getQuantity(int rarity, int card){
        return collection[rarity][card];
    }

    public static int getQuantityWithoutDeck(int rarity, int id){
        int quantity = getQuantity(rarity, id);
        for(Card card : deck){
            if(card.getId() == id && card.getRarity() == rarity){
                quantity--;
            }
        }
        return quantity;
    }

    public static int[][] stringToCollection(String string){
        String[] card_numbers = string.split(";");
        int[][] collection = new int[Constants.RARITY_NUMBER][];
        for(int i = 0; i<Constants.RARITY_NUMBER; i++){
            collection[i] = new int[Constants.CARD_NUMBER];
            for(int j = 0; j<Constants.CARD_NUMBER; j++){
                collection[i][j] = Integer.parseInt(card_numbers[i*Constants.CARD_NUMBER+j]);
            }
        }
        return collection;
    }
}
