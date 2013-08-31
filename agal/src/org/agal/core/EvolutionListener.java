/*
 * EvolutionListener.java
 * 
 * Created on Jun 11, 2013
 * 
 */
package org.agal.core;

/**
 * EvolutionListener
 * @author Dave
 */
public interface EvolutionListener
{
	// LAM - These constants should go elsewhere (perhaps an enum), but until I've decided
	// on whether to keep this event style they'll go here.
	public static final String EVENT_ID_BEGIN_EVOLUTION = "BeginEvolution";
	public static final String EVENT_ID_END_EVOLUTION = "EndEvolution";
	public static final String EVENT_ID_NEW_GENERATION = "NewGeneration";
	public static final String EVENT_ID_MEMBER_ADDED_TO_POPULATION = "MemberAddedToPopulation";
	public static final String EVENT_ID_MEMBER_REMOVED_FROM_POPULATION = "MemberRemovedFromPopulation";


	public void onEvent( String eventKey, Object eventObject );

}
