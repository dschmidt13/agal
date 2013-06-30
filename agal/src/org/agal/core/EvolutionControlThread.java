/*
 * EvolutionControlThread.java
 * 
 * Created on Jul 7, 2012
 * 
 */
package org.agal.core;

import java.util.concurrent.CountDownLatch;

/**
 * EvolutionControlThread is a threaded container which allows for multithreading or
 * backgrounding an evolutionary problem solver (like the type this library is designed to
 * scaffold). Please be aware of the thread safety policy of the objects you use with this
 * class.
 * <p>
 * EvolutionControlThread's {@code evolve} spawns a control thread to manage the problem
 * and a specified number of threads to execute the "evolution" stage: in simple terms,
 * the search for a solution (although it is possible to implement a problem that evolves
 * for a given length of time and then stops, regardless of the progress of the search).
 * The control thread is returned by {@code evolve} after it has been started, so the
 * caller may choose to simply wait for the evolution to complete and the thread's
 * execution to terminate once the given {@link StopCondition} is met (via
 * {@code Thread.join}, or it may run unrelated tasks on its own while it waits. The
 * entire evolutionary process may be halted with a call to {@code Thread.interrupt} on
 * the control thread; this may be favored in simple timed evolution cases over
 * implementing a custom {@code StopCondition}.
 * <p>
 * <i>A note on liveness:</i> interruption is not immediate, as worker threads may require
 * a moment to finish their current tasks and perform cleanup before stopping; therefore,
 * any interruptions should be followed by a call to {@code Thread.join} on the control
 * thread, which as a convenience <b>will wait indefinitely</b> on each of its workers to
 * die before it does. This whole process should be nearly instantaneous; unless an
 * {@link EvolutionListener} or {@link StateManager} is behaving especially poorly, this
 * should never be a livelock risk. In an emergency, a second interrupt <i>may</i> be used
 * on the control thread as a signal to abandon its workers, but this will leave the
 * worker threads and any listeners and data they depend on in an unknown state. It is
 * advised that clients avoid this practice and instead program their listeners and state
 * managers to be either relatively fast or responsive to interrupts.
 * <p>
 * <h2>Thread Safety Policy</h2>All EvolutionAlgorithm implementations used with this
 * library (and their components) must be thread-safe objects. Fortunately, all
 * implementations provided by this library are designed to be thread-safe unless
 * otherwise stated. For most purposes, this will not amount to any extra work for
 * clients. However, anyone extending or implementing a custom {@link EvolutionAlgorithm},
 * {@link EvolutionListener}, or {@link PopulationPool} must be careful to make them
 * thread-safe; if your {@link StateManager} requires internal state, it must be
 * thread-safe as well. Please see the documentation on these interfaces for some thoughts
 * on how this might be done. But first and foremost, be aware that the {@code evolver}
 * passed into this class is shared by all worker threads, so the algorithm's {@code run}
 * method at least should attempt to proceed with as little direct synchronization as
 * possible.
 * @author David Schmidt
 */
public class EvolutionControlThread<S> extends Thread
{

	// Data members.
	private Thread[ ] fieldWorkers;

	/**
	 * A latch that each of the threads and this thread must wait on before any of them
	 * may begin their tasks. Once all threads are waiting, the latch releases and the
	 * tasks begin.
	 */
	private CountDownLatch fieldStartLatch;


	/**
	 * EvolutionControlThread constructor.
	 * @param evolver an EvolutionAlgorithm which is to be run on the specified number of
	 *            threads. This should be a thread-safe, non-blocking runnable. Bundled
	 *            library classes meet these requirements unless they state otherwise. See
	 *            the note on the thread safety policy in the class documentation for more
	 *            details.
	 * @param numThreads an int indicating how many separate worker threads to spawn (not
	 *            including the control thread, which does no real work) to process the
	 *            algorithm. According to <i>Java Concurrency In Practice</i> (Goetz et
	 *            al, 2006), a good rule of thumb for optimal performance is to use
	 *            {@code n+1} threads doing evenly divided work, where {@code n} is the
	 *            number of cores effectively available on the hardware. (This number may
	 *            be obtained from the {@code java.lang.Runtime} class.
	 * @param stopCondition an array of StopCondition which will observe the evolution and
	 *            decide when it is ready to be stopped. Any one of them may trigger the
	 *            algorithm to be stopped.
	 */
	public EvolutionControlThread( final EvolutionAlgorithm evolver, int numThreads,
			StopCondition... stopConditions )
	{
		// Register the stop conditions with the evolver (if it hasn't been already).
		for ( StopCondition stopCondition : stopConditions )
			{
			evolver.registerListener( stopCondition );
			stopCondition.setEvolutionControlThread( this );
			}

		// Create a gate to prevent premature evolution until all threads are ready.
		fieldStartLatch = new CountDownLatch( 1 );

		// Create worker threads.
		fieldWorkers = new Thread[ numThreads ];
		for ( int index = 0; index < fieldWorkers.length; index++ )
			fieldWorkers[ index ] = new Thread( new Runnable( )
			{
				@Override
				public void run( )
				{
					try
						{
						// Wait on the latch so everyone starts at once.
						fieldStartLatch.await( );

						// Start evolving!
						evolver.evolve( );
						}
					catch ( InterruptedException exception )
						{
						// It seems we were canceled before we even began.
						}

				}

			} );

	} // EvolutionControlThread


	@Override
	public void run( )
	{
		// Start off worker threads. They'll wait on a latch before starting, so they
		// won't start until everyone is ready to start.
		for ( Thread workerThread : fieldWorkers )
			workerThread.start( );

		// Release the latch and begin evolution.
		fieldStartLatch.countDown( );

		try
			{
			// Wait for the StopCondition to notify us or someone to interrupt the
			// thread. (Attempting to join your own thread will just wait forever for an
			// interrupt from another thread.)
			join( );
			}
		catch ( InterruptedException exception )
			{
			// We don't want to restore the interrupt status, or we'll end up
			// canceling our worker joining later on.
			}

		// Better let the others know we're done.
		for ( Thread thread : fieldWorkers )
			thread.interrupt( );

		// Now, for the sake of client libraries being aware of "complete" shutdown,
		// wait for them to die before we allow ourselves to die.
		for ( Thread thread : fieldWorkers )
			{
			try
				{
				thread.join( );
				}
			catch ( InterruptedException exception )
				{
				// What can we do? The caller didn't want to wait for this one, so we'll
				// assume they won't want to wait for any of the others either. It's their
				// fault now if the worker threads are in an unknown state; they were
				// warned.
				break;
				}
			}

	} // run

}
