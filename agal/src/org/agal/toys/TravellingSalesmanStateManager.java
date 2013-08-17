/*
 * TravellingSalesmanStateManager.java
 * 
 * Created on Aug 10, 2013
 * 
 */
package org.agal.toys;

import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import org.agal.core.AbstractFitnessEvaluator;
import org.agal.core.StateManager;
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
		int[ ] fieldChromosome1;
		int[ ] fieldChromosome2;

	}

	// Points to travel between.
	private Point2D.Double[] fieldPoints;
	private int[ ] fieldBaseState;
	private int fieldNumberOfPoints;


	public TravellingSalesmanStateManager( int numberOfPoints, double bounds )
	{
		super( true );

		fieldPoints = new Point2D.Double[ numberOfPoints ];
		fieldBaseState = new int[ numberOfPoints ];

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

		// TODO

	} // TravellingSalesmanStateManager


	public static void main( String... args )
	{
		TravellingSalesmanStateManager sm = new TravellingSalesmanStateManager( 10, 50.0 );

		List<StateWrapper> testStates = new ArrayList<>( );
		for ( int i = 0; i < 5; i++ )
			testStates.add( sm.randomize( ) );

		for ( StateWrapper state : testStates )
			sm.mutate( state );

		for ( StateWrapper state : testStates )
			sm.fitness( state );

	} // main


	@Override
	public Integer fitness( StateWrapper wrapper )
	{
		if ( wrapper.fieldFitness == null )
			{
			int[ ] chromosome1 = wrapper.fieldChromosome1;
			int[ ] chromosome2 = wrapper.fieldChromosome2;
			double totalDistance = 0;
			Point2D.Double previousPoint = fieldPoints[ chromosome1[ 0 ] ];
			Deque<Integer> stack = new ArrayDeque<>( );
			for ( int index = 1; index < fieldNumberOfPoints; index++ )
				{
				// FIXME - Does not force path to touch all points.
				Point2D.Double nextPoint = ( index % 2 == 0 ) ? fieldPoints[ chromosome1[ index ] ]
						: fieldPoints[ chromosome2[ index ] ];
				totalDistance += previousPoint.distance( nextPoint );
				previousPoint = nextPoint;
				}
			wrapper.fieldFitness = ( int ) totalDistance * 1000; // fixed point fakeout
			}

		return wrapper.fieldFitness;

	} // fitness


	@Override
	public StateWrapper mutate( StateWrapper wrapper )
	{
		randomSwap( wrapper.fieldChromosome1 );

		return wrapper;

	} // mutate


	@Override
	public StateWrapper randomize( )
	{
		int[ ] chromosome1 = Arrays.copyOf( fieldBaseState, fieldNumberOfPoints );
		int[ ] chromosome2 = Arrays.copyOf( fieldBaseState, fieldNumberOfPoints );

		// Avoid a 0-length bug due to the +1.
		if ( fieldNumberOfPoints > 0 )
			{
			// Shuffle half the list into the whole list.
			for ( int index = 0; index < fieldNumberOfPoints / 2 + 1; index++ )
				{
				randomSwap( chromosome1 );
				randomSwap( chromosome2 );
				}
			}

		StateWrapper wrapper = new StateWrapper( );
		wrapper.fieldChromosome1 = chromosome1;
		wrapper.fieldChromosome2 = chromosome2;

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
		StateWrapper wrapper = new StateWrapper( );
		wrapper.fieldChromosome1 = Arrays.copyOf( mother.fieldChromosome1, fieldNumberOfPoints );
		wrapper.fieldChromosome2 = Arrays.copyOf( father.fieldChromosome2, fieldNumberOfPoints );

		return wrapper;

	} // reproduce

}
