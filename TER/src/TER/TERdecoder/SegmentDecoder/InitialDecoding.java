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
package TER.TERdecoder.SegmentDecoder;

import GiciException.*;
import TER.TERdecoder.ReadFile.ReadBufferedStream;

/**
 * This class decode the quantized DC components or the bitDepthAC_Block. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 * &nbsp; get function<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */

public class InitialDecoding{

	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;

	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthAC}
	 */	
	int bitDepthAC;

	/**
	 *  Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthDC}
	 */	
	int bitDepthDC;

	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 */	
	int blocksPerSegment;

	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.InitialCoding#gaggleSize}
	 */
	int gaggleSize;

	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.InitialCoding#id}
	 */
	int id ;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.InitialCoding#BPLL}
	 */
	int BPLL;

	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.InitialCoding#isDC}
	 */
	boolean isDC;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.InitialCoding#q}
	 */
	int q;

	/**
	 * This integer indicates the last value entropy decoded before the file ends
	 */
	int lastBlockDecoded;
	
	/**
	 * This integer indicates the way that values are completed when an abnormal termination is found.
	 * That means that the file ends and not all the values have been decoded.
	 * Valid values are:<br>
	 *   <ul>
	 *     <li>  0 - Put O to the uncoded values  
	 *     <li>  1 - Repeat the middle value of the magnitude interval where the components are being decoded, i.e., (max(-xMin,xMax)+1)/2.
	 *     <li>  2 - Repeat the mean value of the recovered values
	 *     <li>  n - For n>2, repeat the mean value of the n-2 last recovered values. If less than n-2 values have been recovered, only the recovered values will be used. Note that for n=3 we repeat the last value. 
	 *   </ul>
	 */
	int completionMode; 
	
	/**
	 * This boolean indicates if the decoding process finishes correctly
	 */
	boolean abnormalTermination = false; 
	
	/**
	 * Void constructor for initial decoding.
	 */
	public InitialDecoding(){
	}
	
	/**
	 * Set the parameters used to perform the initial coding of DC or bitDepthACBlock.
	 *
	 * @param blocksPerSegment definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 * @param bitDepthDC definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthDC}
	 * @param bitDepthAC definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthAC}
	 * @param gaggleSize definition in {@link TER.TERcoder.SegmentCoder.InitialCoding#gaggleSize}
	 * @param id definition in {@link TER.TERcoder.SegmentCoder.InitialCoding#id}
	 * @param BPLL definition in {@link TER.TERcoder.SegmentCoder.InitialCoding#BPLL}
	 * @param isDC definition in {@link TER.TERcoder.SegmentCoder.InitialCoding#isDC}
	 */
	public void setParameters(int blocksPerSegment, int bitDepthDC, int bitDepthAC, 
			int gaggleSize, int id, int BPLL, boolean isDC, int completionMode){

		this.blocksPerSegment = blocksPerSegment;
	  
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC;	  
		this.gaggleSize = gaggleSize;
		this.id = id;
		this.BPLL = BPLL;
		this.isDC = isDC;
		this.completionMode = completionMode;
		
		if ( this.id == 0 ){
			this.id = blocksPerSegment;
		}

		parametersSet = true;		

	}

	/**
	 * Run the initial decoding of either DC components or bitDepthAC_Block
	 *
	 * @param codedSegment bit stream which contains the information required for the decoding process.
	 *
	 * @return an integer array containing recovered values.
	 *
	 * @throws ErrorException when something goes wrong, for example parameters are not set, and compression must be stopped.
	 *
	 */
	public int[] run(ReadBufferedStream codedSegment) throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("Segment cannot be intially decoded if parameters are not set.");
		}
		
		abnormalTermination = false; 
		lastBlockDecoded = -1;
		int decodedValues[] = null;
		int N, xMin, xMax;
		setDinamicRange();
		if (isDC){
			// parameters are set and DC components are quantized
 			N = 1;
 			if ( N < bitDepthDC - q ) { // number of bits needed to represent each quantized DC component
 				N = bitDepthDC - q ;
 			}
 			
			xMin = - ( (int) 1 << (N-1) ); //-2^(N-1)
			xMax = ( (int) 1 << (N-1) ) -1 ; //2^(N-1) -1
			
		} else {
 			N = (int) ( Math.ceil( Math.log(1+bitDepthAC) / Math.log(2) ) );
			xMin = 0; 
			xMax = ( (int) 1 << N ) -1 ; //2^(N) -1
		}
		

		//Initial DC decoding
		if ( N > 1 ){			
			int mappedDPCM[] = entropyDecode(codedSegment, N);
			decodedValues = undoMapAndDPCM(mappedDPCM, xMin, xMax , N);
		} else if ( N == 1 ) {
			decodedValues = new int[blocksPerSegment];
			int numberOfGaggles = blocksPerSegment / gaggleSize;
			if ( blocksPerSegment%gaggleSize != 0 ){
				numberOfGaggles++ ;
			}
			int gaggle=0, block=0;
			try{
				for(gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
					for(block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<blocksPerSegment ; block++ ){
						decodedValues[block] = codedSegment.getBits(N);
					}
				}
			} catch(Exception e){
				lastBlockDecoded = block;
				abnormalTermination = true;
				decodedValues = undoMapAndDPCM(decodedValues, xMin, xMax , N);
			}
			
		}		

		
		if (isDC){
			decodedValues = unQuantize(decodedValues, N);
		}

		return decodedValues;

	}

	/**
	 * This function entropy decodes according to the paramateres given.
	 *
	 * @param codedSegment bit stream containing information required for decoding process.
	 * @param N number of bits needed to represent the values.
	 *
	 * @return an array of integers which contain the decoded values.
	 *
	 * @throws Exception when the bitstream cannot be loaded.
	 */
	public int[] entropyDecode(ReadBufferedStream codedSegment, int N) throws Exception{
		int values[] = new int[blocksPerSegment];
		int numberOfGaggles = blocksPerSegment / gaggleSize;
		if ( blocksPerSegment%gaggleSize != 0 ){
			numberOfGaggles++ ;
		}

		int codeOptionLength = getCodeOptionLength(N);
		int gaggle = 0, block =0;
		try{	
			for(gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
				int codeOption = codedSegment.getBits(codeOptionLength);
				int uncodedOption = ((int) 1 << codeOptionLength ) - 1 ;//(2^CodeOptionLenght)-1 Uncoded option
				if ( codeOption == uncodedOption ){
					for( block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<values.length ; block++ ){
						values[block] = codedSegment.getBits(N);
					}				
				} else {
					// first part words are decoded (Fundamental Sequence)
					for( block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<values.length ; block++ ){
						if( block % id == 0){
							//the value to be decoded is an id, which was not entropy encoded
							values[block] = codedSegment.getBits(N);
						} else {
							int firstPart = 0 ; 
							//the N least significant bits are discarded in the first part words
							//remaining bits are encoded using a fundamental sequence
							while( !codedSegment.getBit() ){//for each 0 the value is incremented in one
								firstPart++;
							}
							values[block] = firstPart << codeOption ;
						}				
					}
					// second part words
					for( block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<values.length ; block++ ){
						if( block % id != 0){
							int secondPart = codedSegment.getBits(codeOption);
							values[block] += secondPart;
						} // if ( block % id == 0) the value is an id, which is decoded in the first part words
					}				
				}
			}
		} catch (Exception e){
			this.lastBlockDecoded = block;
			this.abnormalTermination = true;
		}
		if (this.abnormalTermination == false){
			this.lastBlockDecoded = blocksPerSegment;
		}
		
		return values;
	}

	/**
	 * This funtion get the number of bits required to encode the code option for entropy.
	 *
	 * @param N number of bits needed to represent the values.
	 *
	 * @return which represent the number of bits needed to represent the code option.
	 */
	public int getCodeOptionLength(int N){
		int codeLength = 0;
		if (N == 2){
			codeLength = 1;
		} else if ( N > 2 && N <= 4 ) {
			codeLength = 2;
		} else if ( N > 4 && N <= 8 ) {
			codeLength = 3;
		} else if ( N > 8 && N <= 10 ) {
			codeLength = 4;
		}
		return codeLength;
	}

	/**
	 * This function performs unmaps obtained values and undo DPCM.
	 *
	 * @param delta array of integer values that must be unmapped and undo DPCM.
	 * @param xMin reprensents the minimum value that could be taken by the decoded values.
	 * @param xMax reprensents the maximum value that could be taken by the decoded values.
	 * @param N number of bits needed to represent the values.
	 * 
	 * @return an integer array containing the unmapped values and DPCM inversed
	 */
	public int[] undoMapAndDPCM(int[] delta, int xMin, int xMax, int N){
		int values[] = new int[blocksPerSegment];
		
		for(int block=0; block<this.lastBlockDecoded ; block++){
			if ( block%id == 0){//this is a reference value and not should be modified
				if (delta[block]>= (xMax + 1) ){
					values[block] = delta[block] - 2*(xMax + 1);
				} else {
					values[block] = delta[block];
				}
			} else {
				// In case the value is not a reference value mapping must be inverted
				int theta = xMax - values[block-1];
				if ( theta > (values[block-1] - xMin) ){
					theta = values[block-1] - xMin;
				}
				
				if (delta[block] <= 2*theta && delta[block]>=0){
					if ((delta[block]%2) == 0) {
						values[block] = delta[block]/2;
					} else {
						values[block] = -(delta[block]+1)/2 ;
					}
				} else {

					if(theta == values[block-1] - xMin){
						values[block] = delta[block] - theta;
					} else {
						values[block] = theta - delta[block];
					}
				}
				// once mapping is inverted the DPCM can be inverted
				values[block] = values[block] + values[block-1] ;				
				
			}

		}
		
		if (this.abnormalTermination){
			if (completionMode == 0){
				for(int block=lastBlockDecoded; block<blocksPerSegment ; block++){	
					values[block] = 0;
				}
			} else if (completionMode == 1){
				int magnitude = xMax;
				if(magnitude<-xMin){
					magnitude = -xMin;
				}
				int midValue = (magnitude+1)/2;
				
				for(int block=lastBlockDecoded; block<blocksPerSegment ; block++){	
					values[block] = midValue;
				}
			} else if (completionMode == 2){
				//first we compute the mean value of the previously decoded values, and we round it
				int meanValue = 0;
				for(int block=0 ; block<lastBlockDecoded; block++){
					meanValue += values[block];
				}
				meanValue =  Math.round( meanValue /(float) lastBlockDecoded);
				for(int block=lastBlockDecoded; block<blocksPerSegment ; block++){	
					values[block] = meanValue;
				}
			} else if (completionMode > 2){
//				first we compute the mean value of the n-2 previously decoded values, and we round it
				int meanValue = 0;
				int initBlock = 0;
				if (lastBlockDecoded>completionMode-2){
					initBlock = lastBlockDecoded - (completionMode-2) ; 
				}
				for(int block=initBlock; block<lastBlockDecoded; block++){
					meanValue += values[block];
				}
				meanValue =  Math.round( meanValue /(float) (lastBlockDecoded-initBlock));
				for(int block=lastBlockDecoded; block<blocksPerSegment ; block++){	
					values[block] = meanValue;
				}
			}
		}
		
		return values;
	}


	/**
	 * This function unquantizes the decoded values.
	 * 
	 * @param values contain an array of integers that must be dequantized
	 * @param N number of bits needed to represent the values.
	 * 
	 * @return the array of integers after the dequantizing process
	 */
	public int[] unQuantize(int[] values, int N){
		int maxValue = (int) (1<<N);//2^{N}
		for( int block=0 ; block < blocksPerSegment ; block++ ){
			if (values[block] < 0) {
				values[block] =  values[block] + maxValue;
			}
			values[block] = values[block] << q ;
		}
		return values;
	}

	
	/**
	 * Set the quantization factor for initially DC coding.
	 */
	public void setDinamicRange(){
		q=0;
		//Note that bitDepthAC / 2 = (int) Math.floor( ((float) bitDepthAC) / 2 )
		if (bitDepthAC == 0){
			q=0;
		} else if( bitDepthDC <= 3){
			q = 0 ;
		} else if( bitDepthDC - ( 1 + (bitDepthAC/2) ) <= 1 ){
			q = bitDepthDC - 3 ;
		} else if( bitDepthDC - ( 1 + (bitDepthAC/2) ) > 10 ){
			q = bitDepthDC - 10 ; 
		} else {
			q = 1 + ( bitDepthAC/2 ) ;
		} 

		if( q < BPLL ){
			//if unnecessary bitplanes due to the weighting process
			//are going to be encoded, it is corrected here.
			q = BPLL;
		}
		if (bitDepthDC - q > 10){
			q = bitDepthDC -10;
		}
		
	}

	/**
	 * Get the number of quantized bit planes for DC components.
	 * 
	 * @return an integer containing the number of bitplanes quantized for DC components
	 */
	public int getDinamicRange(){
		return q;
	}
	
	///////////////////////////////////////
	////////// GET FUNCTIONS //////////////
	///////////////////////////////////////
	public boolean getAbnormalTermination(){
		return abnormalTermination;
	}

}
