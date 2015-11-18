/* Durelle Rabe
   ITK 328
   Convert a regular expression into a NFA, then into an equivalent DFA, then minimize the DFA
*/

import java.util.*;

/*
*This class contains a NFA graph.  The nodes are known as states.
*The graph has only one starting state and one to many final states.
*/
class Graph{
	static Counter id=Counter.getCounterInstance();
	State startingState;
	SetList<State> states;
	SetList<State> finalStates;
	SetList<SetList<State>> dfaStates;
	ArrayList<ArrayList<SetList<State>>> stateMatrix;
	PairedSetList<SetList<State>, State> stateTable;

	public Graph(){
		states=new SetList<State>();
		finalStates=new SetList<State>();
	}

	public void addState(State nState){
		states.add(nState);
	}

	public void addStartingState(State nState){
		states.add(nState);
		startingState=nState;
	}

	public void addFinalState(State nState){
		states.add(nState);
		finalStates.add(nState);
	}

	public void addFinalStates(SetList<State> states){
		states.addAll(states);
		finalStates.addAll(states);
	}

	public boolean addGraph(Graph oGraph){
		return states.addAll(oGraph.states);
	}

	/*
	 * Doesn't check to see if the state being removed is the starting state.
	 * Doesn't remove the state from other states' neighbor list.
	 */
	public void removeState(State oState){
		states.remove(oState);
		finalStates.remove(oState);
	}

	public State getStartingState(){
		return startingState;
	}

	public SetList<State> getFinalStates(){
		return finalStates;
	}

	public static Graph concatenate(Graph graph1, Graph graph2){
		Graph nGraph=new Graph();
		int index=0;

		SetList<State> graph1FinalStates=graph1.getFinalStates();
		while( index < graph1FinalStates.size() ){
			graph1FinalStates.get(index).addNeighborsFromState(graph2.startingState);
			index++;
		}

		nGraph.addStartingState(graph1.startingState);
		nGraph.addFinalStates(graph2.finalStates);
		nGraph.addGraph(graph1);
		nGraph.addGraph(graph2);

		nGraph.removeState(graph2.startingState);

		return nGraph;
	}

	public static Graph union(Graph graph1, Graph graph2){
		Graph nGraph=new Graph();

		graph1.startingState.addNeighborsFromState(graph2.startingState);

		nGraph.addStartingState(graph1.startingState);
		nGraph.addFinalStates(graph1.finalStates);
		nGraph.addFinalStates(graph2.finalStates);
		nGraph.addGraph(graph1);
		nGraph.addGraph(graph2);

		nGraph.removeState(graph2.startingState);

		return nGraph;
	}

	public static Graph withClosure(Graph graph1){
		Graph nGraph=new Graph();
		State state1=new State(id.getNext());
		State state2=new State(id.getNext());
		int index=0;

		SetList<State> graph1FinalStates=graph1.getFinalStates();
		while(index<graph1FinalStates.size()){
			graph1FinalStates.get(index).addNeighbor(graph1.startingState);
			graph1FinalStates.get(index).addNeighbor(state2);
			state1.addNeighbor(graph1FinalStates.get(index));
			index++;
		}
		nGraph.addStartingState(state1);
		nGraph.addFinalState(state2);
		nGraph.addGraph(graph1);

		return nGraph;
	}









