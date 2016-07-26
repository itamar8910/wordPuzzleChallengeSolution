package wordpuzzle;

import java.util.HashMap;
import java.util.Map;

/**
 * represents a node in a TRIE tree
 * @author Itamar
 */
public class TrieNode {
	public char value;
	public Map<Character, TrieNode> children;
	
	public TrieNode(char value){
		this.value = value;
		this.children = new HashMap<Character, TrieNode>();
	}
	
	/**
	 * returns true if this node has a child with given value
	 * @param c
	 * @return
	 */
	public boolean hasChildValue(char c){
		return this.children.containsKey(new Character(c));
	}
	
	/**
	 * adds a child to this node with given value <p>
	 * NOTE - if already has a child(with no children) with given value - overrides it with new child
	 * @param c
	 * @return child that was created
	 */
	public TrieNode addChild(char c){
		TrieNode newChild = new TrieNode(c);
		this.children.put(c, newChild);
		return newChild;
	}
	
	/**
	 * returns child of this node with given character / null if not found
	 */
	public TrieNode getChild(char c){
		return this.children.get(c);
	}
	
	public void printSubTree(){
		if(this.value == '$')
			System.out.println();

		System.out.print(this.value + ",");
		for(Character c : this.children.keySet()){
			this.children.get(c).printSubTree();
		}
	}
	
}
