
class Move{
	private int rowStart, colStart, rowTarget, colTarget;

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
		int rowStartPlusOne = rowStart + 1;
		int rowTargetPlusOne = rowTarget + 1;
		return MapConversion.getConversionNumberToLetterColumn().get(colStart)+""+rowStartPlusOne+"-"
				+MapConversion.getConversionNumberToLetterColumn().get(colTarget)+""+rowTargetPlusOne;
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
