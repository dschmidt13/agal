/*
 * CensusWrapper.java
 * 
 * Created on Jul 9, 2013
 * 
 */
package org.agal.stats;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.agal.core.AbstractFitnessEvaluator;
import org.agal.core.Population;
import org.agal.impl.PopulationWrapper;

/**
 * CensusWrapper is a simple PopulationWrapper designed to integrate with a population to
 * track (in semi-real time) various statistics of a population. These may be used, for
 * example, for making real-time decisions about evolution bias (such as adjusting
 * mutation rates or selection thresholds), detecting evolutionary stagnation, or even
 * simple monitoring services.
 * <p>
 * The simplest way to obtain accurate statistics is to track them before initializing the
 * population via one of the {@code trackStatistic} methods. They will be incrementally
 * updated as the population is modified. Statistics tracking may be canceled via
 * {@code untrackStatistic}. An individual statistic's value may be obtained via
 * {@code getStatistic}; though it may vary with the {@code PopulationStatistic}
 * implementation in question, this object will generally represent an immutable snapshot
 * of that statistic for a very recent version of the population. (It is recommended that
 * {@code PopulationStatistic} implementations not attempt to update themselves in place,
 * but it is not disallowed.) If a one-time statistic is needed, it may be obtained and
 * calculated once without making use of the tracking mechanism by simply constructing the
 * {@code PopulationStatistic} responsible for its calculation.
 * <p>
 * The ability of this class to modify the statistic tracking list on a live population is
 * designed for use in applications such as interactive population monitoring and
 * instrumentation tools.
 * <p>
 * The "semi-real time" descriptor is used to qualify the nature of the class whereby its
 * tracked statistics are not updated atomically as a whole; they are updated
 * individually, and different statistics may be reporting on different populations at any
 * given time. This class provides no guarantees that reported statistics will represent
 * the most recent population version; but they will represent <u>a</u> very recent
 * population version. Therefore, over time they will all still report the general trend
 * of the population. (This result is a consequence of the class's non-blocking
 * copy-on-write concurrency mechanism.) If an atomic update is required to change several
 * statistics at once, it is recommended that all of them be incorporated into the same
 * {@code PopulationStatistic}. Note that as a consequence of the "looseness" of reporting
 * tracked statistics in a highly concurrent environment, clients requiring absolute
 * accuracy are encouraged to use a class which provides more concrete guarantees about
 * the quality, order, and freshness of information reported instead; this class will
 * generally perform well enough for most purposes, but provides no such guarantees.
 * @author David Schmidt
 * @see PopulationStatistic
 */
@SuppressWarnings( "unchecked" )
public class CensusWrapper<S> extends PopulationWrapper<S>
{
	/**
	 * StatWrapper is an internal class used for storing Statistics and their keys (class
	 * names) together in a sorted list.
	 */
	private static class StatWrapper<S>
	{
		final String fieldKey;
		final AtomicReference<PopulationStatistic<S>> fieldStat = new AtomicReference<>( null );


		public StatWrapper( String key, PopulationStatistic<S> statistic )
		{
			fieldKey = key;
			fieldStat.set( statistic );

		} // StatWrapper


		@Override
		public String toString( )
		{
			return fieldKey + " (" + fieldStat.get( ) + ")";

		} // toString

	} // StatWrapper

	// Data members.
	/*
	 * A FitnessEvaluator to use as a convenience for most PopulationStatistics
	 * implementations, since many statistics may be related to the fitness of the
	 * population.
	 */
	private final AbstractFitnessEvaluator<S> fieldFitnessEvaluator;

	/*
	 * A sorted list of wrappers whose state is guarded by "this" object's monitor lock
	 * (in synchronized methods). Sorting is maintained via binary search insertions and
	 * deletions. The copy on write mechanism allows the list to be iterated over during
	 * population modifications without blocking the addition or removal of statistics to
	 * be tracked. Previously tracked statistics who are in the process of becoming
	 * untracked will have their wrapper's atomic value set to null to indicate that the
	 * statistic is phasing out and the update mechanism need not update it.
	 */
	private final CopyOnWriteArrayList<StatWrapper<S>> fieldTrackedStatistics = new CopyOnWriteArrayList<>( );

