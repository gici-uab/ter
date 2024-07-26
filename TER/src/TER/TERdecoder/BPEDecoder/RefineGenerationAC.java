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

public class RefineGenerationAC{
	
	ACEntropyDecoder entropyDecoder = null;
	float recoveredImage[][][] = null;
	
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
	
	int xInit[]= null, yInit[] = null;
	int channel;
	
	int generation; //in this class the resolution levels of AC components start at 0 in order to simplify notation, that's why we call generation
	int WTLevels;
	int BP[] = null;
	
	int threshold;
	float gamma;
	boolean minusHalf;
	
	public RefineGenerationAC(float recoveredImage[][][]){
		this.recoveredImage = recoveredImage;
		
	}
	
	public void setParameters(ACEntropyDecoder entropyDecoder,
			byte blockStatus[][][][], 
			int WTLevels, int resolutionLevel,
			int BP[],
			int xInit[], int yInit[], int channel,
			float gamma, boolean minusHalf){
		
		this.entropyDecoder = entropyDecoder;
		
		this.blockStatus = blockStatus;
		
		this.generation = resolutionLevel - 1 ;
		this.WTLevels = WTLevels;
		
		this.BP = BP; 
		
		this.gamma = gamma;
		this.minusHalf = minusHalf;
		
		this.xInit = xInit;
		this.yInit = yInit;
		this.channel = channel;
		
		
		parametersSet = true;
	}
	
	/**
	 * Decode as many generations as the user indicates in the parameters 
	 * 
	 * @param bitplane indicates the bit plane that must be refined
	 * 
	 * @throws Exception when something goes wrong and the AC decoding must be stoped
	 */
	public void run(int bitplane) throws Exception{
		// If parameters are not set run cannot be executed
		if (!parametersSet) {
			throw new ParameterException("Blocks cannot be refined if parameters are not set.");
		}
		
		this.setThreshold(bitplane);
		int families = 3;
;
		// refinement pass
		int sizeResolutionLevel = blockStatus[generation][0].length;

		for (int subband = 0; subband < families; subband++) {
			float gammaT = getGamma(bitplane,lastBitPlane(bitplane,generation,subband));
			float previousGammaT = getGamma(bitplane+1,lastBitPlane(bitplane+1,generation,subband));
			int refinementBit = 0;
			if (generation!=2){
				for (int y = 0; y < sizeResolutionLevel; y++) {
					for (int x = 0; x < sizeResolutionLevel; x++) {
						if (blockStatus[generation][subband][y][x] == 2) {
							// the coefficient has been found significant in previous
							// bitplanes, and should be refined.
							if(entropyDecoder.getWord(generation,2,1,false)==1){
								refinementBit = 1;
							} else {
								refinementBit = 0;
							}
							setRefinedValue(subband,y,x,refinementBit,gammaT,previousGammaT);
						} 
					}
				}
			} else {
				for (int y = 0; y < 2; y++) {
					for (int x = 0; x < 2; x++) {
						if (blockStatus[generation][subband][y][x] == 2) {
							// the coefficient has been found significant in previous
							// bitplanes, and should be refined.
							if(entropyDecoder.getWord(generation,2,1,false)==1){
								refinementBit = 1;
							} else {
								refinementBit = 0;
							}
							setRefinedValue(subband,y,x,refinementBit,gammaT,previousGammaT);
						} 
					}
				}
				for (int y = 0; y < 2; y++) {
					for (int x = 2; x < 4; x++) {
						if (blockStatus[generation][subband][y][x] == 2) {
							// the coefficient has been found significant in previous
							// bitplanes, and should be refined.
							if(entropyDecoder.getWord(generation,2,1,false)==1){
								refinementBit = 1;
							} else {
								refinementBit = 0;
							}
							setRefinedValue(subband,y,x,refinementBit,gammaT,previousGammaT);
						} 
					}
				}
				for (int y = 2; y < 4; y++) {
					for (int x = 0; x < 2; x++) {
						if (blockStatus[generation][subband][y][x] == 2) {
							// the coefficient has been found significant in previous
							// bitplanes, and should be refined.
							if(entropyDecoder.getWord(generation,2,1,false)==1){
								refinementBit = 1;
							} else {
								refinementBit = 0;
							}
							setRefinedValue(subband,y,x,refinementBit,gammaT,previousGammaT);
						} 
					}
				}
				for (int y = 2; y < 4; y++) {
					for (int x = 2; x < 4; x++) {
						if (blockStatus[generation][subband][y][x] == 2) {
							// the coefficient has been found significant in previous
							// bitplanes, and should be refined.
							if(entropyDecoder.getWord(generation,2,1,false)==1){
								refinementBit = 1;
							} else {
								refinementBit = 0;
							}
							setRefinedValue(subband,y,x,refinementBit,gammaT,previousGammaT);
						} 
					}
				}
				
				
				
			}
		}
		
	}
	
	private void setThreshold(int bitplane){
		threshold = (int) 1 << bitplane;
	}
	
	private void setRefinedValue(int family, int y, int x, int refinementBit, float gammaT, float previousGammaT){
		
		if (recoveredImage[channel][yInit[family]+ y][xInit[family] + x] > 0){
			recoveredImage[channel][yInit[family]+ y][xInit[family] + x] += - previousGammaT + threshold*refinementBit + gammaT;
		} else {
			recoveredImage[channel][yInit[family]+ y][xInit[family] + x] += + previousGammaT - threshold*refinementBit - gammaT;
		}
			
	}
	
	/**
	 * Get the value to be added or substracted to get the desired approximation of the refined value 
	 * 
	 * @param bitPlane indicates the bit plane that must be refined
	 * @param lastBitPlane indicates if this bit plane is the last to be refined
	 * 
	 * @return an integer that contains the value to be added or substracted to reach the desired approximation
	 */
	private float getGamma(int bitPlane, boolean lastBitPlane){
		float gammaT = 0;
		int threshold = ((int) 1 << bitPlane);
		if(!lastBitPlane){
			gammaT = gamma*threshold;
		} else {//This is the last bitplane, hence remaining bitplanes are zeros.
			gammaT = 0;
		}
		return gammaT;
	}

	/**
	 * Indicates if a bitplane if the last for a given bit plane
	 * 
	 * @param bitPlane indicates the bit plane that is being refined
	 * @param rLevel indicates the resolution level that is being refined
	 * @param subband indicates the family that is being refined
	 * 
	 * @return a boolean indicating if this is the last bit plane to be refined for this subband
	 */
	private boolean lastBitPlane(int bitPlane, int rLevel, int subband){
		boolean lastBitPlane = true;
		int subbandNumber = (3*rLevel) + subband + 1;
		if (bitPlane>BP[subbandNumber]){
			lastBitPlane = false;
		} 
		return lastBitPlane;
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
}