	/*
	 * This function assumes that the graph being minimized is a dfa.
	 */
	public Graph getMinimizedGraph(SetList<Character> alphabet){
		Graph nGraph=new Graph();
		//allows each pair to be marked as distinguishable or indistinguishable
		PairedSetList<ArrayList<State>, Character> markedPairs=new PairedSetList<ArrayList<State>, Character>();
		//equivalenceState is an equivalence class
		SetList<State> equivalenceState=new SetList<State>();
		SetList<State> temp;
		SetList<SetList<State>> equivalenceStates=new SetList<SetList<State>>();
		//connects a regular state to an equivalence class
		stateTable=new PairedSetList<SetList<State>, State>();

		ArrayList<State> pairedStates, transitionPairedStates;
		SetList<State> tStates1, tStates2;
		State state1, state2, state1b, state2b;
		String path;
		boolean changed, found;
		int index1, index2, index3;

		//sets up the initial equivalence classes between final and nonfinal states
		index1=0;
		while( index1 < states.size() ){
			index2=index1;
			while(index2<states.size() ){
				pairedStates=new ArrayList<State>(2);
				pairedStates.add(states.get(index1));
				pairedStates.add(states.get(index2));
				//Used exclusive or
				if( finalStates.contains(states.get(index1)) ^ finalStates.contains(states.get(index2)) ){
					markedPairs.add(pairedStates, 'd');
				}else{
					markedPairs.add(pairedStates, 'i');
				}

				index2++;
			}

			index1++;
		}

		//for each pair compute the transition, if the transition pair is distinguishable mark this pair as distinguishable
		changed=true;
		do{
			changed=false;
			index1=0;
			while( index1 < markedPairs.size() ){
				//skips pairs that are already distinguishable
				if(markedPairs.getValueFromIndex(index1).equals('i')){
					pairedStates=markedPairs.getKeyFromIndex(index1);
					state1=pairedStates.get(0);
					state2=pairedStates.get(1);

					//skip if the two states are the same
					if( !state1.equals(state2) ){
						//calculating the transition for each part of the alphabet
						index2=0;
						while( index2 < alphabet.size() ){
							path=alphabet.get(index2).toString();
							transitionPairedStates=new ArrayList<State>(2);
							tStates1=state1.getNeighborsFromPath(path);
							tStates2=state2.getNeighborsFromPath(path);

							transitionPairedStates.addAll(tStates1);
							transitionPairedStates.addAll(tStates2);

							//if one state transitions but the other doesn't then they are distinguishable
							if( ( tStates1.size()==0 ) ^ ( tStates2.size()==0 ) ){
								markedPairs.changeValueFromIndex(index1, 'd');
								changed=true;
								break;
							}
							//if both transition then check to see if the resulting pair is distinguishable, otherwise leave the pair as indistinguishable
							if( ( tStates1.size() > 0 ) && ( tStates2.size() > 0 ) ){
								//finding the corresponding transition pair's index in the markedPairs
								//this is the part that requires the graph that's being minimized to be a dfa
								state1b=transitionPairedStates.get(0);
								state2b=transitionPairedStates.get(1);
								//make sure that the two states are not the same when checking if the transition pair is distinguishable
								if( !state1b.equals(state2b) ){
									index3=0;
									while( index3 < markedPairs.size() ){
										if( markedPairs.getKeyFromIndex(index3).containsAll(transitionPairedStates) ){
											break;
										}
										index3++;
									}
									//checking to see if the transition pair is distinguishable
									if( markedPairs.getValueFromIndex(index3).equals('d')){
										markedPairs.changeValueFromIndex(index1, 'd');
										changed=true;
										break;
									}
								}
							}

							index2++;
							//end of while loop for checking transitions from a path
						}
					}
				}
				index1++;
				//end of while loop for iterating through every indistinguishable pair
			}
		}while( changed );

		//create the list of equivalence classes  aka the equivalenceStates
		index1=0;
		while( index1 < markedPairs.size() ){
			//check if the pair is indistinguishable
			if( markedPairs.getValueFromIndex(index1).equals('i') ){
				state1=markedPairs.getKeyFromIndex(index1).get(0);
				state2=markedPairs.getKeyFromIndex(index1).get(1);
				//find the equivalenceState the pair go into
				//if they don't go into one then create a new equivalenceState for them to go into.
				if( equivalenceStates.size() > 1 ){
					//create a loop to find the equivalenceState that contains the first state
					//if not found then create a new equivalenceState for both of them to go into.
					found=false;
					index2=0;
					while( index2 < equivalenceStates.size() ){
						equivalenceState=equivalenceStates.get(index2);
						if( equivalenceState.contains(state1) ){
							found=true;
							equivalenceState.add(state2);
							break;
						}
						index2++;
					}
					if( ! found ){
						equivalenceState=new SetList<State>();
						equivalenceState.add(state1);
						equivalenceState.add(state2);
						equivalenceStates.add(equivalenceState);
					}
				}else{
					equivalenceState=new SetList<State>();
					equivalenceState.add(state1);
					equivalenceState.add(state2);
					equivalenceStates.add(equivalenceState);
				}
			}
			index1++;
		}
		//assign a state to the equivalence classes using the stateTable
		index1=0;
		while( index1 < equivalenceStates.size() ){
			stateTable.add(equivalenceStates.get(index1), new State(id.getNext()) );
			index1++;
		}
		//create paths/transitions in the graph
		index1=0;
		while( index1 < stateTable.size() ){
			state1=stateTable.getKeyFromIndex(index1).get(0);
			//find the transition for each part of the alphabet
			index2=0;
			while( index2 < alphabet.size() ){
				path=alphabet.get(index2).toString();
				temp=state1.getNeighborsFromPath(path);
				//if temp contains a state then check to see which equivalence class state2 is in
				if(temp.size()>0){
					state2=temp.get(0);
					found=false;
					index3=0;
					//finding the equivalence class state2 is in
					while( index3 < stateTable.size() ){
						equivalenceState=stateTable.getKeyFromIndex(index3);
						if(equivalenceState.contains(state2)){
							found=true;
							break;
						}
						index3++;
					}
					//check to see if the state already has that neighbor
					if( found ){
						state1b=stateTable.getValueFromIndex(index1);
						state2b=stateTable.getValueFromIndex(index3);

						if( state1b.hasNeigbor(state2b)){
							state1b.changePathToNeighbor(state2b, path);
						}else{
							state1b.addNeighbor(state2b, path);
						}
					}
				}
				index2++;
				//end of while loop that finds the transition for each path
			}
			index1++;
			//end of the while loop that iterates through each equivalence class
		}
		//add the states to the graph
		nGraph.addStartingState(stateTable.getValueFromIndex(0));
		index1=0;
		while( index1 < stateTable.size() ){
			equivalenceState=stateTable.getKeyFromIndex(index1);
			state1=stateTable.getValueFromIndex(index1);

			if( equivalenceState.containsAny(finalStates) ){
				nGraph.addFinalState(state1);
			}else{
				nGraph.addState(state1);
			}
			index1++;
		}

		return nGraph;
	}

