/*
 * IntArrayGene.java
 * 
 * Created on Jul 25, 2013
 * 
 */
package org.agal.xbackburner;

/**
 * IntArrayGene is a more performant implementation of {@code ArrayGene} using hardcoded
 * primitive {@code int} arrays.
 * @author David Schmidt
 */
public class IntArrayGene<F> extends AbstractGene<F>
{
	// Data members.
	private int[ ] fieldArray;
	private int fieldAlleleRange;


	/**
	 * IntArrayGene constructor.
	 * @param cacheFitness
	 */
	public IntArrayGene( boolean cacheFitness, int size, int alleleRange )
	{
		super( cacheFitness );

		fieldArray = new int[ size ];

		// LAM - or something else??
		fieldAlleleRange = alleleRange;

	} // IntArrayGene

	// TODO

}
