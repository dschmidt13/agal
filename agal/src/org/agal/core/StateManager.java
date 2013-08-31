/*
 * StateManager.java
 * 
 * Created on Jul 7, 2012
 * 
 */
package org.agal.core;

/**
 * A StateManager is a Singleton that implements the details of interfacing a particular
 * problem's state model with a particular class of algorithms. Because the methods
 * defined by StateManager are heavily used by Genetic/Eugenic algorithms, implementations
 * should hold in mind the goal of not only constant time complexities, but having
 * relatively small constants as well.
 * <p>
 * As Singletons, implementations of this interface should be thread-safe.
 * @author David Schmidt
 */
public interface StateManager<S>
{

	/**
	 * Generates a random problem state.
	 * @return an {@code S} containing a new random state.
	 */
	public S randomize( );


	/**
	 * Smoothly combines parts of two separate states into one new child state. The
	 * details are left to the implementation; the "crossover" technique is the most
	 * common technique, for example. Used heaviliy by genetic and eugenic algorithms.
	 * This method should attempt to run in constant time with a small constant.
	 * @param mother a {@code S} state to be used as one of the child's parents.
	 * @param father a {@code S} state to be used as one of the child's parents.
	 * @return an {@code S} state consisting of some combination of the two parent states.
	 */
	public S reproduce( S mother, S father );

}
