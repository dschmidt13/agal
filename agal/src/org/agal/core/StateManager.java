/*
 * StateManager.java
 * 
 * Created on Jun 29, 2013
 * 
 */
package org.agal.core;

/**
 * StateManager
 * @author Dave
 */
public interface StateManager<S>
{

	public double fitness( S individual );


	public S mutate( S original );


	public S randomize( );


	public S reproduce( S mother, S father );

}
