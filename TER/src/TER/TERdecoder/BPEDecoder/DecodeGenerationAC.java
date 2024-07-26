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
package TER.TERdecoder.BPEDecoder;

import GiciException.*;

public class DecodeGenerationAC{
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#blockStatus}
	 */
	byte[][][][] blockStatus = null ;
	
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
	
	float recoveredImage[][][] = null;
	
	int xInit[]= null, yInit[] = null;
	int channel;
	int generation; //in this class the resolution levels of AC components start at 0 in order to simplify notation, that's why we call generation
	int WTLevels;
	int BP[] = null;
	
	float gamma;
	boolean minusHalf;
	
	ACEntropyDecoder entropyDecoder = null;
	

	
	public DecodeGenerationAC(float recoveredImage[][][]){
		this.recoveredImage = recoveredImage;
		
	}
	
	public void setParameters(ACEntropyDecoder entropyDecoder,
							byte blockStatus[][][][], 
							int WTLevels, int resolutionLevel,
							int BP[],
							int xInit[], int yInit[], int channel,
							float gamma, boolean minusHalf){
	
		this.entropyDecoder = entropyDecoder;
		
		this.generation = resolutionLevel - 1 ;
		this.WTLevels = WTLevels;
		
		this.BP = BP; 

		this.gamma = gamma;
		this.minusHalf = minusHalf;
		
		this.xInit = xInit;
		this.yInit = yInit;
		this.channel = channel;
		
		if (blockStatus != null){
			this.blockStatus = blockStatus;
		} else {
			initBlockStatus();
		}
		
		parametersSet = true;
	}
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#initBlockStatus()}
	 */
	private void initBlockStatus(){
		int families = 3 ;
		this.blockStatus = new byte[WTLevels][families][][];
		//System.out.println("!!!");
		for(int rLevel = 0; rLevel<WTLevels; rLevel++){
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
	
	public void run(int bitplane,byte[][] significantPiramid, byte Ds, byte D[]) throws Exception {
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("DecodeGenerationAC cannot run if parameters are not properly set.");
		}
		
		this.significantPiramid = significantPiramid;
		this.Ds = Ds;
		this.D = D;
		
		//updateLeastSignificantBP(bitplane);
		updateGenerationStatus(bitplane);
		if (generation == 0){
			decodeParents(bitplane);
		} else if ( generation == 1){
			setPreviousSignificance(bitplane);
			decodeChildren(bitplane);
		} else {
			decodeGeneration(bitplane);
		}
	}
	
	private float getRecoveryValue(int bitplane, int generation, int subband){
		int subbandNumber = (3*generation) + subband + 1;
		float recoveryValue = ((int) 1 << bitplane);// 2^bitPlane = threshold
		if (bitplane>BP[subbandNumber]){
			recoveryValue = recoveryValue + recoveryValue*gamma;
			if (minusHalf){
				recoveryValue -= 0.5F;
			}
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
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#updateLeastSignificantBP(int)}
	 */
	public void updateLeastSignificantBP(int bitPlane) {
		
		for(int generation=0; generation<WTLevels ; generation++){
			int sizeResolutionLevel = blockStatus[generation][0].length;
			for (int subband = 0; subband < 3; subband++) {
				int subbandNumber = (3*generation + subband)+1;
				// residual subband is not considered while
				// coding AC components
				if (bitPlane < BP[subbandNumber] ) {
					//System.out.println("\n subband "+subband+", rLevel"+rLevel+" subbandNumber "+subbandNumber+" BP[subbandNumber] "+BP[subbandNumber]+" bitPlane"+bitPlane);
					// all the values in this are necessarily zero due to the weigthing stage
					// and should not be further encoded
					for (int y = 0; y < sizeResolutionLevel; y++) {
						for (int x = 0; x < sizeResolutionLevel; x++) {
							blockStatus[generation][subband][y][x] = -1;
						}
					}
				}
			}
		}
	}

	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#updateLeastSignificantBP(int)}
	 */
	public void updateGenerationStatus(int bitPlane) {
		
		
		int sizeResolutionLevel = blockStatus[generation][0].length;
		for (int subband = 0; subband < 3; subband++) {
			int subbandNumber = (3*generation + subband)+1;
			// residual subband is not considered while
			// coding AC components
			if (bitPlane < BP[subbandNumber] ) {
				//System.out.println("\n subband "+subband+", rLevel"+rLevel+" subbandNumber "+subbandNumber+" BP[subbandNumber] "+BP[subbandNumber]+" bitPlane"+bitPlane);
				// all the values in this are necessarily zero due to the weigthing stage
				// and should not be further encoded
				for (int y = 0; y < sizeResolutionLevel; y++) {
					for (int x = 0; x < sizeResolutionLevel; x++) {
						blockStatus[generation][subband][y][x] = -1;
					}
				}
			} else {
				for (int y = 0; y < sizeResolutionLevel; y++) {
					for (int x = 0; x < sizeResolutionLevel; x++) {
						if (blockStatus[generation][subband][y][x] == 1){
							blockStatus[generation][subband][y][x] = 2;
						}
					}
				}
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
		for (int rLevel = 0; rLevel < WTLevels; rLevel++) {
			int sizeResolutionLevel = blockStatus[rLevel][0].length;
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

		for (int rLevel = WTLevels-2; rLevel >= 1; rLevel--) {
			for (int subband = 0; subband < families; subband++ ) {
				if (significantPiramid[rLevel][subband] < significantPiramid[rLevel+1][subband]){
					significantPiramid[rLevel][subband] = significantPiramid[rLevel+1][subband];
				}
			}
		}

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
			int word = entropyDecoder.getWord(generation,0,wordLength,false); 
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
			int word = entropyDecoder.getWord(generation,1,wordLength,false);
			for (int subband = families-1; subband >= 0; subband--) {
				if (blockStatus[0][subband][0][0] == 1) {
					if (word % 2 == 1 ){// The coefficient is negative
						setValue(subband,0,0,-getRecoveryValue(bitPlane,0,subband));
					} else{ // The coefficient is not possitve
						setValue(subband,0,0,getRecoveryValue(bitPlane,0,subband));
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
		// children are decoded
		// Ds is set and encoded if needed
		if ( Ds==0 ) {
			//There were no significant descendants in the previous bitplanes
			if (entropyDecoder.getWord(generation,0,1,false)==1){
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
				
				if ( significantPiramid[1][subband] == 0 && !bitPlaneOfZeros(this.WTLevels-1,subband,bitPlane)){
					// this subband at this resolutionLevel has not become significant in previous bit planes 
					// and the previous reloution level is significant
					wordLength ++;
				}
				
			}
			
			int word;
			if (wordLength !=0 ){
				word = entropyDecoder.getWord(generation,0,wordLength,true);
			} else {
				word = 0;
			}
			
			for (int subband = families-1; subband >= 0; subband--) {
				if (significantPiramid[generation][subband] == 0 && !bitPlaneOfZeros(this.WTLevels-1,subband,bitPlane)) {
					if (word % 2 == 1 ){// The subband become significant in this bit plane
						significantPiramid[generation][subband] = 1;
						D[subband] = 2;
					} //else { The subband does not become significant in this bit plane}
					word = word >> 1 ;
				}
			}
			


			for(int subband = 0; subband < 3 ; subband++){
				float recoveryValue = getRecoveryValue(bitPlane,generation,subband);
				if (significantPiramid[generation][subband]!=0  && !bitPlaneOfZeros(generation,subband,bitPlane)){
					wordLength = 0;
					for(int k=0; k<4 ; k++){ // we look how many insignificant children must be decoded
						if (blockStatus[generation][subband][k/2][k%2]==0){
							wordLength++;
						}
					}	
					if (wordLength != 0) {
						word = entropyDecoder.getWord(generation,0,wordLength,false);
						
						int signLength = 0;
						for(int k=3; k>=0 ; k--){// the significance (of the previous insignificant children) is stated 
							if (blockStatus[generation][subband][k/2][k%2]==0){
								int bit = word%2;
								if (bit == 1){
									blockStatus[generation][subband][k/2][k%2] = 1;
									signLength++;
								}
								word = word >> 1;
							}
						}
						if (signLength != 0){
							word = entropyDecoder.getWord(generation,1,signLength,false);
							for(int k=3; k>=0 ; k--){ // the sign of the children that become significant are given
								if (blockStatus[generation][subband][k/2][k%2]==1){
									int bit = word%2;
									if (bit == 1){
										setValue(subband,k/2,k%2,-recoveryValue);
									} else {
										setValue(subband,k/2,k%2, recoveryValue);
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
	private void decodeGeneration(int bitPlane) throws Exception{
		int families  = 3;
		int wordLength = 0;
		for (int subband = 0; subband < families; subband++) {
			if ( D[subband] == 2){
				if ( significantPiramid[generation][subband] == 0 && !bitPlaneOfZeros(generation,subband,bitPlane) ){
					// this subband at this resolutionLevel has not become significant in previous bit planes 
					// and the previous reloution level is significant
					wordLength ++;
				}
			}
		}
		
		if (wordLength !=0 ){
			boolean impossiblePattern = false;

			int word = entropyDecoder.getWord(generation,0,wordLength,impossiblePattern);
			
			for (int subband = families-1; subband >= 0; subband--) {
				if (D[subband] == 2){
					if ( significantPiramid[generation][subband] == 0 && !bitPlaneOfZeros(generation,subband,bitPlane) ){
						if (word % 2 == 1 ){// The subband become significant in this bit plane
							significantPiramid[generation][subband] = 1;
						} // else { The subband does not become significant in this bit plane}
						word = word >> 1 ;
					}
				}
			}
		}
		
		if ( significantPiramid[generation][0]>0 || significantPiramid[generation][1]>0 || significantPiramid[generation][2]>0 ){
			
			int xSize = blockStatus[generation][0][0].length;
			
			byte decodeSquare[][][] = new byte[generation+1][3][];
			
			for (int pass = 0; pass <= generation ; pass++){
				int numberOfWords = (1 << (2*pass));
				for(int subband = 0; subband < 3 ; subband++){
					if (significantPiramid[generation][subband]>0 && !bitPlaneOfZeros(generation,subband,bitPlane) ){// The subband must be decoded
						decodeSquare[pass][subband] = new byte[numberOfWords];
					}
				}
			}
			
			
			for(int subband = 0; subband < 3 ; subband++){
				if (significantPiramid[generation][subband]>0 && !bitPlaneOfZeros(generation,subband,bitPlane) ){// The subband must be decoded
					int numberOfSquares = (1 << (2*generation));
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
							decodeSquare[generation][subband][sq+i] = blockStatus[generation][subband][y+i/2][x+i%2];
						}
						
					}
					for (int pass = generation; pass > 0 ; pass--){
						int numberOfWords = (1 << (2*pass));
						for(int k=0; k<numberOfWords; k++){
							if (decodeSquare[pass-1][subband][k/4] < decodeSquare[pass][subband][k] ){ 
								// this square has significant descendants 
								decodeSquare[pass-1][subband][k/4] = decodeSquare[pass][subband][k];
							}
						}
					}
					
					decodeSquare[0][subband][0] = significantPiramid[generation][subband];
				}
				
			}
			for (int pass = 1; pass < generation ; pass++){//squares are decoded, no sign must be decoded
				for(int subband = 0; subband < 3 ; subband++){
					if (significantPiramid[generation][subband]>0 && !bitPlaneOfZeros(generation,subband,bitPlane) ){// The subband must be decoded
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
									word = entropyDecoder.getWord(generation,0,wordLength,true);
								} else if (wordLength != 0){
									word = entropyDecoder.getWord(generation,0,wordLength,false);
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
				float recoveryValue = getRecoveryValue(bitPlane,generation,subband);
				if (significantPiramid[generation][subband]>0 && !bitPlaneOfZeros(generation,subband,bitPlane) ){// The subband must be decoded
					int numberOfSquares = (1 << (2*generation));
					wordLength = 0;
					for(int sq=0; sq<numberOfSquares; sq++){
						if (decodeSquare[generation-1][subband][sq/4] > 0 && decodeSquare[generation][subband][sq] == 0){ 
							// this square must be decoded
							wordLength++;
						}
						if (sq%4==3 && wordLength!=0){
							int word = 0;
							int signLength = 0;
							if (wordLength == 4){
								word = entropyDecoder.getWord(generation,0,wordLength,true);
							} else if (wordLength != 0){
								word = entropyDecoder.getWord(generation,0,wordLength,false);
							}
							for(int i=0; i<4; i++){
								if (decodeSquare[generation-1][subband][sq/4] > 0 && decodeSquare[generation][subband][sq -i] == 0){ 
									// this square must be decoded
									if (word%2==1){
										decodeSquare[generation][subband][sq -i] = 1;
										signLength++;
									} 
									word = word >> 1;
								}
							}
							
							if (signLength != 0){
								word = entropyDecoder.getWord(generation,1,signLength,false);
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
									if (decodeSquare[generation][subband][sq-i] == 1){ 
										int k=3-i;
										
										if (word%2==1){
											setValue(subband,y + k/2,x + k%2,-recoveryValue);
											blockStatus[generation][subband][ y + k/2 ][ x + k%2 ] = 1;
										} else {
											setValue(subband,y + k/2,x + k%2,recoveryValue);
											blockStatus[generation][subband][ y + k/2 ][ x + k%2 ] = 1;
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
	
	private void setValue(int family, int y, int x, float value){
		recoveredImage[channel][yInit[family]+ y][xInit[family] + x] = value;
	}
	
	//////////////////////////////////
	///////// GET FUNCTIONS //////////
	//////////////////////////////////
	
	public float[][][] getRecoveredImage(){
		return recoveredImage;
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