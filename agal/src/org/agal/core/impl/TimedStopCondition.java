/*
 * TimedStopCondition.java
 * 
 * Created on Jun 25, 2013
 * 
 */
package org.agal.core.impl;

import org.agal.core.EvolutionListener;
import org.agal.core.StopCondition;

/**
 * TimedStopCondition is a simple StopCondition that will trigger termination of the
 * evolution process once an allotted amount of time has elapsed.
 * @author Dave
 */
public class TimedStopCondition extends StopCondition
{
	// Data members.

	/**
	 * Constructs a TimedStopCondition which will trigger the evolution process to halt
	 * after the specified number of milliseconds.
	 * @param millis a {@code long} indicating the number of milliseconds to wait before
	 *            stopping the evolution.
	 */
	public TimedStopCondition( long millis )
	{
		// TODO

	} // TimedStopCondition


	public void cancel( )
	{
		// TODO

	} // cancel


	@Override
	public void onEvent( int eventId, Object eventObject )
	{
		// TODO
		if ( eventId == EvolutionListener.EVENT_ID_BEGIN_EVOLUTION )
			;

	} // onEvent

}
