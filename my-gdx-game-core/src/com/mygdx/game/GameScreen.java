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
import java.util.*;
import com.badlogic.gdx.utils.*;
import java.io.*;

public class GameScreen implements Screen{

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
	Texture tileButtonTIm;
	Texture tileButtonTIm2;
	TextureRegion mapMarkTexture;
	TextureRegion tBTImRegion;
	TextureRegion tBTImRegion2;
	TextureRegion arrowTexture;
	TextureRegionDrawable tBTRDrawable;
	TextureRegionDrawable tBTRDrawable2;
	int imageRow;
	int imageCol;
	MyTiledMap tiledMap;
	MapLayers mapLayers;
	TiledMapTileLayer tiledMapLayer;
	TextureRegion mapTileSheet;
	TextureRegion[][] mapTileTextures;
	StaticTiledMapTile[] mapTiles;
	TiledMapTileLayer.Cell[] tileCells;
	OrthogonalTiledMapRenderer mapRenderer;
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
	TextButton button;
	TextButton saveButton;
	Dialog dialog;
	String saveName;
	TextField dialogTF;
	long lastTime;
	boolean haveMove;
	Archer player;
	float stateTime;
	Vector3[] path;
	int pathPosition;
	AStarPathFinder pathFinder;
	Path newPath;
	Path arrowPath;
	ArrayList<Character> characters;
	ArrayList<Projectile> projectiles;
	TiledMapTileSet tileset;


