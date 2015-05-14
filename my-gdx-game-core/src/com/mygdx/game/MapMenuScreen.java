package com.mygdx.game;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.*;

public class MapMenuScreen implements Screen{
	final MyGdxGame game;

	OrthographicCamera camera;
	Stage stage;
	Skin skin;
	TextButton newButton;
	TextButton loadButton;
	TextButton backButton;
	String mapChoice;
	Dialog dialog;

	public MapMenuScreen(final MyGdxGame gam){
		game = gam;
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		newButton = new TextButton("New Map", skin);
		newButton.setPosition(Gdx.graphics.getWidth()/2 - 200, Gdx.graphics.getHeight()/2 + 150);
		newButton.setSize(400, 100);
		newButton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					int[][] map = new int[1][1];
					map[0][0] = 1;
                    game.setScreen(new MapEditorScreen(game, map));
				}
			});
		stage.addActor(newButton);
		backButton = new TextButton("Back", skin);
		backButton.setPosition(Gdx.graphics.getWidth()/2 - 200, Gdx.graphics.getHeight()/2 - 250);
		backButton.setSize(400, 100);
		backButton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){

				}
			});
		stage.addActor(backButton);
		loadButton = new TextButton("Load Map", skin);
		loadButton.setPosition(Gdx.graphics.getWidth()/2 - 200, Gdx.graphics.getHeight()/2 - 50);
		loadButton.setSize(400, 100);
		loadButton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
                    dialog = new Dialog("Load Map", skin);
					List<String> list = new List<String>(skin);
					FileHandle[] mapHandles = getMapHandles();
					String[] handles = new String[mapHandles.length];
					Gdx.app.log("myapp", "handleLength: " + mapHandles.length);
					for(int i = 0; i < mapHandles.length; i++){
						handles[i] = mapHandles[i].path();
						Gdx.app.log("myapp", "mapHandle: " + handles[i]);
					}
					list.setItems(handles);
					list.addListener(new ChangeListener(){

							@Override
							public void changed(ChangeListener.ChangeEvent p1, Actor p2){
								mapChoice = ((List<String>) p2).getSelected();
							}

					});
					list.setSize(400,400);
					ScrollPane scrollPane = new ScrollPane(list, skin);
					scrollPane.setSize(400,400);
					dialog.getContentTable().add(scrollPane);
				    TextButton okButton = new TextButton("OK", skin);
					okButton.addListener(new ClickListener(){
							public void clicked(InputEvent event, float x, float y){
								Gdx.app.log("myapp", mapChoice);
                                dialog.hide();
								game.setScreen(new MapEditorScreen(game, loadMap(mapChoice)));
							}
					});
					dialog.button(okButton);
					dialog.setSize(500, 500);
					dialog.show(stage);
					
				}
			});
		stage.addActor(loadButton);
	}

	@Override
	public void render(float p1)
	{
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		stage.act();
		stage.draw();

		if(Gdx.input.isTouched()){

		}

	}

	@Override
	public void resize(int p1, int p2)
	{
		// TODO: Implement this method
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
	public void pause()
	{
		// TODO: Implement this method
	}

	@Override
	public void resume()
	{
		// TODO: Implement this method
	}

	@Override
	public void dispose()
	{
		// TODO: Implement this method
	}
	
	public int[][] loadMap(String mapPath){
		int[][] tileMap;
		try{
		    FileHandle in = Gdx.files.local(mapPath);
		    String inString = in.readString();
	    	String[] rows = inString.split("\n");
		    tileMap = new int[rows.length][rows.length];
		    for(int x = 0; x < rows.length; x++){
			    Gdx.app.log("myapp", "rows: " + rows[x]);
			    String[] cols = rows[x].split(";");
			    Gdx.app.log("myapp", "col length: " + cols.length);
			    for(int y = 0; y < cols.length; y++){
				    Gdx.app.log("myapp", "cols: " + cols[y]);
				    tileMap[x][y] = Integer.parseInt(cols[y]);
			    }	
		    }
		}
		catch(Exception e){
			tileMap = new int[1][1];
			tileMap[0][0] = 1;
		}
		return tileMap;
	}
	
	public FileHandle[] getMapHandles(){
		Gdx.app.log("myapp", Gdx.files.local("maps/").list().toString());
		return Gdx.files.local("maps/").list();
	}
}
