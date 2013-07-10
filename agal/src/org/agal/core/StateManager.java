/*
 * StateManager.java
 * 
 * Created on Jul 7, 2012
 * 
 */
package org.agal.core;

/**
 * StateManager is a Singleton that implements the details of interfacing a particular
 * problem's state model with a particular class of algorithms. Most if not all of the
 * methods defined by StateManager are heavily used by Genetic/Eugenic algorithms. As
 * such, implementations should hold in mind the goal of not only constant time
 * complexities, but having relatively small constants as well.
 * <p>
 * As a Singleton, this class is thread safe; concrete extensions of it should be kept so
 * too.
 * @author David Schmidt
 */
public interface StateManager<S>
{

	/**
	 * Calculates the fitness of the given individual. Fitness values are compared with
	 * {@code compare}.
	 * @return a double indicating the fitness of the given individual.
	 */
	public double fitness( S individual );


	/**
	 * Returns a slightly modified version of the original state given. The severity of
	 * the mutation is left to the implementation. May return a copy or modify the
	 * original instance, so long as modifications are thread-safe. Used by genetic and
	 * eugenic algorithms. This should attempt to run in constant time.
	 * @param original a {@code S} containing the original state to mutate.
	 * @return an {@code S} which may be a copy or the original object, with a slight
	 *         change made to the state.
	 */
	public S mutate( S original );


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
