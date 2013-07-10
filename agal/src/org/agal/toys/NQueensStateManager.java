/*
 * NQueensStateManager.java
 * 
 * Created on Jul 8, 2012
 * 
 */
package org.agal.toys;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.agal.core.StateManager;

/**
 * NQueensStateManager is a StateManager implementation for the NQueensProblem.
 * @author David Schmidt
 */
public class NQueensStateManager implements StateManager<NQueensProblem>
{
	// Data members.
	private final int fieldBoardSize;
	private final long fieldMaxConflicts;


	/**
	 * NQueensStateManager constructor.
	 */
	public NQueensStateManager( int boardSize )
	{
		fieldBoardSize = boardSize;

		// Calculate and save the maximum number of conflicts for use in fitness
		// calculations. The works as: Imagine all Queens are placed on the same row, so
		// that all are conflicting. Each Queen added causes a conflict with each queen
		// already existing there: the nth queen causes n-1 new conflicts. So the total
		// number of conflicts with n queens on the same row is the sum from 1 to n-1.
		// This loop does that.
		// LAM - There's probably an equation for this discovered with integral calc. Oh
		// well, it only really runs once.
		long maxConflicts = 0;
		for ( int i = 1; i < fieldBoardSize; maxConflicts += i++ )
			; // Just counting. Not broken.
		fieldMaxConflicts = maxConflicts;

	} // NQueensStateManager


	@Override
	public double fitness( NQueensProblem individual )
	{
		return 1 - ( ( double ) individual.getConflicts( ) / fieldMaxConflicts );

	} // fitness


	@Override
	public NQueensProblem mutate( NQueensProblem original )
	{
		// FIXME
		return singlePointMutation( original );

	} // mutate


	@Override
	public NQueensProblem randomize( )
	{
		return new NQueensProblem( ThreadLocalRandom.current( ), fieldBoardSize );

	} // randomize


	private NQueensProblem randomMeshReproduce( NQueensProblem mother, NQueensProblem father )
	{
		int[ ] momGenes = mother.getPositions( );
		int[ ] dadGenes = father.getPositions( );
		int[ ] childGenes = new int[ fieldBoardSize ];
		Random rand = ThreadLocalRandom.current( );
		for ( int i = 0; i < fieldBoardSize; i++ )
			childGenes[ i ] = ( rand.nextBoolean( ) ? momGenes[ i ] : dadGenes[ i ] );
		return new NQueensProblem( childGenes );

	} // randomMeshReproduce


	@Override
	public NQueensProblem reproduce( NQueensProblem mother, NQueensProblem father )
	{
		// FIXME
		return singleCrossoverReproduce( mother, father );

	} // reproduce


	private NQueensProblem runlengthMultiCrossoverReproduce( NQueensProblem mother,
			NQueensProblem father )
	{
		int[ ] momGenes = mother.getPositions( );
		int[ ] dadGenes = father.getPositions( );
		int[ ] childGenes = new int[ fieldBoardSize ];
		int genePosition = 0;
		Random rand = ThreadLocalRandom.current( );

		// This dodgy little number alternates between parents, taking a random-length
		// strip of alleles and copying it into the child all the way along the
		// chromosome.
		for ( int index = 0; genePosition < fieldBoardSize; index++ )
			System.arraycopy( ( index % 2 == 0 ) ? momGenes : dadGenes, genePosition, childGenes,
					genePosition, rand.nextInt( fieldBoardSize - genePosition ) + 1 );

		return new NQueensProblem( childGenes );

	} // runlengthMultiCrossoverReproduce


	private NQueensProblem singleCrossoverReproduce( NQueensProblem mother, NQueensProblem father )
	{
		// An implementation of the "traditional" method used by modern GAs, simulating
		// crossover from miosis. A crossover point is chosen at random along the state
		// representation; the first "half" of one parent's state and the second "half" of
		// the other's are taken and combined in their original order to form the child's
		// chromosome. (This method is crap.)
		int[ ] momGenes = mother.getPositions( );
		int[ ] dadGenes = father.getPositions( );
		int crossoverPoint = ThreadLocalRandom.current( ).nextInt( fieldBoardSize );
		int[ ] childGenes = new int[ fieldBoardSize ];
		System.arraycopy( momGenes, 0, childGenes, 0, crossoverPoint );
		System.arraycopy( dadGenes, crossoverPoint, childGenes, crossoverPoint, fieldBoardSize
				- crossoverPoint );

		return new NQueensProblem( childGenes );

	} // singleCrossoverReproduce


	private NQueensProblem singlePointMutation( NQueensProblem original )
	{
		Random rand = ThreadLocalRandom.current( );
		int[ ] chromosome = Arrays.copyOf( original.getPositions( ), fieldBoardSize );

		chromosome[ rand.nextInt( fieldBoardSize ) ] = rand.nextInt( fieldBoardSize );

		return new NQueensProblem( chromosome );

	} // singlePointMutation

}
