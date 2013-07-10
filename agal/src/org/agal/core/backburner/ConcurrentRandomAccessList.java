/*
 * ConcurrentRandomAccessList.java
 * 
 * Created on Jul 11, 2012
 * 
 */
package org.agal.core.backburner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO - rewrite all docs ConcurrentRandomAccessList is a performant concurrent list
 * implementation which guarantees that random access ({@code get}), insertion ({@code add}
 * ), and deletion ( {@code remove}) all run in constant time. The tradeoff to this is
 * that list ordering is not guaranteed; any element may be moved to another position in
 * the list by coincidence by another thread at any time. For applications that only
 * desire random access, such as maintaining a randomly ordered population pool, this
 * shouldn't matter.
 * <p>
 * <h3>Soft Indices</h3> This collection specifies a {@code snap} flag which is used to
 * determine appropriate out-of-bounds element return behavior. As a multi-threaded
 * continuously resizable collection, a ConcurrentRandomAccessList may have a {@code size}
 * in constant flux, and therefore it is nearly impossible to guarantee clients will only
 * request in-bounds indices. The class therefore relaxes the exception policy of standard
 * Java collections: if the {@code snap} flag is set to {@code true}, the last element in
 * the list will be returned for an out-of-bounds index request, whereas if the
 * {@code snap} flag is set to false, {@code null} will be returned for such a request
 * instead.
 * <p>
 * Null elements are not supported. However, due to their high performance
 * implementations, {@code get} and {@code remove} may return {@code null} spuriously in
 * rare cases.
 * <p>
 * <h3>Segmentation</h3> ConcurrentRandomAccessList is highly performant in its concurrent
 * properties because it uses lock striping. The actual collection is divided among
 * discrete <i>segments</i>, each of which may be locked and modified separately from the
 * others. The ratio of segments to accessing threads should be greater than 1; this ratio
 * may be tuned with the {@code segmentSize} constructor.
 * <p>
 * Because of the segmented, concurrent, and unordered nature of this collection,
 * iteration and other list-based functionalities are not supported. Iteration may be
 * implemented by clients with index-based techniques, but if the collection is being
 * accessed by more than one thread at the time of iteration, some elements may appear
 * twice or not at all.
 * @author David Schmidt
 */
public class ConcurrentRandomAccessList<E> implements RandomAccess
{

	/*
	 * Okay, so we're not actually lock striping, when it comes down to it -- we're using
	 * atomic variables, which showed in a simple test to be about as fast for replacing
	 * values as intrinsic locking and twice as fast as intrinsic locking for read-only
	 * operations; and both atomic and intrinsic were surprisingly much faster than a
	 * ReentrantReadWriteLock. Of course, note that these test cases ran in one thread;
	 * but we already know atomic variables easily get the best performance when
	 * contention is involved because they don't block.
	 */
	private class Segment
	{
		// An array of elements which may be atomically accessed and modified. Assumed to
		// always be full, and must be kept so.
		private final AtomicReferenceArray<E> fieldElements;


		Segment( E[ ] elements )
		{
			fieldElements = new AtomicReferenceArray<E>( elements );

		} // Segment


		E get( int index )
		{
			return fieldElements.get( index );

		} // get


		void insert( int index, E element )
		{
			if ( element != null )
				{
				E previous = Segment.this.fieldElements.getAndSet( index, element );
				fieldTail.addTip( previous );
				}

		} // insert


		E remove( int index )
		{
			E tip = fieldTail.removeTip( );
			while ( tip == null && !isEmpty( ) )
				tip = fieldTail.removeTip( );
			return ( tip == null ? null : Segment.this.fieldElements.getAndSet( index, tip ) );

		} // delete

	} // Segment

	// FIXME - docs on impl; don't encourage null elements (queue actually doesn't support
	// them), but don't guarantee that we'll never get one due to soft synchronization
	// policies; consider what interfaces to actually specify!

	// TODO - replace queue with nullsafe & self-contained size counter

