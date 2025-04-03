package bzh.enssalpaga.tcg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.states.LoadingScreen;
import bzh.enssalpaga.tcg.utils.Constants;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class LaunchGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shape;
    private GameStateManager gsm;

    //Asset manager
    public static AssetManager assetManager;

    //hudCam
    public static OrthographicCamera hudCam;

    @Override
    public void create() {
        Constants.VIEWPORT_HEIGHT = Gdx.graphics.getHeight()*Constants.VIEWPORT_WIDTH/Gdx.graphics.getWidth();
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        gsm = new GameStateManager();

        //AssetManager
        assetManager = new AssetManager();

        //setup hudCam
        hudCam = new OrthographicCamera();
        hudCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        gsm.push(new LoadingScreen(gsm));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(4/5f, 5/6f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gsm.render(batch, shape);
        gsm.update(Gdx.graphics.getDeltaTime());

        //Mettre à jour état des touches
        gsm.handleInput();
    }

    @Override
    public void dispose() {
        Constants.font.dispose();
        batch.dispose();
        shape.dispose();
        System.exit(0);
    }
}
