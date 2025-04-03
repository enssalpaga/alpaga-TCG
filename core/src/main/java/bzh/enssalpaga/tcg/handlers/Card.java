package bzh.enssalpaga.tcg.handlers;

public class Card {
    private int rarity;
    private int id;

    private int hp;

    public Card(int rarity, int id){
        this.rarity = rarity;
        this.id = id;
        this.hp = initHp(id);
    }

    public int getRarity(){
        return rarity;
    }

    public int getId(){
        return id;
    }

    @Override
    public String toString() {
        return id + ";" + rarity;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    private static int initHp(int id){
        switch (id){
            case 0:
                return 205;
            case 1:
                return 193;
            case 2:
                return 205;
            case 3:
                return 195;
            case 4:
                return 195;
            case 5:
                return 161;
            case 6:
                return 170;
            case 7:
                return 185;
            case 8:
                return 180;
            case 9:
                return 180;
        }
        return 220;
    }
}
