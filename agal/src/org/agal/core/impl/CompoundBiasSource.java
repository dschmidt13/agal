/*
 * CompoundBiasSource.java
 * 
 * Created on Jul 4, 2013
 * 
 */
package org.agal.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.agal.core.BiasSource;

/**
 * CompoundBiasSource is a BiasSource implementation which allows several different
 * simpler BiasSources to be joined together into one handler to be used by the evolution
 * algorithm. It delegates actual {@code getBias} calls to handlers that have been
 * registered to it with
 * @author Dave
 */
public class CompoundBiasSource implements BiasSource
{
	// Data members.
	private Map<Integer, BiasSource> fieldBiasSources;
	private double fieldDefaultBias = 0.0;


	/**
	 * CompoundBiasSource constructor.
	 */
	public CompoundBiasSource( )
	{
		// LAM - Concurrency?
		fieldBiasSources = new HashMap<>( );

	} // CompoundBiasSource


	public CompoundBiasSource( double defaultBias )
	{
		this( );

		fieldDefaultBias = defaultBias;

	} // CompoundBiasSource


	public void addBiasSource( BiasSource biasSource, int biasCode )
	{
		fieldBiasSources.put( biasCode, biasSource );

	} // addBiasSource


	@Override
	public double getBias( int biasCode )
	{
		BiasSource handler = fieldBiasSources.get( biasCode );

		// LAM - Allow custom default bias value?
		return ( handler == null ) ? fieldDefaultBias : handler.getBias( biasCode );

	} // getBias

}
