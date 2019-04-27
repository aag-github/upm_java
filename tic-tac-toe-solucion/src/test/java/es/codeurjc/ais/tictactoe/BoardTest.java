package es.codeurjc.ais.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import es.codeurjc.ais.tictactoe.TicTacToeGame.Cell;

public class BoardTest {

	@Test
	public void player1WinsTest() throws InterruptedException {
		boardTest(new int[] { 0, 3, 1, 4, 2 }, 0);
	}

	@Test
	public void player2WinsTest() throws InterruptedException {
		boardTest(new int[] { 0, 3, 1, 4, 6, 5 }, 1);
	}

	@Test
	public void drawTest() throws InterruptedException {
		boardTest(new int[] { 0, 1, 3, 6, 4, 8, 7, 5, 2 }, -1);
	}

	private void boardTest(int[] cells, int winnerIndex) {
		
		Board board = new Board();
		String[] labels = { "X", "O" };
		
		for(int i=0; i<cells.length; i++) {
		
			Cell c = board.getCell(cells[i]);
			c.value = labels[i % 2];
		}
		
		if(winnerIndex == -1) {
			
			assertThat(board.checkDraw()).isTrue();
			assertThat(board.getCellsIfWinner(labels[0])).isNull();
			assertThat(board.getCellsIfWinner(labels[1])).isNull();
			
		} else {
			
			assertThat(board.checkDraw()).isFalse();
			
			int[] winnerCells;
			
			if(winnerIndex == 0) {
				winnerCells = new int[] { cells[0], cells[2], cells[4] };
			} else {
				winnerCells = new int[] { cells[1], cells[3], cells[5] };
			}
			
			assertThat(board.getCellsIfWinner(labels[winnerIndex])).isEqualTo(winnerCells);
			assertThat(board.getCellsIfWinner(labels[(winnerIndex+1)%2])).isNull();
			
		}
	}	
}
