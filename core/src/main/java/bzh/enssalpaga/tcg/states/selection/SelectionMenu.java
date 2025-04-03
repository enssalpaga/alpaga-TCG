package bzh.enssalpaga.tcg.states.selection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.states.GameState;

public abstract class SelectionMenu extends GameState {
    private final Texture booster;
    private final Texture collection;
//    private final Texture multiplayer;
//    private final Texture deck;
//    private final Texture trade;

    private final float booster_x;
    private final float collection_x;
    private final float multiplayer_x;
    private final float deck_x;
    private final float trade_x;

    protected final float selectionHeight;

    public SelectionMenu(GameStateManager gsm) {
        super(gsm);

        if(this instanceof BoosterSelection){
            this.booster = LaunchGame.assetManager.get("icons/booster_selected.png", Texture.class);
        }else{
            this.booster = LaunchGame.assetManager.get("icons/booster.png", Texture.class);
        }
        if(this instanceof CollectionSelection){
            this.collection = LaunchGame.assetManager.get("icons/collection_selected.png", Texture.class);
        }else{
            this.collection = LaunchGame.assetManager.get("icons/collection.png", Texture.class);
        }
//        if(this instanceof MultiplayerSelection){
//            this.multiplayer = LaunchGame.assetManager.get("icons/multiplayer_selected.png", Texture.class);
//        }else{
//            this.multiplayer = LaunchGame.assetManager.get("icons/multiplayer.png", Texture.class);
//        }
//        if(this instanceof DeckSelection){
//            this.deck = LaunchGame.assetManager.get("icons/deck_selected.png", Texture.class);
//        }else{
//            this.deck = LaunchGame.assetManager.get("icons/deck.png", Texture.class);
//        }
//        if(this instanceof TradeSelection){
//            this.trade = LaunchGame.assetManager.get("icons/trade_selected.png", Texture.class);
//        }else{
//            this.trade = LaunchGame.assetManager.get("icons/trade.png", Texture.class);
//        }
        this.selectionHeight = cam.viewportHeight /12f;

        this.deck_x = cam.viewportWidth/10f - this.selectionHeight/2f;
        this.collection_x = cam.viewportWidth*3/10f - this.selectionHeight/2f;
        this.booster_x = cam.viewportWidth/2f - this.selectionHeight/2f;
        this.multiplayer_x = cam.viewportWidth*7/10f - this.selectionHeight/2f;
        this.trade_x = cam.viewportWidth*9/10f - this.selectionHeight/2f;
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            x = screen_to_cam_x(x);
            y = screen_to_cam_y(y);
            if(y < this.selectionHeight){
                if (booster_x < x && x < booster_x+selectionHeight){
                    if(!(this instanceof BoosterSelection)){
                        gsm.set(new BoosterSelection(gsm));
                    }
                }else if (collection_x < x && x < collection_x+selectionHeight){
                    if(!(this instanceof CollectionSelection)){
                        gsm.set(new CollectionSelection(gsm));
                    }
//                }else if (deck_x < x && x < deck_x+selectionHeight){
//                    if(!(this instanceof DeckSelection)){
//                        gsm.set(new DeckSelection(gsm));
//                    }
//                }else if (multiplayer_x < x && x < multiplayer_x+selectionHeight){
//                    if(!(this instanceof MultiplayerSelection)){
//                        gsm.set(new MultiplayerSelection(gsm));
//                    }
//                }else if (trade_x < x && x < trade_x+selectionHeight){
//                    if(!(this instanceof TradeSelection)){
//                        gsm.set(new TradeSelection(gsm));
//                    }
                }
            }
        }
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr) {
        //Setup spriteBatch
        sb.setProjectionMatrix(cam.combined);
        //Setup ShapeRenderer
        sr.setProjectionMatrix(cam.combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.WHITE);
        sr.rect(0, 0, cam.viewportWidth, this.selectionHeight);
        sr.end();

        sb.begin();
//        sb.draw(this.deck, deck_x, 0, this.selectionHeight, this.selectionHeight);
        sb.draw(this.collection, collection_x, 0, this.selectionHeight, this.selectionHeight);
        sb.draw(this.booster, booster_x, 0, this.selectionHeight, this.selectionHeight);
//        sb.draw(this.multiplayer, multiplayer_x, 0, this.selectionHeight, this.selectionHeight);
//        sb.draw(this.trade, trade_x, 0, this.selectionHeight, this.selectionHeight);
        sb.end();
    }

    @Override
    public void dispose() {
    }
}
