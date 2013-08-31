/*
 * FitnessDrivenSelectionBias.java
 * 
 * Created on Jul 12, 2013
 * 
 */
package org.agal.xbackburner;

import org.agal.core.BiasSource;
import org.agal.impl.PopulationStatisticsWrapper;

/**
 * FitnessDrivenSelectionBias TODO
 * @author Dave
 */
public class FitnessDrivenSelectionBias implements BiasSource
{
	// Data members.
	private PopulationStatisticsWrapper fieldStatsWrapper;
	private double fieldSigmasForward;


	/**
	 * FitnessDrivenSelectionBias constructor.
	 */
	public FitnessDrivenSelectionBias( PopulationStatisticsWrapper statsWrapper,
			double sigmasForward )
	{
		fieldStatsWrapper = statsWrapper;
		fieldSigmasForward = sigmasForward;

	} // FitnessDrivenSelectionBias


	@Override
	public double getBias( String biasKey )
	{
		if ( biasKey.equals( BIAS_KEY_SELECTIVITY ) )
			return ( fieldSigmasForward * fieldStatsWrapper.getFitnessStdDev( ) + fieldStatsWrapper
					.getFitnessMean( ) );
		else
			return 0;

	} // getBias

}
