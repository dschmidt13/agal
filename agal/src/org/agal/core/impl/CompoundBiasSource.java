/*
 * CompoundBiasSource.java
 * 
 * Created on Jul 4, 2013
 * 
 */
package org.agal.core.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.agal.core.BiasSource;

/**
 * CompoundBiasSource is a BiasSource implementation which allows several different
 * simpler BiasSources to be joined together into one handler to be used by the evolution
 * algorithm. It delegates actual {@code getBias} calls to handlers that have been
 * registered to it with {@code setBiasSource}. Handlers may be removed via
 * {@code removeBiasSource}.
 * <p>
 * CompoundBiasSource is thread safe.
 * @author David Schmidt
 */
public class CompoundBiasSource implements BiasSource
{
	// Data members.
	private Map<Integer, BiasSource> fieldBiasSources = new ConcurrentHashMap<>( );
	private double fieldDefaultBias = 0.0;


	public CompoundBiasSource( )
	{
	} // CompoundBiasSource


	public CompoundBiasSource( double defaultBias )
	{
		fieldDefaultBias = defaultBias;

	} // CompoundBiasSource


	@Override
	public double getBias( int biasCode )
	{
		BiasSource handler = fieldBiasSources.get( biasCode );

		return ( handler == null ) ? fieldDefaultBias : handler.getBias( biasCode );

	} // getBias


	public void removeBiasSource( int biasCode )
	{
		fieldBiasSources.remove( biasCode );

	} // removeBiasSource


	public void setBiasSource( BiasSource biasSource, int biasCode )
	{
		fieldBiasSources.put( biasCode, biasSource );

	} // setBiasSource

}
