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
package TER.TERInteractiveDecoder.ReadStream;

import GiciException.ParameterException;
import TER.TERdecoder.ReadFile.ReadBufferedStream;

public class RecommendedExtractor{
	
	ReadBufferedStream encodedStream = null;
	int targetBytes;
	boolean parametersSet = false;
	
	public RecommendedExtractor(ReadBufferedStream encodedStream){
		this.encodedStream = encodedStream;
	}
	
	public void setParameters(int targetBytes){
		this.targetBytes = targetBytes;
		
		parametersSet = true;
	}
	
	public ReadBufferedStream run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("ReadFile cannot run if parameters are not set.");
		}
		
		byte byteStream[] = new byte[targetBytes];
		encodedStream.readFully(byteStream,0,targetBytes);
		
		parametersSet = false;
		return (new ReadBufferedStream(byteStream));
	}
}