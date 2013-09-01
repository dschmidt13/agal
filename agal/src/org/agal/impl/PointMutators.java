/*
 * PointMutators.java
 * 
 * Created on Aug 31, 2013
 * 
 */
package org.agal.impl;

import java.util.Random;

import org.agal.core.Mutator;
import org.agal.core.SearchContext;

/**
 * PointMutators is a static utility class which provides a variety of basic
 * {@link Mutator} implementations designed for primitive array state types. The mutators
 * it provides are of the "point" mutation variety; namely, to {@code mutate} they will
 * use the contextual {@code Random} to generate a new value to replace a single array
 * element.
 * <p>
 * Unless otherwise noted, all Mutators acquired herein will be both thread-safe and
 * descendants of the {@link AbstractPointMutator}. Clients should familiarize themselves
 * with its biases, which must still be present in the {@code SearchContext} for these
 * mutators to work properly.
 * @author David Schmidt
 */
public class PointMutators
{
	private static class BooleanArrayPointMutator extends AbstractPointMutator<boolean[ ]>
	{

		public BooleanArrayPointMutator( SearchContext<boolean[ ]> searchContext )
		{
			super( searchContext );

		} // BooleanArrayPointMutator


		@Override
		protected void updatePoint( boolean[ ] state, int position )
		{
			state[ position ] = getSearchContext( ).getRandom( ).nextBoolean( );

		} // updatePoint

	} // BooleanArrayPointMutator

	// Array point mutator classes.
	private static class ByteArrayPointMutator extends AbstractPointMutator<byte[ ]>
	{

		public ByteArrayPointMutator( SearchContext<byte[ ]> searchContext )
		{
			super( searchContext );

		} // ByteArrayPointMutator


		@Override
		protected void updatePoint( byte[ ] state, int position )
		{
			Random random = getSearchContext( ).getRandom( );
			byte[ ] buf = new byte[ 1 ];
			random.nextBytes( buf );
			state[ position ] = buf[ 0 ];

		} // updatePoint

	} // ByteArrayPointMutator

	private static class DoubleArrayPointMutator extends AbstractPointMutator<double[ ]>
	{
		// Use the nextGaussian function of Random or the nextDouble function?
		private final boolean fieldGaussian;


		public DoubleArrayPointMutator( SearchContext<double[ ]> searchContext, boolean gaussian )
		{
			super( searchContext );

			fieldGaussian = gaussian;

		} // DoubleArrayPointMutator


		@Override
		protected void updatePoint( double[ ] state, int position )
		{
			Random random = getSearchContext( ).getRandom( );

			if ( fieldGaussian )
				state[ position ] = random.nextGaussian( );
			else
				state[ position ] = random.nextDouble( );

		} // updatePoint

	} // DoubleArrayPointMutator

	private static class FloatArrayPointMutator extends AbstractPointMutator<float[ ]>
	{

		public FloatArrayPointMutator( SearchContext<float[ ]> searchContext )
		{
			super( searchContext );

		} // FloatArrayPointMutator


		@Override
		protected void updatePoint( float[ ] state, int position )
		{
			state[ position ] = getSearchContext( ).getRandom( ).nextFloat( );

		} // updatePoint

	} // FloatArrayPointMutator

	private static class IntArrayPointMutator extends AbstractPointMutator<int[ ]>
	{
		// An optional modulo on the random values for range limitation.
		private final Integer fieldValueRange;


		public IntArrayPointMutator( SearchContext<int[ ]> searchContext, Integer valueRange )
		{
			super( searchContext );

			fieldValueRange = valueRange;

		} // IntArrayPointMutator


		@Override
		protected void updatePoint( int[ ] state, int position )
		{
			Random random = getSearchContext( ).getRandom( );

			if ( fieldValueRange != null )
				state[ position ] = random.nextInt( fieldValueRange );
			else
				state[ position ] = random.nextInt( );

		} // updatePoint

	} // IntArrayPointMutator

	private static class LongArrayPointMutator extends AbstractPointMutator<long[ ]>
	{

		public LongArrayPointMutator( SearchContext<long[ ]> searchContext )
		{
			super( searchContext );

		} // LongArrayPointMutator


		@Override
		protected void updatePoint( long[ ] state, int position )
		{
			state[ position ] = getSearchContext( ).getRandom( ).nextLong( );

		} // updatePoint

	} // LongArrayPointMutator


