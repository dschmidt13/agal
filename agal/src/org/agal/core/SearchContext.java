/*
 * SearchContext.java
 * 
 * Created on Jul 29, 2013
 * 
 */
package org.agal.core;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SearchContext provides a single unified lookup facility throughout the Genetic search.
 * In simple cases, it may be used by library or client code as a map for storing and
 * retrieving arbitrary key/value pairs. More commonly, it is used for communicating both
 * static and on-the-fly configuration or search details between library and client code,
 * looking up client-specified random number generators, and so on.
 * <p>
 * TODO - make this true..somehow? SearchContexts are not typically created by the client,
 * but by the SearchConfiguration when it is initialized, or by search-splitting features.
 * <p>
 * The {@code SearchContext} is thread safe.
 * @author David Schmidt
 */
public class SearchContext<S>
{
	// Data members.
	private final ConcurrentHashMap<String, Object> fieldContextMap = new ConcurrentHashMap<>( );
	private final EvolutionConfiguration fieldConfiguration;
	private final StateManager<S> fieldStateManager;
	private final Population<S> fieldPopulation;
	private final BiasSource fieldBiasSource;
	private final RandomSource fieldRandomSource;
	private final AbstractFitnessEvaluator<S> fieldFitnessEvaluator;
	private final AtomicReference<S> fieldBestResult = new AtomicReference<>( null );


	protected SearchContext( EvolutionConfiguration configuration,
			AbstractFitnessEvaluator<S> fitnessEvaluator, StateManager<S> stateManager,
			Population<S> population, BiasSource biasSource, RandomSource randomSource )
	{
		fieldConfiguration = configuration;
		fieldFitnessEvaluator = fitnessEvaluator;
		fieldStateManager = stateManager;
		fieldPopulation = population;
		fieldBiasSource = biasSource;
		fieldRandomSource = randomSource;

	} // SearchContext


	public S getBestResult( )
	{
		return fieldBestResult.get( );

	} // getBestResult


	public double getBias( String biasKey )
	{
		return fieldBiasSource.getBias( biasKey );

	} // getBias


	public EvolutionConfiguration getConfiguration( )
	{
		return fieldConfiguration;

	} // getConfiguration


	public Map<String, Object> getContextMap( )
	{
		return fieldContextMap;

	} // getContextMap


	public AbstractFitnessEvaluator<S> getFitnessEvaluator( )
	{
		return fieldFitnessEvaluator;

	} // getFitnessEvaluator


	public Population<S> getPopulation( )
	{
		return fieldPopulation;

	} // Population


	public Random getRandom( )
	{
		return fieldRandomSource.getRandom( );

	} // getRandom


	public StateManager<S> getStateManager( )
	{
		return fieldStateManager;

	} // getStateManager


	protected void tryUpdateBestResult( S candidate )
	{
		// FIXME - This interface puts the work of comparing results on the SearchContext,
		// which is inappropriate. However, exposing the atomic CAS interface and
		// requiring clients to use it properly would be both inappropriate and dangerous.
		// A new solution for sifting and tracking results through the SearchContext must
		// be created... but this will do for many cases until then.
		S current = fieldBestResult.get( );
		while ( current == null || ( fieldFitnessEvaluator.compare( candidate, current ) > 0 ) )
			{
			if ( fieldBestResult.compareAndSet( current, candidate ) )
				break;
			else
				current = fieldBestResult.get( );
			}

	} // updateBestResult

}
