/*
 * TimedStopCondition.java
 * 
 * Created on Jun 25, 2013
 * 
 */
package org.agal.core.impl;

import java.util.Timer;
import java.util.TimerTask;

import org.agal.core.EvolutionListener;
import org.agal.core.StopCondition;

/**
 * TimedStopCondition is a simple StopCondition that will trigger termination of the
 * evolution process once an allotted amount of time has elapsed.
 * @author Dave
 */
public class TimedStopCondition extends StopCondition
{
	private class StopTask extends TimerTask
	{
		@Override
		public void run( )
		{
			TimedStopCondition.this.stopEvolution( );

		} // run

	} // StopTask

	// Data members.
	private long fieldTimeToWaitMillis;
	private Timer fieldTimer;


	/**
	 * Constructs a TimedStopCondition which will trigger the evolution process to halt
	 * after the specified number of milliseconds.
	 * @param millis a {@code long} indicating the number of milliseconds to wait before
	 *            stopping the evolution.
	 */
	public TimedStopCondition( long millis )
	{
		fieldTimer = new Timer( true );
		fieldTimeToWaitMillis = millis;

	} // TimedStopCondition


	public void cancel( )
	{
		fieldTimer.cancel( );

	} // cancel


	@Override
	public void onEvent( int eventId, Object eventObject )
	{
		if ( eventId == EvolutionListener.EVENT_ID_BEGIN_EVOLUTION )
			fieldTimer.schedule( new StopTask( ), fieldTimeToWaitMillis );

		if ( eventId == EvolutionListener.EVENT_ID_END_EVOLUTION )
			cancel( );

	} // onEvent

}
