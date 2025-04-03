package bzh.enssalpaga.tcg.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.utils.Constants;

public abstract class GameState {
    protected OrthographicCamera cam;
    protected GameStateManager gsm;

    public GameState(GameStateManager gsm){
        this.gsm = gsm;
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
    }

    protected float screen_to_cam_x(float stage_x){
        return stage_x*cam.viewportWidth/Gdx.graphics.getWidth();
    }
    protected float screen_to_cam_y(float stage_y){
        return cam.viewportHeight - stage_y*cam.viewportHeight/Gdx.graphics.getHeight();
    }

    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb, ShapeRenderer sr);
    public abstract void dispose();
    public abstract void message(String event, String message);
}
