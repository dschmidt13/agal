/*
 * StupidSTPopulation.java
 * 
 * Created on Jun 27, 2013
 * 
 */
package org.agal.core.impl;

import org.agal.core.PopulationModel;
import org.agal.core.StateManager;

/**
 * StupidSTPopulation is a stupid single-threaded (non-threadsafe) population.
 * @author Dave
 *
 */
public class StupidSTPopulation<S> implements PopulationModel<S>
{

	/**
	 * StupidSTPopulation constructor.
	 */
	public StupidSTPopulation( )
	{
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see org.agal.core.PopulationModel#destroy()
	 */
	@Override
	public void destroy( )
	{
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.agal.core.PopulationModel#getGenerationSize()
	 */
	@Override
	public int getGenerationSize( )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.agal.core.PopulationModel#initialize(org.agal.core.StateManager, int)
	 */
	@Override
	public void initialize( StateManager<S> stateManager, int populationSize )
	{
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.agal.core.PopulationModel#nextGeneration()
	 */
	@Override
	public void nextGeneration( )
	{
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.agal.core.PopulationModel#reap()
	 */
	@Override
	public S reap( )
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.agal.core.PopulationModel#sample()
	 */
	@Override
	public S sample( )
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.agal.core.PopulationModel#size()
	 */
	@Override
	public int size( )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.agal.core.PopulationModel#sow(java.lang.Object)
	 */
	@Override
	public void sow( S member )
	{
		// TODO Auto-generated method stub
		
	}

}
