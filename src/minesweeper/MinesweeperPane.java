/*
    Name:       Matthew Chen
    Date:       3/28/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
*/

package minesweeper;

import java.io.File;
import java.util.HashMap;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class MinesweeperPane extends Group implements MinesweeperListener {
	
	/** Size of each tile in pixels */
	private double tileSize;
	
	/** Underlying ImageView objects */
	private ImageView[][] tiles;
	
	/** Minesweeper model */
	private MinesweeperModel model;
	
	/** Hashmap for preloaded Image objects and their corresponding filenames */
	private HashMap<String, Image> imgs;
	
	
	public MinesweeperPane() {
		this.model = null;
		this.tiles = null;
		this.tileSize = 25;
		
		this.loadImgs();
	}
	
	
	public void setTileSize(double size) {
		this.tileSize = size;
		resetTiles();
	}
	
	
	public void loadImgs() {
		this.imgs = new HashMap<String, Image>();
		File imgPath = new File("images/");
		File[] imgs = imgPath.listFiles();
		
		for (File f : imgs) {
			String name = f.getName();
			Image img = new Image("file:images/" + name);
			this.imgs.put(name, img);
		}
	}
	
	
	public void setModel(MinesweeperModel model) {
		if (this.model != null) {
			this.model.removeListener(this);
		}
		
		model.addListener(this);
		this.model = model;
		resetTiles();
	}
	
	
	public void resetTiles() {
		this.getChildren().remove(0, this.getChildren().size());
		if (model != null) {
			tiles = new ImageView[model.getNumRows()][model.getNumCols()];
			for (int i = 0; i < model.getNumRows(); i++) {
				for (int j = 0; j < model.getNumCols(); j++) {
					ImageView img;
					
					if (model.isRevealed(i, j)) {
						img = new ImageView(imgs.get("num_" + model.getNumNeighboringMines(i, j) + ".gif"));
						img.setFitWidth(this.tileSize);
						img.setFitHeight(this.tileSize);
						
					} else if (model.isFlag(i, j)) {
						img = new ImageView(imgs.get("bomb_flagged.gif"));
						img.setFitWidth(this.tileSize);
						img.setFitHeight(this.tileSize);
						
					} else {
						img = new ImageView(imgs.get("blank.gif"));
						img.setFitWidth(this.tileSize);
						img.setFitHeight(this.tileSize);
					}
					
					img.setX(tileSize * i);
					img.setY(tileSize * j);
					
					this.getChildren().add(img);
					tiles[i][j] = img;
				}
			}
		}
	}


	@Override
	public void cellChanged(int row, int col, int oldVal, int newVal) {
		// oldVal, newVal are values in the upper layer of the model
		switch (newVal) {
			case MinesweeperModel.REVEALED:
				if (model.isMine(row, col)) {
					tiles[row][col].setImage(imgs.get("bomb_revealed.gif"));
					break;
				}
				tiles[row][col].setImage(imgs.get("num_" + model.getNumNeighboringMines(row, col) + ".gif"));
				break;
			
			case MinesweeperModel.FLAGGED:
				tiles[row][col].setImage(imgs.get("bomb_flagged.gif"));
				break;
				
			default:
				tiles[row][col].setImage(imgs.get("blank.gif"));
				break;
		}
		
	}


	@Override
	public void gridReplaced() {
		resetTiles();
	}
	
	public int colForXPos(double posX) {
		return (int)(posX / tileSize);
	}
	
	public int rowForYPos(double posY) {
		return (int)(posY / tileSize);
	}
}
