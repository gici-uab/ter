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


/**
 * This class decodes as many generations of AC components of a block as user requires for a given bitplane. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 * &nbsp; get functions<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.05
 */
public class DecodeGeneration{
	
	/**
	 * This object is used to entropy decode the words that are needed in decoding process.
	 */
	EntropyDecoderAC encodedSegment = null;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * This array contains AC components of the recovered block. 
	 * Usage : block[resolutionLevel][family][x][y]
	 */
	int[][][][] block = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#blockStatus}
	 */
	byte[][][][] blockStatus = null ;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#BP}
	 */
	int[] BP = null ;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#bitDepthAC_Block}
	 */
	int bitDepthAC_Block ;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#resolutionLevels}
	 */
	int resolutionLevels;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#significantPiramid}
	 */
	byte significantPiramid[][] = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#Ds}
	 */
	byte Ds;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#D}
	 */
	byte D[]= null;
	
	/**
	 * Indicates in which gaggle is located the block to be decoded
	 */
	int gaggle ;
	
	/**
	 * Definition in {@link SegmentDecode2D#gammaValue}
	 */
	float gammaValue;
	
	/**
	 * Constructor that receives the object used to entropy decode the input bit stream
	 * 
	 * @param encodedSegment {@link #encodedSegment}
	 */
	public DecodeGeneration(EntropyDecoderAC encodedSegment){
		this.encodedSegment = encodedSegment;
	}
	
	/**
	 * Set the parameters required to decode the next generation
	 * 
	 * @param block definition in {@link #block}
	 * @param blockStatus definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#blockStatus}
	 * @param bitDepthAC_Block definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#bitDepthAC_Block}
	 * @param BP definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#BP}
	 * @param resolutionLevels definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#resolutionLevels}
	 * @param gaggle definition in {@link #gaggle} 
	 * @param gammaValue definition in {@link SegmentDecode2D#gammaValue}
	 */
	public void setParameters(int[][][][] block, byte[][][][] blockStatus, int bitDepthAC_Block, 
			int[] BP, int resolutionLevels, int gaggle, float gammaValue){
		
		this.BP = BP;
		this.bitDepthAC_Block = bitDepthAC_Block;
		this.resolutionLevels = resolutionLevels;
		this.gaggle = gaggle;
		this.gammaValue = gammaValue;
		
		if (block != null){
			this.block = block;
		}
		
		if (blockStatus != null){
			this.blockStatus = blockStatus;
		} else {
			initBlockStatus(); 
		}
		parametersSet = true;		
	}
	
	/**
	 * Decodes as many generation as the user requires
	 * 
	 * @param bitPlane indicates the bit plane that must be decoded
	 * @param initResolutionLevel indicates the resolution level where decoding process begins 
	 * @param endResolutionLevel indicates the last resolution level to be decoded
	 * @param significantPiramid definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#significantPiramid}
	 * @param Ds definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#Ds}
	 * @param D definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#D}
	 * 
	 * @throws Exception when there appears some problems and the decoding of the block must be stopped
	 */
	public void run(int bitPlane, int initResolutionLevel, int endResolutionLevel,
			byte[][] significantPiramid, byte Ds, byte D[])	throws Exception {
		// If parameters are not set run cannot be executed
		if (!parametersSet) {
			throw new ParameterException(
			"DecodeBlockAC cannot run if parameters are not set.");
		}
		
		this.significantPiramid = significantPiramid;
		this.Ds = Ds;
		this.D = D;

		updateLeastSignificantBP(bitPlane);
		for (int rLevel = initResolutionLevel; rLevel <= endResolutionLevel; rLevel++) {
			if (rLevel == 0){
				decodeParents(bitPlane);
			} else if ( rLevel == 1){
				setPreviousSignificance(bitPlane);
				decodeChildren(bitPlane);
			} else {
				decodeGeneration(rLevel, bitPlane);
			}
		}
	}
	
