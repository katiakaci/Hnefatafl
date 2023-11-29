import java.util.HashMap;
import java.util.Map;

public class MapConversion {
	private static Map<Integer, String> conversionNumberToLetterColumn = new HashMap<>();
	private static Map<String, Integer> conversionLetterToNumberColumn = new HashMap<>();
	private static Map<Integer, Integer> conversionLetterToNumberRow = new HashMap<>(), conversionNumberToLetterRow = new HashMap<>();

	private static MapConversion instanceMapConversion = new MapConversion();

	private MapConversion() {
		// Créer deux maps permettant de convertir une lettre en indice de tableau et vice-versa
		char[] rows = "ABCDEFGHIJKLM".toCharArray();
		for(int i = 0; i < rows.length; i++) {
			conversionNumberToLetterColumn.put(i, String.valueOf(rows[i]));
			conversionLetterToNumberColumn.put(String.valueOf(rows[i]), i);
		}
		for(int i = 1; i <= 13; i++) conversionLetterToNumberRow.put(i, i-1);
		for(int i = 0; i <= 12; i++) conversionNumberToLetterRow.put(i, i+1);
	}

	public static Map<Integer, String> getConversionNumberToLetterColumn() {
		return conversionNumberToLetterColumn;
	}

	public static Map<String, Integer> getConversionLetterToNumberColumn() {
		return conversionLetterToNumberColumn;
	}

	public static Map<Integer, Integer> getConversionLetterToNumberRow() {
		return conversionLetterToNumberRow;
	}

	public static Map<Integer, Integer> getConversionNumberToLetterRow() {
		return conversionNumberToLetterRow;
	}

	public static MapConversion getInstanceMapConversion() {
		return instanceMapConversion;
	}

}
