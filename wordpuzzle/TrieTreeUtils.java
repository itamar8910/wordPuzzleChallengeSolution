package wordpuzzle;

public class TrieTreeUtils {

	/**
	 * creates a TRIE tree from given strings
	 * @param words - array of strings in the TRIE
	 * @return
	 */
	public static TrieNode createFrom(String[] words){
		TrieNode root = new TrieNode('_'); //root of TRIE will have '_' value
		for(String word : words){ //add each words to TRIE
			addWord(root,word);
		}
		return root;
	}

	/**
	 * adds given word to TRIE tree recursively
	 */
	private static void addWord(TrieNode node, String word) {
		char c = word.charAt(0);
		if(node.hasChildValue(c)){ //if TRIE already has a child with first char of the word
			if(word.length() > 1) //continue to add rest of the word recursively
				addWord(node.getChild(c), word.substring(1));
		}else{
			TrieNode newNode = node.addChild(c); //add new child node to current TRIE node
			if(word.length() > 1) //continue to add rest of the word recursively
				addWord(newNode, word.substring(1));
		}
	}
	
	public static boolean hasWord(TrieNode root, String word){
		while(root.hasChildValue(word.charAt(0))){
			root = root.getChild(word.charAt(0));
			if(word.length() == 1){
				return true;			
			}
			word = word.substring(1);
		}
		return false;
	}
	
	public static void print(TrieNode node){
		node.printSubTree();
	}
	
}
