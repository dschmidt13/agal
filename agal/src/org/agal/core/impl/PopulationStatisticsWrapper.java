/*
 * PopulationStatisticsWrapper.java
 * 
 * Created on Jul 9, 2013
 * 
 */
package org.agal.core.impl;

import java.util.concurrent.atomic.AtomicReference;

import org.agal.core.PopulationModel;
import org.agal.core.PopulationModelWrapper;
import org.agal.core.StateManager;

/**
 * PopulationStatisticsWrapper is a simple PopulationModelWrapper designed to integrate
 * with a population to track (in semi-real time) various statistics of a population.
 * These may be used, for example, for making real-time decisions about evolution bias
 * (such as adjusting mutation rates or selection thresholds), detecting evolutionary
 * stagnation, or even simple monitoring services.
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
public class PopulationStatisticsWrapper<S> extends PopulationModelWrapper<S>
{
	// LAM - If this mechanism of copy on write catches on for other non-stats uses, or
	// people want to implement their own stats suppliers, the wrapper itself could be
	// genericized, or at least this inner class can be replaced with a statistics
	// object/interface that knows how to update its values and return *another object*.
	private class CrudeStatsContainer
	{
		private double fieldFitnessStdDev;
		private double fieldFitnessMean;

	} // CrudeStatsContainer

	// Data members.
	private AtomicReference<CrudeStatsContainer> fieldStats = new AtomicReference<>( );
	private StateManager<S> fieldStateManager;


	public PopulationStatisticsWrapper( PopulationModel<S> wrappedPopulationModel,
			StateManager<S> stateManager )
	{
		super( wrappedPopulationModel );

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
		return fieldStats.get( ).fieldFitnessMean;

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

}