	/*
	 * A map of the most recently calculated statistic values for constant-time lookup by
	 * getStatistic. Highly concurrent to permit highly concurrent population changes
	 * (which result in highly concurrent stat value updates).
	 */
	private final ConcurrentHashMap<String, PopulationStatistic<S>> fieldStatValues = new ConcurrentHashMap<>( );


	/**
	 * Creates a CensusWrapper to provide statistics tracking support on the given
	 * {@code Population<S>}.
	 * @param wrappedPopulationModel a {@code Population<S>} to wrap, whose statistics
	 *            will be monitored.
	 * @param fitnessEvaluator an {@code AbstractFitnessEvaluator<S>} to provide fitness
	 *            information, which is presumably one of the primary statistics to track.
	 */
	public CensusWrapper( Population<S> wrappedPopulationModel,
			AbstractFitnessEvaluator<S> fitnessEvaluator )
	{
		super( wrappedPopulationModel );

		// LAM - Replace with something more generic/a reference to the entire context?
		fieldFitnessEvaluator = fitnessEvaluator;

	} // CensusWrapper


	/**
	 * Uses the single-arg constructor of PopulationStatistic to construct an instance of
	 * the given subtype, with {@code this} wrapper as the arg. The subtype must have said
	 * single-arg constructor for this to work. If construction fails for any reason,
	 * {@code null} will be returned instead.
	 * <p>
	 * Used by convenience methods when automatically tracking statistics.
	 * @param statisticClass a {@code Class<T extends PopulationStatistic<S>>} to
	 *            instantiate.
	 * @return a new {@code PopulationStatistic} of the given {@code statisticClass},
	 *         whose population is this object.
	 * @throws Exception if any exceptions occur instantiating the Statistic.
	 */
	private <T extends PopulationStatistic<S>> T autoCreateStatistic( Class<T> statisticClass )
			throws Exception
	{
		// Look up the single-arg constructor specified by the PopulationStatistic class.
		Constructor<T> constructor = statisticClass.getConstructor( CensusWrapper.class );

		// Use it with this population wrapper to create the statistic.
		T statistic = constructor.newInstance( this );

		return statistic;

	} // autoCreateStatistic


	/**
	 * @return the {@code AbstractFitnessEvaluator<S>} to be used on states of type
	 *         {@code S}, which may be convenient to have for many fitness-based
	 *         statistics.
	 */
	protected AbstractFitnessEvaluator<S> getFitnessEvaluator( )
	{
		return fieldFitnessEvaluator;

	} // getFitnessEvaluator


	/**
	 * Retrieves the most recently calculated statistic of the given
	 * {@code statisticClass}, when that statistic class is being tracked. Note that this
	 * value may not necessarily reflect the statistic for the current population state,
	 * but it will reflect the statistic for a very recent valid population state.
	 * Furthermore, multiple simultaneous calls to {@code getStatistic} for different
	 * {@code statisticClasses} may return statistics calculated for different recent
	 * versions of the population. However, if the population is allowed a moment to
	 * quiesce, all statistics will be updated to the current version.
	 * <p>
	 * This operation runs in constant time.
	 * @param statisticClass a {@code Class<T extends PopulationStatistic>} of a
	 *            currently-tracked population statistic to look up.
	 * @return a {@code PopulationStatistic} of type {@code T} with a recently updated
	 *         version of the statistic for a very recent version of the population, or
	 *         {@code null} if the given {@code statisticClass} is not being tracked.
	 */
	public <T extends PopulationStatistic> T getStatistic( Class<T> statisticClass )
	{
		// Attempt to retrieve the statistic from the tracking map. If not present, don't
		// worry about it.
		String key = statisticClass.getName( );
		return ( T ) fieldStatValues.get( key );

	} // getStatistic


