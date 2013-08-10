/*
 * AbstractFitnessEvaluator.java
 * 
 * Created on Jul 24, 2013
 * 
 */
package org.agal.xbackburner;

import java.util.Comparator;


/**
 * AbstractFitnessEvaluator is responsible for determining calculation and ordering of
 * fitness values for genes.
 * @author David Schmidt
 */
public abstract class AbstractFitnessEvaluator<G extends Gene, F> implements Comparator<F>
{
	// Data members.
	private final boolean fieldInverseFitness;
	private final F fieldFitnessLimit;


	/**
	 * AbstractFitnessEvaluator constructor.
	 */
	public AbstractFitnessEvaluator( F fitnessLimit, boolean inverseFitness )
	{
		fieldFitnessLimit = fitnessLimit;
		fieldInverseFitness = inverseFitness;

	} // AbstractFitnessEvaluator


	/**
	 * Indicates whether the {@code first} fitness value is considered "more fit" than the
	 * {@code second} fitness value.
	 * <p>
	 * The default implementation can handle all fitness types implementing
	 * {@code Comparable}, which should handle 99% of cases. Custom fitness models will
	 * require custom {@code compare} implementations.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * @throws IllegalArgumentException
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public int compare( F first, F second )
			throws IllegalArgumentException
	{
		if ( Comparable.class.isInstance( first ) && Comparable.class.isInstance( second ) )
			return ( ( Comparable ) first ).compareTo( second );

		throw new IllegalArgumentException(
				"Default implementation can't compare non-Comparable fitness types." );

	} // compare


	/**
	 * TODO
	 * @param gene
	 * @return
	 */
	public abstract F fitness( G gene );

}
