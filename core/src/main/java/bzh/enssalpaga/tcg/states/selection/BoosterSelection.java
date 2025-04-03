package bzh.enssalpaga.tcg.states.selection;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.states.BoosterMenu;
import bzh.enssalpaga.tcg.utils.Constants;

public class BoosterSelection extends SelectionMenu{
    private final Texture booster_up;
    private final Texture booster_down;
    private final Stage stage;
    private final float booster_down_height;
    private final float booster_up_height;
    private TextField textField;

    public BoosterSelection(GameStateManager gsm) {
        super(gsm);
        this.booster_up = LaunchGame.assetManager.get("booster_up.png", Texture.class);
        this.booster_down = LaunchGame.assetManager.get("booster_down.png", Texture.class);
        booster_down_height = booster_down.getHeight()*cam.viewportWidth/2f/booster_down.getWidth();
        booster_up_height = booster_up.getHeight()*cam.viewportWidth/2f/booster_up.getWidth();

        stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));
        Skin skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);

        if (Constants.BOOSTER_AVAILABLE){
            Button button = new TextButton("Ouvrir un booster", skin);
            button.setHeight(64);
            button.setPosition(cam.viewportWidth/2f - button.getWidth()/2, this.selectionHeight*2);
            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    gsm.set(new BoosterMenu(gsm));
                }
            });
            stage.addActor(button);
        }else{
            Table label_table = new Table();
            label_table.setBounds(cam.viewportWidth/4f, this.selectionHeight*4, cam.viewportWidth/2, booster_down_height+booster_up_height);
            label_table.add(new Label("Vous avez deja", Constants.labelStyle)).row();
            label_table.add(new Label("ouvert un", Constants.labelStyle)).row();
            label_table.add(new Label("booster", Constants.labelStyle)).row();
            label_table.add(new Label("aujourd'hui,", Constants.labelStyle)).row();
            label_table.add(new Label("revenez demain", Constants.labelStyle)).row();
            label_table.add(new Label("pour en ouvrir", Constants.labelStyle)).row();
            label_table.add(new Label("un autre", Constants.labelStyle)).row();
            stage.addActor(label_table);

            if(Gdx.app.getType() == Application.ApplicationType.Desktop){
                textField = new TextField("", skin);
                textField.setMessageText("code");
                textField.setPosition((cam.viewportWidth - textField.getWidth())/2, this.selectionHeight*3);
                stage.addActor(textField);
            }

            Button button = new TextButton("J'ai un code", skin);
            button.setHeight(64);
            button.setPosition(cam.viewportWidth/2f - button.getWidth()/2, this.selectionHeight*2);
            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    if(Gdx.app.getType() == Application.ApplicationType.Desktop){
                        gsm.getConnectionManager().send("booster_code", textField.getText());
                    }else{
                        createTextInput();
                    }
                }
            });
            stage.addActor(button);
        }

        Gdx.input.setInputProcessor(stage);
    }

    private void createTextInput(){
        Input.TextInputListener listener = new Input.TextInputListener() {
            @Override
            public void input(String code) {
                gsm.getConnectionManager().send("booster_code", code);
            }

            @Override
            public void canceled() {

            }
        };
        Gdx.input.getTextInput(listener, "Rentrez votre code", "", "________");
    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr){
        sb.begin();
        sb.draw(this.booster_down, cam.viewportWidth/4f, this.selectionHeight*4, cam.viewportWidth/2f, booster_down_height);
        sb.draw(this.booster_up, cam.viewportWidth/4f, this.selectionHeight*4+booster_down_height,
            cam.viewportWidth/2f, booster_up_height);

        sb.end();

        if(!Constants.BOOSTER_AVAILABLE){
            // Enable blending for transparency
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(1, 1, 1, 3/4f);
            sr.rect(cam.viewportWidth/4f, this.selectionHeight*4, cam.viewportWidth/2f, booster_down_height+booster_up_height);
            sr.end();

            // Disable blending
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        stage.draw();
        super.render(sb, sr);
    }

    @Override
    public void dispose(){
        super.dispose();
        this.stage.dispose();
    }

    @Override
    public void message(String event, String message) {
    }
}