	/**
	 * Performs a binary search to find the insertion/lookup index of a given
	 * PopulationStatistic in the internal wrapper list. In case the synchronized tag
	 * wasn't a clue, access to this method usually coincides with structural
	 * modifications, and should therefore always be made from within a synchronized block
	 * (locking on this object's monitor lock). Accessing it outside a synchronized block
	 * may result in the list becoming unsorted (and therefore useless).
	 * @param key a String containing the fully qualified class name of the population
	 *            statistic to search for.
	 * @return an int indicating the index of the named StatWrapper in the list, or if
	 *         it's not in the list, the index of where it should be inserted into the
	 *         list. This method gives no indication of whether the wrapper is present or
	 *         not; external comparison must be done to determine that. When doing so,
	 *         clients should also ensure that the index returned is within the list
	 *         bounds; an out-of-bounds index will be equal to the list size and will
	 *         indicate that the element should be inserted at the end of the list.
	 */
	private synchronized int index( String key )
	{
		// Binary search for the index with the given class name.
		int top = fieldTrackedStatistics.size( ) - 1;
		int bot = 0;
		int index = top / 2;
		int compare;

		while ( bot <= top )
			{
			index = ( bot + top ) / 2;
			compare = fieldTrackedStatistics.get( index ).fieldKey.compareTo( key );
			if ( compare == 0 )
				return index;
			else if ( compare > 0 )
				bot = index + 1;
			else
				top = index - 1;
			}

		return bot;

	} // index


	@Override
	public S reap( )
	{
		S member = super.reap( );

		updateTrackedStatistics( member, false );

		return member;

	} // reap


	@Override
	public void sow( S member )
	{
		super.sow( member );

		updateTrackedStatistics( member, true );

	} // sow


	/**
	 * Automatically instantiates the {@code PopulationStatistic} class of type {@code T}
	 * via the single-argument
	 * {@link PopulationStatistic#PopulationStatistic(CensusWrapper)} constructor, which
	 * the implementation class must have defined, and then adds the statistic class to
	 * the tracking list via {@link #trackStatistic(PopulationStatistic)}.
	 * @param statisticClass a {@code Class<T extends PopulationStatistic<S>>} to
	 *            instantiate and begin tracking.
	 * @return the initial value of the {@code PopulationStatistic} being tracked, as a
	 *         convenience.
	 * @throws IllegalArgumentException if any exceptions occur during the instantiation
	 *             of the statistic, including (but not limited to) reflection problems
	 *             such as a missing constructor. This exception will have the original
	 *             exception as its cause.
	 */
	public <T extends PopulationStatistic<S>> T trackStatistic( Class<T> statisticClass )
			throws IllegalArgumentException
	{
		try
			{
			// Attempt to auto-create the statistic by class.
			T statistic = autoCreateStatistic( statisticClass );

			// Register for tracking and return the statistic.
			trackStatistic( statistic );
			return statistic;
			}
		catch ( Exception exception )
			{
			// LAM - Exception policy. Why am I using a RuntimeException for what's
			// probably a code/configuration problem?
			throw new IllegalArgumentException(
					"Couldn't auto-create a PopulationStatistic for class '"
							+ statisticClass.getName( ) + "'.", exception );
			}

	} // trackStatistic


	/**
	 * Adds the given {@code PopulationStatistic<S>} to the internal tracking list. Any
	 * structural modifications made to the population via {@code sow} or {@code reap}
	 * after the tracking list is modified will result in the statistic being updated with
	 * the new (or newly removed) population elements via
	 * {@link PopulationStatistic#memberAdded(Object)} and
	 * {@link PopulationStatistic#memberRemoved(Object)}. Upon successful return of
	 * {@code trackStatistic}, the {@code statistic} (or one of its successors) will also
	 * be available for lookup via {@link #getStatistic(Class)}.
	 * <p>
	 * PopulationStatistics are tracked effectively as singletons by class; multiple
	 * statistics of the same class are not permitted. It is intended that a given
	 * {@code PopulationStatistic} represents a discrete value or set of values calculated
	 * in a predictable way on a population.
	 * <p>
	 * Note that this will replace any previously-tracked PopulationStatistic of the same
	 * subclass. This is worth noting for any clients instantiating custom non-basic
	 * statistics, especially ones who do not replace themselves with new objects when
	 * updated.
	 * @param statistic a {@code PopulationStatistic<S>} to begin tracking. Must not be
	 *            {@code null}.
	 * @throws NullPointerException if the {@code statistic} is {@code null}.
	 */
	public synchronized <T extends PopulationStatistic<S>> void trackStatistic( T statistic )
			throws NullPointerException
	{
		if ( statistic == null )
			throw new NullPointerException( "Can't track a null statistic." );

		/*
		 * This method is guarded by the object's monitor lock. All changes to the
		 * statistics being tracked by this wrapper are likewise guarded.
		 */

		// Look up the wrapper (when one is present) and begin tracking it by setting its
		// reference to the new non-null value. If the wrapper isn't already present,
		// create one and insert it forcibly.
		String key = statistic.getClass( ).getName( );
		int index = index( key );
		if ( wrapperPresent( key, index ) )
			fieldTrackedStatistics.get( index ).fieldStat.set( statistic );
		else
			fieldTrackedStatistics.add( index, new StatWrapper<>( key, statistic ) );

		// Add the stat to the map for quick lookups.
		fieldStatValues.put( key, statistic );

	} // trackStatistic


