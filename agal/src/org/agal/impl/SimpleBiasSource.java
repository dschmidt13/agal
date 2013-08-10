/*
 * SimpleBiasSource.java
 * 
 * Created on Jun 29, 2013
 * 
 */
package org.agal.impl;

import org.agal.core.BiasSource;
import org.agal.core.EvolutionListener;

/**
 * SimpleBiasSource
 * @author Dave
 */
public class SimpleBiasSource implements BiasSource, EvolutionListener
{
	// Data members.
	private double fieldMutationRate;
	private double fieldSelectionBiasPerGeneration;
	private double fieldSelectionBias;


	/**
	 * SimpleBiasSource constructor.
	 */
	public SimpleBiasSource( double mutationRate, double selectionBiasPerGeneration )
	{
		fieldMutationRate = mutationRate;
		fieldSelectionBiasPerGeneration = selectionBiasPerGeneration;
		fieldSelectionBias = 0;

	} // SimpleBiasSource


	@Override
	public double getBias( String biasKey )
	{
		switch ( biasKey )
			{
			case BiasSource.BIAS_KEY_SELECTIVITY :
				return fieldSelectionBias;

			case BiasSource.BIAS_KEY_MUTATION_RATE :
				return fieldMutationRate;
			}

		return 0;

	} // getBias


	@Override
	public void onEvent( int eventId, Object eventObject )
	{
		if ( eventId == EvolutionListener.EVENT_ID_NEW_GENERATION )
			{
			fieldSelectionBias += fieldSelectionBiasPerGeneration;
			if ( fieldSelectionBias >= 1 )
				fieldSelectionBias = 0;
			}

	} // onEvent

}
