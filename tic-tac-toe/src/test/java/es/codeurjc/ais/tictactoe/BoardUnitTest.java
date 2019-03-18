package es.codeurjc.ais.tictactoe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BoardUnitTest {
	
	@Parameters(name = "{index}: Given a board, When {0} vs {1} and {2} wins, Then check draw and winning cells functions")
    public static Collection<Object[]> data() {
        Object[][] values = {
        {   "X", "O", "X",
        	new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.TOP_RIGHT},
        	new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER},
           	new int[]{CellId.TOP_LEFT.ordinal(), CellId.TOP_CENTER.ordinal(), CellId.TOP_RIGHT.ordinal()},
       },
        {   "X", "O", "O",
            new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.BOTTOM_RIGHT}, 
            new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER, CellId.MIDDLE_RIGHT},
            new int[]{CellId.MIDDLE_LEFT.ordinal(), CellId.MIDDLE_CENTER.ordinal(), CellId.MIDDLE_RIGHT.ordinal()},
        },
        {   "X", "O", "no one", 
            new CellId[] {CellId.TOP_LEFT,   CellId.TOP_RIGHT ,    CellId.MIDDLE_LEFT, CellId.MIDDLE_RIGHT, CellId.BOTTOM_CENTER}, 
            new CellId[] {CellId.TOP_CENTER, CellId.MIDDLE_CENTER, CellId.BOTTOM_LEFT, CellId.BOTTOM_RIGHT},
            null,
        },
    };
        return Arrays.asList(values);
    }

	@Parameter(0) public String user1Id;
	@Parameter(1) public String user2Id;
	@Parameter(2) public String winnerId;
	@Parameter(3) public CellId[] user1Clicks;
	@Parameter(4) public CellId[] user2Clicks;
	@Parameter(5) public int[] winnerCells;
	
	@Test
	public void GivenBoard_WhenGameEnds_ThenCheckGameEndFunctions() throws InterruptedException {
		//Given
		Board board = new Board();

		// When
		play(board, user1Clicks, user2Clicks);
		
		//Then
        String loserId = "";
        Boolean draw = false;
        if(winnerId == user1Id) {
        	loserId = user2Id;
        } else if (winnerId == user2Id) {
        	loserId = user1Id;
        } else {
        	draw = true;
        }
        
		assertThat(board.getCellsIfWinner(loserId), equalTo(null));
        assertThat(board.getCellsIfWinner(winnerId), equalTo(winnerCells));
		
		assertThat(board.checkDraw(), equalTo(draw));
	}

    private void play(Board board, CellId[] user1Clicks, CellId[] user2Clicks) throws InterruptedException {
        int max = Integer.max(user1Clicks.length, user2Clicks.length);
        for(int i = 0; i < max; i++) {
            if (i < user1Clicks.length) {
            	board.getCell(user1Clicks[i].ordinal()).value = user1Id;
            }
            if (i < user2Clicks.length) {
            	board.getCell(user2Clicks[i].ordinal()).value = user2Id;
            }
        }
    }
}
