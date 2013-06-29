/*
 * SingleDisplacementPool.java
 * 
 * Created on Mar 27, 2013
 * 
 */
package org.agal.core.backburner;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * SingleDisplacementPool is a concurrent non-blocking random access array list
 * implementation with the sole caveat that element insertions into the list cause
 * "displacement" of the element that was previously at that location, causing that
 * element to relocate to the end of the list rather than shuffling the entire list down.
 * Similarly, mid-list removal causes single element replacement, with the replacement
 * taken from the end of the list. This causes random insertions and removals in the pool
 * to run in constant time, but breaks the traditional predictability of a list (hence the
 * name "pool"). It is appropriate for cases where all objects in the collection are
 * equally valid choices, but where simple queueing behavior is not desirable.
 * <p>
 * For the moment, it operates as a constant capacity array, baking in functionality to
 * allow for array elements to be accessed only within the current number of active
 * elements in the pool.
 * @author Dave
 */
public class SingleDisplacementPool<T>
{
	// TODO - Re-implement as copy-on-write growable, with a small global lock?
	// Data members.
	private int fieldCapacity;
	private AtomicReferenceArray<T> fieldElements;
	private AtomicInteger fieldSize = new AtomicInteger( 0 );


	/**
	 * SingleDisplacementPool constructor.
	 */
	public SingleDisplacementPool( int capacity )
	{
		fieldCapacity = capacity;
		fieldElements = new AtomicReferenceArray<T>( capacity );

	} // SingleDisplacementPool


	public void add( T element )
	{

	} // add


	public void insert( int index, T element )
	{

	} // insert


	public T remove( int index )
	{
		return null;

	} // remove


	public T removeLast( )
	{
		return null;

	} // removeLast

}
