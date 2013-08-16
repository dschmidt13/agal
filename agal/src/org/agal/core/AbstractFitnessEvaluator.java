/*
 * AbstractFitnessEvaluator.java
 * 
 * Created on Jul 24, 2013
 * 
 */
package org.agal.core;

import java.util.Comparator;

/**
 * AbstractFitnessEvaluator is responsible for calculating and comparing fitness values of
 * states. Fitnesses are measured by {@code int} values for processing efficiency. If
 * fractional values are desired, a fixed point attachment such as
 * {@code FixedPointFitnessEvaluator} may be of use.
 * <p>
 * Concrete subclasses need only implement {@link #fitness(Object)} in order to have a
 * fully operational fitness evaluator. They may optionally override
 * {@link #fitness(Object[])} (the bulk version) if they wish to leverage some parallel
 * computing framework for expedited fitness calculations. The rest of the base
 * functionality should be adequate as-is.
 * @author David Schmidt
 */
public abstract class AbstractFitnessEvaluator<S> implements Comparator<S>
{
	// LAM - Should this allow custom fitness types instead of forcing Integers?

	// Data members.
	private final boolean fieldInverseFitness;
	private final Comparator<Integer> fieldFitnessComparator;


	/**
	 * Constructs an AbstractFitnessEvaluator.
	 * @param inverseFitness a boolean indicating the direction of the fitness scale;
	 *            {@code false} uses the default type of scale, where higher fitness
	 *            values are more favorable, while {@code true} will invert it and assume
	 *            lower values are more favorable.
	 */
	public AbstractFitnessEvaluator( boolean inverseFitness )
	{
		fieldInverseFitness = inverseFitness;

		fieldFitnessComparator = new Comparator<Integer>( )
		{

			@Override
			public int compare( Integer first, Integer second )
			{
				if ( fieldInverseFitness )
					{
					Integer swap = first;
					first = second;
					second = swap;
					}

				return first.compareTo( second );

			} // compare

		}; // fieldFitnessComparator

	} // AbstractFitnessEvaluator


	/**
	 * Indicates whether the {@code first} fitness value is considered "more fit" than the
	 * {@code second} fitness value.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare( S first, S second )
	{
		Integer firstFitness = fitness( first );
		Integer secondFitness = fitness( second );

		return fieldFitnessComparator.compare( firstFitness, secondFitness );

	} // compare


	/**
	 * Calculates the "fitness" of a state. Fitness values are generally only relevant
	 * within the context of the search being performed, primarily as a way of objectively
	 * comparing the evolutionary utility of two states.
	 * @param state an {@code S} whose fitness is to be evaluated.
	 * @return an {@code int} indicating the fitness of the given {@code state}.
	 */
	public abstract Integer fitness( S state );


	/**
	 * A bulk fitness operation. By default, it will simply iterate over the
	 * {@code states} array and call {@code fitness} for each one, generating a result in
	 * the returned fitness array at the respective index. However, its true intent is to
	 * be overridden to allow the use of programmable video hardware or other
	 * parallelization techniques to calculate many fitness values in parallel to
	 * dramatically accelerate the evolution.
	 * <p>
	 * Be aware that not all Populations and EvolutionAlgorithms make use of bulk fitness
	 * operations. If an implementation does make use of it, it should be noted in the
	 * documentation.
	 * @param states an {@code S[ ]} whose fitness values are to be evaluated.
	 * @return an {@code int[ ]} of the same length as {@code states} and containing the
	 *         fitness values of each state at the same array index as that state.
	 */
	public Integer[ ] fitness( S[ ] states )
	{
		Integer[ ] fitnesses = new Integer[ states.length ];

		for ( int index = 0; index < states.length; index++ )
			fitnesses[ index ] = fitness( states[ index ] );

		return fitnesses;

	} // fitness


	/**
	 * @return a {@code Comparator} which may be used to compare Fitness values according
	 *         to the fitness scale of this fitness evaluator.
	 */
	public Comparator<Integer> getFitnessComparator( )
	{
		return fieldFitnessComparator;

	} // getFitnessComparator

}
