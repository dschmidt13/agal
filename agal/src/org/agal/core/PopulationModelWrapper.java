/*
 * PopulationModelWrapper.java
 * 
 * Created on Jul 9, 2013
 * 
 */
package org.agal.core;

/**
 * PopulationModelWrapper is a simple abstraction that allows generic PopulationModel
 * extensions to be made that can be coupled with various PopulationModel implementations.
 * @author David Schmidt
 */
public abstract class PopulationModelWrapper<S> implements PopulationModel<S>
{
	// Data members.
	private PopulationModel<S> fieldWrappedPopulationModel;


	public PopulationModelWrapper( PopulationModel<S> wrappedPopulationModel )
	{
		fieldWrappedPopulationModel = wrappedPopulationModel;

	} // PopulationModel


	@Override
	public void destroy( )
	{
		fieldWrappedPopulationModel.destroy( );

	} // destroy


	@Override
	public int getGenerationSize( )
	{
		return fieldWrappedPopulationModel.getGenerationSize( );

	} // getGenerationSize


	@Override
	public void initialize( StateManager<S> stateManager, int populationSize )
	{
		fieldWrappedPopulationModel.initialize( stateManager, populationSize );

	} // initialize


	@Override
	public void nextGeneration( )
	{
		fieldWrappedPopulationModel.nextGeneration( );

	} // nextGeneration


	@Override
	public S reap( )
	{
		return fieldWrappedPopulationModel.reap( );

	} // reap


	@Override
	public S sample( )
	{
		return fieldWrappedPopulationModel.sample( );

	} // sample


	@Override
	public int size( )
	{
		return fieldWrappedPopulationModel.size( );

	} // size


	@Override
	public void sow( S member )
	{
		fieldWrappedPopulationModel.sow( member );

	} // sow

}
