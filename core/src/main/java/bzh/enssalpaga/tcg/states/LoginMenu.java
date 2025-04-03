package bzh.enssalpaga.tcg.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.handlers.GameStateManager;

public class LoginMenu extends GameState{
    private Stage stage;
    private Skin skin;
    private TextField textField;
    private Button button;
    private Label label;

    private String login;

    public LoginMenu(GameStateManager gsm) {
        super(gsm);
        createTextInput("Rentrez votre identifiant ENSSAT", "adresse mail sans \"@enssat.fr\"");
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr) {
        if (textField == null){
            stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));
            skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);
            textField = new TextField("", skin);
            textField.setMessageText("login ENSSAT");
            textField.setPosition(30, cam.viewportHeight - 64);
            stage.addActor(textField);

            button = new TextButton("ok", skin);
            button.setPosition(200, cam.viewportHeight - 64);
            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    sendInput(textField.getText());
                }
            });
            stage.addActor(button);

            label = new Label("Rentrez votre identifiant ENSSAT", skin);
            label.setPosition(30, cam.viewportHeight - 32);
            stage.addActor(label);

            Gdx.input.setInputProcessor(stage);
        }

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void message(String event, String message) {

    }

    private void sendInput(String text){
        if (login == null){
            login = text;

            Gdx.input.closeTextInputField(false);
            createTextInput("Saisissez le code reçu par mail", "________");

            label.setText("Saisissez le code reçu par mail");

            textField.setMessageText("________");
            textField.setText("");

            gsm.getConnectionManager().send("new_device", text);
        }else{
            gsm.getConnectionManager().send("mail_code", login + "@" + text);
        }
    }

    private void createTextInput(String title, String hint){
        Input.TextInputListener listener = new Input.TextInputListener() {
            @Override
            public void input(String text) {
                sendInput(text);
            }

            @Override
            public void canceled() {

            }
        };
        Gdx.input.getTextInput(listener, title, "", hint);
    }
}
