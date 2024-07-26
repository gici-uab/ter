/*
 * TER Software - More than an implementation of CCSDS Recommendation for Image Data Compression
 * Copyright (C) 2007  Group on Interactive Coding of Images (GICI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Group on Interactive Coding of Images (GICI)
 * Department of Information and Communication Engineering
 * Autonomous University of Barcelona
 * 08193 - Bellaterra - Cerdanyola del Valles (Barcelona)
 * Spain
 *
 * http://gici.uab.es
 * http://sourceforge.net/projects/ter
 * gici-info@deic.uab.es
 */
package TER.TERcoder.SegmentCoder;
import GiciException.*;
import GiciStream.*;


/**
 * This interface defines the methods that a coder must contain.
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public interface ArithmeticCoder{

	/**
	 * Code a bit taking into account context probabilities in an artihmetic like coder.
	 *
	 * @param bit bit to encode
	 * @param context context the bit
	 *
	 * @throws WarningException when some problem with the manipulation of the ByteStream happens
	 */
	public void codeBit(boolean bit, int context);

	/**
	 * Code a bit without taking into account context probabilities in an artihmetic like coder.
	 *
	 * @param bit bit to encode
	 *
	 * @throws WarningException when some problem with the manipulation of the ByteStream happens
	 */
	public void codeBit(boolean bit);

	/**
	 * Swaps the current outputByteStream. Before to call this function you should terminate the last ByteStream with the function terminate.
	 *
	 * @param outputByteStream ByteStream where the byte are flushed
	 */
	public void swapOutputByteStream(ByteStream outputByteStream);

	/**
	 * Finishes the outputByteStream, if needed.
	 *
	 * @throws WarningException when some problem with the manipulation of the ByteStream happens
	 */
	public void terminate() throws WarningException;

	/**
	 * Restart the internal variables of the coder.
	 */
	public void restart();

	/**
	 * Reset the context probabilities of the coder, if any.
	 */
	public void reset();

}
