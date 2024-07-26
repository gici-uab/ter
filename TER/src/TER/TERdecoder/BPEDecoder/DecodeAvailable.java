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
import GiciImageExtension.ImageExtension;

public class DecodeAvailable{
	
	
	float recoveredImage[][][] = null;
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	int zSize, yOriginalSize, xOriginalSize;
	
	int WTType[] = null;
	int WTLevels[] = null;
	int resolutionLevels[] = null;
	
	int customWtFlag[] = null;
	float customWeight[][] = null;
	int BP[][] = null;
	
	int blocksPerSegment[][] = null;
	
	//real size of each gaggle; for DC gaggleSize[z][segment][0] and for AC   gaggleSize[z][segment][1]
	int gaggleSize[][][][] = null;
	int gaggleSizeAC[][] = null;
	int gaggleSizeDC[][] = null;
	int entropyAC[][] = null;
	
	int bitDepthDC[][] = null;
	int bitDepthAC[][] = null;
	
	byte packet[][][][][] = null;
	
	int completionMode[];
	float gammaValue[] = null;
	
	public DecodeAvailable(byte packet[][][][][]){
		this.packet = packet;
	}
	
	public void setParameters(int zSize, int  yOriginalSize, int xOriginalSize,
			int imageExtensionType[], 
			int WTType[], int WTLevels[], int resolutionLevels[],
			int customWtFlag[], float customWeight[][],
			int blocksPerSegment[][],int gaggleSizeDC[][],int gaggleSizeAC[][], int entropyAC[][],
			int bitDepthDC[][], int bitDepthAC[][],
			int completionMode[], float gammaValue[]){
		
		this.zSize = zSize;
		this.yOriginalSize = yOriginalSize;
		this.xOriginalSize = xOriginalSize;
		
		this.WTType = WTType;
		this.WTLevels = WTLevels;
		this.resolutionLevels = resolutionLevels;
		
		this.customWtFlag = customWtFlag;
		this.customWeight = customWeight;
		setBP();
		
		this.blocksPerSegment = blocksPerSegment;
		this.gaggleSizeDC = gaggleSizeDC;
		this.gaggleSizeAC = gaggleSizeAC;
		this.entropyAC = entropyAC;
		
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC;
		
		setGaggleSize();
		
		this.completionMode = completionMode;
		this.gammaValue = gammaValue;
		
		this.recoveredImage = new float[zSize][][];
		
		int nonDecodedResolutionLevels[] = new int[zSize];
		for(int z=0;z<zSize;z++){
			nonDecodedResolutionLevels[z] = WTLevels[z]+1 -  resolutionLevels[z];
		}
		boolean needExtension = ImageExtension.needImageExtension(imageExtensionType,WTLevels,xOriginalSize,yOriginalSize,zSize);
		if (needExtension){
			for(int z=0;z<zSize;z++){
				int linesToAdd = 0;
				int columnsToAdd = 0;
				
				int requiredLines = ((int) 1 << this.WTLevels[z]); //2^(WTLevels[z]) 
				int requiredColumns = ((int) 1 << this.WTLevels[z]); //2^(WTLevels[z])
				
				if( yOriginalSize%requiredLines!=0){ 
					linesToAdd = requiredLines - yOriginalSize%requiredLines;
				}
				if( xOriginalSize%requiredColumns!=0 ){
					columnsToAdd = requiredColumns - xOriginalSize%requiredColumns;
				}
				
				int extendedySize = ((yOriginalSize + linesToAdd)>> nonDecodedResolutionLevels[z]) ;
				int extendedxSize = ((xOriginalSize + columnsToAdd)>> nonDecodedResolutionLevels[z]);
				this.recoveredImage[z] = new float[extendedySize][extendedxSize];
			}
		} else {
			for(int z=0;z<zSize;z++){
				int extendedySize = ((yOriginalSize)>> nonDecodedResolutionLevels[z]) ;
				int extendedxSize = ((xOriginalSize)>> nonDecodedResolutionLevels[z]);
				this.recoveredImage[z] = new float[extendedySize][extendedxSize];
			}
		}
		
		parametersSet = true;
	}
	
