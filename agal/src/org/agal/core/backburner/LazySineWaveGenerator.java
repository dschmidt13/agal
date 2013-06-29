/*
 * LazySineWaveGenerator.java
 * 
 * Created on Jul 18, 2012
 * 
 */
package org.agal.core.backburner;

/**
 * LazySineWaveGenerator approximates a sine wave with a given wavelength in milliseconds.
 * To improve performance, a {@code granularity} is used, and the sine value is not
 * calculated on each call to determine its value, but rather is cached, the cached value
 * expiring every {@code granularity} milliseconds; this is what makes the class "Lazy."
 * <p>
 * Note that for the approximated sine curve to be very smooth, higher ratios of
 * {@code wavelength:granularity} are necessary. In the case of the Windows OS, this means
 * that the wavelength must be large enough to compensate for the fact that the Windows OS
 * only seems to report changes in the system clock every 15-16ms.
 * @author David Schmidt
 */
public class LazySineWaveGenerator
{
	// Constants.
	// LAM - Forcibly impose these minimums?
	private static final long ABSOLUTE_MINIMUM_GRANULARITY_MILLIS = 16;
	private static final long ABSOLUTE_MINIMUM_WAVELENGTH_MILLIS = 2 * ABSOLUTE_MINIMUM_GRANULARITY_MILLIS;

	// Data members.
	private final long fieldWavelengthMillis;
	private final double fieldFrequency;

	private final long fieldGranularityMillis;
	private final double fieldPremultipliedTerm;

	// Let's try these as nonvolatile for now. Explore ThreadLocal, volatile, etc. later.
	private long fieldLastUpdate = 0;
	private double fieldSineValue = 0;


	/**
	 * LazySineWaveGenerator constructor.
	 */
	public LazySineWaveGenerator( long wavelengthMillis, long granularityMillis )
	{
		fieldWavelengthMillis = Math.max( ABSOLUTE_MINIMUM_WAVELENGTH_MILLIS, wavelengthMillis );
		fieldFrequency = 1 / ( double ) fieldWavelengthMillis;
		fieldGranularityMillis = Math.max( ABSOLUTE_MINIMUM_GRANULARITY_MILLIS, granularityMillis );

		// For a wavelength of 3600, eg, 2pi/3600 produces a value which, when multiplied
		// by the current time and passed into a sine function, results in the correct
		// wavelength. (Use a graph to prove it if you wish.) This reduces the amount of
		// math required for each new calculation of the wave state.
		fieldPremultipliedTerm = 2 * Math.PI / fieldGranularityMillis;

	} // LazySineWaveGenerator


	public double getSineValue( )
	{
		// LAM - Allow a granularity of 0, ie. live wave?
		if ( ( System.currentTimeMillis( ) - fieldLastUpdate ) > fieldGranularityMillis )
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
		fieldSineValue = StrictMath.sin( newTime * fieldPremultipliedTerm );
		fieldLastUpdate = newTime;

	} // update

}
