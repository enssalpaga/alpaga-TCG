package bzh.enssalpaga.tcg.viewer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.List;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.handlers.Card;
import bzh.enssalpaga.tcg.utils.Functions;

public class ZoomViewer {
    private final Stage stage;
    private final Stage zoom_stage;
    private final Table table;

    private final float card_width;
    private final float card_height;
    private boolean just_touched;
    private boolean disable;

    public ZoomViewer(Stage stage){
        this.stage = stage;
        this.zoom_stage = new Stage(new ExtendViewport(stage.getWidth(), 0, stage.getCamera()));
        this.just_touched = false;
        this.disable = false;

        Texture texture = LaunchGame.assetManager.get("cards/none.png", Texture.class);
        card_width = stage.getWidth()*3/4;
        card_height = card_width*texture.getHeight()/texture.getWidth();

        table = new Table();
        table.setFillParent(true);
        zoom_stage.addActor(table);
    }

    public void enable(Card card, String text, ClickListener clickListener){
        if(text == null){
            enable(card);
        }else{
            Skin skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);
            enable(card.getRarity(), card.getId());

            Button button = new TextButton(text, skin);
            button.addListener(clickListener);
            table.add(button).height(64);
        }
    }

    public void enable(Card card, List<String> text, List<ClickListener> clickListener){
        if(text == null){
            enable(card);
        }else{
            Skin skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);
            enable(card.getRarity(), card.getId());

            Table button_table = new Table();
            for(int i = 0; i < text.size(); i++){
                Button button = new TextButton(text.get(i), skin);
                button.addListener(clickListener.get(i));
                button_table.add(button).height(64).padLeft(card_width/16).padRight(card_width/16);
            }
            table.add(button_table);
        }
    }

    public void enable(Card card){
        enable(card.getRarity(), card.getId());
    }

    public void enable(int rarity, int card){
        Gdx.input.setInputProcessor(zoom_stage);

        Stack stack = new Stack();
        Texture zoom_card = LaunchGame.assetManager.get(Functions.getPath(rarity, card), Texture.class);
        Texture zoom_rarity = LaunchGame.assetManager.get("cards/r" + rarity + ".png", Texture.class);
        stack.add(new Image(zoom_card));
        stack.add(new Image(zoom_rarity));
        table.add(stack).width(card_width).height(card_height).row();

//        Button button = new TextButton("Finir le tour", skin);
//        button.addListener(new ClickListener(){
//            @Override
//            public void clicked(InputEvent event, float x, float y){
//                System.out.println("c'est nul");
//            }
//        });
        just_touched = false;
        disable = false;
    }

    private void disable(){
        Gdx.input.setInputProcessor(stage);
        table.clear();
    }

    public void handleInput(){
        if(Gdx.input.getInputProcessor() == zoom_stage){
            if(just_touched){
                if(!Gdx.input.isTouched()){
                    if(disable){
                        disable();
                    }else{
                        disable = true;
                    }
                }
            }else if(Gdx.input.justTouched()) {
                this.just_touched = true;
            }
        }
    }

    public void render(ShapeRenderer sr){
        if(Gdx.input.getInputProcessor() == zoom_stage){
            // Enable blending for transparency
            Gdx.gl.glEnable(GL20.GL_BLEND);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(0, 0, 0, 3/4f);
            sr.rect(0, 0, stage.getWidth(), stage.getHeight());
            sr.end();
            // Disable blending
            Gdx.gl.glDisable(GL20.GL_BLEND);

            zoom_stage.draw();
//            sb.begin();
//            sb.draw(zoom_card, stage.getWidth()/8, (stage.getHeight()-card_height)/2, card_width, card_height);
//            sb.draw(zoom_rarity, stage.getWidth()/8, (stage.getHeight()-card_height)/2, card_width, card_height);
//            sb.end();
        }
    }

}
