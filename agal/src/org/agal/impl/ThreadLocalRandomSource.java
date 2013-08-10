/*
 * ThreadLocalRandomSource.java
 * 
 * Created on Aug 9, 2013
 * 
 */
package org.agal.impl;

import java.util.Random;

import org.agal.core.RandomSource;

/**
 * ThreadLocalRandomSource is a simple RandomSource implementation which stores separate
 * Random instances in a ThreadLocal to eliminate contention for random number generation.
 * <p>
 * ThreadLocalRandomSource is thread safe.
 * @author David Schmidt
 */
public class ThreadLocalRandomSource extends RandomSource
{
	// Data members.
	private final ThreadLocal<Random> fieldRandom = new ThreadLocal<Random>( );


	public ThreadLocalRandomSource( Class<? extends Random> randomClass )
			throws IllegalArgumentException
	{
		super( randomClass );

	} // ThreadLocalRandomSource


	@Override
	public Random getRandom( )
	{
		Random random = fieldRandom.get( );

		if ( random == null )
			{
			random = newRandom( );
			fieldRandom.set( random );
			}

		return random;

	} // getRandom

}
