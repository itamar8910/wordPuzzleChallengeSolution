package wordpuzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * solution to part 3  of the word puzzle challenge 
 * note: solution needs to be optimized
 * @author Itamar
 *
 */
public class Part3 {

	public static int maximumNumberWithoutOverlapping(Map<String, List<Point[]>> wordToPaths, final int BOARD_SIZE){
		//init used points arr
		boolean[][] usedPoints = new boolean[BOARD_SIZE][BOARD_SIZE];
		for (int i = 0; i < usedPoints.length; i++) {
			for (int j = 0; j < usedPoints[i].length; j++) {
				usedPoints[i][j] = false;
			}
		}
		
		String[] words = (new ArrayList<String>(wordToPaths.keySet())).toArray(new String[wordToPaths.keySet().size()]);
		int[] opts = new int[words.length];
		for(int i = 0; i < opts.length; i++){
			opts[i] = -1;
		}
		
		return opt(wordToPaths, words, usedPoints,  0);

	}

	
	/**
	 * recursive func
	 * returns the MAX number of words paths that can fit without overlapping each other, for words i to n
	 * note: solution needs to be optimized
	 */
	private static int opt(Map<String, List<Point[]>> wordToPaths, String[] words, boolean[][] usedPoints,  int i) {
	
		if(i == words.length)
			return 0;
		
		List<Point[]> pathsForWord = wordToPaths.get(words[i]);
		
		int max = 0;
		//loop through each possible word path
		for(Point[] path : pathsForWord){
			if(intersectsUsed(path, usedPoints))
				continue;
			
			markPath(path, usedPoints, true);
			
			int resultForPath = opt(wordToPaths, words, usedPoints, i + 1) + 1;
			if(resultForPath > max)
				max = resultForPath;
			
			markPath(path, usedPoints, false);
		}
		//case where no path for word is used
		int resultNoPath = opt(wordToPaths, words, usedPoints, i + 1);
		if(resultNoPath > max)
			max = resultNoPath;
		
		return max;
	}

	/**
	 * marks given path with true(used) or false(unused)
	 */
	private static void markPath(Point[] path, boolean[][] usedPoints, boolean b) {
		for(Point p : path){
			usedPoints[p.i][p.j] = b;
		}	
	}

	/**
	 * checks if given path intersects any used points
	 */
	private static boolean intersectsUsed(Point[] path, boolean[][] usedPoints) {
		for(Point p : path){
			if(usedPoints[p.i][p.j])
				return true;
		}
		return false;
	}
	
	/**
	 * counts number of total possible paths marked in the puzzle
	 * @param wordToPaths
	 * @return
	 */
	public static int countNumOfPaths(Map<String, List<Point[]>> wordToPaths){
		int count = 0;
		for(String word : wordToPaths.keySet()){
			count += wordToPaths.get(word).size();
		}
		return count;
	}
	
}
