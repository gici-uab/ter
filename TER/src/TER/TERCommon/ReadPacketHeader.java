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
package TER.TERCommon;

import java.io.EOFException;
import java.net.ProtocolException;

import GiciException.ParameterException;
import TER.TERdecoder.ReadFile.ReadBufferedStream;

public class ReadPacketHeader{

	ReadBufferedStream encodedStream = null;
	boolean parametersSet = false;
	
	/**
	 * Constructor of ReadTERPacketHeader. It does not receive anything. 
	 */
	public ReadPacketHeader(){
		
	}

	/**
	 * Set the parameters needed used to read the segment header. 
	 * 
	 */
	public void setParameters(ReadBufferedStream encodedStream){
		this.encodedStream = encodedStream ;	
		this.parametersSet = true;
	}
	
	public int readPacketHeader() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("ReadPacketHeader cannot run if parameters are not set.");
		}
		int value = 0;
		int tempByte;
		int numBytesVBAS = 0;

		do {			
			tempByte = encodedStream.read();

			if (tempByte == -1) {
				throw new EOFException("There is not data available to read the VBAS");
			}

			value = (value << 7) | (int) (tempByte & 0x7F);
			numBytesVBAS++;

			if (numBytesVBAS > 5) {	// maximum int value is 2^31 - 1 => 9 bytes VBAS 
				throw new ProtocolException("VBAS length is larger than 31 bits (which is the maximum of int)");
			}

		} while ( (tempByte & 0x80) != 0 );					

		return value;		
	}
}