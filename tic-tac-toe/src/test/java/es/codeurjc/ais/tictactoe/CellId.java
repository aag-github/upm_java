package es.codeurjc.ais.tictactoe;

enum CellId {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        MIDDLE_LEFT,
        MIDDLE_CENTER,
        MIDDLE_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT;

    static public int[] cellsToInt(CellId[] cells) {
            if (cells == null) {
                return null;
            }
            int[] intCells = new int[cells.length];
            for (int i = 0; i < cells.length; i++) {
                intCells[i] = cells[i].ordinal();
            }
            return intCells;
        }
    }
