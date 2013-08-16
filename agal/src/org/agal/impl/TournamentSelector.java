/*
 * TournamentSelector.java
 * 
 * Created on Aug 10, 2013
 * 
 */
package org.agal.impl;

import java.util.List;

import org.agal.core.AbstractFitnessEvaluator;
import org.agal.core.Population;
import org.agal.core.Selector;

/**
 * TournamentSelector
 * @author Dave
 */
public class TournamentSelector<S> implements Selector<S>
{
	// Data members.
	private final AbstractFitnessEvaluator<S> fieldFitnessEvaluator;


	/**
	 * TournamentSelector constructor.
	 */
	public TournamentSelector( AbstractFitnessEvaluator<S> fitnessEvaluator )
	{
		fieldFitnessEvaluator = fitnessEvaluator;

	} // TournamentSelector


	@Override
	public void selectParents( Population<S> population, List<S> parents )
	{
		// Select two parents.
		for ( int index = 0; index < 2; index++ )
			{
			// Obtain two individuals from the population.
			S first = population.sample( );
			S second = population.sample( );

			// With arbitrary bias toward the first, compare fitnesses and choose a
			// winner and add it as a parent.
			if ( fieldFitnessEvaluator.compare( first, second ) >= 0 )
				parents.add( first );
			else
				parents.add( second );
			}

	} // selectParents

}
