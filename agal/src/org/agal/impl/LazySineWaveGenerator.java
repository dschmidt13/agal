/*
 * LazySineWaveGenerator.java
 * 
 * Created on Jul 18, 2012
 * 
 */
package org.agal.impl;

/**
 * LazySineWaveGenerator approximates a basic sine wave with a given wavelength in
 * milliseconds. This wave has an amplitude of 1 and is centered on the x-axis. To improve
 * performance, a {@code granularity} is specified, and the sine value is not calculated
 * on each call to determine its value, but rather is cached, the cached value expiring
 * every {@code granularity} milliseconds and <i>only being recalculated on request rather
 * than actively when the old value expires</i>; this is what makes the class "Lazy."
 * However, for the approximated sine curve to be very smooth, higher ratios of
 * {@code wavelength:granularity} are necessary. For the smoothest curve, a granularity of
 * {@code 1} may be used in some environments (see note below).
 * <p>
 * <b>Please note that when using this class on any Windows OS,</b> the wavelength may
 * need to be several times greater than 15-16ms to get any decent wave approximation,
 * because the Windows OS appears to only report changes to the system clock every
 * 15-16ms, making that a sort of default minimum granularity. This limit is NOT imposed
 * nor enforced by this class, but is merely a consequence of the mysterious inner
 * workings of Windows. (Linux does not appear to have any such limitation.) Clients of
 * this library are encouraged to test their own system clock resolution before choosing a
 * wavelength here. <i>This class makes no guarantees about the quality of its performance
 * if the wavelength specified is smaller than the resolution of the user's system
 * clock.</i>
 * @author David Schmidt
 */
public class LazySineWaveGenerator
{
	// Data members.
	private final long fieldWavelengthMillis;
	private final double fieldFrequency;

	private final long fieldGranularityMillis;
	private final double fieldPremultipliedTerm;

	// LAM - Thread safety - see update().
	private volatile long fieldLastUpdate = 0;
	private volatile double fieldSineValue = 0;


	/**
	 * LazySineWaveGenerator constructor.
	 * @param wavelengthMillis a long indicating the wavelength in milliseconds to
	 *            approximate.
	 * @param granularityMillis a long indicating how frequently (in ms) the cached sine
	 *            value should be updated. A value of {@code 1ms} (the lowest possible
	 *            granularity for this class) will ensure that the approximation is as
	 *            smooth as possible. Any value less than {@code 1ms} will be treated as
	 *            {@code 1ms}.
	 */
	public LazySineWaveGenerator( long wavelengthMillis, long granularityMillis )
	{
		if ( wavelengthMillis <= 0 )
			throw new IllegalArgumentException( "Wavelength must be greater than 0." );

		fieldWavelengthMillis = wavelengthMillis;
		fieldFrequency = 1 / ( double ) fieldWavelengthMillis;
		fieldGranularityMillis = Math.max( 1, granularityMillis );

		// For a wavelength of 3600, eg, 2pi/3600 produces a value which, when multiplied
		// by the current time and passed into a sine function, results in the correct
		// wavelength. (Use a graph to prove it if you wish.) This reduces the amount of
		// math required for each new calculation of the wave state.
		fieldPremultipliedTerm = 2.0 * Math.PI / fieldWavelengthMillis;

	} // LazySineWaveGenerator


	public double getSineValue( )
	{
		if ( ( System.currentTimeMillis( ) - fieldLastUpdate ) >= fieldGranularityMillis )
			update( );

		return fieldSineValue;

	} // getSineValue


	private void update( )
	{
		long newTime = System.currentTimeMillis( );

		// Broken invariants don't matter so much here, because anyone contending will
		// have a similar time and a similar value (assuming wavelength isn't super
		// small), and really this class is all about *approximating* a sine wave. Plus,
		// contention will hopefully be rare since these fields aren't protected by memory
		// fences.
		// FIXME - Couldn't someone see a fresh lastUpdate time but a stale sine value? Or
		// does the argument above hold: ie, How long could this class go being out of
		// sync in that way without hitting a full memory fence?
		fieldSineValue = StrictMath.sin( newTime * fieldPremultipliedTerm );
		fieldLastUpdate = newTime;

	} // update

}
