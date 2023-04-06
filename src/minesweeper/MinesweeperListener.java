/*
    Name:       Matthew Chen
    Date:       3/28/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
 */

package minesweeper;


public interface MinesweeperListener {

    public void cellChanged(int row, int col, int oldVal, int newVal);

    public void gridReplaced();

}
