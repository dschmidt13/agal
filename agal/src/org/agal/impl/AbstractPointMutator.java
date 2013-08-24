/*
 * AbstractPointMutator.java
 * 
 * Created on Aug 24, 2013
 * 
 */
package org.agal.impl;

import java.lang.reflect.Array;
import java.util.Random;

import org.agal.core.Mutator;
import org.agal.core.SearchContext;

/**
 * AbstractPointMutator is a convenience {@link Mutator} implementation for all
 * array-based states whose mutations can be implemented as updating a single element at a
 * position in a state array. While no compiler-checked constraints can exist on the type
 * {@code S}, it <b>must</b> be an array type or an {@code IllegalArgumentException} will
 * be thrown at runtime.
 * <p>
 * In addition to randomizing the position to mutate, this class also uses the array
 * length and {@link #BIAS_KEY_ELEMENTS_PER_MUTATION} to provides a mechanism to
 * dynamically scale the number of mutations performed. Use of this feature is optional;
 * to use it, one must define a {@code BiasSource} for the aforementioned key.
 * <p>
 * AbstractPointMutator is thread safe.
 * @author David Schmidt
 */
public abstract class AbstractPointMutator<S> extends AbstractBiasedMutator<S>
{
	// Class constants.
	/**
	 * The bias retrieved by this key will be treated as an integer {@code n} as in the
	 * statement {@code m mutations per n elements}. The {@code m} is the mutation count
	 * as determined by {@code AbstractBiasedMutator}. This bias allows one to dynamically
	 * scale the number of mutations performed based on the size of the genome. If this
	 * bias is not present, no further {@code mutateCount} adjustments will be made after
	 * the value is obtained from the superclass.
	 * <p>
	 * Note that with this implementation, while the overall boosted mutation rate will
	 * approximately follow the suggested rate (uneven division of lengths aside), the
	 * mutations are likely to come in bursts on individual states rather than being
	 * spread out evenly over the entire population.
	 */
	public static final String BIAS_KEY_ELEMENTS_PER_MUTATION = AbstractPointMutator.class
			.getName( ) + ".elementsPerMutation";


	public AbstractPointMutator( SearchContext<S> searchContext )
	{
		super( searchContext );

	} // AbstractPointMutator


	@Override
	public void mutate( S state )
	{
		// This may throw an IllegalArgumentException. We're placing a lot of trust in the
		// client to hook it up correctly.
		int length = Array.getLength( state );

		// Using the SearchContext to get a random so clients don't have to know too
		// much about the framework out of the box is half the point of this class.
		Random random = getSearchContext( ).getRandom( );

		// Determine a point to mutate, and mutate!
		int mutatePosition = random.nextInt( length );
		updatePoint( state, mutatePosition );

	} // mutate


	/**
	 * Obtains the {@code mutateCount} from {@link AbstractBiasedMutator} and, if
	 * necessary, modifies it based on the length of the state array according to
	 * {@link #BIAS_KEY_ELEMENTS_PER_MUTATION}.
	 * @see org.agal.impl.AbstractBiasedMutator#mutateCount(java.lang.Object)
	 */
	@Override
	public int mutateCount( S state )
	{
		int count = super.mutateCount( state );

		// LAM - Perhaps a better way of calculating this would be to include the base
		// mutation rate in the calculation? For instance, given a rate of 0.2 mutations
		// per 1000 loci and a state with 5000 loci, it might always mutate exactly once.
		// The end result is the same, but the mutations would be spread out consistently
		// rather than coming in bursts as they would now.
		// Shortcut for infrequently mutating evolutions.
		if ( count == 0 )
			return 0;

		// This may also throw an IllegalArgumentException if the client doesn't use
		// array-based states.
		int arrayLength = Array.getLength( state );

		int multiplier = 1;
		try
			{
			int elementsPerMutation = ( int ) getSearchContext( ).getBias(
					BIAS_KEY_ELEMENTS_PER_MUTATION );
			multiplier += arrayLength / elementsPerMutation;
			}
		catch ( Exception ignored )
			{
			// FIXME - This makes the exception policy of CompoundBiasSource seem broken.
			// This is to account for the possibility of a CompoundBiasSource with no key
			// mapping and no default.
			}

		return ( count * multiplier );

	} // mutateCount


	/**
	 * Mutates the array at the given position. The pre-randomized position is merely a
	 * courtesy, but most implementations should be satisfied with it.
	 * @param state a state of type {@code S} (and is an array) to be mutated.
	 * @param position a int containing a pre-randomized position at which to update the
	 *            state array. Guaranteed to be within the bounds of the state array.
	 */
	protected abstract void updatePoint( S state, int position );

}
