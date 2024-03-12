package Package;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 15;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;
	
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	
	final String backgroundColor = "#512C62";
	final String foodColor = "#F45905";
	final String snakeColor = "#F45905";
	final String textColor = "#F45905";
	
	int bodyParts;
	int foodEaten;
	int foodX;
	int foodY;
	
	char direction;
	
	boolean running = false;
	
	Timer timer;
	Random random;
	
	Clip eatSound, gameOverSound;
	Font customFont;
	
	GamePanel() {
		random = new Random();
		
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.decode(backgroundColor));
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		
		startGame();
		loadSounds();
	}
	
	public void startGame() {
		bodyParts = 3;
        foodEaten = 0;
        direction = 'R';
        
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        
		newFood();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	public void loadSounds() {
		try {
            File eatSoundFile = new File("sfx_eat.wav");
            AudioInputStream audioInputStream_1 = AudioSystem.getAudioInputStream(eatSoundFile);

            eatSound = AudioSystem.getClip();
            eatSound.open(audioInputStream_1);
            
            File gameOverSoundFile = new File("sfx_game_over.wav");
            AudioInputStream audioInputStream_2 = AudioSystem.getAudioInputStream(gameOverSoundFile);

            gameOverSound = AudioSystem.getClip();
            gameOverSound.open(audioInputStream_2);

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
	}
	
	private void playEatSound() {
        if (eatSound != null) {
            eatSound.stop();
            eatSound.setFramePosition(0);
            eatSound.start();
        }
    }
	
	private void playGameOverSound() {
        if (gameOverSound != null) {
        	gameOverSound.stop();
        	gameOverSound.setFramePosition(0);
        	gameOverSound.start();
        }
    }
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		if(running) {
			g.setColor(Color.decode(foodColor));
			g.fillRect(foodX, foodY, UNIT_SIZE, UNIT_SIZE);
			
			for (int i = 0; i < bodyParts; i++) {
				if (i == 0) {
					g.setColor(Color.decode(snakeColor));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					g.setColor(Color.decode(snakeColor));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			
			// Score text
			g.setColor(Color.decode(textColor));
			g.setFont(new Font("Arial", Font.BOLD, 20));
			
			FontMetrics metrics = getFontMetrics(g.getFont());
			
			g.drawString("Score: " + foodEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2, g.getFont().getSize());
		} else {
			gameOver(g);
		}
	}
	
	public void newFood() {
		foodX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		foodY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
	}
	
	public void move() {
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		
		switch(direction) {
			case 'U':
				y[0] = y[0] - UNIT_SIZE;
				break;
			case 'D':
				y[0] = y[0] + UNIT_SIZE;
				break;
			case 'L':
				x[0] = x[0] - UNIT_SIZE;
				break;
			case 'R':
				x[0] = x[0] + UNIT_SIZE;
				break;
		}
	}
	
	public void checkFood() {
		if ((x[0] == foodX) && (y[0] == foodY)) {
			bodyParts++;
			foodEaten++;
			playEatSound();
			newFood();
		}
	}
	
	public void checkCollisions() {
		// Check if head collides with body
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		
		// Check if head touches left border
		if (x[0] < 0) {
			x[0] = SCREEN_WIDTH - UNIT_SIZE;
		}
		
		// Check if head touches right border
		if (x[0] > SCREEN_WIDTH) {
			x[0] = 0;
		}
		
		// Check if head touches top border
		if (y[0] < 0) {
			y[0] = SCREEN_HEIGHT - UNIT_SIZE;
		}
		
		// Check if head touches bottom border
		if (y[0] > SCREEN_HEIGHT) {
			y[0] = 0;
		}
		
		// Check running
		if (!running) {
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g) {
		playGameOverSound();
		
		g.setColor(Color.decode(textColor));
		
		// Game Over text
		g.setFont(new Font("Arial", Font.BOLD, 75));				
		
		FontMetrics metrics_1 = getFontMetrics(g.getFont());
		
		g.drawString("YOU DIED", (SCREEN_WIDTH - metrics_1.stringWidth("YOU DIED")) / 2, SCREEN_HEIGHT / 2);
		
		// Score and Game Over text
		g.setFont(new Font("Arial", Font.BOLD, 20));
		
		FontMetrics metrics_2 = getFontMetrics(g.getFont());
		
		g.drawString("Score: " + foodEaten, (SCREEN_WIDTH - metrics_2.stringWidth("Score: " + foodEaten)) / 2, SCREEN_HEIGHT / 2 + 2 + metrics_2.getHeight());
		g.drawString("Press SPACEBAR to restart!", (SCREEN_WIDTH - metrics_2.stringWidth("Press SPACEBAR to restart!")) / 2, SCREEN_HEIGHT / 2 + metrics_2.getHeight() * 2);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			move();
			checkFood();
			checkCollisions();
		}
		
		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					if(direction != 'R') {
						direction = 'L';
					}
					break;
				case KeyEvent.VK_RIGHT:
					if(direction != 'L') {
						direction = 'R';
					}
					break;
				case KeyEvent.VK_UP:
					if(direction != 'D') {
						direction = 'U';
					}
					break;
				case KeyEvent.VK_DOWN:
					if(direction != 'U') {
						direction = 'D';
					}
					break;
				case KeyEvent.VK_SPACE:
                    startGame();
                    break;
			}
		}
	}
}