	/**
	 * Removes a PopulationStatistic from the internal tracking list, preventing all
	 * future updates to it. However, the statistic will remain available in the value map
	 * via {@link #getStatistic(Class)}, so the last calculated value will always be
	 * accessible to clients. This should not affect performance. After tracking to a
	 * statistic stops, there may still be updates reported to it by threads which began
	 * iterating over the internal {@code CopyOnWriteArrayList} before the statistic
	 * became untracked. Once all such threads have completed their updates, the "final"
	 * version of the statistic, including all previous population updates, will be
	 * present for lookup.
	 * @param statisticClass a {@code Class<T extends PopulationStatistic<S>>} defining
	 *            the PopulationStatistic to stop tracking.
	 */
	public synchronized <T extends PopulationStatistic<S>> void untrackStatistic(
			Class<T> statisticClass )
	{
		/*
		 * This method is guarded by the object's monitor lock. All updates to the
		 * statistics being tracked by this wrapper are likewise guarded.
		 */

		// Attempt to look up the statistic.
		String key = statisticClass.getName( );
		int index = index( key );

		// When present, remove it from the tracking list.
		if ( wrapperPresent( key, index ) )
			fieldTrackedStatistics.remove( index );

	} // untrackStatistic


	/**
	 * A (probably) non-blocking algorithm for updating all PopulationStatistics being
	 * tracked by this population wrapper.
	 * @param member an {@code S} being added to or removed from the population.
	 * @param memberAdded a {@code boolean} indicating whether to inform tracked
	 *            PopulationStatistics that the {@code member} is being added.
	 */
	private void updateTrackedStatistics( S member, boolean memberAdded )
	{
		// Update each Statistic being tracked.
		for ( StatWrapper<S> wrapper : fieldTrackedStatistics )
			{
			PopulationStatistic<S> oldStat;
			PopulationStatistic<S> newStat = null;
			do
				{
				// Non-blocking stat update algorithm. Apparently we can't call clone
				// on another object, so we'll rely on the implementers to make the
				// decision to copy/clone.
				oldStat = wrapper.fieldStat.get( );
				newStat = ( memberAdded ) ? oldStat.memberAdded( member ) : oldStat
						.memberRemoved( member );
				} while ( !wrapper.fieldStat.compareAndSet( oldStat, newStat ) );

			// Update the stat map. If there was a race for that stat, we just bought some
			// time by winning that CAS, so hopefully the current thread will get its
			// object in the map first too. This is nowhere near a guarantee.
			if ( newStat != null )
				fieldStatValues.put( wrapper.fieldKey, newStat );
			else
				fieldStatValues.remove( wrapper.fieldKey );
			}

	} // updateTrackedStatistics


	/**
	 * Indicates whether the wrapper with the given {@code key} is present in the tracking
	 * list at the given {@code index}. While it would be possible for this method to
	 * calculate the index for itself using {@link #index(String)}, all clients of it
	 * require the index for their own use anyway, so this internal method provides no
	 * such convenience.
	 * <p>
	 * Note that this method should be called under {@code synchronized} conditions
	 * (locking on {@code this} object's monitor lock) to prevent the list from being
	 * modified and the calculated index from becoming stale while the check is performed.
	 * @param key a String containing the class name of a statistic whose wrapper to
	 *            check.
	 * @param index an int indicating the correct index of the wrapper with the given key
	 *            in the sorted tracking wrapper list.
	 * @return true iff the wrapper at the given index in the tracking list contains the
	 *         specified key; false otherwise.
	 */
	private boolean wrapperPresent( String key, int index )
	{
		return ( index < fieldTrackedStatistics.size( ) && fieldTrackedStatistics.get( index ).fieldKey
				.equals( key ) );

	} // wrapperPresent

}
