/*
 * NQueensProblem.java
 * 
 * Created on Jul 6, 2012
 * 
 */
package org.agal.toys;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

import org.agal.core.EvolutionAlgorithm;
import org.agal.core.EvolutionControlThread;
import org.agal.core.StateManager;
import org.agal.core.impl.EugenicAlgorithm;
import org.agal.core.impl.FitnessThresholdStopCondition;
import org.agal.core.impl.SimpleBiasSource;
import org.agal.core.impl.StupidSTPopulation;
import org.agal.core.impl.TimedStopCondition;

/**
 * NQueensProblem represents a generic nxn chessboard with n Queens on it. The problem is
 * solved when no Queen is able to attack another; that is, when the number of
 * {@code conflicts} is {@code 0}. The analysis of conflicted Queens runs in O(n) time and
 * is intended for use as a fitness function of the board's state. It is performed at time
 * of creation.
 * <p>
 * Random instances may be easily created with {@link #NQueensProblem(Random, int)}.
 * <p>
 * Once a board is created, it may not be modified. This class is thread safe as long as
 * no modifications are made to the positions array returned by {@link #getPositions()}
 * nor to the one passed into the constructor. The number of reported conflicts will not
 * be updated if such a modification occurs.
 * @author David Schmidt
 */
public class NQueensProblem
{
	// Data members.
	private final int fieldN;
	private final int fieldConflicts;

	/**
	 * Contains the row positions of the Queens in their respective (indexed) columns; for
	 * example, a Queen at row 3, col 1 would be defined by arr[1] = 3. Only one Queen per
	 * column is possible in this model. The leftmost column is index 0.
	 */
	private final int[ ] fieldPositions;


	/**
	 * Constructs an nxn board with the given Queen positions. The size of the board will
	 * be taken from the length of the {@code positions} array. <b>To ensure thread
	 * safety, the given array must not be modified by the caller after this constructor
	 * returns.</b>
	 * @param positions an {@code int[ ]} containing row positions of Queens. Each index
	 *            represents a column, its value the row position of the Queen in that
	 *            column. Must not be {@code null}.
	 */
	public NQueensProblem( int[ ] positions )
	{
		fieldN = positions.length;
		fieldPositions = positions;

		fieldConflicts = countConflicts( );

	} // NQueensProblem


	/**
	 * Constructs a new nxn board with random Queen positions.
	 * @param random an optional Random which is used to generate the state.
	 * @param n an int indicating the length of the square board to place queens on.
	 */
	public NQueensProblem( Random random, int n )
	{
		fieldN = n;
		fieldPositions = new int[ fieldN ];

		if ( random == null )
			random = new Random( );

		for ( int index = 0; index < fieldN; index++ )
			fieldPositions[ index ] = random.nextInt( fieldN );

		fieldConflicts = countConflicts( );

	} // NQueensProblem


	public static void main( String[ ] args )
			throws InterruptedException
	{
		int SIZE = 20;
		int POPULATION_SIZE = 250;
		int MAX_TIME_MILLIS = 2 * 1000;
		double MUTATION_RATE = 0.12;
		double BIAS_PER_GEN = 0.015;

		try
			{
			SIZE = Integer.parseInt( args[ 0 ] );
			POPULATION_SIZE = Integer.parseInt( args[ 1 ] );
			MAX_TIME_MILLIS = Integer.parseInt( args[ 2 ] );
			MUTATION_RATE = Double.parseDouble( args[ 3 ] );
			BIAS_PER_GEN = Double.parseDouble( args[ 4 ] );
			}
		catch ( Exception ignored )
			{
			}

		StateManager<NQueensProblem> stateManager = new NQueensStateManager( SIZE );
		StupidSTPopulation<NQueensProblem> pop = new StupidSTPopulation<>( NQueensProblem.class,
				POPULATION_SIZE );
		pop.initialize( stateManager, POPULATION_SIZE );
		SimpleBiasSource bias = new SimpleBiasSource( MUTATION_RATE, BIAS_PER_GEN );
		EvolutionAlgorithm algo = new EugenicAlgorithm<NQueensProblem>( pop, stateManager, bias );
		algo.registerListener( bias );
		FitnessThresholdStopCondition<NQueensProblem> stopCondition = new FitnessThresholdStopCondition<NQueensProblem>(
				stateManager, 1.0 );
		EvolutionControlThread controlThread = new EvolutionControlThread<>( algo, 1,
				stopCondition, new TimedStopCondition( MAX_TIME_MILLIS ) );

		long millis = System.currentTimeMillis( );

		controlThread.start( );
		controlThread.join( );

		millis = System.currentTimeMillis( ) - millis;

		NQueensProblem solution = stopCondition.getSolution( );
		NumberFormat format = NumberFormat.getPercentInstance( );
		format.setMaximumFractionDigits( 2 );
		System.out.println( ( solution.getConflicts( ) > 0 ? "Best solution in " : "Solved! in " )
				+ millis + "ms/" + pop.getNumGenerations( ) + " generations ("
				+ format.format( stateManager.fitness( solution ) ) + " fitness): "
				+ Arrays.toString( solution.getPositions( ) ) );

	} // main


