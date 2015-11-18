/* Durelle Rabe
   ITK 328
   Convert a regular expression into a NFA, then into an equivalent DFA, then minimize the DFA
*/

import java.util.*;
/*
 * This class maps a value to a key.  This class is more memory efficient than a Hashmap.
 * The order of the objects is guaranteed to stay the same, making it easier to check results for each run.
 */
class PairedSetList<L1, L2>{
	/*
	 * This list is the key to the value in the second list.  There are no duplicate values.
	 */
	private SetList<L1> List1;
	/*
	 * This list contains a value.  There can be duplicate values in this list.
	 */
	private ArrayList<L2> List2;

	public PairedSetList(){
		List1=new SetList<L1>();
		List2=new ArrayList<L2>();
	}

	public void printList(){
		if( !List1.isEmpty() ){
			int index=0;
			while(index<List1.size()){
				System.out.println("   "+(index+1)+")"+List2.get(index)+"--->"+List1.get(index));
				index++;
			}
		}else{
			System.out.println("   No neighbors :(");
		}
	}

	public boolean add(L1 k, L2 v){
		if(contains(k)){
			return false;
		}else{
			return List1.add(k) && List2.add(v);
		}
	}

	public void clear(){
		List1.clear();
		List2.clear();
	}

	public boolean contains(L1 k) {
		return List1.contains(k);
	}




/*
 * Returns the value associated with the key.
 */
	public L2 getValueFrom(L1 key){
		int index=-1;
		if(List1.contains(key)){
			index=List1.indexOf(key);
		}
		return List2.get(index);
	}
/*
 * Returns the Key at a given index.  There is only one instance of a Key in the list.
 */
	public L1 getKeyFromIndex(int i){
		return List1.get(i);
	}
/*
 * Returns the Value placed at a given index.  There can be duplicate Values.
 */
	public L2 getValueFromIndex(int i){
		return List2.get(i);
	}

	public void changeValueFor(L1 key, L2 value){
		List2.set( List1.indexOf(key), value );
	}

	public void changeValueFromIndex(int i, L2 value){
		List2.set(i, value);
	}

	public int indexOf(L1 k){
		return List1.indexOf(k);
	}

	public boolean isEmpty() {
		return List1.isEmpty();
	}

	public boolean remove(L1 k) {
		int index=List1.indexOf(k);
		if(index>-1){
			List1.remove(index);
			List2.remove(index);
			return true;
		}else{
			return false;
		}
	}

	public int size() {
		return List1.size();
	}
}