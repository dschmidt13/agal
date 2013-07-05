/*
 * FluctuatingBiasSource.java
 * 
 * Created on Jul 4, 2013
 * 
 */
package org.agal.core.impl;

import org.agal.core.BiasSource;

/**
 * FluctuatingBiasSource varies its bias to fit the curve of a time-driven sine wave to
 * simulate environments that fluctuate independently with respect to time. The wave
 * itself will be shifted up on the y-axis by 1 so it never becomes negative, oscillating
 * between 0 and 2. For this reason, an {@code amplitudeScalar} is specified to adjust the
 * final value to the user's desired bias range.
 * <p>
 * A dynamic equation-based bias source is on the To-do list.
 * @author Dave
 */
public class FluctuatingBiasSource implements BiasSource
{
	// TODO - Make a version that can handle all sorts of equations dynamically...
	// while somehow remaining lightweight.

	// Data members.
	private LazySineWaveGenerator fieldSineWaveGenerator;
	private double fieldAmplitudeScalar;


	/**
	 * FluctuatingBiasSource constructor.
	 * @param wavelengthMillis a long indicating the desired wavelength in ms. Note that
	 *            on Windows machines, the system clock resolution seems to be around
	 *            15-16ms (especially for short-lived timers), so wavelengths at this or
	 *            lower probably won't model a wave very closely. For decent results, a
	 *            wavelength longer that allows at least a few of these sampling windows
	 *            within it will more closely approximate a wave.
	 * @param granularityMillis a long indicating the frequency with which new values are
	 *            calculated. Must be greater than 0. Larger values will result in rougher
	 *            wave approximations but less time spent calculating. Also as noted
	 *            above, the minimum resolution for this on Windows machines may appear to
	 *            be 15-16ms, but this limit is not imposed by this implementation.
	 * @param amplitudeScalar a double which is multiplied to adjust generated sine wave
	 *            values to the user's desired bias range before returning them.
	 */
	public FluctuatingBiasSource( long wavelengthMillis, long granularityMillis,
			double amplitudeScalar )
	{
		fieldSineWaveGenerator = new LazySineWaveGenerator( wavelengthMillis, granularityMillis );

	} // FluctuatingBiasSource


	@Override
	public double getBias( int biasCode )
	{
		return fieldSineWaveGenerator.getSineValue( ) * fieldAmplitudeScalar;

	} // getBias

}
