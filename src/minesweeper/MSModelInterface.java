/*
    Name:       Matthew Chen
    Date:       3/28/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
 */

package minesweeper;

public interface MSModelInterface {

    boolean isFlag(int row, int col);
    boolean isMine(int row, int col);
    boolean isRevealed(int row, int col);
    boolean isGameWon();
    boolean isGameOver();

    int getNumNeighboringMines(int row, int col);
    int getNumRows();
    int getNumCols();
    int getNumFlags();
    int getNumMines();
    int getNumRevealed();

    void reveal(int row, int col);
    void initBoard(int mines);
    void setFlag(int row, int col);
    void resetBoard();
}
