/*
 * PopulationWrapper.java
 * 
 * Created on Jul 9, 2013
 * 
 */
package org.agal.impl;

import org.agal.core.AbstractFitnessEvaluator;
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
	public int getGenerationCount( )
	{
		return fieldWrappedPopulation.getGenerationCount( );

	} // getGenerationCount


	@Override
	public int getGenerationSize( )
	{
		return fieldWrappedPopulation.getGenerationSize( );

	} // getGenerationSize


	public Population<S> getWrappedPopulation( )
	{
		return fieldWrappedPopulation;

	} // getWrappedPopulation


	@Override
	public void initialize( AbstractFitnessEvaluator<S> fitnessEvaluator,
			StateManager<S> stateManager, int populationSize )
	{
		fieldWrappedPopulation.initialize( fitnessEvaluator, stateManager, populationSize );

	} // initialize


	public boolean isWrappedBy( Class<? extends PopulationWrapper> wrapperClass )
	{
		if ( wrapperClass.isAssignableFrom( this.getClass( ) ) )
			return true;

		if ( fieldWrappedPopulation instanceof PopulationWrapper )
			return ( ( PopulationWrapper<S> ) fieldWrappedPopulation ).isWrappedBy( wrapperClass );
		else
			return false;

	} // isWrappedBy


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
