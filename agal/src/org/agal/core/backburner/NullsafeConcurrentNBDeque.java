/*
 * NullsafeConcurrentNBDeque.java
 * 
 * Created on Jul 14, 2012
 * 
 */
package org.agal.core.backburner;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * NullsafeConcurrentNBDeque is a wrapper class for ConcurrentLinkedDeque. The "NB"
 * signifies the non-blocking algorithm that ConcurrentLinkedDeque implements. In addition
 * to null safety, this wrapper provides constant-time {@code size( )} operations which
 * are not guaranteed to be perfectly accurate, but reflect the number of elements which
 * will be, or recently were, in the queue.
 * @author David Schmidt
 */
public class NullsafeConcurrentNBDeque<E> extends ConcurrentLinkedDeque<E>
{
	// Default serial version.
	private static final long serialVersionUID = 1L;

	// Constant-time size field.
	private AtomicInteger fieldSize = new AtomicInteger( 0 );


	/**
	 * NullsafeConcurrentNBDeque constructor.
	 */
	public NullsafeConcurrentNBDeque( )
	{
	} // NullsafeConcurrentNBDeque


	/**
	 * NullsafeConcurrentNBDeque constructor.
	 * @param collection a {@code Collection<E>} to use to pre-populate this queue.
	 *            Elements are added to the tail of the queue in iteration order of the
	 *            collection, as by {@code addAll( )}.
	 */
	public NullsafeConcurrentNBDeque( Collection<E> collection )
	{
		super( );

		addAll( collection );

	} // NullsafeConcurrentNBDeque

	// TODO - everything else

}
