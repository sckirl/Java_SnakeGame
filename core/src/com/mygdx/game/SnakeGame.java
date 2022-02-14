package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static java.lang.Thread.sleep;

public class SnakeGame extends ApplicationAdapter {
	private Player snake;
	private Food snakeFood;

	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	@Override
	public void create () {
		// the snakeBody should be an Array class so the whole snake coordinates
		// (counting from head to tail) can be saved into memory.

		// snake variable contains ShapeRenderer, this is because the snake doesn't
		// have a sprite and the way to render it is to draw it as a rectangle with color.
		snake = new Player();
		snake.spawnSnake();

		snakeFood = new Food();
		snakeFood.spawnFood();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 500, 500);
	}

	private class Player{
		private Array<Rectangle> body = new Array<Rectangle>();
		private ShapeRenderer length = new ShapeRenderer();
		int[] movements = {20, 0};
		int bodyLength = 1;
		final int SCALE = 20;
		final int VELOCITY = 20;

		private void spawnSnake(){
			// Spawn snake at random place in the screen.
			Rectangle head = new Rectangle();

			// I can't seem to find a replacement of random(start, stop, STEP) like in python. So I use round solution;
			// which divide random num with SCALE, round the num, and multiply the round number with SCALE.
			// similar to this: https://stackoverflow.com/a/19946165
			head.x = MathUtils.round((float)MathUtils.random(0, 500-10) / SCALE);
			head.y = MathUtils.round((float)MathUtils.random(0, 500-10) / SCALE);
			// multiply it back to SCALE
			head.x = head.x * SCALE;
			head.y = head.y * SCALE;

			head.width = head.height = 20;
			body.add(head);
		}

		private void setMove(int[] move) {
			Rectangle newHead = new Rectangle();
			// keep adding the 'x' and the 'y' to make the snake 'move'
			// the movements are from the 'move' array, getting the array from user input
			newHead.x = body.first().x + move[0];
			newHead.y = body.first().y + move[1];
			newHead.width = newHead.height = 20;

			// keep adding the newHead into the 'snake' array.
			body.insert(0, newHead);
		}

		private void eat(Rectangle food) {
			if(body.first().x == food.x // only count as 'eating' if the head = food
					&& snake.body.first().y == food.y) {
				snakeFood.spawnFood();
				bodyLength++;
			}
			else snake.body.pop();	// keep popping the array until the snake eats its food.
									// meaning that the snake keeps on 'moving' but not adding the body length until it eats.
		}

		private void onScreenLimit(Rectangle body) {
			// if the body is outside of the screen limit, put the body at the opposite side of the screen.
			if(body.x > 500-SCALE) body.x = 0;
			if(body.x < 0) body.x = 500;
			if(body.y > 500-SCALE) body.y = 0;
			if(body.y < 0) body.y = 500;
		}

		private void checkIfDie() {
			// check if the head of the snake hits a part of its body.
			// restart if true
			Rectangle head = body.first();
				for(int i = 1; i < bodyLength; i++){
					try{
					if (head.x == body.get(i).x &&
							head.y == body.get(i).y){
						body.clear();
						spawnSnake();
						bodyLength = 1;
							}
						} catch (Exception e) {}
					}
		}

		private void draw(Rectangle rect) {
			// draw the snake body as rectangle
			length.setColor(255.0f, 255.0f, 255.0f, 1);
			length.rect(rect.x, rect.y, 20, 20);
		}
	}

	private class Food {
		Rectangle food = new Rectangle();
		ShapeRenderer foodRect = new ShapeRenderer();

		private void spawnFood() {
			// This function spawns food at a random place, this happens at the start of the game
			// and when the food gets eaten by the snake
			final int SCALE = 20;
			float foodX = MathUtils.round((float)MathUtils.random(0, 500-SCALE)/SCALE);
			float foodY = MathUtils.round((float)MathUtils.random(0, 500-SCALE)/SCALE);

			food.x = foodX * SCALE;
			food.y = foodY * SCALE; // the same method I use for replacing 'STEP'

			food.width = food.height = 20;
		}

		private void draw() {
			foodRect.setColor(255.0f, 0.0f, 0.0f, 1);
			foodRect.rect(food.x, food.y, 20, 20);
		}
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 0);
		camera.update();

		snakeFood.foodRect.begin(ShapeRenderer.ShapeType.Filled);
		snake.length.begin(ShapeRenderer.ShapeType.Filled);

		snakeFood.draw();
		for(Rectangle body : snake.body){
			snake.draw(body);
			snake.onScreenLimit(body);
		}
		// putting the movement into a variable so that the snake can constantly move in the same line.
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) snake.movements = new int[]{-snake.VELOCITY, 0};
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) snake.movements = new int[]{snake.VELOCITY, 0};
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) snake.movements = new int[]{0, snake.VELOCITY};
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) snake.movements = new int[]{0, -snake.VELOCITY};

		try {
			sleep((long)(2000/30-Gdx.graphics.getDeltaTime()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		snake.setMove(snake.movements);
		snake.eat(snakeFood.food);

		if (snake.bodyLength > 3) snake.checkIfDie();

		snakeFood.foodRect.end();
		snake.length.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		snakeFood.foodRect.dispose();
		snake.length.dispose();
	}
}