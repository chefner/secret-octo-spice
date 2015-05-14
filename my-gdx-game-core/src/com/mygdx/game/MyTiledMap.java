package com.mygdx.game;
import com.badlogic.gdx.maps.tiled.*;

public class MyTiledMap extends TiledMap implements TileBasedMap{

	@Override
	public int getWidthInTiles(){
		return ((TiledMapTileLayer)this.getLayers().get(0)).getWidth();
	}

	@Override
	public int getHeightInTiles(){
		return ((TiledMapTileLayer)this.getLayers().get(0)).getHeight();
	}

	@Override
	public void pathFinderVisited(int x, int y){
		// TODO: Implement this method
	}

	@Override
	public boolean blocked(Mover mover, int x, int y){
		// TODO: Implement this method
		return false;
	}

	@Override
	public float getCost(Mover mover, int sx, int sy, int tx, int ty){
		return 1;
	}

}