	/**
	 * Creates and returns a Mutator to perform point mutations using
	 * {@code Random.nextBoolean()}.
	 * @param searchContext the SearchContext in use. Used to obtain Random instances,
	 *            bias values, etc.
	 * @return a {@code Mutator<boolean[]>} which may be used to mutate {@code boolean[]}
	 *         states.
	 */
	public static Mutator<boolean[ ]> getBooleanArrayMutator(
			SearchContext<boolean[ ]> searchContext )
	{
		return new BooleanArrayPointMutator( searchContext );

	} // getBooleanArrayMutator


	/**
	 * Creates and returns a Mutator to perform point mutations using
	 * {@code Random.nextBytes( buf )}, where {@code buf} is a 1-byte array whose only
	 * value is used as the new point value.
	 * @param searchContext the SearchContext in use. Used to obtain Random instances,
	 *            bias values, etc.
	 * @return a {@code Mutator<byte[]>} which may be used to mutate {@code byte[]}
	 *         states.
	 */
	public static Mutator<byte[ ]> getByteArrayMutator( SearchContext<byte[ ]> searchContext )
	{
		return new ByteArrayPointMutator( searchContext );

	} // getByteArrayMutator


	/**
	 * Creates and returns a Mutator to perform point mutations using
	 * {@code Random.nextDouble()} or {@code Random.nextGaussian()}.
	 * @param searchContext the SearchContext in use. Used to obtain Random instances,
	 *            bias values, etc.
	 * @param gaussian a boolean indicating whether gaussian-distributed {@code double}
	 *            values should be generated through the use of
	 *            {@code Random.nextGaussian()}.
	 * @return a {@code Mutator<double[]>} which may be used to mutate {@code double[]}
	 *         states.
	 */
	public static Mutator<double[ ]> getDoubleArrayMutator( SearchContext<double[ ]> searchContext,
			boolean gaussian )
	{
		return new DoubleArrayPointMutator( searchContext, gaussian );

	} // getDoubleArrayMutator


	/**
	 * Creates and returns a Mutator to perform point mutations using
	 * {@code Random.nextFloat()}.
	 * @param searchContext the SearchContext in use. Used to obtain Random instances,
	 *            bias values, etc.
	 * @return a {@code Mutator<float[]>} which may be used to mutate {@code float[]}
	 *         states.
	 */
	public static Mutator<float[ ]> getFloatArrayMutator( SearchContext<float[ ]> searchContext )
	{
		return new FloatArrayPointMutator( searchContext );

	} // getFloatArrayMutator


	/**
	 * Creates and returns a Mutator to perform point mutations using
	 * {@code Random.nextInt()}.
	 * @param searchContext the SearchContext in use. Used to obtain Random instances,
	 *            bias values, etc.
	 * @return a {@code Mutator<int[]>} which may be used to mutate {@code int[]} states.
	 */
	public static Mutator<int[ ]> getIntArrayMutator( SearchContext<int[ ]> searchContext )
	{
		return new IntArrayPointMutator( searchContext, null );

	} // getIntArrayMutator


	/**
	 * Creates and returns a Mutator to perform point mutations using
	 * {@code Random.nextInt( range )}.
	 * @param searchContext the SearchContext in use. Used to obtain Random instances,
	 *            bias values, etc.
	 * @param range an int specifying the modulo value to pass to the Random to limit the
	 *            range of values produced.
	 * @return a {@code Mutator<int[]>} which may be used to mutate {@code int[]} states.
	 */
	public static Mutator<int[ ]> getIntArrayMutator( SearchContext<int[ ]> searchContext, int range )
	{
		return new IntArrayPointMutator( searchContext, range );

	} // getIntArrayMutator


	/**
	 * Creates and returns a Mutator to perform point mutations using
	 * {@code Random.nextLong()}.
	 * @param searchContext the SearchContext in use. Used to obtain Random instances,
	 *            bias values, etc.
	 * @return a {@code Mutator<long[]>} which may be used to mutate {@code long[]}
	 *         states.
	 */
	public static Mutator<long[ ]> getLongArrayMutator( SearchContext<long[ ]> searchContext )
	{
		return new LongArrayPointMutator( searchContext );

	} // getLongArrayMutator

}