	/**
	 * Given a bitplane updates the significance piramid. Note that this only must be done once for each bit plane
	 * 
	 * @param bitPlane indicates the bit plane that must be decoded
	 */
	private void setPreviousSignificance(int bitPlane){
		int families = 3;
		
		// fisrt, we look for the subbands that must be encoded and mark the components
		// that become significant in this bitPlane
		for (int rLevel = 0; rLevel < resolutionLevels; rLevel++) {
			int sizeResolutionLevel = block[rLevel][0].length;
			for (int subband = 0; subband < families; subband++) {
				for (int y = 0; y < sizeResolutionLevel; y++) {
					for (int x = 0; x < sizeResolutionLevel; x++) {
						if (blockStatus[rLevel][subband][y][x] > significantPiramid[rLevel][subband]){
							significantPiramid[rLevel][subband] = blockStatus[rLevel][subband][y][x];
						}
					}
				}
			}
		}

		for (int rLevel = resolutionLevels-2; rLevel >= 1; rLevel--) {
			for (int subband = 0; subband < families; subband++ ) {
				if (significantPiramid[rLevel][subband] < significantPiramid[rLevel+1][subband]){
					significantPiramid[rLevel][subband] = significantPiramid[rLevel+1][subband];
				}
			}
		}

	}
	
	/**
	 * Gets the recovered value for each bit plane.
	 * 
	 * @param bitPlane indicates the bitplane that is being decoded
	 * @param rLevel indicates the resolution level of the subband to be encoded
	 * @param subband determines the subband once is known the resolution level
	 *
	 * @return an integer that represents the recovered value
	 */
	private int getRecoveryValue(int bitPlane, int rLevel, int subband){
		int subbandNumber = (3*rLevel) + subband + 1;
		int recoveryValue = ((int) 1 << bitPlane);// 2^bitPlane = threshold
		if (bitPlane>BP[subbandNumber]){
			int gamma = Math.round(gammaValue*recoveryValue);
			recoveryValue = recoveryValue + gamma;
		} 
		return recoveryValue;
	}
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#bitPlaneOfZeros(int, int, int)}
	 */
	private boolean bitPlaneOfZeros(int rLevel, int subband, int bitPlane){
		boolean zeros;
		int subbandNumber = (3*rLevel) + subband + 1;
		if (BP[subbandNumber] > bitPlane){
			zeros = true;
		} else {
			zeros = false;
		}
		return zeros;
	}
	
	/**
	 * Decode parents of the block
	 * 
	 * @param bitPlane indicates the bitplane that is being decoded
	 * 
	 * @throws Exception when it is not possible to finish the decoding process for parents
	 */
	private void decodeParents(int bitPlane) throws Exception{
		//int threshold =getThreshold(bitPlane); 

		int families = 3;
//		 parents are decoded
		int wordLength = 0;
		for (int subband = 0; subband < families; subband++) {
			if (blockStatus[0][subband][0][0] == 0) {
				wordLength++;
			} 
		}
		 
		if ( wordLength != 0 ){
			int word = encodedSegment.getWord(0,wordLength,false,gaggle); 
			// get the word containing the significance of the parents
			wordLength = 0;
			for (int subband = families-1; subband >= 0; subband--) {
				if (blockStatus[0][subband][0][0] == 0 ) {
					if (word % 2 == 1 ){// The coefficient is significant
						blockStatus[0][subband][0][0] = 1;
						wordLength++;
					} //else { The coefficient is not significant}
					word = word >> 1 ;
				} 
			}		
		}
		if ( wordLength != 0 ){
			int word = encodedSegment.getWord(1,wordLength,false,gaggle);
			for (int subband = families-1; subband >= 0; subband--) {
				if (blockStatus[0][subband][0][0] == 1) {
					if (word % 2 == 1 ){// The coefficient is negative
						block[0][subband][0][0] = -getRecoveryValue(bitPlane,0,subband);
					} else{ // The coefficient is not possitve
						block[0][subband][0][0] = getRecoveryValue(bitPlane,0,subband);
					}
					word = word >> 1 ;
				} 
			}
		}	
	}
	
