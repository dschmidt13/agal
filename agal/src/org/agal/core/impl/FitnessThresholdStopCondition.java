/*
 * FitnessThresholdStopCondition.java
 * 
 * Created on Jun 27, 2013
 * 
 */
package org.agal.core.impl;

import java.util.concurrent.atomic.AtomicReference;

import org.agal.core.EvolutionListener;
import org.agal.core.StateManager;
import org.agal.core.StopCondition;

/**
 * FitnessThresholdStopCondition will stop evolution only once the population has found an
 * element which surpasses the given fitness threshold. (In other words, it waits for an
 * adequate solution to be found.)
 * @author Dave
 */
public class FitnessThresholdStopCondition<S> extends StopCondition
{
	// Data members.
	private StateManager<S> fieldStateManager;
	private double fieldFitnessThreshold;
	private AtomicReference<S> fieldCurrentBestSolution;


	/**
	 * FitnessThresholdStopCondition constructor.
	 */
	public FitnessThresholdStopCondition( StateManager<S> stateManager, double fitnessThreshold )
	{
		fieldStateManager = stateManager;
		fieldFitnessThreshold = fitnessThreshold;
		fieldCurrentBestSolution = new AtomicReference<>( );

	} // FitnessThresholdStopCondition


	public S getSolution( )
	{
		return fieldCurrentBestSolution.get( );

	} // getSolution


	@Override
	public void onEvent( int eventId, Object eventObject )
	{
		if ( eventId == EvolutionListener.EVENT_ID_MEMBER_ADDED_TO_POPULATION )
			{
			S newMember = ( S ) eventObject;
			S currentBest = fieldCurrentBestSolution.get( );
			while ( currentBest == null
					|| ( fieldStateManager.fitness( newMember ) > fieldStateManager
							.fitness( currentBest ) ) )
				if ( fieldCurrentBestSolution.compareAndSet( currentBest, newMember ) )
					break;
				else
					currentBest = fieldCurrentBestSolution.get( );

			if ( fieldStateManager.fitness( fieldCurrentBestSolution.get( ) ) >= fieldFitnessThreshold )
				stopEvolution( );
			}

	} // onEvent

}
