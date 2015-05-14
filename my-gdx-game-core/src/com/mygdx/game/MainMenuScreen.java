package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.*;

public class MainMenuScreen implements Screen
{
	
	final MyGdxGame game;
	
	OrthographicCamera camera;
	Stage stage;
	Skin skin;
	TextButton mapButton;
	TextButton gameButton;
	TextButton exitButton;
	Dialog dialog;
	String mapChoice;
	
	public MainMenuScreen(final MyGdxGame gam){
		game = gam;
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		gameButton = new TextButton("New Game", skin);
		gameButton.setPosition(Gdx.graphics.getWidth()/2 - 200, Gdx.graphics.getHeight()/2 + 150);
		gameButton.setSize(400, 100);
		gameButton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					dialog = new Dialog("Load Map", skin);
					List<String> list = new List<String>(skin);
					FileHandle[] mapHandles = getMapHandles();
					String[] handles = new String[mapHandles.length];
					//Gdx.app.log("myapp", "handleLength: " + mapHandles.length);
					for(int i = 0; i < mapHandles.length; i++){
						handles[i] = mapHandles[i].path();
						//Gdx.app.log("myapp", "mapHandle: " + handles[i]);
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
								//Gdx.app.log("myapp", mapChoice);
                                dialog.hide();
								game.setScreen(new GameScreen(game, mapChoice));
							}
						});
					dialog.button(okButton);
					dialog.setSize(500, 500);
					dialog.show(stage);
				}
			});
		stage.addActor(gameButton);
		exitButton = new TextButton("Quit", skin);
		exitButton.setPosition(Gdx.graphics.getWidth()/2 - 200, Gdx.graphics.getHeight()/2 - 250);
		exitButton.setSize(400, 100);
		exitButton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
                    
				}
			});
		stage.addActor(exitButton);
		mapButton = new TextButton("Map Editor", skin);
		mapButton.setPosition(Gdx.graphics.getWidth()/2 - 200, Gdx.graphics.getHeight()/2 - 50);
		mapButton.setSize(400, 100);
		mapButton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
                    game.setScreen(new MapMenuScreen(game));
					dispose();
				}
			});
		stage.addActor(mapButton);
	}

	@Override
	public void render(float p1)
	{
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		game.font.draw(game.batch, "Welcome to MyGame!!", 100, 150);
		game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
		game.batch.end();
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

	public FileHandle[] getMapHandles(){
		//Gdx.app.log("myapp", Gdx.files.local("maps/").list().toString());
		Gdx.app.log("myapp", Gdx.files.internal("data/maps/").list().toString());
		//return Gdx.files.local("maps/").list();
		return Gdx.files.internal("data/maps/").list();
	}
	
}
