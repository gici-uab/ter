/*
 * GICI Library -
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
 * gici-info@deic.uab.es
 */
package GiciStream;
import GiciException.*;

import java.nio.ByteBuffer;


/**
 * This class supports the writting and reading of values from/to a BitStream using different data types(boolean, byte, short, integer, integer specifying the number of bits to write, long, float, double). This class is not instantiable, all the functions are declared static.
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class IODataStream{

	/**
	 * Write a boolean to a BitStream.
	 *
	 * @param value its the boolean to insert
	 * @param IODataBitStream its the BitStream to insert data readed
	 */
	static public void writeBoolean(BitStream IODataBitStream, boolean value){
		IODataBitStream.addBit(value);
	}

	/**
	 * Reads a boolean from a BitStream and returns its value.
	 *
	 * @param IODataBitStream its the BitStream to read the data
	 * @return the boolean read
	 *
	 * @throws WarningException when the input BitStream end is reached
	 */
	static public boolean readBoolean(BitStream IODataBitStream) throws WarningException{
		return(IODataBitStream.getBit());
	}

	/**
	 * Write a byte to a BitStream.
	 *
	 * @param IODataBitStream its the BitStream to write the data
	 * @param value its the byte to insert
	 */
	static public void writeByte(BitStream IODataBitStream, byte value){
		for(int mask = Byte.SIZE - 1; mask >= 0; mask--){
			IODataBitStream.addBit((value & (byte)(1 << mask)) != 0);
		}
	}

	/**
	 * Reads a byte from a BitStream and returns its value.
	 *
	 * @param IODataBitStream it is the BitStream to read the data
	 * @return the byte read
	 *
	 * @throws WarningException when the input BitStream end is reached
	 */
	static public byte readByte(BitStream IODataBitStream) throws WarningException{
		byte value = 0;
		for(int mask = Byte.SIZE - 1; mask >= 0; mask--){
			if(IODataBitStream.getBit()){
				value |= (byte)(1 << mask);
			}
		}
		return(value);
	}

	/**
	 * Write a short to a BitStream.
	 *
	 * @param IODataBitStream its the BitStream to write the data
	 * @param value it is the short to insert
	 */
	static public void writeShort(BitStream IODataBitStream, short value){
		for(int mask = Short.SIZE - 1; mask >= 0; mask--){
			IODataBitStream.addBit((value & (short)(1 << mask)) != 0);
		}
	}

	/**
	 * Reads a short from a BitStream and returns its value.
	 *
	 * @param IODataBitStream its the BitStream to read the data
	 * @return the short readed
	 *
	 * @throws WarningException when the input BitStream end is reached
	 */
	static public short readShort(BitStream IODataBitStream) throws WarningException{
		short value = 0;
		for(int mask = Short.SIZE - 1; mask >= 0; mask--){
			if(IODataBitStream.getBit()){
				value |= (short)(1 << mask);
			}
		}
		return(value);
	}

	/**
	 * Write an integer to a BitStream.
	 *
	 * @param IODataBitStream its the BitStream to write the data
	 * @param value its the integer to insert
	 */
	static public void writeInteger(BitStream IODataBitStream, int value){
		for(int mask = Integer.SIZE - 1; mask >= 0; mask--){
			IODataBitStream.addBit((value & (1 << mask)) != 0);
		}
	}

	/**
	 * Reads an integer from a BitStream and returns its value.
	 *
	 * @param IODataBitStream its the BitStream to read the data
	 * @return the integer readed
	 *
	 * @throws WarningException when the input BitStream end is reached
	 */
	static public int readInteger(BitStream IODataBitStream) throws WarningException{
		int value = 0;
		for(int mask = Integer.SIZE - 1; mask >= 0; mask--){
			if(IODataBitStream.getBit()){
				value |= (1 << mask);
			}
		}
		return(value);
	}

	/**
	 * Write a long to a BitStream.
	 *
	 * @param IODataBitStream its the BitStream to write the data
	 * @param value its the long to insert
	 */
	static public void writeLong(BitStream IODataBitStream, long value){
		for(long mask = Long.SIZE - 1; mask >= 0; mask--){
			IODataBitStream.addBit((value & ((long) 1 << mask)) != 0);
		}
	}

	/**
	 * Reads a long from a BitStream and returns its value.
	 *
	 * @param IODataBitStream its the BitStream to read the data
	 * @return the long readed
	 *
	 * @throws WarningException when the input BitStream end is reached
	 */
	static public long readLong(BitStream IODataBitStream) throws WarningException{
		long value = 0;
		for(long mask = Long.SIZE - 1; mask >= 0; mask--){
			if(IODataBitStream.getBit()){
				value |= ((long) 1 << mask);
			}
		}
		return(value);
	}

	/**
	 * Write a float to a BitStream.
	 *
	 * @param IODataBitStream its the BitStream to write the data
	 * @param value its the float to insert
	 */
	static public void writeFloat(BitStream IODataBitStream, float value){
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.asFloatBuffer().put(value);
		for(int i = 0; i < 4; i++){
			writeByte(IODataBitStream, bb.get(i));
		}
	}

	/**
	 * Reads a float from a BitStream and returns its value.
	 *
	 * @param IODataBitStream its the BitStream to read the data
	 * @return the float read
	 *
	 * @throws WarningException when the input BitStream end is reached
	 */
	static public float readFloat(BitStream IODataBitStream) throws WarningException{
		float value = 0;
		ByteBuffer bb = ByteBuffer.allocate(4);
		for(int i = 0; i < 4; i++){
			bb.put(readByte(IODataBitStream));
		}
		bb.rewind();
		value = bb.getFloat();
		return(value);
	}

	/**
	 * Write a double to a BitStream.
	 *
	 * @param IODataBitStream its the BitStream to write the data
	 * @param value its the double to insert
	 */
	static public void writeDouble(BitStream IODataBitStream, double value){
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.asDoubleBuffer().put(value);
		for(int i = 0; i < 8; i++){
			writeByte(IODataBitStream, bb.get(i));
		}
	}

	/**
	 * Reads a double from a BitStream and returns its value.
	 *
	 * @param IODataBitStream its the BitStream to read the data
	 * @return the double read
	 *
	 * @throws WarningException when the input BitStream end is reached
	 */
	static public double readDouble(BitStream IODataBitStream) throws WarningException{
		double value = 0;
		ByteBuffer bb = ByteBuffer.allocate(8);
		for(int i = 0; i < 8; i++){
			bb.put(readByte(IODataBitStream));
		}
		bb.rewind();
		value = bb.getDouble();
		return(value);
	}

	/**
	 * Write a number of bits of an unsigned int to a bitstream.
	 *
	 * @param IODataBitStream its the BitStream to write the data
	 * @param value a integer that contains the sequence of values to be inserted
	 * @param numBits a integer that indicates the number of bits to take into account
	 *
	 * @throws WarningException when the value cannot be saved with the passed number of bits or is negative
	 */
	static public void writeBitsUnsignedInteger(BitStream IODataBitStream, int value, int numBits) throws WarningException{
		if((value > 0) && (numBits < (Math.log(value) / Math.log(2)))){
			throw new WarningException("The value cannot be saved with the passed number of bits.");
		}
		if(value < 0){
			throw new WarningException("Only positive values allowed.");
		}
		for(int mask = numBits-1; mask >= 0; mask--){
			IODataBitStream.addBit((value & (1 << mask)) != 0);
		}
	}

	/**
	 * Reads a number of bits of an integer from a BitStream and returns its value.
	 *
	 * @param IODataBitStream its the BitStream to read the data
	 * @param numBits the number of bits of the integer
	 * @return the integer read
	 *
	 * @throws WarningException when the input BitStream end is reached
	 */
	static public int readBitsUnsignedInteger(BitStream IODataBitStream, int numBits) throws WarningException{
		int value = 0;
		for(int mask = numBits-1; mask >= 0; mask--){
			if(IODataBitStream.getBit()){
				value |= (1 << mask);
			}
		}
		return(value);
	}

	/**
	 * Write the bits of an signed int including the sign bit, to the bitstream starting at length MSB position of the int passed.
	 *
	 * @param IODataBitStream its the BitStream to write the data
	 * @param value a integer that contains the sequence of values to be inserted
	 * @param numBits a integer that indicates the number of bits to take into account the sign is included
	 *
	 * @throws WarningException when the value cannot be saved with the passed number of bits
	 */
	static public void writeBitsSignedInteger(BitStream IODataBitStream, int value, int numBits) throws WarningException{
		IODataBitStream.addBit(value < 0);
		writeBitsUnsignedInteger(IODataBitStream, Math.abs(value), numBits-1);
	}

	/**
	 * Reads an signed integer from a BitStream and returns its value.
	 *
	 * @param IODataBitStream its the BitStream to read the data
	 * @param numBits the number of bits of the integer includign the sign bit
	 * @return the integer read
	 *
	 * @throws WarningException when the input BitStream end is reached
	 */
	static public int readBitsSignedInteger(BitStream IODataBitStream, int numBits) throws WarningException{
		int sign = IODataBitStream.getBit() ? -1 : 1;
		int value = readBitsUnsignedInteger(IODataBitStream, numBits-1);
		return(value * sign);
	}
}
