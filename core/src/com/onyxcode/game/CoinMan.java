package com.onyxcode.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background ;
	Texture[] man ;
	Texture dizzy ;
	int manState = 0 ;
	int pause = 0 ;
	float gravity = 2f ;
	float velocity = 0 ;
	int manY ;
	ArrayList<Integer> conXs ;
	ArrayList<Integer> conYs ;
	ArrayList<Integer> bombXs ;
	ArrayList<Integer> bombYs ;
	ArrayList<Rectangle> coinRectangle ;
	ArrayList<Rectangle> bombRectangle ;
	Texture coin ;
	int coinCount ;
	Texture bomb ;
	int bombCount ;
	int getBombCount ;
	int getCoinCount ;
	Rectangle manRectangle ;
	int score ;
	BitmapFont scoreFont ;
	int gameState ;
	int gameOverTime ;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png") ;
		man = new Texture[4] ;
		man[0] = new Texture("frame-1.png") ;
		man[1] = new Texture("frame-2.png") ;
		man[2] = new Texture("frame-3.png") ;
		man[3] = new Texture("frame-4.png") ;
		dizzy = new Texture("dizzy-1.png") ;
		manY = Gdx.graphics.getHeight()/2 ;
		coin = new Texture("coin.png") ;
		bomb = new Texture("bomb.png") ;
		conXs = new ArrayList<>() ;
		conYs = new ArrayList<>() ;
		bombXs = new ArrayList<>() ;
		bombYs = new ArrayList<>() ;
		coinRectangle = new ArrayList<>() ;
		bombRectangle = new ArrayList<>() ;
		getBombCount = 250 ;
		getCoinCount = 100 ;
		manRectangle = new Rectangle() ;
		score = 0 ;
		scoreFont = new BitmapFont() ;
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().scale(10);
		gameState = 0 ;
		gameOverTime = 0 ;
	}

	public void makeCoin(){
		float height = (float) (Math.random() * Gdx.graphics.getHeight());
		conYs.add((int) height) ;
		conXs.add( Gdx.graphics.getWidth()) ;
	}

	public void makeBomb(){
		float height = (float) (Math.random() * Gdx.graphics.getHeight());
		bombYs.add((int) height) ;
		bombXs.add( Gdx.graphics.getWidth()) ;
	}

	@Override
	public void render () {
		batch.begin();
		//background
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if (gameState == 1){
			// GAme is Live
			//Bomb
			if (bombCount < getBombCount){
				bombCount++ ;
			}else{
				bombCount = 0 ;
				getBombCount = (int) (Math.random()*200 + 50) ;
				makeBomb();
			}

			bombRectangle.clear();
			for(int i = 0; i < bombXs.size(); i++){
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i) - 12) ;
				bombRectangle.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight())) ;
			}

			//Coin
			if (coinCount < getCoinCount){
				coinCount++ ;
			}else{
				coinCount = 0 ;
				getCoinCount = (int) (Math.random()*50 + 30);
				makeCoin();
			}

			coinRectangle.clear();
			for(int i = 0; i < conXs.size(); i++){
				batch.draw(coin, conXs.get(i), conYs.get(i));
				conXs.set(i, conXs.get(i) - 8) ;
				coinRectangle.add(new Rectangle(conXs.get(i),conYs.get(i),coin.getWidth(),coin.getHeight())) ;
			}


			//Character's Physics
			if (Gdx.input.justTouched()){
				velocity = -30f ;
			}

			if (pause < 4){
				pause++ ;
			}else {
				pause = 0 ;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity ;
			manY -= velocity ;
			if (manY <= 0){
				manY = 0 ;
			}


		}else if (gameState == 0){
			//waiting to start
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		}else if (gameState == 2){
			if (gameOverTime < 50){
				gameOverTime++ ;
			}else {
				//game over
				if (Gdx.input.justTouched()) {
					gameState = 1;
					//manY = Gdx.graphics.getHeight() / 2 ;
					score = 0;
					velocity = 0;
					conYs.clear();
					conXs.clear();
					coinRectangle.clear();
					coinCount = 0;
					bombYs.clear();
					bombXs.clear();
					bombRectangle.clear();
					bombCount = 0;
					gameOverTime = 0 ;
				}
			}
		}



		if (gameState == 2) {
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}
		manRectangle.set(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY,man[manState].getWidth(),man[manState].getHeight()) ;

		// Intersecting conditions

		//For Coins
		for (int i = 0; i < coinRectangle.size(); i++){
			if (Intersector.overlaps(manRectangle,coinRectangle.get(i))){
				score++ ;
				coinRectangle.remove(i) ;
				conXs.remove(i) ;
				conYs.remove(i) ;
				break;

			}
		}

		//For Bombs
		for (int i = 0; i < bombRectangle.size(); i++){
			if (Intersector.overlaps(manRectangle,bombRectangle.get(i))){
				gameState = 2 ;

			}
		}

		//score
		scoreFont.draw(batch,String.valueOf(score),100,200) ;

		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
