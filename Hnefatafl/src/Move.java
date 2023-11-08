import java.util.HashMap;
import java.util.Map;

class Move
{
	private int rowStart;
	private int colStart;
	private int rowTarget;
	private int colTarget;
	
	private Map<Integer, String> boardConversionRow = new HashMap<>();
	private char[] rows = "ABCDEFGHIJKLM".toCharArray();

	public Move(){
		rowStart = -1;
		colStart = -1;
		rowTarget = -1;
		colTarget = -1;
		for(int i=0; i<rows.length;i++) {
			boardConversionRow.put(i, String.valueOf(rows[i]));
		}
	}

	public Move(int rS, int cS, int rT, int cT){
		rowStart = rS;
		colStart = cS;
		rowTarget = rT;
		colTarget = cT;
	}

	public int getRowStart(){
		return rowStart;
	}

	public int getColStart(){
		return colStart;
	}

	public void setRowStart(int r){
		rowStart = r;
	}

	public void setColStart(int c){
		colStart = c;
	}

	public int getRowTarget() {
		return rowTarget;
	}

	public void setRowTarget(int rowTarget) {
		this.rowTarget = rowTarget;
	}

	public int getColTarget() {
		return colTarget;
	}

	public void setColTarget(int colTarget) {
		this.colTarget = colTarget;
	}

	public String printMove() {
		return boardConversionRow.get(rowStart)+""+colStart+" - "+boardConversionRow.get(rowTarget)+""+colTarget;
	}
}
