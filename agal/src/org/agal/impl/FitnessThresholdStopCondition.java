/*
 * FitnessThresholdStopCondition.java
 * 
 * Created on Jun 27, 2013
 * 
 */
package org.agal.impl;

import org.agal.core.EvolutionListener;
import org.agal.core.StateManager;
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
	private StateManager<S> fieldStateManager;
	private double fieldFitnessThreshold;


	/**
	 * FitnessThresholdStopCondition constructor.
	 */
	public FitnessThresholdStopCondition( StateManager<S> stateManager, double fitnessThreshold )
	{
		fieldStateManager = stateManager;
		fieldFitnessThreshold = fitnessThreshold;

	} // FitnessThresholdStopCondition


	@Override
	public void onEvent( int eventId, Object eventObject )
	{
		if ( eventId == EvolutionListener.EVENT_ID_MEMBER_ADDED_TO_POPULATION )
			{
			// LAM - Is it safe to assume the things listeners will listen to will always
			// use this properly? How can we class-verify this with only a type parameter?
			if ( fieldStateManager.fitness( ( S ) eventObject ) >= fieldFitnessThreshold )
				stopEvolution( );
			}

	} // onEvent

}
