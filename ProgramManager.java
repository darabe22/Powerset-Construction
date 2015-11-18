/* Durelle Rabe
   ITK 328
   Convert a regular expression into a NFA, then into an equivalent DFA, then minimize the DFA
*/
import java.io.*;
class ProgramManager{
	static BufferedReader reader=new BufferedReader( new InputStreamReader(System.in) );
	static RegExParser parser=new RegExParser();
	static Graph nfa, dfa, mdfa;

	public static void main(String args[]){
		boolean again=false;
		String reply="";
		String expression;
		Counter idCounter=Counter.getCounterInstance();
		do{
			System.out.println("Enter the expression to be parsed: ");
			try{
				expression=reader.readLine();
			}catch(IOException ioe){
				expression="";
			};

			parser.changeRegEx(expression);
			System.out.println(parser.getPostFixExpression());
			System.out.println();

			nfa=parser.getGraph();
			System.out.println("Printing nfa");
			nfa.printGraph();

			dfa=nfa.getDFA(parser.getInputAlphabet());
			System.out.println("Printing dfa");
			dfa.printGraph();

			mdfa=dfa.getMinimizedGraph(parser.getInputAlphabet());
			System.out.println("Printing minimum dfa");
			mdfa.printGraph();

			//this part simply determines if the program should run again based from the user's answer
			System.out.println("Do you want to parse again (Y/N)?");
			try{
				reply=reader.readLine();
			}catch(IOException ioe){};
			reply.toLowerCase();
			if( reply.startsWith("y") ){
				again=true;
			}else{
				again=false;
			}
			//This starts the id counter at 0 again so that
			//the numbers don't get to big as the program continues parsing expressions.
			idCounter.restart();

		}while(again);
	}
}