/*
 * StupidSTPopulation.java
 * 
 * Created on Jun 27, 2013
 * 
 */
package org.agal.impl;

import java.util.concurrent.ThreadLocalRandom;

import org.agal.core.AbstractFitnessEvaluator;
import org.agal.core.Population;
import org.agal.core.StateManager;

/**
 * StupidSTPopulation is a stupid single-threaded (non-threadsafe) population.
 * @author Dave
 */
public class StupidSTPopulation<S> implements Population<S>
{
	// Data members.
	private int fieldSize;
	private Object[ ] fieldCurrentGeneration;
	private Object[ ] fieldNextGeneration;
	private int fieldCurrentElementIndex = 0;
	private int fieldGenerationCount = 0;


	/**
	 * StupidSTPopulation constructor.
	 */
	public StupidSTPopulation( )
	{
	} // StupidSTPopulation


	private Object[ ] createGenerationArray( )
	{
		return new Object[ fieldSize ];

	} // createGenerationArray


	@Override
	public void destroy( )
	{
		// Nothing to do, really.

	} // destroy


	@Override
	public int getGenerationCount( )
	{
		return fieldGenerationCount;

	} // getGenerationCount


	@Override
	public int getGenerationSize( )
	{
		return fieldSize;

	} // getGenerationSize


	@Override
	public void initialize( AbstractFitnessEvaluator<S> fitnessEvaluator,
			StateManager<S> stateManager, int populationSize )
	{
		fieldSize = populationSize;

		fieldNextGeneration = createGenerationArray( );

		for ( int index = 0; index < populationSize; index++ )
			sow( stateManager.randomize( ) );

		nextGeneration( );

	} // initialize


	@Override
	public void nextGeneration( )
	{
		fieldCurrentGeneration = fieldNextGeneration;
		fieldNextGeneration = createGenerationArray( );
		fieldCurrentElementIndex = 0;
		fieldGenerationCount++;

	} // nextGeneration


	@Override
	public S reap( )
	{
		return sample( );

	} // reap


	@Override
	@SuppressWarnings( "unchecked" )
	public S sample( )
	{
		return ( S ) fieldCurrentGeneration[ ThreadLocalRandom.current( ).nextInt( fieldSize ) ];

	} // sample


	@Override
	public int size( )
	{
		return fieldSize;

	} // size


	@Override
	public void sow( S member )
	{
		fieldNextGeneration[ fieldCurrentElementIndex++ ] = member;
		fieldCurrentElementIndex %= fieldSize;

	} // sow

}
