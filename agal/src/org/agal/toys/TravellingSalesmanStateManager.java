/*
 * TravellingSalesmanStateManager.java
 * 
 * Created on Aug 10, 2013
 * 
 */
package org.agal.toys;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.agal.core.AbstractFitnessEvaluator;
import org.agal.core.EvolutionConfiguration;
import org.agal.core.EvolutionControlThread;
import org.agal.core.SearchContext;
import org.agal.core.StateManager;
import org.agal.impl.ArraySharedMixedGenPopulation;
import org.agal.impl.EugenicAlgorithm;
import org.agal.impl.FixedBiasSource;
import org.agal.impl.FluctuatingBiasSource;
import org.agal.impl.ThreadLocalRandomSource;
import org.agal.impl.TimedStopCondition;
import org.agal.impl.TournamentSelector;
import org.agal.toys.TravellingSalesmanStateManager.StateWrapper;

/**
 * TravellingSalesmanStateManager
 * @author Dave
 */
public class TravellingSalesmanStateManager extends AbstractFitnessEvaluator<StateWrapper>
		implements StateManager<StateWrapper>
{

	static class StateWrapper
	{
		// Data members.
		Integer fieldFitness;
		int[ ] fieldChromosome;

	}

	private static final double BOUNDS_MARGIN = 1.1;

	// Data members.
	private Point2D.Double[] fieldPoints;
	private int[ ] fieldBaseState;
	private int fieldNumberOfPoints;
	private double fieldWidth;
	private double fieldHeight;


	public TravellingSalesmanStateManager( int numberOfPoints, double bounds )
	{
		super( true );

		fieldPoints = new Point2D.Double[ numberOfPoints ];
		fieldBaseState = new int[ numberOfPoints ];
		fieldWidth = bounds * BOUNDS_MARGIN;
		fieldHeight = bounds * BOUNDS_MARGIN;
		fieldNumberOfPoints = numberOfPoints;

		// LAM - Scoped injection or something?
		Random random = new Random( );

		// Generate the points to traverse. Also double down on the loop and fill up the
		// base state array.
		for ( int index = 0; index < numberOfPoints; index++ )
			{
			fieldPoints[ index ] = new Point2D.Double( random.nextDouble( ) * bounds,
					random.nextDouble( ) * bounds );
			fieldBaseState[ index ] = index;
			}

	} // TravellingSalesmanStateManager


	public TravellingSalesmanStateManager( List<Point2D.Double> points )
	{
		super( true );

		fieldNumberOfPoints = points.size( );
		fieldPoints = new Point2D.Double[ fieldNumberOfPoints ];
		fieldBaseState = new int[ fieldNumberOfPoints ];
		Path2D.Double boundsPath = new Path2D.Double( );

		// Unwind the first point since path won't retain moveTos and won't accept
		// starting with a lineTo.
		fieldPoints[ 0 ] = points.get( 0 );
		boundsPath.moveTo( fieldPoints[ 0 ].x, fieldPoints[ 0 ].y );
		for ( int i = 1; i < fieldNumberOfPoints; i++ )
			{
			fieldPoints[ i ] = points.get( i );
			fieldBaseState[ i ] = i;
			boundsPath.lineTo( fieldPoints[ i ].x, fieldPoints[ i ].y );
			}
		fieldHeight = boundsPath.getBounds2D( ).getHeight( ) * BOUNDS_MARGIN;
		fieldWidth = boundsPath.getBounds2D( ).getWidth( ) * BOUNDS_MARGIN;

	} // TravellingSalesmanStateManager


	public static void main( String... args )
			throws Exception
	{
		// Arg 0 (if not using a TSP file)
		int POINT_COUNT = 15;

		// Arg 0 (if using a TSP file)
		String TSP_FILE = null;

		double BOUNDS = 250.0;
		long TIME_LIMIT = 1000;
		int POPULATION_SIZE = 25;
		double MUTATION_RATE = 0.25;
		boolean MUTATION_FLUX = false;
		int MUTATION_FLUX_WAVELENGTH = 150;
		int MUTATION_FLUX_SINE_POWER = 5;
		int THREAD_COUNT = Runtime.getRuntime( ).availableProcessors( ) + 1;
		String RESULT_FILE = "bin/tsp.png";

		try
			{
			POINT_COUNT = Integer.parseInt( args[ 0 ] );
			}
		catch ( NumberFormatException exception )
			{
			TSP_FILE = args[ 0 ];
			}
		catch ( Exception ignored )
			{
			}

		try
			{
			BOUNDS = Double.parseDouble( args[ 1 ] );
			TIME_LIMIT = Long.parseLong( args[ 2 ] );
			POPULATION_SIZE = Integer.parseInt( args[ 3 ] );
			MUTATION_RATE = Double.parseDouble( args[ 4 ] );
			MUTATION_FLUX = Boolean.parseBoolean( args[ 5 ] );
			MUTATION_FLUX_WAVELENGTH = Integer.parseInt( args[ 6 ] );
			MUTATION_FLUX_SINE_POWER = Integer.parseInt( args[ 7 ] );
			THREAD_COUNT = Integer.parseInt( args[ 8 ] );
			RESULT_FILE = args[ 9 ];
			}
		catch ( Exception ignored )
			{
			}

		TravellingSalesmanStateManager sm;
		if ( TSP_FILE == null )
			sm = new TravellingSalesmanStateManager( POINT_COUNT, BOUNDS );
		else
			sm = new TravellingSalesmanStateManager( readTSPFile( TSP_FILE ) );

		EvolutionConfiguration<StateWrapper> config = new EvolutionConfiguration<>( );
		config.setStateManager( sm );
		config.setFitnessEvaluator( sm );
		config.setAlgorithmClass( EugenicAlgorithm.class );
		config.setPopulationClass( ArraySharedMixedGenPopulation.class );
		config.setPopulationSize( POPULATION_SIZE );
		config.setRandomClass( Random.class );
		config.setRandomSourceClass( ThreadLocalRandomSource.class );
		config.setSelectorClass( TournamentSelector.class );
		if ( MUTATION_FLUX )
			config.setDefaultBiasSource( new FluctuatingBiasSource( MUTATION_FLUX_WAVELENGTH, 1,
					MUTATION_RATE, MUTATION_FLUX_SINE_POWER ) );
		else
			config.setDefaultBiasSource( new FixedBiasSource( MUTATION_RATE ) );

		SearchContext<StateWrapper> searchContext = config.initialize( );

		EvolutionControlThread controlThread = new EvolutionControlThread<>( searchContext,
				THREAD_COUNT, new TimedStopCondition( TIME_LIMIT ) );

		long time = System.currentTimeMillis( );

		controlThread.start( );
		controlThread.join( );

		time = System.currentTimeMillis( ) - time;

		StateWrapper solution = searchContext.getBestResult( );
		sm.drawSolution( solution, RESULT_FILE, BOUNDS );

		System.out.println( "Path length: " + ( ( double ) solution.fieldFitness / 1000.0 ) );

		int gens = searchContext.getPopulation( ).getGenerationCount( );
		double gensPerMs = ( double ) gens / time;
		double statesPerMs = gensPerMs * searchContext.getPopulation( ).size( );
		NumberFormat format = new DecimalFormat( "0.00" );
		System.out.println( "Runtime: " + time + "ms, " + gens + " generations" );
		System.out.println( "Performance: " + format.format( gensPerMs ) + " gens/ms; "
				+ format.format( statesPerMs ) + " states/ms" );

	} // main


	private static List<Point2D.Double> readTSPFile( String filename )
			throws IOException
	{
		Pattern pattern = Pattern.compile( "\\d+ (\\d+(?:\\.\\d+)?) (\\d+(?:\\.\\d+)?)" );

		try ( FileInputStream input = new FileInputStream( filename );
				BufferedReader reader = new BufferedReader( new InputStreamReader( input ) ) )
			{
			// Parse file. Important lines are DIMENSION, NODE_COORD_SECTION, and EOF.
			int dimension = -1;
			boolean coordSection = false;
			String line = reader.readLine( );
			while ( line != null )
				{
				if ( line.startsWith( "DIMENSION" ) )
					dimension = Integer
							.parseInt( line.substring( line.indexOf( ':' ) + 1 ).trim( ) );

				if ( line.startsWith( "NODE_COORD_SECTION" ) )
					{
					coordSection = true;
					break;
					}

				line = reader.readLine( );
				}

			if ( dimension == -1 || !coordSection )
				throw new IOException( "Couldn't parse file format for '" + filename + "'." );

			List<Point2D.Double> points = new ArrayList<>( dimension );

			line = reader.readLine( );
			for ( int pointCount = 0; pointCount < dimension; pointCount++, line = reader
					.readLine( ) )
				{
				if ( line == null || line.contains( "EOF" ) )
					throw new IOException( "File '" + filename + "' ended unexpectedly." );

				Matcher matcher = pattern.matcher( line );
				if ( !matcher.matches( ) )
					throw new IOException( "Line input of '" + line
							+ "' didn't match expected pattern of '" + pattern + "'." );

				points.add( new Point2D.Double( Double.parseDouble( matcher.group( 1 ) ), Double
						.parseDouble( matcher.group( 2 ) ) ) );
				}

			return points;
			}

	} // readTSPFile


	private void drawSolution( StateWrapper solution, String filename, double maxDimension )
			throws IOException
	{
		Path2D.Double path = new Path2D.Double( );
		path.moveTo( fieldPoints[ solution.fieldChromosome[ 0 ] ].x,
				fieldPoints[ solution.fieldChromosome[ 0 ] ].y );
		for ( int i = 1; i < fieldNumberOfPoints; i++ )
			path.lineTo( fieldPoints[ solution.fieldChromosome[ i ] ].x,
					fieldPoints[ solution.fieldChromosome[ i ] ].y );

		// Scale image to max bounds if above. If not it'll turn out the original size.
		double scaleFactor = ( maxDimension > 0 ) ? Math.min(
				maxDimension / Math.max( fieldWidth, fieldHeight ), 1 ) : 1;

		BufferedImage image = new BufferedImage( ( int ) ( fieldWidth * scaleFactor ),
				( int ) ( fieldHeight * scaleFactor ) + 1, BufferedImage.TYPE_INT_ARGB );
		Graphics2D graphics = image.createGraphics( );

		graphics.scale( scaleFactor, scaleFactor );
		graphics.setBackground( Color.WHITE );
		graphics.setPaint( Color.BLACK );
		graphics.setStroke( new BasicStroke( ( float ) ( Math.max( fieldWidth * scaleFactor,
				fieldHeight * scaleFactor ) / 75.0 ) ) );

		// Draw black path on white background.
		graphics.clearRect( 0, 0, image.getWidth( ), image.getHeight( ) );
		graphics.draw( path );

		// Draw red points.
		graphics.setColor( Color.RED );
		for ( Point2D.Double point : fieldPoints )
			{
			Path2D.Double pointPath = new Path2D.Double( );
			pointPath.moveTo( point.x, point.y );
			graphics.draw( pointPath );
			}

		ImageIO.write( image, "PNG", new File( filename ) );

	} // drawSolution


	@Override
	public Integer fitness( StateWrapper wrapper )
	{
		if ( wrapper.fieldFitness == null )
			{
			int[ ] chromosome = wrapper.fieldChromosome;
			double totalDistance = 0;
			Point2D.Double previousPoint = fieldPoints[ chromosome[ 0 ] ];
			for ( int index = 1; index < fieldNumberOfPoints; index++ )
				{
				Point2D.Double nextPoint = fieldPoints[ chromosome[ index ] ];
				totalDistance += previousPoint.distance( nextPoint );
				previousPoint = nextPoint;
				}

			// Fixed point fitness adjustment.
			wrapper.fieldFitness = ( int ) ( totalDistance * 1000 );
			}

		return wrapper.fieldFitness;

	} // fitness


	@Override
	public StateWrapper mutate( StateWrapper wrapper )
	{
		randomSwap( wrapper.fieldChromosome );

		return wrapper;

	} // mutate


	@Override
	public StateWrapper randomize( )
	{
		int[ ] chromosome = Arrays.copyOf( fieldBaseState, fieldNumberOfPoints );

		// Avoid a 0-length bug due to the +1.
		if ( fieldNumberOfPoints > 0 )
			{
			// Shuffle half the list into the whole list.
			for ( int index = 0; index < fieldNumberOfPoints / 2 + 1; index++ )
				randomSwap( chromosome );
			}

		StateWrapper wrapper = new StateWrapper( );
		wrapper.fieldChromosome = chromosome;

		return wrapper;

	} // randomize


	private void randomSwap( int[ ] state )
	{
		// TODO - Get this from the search context (which we don't have yet).
		Random random = new Random( );

		int swapIndex1 = random.nextInt( fieldNumberOfPoints );
		int swapIndex2 = random.nextInt( fieldNumberOfPoints );
		int swapValue = state[ swapIndex1 ];
		state[ swapIndex1 ] = state[ swapIndex2 ];
		state[ swapIndex2 ] = swapValue;

	} // randomSwap


	@Override
	public StateWrapper reproduce( StateWrapper mother, StateWrapper father )
	{
		// TODO - This seems pretty bad in practice.

		int[ ] p1 = mother.fieldChromosome;
		int[ ] p2 = father.fieldChromosome;

		// We'll use every odd value from p2 and every even value from p1, with even
		// values taking priority for conflicts.
		ArrayDeque<Integer> queue = new ArrayDeque<>( );
		for ( int i = 0; i < fieldNumberOfPoints; i++ )
			{
			if ( p1[ i ] % 2 == 0 )
				queue.add( p1[ i ] );
			if ( p2[ i ] % 2 == 1 )
				queue.add( p2[ i ] );
			}

		// Sanity check.
		assert ( queue.size( ) == fieldNumberOfPoints ) : "Mating algorithm failed!";

		// Convert the queue to an array.
		int[ ] chromosome = new int[ fieldNumberOfPoints ];
		for ( int i = 0; i < fieldNumberOfPoints; i++ )
			chromosome[ i ] = queue.remove( );

		StateWrapper wrapper = new StateWrapper( );
		wrapper.fieldChromosome = chromosome;
		return wrapper;

	} // reproduce

}