	public float[][][] run() throws Exception{
		if(!parametersSet){
			throw new ParameterException("Decode Available cannot run if parameters are not properly set.");
		}
		
		//packet[channel][segment][rLevel][gaggle][layer][]
		if (packet!=null){
			DecodeGaggleDC dc = new DecodeGaggleDC(recoveredImage);
			for(int z=0;z<zSize;z++){
				if (resolutionLevels[z]>0){
					//DC components are decoded
					if (packet[z]!=null){
						for(int segment=0;segment<blocksPerSegment[z].length;segment++){
							if (packet[z][segment]!=null){							
								if (packet[z][segment][0]!=null){
									for(int gaggle=0;gaggle<packet[z][segment][0].length;gaggle++){
										if (packet[z][segment][0][gaggle]!=null){
											dc.setParameters(packet[z][segment][0][gaggle],
													z, segment, gaggle,
													gaggleSize[z][segment][0][gaggle], gaggleSizeDC[z][segment],
													bitDepthDC[z][segment], bitDepthAC[z][segment],
													blocksPerSegment[z],
													resolutionLevels[z], BP[z][0],
													completionMode[z], gammaValue[z]);
											dc.run();						
										}
									}
								}
							}
							
						}
					}
				}
			}
			this.recoveredImage = dc.getRecoveredImage();
			dc = null;
			
			ACEntropyDecoder ea = new ACEntropyDecoder(packet);
			DecodeGaggleAC ac = new DecodeGaggleAC(recoveredImage);
			
			for(int z=0;z<zSize;z++){
				if (resolutionLevels[z]>=1){
					if (packet[z]!=null){
						for(int segment=0;segment<blocksPerSegment[z].length;segment++){
							if (packet[z][segment]!=null && packet[z][segment].length>1){							
								for(int gaggle=0;gaggle<packet[z][segment][1].length;gaggle++){						
									//AC components must be decoded
									ea.setParameters(z,segment,gaggle,resolutionLevels[z],entropyAC[z][segment]);
									ac.setParameters(ea,
											z, segment,gaggle,
											gaggleSize[z][segment][1][gaggle], gaggleSizeAC[z][segment],
											bitDepthAC[z][segment],
											blocksPerSegment[z], WTLevels[z],
											resolutionLevels[z], BP[z],gammaValue[z]);
									ac.run();
								}
							}
						}
					}
				}
			}
			recoveredImage = ac.getRecoveredImage();
			
			ea = null;
			ac = null;
		}
		return recoveredImage;
	}
	
	public void setGaggleSize(){
		gaggleSize = new int[zSize][][][];
		for(int z=0;z<zSize;z++){
			gaggleSize[z] = new int[blocksPerSegment[z].length][2][];
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				gaggleSize[z][segment][0] = new int[getGagglesPerSegment(z, segment, gaggleSizeDC[z][segment])];
				for(int gaggle=0;gaggle<gaggleSize[z][segment][0].length;gaggle++){
					if (gaggle<blocksPerSegment[z][segment]/gaggleSizeDC[z][segment]){
						gaggleSize[z][segment][0][gaggle] = gaggleSizeDC[z][segment];
					} else {
						gaggleSize[z][segment][0][gaggle] = blocksPerSegment[z][segment]%gaggleSizeDC[z][segment];
					}
				}
				gaggleSize[z][segment][1] = new int[getGagglesPerSegment(z, segment, gaggleSizeAC[z][segment])];
				for(int gaggle=0;gaggle<gaggleSize[z][segment][1].length;gaggle++){
					if (gaggle<blocksPerSegment[z][segment]/gaggleSizeAC[z][segment]){
						gaggleSize[z][segment][1][gaggle] = gaggleSizeAC[z][segment];
					} else {
						gaggleSize[z][segment][1][gaggle] = blocksPerSegment[z][segment]%gaggleSizeAC[z][segment];
					}
				}
			}
		}
	}
	
	private int getGagglesPerSegment(int channel, int segment, int gaggleSize){
		int numberOfGaggles = blocksPerSegment[channel][segment] / gaggleSize;
		if ( blocksPerSegment[channel][segment]%gaggleSize != 0 ){
			numberOfGaggles++ ;
		}
		return numberOfGaggles;
	}
	
	private void setBP(){
		BP = new int[zSize][];
		for(int z=0;z<zSize;z++){
			BP[z] = new int[3*WTLevels[z]+1];
			for(int subband = 0 ; subband < 3*WTLevels[z]+1 ; subband++){
				if(customWtFlag[z]==0 && WTType[z]==4) {
					BP[z][subband] = ( WTLevels[z] - subband/3 );
				} else if( customWtFlag[z] == 1){
					//BP[subband] = 0;
					//this point must be improved. There should be a way to find whether the 
					//user-given weights produce zeros in the leasts significant bitplanes.
					BP[z][subband] = (int) Math.round(Math.log(customWeight[z][subband])/Math.log(2));
				} else {
					BP[z][subband] = 0;
				}
				
			}
			
		}
	}
}