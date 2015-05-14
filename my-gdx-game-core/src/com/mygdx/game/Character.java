package com.mygdx.game;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.*;

public class Character extends Entity{
	Animation downAnimation;
	Animation upAnimation;
	Animation leftAnimation;
	Animation rightAnimation;
	int maxHP;
	int curHP;
	int atk;
	int atkRange;
	int direction;
	double atkRate;
	
	public Character(TextureRegion textureRegion){
		super();
		TextureRegion[][] textureRegionArray;
		atkRange = 10;
		maxHP = 10;
		curHP = 10;
		atk = 1;
		direction = 0;
		atkRate = 2;
		textureRegionArray = textureRegion.split(textureRegion.getRegionWidth()/9, textureRegion.getRegionHeight()/4);
		TextureRegion[] frames = new TextureRegion[9];
        int index = 0;
		for(int i = 0; i < 4; i++){
			index = 0;
		    for(int p = 0; p < 9; p++){
			    frames[index++] = textureRegionArray[i][p];
			}
			if(i == 0){
				upAnimation = new Animation(1f, frames);
			}
			else if(i == 1){
				rightAnimation = new Animation(1f, frames);
			}
			else if(i == 2){
				downAnimation = new Animation(1f, frames);
			}
			else if(i == 3){
				leftAnimation = new Animation(1f, frames);
			}
			frames = new TextureRegion[9];
		}
	    
	}

	public TextureRegion getCurrentAnimation(float stateTime){
		if(direction == 0){
			return upAnimation.getKeyFrame(stateTime, true);
		}
		else if(direction == 1){
			return rightAnimation.getKeyFrame(stateTime, true);
		}
		else if(direction == 2){
			return downAnimation.getKeyFrame(stateTime, true);
		}
		else if(direction == 3){
			return leftAnimation.getKeyFrame(stateTime, true);
		}
		return null;
	}
	
	public void changeDirection(int d){
		direction = d;
	}
	
	public void draw(SpriteBatch batch, float stateTime){
		batch.draw(getCurrentAnimation(stateTime), rectangle.x, rectangle.y,
		    1, 1);
	}		
	
	public boolean canAttack()
	{
	    long current = TimeUtils.nanoTime();
		if(current - lastTime >= atkRate * 1000000000l){
			lastTime = current;
		    return true;
		}
		return false;
	}
}
