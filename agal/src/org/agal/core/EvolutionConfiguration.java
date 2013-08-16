/*
 * EvolutionConfiguration.java
 * 
 * Created on Aug 9, 2013
 * 
 */
package org.agal.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.agal.impl.CompoundBiasSource;

/**
 * EvolutionConfiguration is designed to handle the details of instantiating and hooking
 * together an evolutionary search, providing a simple all-inclusive way for clients to
 * set up their evolution without the burden of intricate boilerplate code. Instead,
 * they'll create a configuration (in a similar way to configuring EHCache), which
 * simplifies the boilerplate code to defining a few consolidated and well-documented
 * choices. To further streamline configuration, modified "builder" style setters are
 * supplied.
 * @author David Schmidt
 */
public class EvolutionConfiguration<S>
{
	// TODO - To Hell with all this mess! Let's just use dependency injection (via Guice).

	// Data members.
	private Map<Class, Class> fieldClassMap = new HashMap<>( );
	private int fieldPopulationSize;

	// LAM - Sorta duplicates the CompoundBiasSource API...
	// LAM - Do we even need bias sources anymore?
	private Map<String, BiasSource> fieldBiasSources = new HashMap<>( );
	private BiasSource fieldDefaultBiasSource;

	private List<EvolutionListener> fieldListeners = new ArrayList<>( );

	private AbstractFitnessEvaluator<S> fieldFitnessEvaluator;
	private StateManager<S> fieldStateManager;


	/**
	 * EvolutionConfiguration constructor.
	 */
	public EvolutionConfiguration( )
	{
		// TODO - A constructor with a java.util.Properties argument might be useful to
		// quickly populate the class mappings, but for less obvious choices we'd still
		// need constants.

	} // EvolutionConfiguration


	public EvolutionConfiguration addBiasSource( String biasKey, BiasSource biasSource )
	{
		fieldBiasSources.put( biasKey, biasSource );
		return this;

	} // addBiasSource


	public EvolutionConfiguration addListener( EvolutionListener listener )
	{
		fieldListeners.add( listener );
		return this;

	} // addListenerClass


	@SuppressWarnings( "unchecked" )
	protected EvolutionAlgorithm createAlgorithm( SearchContext searchContext )
			throws Exception
	{
		// LAM - Is this really the right place for this method?
		// FIXME - Exception policy?

		// Instantiate the requested Selector.
		Selector<S> selector;
			{
			Class<? extends Selector> selectorClass = fieldClassMap.get( Selector.class );
			Constructor<? extends Selector> constructor = selectorClass
					.getConstructor( AbstractFitnessEvaluator.class );
			selector = constructor.newInstance( fieldFitnessEvaluator );
			}

		// Create an instance of the requested algorithm.
		EvolutionAlgorithm algorithm;
			{
			Class<? extends EvolutionAlgorithm> algorithmClass = fieldClassMap
					.get( EvolutionAlgorithm.class );
			Constructor<? extends EvolutionAlgorithm> constructor = algorithmClass.getConstructor(
					SearchContext.class, Selector.class );
			algorithm = constructor.newInstance( searchContext, selector );
			}

		// Register all the requested listeners.
		for ( EvolutionListener listener : fieldListeners )
			algorithm.registerListener( listener );

		// TODO - Better solution listener configuration.
		// Register a default "solution" listener.
		DefaultSolutionListener solutionListener = new DefaultSolutionListener<>( searchContext );
		algorithm.registerListener( solutionListener );

		return algorithm;

	} // createAlgorithm


	@SuppressWarnings( "unchecked" )
	public SearchContext<S> initialize( )
			throws Exception
	{
		// LAM - Exception policy

		// TODO - Default values...

		// Merge the bias sources.
		CompoundBiasSource biasSource = new CompoundBiasSource( fieldDefaultBiasSource );
		for ( Entry<String, BiasSource> entry : fieldBiasSources.entrySet( ) )
			biasSource.setBiasSource( entry.getKey( ), entry.getValue( ) );

		// Instantiate the RandomSource.
		RandomSource randomSource;
			{
			Class<? extends RandomSource> randomSourceClass = fieldClassMap
					.get( RandomSource.class );
			Class<? extends Random> randomClass = fieldClassMap.get( Random.class );
			Constructor<? extends RandomSource> constructor = randomSourceClass
					.getConstructor( Class.class );
			randomSource = constructor.newInstance( randomClass );
			}

		// Create and initialize the population.
		Population population;
			{
			Class<? extends Population> populationClass = fieldClassMap.get( Population.class );
			population = populationClass.newInstance( );
			population.initialize( fieldStateManager, fieldPopulationSize );
			}

		// TODO - Population wrapper support. (Wrapper needs to support access to wrapped
		// population.)

		// Finally, create the SearchContext, the ultimate wrapper for all this crap.
		SearchContext<S> searchContext = new SearchContext( this, fieldFitnessEvaluator,
				fieldStateManager, population, biasSource, randomSource );

		return searchContext;

	} // initialize


	public EvolutionConfiguration setAlgorithmClass(
			Class<? extends EvolutionAlgorithm> algorithmClass )
	{
		fieldClassMap.put( EvolutionAlgorithm.class, algorithmClass );
		return this;

	} // setAlgorithmClass


	public EvolutionConfiguration setDefaultBiasSource( BiasSource defaultBiasSource )
	{
		fieldDefaultBiasSource = defaultBiasSource;
		return this;

	} // setDefaultBiasSource


	public EvolutionConfiguration setFitnessEvaluator( AbstractFitnessEvaluator<S> fitnessEvaluator )
	{
		fieldFitnessEvaluator = fitnessEvaluator;
		return this;

	} // setFitnessEvaluator


	public EvolutionConfiguration setPopulationClass( Class<? extends Population> populationClass )
	{
		fieldClassMap.put( Population.class, populationClass );
		return this;

	} // setPopulationClass


	public EvolutionConfiguration setPopulationSize( int size )
	{
		fieldPopulationSize = size;
		return this;

	} // setPopulationSize


	public EvolutionConfiguration setRandomClass( Class<? extends Random> randomClass )
	{
		fieldClassMap.put( Random.class, randomClass );
		return this;

	} // setRandomClass


	public EvolutionConfiguration setRandomSourceClass(
			Class<? extends RandomSource> randomSourceClass )
	{
		fieldClassMap.put( RandomSource.class, randomSourceClass );
		return this;

	} // setRandomSourceClass


	public EvolutionConfiguration setSelectorClass( Class<? extends Selector> selectorClass )
	{
		fieldClassMap.put( Selector.class, selectorClass );
		return this;

	} // setSelectorClass


	public EvolutionConfiguration setStateManager( StateManager<S> stateManager )
	{
		fieldStateManager = stateManager;
		return this;

	} // setStateManager

}
