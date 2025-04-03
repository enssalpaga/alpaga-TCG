package bzh.enssalpaga.tcg.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.enums.BoosterEnum;
import bzh.enssalpaga.tcg.handlers.CollectionManager;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.states.selection.BoosterSelection;
import bzh.enssalpaga.tcg.utils.Constants;
import bzh.enssalpaga.tcg.utils.Functions;

public class BoosterMenu extends GameState{

    private final Texture booster_up;
    private final Texture booster_down;
    private final Texture new_icon;
    private Texture[] cards;
    private Texture[] rarities;
    private boolean[] news;

    private final float booster_x;
    private final float booster_width;
    private final float booster_down_height;
    private final float booster_up_height;
    private float booster_down_y;
    private float booster_up_y;
    private float scratch_x;
    private final float scratch_precision;

    private float card_width;
    private float card_height;
    private float card_x;
    private float card_y;
    private float card_x_animated;

    private BoosterEnum state;
    private int card_number;

    public BoosterMenu(GameStateManager gsm) {
        super(gsm);
        Constants.BOOSTER_AVAILABLE = false;
        gsm.getConnectionManager().send("booster", "");

        this.booster_up = LaunchGame.assetManager.get("booster_up.png", Texture.class);
        this.booster_down = LaunchGame.assetManager.get("booster_down.png", Texture.class);
        this.new_icon = LaunchGame.assetManager.get("icons/new.png", Texture.class);

        booster_x = (float) cam.viewportWidth /8/2;
        booster_width = (float) (cam.viewportWidth * 7) /8;
        booster_down_height = booster_width * booster_down.getHeight() /booster_down.getWidth();
        booster_up_height = booster_width * booster_up.getHeight() /booster_up.getWidth();
        scratch_x = booster_x;
        scratch_precision = booster_down_height/8;
        booster_down_y = cam.viewportHeight*7/8 - booster_down_height - booster_up_height;
        booster_up_y = booster_down_height + booster_down_y;

        state = BoosterEnum.WAITING4SERVER;
        card_number = 0;
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
            if(state == BoosterEnum.OPENING){
                state = BoosterEnum.WAITING;
            }else if(state == BoosterEnum.CARD_WAITING){
                state = BoosterEnum.CARD_ANIMATING;
            }
        }

        if(Gdx.input.isTouched()){
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            x = screen_to_cam_x(x);
            y = screen_to_cam_y(y);

            if(state == BoosterEnum.WAITING){
                if(x - scratch_precision <= scratch_x){
                    state = BoosterEnum.OPENING;
                }
            }

            if(state == BoosterEnum.OPENING){
                if(booster_up_y - scratch_precision < y && booster_up_y + scratch_precision > y){
                    if(x + scratch_precision > booster_width + booster_x){
                        this.scratch_x = booster_width + booster_x;
                        this.state = BoosterEnum.ANIMATING;
                    }else if (x > this.scratch_x){
                        this.scratch_x = x;
                    }
                }else{
                    state = BoosterEnum.WAITING;
                }
            }
        }
    }

    @Override
    public void update(float dt) {
        if(state == BoosterEnum.ANIMATING){
            this.booster_up_y += dt*512;
            this.booster_down_y -= dt*512;
            if(booster_down_y < -booster_down_height){
                state = BoosterEnum.CARD_WAITING;
            }
        }else if(state == BoosterEnum.CARD_ANIMATING){
            this.card_x_animated += dt*1024;
            if(card_x_animated > cam.viewportWidth){
                card_number++;
                card_x_animated = card_x;
                if(card_number == 5){
                    gsm.set(new BoosterSelection(gsm));
                }else{
                    state = BoosterEnum.CARD_WAITING;
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr) {
        //Setup spriteBatch
        sb.setProjectionMatrix(cam.combined);
        //Setup ShapeRenderer
        sr.setProjectionMatrix(cam.combined);

        sb.begin();
        sr.begin(ShapeRenderer.ShapeType.Filled);

        if(state == BoosterEnum.CARD_ANIMATING && card_number+1 < 5){
            sb.draw(this.cards[card_number+1], card_x, card_y, card_width, card_height);
            sb.draw(this.rarities[card_number+1], card_x, card_y, card_width, card_height);
        }

        if(state == BoosterEnum.ANIMATING || state == BoosterEnum.CARD_WAITING || state == BoosterEnum.CARD_ANIMATING){
            sb.draw(this.cards[card_number], card_x_animated, card_y, card_width, card_height);
            sb.draw(this.rarities[card_number], card_x_animated, card_y, card_width, card_height);

            if(news[card_number]){
                float new_width = card_width/3;
                sb.draw(new_icon, card_x, card_y + card_height + card_height/16, new_width, new_width*new_icon.getHeight()/new_icon.getWidth());
            }
        }

        if(state == BoosterEnum.OPENING || state == BoosterEnum.WAITING || state == BoosterEnum.ANIMATING){
            sb.draw(this.booster_down, booster_x, booster_down_y, booster_width, booster_down_height);
            sb.draw(this.booster_up, booster_x, booster_up_y, booster_width, booster_up_height);

            sr.setColor(Color.GREEN);
            sr.rect(booster_x, booster_up_y-booster_down_height/128/2, scratch_x-booster_x, booster_down_height/128);
        }

        sb.end();
        sr.end();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void message(String event, String message) {
        String[] booster = message.split(";");
        int booster_size = booster.length/2;
        cards = new Texture[booster_size];
        rarities = new Texture[booster_size];
        news = new boolean[booster_size];
        for (int i = 0; i < booster_size; i++){
            int card = Integer.parseInt(booster[i*2]);
            int rarity = Integer.parseInt(booster[i*2+1]);
            cards[i] = LaunchGame.assetManager.get(Functions.getPath(rarity, card), Texture.class);
            rarities[i] = LaunchGame.assetManager.get("cards/r" + rarity + ".png", Texture.class);
            CollectionManager.add(rarity, card);
            news[i] = CollectionManager.getQuantity(rarity, card) == 1;
        }

        card_width = booster_width*7/8;
        card_height = card_width*cards[0].getHeight()/cards[0].getWidth();
        card_x = booster_x + (booster_width - card_width)/2;
        card_y = booster_down_y + card_height/32;
        this.card_x_animated = card_x;

        state = BoosterEnum.WAITING;
    }
}
