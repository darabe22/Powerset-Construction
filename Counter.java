/* Durelle Rabe
 * ITK 328
 * Convert a regular expression into a NFA, then into an equivalent DFA, then minimize the DFA
 */

/*
 * This class is used to insure that there is a unique number every time a program is run.  A class structured this way is referred to as a singleton.
 */
public final class Counter {
	/*
	 * This is the only reference allowed and is only initialized once during the run of the program.
	 */
	private final static Counter singleReference=new Counter();
	private static Long number;
	/*
	 * Making this private insures that there can be only one instance of this class.
	 */
	private Counter(){
		number=new Long(0);
	}

	/*
	 * Allows other classes to get the one reference.
	 */
	public static Counter getCounterInstance(){
		return singleReference;
	}

	/*
	 * Returns the current number and then increments it so that it is different on the next call.
	 */
	public Long getNext(){
		return number++;
	}

	/*
	 * This restarts the number to zero.  This method should be used very carefully.
	 */
	public void restart(){
		number-=number;
	}

	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}
}
