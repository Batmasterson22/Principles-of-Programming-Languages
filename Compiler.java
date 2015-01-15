//Jason R Hodges 
//CSE 340 - Summer 2014
//Project 04 - Code Generation

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.*;

public class Compiler {
	
  private static Vector<Token> tokens;  
  private static ArrayList<String> tempArr;
  
  private static String[] split(String line) {
	  
    String words = "";
    tempArr = new ArrayList<String>();
    boolean sFlag = false;
    char current = ' ';
    int x = 0;
    
    for (x = 0; x <= line.length()-1; x++){
    	current = line.charAt(x);
    	
    	if (current !='\t')
    	{
	    	
	    	if (current == '\"' )				//Used for multiple quotes error checks
	    		sFlag = true;
	    	if (current == '\'')
	    		sFlag = true;    	
	    	
	    	
	    	//Enters this statement if whitespace, delimiter, or operator is the current character of the string
	    	//and the quote flag needs to be set to false meaning a one quote has been seen
		  	if ((current == ' ' || Lexer.isDelimiter(current) || Lexer.isOperator(current)) && sFlag == false && current != '!') {
	    		
		  		if (current == '=' && words.equals("!"))
		  			words = words + current;   		//Add current character to the current word string
		  		
	    		tempArr.add(words);  			//Add current word to the temp arrayList
	    		    		
	    		if (Lexer.isDelimiter(current) && current != ' '){
	    			words = new Character(current).toString();	//Makes char into string
	    			tempArr.add(words);							//Add current word to the temp arrayList
	    		}
	    		else if (Lexer.isOperator(current) && current != ' '){
	    			if (!words.equals("!=")) {
		    			words = new Character(current).toString();	//Makes char into string
		    			tempArr.add(words);							//Add current word to the temp arrayList
		    		}
	    		}   		
	    		    		
	    		words = "";						//Used to rest word after it is in the arrayList
	    	}
		  	else {
	    		words = words + current;   		//Add current character to the current word string
	    	}  	  
		  	
		  	//Character Pattern for '.'
		  	String chaString = ("^\'.\'");
		  	Pattern chaPattern = Pattern.compile(chaString);
		  	Matcher chaMatch = chaPattern.matcher(words);
		  	
		  	//Character2 Pattern for '\''
		  	String cha2String = ("^\'\\\\.\'");
		  	Pattern cha2Pattern = Pattern.compile(cha2String);
		  	Matcher cha2Match = cha2Pattern.matcher(words);
		  	
	    	//String Pattern for ".*"
		  	String strString = ("^\".*\"");
		  	Pattern strPattern = Pattern.compile(strString);
		  	Matcher strMatch = strPattern.matcher(words);
		  	
		  	//Not Character Pattern describes errors that start with \' but doesn't match the char def
		  	String notString = ("^\'..+\'");
		  	Pattern notPattern = Pattern.compile(notString);
		  	Matcher notMatch = notPattern.matcher(words);
		  	
		  	//Not Character2 Pattern describes errors that start with \' but doesn't match the char def
		  	String not2String = ("^\'\'");
		  	Pattern not2Pattern = Pattern.compile(not2String);
		  	Matcher not2Match = not2Pattern.matcher(words);
	
		  	//If the it doesn't match a char and a single quote happened do this 
		  	if (notMatch.find() && sFlag == true){
		  		tempArr.add(words);			//add the word to the tempArray
	    		words = "";	  				//set word to empty
	    		sFlag = false;				//Set quote error check to false
		  	}
		  	//If the it doesn't match a char and a single quote happened do this 
		  	if (not2Match.find() && sFlag == true){
		  		tempArr.add(words);			//add the word to the tempArray
	    		words = "";	  				//set word to empty	
		  	}	  	
		  	
		  	//Checks for '\' and "\"" and if they are do nothing
		  	if (words.equals("'\\'") || words.equals("\"\\\"")){	  		
		  	}else if (strMatch.find() || chaMatch.find() || cha2Match.find()){
		  		sFlag = false;	  			//Set quote error check to false
	    		tempArr.add(words);			//add the word to the tempArray
	    		words = "";					//set word to empty
		  	}	  	
    	}
    }
    
    tempArr.add(words);					//add the word to the tempArray
    words = words + current;			//makes new word from words and the current char
    
    tempArr.removeAll(Collections.singleton(""));	//Remove spaces from the tempArray
    
    //Adds tempArray to a string array
    String [] strings = tempArr.toArray(new String[tempArr.size()]);   
            
    return strings;
  }
  
  public static void main(String[] args) throws FileNotFoundException, IOException {

    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    //Writer out = new OutputStreamWriter(new FileOutputStream("outSemantic.txt")); 
    tokens = new Vector<Token>();
    
    int totalLexicalErrors = 0;
    int lineNum = 0;
    
    try {                  
      String line = br.readLine();   
      while (line != null) {  
    	  lineNum++;
          String[] strings = split (line);
          for (String string : strings) {
        	  String token = Lexer.lexer(string);        	 
        	  tokens.add(new Token(string, token, lineNum));
        	  if (token.equals("ERROR")) {
        		  totalLexicalErrors++;
        	  }
          }
          
        line = br.readLine();  
      }        
    } finally {    	   	
     	
    	//Enumeration<Token> en = tokens.elements();
    	//while (en.hasMoreElements()){
    	//	Token tkn = (Token)en.nextElement();
    	//	System.out.println(tkn.getToken() + " " + tkn.getWord() + " " + tkn.getLine() +  "\n\n");
    	//	out.write(tkn.getToken() + " " + tkn.getWord() + " " + tkn.getLine() + "\n\n");    		
    	//}
    	
      br.close();
      //out.close();
    }   
    
    Parser toParse = new Parser();
    toParse.parse(tokens, args);
    
    
  }
  
}
