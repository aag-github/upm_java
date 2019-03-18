package es.codeurjc.ais.tictactoe;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.any;

import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import es.codeurjc.ais.tictactoe.TicTacToeGame.EventType;

@RunWith(Parameterized.class)
public class DoublesTest {
	Connection connection1 = mock(Connection.class);
	Connection connection2 = mock(Connection.class);

	@Parameters(name = "{index}: Given a board, When {0} wins, Then check draw and winning cells functions")
    public static Collection<Object[]> data() {
        Object[][] values = {
        {
        	new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.TOP_RIGHT},
        	new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER},
           	new int[]{CellId.TOP_LEFT.ordinal(), CellId.TOP_CENTER.ordinal(), CellId.TOP_RIGHT.ordinal()},
        },
        {
            new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.BOTTOM_RIGHT}, 
            new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER, CellId.MIDDLE_RIGHT},
            new int[]{CellId.MIDDLE_LEFT.ordinal(), CellId.MIDDLE_CENTER.ordinal(), CellId.MIDDLE_RIGHT.ordinal()},
        },
        {
            new CellId[] {CellId.TOP_LEFT,   CellId.TOP_RIGHT ,    CellId.MIDDLE_LEFT, CellId.MIDDLE_RIGHT, CellId.BOTTOM_CENTER}, 
            new CellId[] {CellId.TOP_CENTER, CellId.MIDDLE_CENTER, CellId.BOTTOM_LEFT, CellId.BOTTOM_RIGHT},
            null,
        },
    };
        return Arrays.asList(values);
    }

	@Parameter(0) public CellId[] user1Clicks;
	@Parameter(1) public CellId[] user2Clicks;
	@Parameter(2) public int[] winnerCells;
	
	@Test
	public void givenTicTacToeGameWithTwoUsers_whenPlay_thenCheckResults() {
		// Given
		TicTacToeGame game = new TicTacToeGame();
		
		game.addConnection(connection1);
		game.addConnection(connection2);
		
		Player player1 = new Player(1, "X", "User 1");
		Player player2 = new Player(2, "O", "User 2");
		
		// When
		game.addPlayer(player1);		
		game.addPlayer(player2);

		// Then
		verify(connection1, times(2)).sendEvent(
				eq(EventType.JOIN_GAME), argThat(hasItems(player1, player2)));
		verify(connection2, times(2)).sendEvent(
				eq(EventType.JOIN_GAME), argThat(hasItems(player1, player2)));
		reset(connection1);
		reset(connection2);

		// When/Then
    	play(game, player1, player2);
    	
    	//When game ends Then check Draw and Winner
		assertThat(game.checkWinner().win, equalTo(winnerCells != null));
		assertThat(game.checkWinner().pos, equalTo(winnerCells));
	}	

    private void play(TicTacToeGame game, Player player1, Player player2) {
        int max = Integer.max(user1Clicks.length, user2Clicks.length);
        int left = user1Clicks.length + user2Clicks.length;
        for(int i = 0; i < max; i++) {
            if (i < user1Clicks.length) {
        		System.out.println("Playing '" + i + "' user1Clicks");
        		left--;
        		
        		// When
            	game.mark(user1Clicks[i].ordinal());
        		
            	// Then
            	checkConnectionEvents(player2, left == 0); 
        	}
            if (i < user2Clicks.length) {
        		System.out.println("Playing '" + i + " user2Clicks");
        		left--;
        		
        		// When
            	game.mark(user2Clicks[i].ordinal());
            	
            	//Then
            	checkConnectionEvents(player1, left == 0);
          	}
        }
    }
    
    private void checkConnectionEvents(Player nextPlayer, Boolean lastCLick) {
		verify(connection1, times(1)).sendEvent(
				eq(EventType.MARK), any());            	
		verify(connection2, times(1)).sendEvent(
				eq(EventType.MARK), any());
		
		if (lastCLick) {
			verify(connection1, times(1)).sendEvent(
					eq(EventType.GAME_OVER), any());            	
			verify(connection2, times(1)).sendEvent(
					eq(EventType.GAME_OVER), any());        			
		} else {
			verify(connection1, times(1)).sendEvent(
					eq(EventType.SET_TURN), eq(nextPlayer));            	
			verify(connection2, times(1)).sendEvent(
					eq(EventType.SET_TURN), eq(nextPlayer));
		}
		reset(connection1);
		reset(connection2); 
    }
}