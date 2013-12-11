/*
 * PopulationStatistic.java
 * 
 * Created on Nov 27, 2013
 * 
 */
package org.agal.stats;

/**
 * A PopulationStatistic is an object used to track summarizing data about a population.
 * Once one is added to or requested from a {@link CensusWrapper}, it will automatically
 * become tracked with individual changes to the population.
 * <p>
 * <h2>Thread Safety</h2> Due to the high concurrency requirements of populations in
 * multi-threaded environments, thread safety is usually handled by the wrapper via a
 * copy-on-write mechanism; the simplest way to take advantage of this is for subclasses
 * to return a copy or {@code clone} with updated information from {@code memberAdded} and
 * {@code memberRemoved}. Authors may prefer to update the object in place and return
 * itself, but this will push the thread-safety responsibility onto those authors, as well
 * as potentially creating a bottleneck for concurrent population updates. (The
 * {@code clone} or copy mechanism is non-blocking when used in a {@code CensusWrapper}.
 * @author David Schmidt
 * @see CensusWrapper
 */
public abstract class PopulationStatistic<S>
{
	// Data members.
	private final CensusWrapper<S> fieldPopulation;
	private final long fieldTimestamp = System.currentTimeMillis( );


	/**
	 * PopulationStatistic constructor.
	 */
	public PopulationStatistic( CensusWrapper<S> population )
	{
		fieldPopulation = population;

	} // PopulationStatistic


	protected CensusWrapper<S> getPopulation( )
	{
		return fieldPopulation;

	} // getPopulation


	/**
	 * Retrieves the timestamp of this PopulationStatistic, which is by default the time
	 * at which it was instantiated. The timestamp does NOT play a role in versioning or
	 * concurrency; it is simply intended as a convenience for tracking statistics over
	 * time.
	 * @return a {@code long} indicating, by default, the time at which the statistic was
	 *         created. This will suffice for any implementation using a replacement
	 *         policy rather than an in-place update policy.
	 */
	public long getTimestamp( )
	{
		return fieldTimestamp;

	} // getTimestamp


	protected abstract PopulationStatistic<S> memberAdded( S member );


	protected abstract PopulationStatistic<S> memberRemoved( S member );

}
