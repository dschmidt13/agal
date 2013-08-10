/*
 * ArrayGene.java
 * 
 * Created on Jul 24, 2013
 * 
 */
package org.agal.xbackburner;

/**
 * ArrayGene
 * @author Dave
 */
public abstract class ArrayGene<T> extends AbstractGene<T>
{
	// Data members.
	private T[ ] fieldArray;


	/**
	 * ArrayGene constructor.
	 * @param cacheFitness
	 */
	@SuppressWarnings( "unchecked" )
	public ArrayGene( int size, boolean cacheFitness )
	{
		super( cacheFitness );

		// As of Java 1.7, parameterized types provide only compile-time type checking,
		// but not runtime type checking. So this technically works out to be legal. The
		// rest of the accesses to this private data member will be type checked, ensuring
		// only T's are added to (and removed from) the array.
		fieldArray = ( T[ ] ) new Object[ size ];
		for ( int index = 0; index < size; index++ )
			fieldArray[ index ] = generateAllele( index );

	} // ArrayGene


	// TODO
	protected abstract T generateAllele( int index );

}
