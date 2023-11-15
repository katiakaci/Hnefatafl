import java.util.*;

class Move
{
	private int rowStart;
	private int colStart;
	private int rowTarget;
	private int colTarget;

	public Move(int rS, int cS, int rT, int cT){
		rowStart = rS;
		colStart = cS;
		rowTarget = rT;
		colTarget = cT;
	}

	/***
	 * Retourne format D6-D5
	 */
	public String toString() {
		Map<Integer, String> conversionNumberToLetterRow = new HashMap<>();
		char[] rows = "ABCDEFGHIJKLM".toCharArray();
		for(int i=0; i<rows.length;i++) {
			conversionNumberToLetterRow.put(i, String.valueOf(rows[i]));
		}
		rowStart++;
		rowTarget++;
		return conversionNumberToLetterRow.get(colStart)+""+rowStart+"-"+conversionNumberToLetterRow.get(colTarget)+""+rowTarget;
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
}
