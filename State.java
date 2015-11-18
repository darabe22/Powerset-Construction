/* Durelle Rabe
   ITK 328
   Convert a regular expression into a NFA, then into an equivalent DFA, then minimize the DFA
*/

/*
 * This class represents a single state in a transition graph.
 * The state's neighbors and path is represented by a paired list.
 * The state can't have the same neighbor twice but can have itself as a neighbor.
 */
class State{
	/*
	 * The state's name.
	 */
	private String name;
	/*
	*For neighboringStates the neighboring states and the path to that state are stored as a pair.
	*/
	private PairedSetList<State,String> neighboringStates;

	/*
	 * Generally a String should be used for the name but if a primitive is wrapped into it's class then that will also work.
	 * In my implementation I used the Long object for names so I could guarantee that the names were unique.
	 */
	public State(Object nName){
		name=nName.toString();
		neighboringStates=new PairedSetList<State,String>();
	}

	public String toString(){
		return name;
	}

	public String getName(){
		return name;
	}

	/*
	 * Checks to see if this state has another state as a neighbor.
	 */
	public Boolean hasNeigbor(State possibleNeighbor){
		return neighboringStates.contains(possibleNeighbor);
	}















	/*
	 * Returns all neighboring states that can be reached immediately through the given path.
	 */
	public SetList<State> getNeighborsFromPath(String path){
		SetList<State> neighborsFromPath=new SetList<State>();
		State current;
		int index=0;

		while( index < neighboringStates.size() ){
			current=neighboringStates.getKeyFromIndex(index);

			if( neighboringStates.getValueFromIndex(index).equals(path) ){
				neighborsFromPath.add(current);
			}
			index++;
		}

		return neighborsFromPath;
	}

	/*
	 * Returns the path that is taken to get from this state to the neighbor.
	 */
	public String getPathToNeighbor(State possibleNeighbor){
		return neighboringStates.getValueFrom(possibleNeighbor);
	}

	/*
	 * Adds a neighbor that this state can reach through the given path.
	 */
	public boolean addNeighbor(State someState, String path){
		boolean addedNeighbor=false;

		if(neighboringStates.contains(someState)){
			System.out.println("You tried adding a state that was already a neighbor");
		}else{
			neighboringStates.add(someState, path);
			addedNeighbor=true;
		}

		return addedNeighbor;
	}

















	/*
	 * This method adds all of the given state's neighbors to this state.
	 */
	public void addNeighborsFromState(State someState){
		int index=0;
		State nNeighbor;
		String nnPath;
		while( index < someState.neighboringStates.size() ){
			nNeighbor=someState.neighboringStates.getKeyFromIndex(index);
			nnPath=someState.neighboringStates.getValueFromIndex(index);
			addNeighbor( nNeighbor , nnPath );
			index++;
		}
	}

	/*
	 * This adds a state as a neighbor with a blank path.
	 */
	public boolean addNeighbor(State someState){
		if(neighboringStates.contains(someState)){
			return false;
		}else{
			return addNeighbor(someState, "");
		}
	}

	/*
	 * This method changes the path used to get to the neighbor.  This method actually adds the path in as an or statement.
	 */
	public void changePathToNeighbor(State someState, String path){
		String oldPath=neighboringStates.getValueFrom(someState);
		neighboringStates.changeValueFor(someState, oldPath+"|"+path);
	}

	/*
	 * Returns all of the neighbors.
	 */
	public PairedSetList<State,String> getNeighbors(){
		return neighboringStates;
	}

	/*
	 * Removes a Neighbor.
	 */
	public boolean removeNeigbor(State someState){
		return neighboringStates.remove(someState);
	}

	public boolean equals(Object o){
		return name.equals(o.toString());
	}

	/*
	 * Prints all of this state's neighbors.
	 */
	public void printNeighbors(){
		neighboringStates.printList();
	}
}