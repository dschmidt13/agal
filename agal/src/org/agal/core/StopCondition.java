/*
 * StopCondition.java
 * 
 * Created on Jun 11, 2013
 * 
 */
package org.agal.core;

/**
 * StopCondition is used by {@code EvolutionControlThread} to decide when the evolution
 * should stop. Whether it bases this decision on the state of the evolution while
 * listening in (for example, a solution has been found) or its own private knowledge (for
 * example, a length of time has elapsed) is up to the implementation. To notify the
 * control thread that evolution is complete, the implementation need only call
 * {@code Object.notify()}. Concrete subclasses which also implement
 * {@code EvolutionListener} will automatically be registered with the
 * {@code EvolutionAlgorithm} by the control thread before evolution begins.
 * @author David Schmidt
 */
public abstract class StopCondition
{
	// Control thread to interrupt.
	private EvolutionControlThread fieldEvolutionControlThread;


	final void setEvolutionControlThread( EvolutionControlThread evolutionControlThread )
	{
		// Note: we don't care about generic arguments here since the type of State being
		// evolved is theoretically irrelevant to controlling the evolution.

		fieldEvolutionControlThread = evolutionControlThread;

	} // setEvolutionControlThread


	/**
	 * Notifies the associated EvolutionControlThread that it's time to stop the evolution
	 * process. Does nothing if the StopCondition has not been used with an
	 * EvolutionControlThread.
	 */
	public final void stopEvolution( )
	{
		if ( fieldEvolutionControlThread != null )
			fieldEvolutionControlThread.interrupt( );

	} // stop

}
