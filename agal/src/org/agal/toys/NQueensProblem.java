/*
 * NQueensProblem.java
 * 
 * Created on Jul 6, 2012
 * 
 */
package org.agal.toys;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

import org.agal.core.EvolutionConfiguration;
import org.agal.core.EvolutionControlThread;
import org.agal.core.SearchContext;
import org.agal.impl.ArraySharedMixedGenPopulation;
import org.agal.impl.EugenicAlgorithm;
import org.agal.impl.FitnessThresholdStopCondition;
import org.agal.impl.FixedBiasSource;
import org.agal.impl.FluctuatingBiasSource;
import org.agal.impl.ThreadLocalRandomSource;
import org.agal.impl.TimedStopCondition;
import org.agal.impl.TournamentSelector;

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
	private final long fieldConflicts;

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
			throws Exception
	{
		int SIZE = 50;
		int POPULATION_SIZE = 100;
		int MAX_TIME_MILLIS = 5 * 100;
		boolean MUTATION_FLUX = false;
		double MUTATION_RATE = 0.115;
		int MUTATION_FLUX_WAVELENGTH = 250;
		double GOAL_FITNESS = 1;
		boolean OUTPUT_BOARD = false;

		try
			{
			SIZE = Integer.parseInt( args[ 0 ] );
			POPULATION_SIZE = Integer.parseInt( args[ 1 ] );
			MAX_TIME_MILLIS = Integer.parseInt( args[ 2 ] );
			MUTATION_FLUX = Boolean.parseBoolean( args[ 3 ] );
			MUTATION_RATE = Double.parseDouble( args[ 4 ] );
			MUTATION_FLUX_WAVELENGTH = Integer.parseInt( args[ 5 ] );
			GOAL_FITNESS = Double.parseDouble( args[ 6 ] );
			OUTPUT_BOARD = Boolean.parseBoolean( args[ 7 ] );
			}
		catch ( Exception ignored )
			{
			}

		NQueensStateManager sm = new NQueensStateManager( SIZE );

		// Convert the "goal fitness" into something that makes sense.
		int goalConflicts = ( int ) ( ( 1 - GOAL_FITNESS ) * sm.getMaxConflicts( ) );

		EvolutionConfiguration<NQueensProblem> config = new EvolutionConfiguration<>( );
		config.setStateManager( sm );
		config.setFitnessEvaluator( sm );
		config.setAlgorithmClass( EugenicAlgorithm.class );
		config.setSelectorClass( TournamentSelector.class );
		config.setPopulationClass( ArraySharedMixedGenPopulation.class );
		config.setPopulationSize( POPULATION_SIZE );
		if ( MUTATION_FLUX )
			config.setDefaultBiasSource( new FluctuatingBiasSource( MUTATION_FLUX_WAVELENGTH, 0,
					MUTATION_RATE ) );
		else
			config.setDefaultBiasSource( new FixedBiasSource( MUTATION_RATE ) );
		config.setRandomSourceClass( ThreadLocalRandomSource.class );
		config.setRandomClass( Random.class );

		SearchContext<NQueensProblem> searchContext = config.initialize( );

		EvolutionControlThread controlThread = new EvolutionControlThread<>( searchContext, 5,
				new FitnessThresholdStopCondition<NQueensProblem>( sm, goalConflicts ),
				new TimedStopCondition( MAX_TIME_MILLIS ) );

		long millis = System.currentTimeMillis( );

		controlThread.start( );
		controlThread.join( );

		millis = System.currentTimeMillis( ) - millis;

		NQueensProblem solution = searchContext.getBestResult( );

		NumberFormat format = NumberFormat.getPercentInstance( );
		format.setMaximumFractionDigits( 6 );

		if ( OUTPUT_BOARD )
			System.out.println( "Solution: " + Arrays.toString( solution.getPositions( ) ) );
		int solutionFitness = searchContext.getFitnessEvaluator( ).fitness( solution );

		System.out
				.println( ( solution.getConflicts( ) > 0 ? "Best solution in " : "Solved! in " )
						+ millis
						+ "ms/"
						+ searchContext.getPopulation( ).getGenerationCount( )
						+ " generations ("
						+ solutionFitness
						+ " conflicts, ~"
						+ format.format( 1.0 - ( double ) solutionFitness
								/ ( double ) sm.getMaxConflicts( ) ) + " fitness)" );

		DecimalFormat df = new DecimalFormat( "0.00" );
		double gensPerMs = ( double ) searchContext.getPopulation( ).getGenerationCount( )
				/ ( double ) millis;
		double statesPerMs = gensPerMs * POPULATION_SIZE;
		System.out.println( "Performance: " + df.format( gensPerMs ) + " gens/ms; "
				+ df.format( statesPerMs ) + " states/ms" );

	} // main


	/**
	 * Counts the number of conflicts between any two Queens on the board in O(n) time.
	 * This value is cached for efficiency and must be cleared with
	 * {@link #clearConflicts()} if the board state is changed.
	 * @return a long indicating the number of conflicts between any two Queens on this
	 *         board.
	 */
	private long countConflicts( )
	{
		// Use a local variable to count for thread safety reasons.
		long conflicts = 0;

		/*
		 * After analysis, contains the number of Queens in each row of the board. The
		 * topmost row is index 0. Used for determining conflicts.
		 */
		long[ ] rowCounts = new long[ fieldN ];

		/*
		 * After analysis, contains the number of Queens in diagonals lying along the
		 * "Los Angeles to New York" direction. There are 2n - 1 diagonals in a given
		 * direction on an nxn chessboard. Counting from index 0 begins with the top-left
		 * corner (Seattle); the nth diagonal bisects the board along the "lany" axis; and
		 * the 2n - 1th diagonal is the bottom-right corner (Miami). Used for determining
		 * conflicts.
		 */
		long[ ] lanyDiagonalCounts = new long[ 2 * fieldN - 1 ];

		/*
		 * After analysis, contains the number of Queens in diagonals lying along the
		 * "Seattle to Miami" direction. There are 2n - 1 diagonals in a given direction
		 * on an nxn chessboard. Counting from index 0 begins with the bottom-left corner
		 * (Los Angeles); the nth diagonal bisects the board along the "sami" axis; and
		 * the 2n - 1th diagonal is the top-right corner (New York). Used for determining
		 * conflicts. Of course, I'm not going to do the math on it a second time when
		 * it's symmetrical...
		 */
		long[ ] samiDiagonalCounts = new long[ lanyDiagonalCounts.length ];

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
	 * @return a {@code long} indicating the number of conflicts on this board. This may
	 *         be used as a fitness function in a search heuristic.
	 */
	public long getConflicts( )
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
