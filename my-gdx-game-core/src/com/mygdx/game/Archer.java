package com.mygdx.game;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;

public class Archer extends Character
{
	
	TextureRegion bowTexture;
	TextureRegion bowDrawnTexture;
	Rectangle bowRectangle;
	boolean attacking;
	
	Archer(TextureRegion texture, TextureRegion bow, TextureRegion bowDrawn){
		super(texture);
		attacking = false;
		bowTexture = bow;
		bowDrawnTexture = bowDrawn;
		bowRectangle = new Rectangle();
		bowRectangle.width = 1;
		bowRectangle.height = 1;
		
	}
	
	public void draw(SpriteBatch batch, float stateTime){
		super.draw(batch, stateTime);
		if(attacking){
			batch.draw(bowDrawnTexture, bowRectangle.x, bowRectangle.y, 1, 1);
		}
		else{
			batch.draw(bowTexture, bowRectangle.x, bowRectangle.y, 1, 1);
		}
	}

	@Override
	public void setPosition(float x, float y)
	{
		super.setPosition(x, y);
		bowRectangle.x = rectangle.x + 0.2f;
		bowRectangle.y = rectangle.y - 0.2f;
	}
	
	public void setAttacking(boolean bool){
		attacking = bool;
	}
}
