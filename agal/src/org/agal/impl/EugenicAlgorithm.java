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

import org.agal.core.EvolutionAlgorithm;
import org.agal.core.EvolutionListener;
import org.agal.core.Mutator;
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
	private final CopyOnWriteArrayList<EvolutionListener> fieldListeners = new CopyOnWriteArrayList<>( );
	private final StateManager<S> fieldStateManager;
	private final SearchContext<S> fieldSearchContext;
	private final Population<S> fieldPopulation;
	private final Selector<S> fieldSelector;
	private final Mutator<S> fieldMutator;


	/**
	 * EugenicAlgorithm constructor.
	 */
	public EugenicAlgorithm( SearchContext<S> searchContext, Selector<S> selector,
			Mutator<S> mutator )
	{
		fieldSearchContext = searchContext;
		fieldSelector = selector;
		fieldMutator = mutator;

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

		// Mutate sometimes, according to the mutator's wishes.
		for ( int mutationCount = fieldMutator.mutateCount( child ); mutationCount > 0; mutationCount-- )
			fieldMutator.mutate( child );

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


	protected void notifyListeners( String eventId, Object eventObject )
	{
		for ( EvolutionListener listener : fieldListeners )
			listener.onEvent( eventId, eventObject );

	} // notifyListeners


	@Override
	public void registerListener( EvolutionListener listener )
	{
		fieldListeners.add( listener );

	} // registerListener

}