	private class Tail
	{
		// Our nonblocking queue that can handle the entropy of near-constant use. And its
		// size, which would otherwise not run in constant time (and would probably be
		// even less accurate).
		private ConcurrentLinkedDeque<E> fieldQueue = new ConcurrentLinkedDeque<>( );
		private AtomicInteger fieldRecentSize = new AtomicInteger( 0 );

		// This will allow a buffer of 3 segments worth of elements in our tail, which
		// should give us a good range to work with.
		private int fieldSegmentationSize = ( int ) ( fieldSegmentSize * 3.5 );
		private int fieldDesegmentationSize = ( int ) ( fieldSegmentSize * 0.5 );

		// A lock for preventing excessive simultaneous growing/shrinking by several
		// threads that notice the queue size simultaneously.
		private ReentrantLock fieldLock = new ReentrantLock( );


		void addTip( E element )
		{
			if ( element != null )
				{
				fieldQueue.offerLast( element );
				fieldRecentSize.incrementAndGet( );

				if ( isTooBig( ) )
					segment( );
				}

		} // addTip


		void clear( )
		{
			fieldQueue.clear( );
			fieldRecentSize.set( 0 );

		} // clear


		private void desegment( )
		{
			// If someone else is using it, no problem. Another thread will notice the
			// same thing we did and deal with it once the current user is done.
			if ( fieldLock.tryLock( ) )
				{
				try
					{
					// Don't bother double-checking the trigger condition. Even if the
					// queue has grown some (or even absorbed another segment in another
					// thread), it's better to segment than to fluctuate on the edge of
					// overflow, which would cause gratuitous locking.
					Segment segment = fieldSegments.remove( fieldSegments.size( ) - 1 );
					if ( segment != null )
						{
						// Iterate and queue on the "base" side of the tail (rather than
						// the "tip").
						for ( int index = fieldSegmentSize - 1; index >= 0; index-- )
							{
							// It's common for a segment to contain a null where the queue
							// was empty during an insert.
							E element = segment.get( index );
							if ( element != null )
								{
								fieldQueue.offerFirst( element );
								fieldRecentSize.incrementAndGet( );
								}
							}
						}
					}
				finally
					{
					fieldLock.unlock( );
					}
				}

		} // desegment


		E getBase( )
		{
			return fieldQueue.peekFirst( );

		} // getBase


		E getTip( )
		{
			return fieldQueue.peekLast( );

		} // getTip


		private boolean isTooBig( )
		{
			return ( fieldRecentSize.get( ) > fieldSegmentationSize );

		} // isTooBig


		private boolean isTooSmall( )
		{
			// If there are more segments to consume and we've shrunk enough to possibly
			// need one, get one.
			return ( fieldRecentSize.get( ) <= fieldDesegmentationSize && !fieldSegments.isEmpty( ) );

		} // isTooSmall


		E removeBase( )
		{
			E element = fieldQueue.pollFirst( );
			if ( element != null )
				fieldRecentSize.decrementAndGet( );

			if ( isTooSmall( ) )
				desegment( );

			return element;

		} // removeBase


		E removeTip( )
		{
			E element = fieldQueue.pollLast( );
			if ( element != null )
				fieldRecentSize.decrementAndGet( );

			if ( isTooSmall( ) )
				desegment( );

			return element;

		} // removeTip


