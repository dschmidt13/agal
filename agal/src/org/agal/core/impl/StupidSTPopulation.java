/*
 * StupidSTPopulation.java
 * 
 * Created on Jun 27, 2013
 * 
 */
package org.agal.core.impl;

import java.lang.reflect.Array;
import java.util.concurrent.ThreadLocalRandom;

import org.agal.core.PopulationModel;
import org.agal.core.StateManager;

/**
 * StupidSTPopulation is a stupid single-threaded (non-threadsafe) population.
 * @author Dave
 */
public class StupidSTPopulation<S> implements PopulationModel<S>
{
	// Data members.
	private int fieldSize;
	private Class<S> fieldElementClass;
	private S[ ] fieldCurrentGeneration;
	private S[ ] fieldNextGeneration;
	private int fieldCurrentElementIndex = 0;
	private int fieldGenerationCount = 0;


	/**
	 * StupidSTPopulation constructor.
	 */
	public StupidSTPopulation( Class<S> clazz, int size )
	{
		fieldSize = size;
		fieldElementClass = clazz;

	} // StupidSTPopulation


	@SuppressWarnings( "unchecked" )
	private S[ ] createGenerationArray( )
	{
		return ( S[ ] ) Array.newInstance( fieldElementClass, fieldSize );

	} // createGenerationArray


	@Override
	public void destroy( )
	{
		// Nothing to do, really.

	} // destroy


	@Override
	public int getGenerationSize( )
	{
		return fieldSize;

	} // getGenerationSize


	public int getNumGenerations( )
	{
		return fieldGenerationCount;

	} // getNumGenerations


	@Override
	public void initialize( StateManager<S> stateManager, int populationSize )
	{
		fieldCurrentGeneration = createGenerationArray( );
		fieldNextGeneration = createGenerationArray( );

		for ( int index = 0; index < fieldSize; index++ )
			fieldCurrentGeneration[ index ] = stateManager.randomize( );

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
	public S sample( )
	{
		return fieldCurrentGeneration[ ThreadLocalRandom.current( ).nextInt( fieldSize ) ];

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
