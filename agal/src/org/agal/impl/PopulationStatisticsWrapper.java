/*
 * PopulationStatisticsWrapper.java
 * 
 * Created on Jul 9, 2013
 * 
 */
package org.agal.impl;

import java.util.concurrent.atomic.AtomicReference;

import org.agal.core.AbstractFitnessEvaluator;
import org.agal.core.Population;

/**
 * PopulationStatisticsWrapper is a simple PopulationWrapper designed to integrate with a
 * population to track (in semi-real time) various statistics of a population. These may
 * be used, for example, for making real-time decisions about evolution bias (such as
 * adjusting mutation rates or selection thresholds), detecting evolutionary stagnation,
 * or even simple monitoring services.
 * <p>
 * This implementation uses a copy-on-write mechanism of thread safety to ensure
 * statistics are as accurate as possible while preserving their integrity. However, as it
 * is (currently) lacking a robust external object for storing statistics, <i>values may
 * be updated between calls to retrieve them, so two subsequent calls to the same or
 * different methods may not be reporting on the same population.</i> (Perhaps later, the
 * underlying object will be exposed, and a complete snapshot of a population's statistics
 * may be observed at the caller's leisure.)
 * @author Dave
 */
public class PopulationStatisticsWrapper<S> extends PopulationWrapper<S>
{
	// LAM - If this mechanism of copy on write catches on for other non-stats uses, or
	// people want to implement their own stats suppliers, the wrapper itself could be
	// genericized, or at least this inner class can be replaced with a statistics
	// object/interface that knows how to update its values and return *another object*.
	private class CrudeStatsContainer implements Cloneable
	{
		private int fieldCount = 0;
		private double fieldFitnessPreMeanSum = 0;
		private double fieldFitnessArithMean = 0;
		private double fieldFitnessSquareVarianceSum = 0;
		private double fieldFitnessStdDev = 0;


		@SuppressWarnings( "unchecked" )
		@Override
		public CrudeStatsContainer clone( )
				throws CloneNotSupportedException
		{
			return ( CrudeStatsContainer ) super.clone( );

		} // clone

	} // CrudeStatsContainer

	// Data members.
	private AtomicReference<CrudeStatsContainer> fieldStats = new AtomicReference<>( );
	private AbstractFitnessEvaluator<S> fieldFitnessEvaluator;


	public PopulationStatisticsWrapper( Population<S> wrappedPopulationModel,
			AbstractFitnessEvaluator<S> fitnessEvaluator )
	{
		super( wrappedPopulationModel );

		fieldFitnessEvaluator = fitnessEvaluator;

		// Set an initial stats object to obviate null checks.
		fieldStats.set( new CrudeStatsContainer( ) );

	} // PopulationStatisticsWrapper


	/**
	 * @return a double containing the current arithmetic mean of the population's fitness
	 *         values. This value is NOT guaranteed to be in sync with other returned
	 *         statistics from call to call.
	 */
	public double getFitnessMean( )
	{
		return fieldStats.get( ).fieldFitnessArithMean;

	} // getFitnessMean


	/**
	 * @return a double which is the current standard deviation of the population's
	 *         fitness values. This value is NOT guaranteed to be in sync with other
	 *         returned statistics values from call to call.
	 */
	public double getFitnessStdDev( )
	{
		return fieldStats.get( ).fieldFitnessStdDev;

	} // getFitnessStdDev


	@Override
	public S reap( )
	{
		S element = super.reap( );

		double fitness = fieldFitnessEvaluator.fitness( element );

		// LAM - null check?
		while ( true )
			{
			CrudeStatsContainer oldStats = fieldStats.get( );
			CrudeStatsContainer newStats = null;
			try
				{
				newStats = ( CrudeStatsContainer ) oldStats.clone( );
				}
			catch ( CloneNotSupportedException exception )
				{
				// Shouldn't be possible.
				}

			newStats.fieldCount--;
			if ( newStats.fieldCount <= 0 )
				newStats = new CrudeStatsContainer( );
			else
				{
				newStats.fieldFitnessPreMeanSum -= fitness;
				newStats.fieldFitnessArithMean = newStats.fieldFitnessPreMeanSum
						/ ( double ) newStats.fieldCount;
				newStats.fieldFitnessSquareVarianceSum -= StrictMath.pow(
						( newStats.fieldFitnessArithMean - fitness ), 2.0 );
				newStats.fieldFitnessStdDev = StrictMath.pow(
						( newStats.fieldFitnessSquareVarianceSum / newStats.fieldCount ), 0.5 );
				}

			if ( fieldStats.compareAndSet( oldStats, newStats ) )
				break;
			}

		return element;

	} // reap


	@Override
	public void sow( S member )
	{
		double fitness = fieldFitnessEvaluator.fitness( member );

		while ( true )
			{
			CrudeStatsContainer oldStats = fieldStats.get( );
			CrudeStatsContainer newStats = null;
			try
				{
				newStats = ( CrudeStatsContainer ) oldStats.clone( );
				}
			catch ( CloneNotSupportedException exception )
				{
				// Shouldn't be possible.
				}

			newStats.fieldCount++;
			newStats.fieldFitnessPreMeanSum += fitness;
			newStats.fieldFitnessArithMean = newStats.fieldFitnessPreMeanSum
					/ ( double ) newStats.fieldCount;
			newStats.fieldFitnessSquareVarianceSum += StrictMath.pow(
					( newStats.fieldFitnessArithMean - fitness ), 2.0 );
			newStats.fieldFitnessStdDev = StrictMath.pow(
					( newStats.fieldFitnessSquareVarianceSum / ( double ) newStats.fieldCount ),
					0.5 );

			if ( fieldStats.compareAndSet( oldStats, newStats ) )
				break;
			}

		super.sow( member );

	} // sow

}
