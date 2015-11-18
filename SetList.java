/* Durelle Rabe
   ITK 328
   Convert a regular expression into a NFA, then into an equivalent DFA, then minimize the DFA
*/
import java.util.*;
/*
 * This is the base class from which the rest of the data structures for the graph are based.
 * This class insures that there is only one of each element in the array by implementing methods that use the superclass Object.equals method.
 */
public class SetList<E> implements Collection<E>{
	/*
	 * This is the list.  Uniqueness of values is guaranteed through the input methods.
	 */
	private ArrayList<E> list=new ArrayList<E>();
	
	public SetList(){
		list=new ArrayList<E>();
	}
	
	public boolean add(E e){
		boolean addedE=false;
		if( ! list.contains(e) ){
			addedE=list.add(e);
		}
		return addedE;
	}
	
	public void add(int index, E element){
		if( ! list.contains(element) ){
			list.add(index, element);
		}
	}
	
	public boolean addAll(Collection<? extends E> c) {
		boolean addedAll=true;
		Iterator<? extends E> iter=c.iterator();
		while( iter.hasNext() ){
			if( ! add( iter.next() ) ){
				addedAll=false;
			}
		}
		return addedAll;
	}
	
/*
 * If the list already contains the given element then the IllegalArgumentException is thrown.	
 */
	public E set(int index, E element) throws IllegalArgumentException{
		if(list.contains(element)){
			throw new IllegalArgumentException();
		}
		return list.set(index, element);
	}
	
	public boolean contains(Object o){
		return list.contains(o);
	}
	
	public int indexOf(Object o){
		return list.indexOf(o);
	}

	public E get(int i) {
		return list.get(i);
	}

	public int size() {
		return list.size();
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}

	public E remove(int index) {
		return list.remove(index);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public void clear() {
		list.clear();
	}

	public Iterator<E> iterator() {
		return list.iterator();
	}
	/*
	 * This method only checks to make sure that the same objects are in the list.
	 * The order that the objects appear isn't checked.
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object o){
		boolean isEqual=true;
		if(o==null){
			return false;
		}
		if( this == o ){
			return true;
		}
		if( o.getClass()==this.getClass() ){
			SetList<E> oList = (SetList<E>)o;
			if( this.size()== oList.size() ){
				int index=0;
				while( isEqual && ( index < this.size() ) ){
					if( ! this.contains( oList.get(index) ) ){
						isEqual=false;
					}
					index++;
				}
			}else{
				isEqual=false;
			}
		}else{
			isEqual=false;
		}
		return isEqual;
	}
	
	public int hashCode(){
		Integer hash=this.size();
		hash=hash.hashCode();
		
		int index=0;
		while(index<list.size()){
			hash+=list.get(index).hashCode();
		}
		
		return hash;
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}
	
	public boolean containsAny(Collection<?> c){
		boolean contains=false;
		Iterator<?> iter=c.iterator();
		while( iter.hasNext() && (! contains) ){
			contains=list.contains(iter.next());
		}
		return contains;
	}

	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}
	
	public void printList(){
		System.out.print("{ ");
		int index=0;
		while(index<list.size()){
			System.out.print(" "+list.get(index)+" ");
			index++;
		}
		System.out.print(" }");
		System.out.println();
	}
	
}
