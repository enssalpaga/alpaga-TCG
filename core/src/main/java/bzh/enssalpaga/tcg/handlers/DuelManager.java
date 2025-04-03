package bzh.enssalpaga.tcg.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bzh.enssalpaga.tcg.utils.Constants;

public class DuelManager {
    private List<Card> hand_cards;
    private final Card[] bench = new Card[Constants.BENCH_SIZE];
    private Card[] opponent_bench = new Card[Constants.BENCH_SIZE];
    private Card active;
    private Card opponent_active;

    public void setHand(Card[] hand){
        hand_cards = new ArrayList<>();
        Collections.addAll(hand_cards, hand);
    }

    public void setOpponentActive(Card card){
        opponent_active = card;
    }

    public void setOpponentBench(Card[] bench){
        opponent_bench = bench;
    }

    public void handToActive(Card card){
        active = card;
        hand_cards.remove(card);
    }

    public void handToBench(Card card){
        int void_index = search(bench, null);
        if(void_index >= 0){
            bench[void_index] = card;
            hand_cards.remove(card);
        }
    }

    public void benchToActive(int index){
        active = bench[index];
        bench[index] = null;
    }

    public List<Card> getHand(){
        return hand_cards;
    }

    public Card[] getBench(){
        return bench;
    }

    public Card[] getOpponentBench(){
        return opponent_bench;
    }

    public Card getActive(){
        return active;
    }

    public Card getOpponentActive(){
        return opponent_active;
    }

    private static int search(Object[] objects, Object object){
        for(int i = 0; i < objects.length; i++){
            if(objects[i] == object){
                return i;
            }
        }
        return -1;
    }
}
