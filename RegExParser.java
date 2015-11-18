/* Durelle Rabe
   ITK 328
   Convert a regular expression into a NFA, then into an equivalent DFA, then minimize the DFA
*/

import java.util.*;

/*
 * This class doesn't parse whitespace.  Each whitespace is seen as a character in the regular expression.
 * Regular expressions can have the digits 0-9 and any letter in the alphabet (capitalized and not capitalized).
 * The 3 operations that can be used in the regular expression are |, ., and *.
 * | = or/union
 * . = concatenation
 * * = Kleene closure
 * () are also used for grouping.
 */
class RegExParser{
	/*
	 * The expression to be parsed and converted into a graph.
	 */
	private String regEx, postFixRegEx;
	/*
	 * Used when parsing the expression to create the final graph.
	 */
	private Stack <Graph> myStack=new Stack<Graph>();
	/*
	 * Used to insure that each new state has a unique name.
	 */
	private Counter id=Counter.getCounterInstance();
	/*
	 * Stores the different characters used in the regular expression.
	 */
	private SetList <Character> inputCharacters=new SetList<Character>();

	public RegExParser(){
		regEx="";
		postFixRegEx=convertToPostFix(regEx);
	}

	public RegExParser(String regularExpression){
		regEx=regularExpression;
		postFixRegEx=convertToPostFix(regEx);
	}

	/*
	 * This function converts the regular expression from in-fix notation to post-fix notation, thus eliminating the need for parenthesis.
	 */
	public String convertToPostFix(String ex){
		myStack=new Stack<Graph>();
		inputCharacters=new SetList<Character>();

		String pfex=new String();
		Stack <Character> operators=new Stack <Character>();
		int index=0;
		Character currentChar;
		Boolean isGreaterThan=false;

		while(index<regEx.length()){
			currentChar=regEx.charAt(index);
			isGreaterThan=false;

			switch(currentChar){
				case '(':
					operators.push(currentChar);
				break;
				case ')':
					if( operators.isEmpty() ){
						System.out.println("Closing Parenthesis with an empty stack!");
					}else{
						do{
							if( operators.peek()=='(' ){
								operators.pop();
								isGreaterThan=true;
							}else{
								pfex+=operators.pop();
							}
						}while( ! (operators.isEmpty() | isGreaterThan) );
					}
				break;
				case '|':
					if(operators.isEmpty()){
						operators.push(currentChar);
					}else{
						do{
							isGreaterThan=compareOperators(currentChar,operators.peek());
							if(isGreaterThan){
								operators.push(currentChar);
							}else{
								pfex+=operators.pop();
							}
						}while( ! (operators.isEmpty() | isGreaterThan) );
					}
					if(operators.isEmpty()){
						operators.push(currentChar);
					}
				break;
				case '.':
					if(operators.empty()){
						operators.push(currentChar);
					}else{
						do{
							isGreaterThan=compareOperators(currentChar,operators.peek());
							if(isGreaterThan){
								operators.push(currentChar);
							}else{
								pfex+=operators.pop();
							}
						}while( ! (operators.isEmpty() | isGreaterThan) );
					}
					if(operators.isEmpty()){
						operators.push(currentChar);
					}
				break;
				case '*':
					pfex+=currentChar;
				break;
				default:
					pfex+=currentChar;
					inputCharacters.add(currentChar);
				break;
			}
			index++;
		}

		while( ! operators.isEmpty() ){
			pfex+=operators.pop();
		}

		return pfex.toString();
	}

	/*
	 * This determines if the leftChar has a higher precedence than the rightChar.
	 */
	public Boolean compareOperators(Character leftChar, Character rightChar){
		Boolean isGreaterThan=false;
		switch(leftChar){
		case '|':
			switch(rightChar){
			case '(':
				isGreaterThan=true;
				break;
			case ')':
				isGreaterThan=true;
				break;
			}
			break;
		case '.':
			switch(rightChar){
			case '(':
				isGreaterThan=true;
				break;
			case ')':
				isGreaterThan=true;
				break;
			case '|':
				isGreaterThan=true;
				break;
			}
			break;
		}
		return isGreaterThan;
	}

	public void changeRegEx(String regularExpression){
		inputCharacters=new SetList<Character>();
		regEx=regularExpression;
		postFixRegEx=convertToPostFix(regEx);
	}

	/*
	 * This function is present simply because of readability.  The real function is parse.
	 */
	public Graph getGraph(){
		return parse();
	}

	public SetList<Character> getInputAlphabet(){
		return inputCharacters;
	}

	public String getPostFixExpression(){
		return postFixRegEx;
	}

	/*
	 * This function parses the post-fix expression and converts it into a graph.
	 */
	public Graph parse(){
		//uses postFixRegEx to construct a Graph
		Graph nfa=new Graph();
		int index=0;
		while(index<postFixRegEx.length()){
			//evaluate expression to create graph
			Character currentChar=postFixRegEx.charAt(index);
			if(currentChar=='|'){
				if( myStack.size() > 1 ){
					Graph oGraph1=myStack.pop();
					Graph oGraph2=myStack.pop();
					myStack.push(Graph.union(oGraph1, oGraph2));
				}
			}else if(currentChar=='.'){
				if( myStack.size() > 1 ){
					Graph oGraph2=myStack.pop();
					Graph oGraph1=myStack.pop();
					myStack.push(Graph.concatenate(oGraph1, oGraph2));
				}
			}else if(currentChar=='*'){
				Graph nGraph=myStack.pop();
				myStack.push(Graph.withClosure(nGraph));
			}else{
				Graph aGraph=new Graph();
				State state1=new State(id.getNext());
				State state2=new State(id.getNext());

				state1.addNeighbor(state2, currentChar.toString());
				aGraph.addStartingState(state1);
				aGraph.addFinalState(state2);

				myStack.push(aGraph);
			}
			index++;
			//end of while loop
		}
		if(myStack.size()>0){
			nfa=myStack.pop();
		}else{
			System.out.println("Nothing was in the stack!");
		}
		return nfa;
	}
}