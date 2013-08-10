/*
 * PopulationWrapper.java
 * 
 * Created on Jul 9, 2013
 * 
 */
package org.agal.impl;

import org.agal.core.Population;
import org.agal.core.StateManager;

/**
 * PopulationWrapper is a simple abstraction that allows generic Population extensions to
 * be made that can be coupled with various Population implementations.
 * @author David Schmidt
 */
public abstract class PopulationWrapper<S> implements Population<S>
{
	// Data members.
	private Population<S> fieldWrappedPopulation;


	public PopulationWrapper( Population<S> wrappedPopulation )
	{
		fieldWrappedPopulation = wrappedPopulation;

	} // Population


	@Override
	public void destroy( )
	{
		fieldWrappedPopulation.destroy( );

	} // destroy


	@Override
	public int getGenerationSize( )
	{
		return fieldWrappedPopulation.getGenerationSize( );

	} // getGenerationSize


	@Override
	public void initialize( StateManager<S> stateManager, int populationSize )
	{
		fieldWrappedPopulation.initialize( stateManager, populationSize );

	} // initialize


	@Override
	public void nextGeneration( )
	{
		fieldWrappedPopulation.nextGeneration( );

	} // nextGeneration


	@Override
	public S reap( )
	{
		return fieldWrappedPopulation.reap( );

	} // reap


	@Override
	public S sample( )
	{
		return fieldWrappedPopulation.sample( );

	} // sample


	@Override
	public int size( )
	{
		return fieldWrappedPopulation.size( );

	} // size


	@Override
	public void sow( S member )
	{
		fieldWrappedPopulation.sow( member );

	} // sow

}
