package com.bezirk.middleware.messages;

import com.bezirk.middleware.addressing.ZirkEndPoint;

import java.io.File;
import java.io.PipedOutputStream;

/**
 * Class used to create a set of {@link StreamDescriptor streams} to subscribe to. Extend this set to design an
 * interface declaring the streams to subscribe to, otherwise directly instantiate it to subscribe
 * to a small set of ad hoc streams. See {@link MessageSet} for examples.
 */
public class StreamSet extends MessageSet {
    private StreamReceiver receiver;

    public StreamSet(Class<? extends StreamDescriptor>... s) {
        super(s);
    }

    /**
     * Get the listener that will be notified when a <code>StreamDescriptor</code> in this set is received.
     *
     * @return the listener that will be notified when a StreamDescriptor in this set is received, or
     * <code>null</code> if one is not set
     */
    public StreamReceiver getStreamReceiver() {
        return receiver;
    }

    /**
     * Set the listener that will be notified when a <code>StreamDescriptor</code> in this set is received
     * after subscription. Set this listener before calling
     * {@link com.bezirk.middleware.Bezirk#subscribe(MessageSet)}, otherwise streams
     * may be missed.
     * <pre>
     *     // Create the Event set
     *     StreamSet s = new StreamSet(SecurityCameraStream.class);
     *
     *     // Set the listener before subscribing to the set
     *     s.setStreamReceiver((stream, inputStream, sender) -&gt; {
     *        // Do something with the security camera stream
     *     });
     *
     *     bezirk.subscribe(s);
     *
     *     // If we set the stream listener here instead, we might miss streams we expected
     *     // to receive
     * </pre>
     *
     * @param receiver the listener to notify when a StreamDescriptor in this set is received, or
     *                 <code>null</code> to remove an existing listener
     */
    public void setStreamReceiver(StreamReceiver receiver) {
        this.receiver = receiver;
    }

    /**
     * Interface implemented by observers of a <code>StreamSet</code> that want to be notified when
     * a stream in this set is received.
     * <p>
     * Snippet from a Zirk that sends a file:
     * </p>
     * <pre>
     *     // Sender
     *     ZirkEndPoint z = ...;
     *     StreamDescriptor s = new PictureStream(...);
     *     bezirk.sendStream(z, s, new File("/home/user/pictures/Puppy.png");
     * </pre>
     * <p>
     * Snippet from a Zirk that receives the sent file:
     * </p>
     * <pre>
     *     // Receiver
     *     StreamSet s = new StreamSet(PictureStream.class);
     *
     *     s.setStreamReceiver(new StreamReceive&lt;File&gt;() {
     *         {@literal @}Override
     *         void receiveStream(StreamDescriptor stream, File receivedFile, ZirkEndPoint sender) {
     *             // Do something with the receivedFile
     *         }
     *     });
     *
     *     bezirk.subscribe(s);
     * </pre>
     *
     * @param <T> The type of the container for the contents of the received stream. This is
     *            typically <code>java.io.InputStream</code> when a stream is sent with
     *            {@link com.bezirk.middleware.Bezirk#sendStream(ZirkEndPoint, StreamDescriptor, PipedOutputStream)}
     *            and <code>java.io.File</code> when a stream is sent with
     *            {@link com.bezirk.middleware.Bezirk#sendStream(ZirkEndPoint, StreamDescriptor, File)}.
     */
    public interface StreamReceiver<T> {
        void receiveStream(StreamDescriptor streamDescriptor, T streamContents, ZirkEndPoint sender);
    }
}