	public GameScreen(final MyGdxGame gam, String map){
		projectiles = new ArrayList<Projectile>();
		characters = new ArrayList<Character>();
		game = gam;
		haveCommand = false;
		buttonNum = 0;
		imageRow = 0;
		imageCol = 0;
		pathPosition = 0;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		numTiles = getNumTiles();
		//Gdx.app.log("myapp", "numTiles: " + numTiles);
		//tileHandles = getTileHandles();
		//initTileTextures();
		mapMarkTexture = new TextureRegion(new Texture(Gdx.files.internal("data/mapMarker.png")));
		spriteSheet = new Texture(Gdx.files.internal("data/characters/BODY_male2.png"));	
		textureRegion = new TextureRegion(spriteSheet);
		TextureRegion bowTexture = new TextureRegion(new Texture(Gdx.files.internal("data/weapons/bowTemplate.png")));
		TextureRegion bowDrawnTexture = new TextureRegion(new Texture(Gdx.files.internal("data/weapons/drawnBowTemplate.png")));
		arrowTexture = new TextureRegion(new Texture(Gdx.files.internal("data/weapons/arrowTemplate.png")));
		player = new Archer(textureRegion, bowTexture, bowDrawnTexture);
		characters.add(player);
		characters.add(new Character(textureRegion));
		characters.get(1).setPosition(3, 3);
		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("data/drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("data/rain.mp3"));

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, 8, 8);
		batch = new SpriteBatch();
        batch.enableBlending();
		// create a Rectangle to logically represent the bucket
		bucket = new Rectangle();
		//bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
		//bucket.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		bucket.width = 1;
		bucket.height = 1;
		lastImageTime = TimeUtils.millis();
		loadMap(map);
		pathFinder = new AStarPathFinder(tiledMap, 10, true);
		mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1/16f);
		mapRenderer.setView(camera);
		stage = new Stage();
		InputMultiplexer multi = new InputMultiplexer();
	    CameraInputController camControl = new CameraInputController(camera){
			public boolean touchDown(int x, int y, int p, int b){
				mapTouchPos = new Vector3();
				Gdx.app.log("myapp", "touchDown");
				//Gdx.app.log("myapp", "inputX: " + lastTouchedX + 
				// " inputY: " + lastTouchedY);
				mapTouchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(mapTouchPos);
				//Gdx.app.log("myapp", "touchX: " + Math.round(mapTouchPos.x) + 
				//	" touchY: " + Math.round(mapTouchPos.y));
				if(isOnMap(Math.round(mapTouchPos.x), Math.round(mapTouchPos.y))){
					//path = getPath(Math.round(mapTouchPos.x), Math.round(mapTouchPos.y));
					bucket.x = Math.round(mapTouchPos.x);
					bucket.y = Math.round(mapTouchPos.y);
					Path tempPath = pathFinder.findPath((Mover)player, (int)player.getXPosition(), (int)player.getYPosition(), 
												  Math.round(mapTouchPos.x), Math.round(mapTouchPos.y));
					if(tempPath != null){
						player.setPath(tempPath);
						player.setHasMove(true);
					}
				}
				return true;
			}
			
			public boolean touchDragged(int x, int y, int p){
				Vector3 vector = new Vector3();
				vector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(vector);
				Gdx.app.log("myapp", "touchX: " + mapTouchPos.x + 
					"  touchY: " + mapTouchPos.y);
				Gdx.app.log("myapp", "vectorX: " + vector.x + "  vectorY: " + vector.y);
				Vector3 delta = new Vector3();
				delta.set(camera.position.x + (mapTouchPos.x - vector.x),
						  camera.position.y + (mapTouchPos.y - vector.y), 0);
				camera.position.lerp(delta, 0.5f);
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
		lastTime = TimeUtils.nanoTime();
		haveMove = false;
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
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		mapRenderer.setView(camera);
		batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the bucket and
		// all drops
		mapRenderer.render();
		stateTime += Gdx.graphics.getDeltaTime();
		stage.act();
		stage.draw();
		//tileButtonTable.drawDebug(stage);
		batch.begin();
		for(int i = 0; i < characters.size(); i++){
			if(characters.get(i) != null)
		        characters.get(i).draw(batch, stateTime);
		}
		for(int i = 0; i < projectiles.size(); i++){
			if(projectiles.get(i) != null)
			    projectiles.get(i).draw(batch, stateTime);
		}
		batch.draw(mapMarkTexture, bucket.x, bucket.y, 1, 1);
		batch.end();
		for(int i = 0; i < characters.size(); i++){
			if(characters.get(i) == null)
				continue;
		    if(characters.get(i).hasMove() && characters.get(i).canMove()){
			    float deltaX = characters.get(i).getCurrentStep().getX() - characters.get(i).getXPosition();
			    float deltaY = characters.get(i).getCurrentStep().getY() - characters.get(i).getYPosition();
			    if(deltaY == 1){
				    characters.get(i).changeDirection(0);
			    }
			    else if(deltaY == -1){
			    	characters.get(i).changeDirection(2);
		    	}
		    	else if(deltaX == 1 && deltaY == 0){
		    		characters.get(i).changeDirection(1);
			    }
			    else if(deltaX == -1 && deltaY == 0){
			    	characters.get(i).changeDirection(3);
		    	}
	    		characters.get(i).advance();
	    	}
		}
		
		for(int i = 0; i < projectiles.size(); i++){
			if(projectiles.get(i).hasMove() && projectiles.get(i).canMove()){
		    	if(projectiles.get(i) == null)
		            continue;
		    	if(!projectiles.get(i).advance()){
	    			projectiles.remove(i);
	    		}
			}
		}
		
		for(int t = 0; t < characters.size(); t++){
		    for(int i = 0; i < characters.size(); i++){
				if(i == t)
					continue;
		    	if(characters.get(i) == null)
	    			continue;
	    		if(getDistance(characters.get(t).getXPosition(), characters.get(i).getYPosition(), 
	    					   characters.get(i).getXPosition(), characters.get(i).getYPosition()) <= 6 &&
		    				   characters.get(i).canAttack()){
			    	Gdx.app.log("myapp", "true");
			    	Gdx.app.log("myapp", "characterPosX: " + characters.get(i).getXPosition() + 
		    		    "   characterPosY: " + characters.get(i).getYPosition());
								arrowPath = pathFinder.findPath(player, (int)characters.get(t).getXPosition(),
				    		(int)characters.get(t).getYPosition(), (int)characters.get(i).getXPosition(),
				    		(int)characters.get(i).getYPosition());
			    	if(arrowPath != null){
				    	Gdx.app.log("myapp", "not null");
			            Projectile tempArrow = new Projectile();
				        tempArrow.setPath(arrowPath);
				        tempArrow.setCurrentTexture(arrowTexture);
						tempArrow.setHasMove(true);
				        projectiles.add(tempArrow);
				    }
			    }
	    	}
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

	public void initTileTextures(){
		tileTextureRegions = new TextureRegion[numTiles];
		for(int x = 0; x < numTiles; x++){
			tileTextureRegions[x] = new TextureRegion(new Texture(tileHandles[x]));
		}
	}

	public boolean isOnMap(int x, int y){
		if(((TiledMapTileLayer)(tiledMap.getLayers().get(0))).getCell(x, y) == null)
			return false;
		return true;
	}
	
	private boolean isNextTurn()
	{
		long currentTime = TimeUtils.nanoTime();
		if((currentTime - lastTime) >= 5000000000l){
			lastTime = currentTime;
			return true;
		}
		return false;
	}
	
	public boolean isLegalMove(){
		return true;
	}
	
	public double getDistance(float sx, float sy, float gx, float gy){
		return Math.sqrt((gx - sx) * (gx - sx) + (gy - sy) * (gy - sy));
	}
	
	public void loadMap(String map){
		XmlReader reader = new XmlReader();
		Gdx.app.log("myapp", "map = " + map);
		try
		{
			//XmlReader.Element root = reader.parse(Gdx.files.local(map));
			XmlReader.Element root = reader.parse(Gdx.files.internal(map));
			String orientation = root.getAttribute("orientation");
			int mapWidth = Integer.parseInt(root.getAttribute("width"));
			int mapHeight = Integer.parseInt(root.getAttribute("height"));
			String tileWidth = root.getAttribute("tilewidth");
			String tileHeight = root.getAttribute("tileheight");
			tiledMap = new MyTiledMap();
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
				int y = mapHeight - 1;
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
						y--;
					}
				}
				mapLayers.add(tileLayer);
			}


		}
		catch (IOException e){

		}

	}
	
}