		private void segment( )
		{
			if ( fieldLock.tryLock( ) )
				{
				try
					{
					// Don't bother double-checking the trigger condition. Even if the
					// queue has shrunk some, it's better to segment than to fluctuate on
					// the edge of overflow, which would cause gratuitous locking.

					// This *should* always return something, but...
					E typeElement = fieldQueue.pollFirst( );
					if ( typeElement != null )
						{
						// Needed the 0th element to create the array because Java
						// generics are far from perfect.
						@SuppressWarnings( "unchecked" )
						E[ ] elements = ( E[ ] ) Array.newInstance( typeElement.getClass( ),
								fieldSegmentSize );
						elements[ 0 ] = typeElement;

						// Take some elements from the "base" of the tail (not the tip)
						// and store them in an array to put in the segment.
						for ( int index = 1; index < fieldSegmentSize && !fieldQueue.isEmpty( ); index++ )
							{
							E element = fieldQueue.pollFirst( );

							// Never put a null element into the array; if we get one, try
							// this index again, and break the loop if the queue is empty.
							if ( element == null )
								index--;
							else
								elements[ index ] = element;
							}

						// Did we break the loop because the queue was empty?
						if ( elements[ fieldSegmentSize - 1 ] == null )
							{
							// If so, restore the stolen queue items.
							for ( int index = fieldSegmentSize - 1; index >= 0; index-- )
								if ( elements[ index ] != null )
									fieldQueue.offerFirst( elements[ index ] );
							}
						else
							{
							// If not, finalize the segment and update the count all at
							// once for simplicity.
							fieldRecentSize.addAndGet( fieldSegmentSize * -1 );
							fieldSegments.add( new Segment( elements ) );
							}
						}
					}
				finally
					{
					fieldLock.unlock( );
					}
				}

		} // segment

	} // Tail

	// Constants.
	private static final int DEFAULT_SEGMENT_SIZE = 19;

	// Data members.
	private final AtomicInteger fieldSize = new AtomicInteger( 0 );

	private final List<Segment> fieldSegments = Collections
			.synchronizedList( new ArrayList<Segment>( ) );
	private final Tail fieldTail; // tail may not be initialized until after segment size
	private final boolean fieldSnap;
	private final int fieldSegmentSize;


	/**
	 * Constructs a new ConcurrentRandomAccessList with the given {@code snap} and a
	 * default {@code segmentSize}.
	 * @param snap a {@code boolean} indicating whether to return the end of the list or
	 *            {@code null} if a {@code get} or {@code remove} request specifies an
	 *            out-of-bounds index past the end of the collection. (This is easily
	 *            possible in a multi-threaded environment: for instance, if any other
	 *            thread shrinks the array after your thread has decided to request the
	 *            last element by randomly calculated index.) When {@code snap} is on,
	 *            such requests will "snap" to the end of the collection and return the
	 *            last element; when it is off, they will simply return {@code null}.
	 *            Attempts to add elements outside the boundary of the collection will
	 *            always result in the element being added to the end, regardless of
	 *            {@code snap}'s value. Note that indices below {@code 0} will still cause
	 *            {@code ArrayIndexOutOfBoundsException}s to be thrown.
	 */
	public ConcurrentRandomAccessList( boolean snap )
	{
		this( snap, DEFAULT_SEGMENT_SIZE );

	} // ConcurrentRandomAccessList


	// note - segment arrays are always full for proper indexed access; tail node last in
	// list of segments; segments must be small enough to distribute load on smallest
	// population over all of them without much contention, but large enough to scale to a
	// large population without getting out of hand; auto-growth (array resize)
	// loadfactor/policy?

	/**
	 * Constructs a new ConcurrentRandomAccessList with the given {@code snap} and
	 * {@code segmentSize}.
	 * @param snap a {@code boolean} indicating whether to return the end of the list or
	 *            {@code null} if a {@code get} or {@code remove} request specifies an
	 *            out-of-bounds index past the end of the collection. (This is easily
	 *            possible in a multi-threaded environment: for instance, if any other
	 *            thread shrinks the array after your thread has decided to request the
	 *            last element by randomly calculated index.) When {@code snap} is on,
	 *            such requests will "snap" to the end of the collection and return the
	 *            last element; when it is off, they will simply return {@code null}.
	 *            Attempts to add elements outside the boundary of the collection will
	 *            always result in the element being added to the end, regardless of
	 *            {@code snap}'s value. Note that indices below {@code 0} will still cause
	 *            {@code ArrayIndexOutOfBoundsException}s to be thrown.
	 * @param segmentSize an {@code int} indicating the number of elements which should be
	 *            contained by each segment. The elements in this list will be divided
	 *            into {@code n} segments with {@code segmentSize} elements in each. In
	 *            general, it is wise to choose a number that, considering the expected
	 *            size of the list, will lead to more segments than the expected number of
	 *            threads that will be accessing the collection. This is because the
	 *            implementation uses lock striping to reduce blocking: each segment may
	 *            be locked for writing by one thread at a time. Choosing a
	 *            {@code segmentSize} value that is too large could cause the entire
	 *            collection to lock every time a structural modification is made, while
	 *            choosing one that is too small will lead to excessive overhead in
	 *            maintaining segments as the list grows and shrinks.
	 */
	public ConcurrentRandomAccessList( boolean snap, int segmentSize )
	{
		fieldSnap = snap;
		fieldSegmentSize = segmentSize;

		fieldTail = new Tail( );

	} // ConcurrentRandomAccessList


