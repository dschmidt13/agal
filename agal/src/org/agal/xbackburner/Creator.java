/*
 * Creator.java
 * 
 * Created on Aug 24, 2013
 * 
 */
package org.agal.xbackburner;

/**
 * Creator is responsible for instantiating new instances of the state type {@code S}. It
 * must be able to randomly create new instances for the population.
 * @author Dave
 */
public interface Creator<S>
{
	/**
	 * Create a fresh state of type {@code S}.
	 */
	public S create( );

}
