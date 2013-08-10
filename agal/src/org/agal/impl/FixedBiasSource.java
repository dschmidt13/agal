/*
 * FixedBiasSource.java
 * 
 * Created on Jul 4, 2013
 * 
 */
package org.agal.impl;

import org.agal.core.BiasSource;

/**
 * FixedBiasSource maintains an unchanging bias value. Calls to {@code getBias} will
 * always return the same bias, regardless of the {@code biasKey} given. This streamlined
 * behavior is designed to be coupled with {@link CompoundBiasSource} for increased
 * control over when a given fixed bias value is returned.
 * <p>
 * FixedBiasSource is thread safe.
 * @author David Schmidt
 */
public class FixedBiasSource implements BiasSource
{
	// Data members.
	private final double fieldBias;


	/**
	 * FixedBiasSource constructor.
	 * @param bias a double containing the value that this BiasSource should always
	 *            return.
	 */
	public FixedBiasSource( double bias )
	{
		fieldBias = bias;

	} // FixedBiasSource


	@Override
	public double getBias( String biasKey )
	{
		return fieldBias;

	} // getBias

}
