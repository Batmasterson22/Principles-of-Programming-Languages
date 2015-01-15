//Jason R Hodges - 1205172549 
//CSE 340 - Summer 2014
//Project 04 - Code Generation

import java.util.regex.*;

public class Lexer {
 
  private final static String[] keywords = {"if", "else", "WHILE", "SWITCH", "CASE", "return", "integer", "float", "void", "char", "string", "boolean", "true", "false", "print", "DEFAULT"};
  
  public static String lexer(String string) {
	  
	  for (int d = 0; d <= keywords.length-1; d++){
		  if (string.equals(keywords[d]))
			  return "KEYWORD";		  
	  }	  
	  
	  //Checks the string for an operator or delimiter
	  char tempC = string.charAt(0);
	  if (Lexer.isDelimiter(tempC))
		  return "DELIMITER";
	  else if (Lexer.isOperator(tempC))
		  return "OPERATOR";	  	
	  
	  //The following are the varies regular expression for the required catagories
	  //Identifier Pattern
	  String idString = ("^[a-zA-Z_$][a-zA-Z0-9_$]*$");
	  Pattern idPattern = Pattern.compile(idString);
	  Matcher idMatch = idPattern.matcher(string);
	  
	  String idString2 = ("[a-zA-Z_$]$");
	  Pattern idPattern2 = Pattern.compile(idString2);
	  Matcher idMatch2 = idPattern2.matcher(string);
	  	  
	  //Float Pattern
	  String floString = ("^[0-9]*\\.[0-9]*$");
	  Pattern floPattern = Pattern.compile(floString);
	  Matcher floMatch = floPattern.matcher(string);
	  
	  //Float E Pattern
	  String floEString = ("^[0-9]*\\.[0-9]*e[0-9]+$");
	  Pattern floEPattern = Pattern.compile(floEString);
	  Matcher floEMatch = floEPattern.matcher(string);
	  
	  //Integer Pattern
	  String intString = ("^[1-9][0-9]*$");
	  Pattern intPattern = Pattern.compile(intString);
	  Matcher intMatch = intPattern.matcher(string);
	  
	  //Integer Pattern for 0
	  String intZString = ("^0$");
	  Pattern intZPattern = Pattern.compile(intZString);
	  Matcher intZMatch = intZPattern.matcher(string);
	  
	  //String Pattern
	  String strString = ("^\"(.*)\"$");
	  //String strString = ("\"(.*\\.)\"");
	  Pattern strPattern = Pattern.compile(strString);
	  Matcher strMatch = strPattern.matcher(string);
	  
	  //Operator Pattern
	  String optString = ("\\+\\-\\*\\/\\%");
	  Pattern optPattern = Pattern.compile(optString);
	  Matcher optMatch = optPattern.matcher(string);
	  
	  //Hexadecimal Pattern
	  String hexString = ("^0x[A-F0-9]+$");
	  Pattern hexPattern = Pattern.compile(hexString);
	  Matcher hexMatch = hexPattern.matcher(string);
	  
	  //Octal Pattern
	  String octString = ("^0[0-7]+$");
	  Pattern octPattern = Pattern.compile(octString);
	  Matcher octMatch = octPattern.matcher(string);
	  
	  //Binary Pattern
	  String binString = ("^0b[01]*$");
	  Pattern binPattern = Pattern.compile(binString);
	  Matcher binMatch = binPattern.matcher(string);	  
	  
	  //Character Pattern
	  String chaString = ("^\'.\'$");
	  Pattern chaPattern = Pattern.compile(chaString);
	  Matcher chaMatch = chaPattern.matcher(string);  	 	
	  
	  //Character Pattern #2
	  String cha2String = ("^\'\\\\[a-z'\"]\'$");
	  Pattern cha2Pattern = Pattern.compile(cha2String);
	  Matcher cha2Match = cha2Pattern.matcher(string);  
	  
	  //This section is used to return correct token for the given string
	  if (idMatch.find())
		  return "IDENTIFIER";
	  
	  else if (idMatch2.find())
		  return "IDENTIFIER";
	  
	  else if (floEMatch.find())
		  return "FLOAT";
	  
	  else if (floMatch.find())
		  return "FLOAT";
	  
	  else if (intMatch.find())
		  return "INTEGER";
	  
	  else if (intZMatch.find())
		  return "INTEGER";
	  
	  else if (strMatch.find())
		  return "STRING";
	  
	  else if (optMatch.find())
		  return "OPERATOR";
	  
	  else if (hexMatch.find())
		  return "HEXADECIMAL";
	  
	  else if (octMatch.find())
		  return "OCTAL";
	  
	  else if (binMatch.find())
		  return "BINARY";
	  
	  else if (chaMatch.find())
		  return "CHARACTER";
	  
	  else if (cha2Match.find())
		  return "CHARACTER";
	  
	 
   return "ERROR";
  }

  public static boolean isDelimiter(char c) {
     char [] delimiters = {':',';', ' ', '}','{', '[',']','(',')',','};
     for (int x=0; x<delimiters.length; x++) {
      if (c == delimiters[x]) return true;      
     }
     return false;
  }
  
  public static boolean isOperator(char o) {
     char [] operators = {'+', '-', '*','/', '%','<','>','=','!','&','|'};
     for (int x=0; x<operators.length; x++) {
      if (o == operators[x]) return true;      
     }
     return false;
  }

}
