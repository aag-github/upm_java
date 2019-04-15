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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;

import es.codeurjc.ais.tictactoe.TicTacToeGame.EventType;
import es.codeurjc.ais.tictactoe.TicTacToeGame.WinnerValue;

@RunWith(Parameterized.class)
public class TicTacToeGameTest {
    enum User {
        USER1,
        USER2,
        NONE
    }
    
    Connection connection1 = mock(Connection.class);
    Connection connection2 = mock(Connection.class);

    @Parameters(name = "{index}: Given a TicTacToe game with 2 players, When {0} wins, Then winning cells belong to {0}") 
    public static Collection<Object[]> data() {
        Object[][] values = {
        {
            User.USER1,
            new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.TOP_RIGHT},
            new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER},
            new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.TOP_RIGHT},
        },
        {
            User.USER2,
            new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.BOTTOM_RIGHT}, 
            new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER, CellId.MIDDLE_RIGHT},
            new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER, CellId.MIDDLE_RIGHT},
        },
        {
            User.NONE,
            new CellId[] {CellId.TOP_LEFT,   CellId.TOP_RIGHT ,    CellId.MIDDLE_LEFT, CellId.MIDDLE_RIGHT, CellId.BOTTOM_CENTER}, 
            new CellId[] {CellId.TOP_CENTER, CellId.MIDDLE_CENTER, CellId.BOTTOM_LEFT, CellId.BOTTOM_RIGHT},
            null,
        },
        {   User.USER1,
            new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.MIDDLE_CENTER, CellId.MIDDLE_LEFT, CellId.BOTTOM_RIGHT}, 
            new CellId[]{CellId.BOTTOM_LEFT,  CellId.TOP_RIGHT,  CellId.BOTTOM_CENTER,CellId.MIDDLE_RIGHT}, 
            new CellId[]{CellId.TOP_LEFT, CellId.MIDDLE_CENTER, CellId.BOTTOM_RIGHT},
        },
    };
        return Arrays.asList(values);
    }

    @Parameter(0) public User winner;    
    @Parameter(1) public CellId[] user1Clicks;
    @Parameter(2) public CellId[] user2Clicks;
    @Parameter(3) public CellId[] winnerCells;
    
    @Test
    public void givenTicTacToeGameWithTwoUsers_whenPlay_thenCheckResults() {
        // Given
        TicTacToeGame game = new TicTacToeGame();
        game.addConnection(connection1);
        game.addConnection(connection2);
        
        Player[] players = { new Player(1, "X", "User 1"), new Player(2, "O", "User 2") };
        
        CellId[][] clicks = {user1Clicks, user2Clicks}; 
        
        // When
        game.addPlayer(players[0]);        
        game.addPlayer(players[1]);

        // Then
        verify(connection1, times(2)).sendEvent(
                eq(EventType.JOIN_GAME), argThat(hasItems(players[0], players[1])));
        verify(connection2, times(2)).sendEvent(
                eq(EventType.JOIN_GAME), argThat(hasItems(players[0], players[1])));
        resetConnections();

        // When/Then
        play(game, players[0], players[1], winnerCells == null);
        
        //When game ends Then check Draw and Winner
        assertThat(game.checkWinner().win, equalTo(winnerCells != null));
        assertThat(game.checkWinner().pos, equalTo(CellId.cellsToInt(winnerCells)));
        
        if (winner != User.NONE) {
            CellId[] winnerClicks = clicks[winner.ordinal()];
            for(CellId cell : winnerCells) {
                assertThat(Arrays.asList(winnerClicks), hasItem(cell));
            }
        }
    }    

    private void play(TicTacToeGame game, Player player1, Player player2, boolean draw) {
        int max = Integer.max(user1Clicks.length, user2Clicks.length);
        int left = user1Clicks.length + user2Clicks.length;
        for(int i = 0; i < max; i++) {
            if (i < user1Clicks.length) {
                System.out.println("Playing '" + i + "' user1Clicks");
                left--;
                
                // When
                game.mark(user1Clicks[i].ordinal());
                
                // Then
                if (left == 0) {
                    checkGameOverConnectionEvents(draw ? "" : player1.getName());                                         
                } else {
                    checkTurnConnectionEvents(player2.getName());                     
                }
                resetConnections();
            }
            if (i < user2Clicks.length) {
                System.out.println("Playing '" + i + " user2Clicks");
                left--;
                
                // When
                game.mark(user2Clicks[i].ordinal());
                
                //Then
                if (left == 0) {
                    checkGameOverConnectionEvents(draw ? "" : player2.getName());                                         
                } else {
                    checkTurnConnectionEvents(player1.getName());                     
                }
                resetConnections();
            }
        }
    }
    
    private void checkGameOverConnectionEvents(String expectedWinner) {
        verify(connection1, times(1)).sendEvent(
                eq(EventType.MARK), any());
        verify(connection2, times(1)).sendEvent(
                eq(EventType.MARK), any());
        
        String actualWinner;
        ArgumentCaptor<WinnerValue> argument = ArgumentCaptor.forClass(WinnerValue.class);
        verify(connection1, times(1)).sendEvent(
                eq(EventType.GAME_OVER), argument.capture());
        actualWinner = argument.getValue() != null ? argument.getValue().player.getName() : "";
        assertThat(actualWinner, equalTo(expectedWinner));
         
        verify(connection2, times(1)).sendEvent(
                eq(EventType.GAME_OVER), argument.capture());
        actualWinner = argument.getValue() != null ? argument.getValue().player.getName() : "";
        assertThat(actualWinner, equalTo(expectedWinner));
    }

    private void checkTurnConnectionEvents(String expectedNextPlayer) {
        ArgumentCaptor<Player> argument = ArgumentCaptor.forClass(Player.class);
        verify(connection1, times(1)).sendEvent(
               eq(EventType.MARK), any());
        verify(connection2, times(1)).sendEvent(
               eq(EventType.MARK), any());
        
        verify(connection1, times(1)).sendEvent(
                eq(EventType.SET_TURN), argument.capture());
        assertThat(argument.getValue().getName(), equalTo(expectedNextPlayer));

        verify(connection2, times(1)).sendEvent(
                eq(EventType.SET_TURN), argument.capture());
        assertThat(argument.getValue().getName(), equalTo(expectedNextPlayer));
    }

    private void resetConnections() {
        reset(connection1);
        reset(connection2); 
    }
}
