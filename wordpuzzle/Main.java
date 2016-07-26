package wordpuzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * The word puzzle challenge
 * Reddit thread - https://www.reddit.com/r/TheHappieMakers/comments/4bb8lp/game_dev_challenge_word_puzzles/
 * @author Itamar
 *
 */
public class Main {

	private static Map<String, List<Point[]>> wordToPaths; //all possible paths for each word in puzzle will be stored here
	
	public static void main(String args[]) throws IOException{
		
		wordToPaths = new HashMap<String, List<Point[]>>();
		
		char[][] puzzle2DArr = getPuzzle2DArr(); //get 2dArr of puzzle's chars
		print2DArr(puzzle2DArr); //print it to screen
		
		String[] wordBank = getWordBankArr("wordbank.txt"); //init array of words from wordbank
		System.out.println(Arrays.toString(wordBank));
		TrieNode trieRoot = TrieTreeUtils.createFrom(wordBank); //create a TRIE tree of all words in the bank. used for searching words in the puzzle's grid
		
		searchForAllPuzzle(puzzle2DArr, trieRoot); //searches for words from the bank in the puzzle. stores results in 'wordToPaths'
		
		printAllResultsFromMap();
		
		System.out.println("\nMaximal um of words withought overlapping: " + Part3.maximumNumberWithoutOverlapping(wordToPaths, puzzle2DArr.length));
		
				
	}
	
	/**
	 * prints each word and its path from 'wordToPathArr' map
	 */
	private static void printAllResultsFromMap() {
		for(String word : wordToPaths.keySet()){
			for(Point[] path : wordToPaths.get(word)){
				System.out.println(word + " :" + Arrays.toString(path));
			}
		}
	}

	/**
	 * searches all puzzle grid for word matching
	 * stores results (possible paths for each word) in the 'wordToPaths' map
	 */
	private static void searchForAllPuzzle(char[][] puzzle2DArr, TrieNode trieRoot) {
		//loop through all chars in the puzzle's grid
		for(int i = 0; i < puzzle2DArr.length; i++){
			for(int j = 0; j < puzzle2DArr[i].length; j++){
				char current = puzzle2DArr[i][j];
				TrieNode nodeForCurrentChar = trieRoot.getChild(current);
				if(nodeForCurrentChar == null) //if trie tree doesn't have a word that starts with current char - continue to next point on the grid
					continue;
				
				ArrayList<Point> path = new ArrayList<Point>();
				path.add(new Point(i,j));
				
				//init visted array & set all its values to false
				boolean[][] visited = new boolean[puzzle2DArr.length][puzzle2DArr[i].length];
				for (int k = 0; k < visited.length; k++) {
					for (int k2 = 0; k2 < visited[k].length; k2++) {
						visited[k][k2] = false;
					}
				}
				visited[i][j] = true; //mark visit in current point
					
				//start recursive paths search for current position in the puzzle grid
				gridTrieSeachRec(puzzle2DArr, nodeForCurrentChar, path, visited, i, j);

				visited[i][j] = false;//unmark visit in current point
				
			}
		}
	}
	
	
	/**
	 * recursively finds paths on puzzle that form words in word bank <p> saves path's points in path arr
	 * @param puzzle2dArr - 2D array of puzzle
	 * @param trieNode - node in TRIE for current path (its value is the same as the char in given position in the grid)
	 */
	private static void gridTrieSeachRec(char[][] puzzle2dArr, TrieNode trieNode, ArrayList<Point> path, boolean[][] visited, int i, int j) {
		//stop condition - when there are no neighbor points left to proceed to
		
		/*
		 * if current trieNode has a '$' child - it means that we successfully formed a word with current path
		 * store the word's path in map
		 */
		if(trieNode.hasChildValue('$')){
			String word = getWordFromPath(puzzle2dArr, path);
			//add curent path to list of possible paths for this word
			List<Point[]> currentPathsForWord = wordToPaths.get(word);
			if(currentPathsForWord == null)
				currentPathsForWord = new ArrayList<Point[]>();
			
			currentPathsForWord.add(path.toArray(new Point[path.size()]));
			
			wordToPaths.put(word, currentPathsForWord);
			
		}
		
		//get all available neighbor points - points that are adjacent to current point, haven't been visited & are continuing a word in the TRIE tree
		List<Point> availPoints = getAvailPointsStrict(puzzle2dArr, trieNode, visited, path,i, j); //list of points that are neighbors of this point & are continuing a word in the TRIE tree
		
		//loop through available points and continue their paths recursively
		for(Point p : availPoints){
			visited[p.i][p.j] = true; //mark point visited
			path.add(p); //add point to current path
			gridTrieSeachRec(puzzle2dArr, trieNode.getChild(puzzle2dArr[p.i][p.j]), path, visited, p.i, p.j);
			visited[p.i][p.j] = false;//un mark point visited
			path.remove(p); //remove point from current path
		}
	}

	/**
	 * returns word created by following points on the puzzle in given path
	 */
	private static String getWordFromPath(char[][] puzzle2dArr, ArrayList<Point> path) {
		String word = "";
		for(int i = 0; i < path.size(); i++){
			word += puzzle2dArr[path.get(i).i][path.get(i).j];
		}
		return word;
	}

