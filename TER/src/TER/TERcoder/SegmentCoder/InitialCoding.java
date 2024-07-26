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
 * This class code the quantized DC components or the bitDepthAC_Block. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 * &nbsp; get functions<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class InitialCoding{
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * This array contains the values to be encoded.
	 */
	int valuesToCode[] = null;
	
	/**
	 * Definition in {@link SegmentCode2D#bitDepthAC}
	 */	
	int bitDepthAC;
	
	/**
	 * Definition in {@link SegmentCode2D#bitDepthDC}
	 */	
	int bitDepthDC;
	
	/**
	 * This integer determines the number of blocks in the segment
	 */	
	int blocksPerSegment;
	
	/**
	 * Specifies the method employed to select value 
	 * of k parameters when coding the valuesToCode 
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 -  Recommended heuristic selection
	 *     <li> 1 -  Optimum (exhaustive) selection
	 *     <li> 2 -  Cristina's heuristic selection
	 *     <li> 3 -  Fernando's heuristic selection
	 *   </ul>
	 */
	int optSelect;
	
	
	/**
	 * Specifies the size of a gaggle for the coded values
	 * <p>
	 * Valid values are positive values.
	 */
	int gaggleSize;
	
	
	/**
	 * Specifies the frequency of appareance of id in the segment.
	 * <p>
	 * Valid values are positive values. 0 value means that only one id is used 
	 * in the segment.
	 */
	int id ;
	
	/**
	 * For the residual subband indicates the number of bitplanes that are necessary zero
	 * <p>
	 * Negative values are not allowed.
	 */
	int BPLL;
	
	/**
	 * Indicate if the valuesToCode are DC components (true) or not (false).
	 *
	 */
	boolean isDC;
	
	/**
	 * Definition in {@link SegmentCode2D#distortion}
	 */
	DistortionCompute distortion = null;
	
	/**
	 * This integer indicates the number of bitplanes quantized for DC components
	 */
	int q;
	
	
	boolean computeDistortion = false;
	
	/**
	 * Constructor that receives the values to be encoded.
	 *
	 * @param valuesToCode defintion in {@link #valuesToCode}
	 *
	 */
	public InitialCoding(int[] valuesToCode){
		blocksPerSegment = valuesToCode.length;
		
		this.valuesToCode = new int[blocksPerSegment];
		for (int block=0; block<blocksPerSegment; block++){ 
			this.valuesToCode[block] = valuesToCode[block];
		}
	}
	
	/**
	 * Set the parameters used to perform the initial coding of DC or bitDepthACBlock.
	 *
	 * @param bitDepthDC {@link SegmentCode2D#bitDepthDC}
	 * @param bitDepthAC {@link SegmentCode2D#bitDepthAC}
	 * @param optSelect {@link #optSelect}
	 * @param gaggleSize {@link #gaggleSize}
	 * @param id {@link #id}
	 * @param BPLL {@link #BPLL}
	 * @param isDC {@link #isDC}
	 * @param distortion {@link #distortion}
	 */
	public void setParameters(int bitDepthDC, int bitDepthAC, int optSelect, int gaggleSize, int id,
			int BPLL, boolean isDC , DistortionCompute distortion){
		
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC;	  
		this.optSelect = optSelect;
		this.gaggleSize = gaggleSize;
		this.id = id;
		this.BPLL = BPLL;
		this.isDC = isDC;
		this.distortion = distortion;
		
		if ( this.id == 0 ){
			this.id = blocksPerSegment;
		}
		
		parametersSet = true;
		
	}
	
	/**
	 * Run the initial coding of either DC components or bitDepthAC_Block
	 *
	 * @return a bit stream array containing the values coded according to the parameters set.
	 *
	 * @throws ErrorException when something goes wrong, for example parameters are not set, and compression must be stopped.
	 *
	 */
	public BitStream[] run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ErrorException("Segment cannot be intially encoded if parameters are not set.");
		}
		
		BitStream codedValues[] = null;
		
		int N, xMin, xMax;
		if (isDC){
			// parameters are set and DC components are quantized
			setDinamicRange();
			N = 1;
			if ( N < bitDepthDC - q ) { // number of bits needed to represent each quantized DC component
				N = bitDepthDC - q ;
			}
			
			xMin = - ( (int) 1 << (N-1) ); //-2^(N-1)
			xMax = ( (int) 1 << (N-1) ) -1 ; //2^(N-1) -1

			quantizeDCs(N);
		} else {
			N = (int) ( Math.ceil( Math.log(1+bitDepthAC) / Math.log(2) ) );
			xMin = 0; 
			xMax = ( (int) 1 << N ) -1 ; //2^(N) -1
		}
		
		
		if( N > 1 ){
			int mappedDPCM[] = doDPCMandMap(xMin,xMax);
			codedValues = entropyCode(mappedDPCM, N);
		} else if ( N == 1 ){
			int numberOfGaggles = blocksPerSegment / gaggleSize;
			if ( blocksPerSegment%gaggleSize != 0 ){
				numberOfGaggles++ ;
			}
			codedValues = new BitStream[numberOfGaggles];
			for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
				codedValues[gaggle] = new BitStream();
				for( int block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<blocksPerSegment ; block++ ){
					if ( valuesToCode[block] != 0 && valuesToCode[block] !=-1) {
						throw new ErrorException("In this case all values should be represented in one single bit.!!");
					}
					codedValues[block/gaggleSize].addBit(valuesToCode[block]);
				}
			}
		}

		return codedValues;
	}
	
	/**
	 * This function entropy codes the given values according to the paramateres selected by the user.
	 *
	 * @param values array containing the values to be entropy coded.
	 * @param N number of bits needed to represent the values.
	 *
	 * @return an array of bit streams which return the values entropy coded.
	 */
	public BitStream[] entropyCode(int[] values, int N){
		int numberOfGaggles = blocksPerSegment / gaggleSize;
		if ( blocksPerSegment%gaggleSize != 0 ){
			numberOfGaggles++ ;
		}
		int codeOptionLength = getCodeOptionLength(N);
		
		BitStream codedValues[] = new BitStream[numberOfGaggles];
		for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
			codedValues[gaggle] = new BitStream();
			int codeOption = getCodeOption(values, gaggle, codeOptionLength, N);
			codedValues[gaggle].addBits(codeOption,codeOptionLength);
			if ( codeOption == ((int) 1 << codeOptionLength ) - 1 ){ 
				//uncoded option has been choosen, the components are encoded using raw data
				for( int block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<values.length ; block++ ){
					codedValues[gaggle].addBits(values[block], N);
				}
			} else {
				// first part words are encoded (Fundamental Sequence)
				for( int block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<values.length ; block++ ){
					if( block % id == 0){
						//the value to be encoded is an id, which can not be entropy encoded
						codedValues[gaggle].addBits(values[block], N);
					} else {
						int firstPart = values[block] >> codeOption ; 
						//the N least significant bits are discarded in the first part words
						//remaining bits are encoded using a fundamental sequence
						for(int k=0; k<firstPart; k++){
							codedValues[gaggle].addBit(0);
						}
						codedValues[gaggle].addBit(1);
					}
				}
				// second part words
				for( int block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<values.length ; block++ ){
					if( block % id != 0){
						int mod = ( (int) 1 << codeOption );
						int secondPart = values[block] % mod ;
						codedValues[gaggle].addBits(secondPart, codeOption);
					} // if ( block % id == 0) the value is an id, which is encoded in the first part words
				}				
			}
		}
		
		return codedValues;
	}
	
	/**
	 * This funtion selects for each gaggle the number of bits that should be split in the entropy coder
	 *
	 * @param values array containing the values to be entropy encoded.
	 * @param gaggle indicates the gaggle that is going to be encoded.
	 * @param codeLength indicates the length of the code (i.e. the number of bits needed to represent the code option).
	 * @param N number of bits needed to represent the values.
	 *
	 * @return an integer that represents the option to be taken for the entropy coder
	 */
	public int getCodeOption(int[] values, int gaggle, int codeLength, int N){
		int uncodedOption = ((int) 1 << codeLength ) - 1 ;//(2^CodeLenght)-1 Uncoded option
		int code = uncodedOption; //if no option is selected, no code is applied 
		if ( optSelect == 0 ){ //heuristic mode has been chosen
			int sum = 0 ;
			for( int block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<values.length ; block++ ){
				if( block % id != 0){
					sum += values[block];
				} // if ( block % id == 0) the value is an id, which is encoded in the first part words in raw mode
			}
			if ( 64*sum >= 23*gaggleSize*Math.pow(2,N) ){
				code = uncodedOption ;//(2^CodeLenght)-1 Uncoded option
			} else if ( 207*gaggleSize > 128*sum ) {
				code = 0;
			} else if ( gaggleSize*Math.pow(2,N-5) <= 128*sum + 49*gaggleSize ){
				// in my opinion this condition is not properly selected, if desactivated results are better.
				code = N - 2 ;
			} else {
				int k = N - 2 ;
				while( gaggleSize*Math.pow(2,k+7) > 128*sum + 49*gaggleSize && k>0){
					k--;
				}
				code = k;
			}
		} else if ( optSelect == 1 ){//Optimum (exhaustive) selection
			code = uncodedOption; //first we take into consideration the uncoded option
			int countBits = N*gaggleSize;
			for(int k=0;k<N-1;k++){// all the possible cases are studied
				int bitsCounter = 0;
				int valuesCounter = 0;
				for( int block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<values.length ; block++ ){
					if( block % id != 0){
						int firstPart = values[block] >> k ; 
						bitsCounter += firstPart + 1 ;
						valuesCounter++;
					} else {
						bitsCounter += N;
					}// if ( block % id == 0) the value is an id, which is encoded in the first part words in raw mode
				}
				bitsCounter += valuesCounter*k; 
				if ( bitsCounter < countBits ){
					countBits = bitsCounter;
					code = k;
				}
			}
		} else if( optSelect == 2) { //Cristina's heuristic selection
			int extraZeros = 0;
			int countValues = 0;
			for( int block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<values.length ; block++ ){
				if( block % id != 0){
					if (values[block] != 0){
						extraZeros += Math.ceil( Math.log( 1 + values[block] ) / Math.log(2) );
					}
					countValues++;
				} // if ( block % id == 0) the value is an id, which is encoded in the first part words in raw mode
			}
			if ( countValues!=0 ){
				extraZeros = extraZeros/countValues;
			}
			code = N - (extraZeros + 1);
			if ( code>0 ){
				code--;
			}
			if ( code > uncodedOption ){
				code = uncodedOption;
			}
		} else if( optSelect == 3 ){//Fernando's heuristic selection
			int meanValue = 0;
			int countValues = 0;
			for( int block = gaggle*gaggleSize ; block<(gaggle+1)*gaggleSize && block<values.length ; block++ ){
				if( block % id != 0){
					meanValue += values[block];
					countValues++;
				} // if ( block % id == 0) the value is an id, which is encoded in the first part words in raw mode
			}
			if ( countValues !=0 ){
				meanValue = meanValue / countValues ;
			}
			code = (int) Math.ceil( Math.log( 1 + meanValue ) / Math.log(2) );
			if ( code>0 ){
				code--;
			}
			if ( code > uncodedOption ){
				code = uncodedOption;
			}
		} else if ( optSelect == 4 ){
			code = uncodedOption ;
		}
		
		return code;
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
	 * This function performs DPCM and maps obtained values into non-negative integers.
	 *
	 * @param xMin reprensents the minimum value that could be taken by the input values.
	 * @param xMax reprensents the maximum value that could be taken by the input values.
	 *
	 * @return an integer array containing the DPCM and mapped values.
	 */
	public int[] doDPCMandMap(int xMin, int xMax){
		int delta[] = new int[blocksPerSegment];
		for(int block=0; block<blocksPerSegment; block++){
			if ( block%id == 0){//this is a reference value and not should be modified
				delta[block] = valuesToCode[block];
			} else {
				delta[block] = valuesToCode[block] - valuesToCode[block-1];
				//once DPCM is applied, the result is mapped into non negative integers
				int theta = xMax - valuesToCode[block-1];
				if ( theta > (valuesToCode[block-1] - xMin) ){
					theta = valuesToCode[block-1] - xMin ;
				}
				if ( delta[block] >= 0 && delta[block] <= theta ){
					delta[block] = 2 * delta[block];
				} else if ( delta[block] >= -theta && delta[block] < 0 ){
					delta[block] = -(2 * delta[block]) - 1 ;
				} else { 
					if (delta[block] < 0 ){
						delta[block] = theta - delta[block];
					} else {
						delta[block] = theta + delta[block];
					}
				}
			}
		} 
		return delta;
	}
	
	/**
	 * This function quantizes (and computes distortion if needed) the initial values.
	 * 
	 * @param N number of bits to required to represent quantized DCs in two's-complement
	 *
	 */
	public void quantizeDCs(int N){
		for( int block=0 ; block < blocksPerSegment ; block++ ){
			int initialValue = valuesToCode[block];
			
			valuesToCode[block] = valuesToCode[block] >> q;
			
			if (computeDistortion){
				distortion.initNewValue(initialValue);
				distortion.improvedValue(0,(valuesToCode[block]<<q),initialValue);
			}
			
			
			int midValue = (int) (1<<(N-1));//2^{N-1}
			
			if(valuesToCode[block]>=midValue){
				valuesToCode[block] = valuesToCode[block] - 2*midValue ;
			}
		}
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
	 */
	public int getDinamicRange(){
		return q;
	}
}
