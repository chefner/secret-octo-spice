package com.mygdx.game;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.tiles.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;
import com.badlogic.gdx.utils.*;
import java.io.*;

public class MapEditorScreen implements Screen{

    final MyGdxGame game;
	Animation animation;
	Texture bucketImage;
	Sound dropSound;
	Music rainMusic;
	SpriteBatch batch;
	OrthographicCamera camera;
	Rectangle bucket;
	Array<Rectangle> raindrops;
	long lastDropTime;
	long lastImageTime;
	TextureRegion textureRegion;
	TextureRegion[][] textureRegionArray;
	Texture spriteSheet;
	Texture mapMarkImage;
	Texture tileButtonTIm;
	Texture tileButtonTIm2;
	TextureRegion mapMarkTexture;
	TextureRegion tBTImRegion;
	TextureRegion tBTImRegion2;
	TextureRegionDrawable tBTRDrawable;
	TextureRegionDrawable tBTRDrawable2;
	int imageRow;
	int imageCol;
	TiledMap tiledMap;
	MapLayers mapLayers;
	TiledMapTileLayer tiledMapLayer;
	TextureRegion mapTileSheet;
	TextureRegion[][] mapTileTextures;
	StaticTiledMapTile[] mapTiles;
	TiledMapTileLayer.Cell[] tileCells;
	OrthogonalTiledMapRenderer mapRenderer;
    private int lastTouchedX;
    private int lastTouchedY;
	Stage stage;
	Vector3 mapTouchPos;
	String textFieldString;
	boolean haveCommand;
	Label label;
	ScrollPane labelScrollPane;
	ScrollPane tileButtonSPane;
	ScrollPane textScrollPane;
	Table tileButtonTable;
    int buttonNum;
	int numTiles;
	FileHandle[] tileHandles;
	Skin skin;
	TextureRegion[] tileTextureRegions;
	int tileMap[][];
	TextButton button;
	TextButton saveButton;
	Dialog dialog;
	String saveName;
	TextField dialogTF;
	TiledMapTileSet tileset;

	
	public MapEditorScreen(final MyGdxGame gam) {
		game = gam;
		haveCommand = false;
		int regionWidth;
		int regionHeight;
		buttonNum = 0;
		imageRow = 0;
		imageCol = 0;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		//numTiles = getNumTiles();
		//Gdx.app.log("myapp", "numTiles: " + numTiles);
		//tileHandles = getTileHandles();
		//initTileTextures();
		//loadMap();
		spriteSheet = new Texture(Gdx.files.internal("data/res_viewer2.png"));
		mapMarkImage = new Texture(Gdx.files.internal("data/tileoutline.png"));
		textureRegion = new TextureRegion(spriteSheet);
		mapMarkTexture = new TextureRegion(mapMarkImage);
		regionWidth = textureRegion.getRegionWidth();
		regionHeight = textureRegion.getRegionHeight();
		textureRegionArray = textureRegion.split(regionWidth/3, regionHeight/4);
		TextureRegion[] frames = new TextureRegion[12];
        int index = 0;
		for(int i = 0; i < 4; i++)
		    for(int p = 0; p < 3; p++)
			    frames[index++] = textureRegionArray[i][p];
	    animation = new Animation(0.025f, frames);
		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("data/drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("data/rain.mp3"));

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, 16, 16);
		batch = new SpriteBatch();

		// create a Rectangle to logically represent the bucket
		bucket = new Rectangle();
		//bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
		//bucket.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		bucket.width = 1;
		bucket.height = 1;

		lastImageTime = TimeUtils.millis();
        loadMap3();
		//buildMap();
		//Gdx.app.log("myapp", "maplayers = " + mapLayers.getCount());
		mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1/32f);
		mapRenderer.setView(camera);
		stage = new Stage();
		InputMultiplexer multi = new InputMultiplexer();
	    CameraInputController camControl = new CameraInputController(camera){
			public boolean touchDown(int x, int y, int p, int b){
				mapTouchPos = new Vector3();
				//Gdx.app.log("myapp", "inputX: " + lastTouchedX + 
				// " inputY: " + lastTouchedY);
				mapTouchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(mapTouchPos);
				//Gdx.app.log("myapp", "touchX: " + Math.round(mapTouchPos.x) + 
				//	" touchY: " + Math.round(mapTouchPos.y));
				bucket.x = Math.round(mapTouchPos.x);
				bucket.y = Math.round(mapTouchPos.y);
				if(buttonNum > 0){
					TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
					cell.setTile(mapTiles[buttonNum - 1]);
					tiledMapLayer.setCell((int)mapTouchPos.x, (int)mapTouchPos.y, cell);
					buttonNum = 0;
				}
				if(!isOnMap((int)mapTouchPos.x, (int)mapTouchPos.y))
					expandMap();
				label.setText("isOnMap: " + isOnMap((int)mapTouchPos.x, (int)mapTouchPos.y));
				camera.position.add(mapTouchPos.x - camera.position.x, mapTouchPos.y - camera.position.y, 0);
				return true;
			}
		};
		multi.addProcessor(stage);
		multi.addProcessor(camControl);
		Gdx.input.setInputProcessor(multi);
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		label = new Label("numTiles" + numTiles, skin);
		label.setWrap(true);
		labelScrollPane = new ScrollPane(label, skin);
		labelScrollPane.setPosition(0, Gdx.graphics.getHeight()-80);
		labelScrollPane.setSize(Gdx.graphics.getWidth(), 80);
		labelScrollPane.setVisible(false);
        TextField textField = new TextField("", skin);
		textField.setTextFieldListener(new TextField.TextFieldListener() {
				public void keyTyped (TextField textField, char key){
					if(key == '\n'){
						textFieldString = textField.getText();
						haveCommand = true;
						label.setText(label.getText() + "\n" + textFieldString);
						labelScrollPane.layout();
						labelScrollPane.setScrollPercentY(100);
						//labelScrollPane.setScrollY(labelScrollPane.getMaxY() + 10);
					}
				}
			});
		textScrollPane = new ScrollPane(textField, skin);
		textScrollPane.setPosition(0, Gdx.graphics.getHeight()-120);
		textScrollPane.setSize(Gdx.graphics.getWidth(), 40); 
		textScrollPane.setVisible(false);
		tileButtonTable = buildButtonTable();
		tileButtonSPane = new ScrollPane(tileButtonTable, skin);
		tileButtonSPane.setPosition(Gdx.graphics.getWidth()-200, 0);
		tileButtonSPane.setSize(200, Gdx.graphics.getHeight()-160);
		tileButtonSPane.setVisible(false);
		stage.addActor(tileButtonSPane);
		stage.addActor(labelScrollPane);
		stage.addActor(textScrollPane);
		button = new TextButton("Click me", skin);
		button.setPosition(Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight() - 150);
		button.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					if(tileButtonSPane.isVisible()){
						tileButtonSPane.setVisible(false);
						textScrollPane.setVisible(false);
						labelScrollPane.setVisible(false);
						saveButton.setVisible(false);
						button.setPosition(Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight() - 150);
					}	
					else{
						tileButtonSPane.setVisible(true);
						textScrollPane.setVisible(true);
						labelScrollPane.setVisible(true);
						saveButton.setVisible(true);
						button.setPosition(Gdx.graphics.getWidth()-250, Gdx.graphics.getHeight() - 150);
					}

				}
			});
		stage.addActor(button); 
		saveButton = new TextButton("Save Map", skin);
		saveButton.setPosition(Gdx.graphics.getWidth()-200, Gdx.graphics.getHeight() - 150);
		saveButton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
                    dialog = new Dialog("Save Game", skin);
					TextField dialogTF = new TextField("Untitled", skin);
					dialog.getContentTable().add(dialogTF);
					TextButton okButton = new TextButton("OK", skin);
					okButton.addListener(new ClickListener(){
							public void clicked(InputEvent event, float x, float y){

							}
					});
				}
			});
		stage.addActor(saveButton);
	}
	
	public MapEditorScreen(final MyGdxGame gam, int[][] map){
		tileMap = map;
		game = gam;
		haveCommand = false;
		int regionWidth;
		int regionHeight;
		buttonNum = 0;
		imageRow = 0;
		imageCol = 0;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		//numTiles = getNumTiles();
		Gdx.app.log("myapp", "numTiles: " + numTiles);
		//tileHandles = getTileHandles();
		//initTileTextures();
		spriteSheet = new Texture(Gdx.files.internal("data/res_viewer2.png"));
		mapMarkImage = new Texture(Gdx.files.internal("data/mapMarker.png"));
		textureRegion = new TextureRegion(spriteSheet);
		mapMarkTexture = new TextureRegion(mapMarkImage);
		regionWidth = textureRegion.getRegionWidth();
		regionHeight = textureRegion.getRegionHeight();
		textureRegionArray = textureRegion.split(regionWidth/3, regionHeight/4);
		TextureRegion[] frames = new TextureRegion[12];
        int index = 0;
		for(int i = 0; i < 4; i++)
		    for(int p = 0; p < 3; p++)
			    frames[index++] = textureRegionArray[i][p];
	    animation = new Animation(0.025f, frames);
		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("data/drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("data/rain.mp3"));

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, 16, 16);
		batch = new SpriteBatch();

		// create a Rectangle to logically represent the bucket
		bucket = new Rectangle();
		//bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
		//bucket.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		bucket.width = 1;
		bucket.height = 1;

		lastImageTime = TimeUtils.millis();
        loadMap3();
		//buildMap();
		//Gdx.app.log("myapp", "maplayers = " + mapLayers.getCount());
		mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1/16f);
		mapRenderer.setView(camera);
		stage = new Stage();
		InputMultiplexer multi = new InputMultiplexer();
	    CameraInputController camControl = new CameraInputController(camera){
			public boolean touchDown(int x, int y, int p, int b){
				mapTouchPos = new Vector3();
				//Gdx.app.log("myapp", "inputX: " + lastTouchedX + 
				// " inputY: " + lastTouchedY);
				mapTouchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(mapTouchPos);
				//Gdx.app.log("myapp", "touchX: " + Math.round(mapTouchPos.x) + 
				//	" touchY: " + Math.round(mapTouchPos.y));
				bucket.x = Math.round(mapTouchPos.x);
				bucket.y = Math.round(mapTouchPos.y);
				if(buttonNum > 0){
					TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
					cell.setTile(mapTiles[buttonNum - 1]);
					tiledMapLayer.setCell((int)mapTouchPos.x, (int)mapTouchPos.y, cell);
					buttonNum = 0;
				}
				if(!isOnMap((int)mapTouchPos.x, (int)mapTouchPos.y))
					expandMap();
				label.setText("isOnMap: " + isOnMap((int)mapTouchPos.x, (int)mapTouchPos.y));
				//camera.position.add(mapTouchPos.x - camera.position.x, mapTouchPos.y - camera.position.y, 0);
				return true;
			}
			
			public boolean touchDragged(int x, int y, int p){
				Vector3 vector = new Vector3();
				vector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(vector);
				//Gdx.app.log("myapp", "touchX: " + Math.round(mapTouchPos.x) + 
				//	" touchY: " + Math.round(mapTouchPos.y));
				bucket.x = Math.round(vector.x);
				bucket.y = Math.round(vector.y);
				if(buttonNum > 0){
					TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
					cell.setTile(mapTiles[buttonNum - 1]);
					tiledMapLayer.setCell((int)mapTouchPos.x, (int)mapTouchPos.y, cell);
					buttonNum = 0;
				}
				if(!isOnMap((int)mapTouchPos.x, (int)mapTouchPos.y))
					expandMap();
				label.setText("isOnMap: " + isOnMap((int)mapTouchPos.x, (int)mapTouchPos.y));
				camera.position.add(mapTouchPos.x - vector.x, mapTouchPos.y - vector.y, 0);
				mapTouchPos.set(vector);
				return true;
			}
			
			public boolean touchUp(int x, int y, int p, int b){
				
				return true;
			}
		};
		multi.addProcessor(stage);
		multi.addProcessor(camControl);
		Gdx.input.setInputProcessor(multi);
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		label = new Label("numTiles" + numTiles, skin);
		label.setWrap(true);
		labelScrollPane = new ScrollPane(label, skin);
		labelScrollPane.setPosition(0, Gdx.graphics.getHeight()-80);
		labelScrollPane.setSize(Gdx.graphics.getWidth(), 80);
		labelScrollPane.setVisible(false);
        TextField textField = new TextField("", skin);
		textField.setTextFieldListener(new TextField.TextFieldListener() {
				public void keyTyped (TextField textField, char key){
					if(key == '\n'){
						textFieldString = textField.getText();
						haveCommand = true;
						label.setText(label.getText() + "\n" + textFieldString);
						labelScrollPane.layout();
						labelScrollPane.setScrollPercentY(100);
						//labelScrollPane.setScrollY(labelScrollPane.getMaxY() + 10);
					}
				}
			});
		textScrollPane = new ScrollPane(textField, skin);
		textScrollPane.setPosition(0, Gdx.graphics.getHeight()-120);
		textScrollPane.setSize(Gdx.graphics.getWidth(), 40); 
		textScrollPane.setVisible(false);
		tileButtonTable = buildButtonTable();
		tileButtonSPane = new ScrollPane(tileButtonTable, skin);
		tileButtonSPane.setPosition(Gdx.graphics.getWidth()-200, 0);
		tileButtonSPane.setSize(200, Gdx.graphics.getHeight()-160);
		tileButtonSPane.setVisible(false);
		stage.addActor(tileButtonSPane);
		stage.addActor(labelScrollPane);
		stage.addActor(textScrollPane);
		button = new TextButton("Click me", skin);
		button.setPosition(Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight() - 150);
		button.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					if(tileButtonSPane.isVisible()){
						tileButtonSPane.setVisible(false);
						textScrollPane.setVisible(false);
						labelScrollPane.setVisible(false);
						saveButton.setVisible(false);
						button.setPosition(Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight() - 150);
					}	
					else{
						tileButtonSPane.setVisible(true);
						textScrollPane.setVisible(true);
						labelScrollPane.setVisible(true);
						saveButton.setVisible(true);
						button.setPosition(Gdx.graphics.getWidth()-250, Gdx.graphics.getHeight() - 150);
					}

				}
			});
		stage.addActor(button);
		saveButton = new TextButton("Save Game", skin);
		saveButton.setVisible(false);
		saveButton.setPosition(Gdx.graphics.getWidth()-400, Gdx.graphics.getHeight() - 150);
		saveButton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
                    dialog = new Dialog("Save Game", skin);
					dialogTF = new TextField("Untitled", skin);
					dialog.getContentTable().add(dialogTF);
					TextButton okButton = new TextButton("OK", skin);
					okButton.addListener(new ClickListener(){
							public void clicked(InputEvent event, float x, float y){
                                saveName = dialogTF.getText();
								saveMap(saveName);
								dialog.hide();
							}
					});
					dialog.button(okButton);
					dialog.show(stage);
				}
			});
		stage.addActor(saveButton);
	}
	
	
	@Override
	public void show()
	{
		// TODO: Implement this method
	}

	@Override
	public void hide()
	{
		// TODO: Implement this method
	}

	@Override
	public void render(float p1) {

		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		mapRenderer.setView(camera);
		batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the bucket and
		// all drops
		mapRenderer.render();
		textureRegion = animation.getKeyFrame(0f, true);
		stage.act();
		stage.draw();
		//tileButtonTable.drawDebug(stage);
		batch.begin();
		//batch.draw(textureRegionArray[imageRow][imageCol], bucket.x, bucket.y);
		batch.draw(mapMarkTexture, bucket.x, bucket.y, 1, 1);
		//for(Rectangle raindrop: raindrops) {
		//batch.draw(dropImage, raindrop.x, raindrop.y);
		//}
		batch.end();

		// process user input
		/*if(Gdx.input.justTouched()){

		 mapTouchPos = new Vector3();
		 //Gdx.app.log("myapp", "inputX: " + lastTouchedX + 
		 // " inputY: " + lastTouchedY);
		 mapTouchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		 camera.unproject(mapTouchPos);
		 //Gdx.app.log("myapp", "touchX: " + Math.round(mapTouchPos.x) + 
		 //	" touchY: " + Math.round(mapTouchPos.y));
		 bucket.x = Math.round(mapTouchPos.x);
		 bucket.y = Math.round(mapTouchPos.y);
		 if(buttonNum > 0){
		 TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		 cell.setTile(mapTiles[buttonNum - 1]);
		 tiledMapLayer.setCell((int)mapTouchPos.x, (int)mapTouchPos.y, cell);
		 buttonNum = 0;
		 }
		 if(!isOnMap((int)mapTouchPos.x, (int)mapTouchPos.y))
		 expandMap();
		 label.setText("isOnMap: " + isOnMap((int)mapTouchPos.x, (int)mapTouchPos.y));
		 camera.position.add(mapTouchPos.x - camera.position.x, mapTouchPos.y - camera.position.y, 0);
		 }
		 else if(Gdx.input.isTouched()) {
		 Gdx.app.log("myapp", "isTouched");
		 mapTouchPos = new Vector3();
		 int x, y;
		 mapTouchPos.set(x = Gdx.input.getX(), y = Gdx.input.getY(), 0);
		 //Gdx.app.log("myapp", "X: " + x + " Y: " + y);
		 camera.unproject(mapTouchPos);
		 bucket.x = Math.round(mapTouchPos.x);
		 bucket.y = Math.round(mapTouchPos.y);
		 Gdx.app.log("myapp", "touchX: " + mapTouchPos.x + 
		 " touchY: " + mapTouchPos.y);
		 //Gdx.app.log("myapp", "camPos: " + camera.position.toString());
		 //camera.position.add(mapTouchPos.x - camera.position.x, mapTouchPos.y - camera.position.y, 0);
		 //Gdx.app.log("myapp", "camPos2: " + camera.position.toString());
		 //camera.position.x = mapTouchPos.x;
		 //camera.position.y = mapTouchPos.y;
		 lastTouchedY = Math.round(mapTouchPos.y);
		 lastTouchedX = Math.round(mapTouchPos.x);

		 }*/

		if(haveCommand){
			haveCommand=false;
		}

	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		//dropImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public int getNumTiles(){
		FileHandle[] files = Gdx.files.internal("data/tiles/").list();
		int numFiles = 0;
		int count = 0;
		if(files == null)
			count = -1;
		for(int i = 0; i < files.length; i++){
			count++;
			if(files[i].name().contains("png")){
				numFiles++;
			}
		}
		return numFiles;
	}

	public FileHandle[] getTileHandles(){
		return Gdx.files.internal("data/tiles/").list();
	}

	public Table buildButtonTable(){
		Table table = new Table(skin);
		table.debug();
		for(int x = 0; x < numTiles; x++){
			tBTRDrawable = new TextureRegionDrawable(tileTextureRegions[x]);
			tBTRDrawable.setMinWidth(90);
			tBTRDrawable.setMinHeight(90);
			ImageButton tileButton = new ImageButton(tBTRDrawable);
			tileButton.setName(Integer.toString(x+1));
			tileButton.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						buttonNum = Integer.parseInt(event.getListenerActor().getName());
					}
				});
			table.add(tileButton).padRight(10).width(90).height(90).padLeft(4);
		}
		return table;
	}

	public void initTileTextures(){
		tileTextureRegions = new TextureRegion[numTiles];
		for(int x = 0; x < numTiles; x++){
			tileTextureRegions[x] = new TextureRegion(new Texture(tileHandles[x]));
		}
	}

	public void buildMap(){
		mapTiles = new StaticTiledMapTile[numTiles];
		tileCells = new TiledMapTileLayer.Cell[numTiles];
		for(int i = 0; i < numTiles; i++){
			mapTiles[i] = new StaticTiledMapTile(tileTextureRegions[i]);
			mapTiles[i].setId(i);
			tileCells[i] = new TiledMapTileLayer.Cell();
			tileCells[i].setTile(mapTiles[i]);

		}
		tiledMap = new TiledMap();
		mapLayers = tiledMap.getLayers();
		tiledMapLayer = new TiledMapTileLayer(tileMap.length, tileMap.length, 32, 32);
		for(int i = 0; i < tileMap.length; i++){
			for(int t = 0; t < tileMap.length; t++){
				int x = 0;
				while(tileCells[x].getTile().getId() != tileMap[i][t])
					x++;
				tiledMapLayer.setCell(t, i, tileCells[x]);
			}
		}
		mapLayers.add(tiledMapLayer);
	}

	public void saveMap(String name){
		FileHandle out = Gdx.files.local("maps/" + name + ".txt");
		if(!out.exists()){
			try{
				out.file().createNewFile();
			}
			catch(Exception e){

			}
		}
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0); 
		out.writeString("", false);
		for(int y = 0; y < layer.getHeight(); y++){
			for(int x = 0; x < layer.getWidth(); x++){
				out.writeString(Integer.toString(layer.getCell(x, y).getTile().getId()) + ";", true);
			}
			if(y < layer.getHeight() - 1)
			    out.writeString("\n", true);
		}
    }
	
	public void loadMap2(){
		tiledMap = new TmxMapLoader().load("data/tiles/map.tmx");
	}
	
	public void loadMap3(){
		XmlReader reader = new XmlReader();
		try
		{
			XmlReader.Element root = reader.parse(Gdx.files.internal("data/tiles/map.tmx"));
			String orientation = root.getAttribute("orientation");
			int mapWidth = Integer.parseInt(root.getAttribute("width"));
			int mapHeight = Integer.parseInt(root.getAttribute("height"));
			String tileWidth = root.getAttribute("tilewidth");
			String tileHeight = root.getAttribute("tileheight");
			tiledMap = new TiledMap();
			MapLayers mapLayers = tiledMap.getLayers();	
			//Gdx.app.log("myapp", root.toString());
			Array<XmlReader.Element> tilesets = root.getChildrenByNameRecursively("tileset");
			tileset = new TiledMapTileSet();
			for(int i = 0; i < tilesets.size; i++){
				if(tilesets.get(i) != null){
					XmlReader.Element currentSet = tilesets.get(i);
					XmlReader.Element image = currentSet.getChildByName("image");
					String firstgid = currentSet.getAttribute("firstgid");
					String name = currentSet.getAttribute("name");
					String tilewidth = currentSet.getAttribute("tilewidth");
					String tileheight = currentSet.getAttribute("tileheight");
					String source = image.getAttribute("source");
					String width = image.getAttribute("width");
					String height = image.getAttribute("height");
					TextureRegion[][] tiles = new TextureRegion(new Texture(
					    Gdx.files.internal("data/tiles/" + source))).split(
						Integer.parseInt(tilewidth), Integer.parseInt(tileheight));
					int gid = Integer.parseInt(firstgid);
					for(int r = 0; r < tiles.length; r++){
						for(int c = 0; c < tiles[r].length; c++){
							tileset.putTile(gid, new StaticTiledMapTile(tiles[r][c]));
							gid++;
						}
					}
				}
			}
			tiledMap.getTileSets().addTileSet(tileset);
			Array<XmlReader.Element> layers = root.getChildrenByNameRecursively("layer");
			for(int i = 0; i < layers.size; i++){
				TiledMapTileLayer tileLayer = new TiledMapTileLayer(mapWidth, mapHeight, 
				    Integer.parseInt(tileWidth), Integer.parseInt(tileHeight));
				Array<XmlReader.Element> tiles = layers.get(i).getChildByName("data").getChildrenByNameRecursively("tile");
				int y = 0;
				int x = 0;
				//Gdx.app.log("myapp", "tiles.size = " + tiles.size);
				for(int s = 0; s < tiles.size; s++){
					TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
					cell.setTile(tileset.getTile(Integer.parseInt(tiles.get(s).getAttribute("gid"))));
					//Gdx.app.log("myapp", "s = " + s + "  x = " + x + "  y = " + y);
					tileLayer.setCell(x, y, cell);
					if(x < mapWidth - 1){
						x++;
					}
					else{
						x = 0;
						y++;
					}
				}
				mapLayers.add(tileLayer);
			}
			
			
		}
		catch (IOException e){
			
		}
		
	}

	public boolean isOnMap(int x, int y){
		if(tiledMapLayer.getCell(x, y) == null)
			return false;
		return true;
	}

	public void expandMap(){
		int height = tiledMapLayer.getHeight();
		TiledMapTileLayer temp = new TiledMapTileLayer(height+2, height+2, 16, 16);
		for(int x = 0; x < height; x++){
			for(int y = 0; y < height; y++){
				temp.setCell(x+1, y+1, tiledMapLayer.getCell(x, y));
			}
		}
		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		cell.setTile(mapTiles[0]);
		for(int i = 0; i < height + 2; i++){
		    temp.setCell(0, i, cell);
			temp.setCell(i, 0, cell);
			temp.setCell(i, height+1, cell);
			temp.setCell(height+1, i, cell);
		}
		tiledMap.getLayers().remove(0);
		tiledMapLayer = temp;
		tiledMap.getLayers().add(tiledMapLayer);
	}
}