	/**
	 * Counts the number of conflicts between any two Queens on the board in O(n) time.
	 * This value is cached for efficiency and must be cleared with
	 * {@link #clearConflicts()} if the board state is changed.
	 * @return an int indicating the number of conflicts between any two Queens on this
	 *         board.
	 */
	private int countConflicts( )
	{
		// Use a local variable to count for thread safety reasons.
		int conflicts = 0;

		/*
		 * After analysis, contains the number of Queens in each row of the board. The
		 * topmost row is index 0. Used for determining conflicts.
		 */
		int[ ] rowCounts = new int[ fieldN ];

		/*
		 * After analysis, contains the number of Queens in diagonals lying along the
		 * "Los Angeles to New York" direction. There are 2n - 1 diagonals in a given
		 * direction on an nxn chessboard. Counting from index 0 begins with the top-left
		 * corner (Seattle); the nth diagonal bisects the board along the "lany" axis; and
		 * the 2n - 1th diagonal is the bottom-right corner (Miami). Used for determining
		 * conflicts.
		 */
		int[ ] lanyDiagonalCounts = new int[ 2 * fieldN - 1 ];

		/*
		 * After analysis, contains the number of Queens in diagonals lying along the
		 * "Seattle to Miami" direction. There are 2n - 1 diagonals in a given direction
		 * on an nxn chessboard. Counting from index 0 begins with the bottom-left corner
		 * (Los Angeles); the nth diagonal bisects the board along the "sami" axis; and
		 * the 2n - 1th diagonal is the top-right corner (New York). Used for determining
		 * conflicts.
		 */
		int[ ] samiDiagonalCounts = new int[ 2 * fieldN - 1 ];

		// Loop optimization.
		int samiBase = fieldN - 1;

		// Iterate over columns.
		for ( int index = 0; index < fieldN; index++ )
			{
			// Calculate count array indices.
			// Row value is column index position.
			int row = fieldPositions[ index ];

			// Lany diags increase with both coordinates.
			int lany = index + row;

			// Sami diags increase with only one coordinate.
			int sami = samiBase - row + index;

			// Increment count arrays.
			rowCounts[ row ]++;
			lanyDiagonalCounts[ lany ]++;
			samiDiagonalCounts[ sami ]++;

			// Increment total conflict index. Adding a Queen to a line (along any
			// axis) creates a conflict with each Queen that was there before it. A
			// Queen in a given position may conflict with other Queens along more
			// than one axis, so all axes must be considered.
			if ( rowCounts[ row ] > 1 )
				conflicts += ( rowCounts[ row ] - 1 );
			if ( lanyDiagonalCounts[ lany ] > 1 )
				conflicts += ( lanyDiagonalCounts[ lany ] - 1 );
			if ( samiDiagonalCounts[ sami ] > 1 )
				conflicts += ( samiDiagonalCounts[ sami ] - 1 );
			}

		return conflicts;

	} // countConflicts


	/**
	 * @return an {@code int} indicating the number of conflicts on this board. This may
	 *         be used as a fitness function in a search heuristic.
	 */
	public int getConflicts( )
	{
		return fieldConflicts;

	} // getConflicts


	/**
	 * @return an {@code int[ ]} which is the original array of Queen positions on this
	 *         board. <b>To ensure thread safety, this array must not be modified.</b> An
	 *         array index corresponds to a column; a value corresponds to a row position.
	 *         (Thus, there is exactly one Queen per column, by definition.)
	 */
	public int[ ] getPositions( )
	{
		return fieldPositions;

	} // getPositions

}
