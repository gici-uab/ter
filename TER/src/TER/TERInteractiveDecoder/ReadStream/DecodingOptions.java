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
import TER.TERdecoder.ReadFile.ReadTERHeader;


public class DecodingOptions{
	boolean parametersSet = false;

	ReadBufferedStream encodedStream;	
	int inputFileType;
	
	boolean targetScalable;
	boolean resolutionScalable;
	boolean positionScalable;
	boolean componentScalable;
	boolean layerScalable;
	
	long layerLocation[][][][][] = null;
	long initialStreamPosition;
	
	ReadTERHeader TERheader = null;
	//ReadRecommendedHeader CCSDSheader = null;
	
	public DecodingOptions(String inputFile)  throws Exception{ 
		// Check the type of file		
		if (inputFile.endsWith(".rec")){
			inputFileType = 0;
		} else if (inputFile.endsWith(".ter")){
			inputFileType = 1;
		} else {
			throw new ParameterException("Unkown file type for decoding.");
		}
		
		encodedStream= new ReadBufferedStream(inputFile);
	}
	
	public void setParameters(){
		parametersSet = true;
	}
	
	public void run() throws Exception{
		// If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("StreamOptions cannot run if parameters are not set.");
		}
	
		if (inputFileType==0){
			// we have an image encoded using the Recommendation, thus, only a few options are supported
			targetScalable = true;
			resolutionScalable = false;
			positionScalable = false;
			componentScalable = false;
			layerScalable = false;
			initialStreamPosition = 0;
		} else {
			
			TERheader = new ReadTERHeader();		
			TERheader.setParameters(encodedStream);
			TERheader.run();
			initialStreamPosition = encodedStream.getPos();
			
			if (TERheader.getProgressionOrder()==0){
				targetScalable = true;
				resolutionScalable = false;
				positionScalable = false;
				componentScalable = false;
				layerScalable = false;
			} else {
				targetScalable = true;
				resolutionScalable = true;
				positionScalable = true;
				componentScalable = true;
				layerScalable = true;
				
				TERIndexing ti = new TERIndexing(encodedStream);
				ti.setParameters(TERheader.getZSize(),TERheader.getProgressionOrder(),TERheader.getLayers(),TERheader.getWTLevels(),
						TERheader.getBlocksPerSegment(),TERheader.getGaggleSizeDC(),TERheader.getGaggleSizeAC());
				layerLocation = ti.run();
			}
			
		}
		
	}
	
	///////////////////////
	/////GET FUNCTIONS/////
	///////////////////////
	public int getInputFileType(){
		return inputFileType;
	}
	
	public float[][][] getThumbNail(){
		return null;
	}
	
	public ReadTERHeader getTERHeader(){
		return TERheader;
	}
	
	public ReadBufferedStream getEncodedStream(){
		return encodedStream;
	}
	
	public long[][][][][] getLayerLocation(){
		return layerLocation;
	}
	
	public long getInitialStreamPosition(){
		return initialStreamPosition;
	}
}