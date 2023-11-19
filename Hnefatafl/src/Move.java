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
		int rowStartPlusOne = rowStart + 1;
		int rowTargetPlusOne = rowTarget + 1;
		return conversionNumberToLetterRow.get(colStart)+""+rowStartPlusOne+"-"+conversionNumberToLetterRow.get(colTarget)+""+rowTargetPlusOne;
	}

	public int getRowStart(){
		return rowStart;
	}

	public int getColStart(){
		return colStart;
	}

	public int getRowTarget() {
		return rowTarget;
	}

	public int getColTarget() {
		return colTarget;
	}

}
