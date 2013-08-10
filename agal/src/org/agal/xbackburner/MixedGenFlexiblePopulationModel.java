/*
 * MixedGenFlexiblePopulationModel.java
 * 
 * Created on Jul 12, 2012
 * 
 */
package org.agal.xbackburner;

import java.util.Random;

import org.agal.core.Population;
import org.agal.core.StateManager;
import org.agal.impl.LazySineWaveGenerator;

/**
 * MixedGenFlexiblePopulationModel is a {@code Population} implementation which does
 * not have discrete generations, instead mixing parents and children in the population
 * pool. It is also dynamic in that it does not maintain a static population size, but
 * rather grows and shrinks as necessary when members are sown and reaped.
 * @deprecated Until restored to working order and made properly configurable.
 * @author David Schmidt
 */
@Deprecated
public class MixedGenFlexiblePopulationModel<S> implements Population<S>
{
	private class Reaper implements Runnable
	{
		private LazySineWaveGenerator fieldGrowthRate;


		Reaper( long wavelengthMillis, long granularityMillis )
		{
			fieldGrowthRate = new LazySineWaveGenerator( wavelengthMillis, granularityMillis );

		} // Reaper


		@Override
		public void run( )
		{
			// TODO

		} // run

	} // Reaper

	// Data members.
	private ConcurrentRandomAccessList<S> fieldPopulation;
	private RandomPool fieldRandomPool = new RandomPool( );
	private int fieldGenerationCount = 1;
	private Thread fieldReaper;


	/**
	 * MixedGenFlexiblePopulationModel constructor.
	 */
	protected MixedGenFlexiblePopulationModel( long wavelengthMillis, long granularityMillis )
	{
		fieldPopulation = new ConcurrentRandomAccessList<S>( true );
		fieldReaper = new Thread( new Reaper( wavelengthMillis, granularityMillis ) );

	} // MixedGenFlexiblePopulationModel


	/*
	 * (non-Javadoc)
	 * @see org.agal.core.Population#destroy()
	 */
	@Override
	public void destroy( )
	{
		// TODO Auto-generated method stub

	}


	/**
	 * Generates an index for (roughly) where we want to access the population collection.
	 * @return an {@code int} containing a randomly generated value between {@code 0} and
	 *         the size of the population collection.
	 */
	private int generateIndex( )
	{
		Random rand = fieldRandomPool.borrowRandom( );

		// This normally wouldn't be safe since it's multithreaded, but luckily for us
		// this collection is designed to handle out-of-bounds cases gracefully.
		int populationSize = fieldPopulation.size( );

		int index = rand.nextInt( populationSize );
		return index;

	} // generateIndex


	/*
	 * (non-Javadoc)
	 * @see org.agal.core.Population#getGenerationSize()
	 */
	@Override
	public int getGenerationSize( )
	{
		// TODO Auto-generated method stub
		return 0;
	}


	/*
	 * (non-Javadoc)
	 * @see org.agal.core.Population#initialize(org.agal.core.StateManager, int)
	 */
	@Override
	public void initialize( StateManager<S> stateManager, int populationSize )
	{
		// TODO Auto-generated method stub

	}


	@Override
	public void nextGeneration( )
	{
		fieldGenerationCount++;

	} // nextGeneration


	@Override
	public S reap( )
	{
		return fieldPopulation.remove( generateIndex( ) );

	} // reap


	@Override
	public S sample( )
	{
		return fieldPopulation.get( generateIndex( ) );

	} // sample


	@Override
	public int size( )
	{
		return fieldPopulation.size( );

	} // size


	@Override
	public void sow( S member )
	{
		fieldPopulation.insert( generateIndex( ), member );

	} // sow

}
