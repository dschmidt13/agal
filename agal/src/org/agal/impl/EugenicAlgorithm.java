/*
 * EugenicAlgorithm.java
 * 
 * Created on Jul 7, 2012
 * 
 */
package org.agal.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.agal.core.BiasSource;
import org.agal.core.EvolutionAlgorithm;
import org.agal.core.EvolutionListener;
import org.agal.core.Population;
import org.agal.core.SearchContext;
import org.agal.core.Selector;
import org.agal.core.StateManager;

/**
 * EugenicAlgorithm is a custom "Genetic Algorithm" implementation leveraging a fully
 * modular framework designed to allow for a maximum degree of experimentation while
 * achieving a high degree of performance.
 * <p>
 * The name "Eugenic" is one that I feel more accurately describes the class of algorithms
 * deemed by modern computer scientists to be "Genetic" algorithms. The field of Eugenics
 * "advocates the use of practices aimed at improving the genetic composition of a
 * population." (Wikipedia) One of the most intuitive and obvious sources of inspiration
 * for Eugenics was the selective breeding of dogs as a form of artificial evolution. As
 * that technique is conceptually at the core of the Genetic Algorithm, and as the
 * techniques permitted by this framework are not necessarily strictly genetic in modern
 * terms, it makes far more sense to use the term Eugenics. Perhaps it will catch on in
 * spite of its negative connotations, and in time even rebrand the term as having more to
 * do with obscure computer science and less to do with the holocaust.
 * @author David Schmidt
 */
public class EugenicAlgorithm<S> implements EvolutionAlgorithm
{
	// Data members.
	private CopyOnWriteArrayList<EvolutionListener> fieldListeners = new CopyOnWriteArrayList<>( );
	private StateManager<S> fieldStateManager;
	private SearchContext<S> fieldSearchContext;
	private Population<S> fieldPopulation;
	private Selector<S> fieldSelector;


	// FIXME - Extracted from BiasSource documentation. Probably belongs with the mutator
	// once it gets separated from the algorithm.
	/*
	 * For instance, the bias value for {@code BIAS_CODE_MUTATION_PROBABILITY} must be
	 * greater than 0. Any value {@code x} greater than 1 and less than 2 will guarantee
	 * at least 1 mutation and have an {@code x - 1} probability of a second mutation; a
	 * value of {@code 2.3} would guarantee two mutations and cause a 30% chance at a
	 * third. (This is the default definition for this bias, and all EvolutionAlgorithms
	 * supplied with this library comply with it; custom EvolutionAlgorithm
	 * implementations need not necessarily respect it or any other bias defined herein.)
	 */

	/**
	 * EugenicAlgorithm constructor.
	 */
	public EugenicAlgorithm( SearchContext<S> searchContext, Selector<S> selector )
	{
		fieldSearchContext = searchContext;
		fieldSelector = selector;

		fieldStateManager = searchContext.getStateManager( );
		fieldPopulation = searchContext.getPopulation( );

	} // EugenicAlgorithm


	/**
	 * Breeds the given collection of parents, applies any desired mutations to the
	 * offspring, and populates the given children collection with the results. The number
	 * of children to produce is up to the implementation; the default produces only one
	 * child. The details of "reproducing" and mutating children are handled by the
	 * StateManager.
	 * @param parents a {@code List<S>} of parent states to be merged into children.
	 * @param children a {@code List<S>} of finalized child states to be added to the
	 *            population.
	 */
	protected void breed( List<S> parents, List<S> children )
	{
		// Reproduce once.
		S child = fieldStateManager.reproduce( parents.get( 0 ), parents.get( 1 ) );

		// Mutate sometimes, based on a chance supplied by the bias source.
		double mutationChance = fieldSearchContext.getBias( BiasSource.BIAS_KEY_MUTATION_RATE );

		// For every full 1 above 0 in our value, mutate once.
		for ( ; mutationChance > 1; mutationChance-- )
			child = fieldStateManager.mutate( child );

		// TODO - Custom random impl.
		// With the remainder, treat that as the % chance of mutating. (0.4 = 40%, 1 - 0.4
		// = 0.6, random double between 0 and 1 should produce a value > 0.6 40% of the
		// time.)
		if ( ThreadLocalRandom.current( ).nextDouble( ) > ( 1 - mutationChance ) )
			child = fieldStateManager.mutate( child );

		// Return only the final result.
		children.add( child );

	} // breed


	/**
	 * Begins working in the given environment to solve the problem.
	 */
	@Override
	public void evolve( )
	{
		notifyListeners( EvolutionListener.EVENT_ID_BEGIN_EVOLUTION, null );

		// Offspring collection.
		List<S> children = new ArrayList<>( );
		List<S> parents = new ArrayList<>( );

		// Allow interruption to cancel the thread. The problem manager will automatically
		// terminate us when that happens.
		while ( !Thread.interrupted( ) )
			{
			for ( int index = 0; index < fieldPopulation.getGenerationSize( ); index++ )
				{
				// Select parents.
				fieldSelector.selectParents( fieldPopulation, parents );

				// Breed & mutate.
				breed( parents, children );

				// Release offspring into wild.
				for ( S child : children )
					{
					fieldPopulation.sow( child );
					notifyListeners( EvolutionListener.EVENT_ID_MEMBER_ADDED_TO_POPULATION, child );
					}

				parents.clear( );
				children.clear( );
				}

			fieldPopulation.nextGeneration( );
			notifyListeners( EvolutionListener.EVENT_ID_NEW_GENERATION, null );
			}

		notifyListeners( EvolutionListener.EVENT_ID_END_EVOLUTION, null );

	} // run


	protected void notifyListeners( int eventId, Object eventObject )
	{
		for ( EvolutionListener listener : fieldListeners )
			listener.onEvent( eventId, eventObject );

	} // notifyListeners


	@Override
	public void registerListener( EvolutionListener listener )
	{
		fieldListeners.add( listener );

	} // registerListener

	// /**
	// * Selects parents from the population to be bred. By default, two parents are
	// chosen
	// * stochastically: on a linear scale, the higher a state's fitness is, the less
	// likely
	// * it is to be rejected. Other implementations may augment this policy, choose more
	// * than two parents (so long as they can also handle breeding that many), etc. A
	// * state's fitness is determined by the StateManager.
	// * @param parents a {@code List<S>} to populate with "parent" states which will be
	// * used to generate new child states.
	// */
	// protected void selectParents( List<S> parents )
	// {
	// for ( int index = 0; index < 2; index++ )
	// {
	// // Generate the odds a candidate has to beat to become a parent.
	// double fitnessThreshold = fieldSearchContext.getBias(
	// BiasSource.BIAS_KEY_SELECTIVITY );
	//
	// // Get a candidate from the population.
	// S candidate = fieldPopulation.sample( );
	//
	// // Do we keep it? Do we break early?
	// // TODO - How do I ask the bias generator how quickly to decay this value?
	// int attempts = fieldPopulation.getGenerationSize( );
	// while ( ( fieldStateManager.fitness( candidate ) < fitnessThreshold )
	// && ( attempts-- > 0 ) )
	// candidate = fieldPopulation.sample( );
	//
	// parents.add( candidate );
	// }
	//
	// } // selectParents

}
