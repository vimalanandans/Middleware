/**
 * This file is part of Bezirk-Middleware-API.
 * <p>
 * Bezirk-Middleware-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * </p>
 * <p>
 * Bezirk-Middleware-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * </p>
 * You should have received a copy of the GNU General Public License
 * along with Bezirk-Middleware-API.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bezirk.middleware.messages;

/**
 * Base class for non-trivial Bezirk messages and data transfers. A stream represents a set of data
 * elements such as multiple messages or picture and music data. This class is extended by protocol
 * implementors to define concrete streams and their custom attributes and payloads. To implement
 * a simple, small message, extend the {@link Event} class.
 * <p>
 * Implementers should favor extending {@link MulticastStream} when a concrete stream will have
 * multiple recipients. {@link UnicastStream} should be favored when the stream will have a single
 * and known recipient.
 * </p>
 *
 * @see Message
 * @see Event
 * @see MulticastStream
 * @see UnicastStream
 */
public class Stream extends Message {
    /**
     * Subclass sets to <code>true</code> if the payload can be processed incrementally (e.g. a
     * music stream) or <code>false</code> if all data elements must be received before processing
     * can continue (e.g. image file data).
     */
    private boolean incremental;

    /**
     * Subclass sets to <code>true</code> if the data elements must be reliably transferred, or
     * <code>false</code> if data elements may be dropped from the stream during transmission
     * without resending to increase performance.
     */
    private boolean reliable;

    /**
     * Subclass sets to <code>true</code> if Bezirk must encrypt the stream's data before transmitting. If
     * set to <code>false</code>, Bezirk will offer the user the opportunity to encrypt the stream anyway.
     * This option is provided to allow users to make a tradeoff between privacy and performance where the
     * protocol designer does not believe the stream will always require confidentiality.
     */
    private boolean encrypted;

    /**
     * The concrete implementation of a <code>Stream</code> must specify the stream's flag
     * and topic. Message flags and topics are documented in {@link Message}.
     *
     * @param flag  flag to mark the intent of this stream
     * @param topic the pub-sub topic for this stream
     */
    public Stream(Flag flag, String topic) {
        this.flag = flag;
        this.topic = topic;
    }

    /**
     * Returns <code>true</code> if the payload can be processed as data elements arrive.
     *
     * @return <code>true</code> if the payload can be processed as data elements arrive
     */
    public boolean isIncremental() {
        return incremental;
    }

    /**
     * Sets whether or not the payload can be processed before all data elements are received.
     * If <code>true</code>, the recipient may begin processing the payload as data elements
     * are received (e.g. a music stream). Otherwise, all of the data must be received first
     * (e.g. a picture stream).
     *
     * @param incremental <code>true</code> if the payload can be processed before all data
     *                    elements arrive
     */
    protected void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    /**
     * Returns <code>true</code> if the stream's data must be reliably transmitted. Essentially,
     * <code>true</code> has the same reliability guarantees as TCP, and <code>false</code> the
     * same guarantees as UDP.
     *
     * @return <code>true</code> if the stream must be reliably transmitted.
     */
    public boolean isReliable() {
        return reliable;
    }

    /**
     * Sets whether or not the middleware must reliably transmit this stream to recipients, or
     * if packets may be dropped. It can be useful to allow packets to be dropped in high
     * performance cases where some loss is acceptable (e.g. streaming video).
     *
     * @param reliable <code>true</code> if the stream must be reliably transmitted
     */
    protected void setReliable(boolean reliable) {
        this.reliable = reliable;
    }

    /**
     * Returns <code>true</code> if the stream's contents must be encrypted.
     *
     * @return <code>true</code> if the stream's contents must be encrypted
     */
    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * Sets whether or not the stream's contents must be encrypted.
     *
     * @param encrypted <code>true</code> if the stream's contents must be encrypted
     */
    protected void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }
}