	public Graph getDFA(SetList<Character> alphabet){
		Graph nGraph=new Graph();
		SetList<State> dfaState=new SetList<State>();
		SetList<State> temp=new SetList<State>();
		//stateTable connects a regular state to a dfaState in the dfaStates
		stateTable=new PairedSetList<SetList<State>, State>();
		State state, state2;
		String path;
		int count, index;
		dfaStates=new SetList<SetList<State>>();
		stateMatrix=new ArrayList< ArrayList< SetList<State> > >();
		//getting the lambda closure for the starting state in the dfa
		dfaState.add(startingState);
		dfaState.addAll(getStatesFromPath(dfaState, ""));
		state=new State(id.getNext());
		stateTable.add(dfaState, state);
		dfaStates.add(dfaState);
		//creating all the sets of states that are reachable given a path
		count=0;
		while( count < dfaStates.size() ){
			dfaState=dfaStates.get(count);
			stateMatrix.add( new ArrayList<SetList<State>>( alphabet.size() ) );
			index=0;
			while( index < alphabet.size() ){
				path=alphabet.get(index).toString();
				temp=getStatesFromPath( dfaState, path );
				if( temp.size() > 0 ){
					dfaStates.add(temp);
					state=new State(id.getNext());
					stateTable.add(temp, state);
				}
				stateMatrix.get(count).add(temp);
				index++;
			}
			count++;
		};

		//creating the transitions/path for the states and adding them to the graph
		nGraph.addStartingState( stateTable.getValueFromIndex(0) );
		count=0;
		while( count < dfaStates.size() ){
			dfaState=dfaStates.get(count);
			if( dfaState.size() > 0 ){
				state=stateTable.getValueFrom(dfaState);
				if(dfaState.containsAny(finalStates)){
					nGraph.addFinalState(state);
				}else{
					nGraph.addState(state);
				}
				index=0;
				while( index < alphabet.size() ){
					temp=stateMatrix.get(count).get(index);
					if( temp.size() > 0 ){
						state2=stateTable.getValueFrom(temp);
						state.addNeighbor(state2, alphabet.get(index).toString() );
						if(temp.containsAny(finalStates)){
							nGraph.addFinalState(state2);
						}else{
							nGraph.addState(state2);
						}
					}
					index++;
				};
			}
			count ++;
		};

		nGraph.dfaStates=dfaStates;
		nGraph.stateMatrix=stateMatrix;

		return nGraph;
	}

