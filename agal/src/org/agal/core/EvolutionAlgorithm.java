/*
 * EvolutionAlgorithm.java
 * 
 * Created on Jun 13, 2013
 * 
 */
package org.agal.core;

/**
 * EvolutionAlgorithm TODO (note - must be threadsafe!)
 * @author Dave
 */
public interface EvolutionAlgorithm
{
	public abstract void evolve( );


	public abstract void registerListener( EvolutionListener listener );

}
