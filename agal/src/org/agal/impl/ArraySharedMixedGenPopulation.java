/*
 * ArraySharedMixedGenPopulation.java
 * 
 * Created on Aug 16, 2013
 * 
 */
package org.agal.impl;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.agal.core.AbstractFitnessEvaluator;
import org.agal.core.Population;
import org.agal.core.StateManager;

/**
 * ArraySharedMixedGenPopulation
 * @author Dave
 */
public class ArraySharedMixedGenPopulation<S> implements Population<S>
{
	// Data members.
	private AtomicReferenceArray<S> fieldArray;
	private AtomicInteger fieldGenerationCount = new AtomicInteger( 1 );
	private AtomicInteger fieldStateCount = new AtomicInteger( 0 );
	private AbstractFitnessEvaluator<S> fieldFitnessEvaluator;


	public ArraySharedMixedGenPopulation( )
	{
	} // ArraySharedMixedGenPopulation


	@Override
	public void destroy( )
	{
		// Nothing to do, actually.

	} // destroy


	@Override
	public int getGenerationCount( )
	{
		return fieldGenerationCount.get( );

	} // getGenerationCount


	@Override
	public int getGenerationSize( )
	{
		return fieldArray.length( );

	} // getGenerationSize


	@Override
	public synchronized void initialize( AbstractFitnessEvaluator<S> fitnessEvaluator,
			StateManager<S> stateManager, int populationSize )
	{
		fieldFitnessEvaluator = fitnessEvaluator;

		// FIXME - Not using sow causes wrapper problems?
		fieldArray = new AtomicReferenceArray<>( populationSize );
		for ( int index = 0; index < populationSize; index++ )
			fieldArray.set( index, stateManager.randomize( ) );

	} // initialize


	@Override
	public void nextGeneration( )
	{
		// Nothing to do. We track generations for ourselves.

	} // nextGeneration


	@Override
	public S reap( )
	{
		return sample( );

	} // reap


	@Override
	public S sample( )
	{
		// FIXME - Get a random instance from the searchcontext (??!)
		Random random = new Random( );

		int index = random.nextInt( fieldArray.length( ) );

		return fieldArray.get( index );

	} // sample


	@Override
	public int size( )
	{
		return getGenerationSize( );

	} // size


	@Override
	public void sow( S member )
	{
		// FIXME - Get a random instance from the searchcontext (??!)
		Random random = new Random( );

		// Non-blocking way to replace a random lower-fitness state with this better one.
		int retryLimit = 3;
		while ( true )
			{
			int index = random.nextInt( size( ) );
			S previousMember = fieldArray.get( index );

			if ( fieldFitnessEvaluator.compare( previousMember, member ) >= 0 && retryLimit-- > 0 )
				continue;

			if ( fieldArray.compareAndSet( index, previousMember, member ) )
				break;
			}

		// Non-blocking way to count generations by rolling over the count of the number
		// of states we have to 0.
		while ( true )
			{
			int currentCount = fieldStateCount.incrementAndGet( );

			if ( currentCount < size( ) )
				break;

			if ( fieldStateCount.compareAndSet( currentCount, 0 ) )
				{
				fieldGenerationCount.incrementAndGet( );
				break;
				}
			}

	} // sow

}
