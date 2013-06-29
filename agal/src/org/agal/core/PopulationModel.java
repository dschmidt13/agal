/*
 * PopulationModel.java
 * 
 * Created on Jul 8, 2012
 * 
 */
package org.agal.core;

/**
 * PopulationModel represents a genetic algorithm population which may be implemented in
 * one of many ways and which may even be altered on the fly by the environment.
 * Implementations must be thread safe.
 * <p>
 * TODO - Notes on thread safety (see EvolutionControlThread).
 * @author David Schmidt
 */
public interface PopulationModel<S>
{

	// TODO - Rename methods to be intuitive in both the collection and the jumbled
	// population sense.

	/**
	 * Indicates to the population that it is no longer (and will never again be) in use
	 * by any other thread, and that it may safely perform any necessary cleanup tasks.
	 */
	public abstract void destroy( );


	/**
	 * Prepares a population model for all manner of requests. Only to be used on freshly
	 * constructed (uninitialized) populations. By returning from this method, the
	 * PopulationModel indicates that it is prepared to service requests.
	 * @param stateManager a StateManager which may be used to generate the initial
	 *            population. If further use of the StateManager is required by the
	 *            PopulationModel, it may be saved.
	 * @param populationSize an int indicating the number of members to be in the
	 *            population to begin with. The implementation may treat this as a
	 *            guideline more than an exact number, but regardless it must be ready to
	 *            produce population members if they are requested by {@code sample} or
	 *            {@code reap}.
	 */
	public abstract void initialize( StateManager<S> stateManager, int populationSize );


	/**
	 * Indicates to the PopulationModel that it's time to move to the next generation.
	 * Different implementations may handle this differently; for example, a static
	 * population will take this as a cue to replace the old static population with the
	 * new one, while a dynamic population may simply use it for tracking statistics.
	 */
	public abstract void nextGeneration( );


	/**
	 * Removes a state at random from the population. If more than one population is being
	 * managed (eg, if the generations are discrete), the state will be removed from the
	 * parent population.
	 * @return a state S which was hitherto a member of the population.
	 */
	public abstract S reap( );


	/**
	 * Looks up a state at random in the population and returns it, but does not remove
	 * it. If more than one population is being managed (eg, if the generations are
	 * discrete), the state is obtained from the parent population.
	 * @return a state S which is a member of the population.
	 */
	public abstract S sample( );


	/**
	 * @return an int indicating the size of the active generation. Implementations may
	 *         choose whether the size of the parent or child generation is more helpful
	 *         to their algorithm.
	 */
	public abstract int size( );


	/**
	 * Adds a state to the population. If more than one population is being managed (eg,
	 * if the generations are discrete), the state will be added to the child population.
	 * @param member a state S to add to the population.
	 */
	public abstract void sow( S member );


	/**
	 * Returns a number indicating the number of elements in the current generation. Even
	 * in growable populations with generational mixing, this will help keep the
	 * population around the right size. Implementations may vary this arbitrarily if they
	 * wish, but EvolutionAlgorithms are not guaranteed to respect changes immediately or
	 * at all.
	 * @return an int indicating the number of members that are considered to constitute a
	 *         generation.
	 */
	public abstract int getGenerationSize( );

}
