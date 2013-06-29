/*
 * ClassicGeneticAlgorithm.java
 * 
 * Created on Jul 7, 2012
 * 
 */
package org.agal.core.impl;

import java.util.Random;

import org.agal.core.EvolutionListener;
import org.agal.core.PopulationModel;
import org.agal.core.StateManager;
import org.agal.toys.NQueensProblem;

/**
 * ClassicGeneticAlgorithm implements what is described as "a more popular version" of
 * their sample algorithm. (Generational population pool is implied.)
 * @deprecated Use EugenicAlgorithm instead for now.
 * @author Dave
 */
@Deprecated
public class ClassicGeneticAlgorithm<S> extends EugenicAlgorithm<S>
{
	// Data members.
	private StateManager<S> fieldStateManager;
	private PopulationModel<S> fieldPopulation;
	private int fieldPopulationSize;
	private int fieldGenerations = 1;


	public ClassicGeneticAlgorithm( StateManager<S> stateManager, int populationSize )
	{
		// TODO !!!
		super( null, stateManager, null );

		fieldStateManager = stateManager;
		fieldPopulationSize = populationSize;

	} // ClassicGeneticAlgorithm


	@Override
	public void evolve( )
	{
		Random mutate = new Random( );
		solve: while ( fieldSolution == null )
			{
			NQueensProblem[ ] newPopulation = new NQueensProblem[ fieldPopulationSize ];
			for ( int index = 0; index < fieldPopulationSize; index++ )
				{
				NQueensProblem x = randomSelection( );
				NQueensProblem y = randomSelection( );

				if ( fieldSolution != null )
					break solve;

				NQueensProblem child = reproduce( x, y );
				if ( mutate.nextDouble( ) > 0.875 )
					child = mutate( child );

				if ( child.getConflicts( ) == 0 )
					{
					fieldSolution = child;
					break solve;
					}

				newPopulation[ index ] = child;
				}

			fieldPopulation = newPopulation;
			fieldGenerations++;
			}

	} // evolve


	public int getGenerations( )
	{
		return fieldGenerations;

	} // getGenerations


	public NQueensProblem getSolution( )
	{
		return fieldSolution;

	} // getSolution


	private NQueensProblem mutate( NQueensProblem original )
	{
		Random r = new Random( );
		int[ ] positions = original.getPositions( );
		positions[ r.nextInt( fieldBoardSize ) ] = r.nextInt( fieldBoardSize );
		// positions[ r.nextInt( fieldBoardSize ) ] = r.nextInt( fieldBoardSize );
		return new NQueensProblem( positions );
		// return new NQueensProblem( null, fieldBoardSize );

	} // mutate


	private NQueensProblem randomSelection( )
	{
		Random r = new Random( );
		NQueensProblem state = fieldPopulation[ r.nextInt( fieldPopulationSize ) ];

		double genBias = ( 0.001 * ( fieldGenerations % 1000 ) );
		int i = 0;
		while ( Math.pow( ( double ) state.getConflicts( ) / ( double ) fieldMaxConflicts, 0.25 ) > ( r
				.nextDouble( ) ) && ( i++ < fieldPopulationSize ) )
			{
			state = fieldPopulation[ r.nextInt( fieldPopulationSize ) ];
			}

		return state;

	} // randomSelection


	/*
	 * (non-Javadoc)
	 * @see org.agal.core.EvolutionAlgorithm#registerListener(ai.search
	 * .algorithms.genetic.core.EvolutionListener)
	 */
	@Override
	public void registerListener( EvolutionListener listener )
	{
		// TODO Auto-generated method stub

	}


	private NQueensProblem reproduce( NQueensProblem x, NQueensProblem y )
	{
		Random r = new Random( );
		int[ ] xpos = x.getPositions( );
		int[ ] ypos = y.getPositions( );
		int[ ] childPos = new int[ fieldBoardSize ];
		int c = r.nextInt( fieldBoardSize );
		System.arraycopy( xpos, 0, childPos, 0, c );
		System.arraycopy( ypos, c, childPos, c, fieldBoardSize - c );
		// for ( int i = 0; i < fieldBoardSize; i++ )
		// childPos[ i ] = ( r.nextBoolean( ) ? xpos[ i ] : ypos[ i ] );
		return new NQueensProblem( childPos );

	} // reproduce

}