	/**
	 * returns list of all available neighbor points - points that are adjacent to given point, haven't been visited & are continuing a word in the TRIE tree
	 */
	private static List<Point> getAvailPoints(char[][] puzzle2dArr, TrieNode trieNode, boolean[][] visited, int i, int j) {
		List<Point> availPoints = new ArrayList<Point>();
		//add avail points that are surrounding current point

		Point[] surroundingPoints = new Point[]{
				new Point(i-1,j),
				new Point(i-1,j+1),
				new Point(i, j+1),
				new Point(i+1,j+1),
				new Point(i+1,j),
				new Point(i+1,j-1),
				new Point(i,j-1),
				new Point(i-1,j-1),
		};
		
		for(Point p : surroundingPoints){
			if(canProgressSearchToPoint(puzzle2dArr, trieNode, visited, p.i, p.j)){
				availPoints.add(p);
			}
		}
	
		return availPoints;
	}
	
	/**
	 * returns list of all available neighbor points - points that are:
	 * a. adjacent to the given point
	 * b. haven't been visited (marked in path)
	 * c.are continuing a word in the TRIE tree 
	 * <p>
	 * points must be on a strict path - only a single straight/diagonal line connects points in the path
	 */
	private static List<Point> getAvailPointsStrict(char[][] puzzle2dArr, TrieNode trieNode, boolean[][] visited, ArrayList<Point> path, int i, int j) {
		List<Point> availPoints = new ArrayList<Point>();
		
		//create array of  points that are surrounding current point
		Point[] surroundingPoints;
		if(path.size() < 2){ //cannot determine straight line/diagonal yet - all adjacent points are possible
			surroundingPoints = new Point[]{
					new Point(i-1,j),
					new Point(i-1,j+1),
					new Point(i, j+1),
					new Point(i+1,j+1),
					new Point(i+1,j),
					new Point(i+1,j-1),
					new Point(i,j-1),
					new Point(i-1,j-1),
		};
		}else{ //only 1 available point - muse continue that path's line/diagonal
			int delta_i = path.get(1).i - path.get(0).i, delta_j = path.get(1).j - path.get(0).j;
			surroundingPoints = new Point[]{
					new Point(i + delta_i, j + delta_j)
			};
		}
		
		for(Point p : surroundingPoints){
			if(canProgressSearchToPoint(puzzle2dArr, trieNode, visited, p.i, p.j)){
				availPoints.add(p);
			}
		}
	
		return availPoints;
	}
	
	/**
	 * checks if can progress to point in word puzzle - if point is with a valid index, hasn't been visited yet & is continuing a word in the TRIE tree
	 */
	private static boolean canProgressSearchToPoint(char[][] puzzle2dArr, TrieNode trieNode, boolean[][] visited, int i, int j){
		return (i >= 0 && i < puzzle2dArr.length && j>= 0 && j < puzzle2dArr[i].length 
				&& !visited[i][j] && trieNode.hasChildValue(puzzle2dArr[i][j]));
	}



	/**
	 * returns String arr of words in the word bank
	 * @param FILE_NAME - name of file to get words from
	 */
	private static String[] getWordBankArr(final String FILE_NAME) throws FileNotFoundException {
		Scanner s = new Scanner(new File(FILE_NAME));
		List<String> words = new ArrayList<String>();
		while(s.hasNextLine()){
			String word = s.nextLine();
			if(word.length() >= 3)
				words.add(word.toUpperCase() + "$");
		}
		s.close();
		return words.toArray(new String[words.size()]);
	}
	
	
	/**
	 * prints given 2D char arr
	 */
	private static void print2DArr(char[][] arr){
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				System.out.print(arr[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("\n\n");
	}
	
	/**
	 * counter lines in puzzle txt file
	 * @return number of lines in puzzle
	 * @throws FileNotFoundException
	 */
	private static int getPuzzleLines() throws FileNotFoundException{
		int counter = 0;
		File f = new File("thepuzzle.txt");
		Scanner fileScanner = new Scanner(f);
		while(fileScanner.hasNextLine()){
			fileScanner.nextLine();
			counter++;
		}
		fileScanner.close();
		return counter;
	}
	
	/**
	 * returns 2D arr of chars in puzzle, created from thepuzzle.txt file
	 * @return
	 * @throws IOException
	 */
	private static char[][] getPuzzle2DArr() throws IOException{

		File f = new File("thepuzzle.txt");
		Scanner fileScanner = new Scanner(f);
		//instantiate puzzle arr
		final int LINES = getPuzzleLines();
		char[][] puzzle2DArr = new char[LINES][LINES];
		
		//iterate through puzzle txt file and parse it to char 2D arr
		int lineNum = 0;
		while(fileScanner.hasNextLine()){ //scan each line
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			lineScanner.useDelimiter("\t");
			int charInLineNum = 0;
			while(lineScanner.hasNext()){
				char c =  lineScanner.next().charAt(0);
				if(c == ' ')
					continue;
				puzzle2DArr[lineNum][charInLineNum] = c; //put current char in 2D arr
				charInLineNum++;
			}
			lineNum++;
		}
		return puzzle2DArr;
	}
	
	public static void printWordFromPath(ArrayList<Point> path, char[][] puzzle2DArr){
		String str = "";
		for(Point p : path){
			str += p + " : " + puzzle2DArr[p.i][p.j] + ",";
		}
		System.out.println(str);
	}
	
}
