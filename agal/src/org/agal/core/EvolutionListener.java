/*
 * EvolutionListener.java
 * 
 * Created on Jun 11, 2013
 * 
 */
package org.agal.core;

/**
 * EvolutionListener
 * @author David Schmidt
 */
public interface EvolutionListener
{
	// LAM - These constants should go elsewhere (perhaps an enum), but until I've decided
	// on whether to keep this event style they'll go here.
	public static final int EVENT_ID_BEGIN_EVOLUTION = 0;
	public static final int EVENT_ID_END_EVOLUTION = 1;
	public static final int EVENT_ID_NEW_GENERATION = 2;
	public static final int EVENT_ID_MEMBER_ADDED_TO_POPULATION = 3;
	public static final int EVENT_ID_MEMBER_REMOVED_FROM_POPULATION = 4;

	public void onEvent( int eventId, Object eventObject );

}
