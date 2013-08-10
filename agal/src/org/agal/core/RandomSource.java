/*
 * RandomSource.java
 * 
 * Created on Jul 29, 2013
 * 
 */
package org.agal.core;

import java.util.Random;

/**
 * RandomSource specifies a thread-safe way to tie a Random or an arbitrary set of Random
 * instances to a SearchContext, allowing custom {@code java.util.Random} extensions to be
 * substituted for the base implementation and at the same time defining a multi-threading
 * policy for sharing (or not sharing) Random instances across multiple search threads.
 * <p>
 * As noted, RandomSource implementations must be thread safe. In fact, defining a
 * multi-threading model is a concrete subclass's primary responsibility, as the abstract
 * form can handle custom Random extensions suitably for most practical use cases on its
 * own.
 * @author David Schmidt
 */
public abstract class RandomSource
{
	// Data members.
	private final Class<? extends Random> fieldRandomClass;


	/**
	 * ThreadLocalRandomSource constructor.
	 * @param randomClass a Class subclassing {@code java.util.Random} to be used as the
	 *            RNG for this RandomSource. This {@code randomClass} must define a
	 *            no-args constructor; if it doesn't, an IllegalArgumentException will be
	 *            thrown from this constructor. If {@code null} is passed in,
	 *            {@code java.util.Random} will be used as a default.
	 * @throws IllegalArgumentException if the given {@code randomClass} cannot be
	 *             instantiated with the no-args constructor.
	 */
	public RandomSource( Class<? extends Random> randomClass )
			throws IllegalArgumentException
	{
		fieldRandomClass = ( randomClass != null ) ? randomClass : Random.class;

		try
			{
			// Pre-emptively ensure that the class can be instantiated with a no-args
			// constructor.
			fieldRandomClass.newInstance( );
			}
		catch ( Exception exception )
			{
			throw new IllegalArgumentException(
					"The Random class provided must have a no-args constructor.", exception );
			}

	} // ThreadLocalRandomSource


	/**
	 * @return a {@code Random} instance to be used to generate entropy.
	 */
	public abstract Random getRandom( );


	public Class<? extends Random> getRandomClass( )
	{
		return fieldRandomClass;

	} // getRandomClass


	/**
	 * Reflectively instantiates an instance of the {@code randomClass} associated with
	 * this RandomSource.
	 * @return a new Random instance of the relevant subtype.
	 * @throws RuntimeException if the class's no-args constructor throws an exception.
	 */
	protected Random newRandom( )
			throws RuntimeException
	{
		// TODO - Now that it's its own method and in the top-level class, we could do
		// something more sophisticated here such as use a varargs arg array for this
		// method and pass whatever we get into the Random's constructor. In that case,
		// though, we'd have to get rid of the verification code in the constructor.

		try
			{
			Random random = fieldRandomClass.newInstance( );
			return random;
			}
		catch ( Exception exception )
			{
			// It's possible that the constructor itself will throw an exception.
			// Since we're verifying the availability of the constructor in OUR
			// constructor, we can assert that this exception is only thrown by the
			// Random's constructor. If that's not the case, someone failed in the
			// code maintenance department, and weird reflection exceptions will start
			// appearing here.
			throw new RuntimeException( "Reflective instantiation of Random failed!", exception );
			}

	} // newRandom

}
