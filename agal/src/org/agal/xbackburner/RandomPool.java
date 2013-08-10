/*
 * RandomPool.java
 * 
 * Created on Jul 22, 2012
 * 
 */
package org.agal.xbackburner;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * RandomPool represents an attempt at introducing further entropy into an already chaotic
 * system out of some superstition or weak intuition that using ThreadLocal randoms in the
 * same algorithm will result in somewhat predictable and therefore not so useful random
 * number generation. Instead, this class allows a concurrent object to share a
 * self-scaling "pool" of randoms across all threads using the object.
 * <p>
 * RandomPool is, obviously, thread safe.
 * @author David Schmidt
 */
public class RandomPool
{
	// Constants.
	private static final int DEFAULT_INITIAL_SIZE = 3;

	// Data members.
	private ConcurrentLinkedQueue<Random> fieldRandomPool = new ConcurrentLinkedQueue<Random>( );


	/**
	 * RandomPool constructor. Uses a default initial size of {@code 3}.
	 */
	public RandomPool( )
	{
		this( DEFAULT_INITIAL_SIZE );

	} // RandomPool


	/**
	 * RandomPool constructor.
	 * @param initialSize an int indicating the number of Randoms that should be in the
	 *            pool to begin with.
	 */
	public RandomPool( int initialSize )
	{
		for ( int i = 0; i < initialSize; i++ )
			fieldRandomPool.offer( new Random( ) );

	} // RandomPool


	/**
	 * Obtains a {@code Random} from the shared Random pool. RandomPool will attempt to
	 * ensure that other threads will not obtain the same Random by moving it to the end
	 * of the queue, but it makes no guarantee. Since {@code Random} is thread safe, this
	 * is only a problem in that hanging on to a Random instance too long may cause
	 * contention during number generation.
	 * @return a {@code Random} from this pool.
	 */
	public Random borrowRandom( )
	{
		Random random = fieldRandomPool.poll( );
		if ( random == null )
			random = new Random( );
		fieldRandomPool.offer( random );
		return random;

	} // borrowRandom

}