	/**
	 * Decode children of the block
	 * 
	 * @param bitPlane indicates the bitplane that is being decoded
	 * 
	 * @throws Exception when it is not possible to finish the decoding process for children
	 */
	private void decodeChildren(int bitPlane) throws Exception{
		//int threshold =((int) 1 << bitPlane);// 2^bitPlane + 2(bitPlane-1)
		int families = 3; 
		int rLevel = 1;
		// children are decoded
		// Ds is set and encoded if needed
		if ( Ds==0 ) {
			//There were no significant descendants in the previous bitplanes
			if (encodedSegment.getWord(0,1,false,gaggle)==1){
				Ds = 1;
				
			}
		} else {
			//The descendants have become significant in previous bitplanes
			Ds = 2;
		}
		if (Ds>0){	
			// each subband is decoded
			int wordLength = 0;
			for (int subband = 0; subband < families; subband++) {
				
				if ( significantPiramid[1][subband] == 0 && !bitPlaneOfZeros(this.resolutionLevels-1,subband,bitPlane)){
					// this subband at this resolutionLevel has not become significant in previous bit planes 
					// and the previous reloution level is significant
					wordLength ++;
				}
				
			}
			
			int word;
			if (wordLength !=0 ){
				word = encodedSegment.getWord(0,wordLength,true,gaggle);
			} else {
				word = 0;
			}
			
			for (int subband = families-1; subband >= 0; subband--) {
				if (significantPiramid[rLevel][subband] == 0 && !bitPlaneOfZeros(this.resolutionLevels-1,subband,bitPlane)) {
					if (word % 2 == 1 ){// The subband become significant in this bit plane
						significantPiramid[rLevel][subband] = 1;
						D[subband] = 2;
					} //else { The subband does not become significant in this bit plane}
					word = word >> 1 ;
				}
			}
			


			for(int subband = 0; subband < 3 ; subband++){
				int recoveryValue = getRecoveryValue(bitPlane,rLevel,subband);
				if (significantPiramid[rLevel][subband]!=0  && !bitPlaneOfZeros(rLevel,subband,bitPlane)){
					wordLength = 0;
					for(int k=0; k<4 ; k++){ // we look how many insignificant children must be decoded
						if (blockStatus[rLevel][subband][k/2][k%2]==0){
							wordLength++;
						}
					}	
					if (wordLength != 0) {
						word = encodedSegment.getWord(0,wordLength,false,gaggle);
						
						int signLength = 0;
						for(int k=3; k>=0 ; k--){// the significance (of the previous insignificant children) is stated 
							if (blockStatus[rLevel][subband][k/2][k%2]==0){
								int bit = word%2;
								if (bit == 1){
									blockStatus[rLevel][subband][k/2][k%2] = 1;
									signLength++;
								}
								word = word >> 1;
							}
						}
						if (signLength != 0){
							word = encodedSegment.getWord(1,signLength,false,gaggle);
							for(int k=3; k>=0 ; k--){ // the sign of the children that become significant are given
								if (blockStatus[rLevel][subband][k/2][k%2]==1){
									int bit = word%2;
									if (bit == 1){
										block[rLevel][subband][k/2][k%2] = -recoveryValue;
									} else {
										block[rLevel][subband][k/2][k%2] = recoveryValue;
									}
									word = word >> 1;
								}
							}
						}
					}
				}
			}
			
			
		}
	}
	
