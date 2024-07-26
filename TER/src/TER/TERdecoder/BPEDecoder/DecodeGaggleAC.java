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

import java.io.EOFException;

import GiciException.*;
import TER.TERdecoder.SegmentDecoder.InitialDecoding;

public class DecodeGaggleAC{
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;

	int channel, segment, gaggle, gaggleSize, gaggleSizeAC;
	
	int resolutionLevels;
	
	int bitDepthAC;
	
	int BP[];
	
	float gammaValue;
	
	boolean minusHalf = false;
	
	float recoveredImage[][][] = null;
	
	int blocksPerSegment[] = null;
	
	int bitDepthAC_block[] = null;
	
	int bitPlaneStop, stageStop;
	
	ACEntropyDecoder entropyDecoder = null;
	
	int xInit[]= null, yInit[] = null;

	
	int WTLevels;
	
	public DecodeGaggleAC(float recoveredImage[][][]){
		this.recoveredImage = recoveredImage;
	}
	
	public void setParameters(ACEntropyDecoder entropyDecoder,
			int channel, int segment, int gaggle,
			int gaggleSize, int gaggleSizeAC,
			int bitDepthAC,
			int blocksPerSegment[],
			int WTLevels,
			int resolutionLevels, int BP[],
			float gammaValue)  throws Exception {
			
			this.entropyDecoder = entropyDecoder;
		
			this.channel = channel;
			this.segment = segment;
			this.gaggle = gaggle;
			
			this.gaggleSize = gaggleSize;
			this.gaggleSizeAC = gaggleSizeAC;
			
			this.bitDepthAC = bitDepthAC;
			this.blocksPerSegment = blocksPerSegment;
			this.resolutionLevels = resolutionLevels;
			this.WTLevels = WTLevels;
			this.BP = BP;
			
			this.gammaValue = gammaValue;
			
			
			
		
		parametersSet = true;
	}
	
	
	public void run() throws Exception {
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("DecodeGaggleAC cannot run if parameters are not properly set.");
		}
		
		
		//first we decode the bit depth of each block in the gaggle
		InitialDecoding idcAC = new InitialDecoding();
		idcAC.setParameters(gaggleSize, 0, bitDepthAC, gaggleSizeAC, gaggleSize, BP[0], false,0);
		bitDepthAC_block = idcAC.run(entropyDecoder.getLayerParents());
		
		
		
		
		// if the decoding has been succesfull we continue the decoding process
		if (!idcAC.getAbnormalTermination()){
			DecodeGenerationAC dac = new DecodeGenerationAC(recoveredImage);
			RefineGenerationAC rac = new RefineGenerationAC(recoveredImage);
			boolean completedSortingPass[] = new boolean[resolutionLevels];
			boolean completedRefinementPass[] = new boolean[resolutionLevels];
			for(int k=0;k<resolutionLevels;k++){
				completedSortingPass[k] = true;
				completedRefinementPass[k] = true;
			}
			byte blockStatus[][][][][] = new byte[gaggleSize][][][][];
			byte significantPiramid[][][] = initSignificance();
			byte Ds[] = initDs();
			byte D[][] = initD();
			for(int bitplane = bitDepthAC-1 ; bitplane >= bitPlaneStop ; bitplane-- ){
				entropyDecoder.resetIDs();
				
				for (int rLevel=1; rLevel<resolutionLevels; rLevel++){ //stages 1 to resolutionLevels
					// sorting decoding
					for(int block=0;  block<gaggleSize ; block++){
						if(bitplane < bitDepthAC_block[block]){// in other case is the bitPlane has not been encoded
							if (completedSortingPass[rLevel-1] && completedSortingPass[rLevel]){
								setInitBlock(block,rLevel-1);
								dac.setParameters(entropyDecoder, blockStatus[block], 
										WTLevels, rLevel, BP,
										xInit, yInit, channel,
										gammaValue,minusHalf);
								try{
									dac.run(bitplane,significantPiramid[block],Ds[block],D[block]);
								} catch (EOFException e){
									completedSortingPass[rLevel] = false;
									completedRefinementPass[rLevel] = false;
								}
								
								blockStatus[block] = dac.getBlockStatus();
								significantPiramid[block] = dac.getSignificantPiramid();
								Ds[block] = dac.getDs();
								D[block] = dac.getD();
							} else {
								completedSortingPass[rLevel] = false;
								completedRefinementPass[rLevel] = false;
							}
						}
					}
				}
				
				for (int rLevel=1; rLevel<resolutionLevels; rLevel++){ //refinement stage
					// refinement decoding
					for(int block=0;  block<gaggleSize ; block++){
						if(bitplane < bitDepthAC_block[block]-1){// in other case is the bitPlane has not been refined	
							if (completedRefinementPass[rLevel]){
								setInitBlock(block,rLevel-1);
								rac.setParameters(entropyDecoder,blockStatus[block],WTLevels,rLevel,
										BP,xInit,yInit,channel,gammaValue,minusHalf);
								try{
									rac.run(bitplane);
								} catch (EOFException e){
									completedRefinementPass[rLevel] = false;
								}
								blockStatus[block] = rac.getBlockStatus();
							}
						}
					}
				}
			}
			rac = null;
			dac = null;
			blockStatus = null;
			significantPiramid = null;
			Ds = null;
			D = null;
			completedSortingPass = null;
			completedRefinementPass = null;
		}
		idcAC = null;
		parametersSet = false;//parameters must be update before running again the class
	}
	
	private void setInitBlock(int block, int generation){
		
		if (xInit == null){
			xInit = new int[3];
		}
		if (yInit == null ){
			yInit = new int[3];
		}
		
		int xSize = recoveredImage[channel][0].length;
		int ySize = recoveredImage[channel].length;

		int generalBlockPosition = gaggleSizeAC*gaggle + block;
		
		int x0 = generalBlockPosition % ((int) xSize >> WTLevels);
		int y0 = generalBlockPosition / ((int) xSize >> WTLevels);
		
		int sizeResolutionLevel = ((int) 1 << generation);
		int xSubBandSize = ((int) xSize >> (WTLevels - generation));
		int ySubBandSize = ((int) ySize >> (WTLevels - generation));
		
		// family 0. Corresponding to HL subband
		// (xInit,yInit) value where the block at the subband HL starts
		xInit[0] = x0 * sizeResolutionLevel + xSubBandSize;
		yInit[0] = y0 * sizeResolutionLevel;

		// family 1. Corresponding to LH subband
		// (xInit,yInit) value where the block at the subband LH starts
		xInit[1] = x0 * sizeResolutionLevel;
		yInit[1] = y0 * sizeResolutionLevel + ySubBandSize;

		// family 2. Corresponding to HH subband
		// (xInit,yInit) value where the block at the subband HH starts
		xInit[2] = x0 * sizeResolutionLevel + xSubBandSize;
		yInit[2] = y0 * sizeResolutionLevel + ySubBandSize;
		
	}

	

	public byte[][][] initSignificance(){
		
		byte[][][] map = new byte[gaggleSize][WTLevels][3];

		for(int block=0;block<gaggleSize;block++){
			for(int rLevel=0;rLevel<WTLevels;rLevel++){
				for(int subband=0;subband<3;subband++){
					map[block][rLevel][subband]=0;
				}
			}
		}
	
		return map;
	}
	

	public byte[] initDs(){
		byte[] Ds = new byte[gaggleSize];
		
		for(int block=0;  block<gaggleSize ; block++){
			Ds[block] = 0;				
		}
		
		return Ds;
	}	


	public byte[][] initD(){

		byte[][] D = new byte[gaggleSize][3];
		
		for(int block=0;block<gaggleSize;block++){
			for(int subband=0; subband<3;subband++){
				D[block][subband] = 0;
			}
		}

		
		return D;
	}
	
	//////////////////////////////////
	///////// GET FUNCTIONS //////////
	//////////////////////////////////
	
	public float[][][] getRecoveredImage(){
		return recoveredImage;
	}
}