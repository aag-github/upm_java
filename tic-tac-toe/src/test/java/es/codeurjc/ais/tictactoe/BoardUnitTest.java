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
	@Parameters(name = "{index}: Given a board, When {0} vs {1} and {2} wins with board {3}, Then draw is {4} and winning cells belong to {2}")
    public static Collection<Object[]> data() {
        Object[][] values = {
        {   "X", "O", "X","not full",false,
        	new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.TOP_RIGHT},
        	new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER},
        	new CellId[]{CellId.TOP_LEFT, CellId.TOP_CENTER, CellId.TOP_RIGHT},
        },
        {   "X", "O", "O", "not full",false,
            new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.BOTTOM_RIGHT}, 
            new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER, CellId.MIDDLE_RIGHT},
            new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER, CellId.MIDDLE_RIGHT},
        },
        {   "X", "O", "no one", "full",true,
            new CellId[] {CellId.TOP_LEFT,   CellId.TOP_RIGHT ,    CellId.MIDDLE_LEFT, CellId.MIDDLE_RIGHT, CellId.BOTTOM_CENTER}, 
            new CellId[] {CellId.TOP_CENTER, CellId.MIDDLE_CENTER, CellId.BOTTOM_LEFT, CellId.BOTTOM_RIGHT},
            null,
        },
        {   "X", "O", "X", "full",false,
            new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.MIDDLE_CENTER, CellId.MIDDLE_LEFT, CellId.BOTTOM_RIGHT}, 
            new CellId[]{CellId.BOTTOM_LEFT,  CellId.TOP_RIGHT,  CellId.BOTTOM_CENTER,CellId.MIDDLE_RIGHT}, 
            new CellId[]{CellId.TOP_LEFT, CellId.MIDDLE_CENTER, CellId.BOTTOM_RIGHT},
        },
    };
        return Arrays.asList(values);
    }

	@Parameter(0) public String user1Id;
	@Parameter(1) public String user2Id;
	@Parameter(2) public String winnerId;
	@Parameter(3) public String boardFull;
	@Parameter(4) public Boolean draw;
	@Parameter(5) public CellId[] user1Clicks;
	@Parameter(6) public CellId[] user2Clicks;
	@Parameter(7) public CellId[] winnerCells;

	@Test
    public void GivenBoard_WhenGameEnds_ThenCheckGameEndFunctions() {
		//Given
		Board board = new Board();
        String loserId="";
        if(winnerId == user1Id) {
            loserId = user2Id;
        } else if (winnerId == user2Id) {
            loserId = user1Id;
        } 
        
        // When
        play(board, user1Clicks, user2Clicks);

        //Then
        assertThat(board.getCellsIfWinner(loserId), equalTo(null));
        assertThat(board.getCellsIfWinner(winnerId), equalTo(CellId.cellsToInt(winnerCells)));
		
		assertThat(board.checkDraw(), equalTo(draw));
	}

    private void play(Board board, CellId[] user1Clicks, CellId[] user2Clicks) {
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
