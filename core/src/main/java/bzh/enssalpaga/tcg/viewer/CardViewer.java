package bzh.enssalpaga.tcg.viewer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.handlers.Card;
import bzh.enssalpaga.tcg.utils.Constants;
import bzh.enssalpaga.tcg.utils.Functions;

public class CardViewer {
    private final Stage stage;
    private final Stack stack;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private Label.LabelStyle style;

    public CardViewer(Stage stage, float x, float y, float width, float height){
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) height/8;
        BitmapFont font = Constants.generator.generateFont(parameter);
        style = new Label.LabelStyle(font, Color.BLACK);

        this.stage = stage;
        this.stack = new Stack();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void set(Card card, ClickListener clickListener){
        stack.clear();
        stack.add(new Image(LaunchGame.assetManager.get(Functions.getPath(card.getRarity(), card.getId()), Texture.class)));
        stack.add(new Image(LaunchGame.assetManager.get("cards/r" + card.getRarity() + ".png", Texture.class)));
        stack.setBounds(x, y, width, height);
        stack.addListener(clickListener);
        addHp(stack, card, width, height, style);
        stage.addActor(stack);
    }

    public void set(){
        stack.clear();
        Image image = new Image(LaunchGame.assetManager.get("cards/none.png", Texture.class));
        stack.add(image);
        stack.setBounds(x, y, width, height);
        stage.addActor(stack);
    }

    public static void addHp(Stack stack, Card card, float width, float height, Label.LabelStyle style){
        Image image = new Image(LaunchGame.assetManager.get("cards/hp.png", Texture.class));
        stack.add(image);

        Table label_table = new Table();
        label_table.right();
        label_table.top();
        Label label = new Label(String.valueOf(card.getHp()), style);
        label_table.add(label).height(label.getHeight()-height/16).width(label.getWidth()-width/16);

        stack.add(label_table);
    }

    public void dispose(){
        style.font.dispose();
    }
}
