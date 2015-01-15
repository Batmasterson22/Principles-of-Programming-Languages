//Jason R Hodges - 1205172549 
//CSE 340 - Summer 2014
//Project 04 - Code Generation

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class Semantic {
	
	Hashtable<String, Vector<String>> haTa;
	Vector<String> veSt, theValues;
	String[][][] cube;
	Vector<Token> tokens;
	Enumeration<Token> en;
	Enumeration<String> enStr;
	String scope = "", temp2 = "", theKey = "", temp3 = "";
	Stack<String> registry;
	int firNum = 0, secNum = 0, thiNum = 0;
	
	//This are used to make the 3D array easier to read
	public static final int INTEGER = 0;
	public static final int BOOLEAN = 1;
	public static final int VOID = 2;
	public static final int ERROR = 3;
	public static final int OP_MATH = 0;
	public static final int OP_COMP = 1;
	public static final int OP_EQUALS = 2;
	public static final int OP_NOTEQUAL = 3;
	
	//This method is ran at the being of the parser and is used to build the 3D array
	//and initilize the various structures to be used
	public void SemanticStart() {
		
		haTa = new Hashtable<String, Vector<String>>();
		registry = new Stack<String>();
		cube = new String[4][4][4];
		
		//This fills the 3D array
		make3DArray();
		
	}
	
	//This method is used to initilize the 3D array to the various values
	private void make3DArray() {
		
		//This block handles +, -, *, /
		cube[INTEGER][OP_MATH][INTEGER] = "integer";
		cube[INTEGER][OP_MATH][BOOLEAN] = "error";
		cube[INTEGER][OP_MATH][VOID] = "error";
		cube[INTEGER][OP_MATH][ERROR] = "error";
		cube[BOOLEAN][OP_MATH][INTEGER] = "error";
		cube[BOOLEAN][OP_MATH][BOOLEAN] = "error";
		cube[BOOLEAN][OP_MATH][VOID] = "error";
		cube[BOOLEAN][OP_MATH][ERROR] = "error";
		cube[VOID][OP_MATH][INTEGER] = "error";
		cube[VOID][OP_MATH][BOOLEAN] = "error";
		cube[VOID][OP_MATH][VOID] = "error";
		cube[VOID][OP_MATH][ERROR] = "error";
		cube[ERROR][OP_MATH][INTEGER] = "error";
		cube[ERROR][OP_MATH][BOOLEAN] = "error";
		cube[ERROR][OP_MATH][VOID] = "error";
		cube[ERROR][OP_MATH][ERROR] = "error";
		
		//This block handles >, <
		cube[INTEGER][OP_COMP][INTEGER] = "boolean";
		cube[INTEGER][OP_COMP][BOOLEAN] = "error";
		cube[INTEGER][OP_COMP][VOID] = "error";
		cube[INTEGER][OP_COMP][ERROR] = "error";
		cube[BOOLEAN][OP_COMP][INTEGER] = "error";
		cube[BOOLEAN][OP_COMP][BOOLEAN] = "error";
		cube[BOOLEAN][OP_COMP][VOID] = "error";
		cube[BOOLEAN][OP_COMP][ERROR] = "error";
		cube[VOID][OP_COMP][INTEGER] = "error";
		cube[VOID][OP_COMP][BOOLEAN] = "error";
		cube[VOID][OP_COMP][VOID] = "error";
		cube[VOID][OP_COMP][ERROR] = "error";
		cube[ERROR][OP_COMP][INTEGER] = "error";
		cube[ERROR][OP_COMP][BOOLEAN] = "error";
		cube[ERROR][OP_COMP][VOID] = "error";
		cube[ERROR][OP_COMP][ERROR] = "error";
		
		//This block handles '='
		cube[INTEGER][OP_EQUALS][INTEGER] = "integer";
		cube[INTEGER][OP_EQUALS][BOOLEAN] = "error";
		cube[INTEGER][OP_EQUALS][VOID] = "error";
		cube[INTEGER][OP_EQUALS][ERROR] = "error";
		cube[BOOLEAN][OP_EQUALS][INTEGER] = "error";
		cube[BOOLEAN][OP_EQUALS][BOOLEAN] = "boolean";
		cube[BOOLEAN][OP_EQUALS][VOID] = "error";
		cube[BOOLEAN][OP_EQUALS][ERROR] = "error";
		cube[VOID][OP_EQUALS][INTEGER] = "error";
		cube[VOID][OP_EQUALS][BOOLEAN] = "error";
		cube[VOID][OP_EQUALS][VOID] = "error";
		cube[VOID][OP_EQUALS][ERROR] = "error";
		cube[ERROR][OP_EQUALS][INTEGER] = "error";
		cube[ERROR][OP_EQUALS][BOOLEAN] = "error";
		cube[ERROR][OP_EQUALS][VOID] = "error";
		cube[ERROR][OP_EQUALS][ERROR] = "error";
		
		//This block handles '!='
		cube[INTEGER][OP_NOTEQUAL][INTEGER] = "boolean";
		cube[INTEGER][OP_NOTEQUAL][BOOLEAN] = "error";
		cube[INTEGER][OP_NOTEQUAL][VOID] = "error";
		cube[INTEGER][OP_NOTEQUAL][ERROR] = "error";
		cube[BOOLEAN][OP_NOTEQUAL][INTEGER] = "error";
		cube[BOOLEAN][OP_NOTEQUAL][BOOLEAN] = "boolean";
		cube[BOOLEAN][OP_NOTEQUAL][VOID] = "error";
		cube[BOOLEAN][OP_NOTEQUAL][ERROR] = "error";
		cube[VOID][OP_NOTEQUAL][INTEGER] = "error";
		cube[VOID][OP_NOTEQUAL][BOOLEAN] = "error";
		cube[VOID][OP_NOTEQUAL][VOID] = "error";
		cube[VOID][OP_NOTEQUAL][ERROR] = "error";
		cube[ERROR][OP_NOTEQUAL][INTEGER] = "error";
		cube[ERROR][OP_NOTEQUAL][BOOLEAN] = "error";
		cube[ERROR][OP_NOTEQUAL][VOID] = "error";
		cube[ERROR][OP_NOTEQUAL][ERROR] = "error";
		
		
	}
	
	//This method is used to check if the passed string is a key in the hashtable
	public boolean checkHash(String chHash) {
		
		//Checks the Hashtable for key chHash
		if (haTa.containsKey(chHash))
			return true;
		
		return false;
	}

	//This method is used to add the passed variables to the Hashtable
	public void addToHash(String theID, String theType, String scope) {
		
		//This block creates a String Vector from the theType and scope 
		veSt = new Vector<String>();			
		veSt.add(theType);
		veSt.add(scope);
		
		//Adds key and string vector to the hashtable where theID is the key
		haTa.put(theID, veSt);					
			
	}
	
	//This method is used to add the passed variables to the Stack
	public void pushStack(String toPush) {
		
		//This pushes toPush on to the Stack
		registry.push(toPush);		
		
	}
	
	//This method is used to remove the top variables of the Stack and return it
	public String popStack() {
		
		//This pops the top element from the stack and returns it
		return registry.pop();
	}
	
	//This method is used to check the the 3D array for the passed variables and return the value
	public String calculateTypeBinary(String fir, String sec, String thi) {
		
		String theReturn = "";
		
		//This block sets the incoming string fir to the appropriate public static final int
		if (fir.equals("integer"))
			firNum = INTEGER;
		else if (fir.equals("boolean"))
			firNum = BOOLEAN;
		else if (fir.equals("void"))
			firNum = VOID;
		else if (fir.equals("error"))
			firNum = ERROR;
		
		//This block sets the incoming string sec to the appropriate public static final int
		if (sec.equals("+") || sec.equals("-") || sec.equals("*") || sec.equals("/"))
			secNum = OP_MATH;
		else if (sec.equals("<") || sec.equals(">"))
			secNum = OP_COMP;
		else if (sec.equals("="))
			secNum = OP_EQUALS;
		else if (sec.equals("!="))
			secNum = OP_NOTEQUAL;
		
		//This block sets the incoming string thi to the appropriate public static final int
		if (thi.equals("integer"))
			thiNum = INTEGER;
		else if (thi.equals("boolean"))
			thiNum = BOOLEAN;
		else if (thi.equals("void"))
			thiNum = VOID;
		else if (thi.equals("error"))
			thiNum = ERROR;
		
		//Gets the value from the cube array from the passed variables 
		theReturn = cube[firNum][secNum][thiNum];
		
		return theReturn;
	}
	
	//This is used to get the first value of the vector in the Hashtable at key
	public String getValue (String key) {
		
		theKey = "";
		theValues = haTa.get(key);
		enStr = theValues.elements();
		
		//This gets the first element of the string vector
		if (enStr.hasMoreElements()) {
			theKey = enStr.nextElement();
		}
		
		return theKey;
	}
	
	public boolean isEmpty() {
		
		if (registry.empty())
			return true;
		
		return false;
	}
	
}
