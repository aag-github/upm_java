package es.codeurjc.ais.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import es.codeurjc.ais.tictactoe.TicTacToeGame.EventType;
import es.codeurjc.ais.tictactoe.TicTacToeGame.WinnerValue;

public class TicTacToeGameTest {

	@Test
	public void player1WinsTest() throws InterruptedException {
		ticTacToeTest(new int[] { 0, 3, 1, 4, 2 }, 0);
	}

	@Test
	public void player2WinsTest() throws InterruptedException {
		ticTacToeTest(new int[] { 0, 3, 1, 4, 6, 5 }, 1);
	}

	@Test
	public void drawTest() throws InterruptedException {
		ticTacToeTest(new int[] { 0, 1, 3, 6, 4, 8, 7, 5, 2 }, -1);
	}

	public void ticTacToeTest(int[] cells, int winnerIndex) {

		Connection c1 = mock(Connection.class);
		Connection c2 = mock(Connection.class);

		TicTacToeGame game = new TicTacToeGame();

		game.addConnection(c1);
		game.addConnection(c2);

		Player p0 = new Player(0, "O", "P1");
		Player p1 = new Player(1, "X", "P2");

		game.addPlayer(p0);
		verify(c1).sendEvent(eq(EventType.JOIN_GAME), argThat(hasItem(p0)));
		verify(c2).sendEvent(eq(EventType.JOIN_GAME), argThat(hasItem(p0)));

		reset(c1);
		reset(c2);
		
		game.addPlayer(p1);
	
		verify(c1).sendEvent(eq(EventType.JOIN_GAME), argThat(hasItems(p0, p1)));
		verify(c2).sendEvent(eq(EventType.JOIN_GAME), argThat(hasItems(p0, p1)));

		for (int i = 0; i < cells.length; i++) {

			verify(c1).sendEvent(EventType.SET_TURN, i % 2 == 0 ? p0 : p1);
			verify(c2).sendEvent(EventType.SET_TURN, i % 2 == 0 ? p0 : p1);

			reset(c1);
			reset(c2);

			game.mark(cells[i]);

		}

		ArgumentCaptor<WinnerValue> argument = ArgumentCaptor.forClass(WinnerValue.class);
		verify(c1).sendEvent(eq(EventType.GAME_OVER), argument.capture());

		if(winnerIndex == -1) {
			
			assertThat(argument.getValue()).isNull();
			
		} else {
			
			assertThat(argument.getValue().player.getId()).isEqualTo(winnerIndex);
			
			int[] winnerCells;
			if(winnerIndex == 0) {
				winnerCells = new int[] { cells[0], cells[2], cells[4] };
			} else {
				winnerCells = new int[] { cells[1], cells[3], cells[5] };
			}
			
			assertThat(argument.getValue().pos).isEqualTo(winnerCells);	
		}		
	}
}
