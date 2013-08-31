/*
 * FitnessThresholdStopCondition.java
 * 
 * Created on Jun 27, 2013
 * 
 */
package org.agal.impl;

import org.agal.core.AbstractFitnessEvaluator;
import org.agal.core.EvolutionListener;
import org.agal.core.StopCondition;

/**
 * FitnessThresholdStopCondition will stop evolution only once the population has found an
 * element which surpasses the given fitness threshold. (In other words, it waits for an
 * adequate solution to be found.)
 * @author Dave
 */
public class FitnessThresholdStopCondition<S> extends StopCondition implements EvolutionListener
{
	// Data members.
	private AbstractFitnessEvaluator<S> fieldFitnessEvaluator;
	private int fieldFitnessThreshold;


	/**
	 * FitnessThresholdStopCondition constructor.
	 */
	public FitnessThresholdStopCondition( AbstractFitnessEvaluator<S> fitnessEvaluator,
			int fitnessThreshold )
	{
		fieldFitnessEvaluator = fitnessEvaluator;
		fieldFitnessThreshold = fitnessThreshold;

	} // FitnessThresholdStopCondition


	@Override
	@SuppressWarnings( "unchecked" )
	public void onEvent( String eventId, Object eventObject )
	{
		if ( EvolutionListener.EVENT_ID_MEMBER_ADDED_TO_POPULATION.equals( eventId ) )
			{
			// LAM - Is it safe to assume the things listeners will listen to will always
			// use this properly? How can we class-verify this with only a type parameter?
			if ( fieldFitnessEvaluator.getFitnessComparator( ).compare(
					fieldFitnessEvaluator.fitness( ( S ) eventObject ), fieldFitnessThreshold ) >= 0 )
				stopEvolution( );
			}

	} // onEvent

}