	/*
	 * Finds all the states that are reachable through the given path.
	 */
	public SetList<State> getStatesFromPath(SetList<State> states, String path){
		SetList<State> neighboringStates=new SetList<State>();
		SetList<State> searchSet=new SetList<State>();
		SetList<State> temp;
		State current;
		int index=0, initialNumber=0, newNumber=0;

		searchSet.addAll(states);

		//This while loop determines which states are already reachable through lambda.
		//If the state is reachable through lambda then it is added to the set of states used to find the path.
		while( index < searchSet.size() ){
			current=searchSet.get(index);
			temp=current.getNeighborsFromPath("");
			searchSet.addAll(temp);
			newNumber=searchSet.size();

			index++;
			if( newNumber > initialNumber ){
				initialNumber=newNumber;
				index=0;
			}
		};

		//This while loop finds the set of states that are actually reachable through the given path.
		index=0;
		while( index < searchSet.size() ){
			current=searchSet.get(index);
			temp=current.getNeighborsFromPath(path);
			neighboringStates.addAll(temp);

			index++;
		};

		//This while loop finds the set of states that are reachable through lambda after the given path.
		initialNumber=neighboringStates.size();
		index=0;
		while( index < neighboringStates.size() ){
			current=neighboringStates.get(index);
			temp=current.getNeighborsFromPath("");
			neighboringStates.addAll(temp);
			newNumber=neighboringStates.size();

			index++;
			if( newNumber > initialNumber ){
				initialNumber=newNumber;
				index=0;
			}
		};

		return neighboringStates;
	}

	public void printGraph(){
		Iterator<State> iter=states.iterator();
		State current;
		PairedSetList<State,String> currentNeighbors;

		if( states.size() == 0){
			System.out.println();
			System.out.println("Empty Graph :(");
			System.out.println();
		}else{
			System.out.println();
			System.out.println("Printing Graph");
			System.out.println("Starting state is:  ");
			System.out.println("   "+startingState.toString());
			System.out.println("There are "+states.size()+" state(s) in the graph:  ");
			while( iter.hasNext() ){
				current=iter.next();
				currentNeighbors=current.getNeighbors();
				System.out.println("State "+current.getName());
				if( currentNeighbors.size() > 0 ){
					System.out.println("   has path(s) to");
					current.printNeighbors();
				}else{
					System.out.println("   has no neighbors :( ");
				}
			}
			int size=finalStates.size();
			if( size > 1 ){
				System.out.println("There are "+finalStates.size()+" Final states:  ");
			}else{
				System.out.println("There is "+size+" Final state:  ");
			}
			iter=finalStates.iterator();
			System.out.print("   ");
			while( iter.hasNext() ){
				current=iter.next();
				System.out.print(current.getName()+"  ");
			}
			System.out.println();
			System.out.println();
		}
	}

}