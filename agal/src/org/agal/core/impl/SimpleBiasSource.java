/*
 * SimpleBiasSource.java
 * 
 * Created on Jun 29, 2013
 * 
 */
package org.agal.core.impl;

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
	public double getBias( int biasCode )
	{
		switch ( biasCode )
			{
			case BiasSource.BIAS_CODE_SELECTIVITY :
				return fieldSelectionBias;

			case BiasSource.BIAS_CODE_MUTATION_PROBABILITY :
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
