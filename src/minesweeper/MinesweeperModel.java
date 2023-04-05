/*
    Name:       Matthew Chen
    Date:       3/28/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
*/


package minesweeper;

import java.util.ArrayList;
import java.util.Random;

public class MinesweeperModel implements MSModelInterface {
	
	public static final int UPPER_LAYER = 0;
	public static final int LOWER_LAYER = 1;
	
	private int[][] upperLayer;
	private int[][] lowerLayer;
	private int mines;
	
	public static final int MINED = -1;
	public static final int HIDDEN = 0;
	public static final int REVEALED = 1;
	public static final int FLAGGED = 2;
	
	private ArrayList<MinesweeperListener> listeners;

	public MinesweeperModel(int rows, int cols, int mines) {
		this.upperLayer = new int[rows][cols];
		this.lowerLayer = new int[rows][cols];
		this.mines = mines;
		this.listeners = new ArrayList<MinesweeperListener>();
		
		initBoard(mines);
	}
	
	@Override
	public boolean isFlag(int row, int col) {
		return this.upperLayer[row][col] == FLAGGED;
	}

	@Override
	public boolean isMine(int row, int col) {
		return this.lowerLayer[row][col] == MINED;
	}

	@Override
	public boolean isRevealed(int row, int col) {
		return this.upperLayer[row][col] == REVEALED;
	}

	@Override
	public boolean isGameWon() {
		return this.getNumRows() * this.getNumCols() - this.getNumRevealed() == this.getNumMines();
	}
	
	@Override
	public boolean isGameOver() {
		for (int i = 0; i < this.upperLayer.length; i++) {
			for (int j = 0; j < this.upperLayer[0].length; j++) {
				if (this.isMine(i, j) && this.isRevealed(i, j))
					return true;
			}
		}
		return false;
	}

	@Override
	public int getNumNeighboringMines(int row, int col) {
		return this.lowerLayer[row][col];
	}
	
	@Override
	public int getNumFlags() {
		int count = 0;
		for (int i = 0; i < upperLayer.length; i++) {
			for (int j = 0; j < upperLayer[0].length; j++) {
				if (this.isFlag(i, j))
					count++;
			}
		}
		
		return count;
	}
	
	@Override
	public int getNumRevealed() {
		int count = 0;
		for (int i = 0; i < upperLayer.length; i++) {
			for (int j = 0; j < upperLayer[0].length; j++) {
				if (this.isRevealed(i, j))
					count++;
			}
		}
		
		return count;
	}
	
	@Override
	public int getNumMines() {
		return this.mines;
	}
		
	private int generateNumMines(int row, int col) {	
		int count = this.isMine(row, col) ? -1 : 0;
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i >= 0 && i < this.getNumRows() && j >= 0 && j < this.getNumCols())
					if (this.isMine(i, j))
						count++;
			}
		}
		
		return count;
	}

	@Override
	public int getNumRows() {
		return this.upperLayer.length;
	}

	@Override
	public int getNumCols() {
		return this.upperLayer[0].length;
	}

	@Override
	public void setFlag(int row, int col) {
		if (!this.isRevealed(row, col)) {
			if (this.isFlag(row, col))
				this.setValueAt(row, col, HIDDEN);
			else
				this.setValueAt(row, col, FLAGGED);
		}
	}

	@Override
	public void reveal(int row, int col) {
		if (row < 0 || row >= this.getNumRows() || col < 0 || col >= this.getNumCols() || this.isRevealed(row, col))
			return;
		
		this.setValueAt(row, col, REVEALED);
		
		// User clicked on a mine
		if (this.lowerLayer[row][col] == MINED) {
			return;
		}
		
		// User clicked on a square with neighboring mines
		if (this.lowerLayer[row][col] != 0)
			return;
		
		// Recursively flip over surrounding squares
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (!(i == 0 && j == 0))
					try {
						this.reveal(row + i, col + j);
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
					}
			}
		}
	}

	@Override
	public void initBoard(int mines) {
		Random rand = new Random();
		for (int i = 0; i < mines; i++) {
			int row = 0;
			int col = 0;
			
			do {
				row = rand.nextInt(this.getNumRows());
				col = rand.nextInt(this.getNumCols());
				
			} while (this.isMine(row, col));
			
			this.lowerLayer[row][col] = MINED;
		}
		
		// Populate the rest of the board
		for (int i = 0; i < this.getNumRows(); i++) {
			for (int j = 0; j < this.getNumCols(); j++) {
				if (this.lowerLayer[i][j] != -1)
					this.lowerLayer[i][j] = this.generateNumMines(i, j);
			}
		}
	}

	@Override
	public void resetBoard() {
		this.upperLayer = new int[this.getNumRows()][this.getNumCols()];
		this.lowerLayer = new int[this.getNumRows()][this.getNumCols()];
		
		for (MinesweeperListener l : listeners) {
			l.gridReplaced();
		}
	}
	
	
	public void addListener(MinesweeperListener l) {
		if (!this.listeners.contains(l)) {
			this.listeners.add(l);
		}
	}
	
	public void removeListener(MinesweeperListener l) {
		this.listeners.remove(l);
	}
	
	/**
	 * Set the value of the upper layer of the board at the given (row, col)
	 * @param row the row index
	 * @param col the column index
	 * @param val the new value
	 */
	public void setValueAt(int row, int col, int val) {
		int oldVal = this.upperLayer[row][col];
		this.upperLayer[row][col] = val;
		
		if (oldVal != val) {
			for (MinesweeperListener l : this.listeners) {
				l.cellChanged(row, col, oldVal, val);
			}
		}
	}

}
