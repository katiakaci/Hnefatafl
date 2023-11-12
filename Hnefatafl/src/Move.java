
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
