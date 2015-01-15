//Jason R Hodges 
//CSE 340 - Summer 2014
//Project 04 - Code Generation

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class Parser {
	
	Vector<Token> tokens;
	Enumeration<Token> en;
	Token tkn, tkn2;
	int lineNum, tempLine, endCount, counter, countBody, assemCount, zxc, jumpCount,
	whileCount, caseCount;
	boolean someEnd, whileEnd;
	String errorCheck;
	PrintWriter out;
	String[] keywords = {"if", "else", "WHILE", "SWITCH", "CASE", "return", "integer", "float", "void", "char", "string", "boolean", "true", "false", "print", "DEFAULT"};
	String theType, scope, temp1, temp2, oper, tempResult1, tempResult2, tempResult4, tempToken,
	tempString1, tempString2, tempString3, tempString4, tempString5, tempString6, tempString7,
	theCase, whichCase;
	Semantic toSem = new Semantic();
	Vector<String> tempVec = new Vector<String>();
	ArrayList<String> globalVar, jumpLines, assemCode;
	Stack<Integer> jumpStack = new Stack<Integer>();
	ArrayList<Integer> jumpNum;
	
	
	public void parse(Vector<Token> tokens, String[] args) throws FileNotFoundException{
		
		//Initilize all of the variables used in this class
		this.tokens = tokens;
		en = tokens.elements();
		tkn = (Token)en.nextElement();
		lineNum = tkn.getLine();
		tempLine = tkn.getLine();
		endCount = 0;
		assemCount = 0;
		zxc = 0;
		jumpCount = 0;
		whileCount = 0;
		out = new PrintWriter(args[1]);
		someEnd = false;
		whileEnd = false;
		theType = "";
		scope = "";
		temp1 = "";
		temp2 = "";
		oper = "";
		tempResult1 = "";
		tempResult2 = "";
		tempToken = "";
		tempString1 = "";
		tempString2 = "";
		tempString3 = "";
		tempString4 = "";
		tempString5 = "";
		tempString6 = "";
		tempString7 = "";
		theCase = "";
		whichCase = "";
		globalVar = new ArrayList<String>();
		jumpLines = new ArrayList<String>();
		assemCode = new ArrayList<String>();
		jumpNum = new ArrayList<Integer>();
		 
		toSem.SemanticStart();
		
		Program();	
		out.close();

		//System.out.println("The End");
		//System.out.println("*****getToken = " + tkn.getToken());
		//System.out.println("getWord = " + tkn.getWord());
		//System.out.println("lineNum" + lineNum);
		
	}
	
	//This begins the parser, it calls the var_section then the body of the input
	private void Program() {
		//System.out.println("Program Start");
		lineNum = tkn.getLine();
		errorCheck = "";
		counter = 0;
		scope = "global";
		
		Var_Section();				//Calls Var_Section()
		
		scope = "main";
		
		Body();						//Calls Body()
	
		//This statement is used as an error check for the program not ending in a closing bracket
		if (!tkn.getWord().equals("}")) {
			System.out.println("Line " + (lineNum+1) + ": expected delimiter }");
			out.println("Line " + lineNum + ": expected delimiter }");
		}
		
		lineNum = tkn.getLine();
		
		//These commands add the final assembly commands to the assemCode arraylist
		assemCode.add("OPR 1, 0");
		assemCode.add("OPR 0, 0");
		
		//This loop is used to print the global variables to the beginning of the output file
		for (int xcx = 0; xcx < globalVar.size(); xcx++) {
			System.out.println(globalVar.get(xcx));
			out.println(globalVar.get(xcx));			
		}
		
		//This loop is used to print the jump lines to the output file
		for (int cxc = 0; cxc < jumpNum.size(); cxc++) {
			System.out.println("#e" + (cxc+1) + "," + jumpNum.get(cxc));
			out.println("#e" + (cxc+1) + "," + jumpNum.get(cxc));	
		}
		System.out.println("@");
		out.println("@");
		
		//This loop is used to print the assembly code to the output file
		for (zxc = 0; zxc < assemCode.size()-1; zxc++) {
			System.out.println(assemCode.get(zxc));
			out.println(assemCode.get(zxc));			
		}
		//This prints the last assembly statement "OPR 0,0"
		System.out.print(assemCode.get(zxc));
		out.print(assemCode.get(zxc));
		
		out.close();
		//System.out.println("Program End");
	} 

	//This method handles any variables before the first open bracket if no variables proceded to the body
	private boolean Var_Section() {
		//System.out.println("Var_Section Start");
		boolean typeStart = false;
		lineNum = tkn.getLine();
		
		//This loop is used to check if the current token is in the keyword and sets a temp varaible to true 
		for (int h = 0; h <= keywords.length-1; h++){
			  if (tkn.getWord().equals(keywords[h]))
				  lineNum = tkn.getLine();
				  typeStart = true;		  
		}
		
		//This statement checks if the first token is '{' which means there are no starting varaibles
		if (tkn.getWord().equals("{")) {
			//System.out.println("Var_Section End True 1");
			return true;
		}
		
		while (!tkn.getWord().equals("{")) {
			//begins the Var_Section rule
			tempToken = tkn.getWord();
			if (typeStart) {
				Type();
				lineNum = tkn.getLine();
				
				if (tkn.getToken().equals("IDENTIFIER")){		//Check the token for identifier 
					Id_List();									//Calls Id_List()
					lineNum = tkn.getLine();
					
					if (tkn.getWord().equals(";")) {			//Check the token for semicolon
						if (en.hasMoreElements()) {
							tkn = (Token)en.nextElement();		//Increment the token using enumeration
							tempLine = tkn.getLine();
						}			
						tempLine = tkn.getLine();
						//System.out.println("Var_Section End True 2");
						typeStart = true;
						
					} else {
						//System.out.println("Line " + lineNum + ": expected delimiter ;");
						//out.println("Line " + lineNum + ": expected delimiter ;");
					}
				} else {
					System.out.println("Line " + lineNum + ": expected identifier");
					out.println("Line " + lineNum + ": expected identifier");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected type");
				out.println("Line " + lineNum + ": expected type");
			}
		
		}
		
		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getWord().equals("{")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
		}
		//System.out.println("Var_Section End False");
		return false;
	}
	
	//The method checks if the token is a identifer or an id then a ',' the a recursive call to itself
	private boolean Id_List() {
		//System.out.println("Id_List Start");
		lineNum = tkn.getLine();
				
		if (tkn.getToken().equals("IDENTIFIER")) {			//Checks vector for identifier
			
			//Check the Hashtable for the current word if it isn't found then add it else Semantic Error
			if (!toSem.checkHash(tkn.getWord())) {
				toSem.addToHash(tkn.getWord(), theType, scope);
				globalVar.add(tkn.getWord() + "," + tempToken);
			}
			else {
				System.out.println("Line " + lineNum + ": Duplicated variable " + tkn.getWord());
				out.println("Line " + lineNum + ": Duplicated variable " + tkn.getWord());				
			}
			
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			
			if (tkn.getWord().equals(";")) {				//Checks vector for semicolon
				lineNum = tkn.getLine();
				//System.out.println("Id_List End True");
				return true;
				
			} else if (tkn.getWord().equals(",")) {			//Checks vector for comma
				
				if (en.hasMoreElements()) {
					tkn = (Token)en.nextElement();			//Increment the token using enumeration	
					tempLine = tkn.getLine();
				}
				
				if (tkn.getToken().equals("IDENTIFIER")) {	//Checks vector for identifier
					Id_List();
					lineNum = tkn.getLine();
					
				} else {
					System.out.println("Line " + lineNum + ": expected identifier");
					out.println("Line " + lineNum + ": expected identifier");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected identifier");
				out.println("Line " + lineNum + ": expected identifier");
			}
		} 
		
		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();			
				tempLine = tkn.getLine();
			}
			//System.out.println("dmethod Count - " + tempLine);
		}
				
		//System.out.println("Id_List End ");
		return false;
	}
	
	//This method is called by the main program and also many other methods like switch. It is a STMT_LIST
	//inside of a opening and closing brakets
	private boolean Body() {
		//System.out.println("Body Start");
		boolean tempBod = false;
		lineNum = tkn.getLine();
		
		if (tkn.getWord().equals("{")) {
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();			//Increment the token using enumeration	
				tempLine = tkn.getLine();
			}
			//Checks vector for a keyword
			if (tkn.getToken().equals("IDENTIFIER") || tkn.getWord().equals("print") || tkn.getWord().equals("WHILE") 
					|| tkn.getWord().equals("IF") || tkn.getWord().equals("SWITCH") || tkn.getWord().equals("DEFAULT")) {
				Stmt_List();							//Calls Stmt_List()
						
				if (en.hasMoreElements()) {
					tkn = (Token)en.nextElement();		//Increment the token using enumeration	
					tempLine = tkn.getLine();
				}
				//Checks vector for a keyword
				if (tkn.getToken().equals("IDENTIFIER") || tkn.getWord().equals("print") || tkn.getWord().equals("WHILE") 
						|| tkn.getWord().equals("IF") || tkn.getWord().equals("SWITCH") || tkn.getWord().equals("DEFAULT")) {
					Stmt();
					
				}
				
				while ((tkn.getToken().equals("INTEGER") || tkn.getWord().equals(";")) && en.hasMoreElements()) {
					if (en.hasMoreElements()) {
						tkn = (Token)en.nextElement();	//Increment the token using enumeration
						tempLine = tkn.getLine();
					}
				}
				
				if (tkn.getWord().equals("}"))			//Checks vector for a '}'
					tkn2 = tkn;
			
				//While loop to handle multiple STMT
				while (tkn.getToken().equals("IDENTIFIER") || tkn.getWord().equals("print") || tkn.getWord().equals("WHILE") 
						|| tkn.getWord().equals("IF") || tkn.getWord().equals("SWITCH") || tkn.getWord().equals("DEFAULT")) {
					
					Stmt();								//Calls Stmt()

					//This while loop is an error checker for incomplete rule
					while ((tkn.getToken().equals("INTEGER") || tkn.getWord().equals(";")) && en.hasMoreElements()) {
						if (en.hasMoreElements()) {
							tkn = (Token)en.nextElement();	//Increment the token using enumeration		
							tempLine = tkn.getLine();
						}
					}
					
				}
				
				if (tkn.getWord().equals("}")) {			//Checks vector for a '}'
					tkn2 = tkn;
					
					//This block is used after a while loop ends to take the numbers stored in the stack
					//for the jump point then add them to the jumpNum arraylist
					if (whileCount > 0) {
						jumpCount++;
						assemCode.add("JMP #e" + jumpCount + ", 0");
						jumpStack.push(assemCode.size()+1);
						assemCount++;
						whileCount--;
					}
					
					//This block is used to pop all the jumpStack numbers and add to the jumpnum arrayList 
					if (whileCount == 0) {
						while (!jumpStack.empty())
							jumpNum.add(jumpStack.pop());						
					}
					
					if (en.hasMoreElements()) {
						tkn = (Token)en.nextElement();	//Increment the token using enumeration	
						tempLine = tkn.getLine();
					}
					
					//This is used to have multipe statements inside a Body() rule
					if (tkn.getToken().equals("IDENTIFIER") || tkn.getWord().equals("print") || tkn.getWord().equals("WHILE") 
							|| tkn.getWord().equals("IF") || tkn.getWord().equals("SWITCH") || tkn.getWord().equals("DEFAULT")) {
						Stmt_List();
						
					}
					
					//System.out.println("Body End True 2");
					return true;
					
				} else {
						//System.out.println("Line " + lineNum + ": expected delimiter }");
						//out.println("Line " + lineNum + ": expected delimiter }");
					
				}
			} else {
				System.out.println("Line " + lineNum + ": expected identifier or print or while or if or switch");
				out.println("Line " + lineNum + ": expected identifier or print or while or if or switch");
			}
		} else {
			System.out.println("Line " + lineNum + ": expected delimiter {");
			out.println("Line " + lineNum + ": expected delimiter {");
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("body Count " + countBody );
			countBody++;
		}
				
		//System.out.println("Body Endf false ");
		return tempBod;
	}
	
	//This method is a way to access the STMT method. It is either a STMT or a STMT concatencated with 
	//a recursive call to itself
	private boolean Stmt_List() {
		//System.out.println("Stmt_List Start");
		boolean tempSL = false;
		lineNum = tkn.getLine();
				
		//Checks vector for a keyword
		if (tkn.getToken().equals("IDENTIFIER") || tkn.getWord().equals("print") || tkn.getWord().equals("WHILE") 
				|| tkn.getWord().equals("IF") || tkn.getWord().equals("SWITCH") || tkn.getWord().equals("DEFAULT")) {
			
			Stmt();											//Calls Stmt() 
			lineNum = tkn.getLine();
			
			if (tkn.getWord().equals(";")) {				//Checks vector for a ';'
				
				if (en.hasMoreElements()) {
					tkn = (Token)en.nextElement();			//Increment the token using enumeration
					lineNum = tkn.getLine();
				}
				
				//this statement is used for end of the file error check
				if (tkn.getToken().equals("The End")) {
					//System.out.println("Stmt_List End for good");
					endCount = 5;
					return true;
				}
				
			}
			
			if (tkn.getWord().equals("}")) {				//Checks vector for a '}'
				lineNum = tkn.getLine();
				
				//This block is used after a while loop ends to take the numbers stored in the stack
				//for the jump point then add them to the jumpNum arraylist
				if (whileCount > 0) {
					jumpCount++;
					assemCode.add("JMP #e" + jumpCount + ", 0");
					jumpStack.push(assemCode.size()+1);
					whileCount--;
					
				//This block is used to pop all the jumpStack numbers and add to the jumpnum arrayList
				} else if (whileCount == 0) {	
					while (!jumpStack.empty())
						jumpNum.add(jumpStack.pop());					
				}
				
				return true;
				
				//Checks vector for a keyword
			} else if (tkn.getToken().equals("IDENTIFIER") || tkn.getWord().equals("print") || tkn.getWord().equals("WHILE") 
					|| tkn.getWord().equals("IF") || tkn.getWord().equals("SWITCH") || tkn.getWord().equals("DEFAULT")) {
		
				Stmt_List(); 								//Calls Stmt_List()
				
				
				if (tkn.getToken().equals("The End") || tkn.getWord().equals("}")) {		//End of file error check

					return true;
				}
				
			} 
			
		} else {
			System.out.println("Line " + lineNum + ": expected identifier or print or while or if or switch");
			out.println("Line " + lineNum + ": expected identifier or print or while or if or switch");
		} 
		counter = 0;
		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")) {
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration	
				tempLine = tkn.getLine();
			}
			//System.out.println("Stmt 2 case Count - " + counter);
			counter++;

		}

		if (tkn.getToken().equals("The End")) {		
			someEnd = true;		
			//System.out.println("Stmt_List End for good");
			return true;
		}
		
		if (en.hasMoreElements()) {
			tkn = (Token)en.nextElement();					//Increment the token using enumeration
			tempLine = tkn.getLine();
		}

		if (tkn.getWord().equals(";"))
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration	
				tempLine = tkn.getLine();
			}
		
		//Checks vector for a keyword
		while (tkn.getToken().equals("IDENTIFIER") || tkn.getWord().equals("print") || tkn.getWord().equals("WHILE") 
				|| tkn.getWord().equals("IF") || tkn.getWord().equals("SWITCH") || tkn.getWord().equals("DEFAULT")) {
			
			Stmt();											//Calls Stmt()
			
			if (tkn.getToken().equals("The End")) {			//Error check for the end of the file
				//System.out.println("Stmt_List End for good");
				return true;
			}
			
			lineNum = tkn.getLine();			
		} 
		
		if (tkn.getWord().equals(";")) {					//Checks vector for a ';'
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			
		}
		
		if (tkn.getToken().equals("IDENTIFIER")) {					//Checks vector for a ';'
			Stmt_List();
			
			
		}
		
		if (tkn.getWord().equals("}"))
			return true;
		
		counter = 0;
		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("stmt Count - " + counter);
			counter++;
		}
	
		
		if (tkn.getToken().equals("The End")) {					//Error check for the end of the file
			return true;
		}
		//System.out.println("Stmt_List End false");
		return tempSL;
	}
	
	private boolean Stmt() {
		//System.out.println("Stmt Start");
		String theCase = "";
		theCase = tkn.getWord();
		lineNum = tkn.getLine();
				
		//This swtich statement is used to identify the which statement to call 
		switch (theCase) {
		
			case "print":						
				Print_Stmt();									//Calls Print_Stmt()
				lineNum = tkn.getLine();
				
				if (tkn.getWord().equals(";")) {				//Checks vector for a "}"
					if (en.hasMoreElements()) {
						tkn = (Token)en.nextElement();			//Increment the token using enumeration
						tempLine = tkn.getLine();
					}
					//System.out.println("Stmt print End True");
					return true;
					
				}
				break;
				
			case "WHILE":
				While_Stmt();									//Calls While_Stmt()
				lineNum = tkn.getLine();
				
				if (tkn.getWord().equals("}")) {				//Checks vector for a "}"
					if (en.hasMoreElements()) {
						tkn = (Token)en.nextElement();			//Increment the token using enumeration
						tempLine = tkn.getLine();
					}
					//System.out.println("Stmt while End True");
					return true;
					
				}
				break;
				
			case "IF":
				If_Stmt();										//Calls If_Stmt()
				lineNum = tkn.getLine();
				
				if (tkn.getWord().equals("}")) {				//Checks vector for a "}"
					if (en.hasMoreElements()) {
						tkn = (Token)en.nextElement();			//Increment the token using enumeration
						tempLine = tkn.getLine();
					}
					//System.out.println("Stmt if End True");
					return true;
					
				}
				break;
				
			case "SWITCH":
				Switch_Stmt();											//Calls Switch_Stmt()
				lineNum = tkn.getLine();
				
				if (tkn.getWord().equals("}")) {						//Checks vector for a "}"
					//System.out.println("Stmt switch End True");
					return true;
					
				}
				break;
				
			case "DEFAULT":
				Default_Case();									//Calls Default_Case()
				lineNum = tkn.getLine();
				
				//Checks vector for a keyword
				if (tkn.getToken().equals("IDENTIFIER") || tkn.getWord().equals("print") || tkn.getWord().equals("WHILE") 
						|| tkn.getWord().equals("IF") || tkn.getWord().equals("SWITCH") || tkn.getWord().equals("DEFAULT")) {
					Stmt_List();
					
				}
				
				if (tkn.getWord().equals("}")) {					//Checks vector for a "}"
					//System.out.println("Stmt default End True");
					return true;
					
				}
				break;
		
		}
		
		if (tkn.getToken().equals("IDENTIFIER")) {				//Checks vector for a identifier
			Assign_Stmt();										//Calls Assign_Stmt()
			lineNum = tkn.getLine();
			//System.out.println("Stmt ident End True");
			return true;
			
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("stmt Count - " + tempLine);
		}
		
		//System.out.println("Stmt End");
		return false;
	}
	
	//This method is a way to recognize if a certain value to an ID i.e. "id = 4" or "id = 4 + 6"
	private boolean Assign_Stmt() {
		//System.out.println("Assign_Stmt Start");
		boolean tempASTM = false;
		lineNum = tkn.getLine();
		tempLine = tkn.getLine();
			
		
		if (tkn.getToken().equals("IDENTIFIER")) {				//Checks vector for a identifer
			
			//Checks if current word is in the hashtable if not error else push token to stack 
			if (!toSem.checkHash(tkn.getWord())) {
				System.out.println("Line " + lineNum + ": Variable " + tkn.getWord() + " not found");
				out.println("Line " + lineNum + ": Variable " + tkn.getWord() + " not found");
			}
			else {		
				tempResult1 = toSem.getValue(tkn.getWord());
				toSem.pushStack(tempResult1);
				tempString1 = "STO " + tkn.getWord() + ", 0";
			}
			
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			
			if (tkn.getWord().equals("=")) {					//Checks vector for a "="
				oper = tkn.getWord();
				if (en.hasMoreElements()) {
					tkn = (Token)en.nextElement();				//Increment the token using enumeration
					tempLine = tkn.getLine();
				}
				
				//Checks vector for a identifier or a number
				if (tkn.getToken().equals("IDENTIFIER") || tkn.getToken().equals("INTEGER") 
						|| tkn.getToken().equals("FLOAT") || tkn.getToken().equals("KEYWORD")) {
						
					Primary();									//Calls Primary()
					
					if (tkn.getWord().equals(";")) {			//Checks vector for a ";"
						lineNum = tkn.getLine();
						
						//These statements are used to add the assembly coded to the assemCode array
						assemCode.add(tempString2);
						assemCount++;
						assemCode.add(tempString1);
						assemCount++;
						
						//This block is used to pop the semantic stack for cube comparison
						if (!toSem.isEmpty()) {
							temp2 = toSem.popStack();
						}
						
						//This block is used to pop the semantic stack for cube comparison
						if (!toSem.isEmpty()) {
							temp1 = toSem.popStack();
							tempResult2 = toSem.calculateTypeBinary(temp1, oper, temp2);
						}
						
						//If the resulting cube comparison is an error print the error report
						if (tempResult2.equals("error")) {
							System.out.println("Line " + lineNum + ": Type mismatch");
							out.println("Line " + lineNum + ": Type mismatch");								
						}
						
						//System.out.println("Assign_Stmt End true 1");
						return true;
						
					} else if (tkn.getToken().equals("OPERATOR")) {	//Checks vector for an operator
						oper = tkn.getWord();
						
						//This block is used to add the operation command to a temp location for later
						if (oper.equals("+"))
							tempString4 = "OPR 2, 0";
						else if (oper.equals("-"))
							tempString4 = "OPR 3, 0";
						else if (oper.equals("*"))
							tempString4 = "OPR 4, 0";
						else if (oper.equals("/"))
							tempString4 = "OPR 5, 0";
						
						if (en.hasMoreElements()) {
							tkn = (Token)en.nextElement();		//Increment the token using enumeration	
							tempLine = tkn.getLine();
						}
						
						//This adds tempString2 to the assemCode arraylist
						assemCode.add(tempString2);
						assemCount++;
						
						Primary();								//Calls Primary()							
						
						//This block adds the tempStrings to the assemCode arraylist
						assemCode.add(tempString2);
						assemCount++;
						assemCode.add(tempString4);
						assemCount++;
						assemCode.add(tempString1);
						assemCount++;
						
						//This block is used to pop the semantic stack for cube comparison
						if (!toSem.isEmpty()) {
							temp2 = toSem.popStack();
						}
						
						//This block is used to pop the semantic stack for cube comparison
						if (!toSem.isEmpty()) {
							temp1 = toSem.popStack();
							tempResult2 = toSem.calculateTypeBinary(temp1, oper, temp2);
						}
						
						//Push the result of the aboove comparison to the stack for more comparison
						toSem.pushStack(tempResult2);
						
						//This block is used to pop the semantic stack for cube comparison
						if (!toSem.isEmpty()) {
							temp2 = toSem.popStack();
						}
						
						//This block is used to pop the semantic stack for cube comparison
						if (!toSem.isEmpty()) {
							temp1 = toSem.popStack();
							tempResult2 = toSem.calculateTypeBinary(temp1, oper, temp2);
						}
						
						lineNum = tkn.getLine();
						
						if (tkn.getWord().equals(";")) {		//Checks vector for a ";"
							lineNum = tkn.getLine();
							if (tempResult2.equals("error")) {
								System.out.println("Line " + lineNum + ": Type mismatch");
								out.println("Line " + lineNum + ": Type mismatch");								
							}
									
							//System.out.println("Assign_Stmt End True 2");
							return true;
							
						} else {
							System.out.println("Line " + lineNum + ": expected delimiter ;");
							out.println("Line " + lineNum + ": expected delimiter ;");
						}
					} else {
						System.out.println("Line " + lineNum + ": expected delimiter ;");
						out.println("Line " + lineNum + ": expected delimiter ;");
					}
				} else {
					System.out.println("Line " + lineNum + ": expected identifier or num");
					out.println("Line " + lineNum + ": expected identifier or num");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected operator");
				out.println("Line " + lineNum + ": expected operator");
			}
		} else {
			System.out.println("Line " + lineNum + ": expected identifier");
			out.println("Line " + lineNum + ": expected identifier");
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("assign Count - " + tempLine);
		}
		
		//System.out.println("Assign_Stmt End false");
		return tempASTM;
	}
		
	//This method handles experssion like 4+6 or 5*2 but I didn't end up using becasue I nested this is assign_stmt 
	private boolean Expr() {
		//System.out.println("Expr Start");
		boolean tempExpr = false;
		lineNum = tkn.getLine();
		
																//Checks vector for a identifier or number
		if (tkn.getToken().equals("IDENTIFIER") || tkn.getToken().equals("INTEGER") || tkn.getToken().equals("FLOAT")) {
			
			Primary();											//Calls Primary()
			lineNum = tkn.getLine();							
			
			if (tkn.getToken().equals("OPERATOR")) {			//Checks vector for an operator
				
				Op();											//calls Op()
				lineNum = tkn.getLine();
				
																//Checks vector for a identifer or a number
				if (tkn.getToken().equals("IDENTIFIER") || tkn.getToken().equals("INTEGER") || tkn.getToken().equals("FLOAT")) {
					
					Primary();									//calss Primary()
					lineNum = tkn.getLine();
					
					if (tkn.getWord().equals(";")) {			//Checks vector for a ";"
						lineNum = tkn.getLine();
						//System.out.println("Expr End True");
						return true;
						
					} else {
						System.out.println("Line " + lineNum + ": expected delimiter ;");
						out.println("Line " + lineNum + ": expected delimiter ;");
					}
				} else {
					System.out.println("Line " + lineNum + ": expected identifier or number");
					out.println("Line " + lineNum + ": expected identifier or number");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected operator");
				out.println("Line " + lineNum + ": expected operator");
			}
		} else {
			System.out.println("Line " + lineNum + ": expected identifier or number");
			out.println("Line " + lineNum + ": expected identifier or number");
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();			
				tempLine = tkn.getLine();
			}
			//System.out.println("expr Count - " + tempLine);
		}
		
		//System.out.println("Expr End- " + tempExpr);
		return tempExpr;
	}
		
	//This method checks if the token is identifer or number
	private boolean Primary() {
		//System.out.println("Primary Start");
		boolean tempPRI = false;
		lineNum = tkn.getLine();
		
		//Checks vector for a identifier or a number
		if (tkn.getToken().equals("IDENTIFIER") || tkn.getToken().equals("INTEGER") 
				|| tkn.getToken().equals("FLOAT") || tkn.getToken().equals("KEYWORD")) {
			
			if (tkn.getToken().equals("IDENTIFIER")) {
				
				//Checks if current word is in the hashtable if not error else push token to stack 
				if (!toSem.checkHash(tkn.getWord())) {
					System.out.println("Line " + lineNum + ": Variable " + tkn.getWord() + " not found");
					out.println("Line " + lineNum + ": Variable " + tkn.getWord() + " not found");	
				}
				else {	
					tempResult1 = toSem.getValue(tkn.getWord());
					toSem.pushStack(tempResult1);
					tempString2 = "LOD " + tkn.getWord() + ", 0";	//Add a LOD instruction to a tempString
				}
				
			} else if (tkn.getToken().equals("INTEGER")){
				toSem.pushStack("integer");
				tempString2 = "LIT " + tkn.getWord() + ", 0";		//Add a LIT instruction to a tempString
				
			} else if (tkn.getWord().equals("true")){
				toSem.pushStack("boolean");
				tempString2 = "LIT " + tkn.getWord() + ", 0";		//Add a LIT instruction to a tempString
				
			} else if (tkn.getWord().equals("false")){
				toSem.pushStack("boolean");
				tempString2 = "LIT " + tkn.getWord() + ", 0";		//Add a LIT instruction to a tempString
				
			} 
						
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("Primary End True");
			return true;
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
		//System.out.println("primary Count - " + tempLine);
		}
		
		//System.out.println("Primary End False");
		return tempPRI;
	}
		
	//This method checks if the token is an operator
	private boolean Op() {
		//System.out.println("Op Start");
		boolean tempOp = false;
		lineNum = tkn.getLine();
		
															//Checks vector for an operator
		if (tkn.getWord().equals("=") || tkn.getWord().equals("+") || tkn.getWord().equals("-") 
				|| tkn.getWord().equals("*") || tkn.getWord().equals("/") || tkn.getWord().equals("%")) {
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			lineNum = tkn.getLine();
			//System.out.println("Op End True");
			return true;
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("op Count - " + tempLine);
		}
		
		//System.out.println("Op End");
		return tempOp;
	}
	
	//This method handles a print statement i.e. "print someThing;" where someThing is an identifer
	private boolean Print_Stmt() {
		//System.out.println("Print_Stmt Start");
		boolean tempPRST = false;
		lineNum = tkn.getLine();
		tempLine = tkn.getLine();
				
		if (tkn.getWord().equals("print")) {				//Checks vector for a print
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration	
				tempLine = tkn.getLine();
			}
			
			if (tkn.getToken().equals("IDENTIFIER")) {		//Checks vector for a identifier
				
				//Checks if current word is in the hashtable if not error else push token to stack 
				if (!toSem.checkHash(tkn.getWord())) {
					System.out.println("Line " + lineNum + ": Variable " + tkn.getWord() + " not found");
					out.println("Line " + lineNum + ": Variable " + tkn.getWord() + " not found");
				}
				else {		
					tempResult1 = toSem.getValue(tkn.getWord());
					assemCode.add("LOD " + tkn.getWord() + ", 0");	//Add a LOD instruction to a assemCode arrayList
					assemCode.add("OPR 21, 0");						//Add a OPR instruction to a assemCode arrayList
					
				}
				
				if (en.hasMoreElements()) {
					tkn = (Token)en.nextElement();			//Increment the token using enumeration
					tempLine = tkn.getLine();
				}
				
				if (tkn.getWord().equals(";")) {			//Checks vector for a ";"
					lineNum = tkn.getLine();
					//System.out.println("Print_Stmt End True");
					return true;
					
				} else {
					System.out.println("Line " + lineNum + ": expected delimiter ;");
					out.println("Line " + lineNum + ": expected delimiter ;");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected identifier");
				out.println("Line " + lineNum + ": expected identifier");
			}
		} else {
			System.out.println("Line " + lineNum + ": expected print");
			out.println("Line " + lineNum + ": expected print");
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();				//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("print Count - " + tempLine);
		}
		
		//System.out.println("Print_Stmt End false");
		return tempPRST;
	}
	
	//This method handles a while loop. It starts with the keyword WHILE then checks Condition() then
	//it checks the Body() of the loop
	private boolean While_Stmt() {
		//System.out.println("While_Stmt Start");
		boolean tempWhSt = false;
		lineNum = tkn.getLine();
		errorCheck = tkn.getWord();
		whileCount++;
		
		if (tkn.getWord().equals("WHILE")) {					//Checks vector for a WHILE
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
																//Checks vector for an identifier or number
			if (tkn.getToken().equals("IDENTIFIER") || tkn.getToken().equals("INTEGER") || tkn.getToken().equals("FLOAT")) {
				
				Condition();									//Calls Condition()
				lineNum = tkn.getLine();
				
				if (tkn.getWord().equals("{")) {				//Checks vector for a "{"
					
					Body();										//Calls Body()
					lineNum = tkn.getLine();
					
																//Checks vector for a "}" or end of file
					if (tkn.getWord().equals("}") || tkn.getToken().equals("The End")){
						lineNum = tkn.getLine();
						//System.out.println("While_Stmt End True");
						return true;
					} else {
						
						if (tkn.getWord().equals("}")) {		//Checks vector for a "}"
							//System.out.println("Line " + lineNum + ": expected delimiter 3 }");
							//out.println("Line " + lineNum + ": expected delimiter }");
						}
					}
				} else {
					System.out.println("Line " + lineNum + ": expected delimiter  {");
					out.println("Line " + lineNum + ": expected delimiter {");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected identifier or number");
				out.println("Line " + lineNum + ": expected identifier or number");
			}
		} else {
			System.out.println("Line " + lineNum + ": expected WHILE");
			out.println("Line " + lineNum + ": expected WHILE");
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("while Count - " + tempLine);
		}
		
		//System.out.println("While_Stmt End false");
		return tempWhSt;
	}
	
	//This method handles a if statement. It starts with the keyword IF then checks Condition() then
	//it checks the Body() of the statement
	private boolean If_Stmt() {
		//System.out.println("If_Stmt Start");
		boolean tempIfSt = false;
		lineNum = tkn.getLine();
		
		if (tkn.getWord().equals("IF")) {						//Checks vector for a IF
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration	
				tempLine = tkn.getLine();
			}
			
																//Checks vector for a identifier or number
			if (tkn.getToken().equals("IDENTIFIER") || tkn.getToken().equals("INTEGER") || tkn.getToken().equals("FLOAT")) {
				
				Condition();									//Calls Condition()
				lineNum = tkn.getLine();
				
				if (tkn.getWord().equals("{")) {				//Checks vector for a "{"
					
					Body();										//Calls Body()
					lineNum = tkn.getLine();
					
					if (tkn.getWord().equals("}")){				//Checks vector for a "}"
						lineNum = tkn.getLine();
						//System.out.println("If_Stmt End True");
						return true;
					} else {
						//System.out.println("Line " + lineNum + ": expected delimiter } 1");
						//out.println("Line " + lineNum + ": expected delimiter }");
					}
				} else {
					System.out.println("Line " + lineNum + ": expected delimiter {");
					out.println("Line " + lineNum + ": expected delimiter {");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected identifier or number");
				out.println("Line " + lineNum + ": expected identifier or number");
			}
		} else {
			System.out.println("Line " + lineNum + ": expected IF");
			out.println("Line " + lineNum + ": expected IF");
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("if Count - " + tempLine);
		}
		
		//System.out.println("If_Stmt End false");
		return tempIfSt;
	}
		
	//This method is called from the IF and WHILE statements to check if the given condition is in a
	//valid format i.e "i > 0" or " j < k"
	private boolean Condition() {
		//System.out.println("Condition Start");
		boolean tempCon = false;
		lineNum = tkn.getLine();
																//Checks vector for a identifier or a number
		if (tkn.getToken().equals("IDENTIFIER") || tkn.getToken().equals("INTEGER") || tkn.getToken().equals("FLOAT")) {
			
			//If the token is an identifier then add a LOD statement to a tempString and add line number to jumpStack
			if (tkn.getToken().equals("IDENTIFIER")) {
				tempString5 = ("LOD " + tkn.getWord() + ", 0");
				jumpStack.push(assemCode.size()+1);
				
			//If the token is an int or keyword then add a LOD statement to a tempString and add line number to jumpStack
			} else if (tkn.getToken().equals("INTEGER") || tkn.getToken().equals("KEYWORD")) {
				tempString5 = ("LIT " + tkn.getWord() + ", 0");
				jumpStack.push(assemCode.size()+1);
			}
			
			Primary();											//Calls Primary()

			//Error check for the no operator found
			if (!(tkn.getWord().equals("<") || tkn.getWord().equals(">") || tkn.getWord().equals("!="))) {
				if (en.hasMoreElements()) {
					tkn = (Token)en.nextElement();				//Increment the token using enumeration
					tempLine = tkn.getLine();
				}				
			}
			
			//Checks vector for a operater > or < or !=
			if (tkn.getWord().equals("<") || tkn.getWord().equals(">") || tkn.getWord().equals("!=")) {
				oper = tkn.getWord();
				
				//This block is used to add the appropriate OPR instruction to a tempString
				if (tkn.getWord().equals(">"))
					tempString7 = ("OPR 11, 0");
				else if (tkn.getWord().equals("<"))
					tempString7 = ("OPR 12, 0");
				else if (tkn.getWord().equals("!="))
					tempString7 = ("OPR 16, 0");
				
				Relop();										//Calls Relop()
				lineNum = tkn.getLine();
				
				//Checks vector for a identifier or number
				if (tkn.getToken().equals("IDENTIFIER") || tkn.getToken().equals("INTEGER") || tkn.getToken().equals("FLOAT")) {
					
					Primary();									//Calls Primary()
					lineNum = tkn.getLine();
					
					//This block is used to add the above temp strings to the assemCode arraylist in a proper order
					assemCode.add(tempString5);
					assemCount++;
					assemCode.add(tempString2);
					assemCount++;
					assemCode.add(tempString7);
					assemCount++;
					
					//This adds a jump conditional statement to the assemCode arrayList
					jumpCount++;
					assemCode.add("JMC #e" + jumpCount + ", false");
					assemCount++;
					
					//This block is used to pop the semantic stack for cube comparison
					if (!toSem.isEmpty()) {
						temp2 = toSem.popStack();
					}
					
					//This block is used to pop the semantic stack for cube comparison
					if (!toSem.isEmpty()) {
						temp1 = toSem.popStack();
						tempResult2 = toSem.calculateTypeBinary(temp1, oper, temp2);
					}
					
					//If the resulting cube comparison is an error print the error report
					if (tempResult2.equals("error")) {
						System.out.println("Line " + lineNum + ": Boolean expression expected");
						out.println("Line " + lineNum + ": Boolean expression expected");								
					}
					
					if (tkn.getWord().equals("{")) {			//Checks vector for a "{"
						lineNum = tkn.getLine();
						//System.out.println("Condition End True");
						return true;
						
					} else {
						System.out.println("Line " + lineNum + ": expected delimiter {");
						out.println("Line " + lineNum + ": expected delimiter {");
					}
				} else {
					System.out.println("Line " + lineNum + ": expected identifier or number");
					out.println("Line " + lineNum + ": expected identifier or number");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected < or > or !=");
				out.println("Line " + lineNum + ": expected < or > or !=");
			}
		} else {
			System.out.println("Line " + lineNum + ": expected identifier or number");
			out.println("Line " + lineNum + ": expected identifier or number");
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End") && !tkn.getWord().equals("{")) {
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("condition Count - " + tempLine);
		}
		
		if (tkn.getWord().equals(";")) {
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration	
				tempLine = tkn.getLine();
			}
		}
		//System.out.println("Condition End false");
		return tempCon;
	}
		
	//This method is used to check if the current token is '<' or '>' or '!='
	private boolean Relop() {
		//System.out.println("Relop Start");
		boolean tempRel = false;
		lineNum = tkn.getLine();
		
		//Checks vector for a operator < or > or !=
		if (tkn.getWord().equals(">") || tkn.getWord().equals("<") || tkn.getWord().equals("!=")) {
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration	
				tempLine = tkn.getLine();
			}
			//System.out.println("Relop End True");
			return true;
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine)){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();	
			}
			//System.out.println("relop Count - " + tempLine);
		}
		
		//System.out.println("Relop End");
		return tempRel;
	}
	
	//This method handles a Switch statement with varies cases
	private boolean Switch_Stmt() {
		//System.out.println("Switch_Stmt Start");
		boolean tempSwSt = false;
		lineNum = tkn.getLine();
		
		if (tkn.getWord().equals("SWITCH")) {					//Checks vector for a SWITCH
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			
			if (tkn.getToken().equals("IDENTIFIER")) {			//Checks vector for a identifier
				
				//This block is used to check the hashtable for the current variable
				if (!toSem.checkHash(tkn.getWord())) {
					System.out.println("Line " + lineNum + ": Variable " + tkn.getWord() + " not found");
					out.println("Line " + lineNum + ": Variable " + tkn.getWord() + " not found");
					
				//If variable is found then add the current word to a tmepResult
				} else {		
					tempResult4 = toSem.getValue(tkn.getWord());
					theCase = "LOD " + tkn.getWord() + ", 0";	//Add LOD instruction to a temp string
					assemCode.add(theCase);						//Add LOD instruction to the assemCode arrayList
					assemCount++;
				}
				
				//This statement is used to check if the switch variable is an integer
				if (!tempResult4.equals("integer") || !toSem.checkHash(tkn.getWord())){
					
					System.out.println("Line " + lineNum + ": Incompatible types: boolean cannot be converted to " + tempResult4);
					out.println("Line " + lineNum + ": Incompatible types: boolean cannot be converted to " + tempResult4);
					
				}
					
				if (en.hasMoreElements()){ 
					tkn = (Token)en.nextElement();				//Increment the token using enumeration
					tempLine = tkn.getLine();
				}
				
				
				if (tkn.getWord().equals("{")) {				//Checks vector for a "{"
					if (en.hasMoreElements()) {
						tkn = (Token)en.nextElement();			//Increment the token using enumeration
						tempLine = tkn.getLine();
					}
					
					if (tkn.getWord().equals("CASE")) {			//Checks vector for a CASE
						Case_List();
						lineNum = tkn.getLine();
						
						if (tkn.getWord().equals("}")) {		//Checks vector for a "}"
							lineNum = tkn.getLine();
							//System.out.println("Switch_Stmt End True");
							return true;
							
						} else if (tkn.getWord().equals("DEFAULT")) {	//Checks vector for a DEFAULT
							Default_Case();
							lineNum = tkn.getLine();
							
														
							if (tkn.getWord().equals("}")) {	//Checks vector for a "}"
								lineNum = tkn.getLine();
								//System.out.println("Switch_Stmt after default End True");
								return true;
								
							} else {
								//System.out.println("Line " + lineNum + ": expected delimiter }");
								//out.println("Line " + lineNum + ": expected delimiter }");
							}
						} else {
							//System.out.println("Line " + lineNum + ": expected delimiter }");
							//out.println("Line " + lineNum + ": expected delimiter }");
						}
					}
				} else {
					System.out.println("Line " + lineNum + ": expected delimiter {");
					out.println("Line " + lineNum + ": expected delimiter {");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected IDENTIFIER");
				out.println("Line " + lineNum + ": expected IDENTIFIER");
			}
		} else {
			System.out.println("Line " + lineNum + ": expected SWITCH");
			out.println("Line " + lineNum + ": expected SWITCH");
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("switch Count - " + tempLine);
		}
		
		//System.out.println("Switch_Stmt End");
		return tempSwSt;
	}
	
	//This method is used to handle multiple cases for the Switch statement by using a recursion to call itself
	private boolean Case_List() {
		//System.out.println("Case_List Start");
		boolean tempCaLi = false;
		lineNum = tkn.getLine();
		
		if (tkn.getWord().equals("CASE")) {						//Checks vector for a CASE
			Case();												//Calls Case()
			lineNum = tkn.getLine();
			
			if (tkn.getWord().equals("}")) {					//Checks vector for a "}"
				lineNum = tkn.getLine();
				//System.out.println("Case_List End True");
				return true;
					
			} else if (tkn.getWord().equals("CASE")) {			//Checks vector for a CASE
				Case_List();									//Calls Case_List()
				lineNum = tkn.getLine();
				
			} else {
				//System.out.println("Line " + lineNum + ": expected delimiter }");
				//out.println("Line " + lineNum + ": expected delimiter }");
			}
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("case list Count - " + tempLine);
		}
		
		//System.out.println("Case_List End");
		return tempCaLi;
	}
		
	//This method handles any cases for the switch statement with the pattern of the keyword CASE followed
	//by a ':' then it calls the Body() method
	private boolean Case() {
		//System.out.println("Case Start");
		boolean tempCas = false;
		lineNum = tkn.getLine();
		
		if (tkn.getWord().equals("CASE")) {							//Checks vector for a CASE
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();						//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			
			if (tkn.getToken().equals("INTEGER")) {					//Checks vector for a INTEGER
				if (en.hasMoreElements()) {
					
					//This block is used to assembly instruction to the assemCode arraylist
					caseCount++;
					whichCase = "LIT " + tkn.getWord() + ", 0";		//Add LIT instruction to the arrayList
					assemCode.add(whichCase);
					assemCount++;
					assemCode.add("OPR 15, 0");						//Add OPR instruction to the arrayList
					assemCount++;
					jumpCount++;
					assemCode.add("JMC #e" + jumpCount + ", false");//Add JMC instruction to the arrayList
					assemCount++;
					
					tkn = (Token)en.nextElement();					//Increment the token using enumeration
					tempLine = tkn.getLine();
					
				}
				
				if (tkn.getWord().equals(":")) {					//Checks vector for a ":"
					if (en.hasMoreElements()) {
						tkn = (Token)en.nextElement();				//Increment the token using enumeration
						tempLine = tkn.getLine();
					}
					
					if (tkn.getWord().equals("{")) {				//Checks vector for a "{"
						Body();
						lineNum = tkn.getLine();
						
						if (tkn.getWord().equals("}")) {			//Checks vector for a "}"
							jumpNum.add(assemCode.size()+1);
							lineNum = tkn.getLine();
							//System.out.println("Case End True");
							return true;
							
						} else {
							//System.out.println("Line " + lineNum + ": expected delimiter }");
							//out.println("Line " + lineNum + ": expected delimiter }");
						}
					} else {
						System.out.println("Line " + lineNum + ": expected delimiter {");
						out.println("Line " + lineNum + ": expected delimiter {");
					}
				} else {
					System.out.println("Line " + lineNum + ": expected delimiter :");
					out.println("Line " + lineNum + ": expected delimiter :");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected INTEGER");
				out.println("Line " + lineNum + ": expected INTEGER");
			}
		} else {
			System.out.println("Line " + lineNum + ": expected CASE");
			out.println("Line " + lineNum + ": expected CASE");
		}

		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();						//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("case Count - " + tempLine);
		}
		
		//System.out.println("Case End");
		return tempCas;
	}
	
	//This method handles the default case for the switch statement with the pattern of the keyword CASE 
	//followed by a ':' then it calls the Body() method
	private boolean Default_Case() {
		//System.out.println("Default_Case Start");
		boolean tempDC = false;
		lineNum = tkn.getLine();
				
		if (tkn.getWord().equals("DEFAULT")) {						//Checks vector for a DEFAULT
			
			//This block adds a jump instruction to the assemCode arrayList
			jumpCount++;
			assemCode.add("JMP #e" + jumpCount + ", 0");
			jumpNum.add(assemCode.size()+1);
			
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();						//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			
			if (tkn.getWord().equals(":")) {						//Checks vector for a ":"
				if (en.hasMoreElements()) {
					tkn = (Token)en.nextElement();					//Increment the token using enumeration
					tempLine = tkn.getLine();
				}
				
				if (tkn.getWord().equals("{")) {					//Checks vector for a "{"
					
					Body();											//Calls Body()
					lineNum = tkn.getLine();
					
					if (tkn.getWord().equals("}")) {				//Checks vector for a "}"
						lineNum = tkn.getLine();
						//System.out.println("Default_Case End True");
						return true;
						
					} else {
							System.out.println("Line " + lineNum + ": expected delimiter }");
							out.println("Line " + lineNum + ": expected delimiter }");
					}
				} else {
					System.out.println("Line " + lineNum + ": expected delimiter {");
					out.println("Line " + lineNum + ": expected delimiter {");
				}
			} else {
				System.out.println("Line " + lineNum + ": expected delimiter :");
				out.println("Line " + lineNum + ": expected delimiter :");
			}
		} else {
			System.out.println("Line " + lineNum + ": expected DEFAULT");
			out.println("Line " + lineNum + ": expected DEFAULT");
		}
		
		
		if (en.hasMoreElements()) {
			tkn = (Token)en.nextElement();						//Increment the token using enumeration
			tempLine = tkn.getLine();
		}
		
		//This loop is used if there is an error from this rule by incrementing the vector
		while (!tkn.getWord().equals(";") && (lineNum == tempLine) && !tkn.getToken().equals("The End")){
			if (en.hasMoreElements()) {
				tkn = (Token)en.nextElement();					//Increment the token using enumeration
				tempLine = tkn.getLine();
			}
			//System.out.println("default case Count - " + tempLine);
		}
		
		//System.out.println("Default_Case End- " + tempDC);
		return tempDC;
	}
	
	//This method checks if the current token is a type i.e integer, boolean, and so on
	private boolean Type() {
		//System.out.println("Type Start");
		boolean tempType = false;
		lineNum = tkn.getLine();
		
		//Checks vector for a keyword
		for (int d = 0; d <= keywords.length-1; d++){
			  if (tkn.getWord().equals(keywords[d])) {
				  
				  if (tkn.getWord().equals("integer"))
					  theType = "integer";
				  else if (tkn.getWord().equals("boolean"))
					  theType = "boolean";
					  
				  if (en.hasMoreElements()) {
						tkn = (Token)en.nextElement();			//Increment the token using enumeration	
						tempLine = tkn.getLine();
					}
				 //System.out.println("Type End True");
				  tempType = true;		  
			  }
		}	
		
		if (!tempType)
			System.out.println("Line " + lineNum + ": expected type");
		
		//System.out.println("Type End");
		return tempType;
	}
	

}
	