	public static void main( String[ ] args )
			throws InterruptedException
	{
		final ConcurrentRandomAccessList<Integer> set = new ConcurrentRandomAccessList<>( false, 23 );
		final Random r = new Random( );
		final int cycles = 1000000;

		for ( int i = 0; i < 500; i++ )
			set.insert( r.nextInt( i + 1 ), i );

		int threadCount = Runtime.getRuntime( ).availableProcessors( ) + 1;
		Thread[ ] threads = new Thread[ threadCount ];
		for ( int i = 0; i < threadCount; i++ )
			{
			threads[ i ] = new Thread( new Runnable( )
			{
				public void run( )
				{
					for ( int i = 0; i < cycles; i++ )
						{
						switch ( r.nextInt( 10 ) )
							{
							case 0 :
								set.remove( r.nextInt( set.size( ) + 1 ) );
								break;

							case 1 :
								set.insert( r.nextInt( set.size( ) + 1 ), r.nextInt( ) );
								break;

							default :
								set.get( r.nextInt( set.size( ) + 1 ) );
								break;
							}
						}

				};
			} );
			}

		long millis = System.currentTimeMillis( );
		for ( int i = 0; i < threadCount; i++ )
			threads[ i ].start( );
		for ( int i = 0; i < threadCount; i++ )
			threads[ i ].join( );
		millis = System.currentTimeMillis( ) - millis;

		while ( !set.isEmpty( ) )
			set.remove( r.nextInt( set.size( ) ) );

		System.out.println( "Used the collection " + ( cycles * threadCount ) + " times in "
				+ millis + "ms (" + ( ( double ) ( cycles * threadCount ) / millis * 1000 )
				+ "/sec)" );

	}


	// TODO docs
	public boolean add( E element )
	{
		fieldTail.addTip( element );
		fieldSize.incrementAndGet( );
		return true;

	} // add


	public void clear( )
	{
		fieldSegments.clear( );
		fieldTail.clear( );
		fieldSize.set( 0 );

	} // clear


	public E get( int index )
	{
		if ( isEmpty( ) )
			return null;

		Segment segment = getSegment( index / fieldSegmentSize );
		return ( segment == null ) ? fieldTail.getBase( ) : segment.get( index % fieldSegmentSize );

	} // get


	private Segment getSegment( int index )
	{
		Segment segment = null;
		try
			{
			segment = fieldSegments.get( index );
			}
		catch ( Exception ignored )
			{
			}

		return segment;

	} // Segment


	public void insert( int index, E element )
	{
		Segment segment = getSegment( index / fieldSegmentSize );

		if ( segment == null )
			// Whatever, don't have time to mess with it. Gotta return to the client. Tail
			// can sort it out. Maybe it'll get a second chance in life.
			fieldTail.addTip( element );
		else
			// Yay.
			segment.insert( index % fieldSegmentSize, element );

		fieldSize.incrementAndGet( );

	} // insert


	public boolean isEmpty( )
	{
		return ( fieldSize.get( ) == 0 );

	} // isEmpty


	public E remove( int index )
	{
		if ( isEmpty( ) )
			return null;

		E element = null;
		Segment segment = getSegment( index / fieldSegmentSize );
		if ( segment != null )
			element = segment.remove( index % fieldSegmentSize );
		if ( element == null )
			element = fieldTail.removeBase( );
		if ( element != null )
			fieldSize.decrementAndGet( );

		return element;

	} // remove


	public int size( )
	{
		return fieldSize.get( );

	} // size

}
