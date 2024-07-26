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
 * This class refines a bitplane of a block of AC components as the user requires. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 * &nbsp; get functions<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.1
 */

public class RefineACs{
	
	/**
	 * Definition in {@link SegmentDecode2D#encodedSegment}
	 */
	EntropyDecoderAC encodedSegment = null;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * Definition in {@link DecodeGeneration#block}
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
	 * Indicates the gaggle that is being decoded
	 */
	int gaggle;
	
	/**
	 * Definition in {@link  SegmentDecode2D#gammaValue}
	 */
	float gammaValue;
	
	/**
	 * Constructor of the class RefineACs.
	 * 
	 * @param encodedSegment {@link SegmentDecode2D#encodedSegment}
	 */
	public RefineACs(EntropyDecoderAC encodedSegment){
		this.encodedSegment = encodedSegment;
	}
	
	
	/**
	 * Set the parameters required to refine AC components
	 *  
	 * @param block definition in {@link DecodeGeneration#block}
	 * @param blockStatus definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#blockStatus}
	 * @param bitDepthAC_Block definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#bitDepthAC_Block}
	 * @param BP definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#BP}
	 * @param gaggle definition in {@link #gaggle}
	 * @param gammaValue definition in {@link SegmentDecode2D#gammaValue}
	 */
	public void setParameters(int[][][][] block, byte[][][][] blockStatus, int bitDepthAC_Block, int[] BP, 
			int gaggle, float gammaValue){
		this.gaggle = gaggle;
		this.BP = BP;
		this.bitDepthAC_Block = bitDepthAC_Block;
		this.gammaValue = gammaValue;
		
		if (block != null){
			this.block = block;
		}
		
		if (blockStatus != null){
			this.blockStatus = blockStatus;
		} 
		
		parametersSet = true;		
	}
	
	
	/**
	 * Get the value to be added or substracted to get the desired approximation of the refined value 
	 * 
	 * @param bitPlane indicates the bit plane that must be refined
	 * @param lastBitPlane indicates if this bit plane is the last to be refined
	 * 
	 * @return an integer that contains the value to be added or substracted to reach the desired approximation
	 */
	private int getGamma(int bitPlane, boolean lastBitPlane){
		int gamma = 0;
		int threshold = ((int) 1 << bitPlane);
		if(!lastBitPlane){
			gamma = Math.round(gammaValue*threshold);
		} else {//This is the last bitplane, hence remaining bitplanes are zeros.
			gamma = 0;
		}
		return gamma;
	}
	/**
	 * Decode as many generations as the user indicates in the parameters 
	 * 
	 * @param bitPlane indicates the bit plane that must be refined
	 * @param initResolutionLevel indicates the resolution level where the decoding process begins
	 * @param endResolutionLevel indicates the resolution level where the decoding process must be stoped
	 * 
	 * @throws Exception when something goes wrong and the AC decoding must be stoped
	 */
	public void run(int bitPlane, int initResolutionLevel, int endResolutionLevel) throws Exception{
		// If parameters are not set run cannot be executed
		if (!parametersSet) {
			throw new ParameterException(
			"Blocks cannot be refined if parameters are not set.");
		}
		int families = 3;
		int threshold = ((int) 1 << bitPlane);
		for (int rLevel = initResolutionLevel; rLevel <= endResolutionLevel; rLevel++) {
			// refinement pass
			int sizeResolutionLevel = block[rLevel][0].length;
			
			for (int subband = 0; subband < families; subband++) {
				int gamma = getGamma(bitPlane,lastBitPlane(bitPlane,rLevel,subband));
				int previousGamma = getGamma(bitPlane+1,lastBitPlane(bitPlane+1,rLevel,subband));
				int refinementBit = 0;
				if (rLevel!=2){
					for (int y = 0; y < sizeResolutionLevel; y++) {
						for (int x = 0; x < sizeResolutionLevel; x++) {
							if (blockStatus[rLevel][subband][y][x] == 2) {
								// the coefficient has been found significant in previous
								// bitplanes, and should be refined.
								if(encodedSegment.getWord(2,1,false,gaggle)==1){
									refinementBit = 1;
								} else {
									refinementBit = 0;
								}
								if (block[rLevel][subband][y][x] < 0){
									block[rLevel][subband][y][x] =
										block[rLevel][subband][y][x] + previousGamma - threshold*refinementBit - gamma;
								} else {
									block[rLevel][subband][y][x] =
										block[rLevel][subband][y][x] - previousGamma + threshold*refinementBit + gamma;
								}
							} else if (blockStatus[rLevel][subband][y][x] == 1) {
								// the coefficient has been found significant in this
								// bitplane, and should be refined in the following bitplanes
								blockStatus[rLevel][subband][y][x] = 2;
							}
						}
					}
				} else {
					for (int y = 0; y < 2; y++) {
						for (int x = 0; x < 2; x++) {
							if (blockStatus[rLevel][subband][y][x] == 2) {
								// the coefficient has been found significant in previous
								// bitplanes, and should be refined.
								if(encodedSegment.getWord(2,1,false,gaggle)==1){
									refinementBit = 1;
								} else {
									refinementBit = 0;
								}
								if (block[rLevel][subband][y][x] < 0){
									block[rLevel][subband][y][x] =
										block[rLevel][subband][y][x] + previousGamma - threshold*refinementBit - gamma;
								} else {
									block[rLevel][subband][y][x] =
										block[rLevel][subband][y][x] - previousGamma + threshold*refinementBit + gamma;
								}
							} else if (blockStatus[rLevel][subband][y][x] == 1) {
								// the coefficient has been found significant in this
								// bitplane, and should be refined in the following bitplanes
								blockStatus[rLevel][subband][y][x] = 2;
							}
						}
					}
					for (int y = 0; y < 2; y++) {
						for (int x = 2; x < 4; x++) {
							if (blockStatus[rLevel][subband][y][x] == 2) {
								// the coefficient has been found significant in previous
								// bitplanes, and should be refined.
								if(encodedSegment.getWord(2,1,false,gaggle)==1){
									refinementBit = 1;
								} else {
									refinementBit = 0;
								}
								if (block[rLevel][subband][y][x] < 0){
									block[rLevel][subband][y][x] =
										block[rLevel][subband][y][x] + previousGamma - threshold*refinementBit - gamma;
								} else {
									block[rLevel][subband][y][x] =
										block[rLevel][subband][y][x] - previousGamma + threshold*refinementBit + gamma;
								}
							} else if (blockStatus[rLevel][subband][y][x] == 1) {
								// the coefficient has been found significant in this
								// bitplane, and should be refined in the following bitplanes
								blockStatus[rLevel][subband][y][x] = 2;
							}
						}
					}
					for (int y = 2; y < 4; y++) {
						for (int x = 0; x < 2; x++) {
							if (blockStatus[rLevel][subband][y][x] == 2) {
								// the coefficient has been found significant in previous
								// bitplanes, and should be refined.
								if(encodedSegment.getWord(2,1,false,gaggle)==1){
									refinementBit = 1;
								} else {
									refinementBit = 0;
								}
								if (block[rLevel][subband][y][x] < 0){
									block[rLevel][subband][y][x] =
										block[rLevel][subband][y][x] + previousGamma - threshold*refinementBit - gamma;
								} else {
									block[rLevel][subband][y][x] =
										block[rLevel][subband][y][x] - previousGamma + threshold*refinementBit + gamma;
								}
							} else if (blockStatus[rLevel][subband][y][x] == 1) {
								// the coefficient has been found significant in this
								// bitplane, and should be refined in the following bitplanes
								blockStatus[rLevel][subband][y][x] = 2;
							}
						}
					}
					for (int y = 2; y < 4; y++) {
						for (int x = 2; x < 4; x++) {
							if (blockStatus[rLevel][subband][y][x] == 2) {
								// the coefficient has been found significant in previous
								// bitplanes, and should be refined.
								if(encodedSegment.getWord(2,1,false,gaggle)==1){
									refinementBit = 1;
								} else {
									refinementBit = 0;
								}
								if (block[rLevel][subband][y][x] < 0){
									block[rLevel][subband][y][x] =
										block[rLevel][subband][y][x] + previousGamma - threshold*refinementBit - gamma;
								} else {
									block[rLevel][subband][y][x] =
										block[rLevel][subband][y][x] - previousGamma + threshold*refinementBit + gamma;
								}
							} else if (blockStatus[rLevel][subband][y][x] == 1) {
								// the coefficient has been found significant in this
								// bitplane, and should be refined in the following bitplanes
								blockStatus[rLevel][subband][y][x] = 2;
							}
						}
					}
					
					
					
				}
			}
		}
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

	/////////////////////////////
	//////GET FUNCTIONS /////////
	/////////////////////////////
	
	public int[][][][] getBlock(){
		return block;
	}	
	
	public byte[][][][] getBlockStatus(){
		return blockStatus;
	}
}