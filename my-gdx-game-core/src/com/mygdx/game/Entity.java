package com.mygdx.game;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.g2d.*;

public class Entity implements Mover{
	
	Rectangle rectangle;
	
	double movement;
	long lastTime;
	TextureRegion currentTexture;
	boolean hasMove;
	Path currentPath;
	int pathPosition;
	
	Entity(){
		rectangle = new Rectangle();
		rectangle.width = 1;
		rectangle.height = 1;
		lastTime = TimeUtils.nanoTime();
		movement = 1;
		hasMove = false;
	}
	
	public boolean canMove()
	{
	    long current = TimeUtils.nanoTime();
		if(current - lastTime >= movement * 1000000000l){
			lastTime = current;
		    return true;
		}
		return false;
	}
	
	public void setHasMove(boolean bool){
		hasMove = bool;
	}
	
	public boolean hasMove(){
		return hasMove;
	}

	public double getMovement(){
		return movement;
	}
	
	public void setMovement(double move){
		movement = move;
	}
	
	public void setPath(Path path){
		currentPath = path;
		pathPosition = 0;
	}
	
	public Path.Step getCurrentStep(){
		return currentPath.getStep(pathPosition);
	}
	
	public boolean advance(){
		if(currentPath == null)
			return false;
		setPosition(currentPath.getStep(pathPosition).getX(),
					currentPath.getStep(pathPosition).getY());
		pathPosition++;
		if(pathPosition == currentPath.getLength()){
			pathPosition = 0;
			currentPath = null;
			hasMove = false;
		}
		return true;
	}

	//public void draw(SpriteBatch batch, float stateTime){
	//	batch.draw(getCurrentAnimation(stateTime), rectangle.x, rectangle.y,
	//			   1, 1);
	//}

	public void setPosition(float x, float y){
		rectangle.x = x;
		rectangle.y = y;
	}

	public float getXPosition(){
		return rectangle.x;
	}

	public float getYPosition(){
		return rectangle.y;
	}
	
	public void setCurrentTexture(TextureRegion texture){
		currentTexture = texture;
	}
	
	public void draw(SpriteBatch batch, float stateTime){
		batch.draw(currentTexture, rectangle.x, rectangle.y,
				   1, 1);
	}	
}
