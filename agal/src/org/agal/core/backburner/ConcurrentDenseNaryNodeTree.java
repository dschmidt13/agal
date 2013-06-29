/*
 * ConcurrentDenseNaryNodeTree.java
 * 
 * Created on Nov 22, 2012
 * 
 */
package org.agal.core.backburner;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * ConcurrentDenseNaryNodeTree
 * @author Dave
 */
public class ConcurrentDenseNaryNodeTree<T>
{
	private class Node
	{
		private AtomicReference<T> fieldValue = new AtomicReference<>( );
		private volatile AtomicReferenceArray<Node> fieldChildren = null;
		private Node fieldParent = null;
		private AtomicInteger fieldSize = new AtomicInteger( 0 );


		private void add( T value )
		{
			fieldSize.incrementAndGet( );

			if ( !fieldValue.compareAndSet( null, value ) )
				{
				// Non-blockingly ensure the heap property.
				T currentValue = fieldValue.get( );
				while ( fieldComparator.compare( currentValue, value ) < 0 )
					{
					if ( fieldValue.compareAndSet( currentValue, value ) )
						{
						value = currentValue;
						break;
						}
					else
						{
						currentValue = fieldValue.get( );
						}
					}

				// Initialize children if necessary.
				if ( fieldChildren == null )
					{
					synchronized ( this )
						{
						if ( fieldChildren == null )
							{
							fieldChildren = new AtomicReferenceArray<>( fieldN );
							for ( int index = 0; index < fieldN; index++ )
								fieldChildren.set( index, new Node( ) );
							}
						}
					}

				// Add the value to the smallest child tree.
				Node smallestSubtree = fieldChildren.get( 0 );
				for ( int index = 1; index < fieldN; index++ )
					{
					Node subtree = fieldChildren.get( index );
					if ( subtree.fieldSize.get( ) < smallestSubtree.fieldSize.get( ) )
						smallestSubtree = subtree;
					}
				smallestSubtree.add( value );
				}

		} // add

	} // Node

	private Node fieldHead;
	private int fieldN;
	private Comparator<T> fieldComparator;


	public void prune( )
	{

	}

}
