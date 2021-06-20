package game_xepgach;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MyGame extends JPanel {

			public static void main(String[] args) {
			JFrame f = new JFrame("Game xếp gạch ");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(12*26+10, 26*23+25);
			f.setVisible(true);
			
			
			final MyGame game = new MyGame();
			game.init();
			f.add(game);
			
			f.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			
			public void keyPressed(KeyEvent e) {
					switch (e.getKeyCode()) {
								case KeyEvent.VK_UP:
								game.rotate(-1);
								break;
					case KeyEvent.VK_DOWN:
								game.rotate(+1);
								break;
					case KeyEvent.VK_LEFT:
								game.move(-1);
								break;
					case KeyEvent.VK_RIGHT:
								game.move(+1);
								break;
					case KeyEvent.VK_SPACE:
								game.dropDown();
								game.score += 1;
								break;
			}
			}
			
			public void keyReleased(KeyEvent e) {
			}
			});
			new Thread() {
			@Override public void run() {
					while (true) {
					try {
								Thread.sleep(1000);
								game.dropDown();
					} catch ( InterruptedException e ) {}
					}
					}
					}.start();
			}
			
			private final Point[][][] MyShapes = {
							// I-Piece
							{
							{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
							{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
							{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
							{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) }
							},
							
							// J-Piece
							{
							{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
							{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
							{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
							{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) }
							},
							
							// L-Piece
							{
							{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
							{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
							{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
							{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) }
							},
							
							// O-Piece
							{
							{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
							{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
							{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
							{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
							}
			};
			
			private final Color[] MyColors = {
			Color.green, Color.MAGENTA, Color.orange, Color.yellow, Color.black, Color.pink,
			             Color.red };
			
			private Point pt;
			private int currentPiece;
			private int rotation;
			private ArrayList<Integer> nextPieces = new ArrayList<Integer>();
			
			private long score;
			private Color[][] well;
			
			private void init() {
					well = new Color[12][24];
					for (int i = 0; i < 12; i++) {
					for (int j = 0; j < 23; j++) {
					if (i == 0 || i == 11 || j == 22) 
					{
							well[i][j] = Color.PINK;
					} 
					else {
							well[i][j] = Color.black;
					}
					}
			}
			newPiece();
			}
			public void newPiece() 
			{
					pt = new Point(5, 2);
					rotation = 0;
					if (nextPieces.isEmpty()) {
					Collections.addAll(nextPieces, 0, 1, 2, 3);
					Collections.shuffle(nextPieces);
			}
			currentPiece = nextPieces.get(0);
			nextPieces.remove(0);
			}
			
			private boolean collidesAt(int x, int y, int rotation) {
					for (Point p : MyShapes[currentPiece][rotation]) {
								if (well[p.x + x][p.y + y] != Color.black) {
								return true;
								}
					}
					return false;
			}
			
			public void rotate(int i) {
							int newRotation = (rotation + i) % 4;
							if (newRotation < 0) {
										newRotation = 3;
							}
							if (!collidesAt(pt.x, pt.y, newRotation)) {
										rotation = newRotation;
							}
							repaint();
			}
			
			public void move(int i) {
					if (!collidesAt(pt.x + i, pt.y, rotation)) {
							pt.x += i; 
					}
					repaint();
			}
			
			public void dropDown() {
						if (!collidesAt(pt.x, pt.y + 1, rotation)) {
								pt.y += 1;
						}
						else {
								fixToWell();
						} 
						repaint();
			}
			public void fixToWell() {
							for (Point p : MyShapes[currentPiece][rotation]) {
											well[pt.x + p.x][pt.y + p.y] = MyColors[currentPiece];
							}
							clearRows();
							newPiece();
			}
			
			public void deleteRow(int row) {
						for (int j = row-1; j > 0; j--) {
									for (int i = 1; i < 11; i++) {
											well[i][j+1] = well[i][j];
									}
						}
			}
			
			public void clearRows() {
							boolean gap;
							int numClears = 0; 
							for (int j = 21; j > 0; j--) {
								gap = false;
								for (int i = 1; i < 11; i++) {
										if (well[i][j] == Color.black) {
												gap = true;
												break;
										}
									}
								if (!gap) {
										deleteRow(j);
										j += 1;
										numClears += 1;
								}
							}
							switch (numClears) {
									case 1: score += 100;break;
									case 2: score += 300;break;
									case 3: score += 500;break;
									case 4: score += 800;break;
							}
			}
			private void drawPiece(Graphics g) { 
					g.setColor(MyColors[currentPiece]);
					for (Point p : MyShapes[currentPiece][rotation]) {
							g.fillRect((p.x + pt.x) * 26,
							  (p.y + pt.y) * 26,
							  25, 25);
					}
			}
			
			@Override
			public void paintComponent(Graphics g)
			{
						g.fillRect(0, 0, 26*12, 26*23);
						for (int i = 0; i < 12; i++) {
								for (int j = 0; j < 23; j++) {
											g.setColor(well[i][j]);
											g.fillRect(26*i, 26*j, 25, 25);
											}
								}
						g.setColor(Color.WHITE);
						g.drawString("điểm : " + score, 19*12, 25);
						
						drawPiece(g);
						}
			}