	/**
	 * Decode generation greater than children of the block
	 * 
	 * @param bitPlane indicates the bitplane that is being decoded
	 * 
	 * @throws Exception when it is not possible to finish the decoding process for the generation
	 */
	private void decodeGeneration(int rLevel, int bitPlane) throws Exception{
		int families  = 3;
		int wordLength = 0;
		for (int subband = 0; subband < families; subband++) {
			if ( D[subband] == 2){
				if ( significantPiramid[rLevel][subband] == 0 && !bitPlaneOfZeros(rLevel,subband,bitPlane) ){
					// this subband at this resolutionLevel has not become significant in previous bit planes 
					// and the previous reloution level is significant
					wordLength ++;
				}
			}
		}
		
		if (wordLength !=0 ){
			boolean impossiblePattern = false;

			int word = encodedSegment.getWord(0,wordLength,impossiblePattern,gaggle);
			
			for (int subband = families-1; subband >= 0; subband--) {
				if (D[subband] == 2){
					if ( significantPiramid[rLevel][subband] == 0 && !bitPlaneOfZeros(rLevel,subband,bitPlane) ){
						if (word % 2 == 1 ){// The subband become significant in this bit plane
							significantPiramid[rLevel][subband] = 1;
						} // else { The subband does not become significant in this bit plane}
						word = word >> 1 ;
					}
				}
			}
		}
		
		if ( significantPiramid[rLevel][0]>0 || significantPiramid[rLevel][1]>0 || significantPiramid[rLevel][2]>0 ){
			
			int xSize = block[rLevel][0][0].length;
			
			byte decodeSquare[][][] = new byte[rLevel+1][3][];
			
			for (int pass = 0; pass <= rLevel ; pass++){
				int numberOfWords = (1 << (2*pass));
				for(int subband = 0; subband < 3 ; subband++){
					if (significantPiramid[rLevel][subband]>0 && !bitPlaneOfZeros(rLevel,subband,bitPlane) ){// The subband must be decoded
						decodeSquare[pass][subband] = new byte[numberOfWords];
					}
				}
			}
			
			
			for(int subband = 0; subband < 3 ; subband++){
				if (significantPiramid[rLevel][subband]>0 && !bitPlaneOfZeros(rLevel,subband,bitPlane) ){// The subband must be decoded
					int numberOfSquares = (1 << (2*rLevel));
					for(int sq=0; sq < numberOfSquares; sq+=4){
						int quadrantxSize = xSize / 2;
						int x = 0;
						int y = 0;
						int positionInQuadrant = sq;
						int componentsInQuadrant = numberOfSquares/4;
						while ( componentsInQuadrant > 1 ){
							int whichQuadrant = positionInQuadrant / componentsInQuadrant ;
							positionInQuadrant = positionInQuadrant % componentsInQuadrant;
							componentsInQuadrant = componentsInQuadrant / 4;
							if ( whichQuadrant == 1 ){
								x += quadrantxSize;
							} else if ( whichQuadrant == 2 ){
								y += quadrantxSize;
							} else if ( whichQuadrant == 3 ){
								x += quadrantxSize;
								y += quadrantxSize;
							}// else if ( whichQuadrant == 0 ) // nothing should be added
							quadrantxSize = quadrantxSize >> 1;
						}
						for (int i = 0; i < 4; i++) {
							decodeSquare[rLevel][subband][sq+i] = blockStatus[rLevel][subband][y+i/2][x+i%2];
						}
						
					}
					for (int pass = rLevel; pass > 0 ; pass--){
						int numberOfWords = (1 << (2*pass));
						for(int k=0; k<numberOfWords; k++){
							if (decodeSquare[pass-1][subband][k/4] < decodeSquare[pass][subband][k] ){ 
								// this square has significant descendants 
								decodeSquare[pass-1][subband][k/4] = decodeSquare[pass][subband][k];
							}
						}
					}
					
					decodeSquare[0][subband][0] = significantPiramid[rLevel][subband];
				}
				
			}
			for (int pass = 1; pass < rLevel ; pass++){//squares are decoded, no sign must be decoded
				for(int subband = 0; subband < 3 ; subband++){
					if (significantPiramid[rLevel][subband]>0 && !bitPlaneOfZeros(rLevel,subband,bitPlane) ){// The subband must be decoded
						int numberOfSquares = (1 << (2*pass));
						wordLength = 0;
						for(int sq=0; sq<numberOfSquares; sq++){
							if (decodeSquare[pass-1][subband][sq/4] > 0 && decodeSquare[pass][subband][sq] == 0){ 
								// this square must be decoded
								wordLength++;
								}
							if (sq%4==3 && wordLength!=0 ){
								int word = 0;
								if (wordLength == 4){
									word = encodedSegment.getWord(0,wordLength,true,gaggle);
								} else if (wordLength != 0){
									word = encodedSegment.getWord(0,wordLength,false,gaggle);
								}
								for(int i=0; i<4; i++){
									if (decodeSquare[pass-1][subband][sq/4] > 0 && decodeSquare[pass][subband][sq -i] == 0){ 
										// this square must be decoded
										if (word%2==1){
											decodeSquare[pass][subband][sq -i] = 1;
										} 
										word = word >> 1;
									}
								}
								wordLength = 0;	 
							}
						}
					}
				}
			}
			//Now components are decoded
			for(int subband = 0; subband < 3 ; subband++){
				int recoveryValue = getRecoveryValue(bitPlane,rLevel,subband);
				if (significantPiramid[rLevel][subband]>0 && !bitPlaneOfZeros(rLevel,subband,bitPlane) ){// The subband must be decoded
					int numberOfSquares = (1 << (2*rLevel));
					wordLength = 0;
					for(int sq=0; sq<numberOfSquares; sq++){
						if (decodeSquare[rLevel-1][subband][sq/4] > 0 && decodeSquare[rLevel][subband][sq] == 0){ 
							// this square must be decoded
							wordLength++;
						}
						if (sq%4==3 && wordLength!=0){
							int word = 0;
							int signLength = 0;
							if (wordLength == 4){
								word = encodedSegment.getWord(0,wordLength,true,gaggle);
							} else if (wordLength != 0){
								word = encodedSegment.getWord(0,wordLength,false,gaggle);
							}
							for(int i=0; i<4; i++){
								if (decodeSquare[rLevel-1][subband][sq/4] > 0 && decodeSquare[rLevel][subband][sq -i] == 0){ 
									// this square must be decoded
									if (word%2==1){
										decodeSquare[rLevel][subband][sq -i] = 1;
										signLength++;
									} 
									word = word >> 1;
								}
							}
							
							if (signLength != 0){
								word = encodedSegment.getWord(1,signLength,false,gaggle);
								// first we find the position of the square inside the block
								int quadrantxSize = xSize / 2;
								int x = 0;
								int y = 0;
								int positionInQuadrant = sq-3;
								int componentsInQuadrant = numberOfSquares/4;
								while ( componentsInQuadrant > 1 ){
									int whichQuadrant = positionInQuadrant / componentsInQuadrant ;
									positionInQuadrant = positionInQuadrant % componentsInQuadrant;
									componentsInQuadrant = componentsInQuadrant / 4;
									if ( whichQuadrant == 1 ){
										x += quadrantxSize;
									} else if ( whichQuadrant == 2 ){
										y += quadrantxSize;
									} else if ( whichQuadrant == 3 ){
										x += quadrantxSize;
										y += quadrantxSize;
									}// else if ( whichQuadrant == 0 ) // nothing should be added
									quadrantxSize = quadrantxSize >> 1;
								}
								for(int i=0; i<4; i++){
									if (decodeSquare[rLevel][subband][sq-i] == 1){ 
										int k=3-i;
										
										if (word%2==1){
											block[rLevel][subband][ y + k/2 ][ x + k%2 ] = -recoveryValue;
											blockStatus[rLevel][subband][ y + k/2 ][ x + k%2 ] = 1;
										} else {
											block[rLevel][subband][ y + k/2 ][ x + k%2 ] = recoveryValue;
											blockStatus[rLevel][subband][ y + k/2 ][ x + k%2 ] = 1;
										}
										word = word >> 1;
									}
								}
							}
							wordLength = 0;	 
						}
					}
				}
			}
		}
	}
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#initBlockStatus()}
	 */
	private void initBlockStatus(){
		int families = 3 ;
		this.blockStatus = new byte[resolutionLevels][families][][];
		//System.out.println("!!!");
		for(int rLevel = 0; rLevel<resolutionLevels; rLevel++){
			int sizeResolutionLevel = (int) (1 << rLevel);
			//System.out.println("rLevel "+rLevel);
			for(int subband=0; subband<families ; subband++){
				//System.out.println("subband "+subband);
				blockStatus[rLevel][subband] = new byte[sizeResolutionLevel][sizeResolutionLevel];
				for(int y=0; y<sizeResolutionLevel; y++){
					for(int x=0; x<sizeResolutionLevel; x++){
						blockStatus[rLevel][subband][y][x] = 0;
					}
				}
			}
		}
	}
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#updateLeastSignificantBP(int)}
	 */
	public void updateLeastSignificantBP(int bitPlane) {
		
		for(int rLevel=0; rLevel<this.resolutionLevels ; rLevel++){
			int sizeResolutionLevel = block[rLevel][0].length;
			for (int subband = 0; subband < 3; subband++) {
				int subbandNumber = (3*rLevel + subband)+1;
				// residual subband is not considered while
				// coding AC components
				if (bitPlane < BP[subbandNumber] ) {
					//System.out.println("\n subband "+subband+", rLevel"+rLevel+" subbandNumber "+subbandNumber+" BP[subbandNumber] "+BP[subbandNumber]+" bitPlane"+bitPlane);
					// all the values in this are necessarily zero due to the weigthing stage
					// and should not be further encoded
					for (int y = 0; y < sizeResolutionLevel; y++) {
						for (int x = 0; x < sizeResolutionLevel; x++) {
							blockStatus[rLevel][subband][y][x] = -1;
						}
					}
				}
			}
		}
	}
	

	/////////////////////////
	//// GET FUNCTIONS //////
	/////////////////////////
	
	public int[][][][] getBlock(){
		return block;
	}
	
	public byte[][][][] getBlockStatus(){
		return blockStatus;
	}
	
	public byte[][] getSignificantPiramid(){
		return this.significantPiramid;
	}

	public byte getDs(){
		return this.Ds;
	}
	
	public byte[] getD() {
		return D;
	}
}
