/*
 * FitnessDrivenSelectionBias.java
 * 
 * Created on Jul 12, 2013
 * 
 */
package org.agal.xbackburner;

import org.agal.core.BiasSource;
import org.agal.stats.CensusWrapper;

/**
 * FitnessDrivenSelectionBias TODO
 * @author Dave
 */
@Deprecated
public class FitnessDrivenSelectionBias implements BiasSource
{
	// Data members.
	private CensusWrapper fieldStatsWrapper;
	private double fieldSigmasForward;


	/**
	 * FitnessDrivenSelectionBias constructor.
	 */
	public FitnessDrivenSelectionBias( CensusWrapper statsWrapper, double sigmasForward )
	{
		fieldStatsWrapper = statsWrapper;
		fieldSigmasForward = sigmasForward;

	} // FitnessDrivenSelectionBias


	@Override
	public double getBias( String biasKey )
	{
		// if ( biasKey.equals( BIAS_KEY_SELECTIVITY ) )
		// return ( fieldSigmasForward * fieldStatsWrapper.getFitnessStdDev( ) +
		// fieldStatsWrapper
		// .getFitnessMean( ) );
		// else
		return 0;

	} // getBias

}
