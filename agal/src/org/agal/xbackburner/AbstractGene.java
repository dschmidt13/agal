/*
 * AbstractGene.java
 * 
 * Created on Jul 24, 2013
 * 
 */
package org.agal.xbackburner;

/**
 * AbstractGene
 * @author David Schmidt
 */
public abstract class AbstractGene<F> implements Gene
{
	// Data members.
	private final boolean fieldCacheFitness;
	private volatile F fieldFitness;


	/**
	 * AbstractGene constructor.
	 * @param cacheFitness
	 */
	public AbstractGene( boolean cacheFitness )
	{
		fieldCacheFitness = cacheFitness;

	} // AbstractGene


	public F getFitness( )
	{
		return ( fieldCacheFitness ) ? fieldFitness : null;

	} // getFitness


	public synchronized void setFitness( F fitness )
	{
		fieldFitness = fitness;

	} // setFitness

}
