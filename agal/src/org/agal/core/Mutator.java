/*
 * Mutator.java
 * 
 * Created on Aug 23, 2013
 * 
 */
package org.agal.core;

/**
 * Mutator defines a simple interface to modify a given problem state of type {@code S}.
 * Mutators are used by evolutionary algorithms as a decoupled way of allowing clients to
 * configure the rate, type, and intensity of mutations occurring during their evolution.
 * A variety of common mutations are included in {@code AGAL} for clients to avail without
 * the need to implement their own, but custom implementations are of course permitted.
 * Many of the provided implementations take advantage of the {@link BiasSource} feature.
 * <p>
 * Mutators are generally singleton within an EA, so implementations must be thread-safe.
 * @author David Schmidt
 */
public interface Mutator<S>
{
	/**
	 * Mutates the given state <b>in place</b>. {@code Mutator} implementations provided
	 * by {@code AGAL} usually will do this randomly, but custom implementations may
	 * choose to mutate intelligently in some cases, for instance if a threshold fitness
	 * is reached where a single solution is nearby and subsequent random mutations are
	 * very likely to be detrimental.
	 * <p>
	 * As a general rule, a single call to {@code mutate} should cause the smallest
	 * possible discrete "step" of its type, whether this step is a random point mutation
	 * or an intelligent step toward a goal. Larger steps should be attained instead by
	 * increasing the value returned by {@code mutateCount}. This strategy should foster
	 * fine performance tuning and assist with evolution tracking.
	 * @param state a state of type {@code S} who is to be mutated.
	 */
	public void mutate( S state );


	/**
	 * Determines how many times a given state should be mutated. This determination may
	 * be random, vary by call, and need not consider the {@code state} for its decision,
	 * although it may choose to. Premade {@code Mutator} implementations often will not
	 * care about the specific state when deciding whether and how severely to mutate it.
	 * <p>
	 * However, a custom implementation may choose to employ part of a heuristic search
	 * here for high-fitness states, mutating "intelligently" to aggressively converge on
	 * a nearby solution rather than randomly wandering toward (or away from) it. In such
	 * a case, the {@code Mutator} should attempt to estimate how many discrete mutation
	 * steps it wants to take toward that goal rather than performing all of them at once
	 * during a single {@code mutate}. (This should ease performance tuning, trace logging
	 * of the evolution, etc.)
	 * @param state a state of type {@code S} whose values may or may not impact the
	 *            decision.
	 * @return an {@code int} indicating how many times the algorithm should call
	 *         {@link #mutate(Object)} on the given state.
	 */
	public int mutateCount( S state );

}
