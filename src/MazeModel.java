import javax.swing.table.AbstractTableModel;

class MazeModel extends AbstractTableModel {
    private int[][] mData; // MazeData

    public MazeModel(int[][] mData) {
        this.mData = mData;
    }

    @Override
    public int getRowCount() {
        return mData.length;
    }
    @Override
    public int getColumnCount() {
        return mData[0].length;
    }
    @Override
    public Object getValueAt(int row, int col) {
        return mData[row][col];
    }

    public void setValueAt(int row, int col, int value) {
        mData[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}
