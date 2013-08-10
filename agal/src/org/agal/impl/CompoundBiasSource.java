/*
 * CompoundBiasSource.java
 * 
 * Created on Jul 4, 2013
 * 
 */
package org.agal.impl;

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
	private final ConcurrentHashMap<String, BiasSource> fieldBiasSources = new ConcurrentHashMap<>( );
	private final BiasSource fieldDefaultBiasSource;


	public CompoundBiasSource( )
	{
		fieldDefaultBiasSource = null;

	} // CompoundBiasSource


	public CompoundBiasSource( BiasSource defaultBiasSource )
	{
		fieldDefaultBiasSource = defaultBiasSource;

	} // CompoundBiasSource


	/**
	 * Retrieves the bias value for the given {@code biasKey} from the BiasSource
	 * registered to that key. If no BiasSource has been registered to that key and there
	 * is no default BiasSource, an {@code IllegalArgumentException} is thrown to indicate
	 * that no suitable value for the given bias may be supplied. I believe this immediate
	 * failure feedback to be preferable to supplying a default bias value, which would
	 * "run" but probably not in the end fulfil the genetic search as desired. Should one
	 * wish to bypass this policy, one may construct this object with a "default" bias
	 * source, to which all unregistered bias requests will be passed instead.
	 * @throws IllegalArgumentException if no BiasSource with the given {@code biasKey} is
	 *             registered with this BiasSource, and this BiasSource does not have a
	 *             default BiasSource.
	 * @see org.agal.core.BiasSource#getBias(java.lang.String)
	 */
	@Override
	public double getBias( String biasKey )
			throws IllegalArgumentException
	{
		BiasSource registeredSource = fieldBiasSources.get( biasKey );

		// Good chance.
		if ( registeredSource != null )
			return registeredSource.getBias( biasKey );

		// Probably small chance.
		if ( fieldDefaultBiasSource != null )
			return fieldDefaultBiasSource.getBias( biasKey );

		// Default solution.
		throw new IllegalArgumentException( "Unregistered biasKey '" + biasKey + "'." );

	} // getBias


	/**
	 * Unregisters the BiasSource associated with the given {@code biasKey}, if one was
	 * registered. It will no longer be used to handle bias requests for that key.
	 * @param biasKey a String containing the key to be unmapped from its corresponding
	 *            BiasSource.
	 */
	public void removeBiasSource( String biasKey )
	{
		fieldBiasSources.remove( biasKey );

	} // removeBiasSource


	/**
	 * Registers the given BiasSource with the given {@code biasKey}, causing all requests
	 * for the bias with the given {@code biasKey} to {@code this} BiasSource to be passed
	 * on to the given {@code biasSource}.
	 * @param biasKey a String which is a key for which the given source must supply bias
	 *            values.
	 * @param biasSource a BiasSource to handle requests for the given key.
	 */
	public void setBiasSource( String biasKey, BiasSource biasSource )
	{
		fieldBiasSources.put( biasKey, biasSource );

	} // setBiasSource

}
