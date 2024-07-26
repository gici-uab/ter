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
package TER.TERcoder.WriteFile;

import GiciStream.*;
import GiciException.*;

/**
 * 
 *  
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 2.0
 */
public class LayerCreation{
	
	BitStream[][][][] allLayers = null;
	int layersOffset[][][][][] = null;
	
	boolean parametersSet = false ;
	
	BitStream initialCodedDC[][][] = null;
	BitStream codedBitDepthACBlock[][][] = null; 
	BitStream refinementDC[][][][] = null;
	BitStream sortingAC[][][][][] = null;
	BitStream terRefinementAC[][][][][] = null;
	
	int bitDepthDC[][] = null;
	int bitDepthAC[][] = null;
	
	int zSize;
	int blocksPerSegment[][] = null;
	int resolutionLevels[] = null;                      
	int numberOfLayers;
	int currentLayer = 0;
	int layersBits[] = null;
	int layerSizeType;
	int layerBytes[] = null;
	
	int layerCreationType;
	int targetBytes;
	
	/**
	 * Constructor of LayerCreation
	 */
	public LayerCreation(){

	}
	
	public void setParameters(BitStream initialCodedDC[][][], BitStream refinementDC[][][][],
			BitStream codedBitDepthACBlock[][][], BitStream sortingAC[][][][][], BitStream terRefinementAC[][][][][],
			int bitDepthDC[][], int bitDepthAC[][],
			int zSize, int blocksPerSegment[][], int resolutionLevels[], 
			int numberOfLayers, int layerCreationType, int targetBytes,
			int layerSizeType, int layerBytes[]){
	
		this.initialCodedDC = initialCodedDC;
		this.refinementDC = refinementDC;
		this.codedBitDepthACBlock = codedBitDepthACBlock;
		this.sortingAC = sortingAC;
		this.terRefinementAC = terRefinementAC;
		
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC;
		
		this.zSize = zSize;
		this.blocksPerSegment = blocksPerSegment;
		this.resolutionLevels = resolutionLevels;
		
		this.numberOfLayers = numberOfLayers;
		this.layerCreationType = layerCreationType;
		this.targetBytes = targetBytes;
		
		this.layerBytes = layerBytes;
		this.layerSizeType = layerSizeType;
		
		initAllLayers();
		
		parametersSet = true;
	}	
	
	public void run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("Layers cannnot be created if parameters are not set.");
		}
		
		if (layerCreationType == 0){
			oneSingleLayer();
		} else if (layerCreationType == 1){
			maxLengthlayer();
		} else if (layerCreationType == 2){
			codingPassesInterleaving();
		} else if (layerCreationType == 3){
			bitPlaneInterleaving();
		} 

			
	}
	
	private void initAllLayers(){
		allLayers = new BitStream[zSize][][][];
		
		for(int z=0;z<zSize;z++){	
			allLayers[z] = new BitStream[blocksPerSegment[z].length][][];
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				allLayers[z][segment] = new BitStream[resolutionLevels[z]+1][];
				for(int rLevel=0;rLevel<resolutionLevels[z]+1;rLevel++){
					if (rLevel==0){
						allLayers[z][segment][rLevel] = new BitStream[initialCodedDC[z][segment].length];
						for(int gaggle=0;gaggle<initialCodedDC[z][segment].length;gaggle++){
							allLayers[z][segment][rLevel][gaggle] = new BitStream();
						}
					} else {
						if (sortingAC!=null){
							if (sortingAC[z]!=null){
								if (sortingAC[z][segment]!=null){
									allLayers[z][segment][rLevel] = new BitStream[sortingAC[z][segment].length];
									for(int gaggle=0;gaggle<sortingAC[z][segment].length;gaggle++){
										allLayers[z][segment][rLevel][gaggle] = new BitStream();
									}
								}
							}
						}
					}
				}
			}
		}		
	}
	
	private void initLayersOffset(){
		
		layersOffset = new int[zSize][][][][];
		for(int z=0;z<zSize;z++){	
			layersOffset[z] = new int[blocksPerSegment[z].length][][][];
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				layersOffset[z][segment] = new int[resolutionLevels[z]+1][][];
				for(int rLevel=0;rLevel<resolutionLevels[z]+1;rLevel++){
					if (rLevel==0){
						layersOffset[z][segment][rLevel] = new int[initialCodedDC[z][segment].length][numberOfLayers+1];
						for(int gaggle=0;gaggle<initialCodedDC[z][segment].length;gaggle++){
							for(int layer=0;layer<numberOfLayers+1;layer++){
								layersOffset[z][segment][rLevel][gaggle][layer] = 0;
							}
						}
					} else {
						if (sortingAC!=null){
							if (sortingAC[z]!=null){
								if (sortingAC[z][segment]!=null){
									layersOffset[z][segment][rLevel] = new int[sortingAC[z][segment].length][numberOfLayers+1];
									for(int gaggle=0;gaggle<sortingAC[z][segment].length;gaggle++){
										for(int layer=0;layer<numberOfLayers+1;layer++){
											layersOffset[z][segment][rLevel][gaggle][layer] = 0;
										}
									}
								}
							}
						}
					}
				}
			}
		}		
	}
	
	private void initLayersLength(){
		this.layersBits = new int[numberOfLayers];
		
		if (this.layerSizeType == 0){
			//size is given by the user
			if (this.layerBytes!=null){
				for (int layer = 0; layer<layerBytes.length; layer++){
					layersBits[layer] = layerBytes[layer]*8;
				}
				for (int layer = layerBytes.length; layer<numberOfLayers; layer++){
					layersBits[layer] = layerBytes[layerBytes.length-1]*8;
				}
			} else {
				//parameters are not properly set and all layers have the same length
				for (int layer = 0; layer<numberOfLayers; layer++){
					layersBits[layer] = (targetBytes / numberOfLayers)*8;
				}
			}
		} else if (this.layerSizeType == 1){
			//all layers have the same length
			for (int layer = 0; layer<numberOfLayers; layer++){
				layersBits[layer] = (targetBytes / numberOfLayers)*8;
			}
		} else if (this.layerSizeType == 2){
			//each layer has half of the length that the next layer (except for the first)
			int availableBits = targetBytes*8;
			for (int layer = numberOfLayers-1; layer>0; layer--){
				availableBits /=2;
				layersBits[layer] = availableBits;
			}
			layersBits[0] = availableBits;
		}

	}
	
	private void maxLengthlayer(){
		joinLayers();
		int layerLength = targetBytes/numberOfLayers;
		long maxSize = 0;
		//first we compute the maximum size of all the streams. After we will be able to compute the numberOfLayers needed
		for(int z=0;z<zSize;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				for(int rLevel=0;rLevel<resolutionLevels[z]+1;rLevel++){
					if (rLevel==0){
						for(int gaggle=0;gaggle<initialCodedDC[z][segment].length;gaggle++){
							if (maxSize<allLayers[z][segment][rLevel][gaggle].getNumBytes()){
								maxSize = allLayers[z][segment][rLevel][gaggle].getNumBytes(); 
							}
						}
					} else {
						if (sortingAC!=null){
							if (sortingAC[z]!=null){
								if (sortingAC[z][segment]!=null){
									for(int gaggle=0;gaggle<sortingAC[z][segment].length;gaggle++){
										if (maxSize<allLayers[z][segment][rLevel][gaggle].getNumBytes()){
											maxSize = allLayers[z][segment][rLevel][gaggle].getNumBytes(); 
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		//now we compute the number of required layers for the given length
		this.numberOfLayers = (int) (maxSize / layerLength);
		if (maxSize%layerLength!=0 ){
			this.numberOfLayers++;
		}
		
		initLayersOffset();
		
		//once the number of layers is known, we set the layers Offset
		
		for(int z=0;z<zSize;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				for(int rLevel=0;rLevel<resolutionLevels[z]+1;rLevel++){
					for(int gaggle=0;gaggle<initialCodedDC[z][segment].length;gaggle++){				
						for(int layer=1;layer<numberOfLayers;layer++){
						
							int remainingBytes = (int) allLayers[z][segment][rLevel][gaggle].getNumBytes() - layersOffset[z][segment][rLevel][gaggle][layer-1];
							if (remainingBytes>0){
								//there are bytes to add in this layer
								if(layerLength<=remainingBytes){
									layersOffset[z][segment][rLevel][gaggle][layer] = layersOffset[z][segment][rLevel][gaggle][layer-1] + layerLength;
								} else {
									//there are not as much bytes as the maximum number of bytes that can be added to the layer
									layersOffset[z][segment][rLevel][gaggle][layer] =  layersOffset[z][segment][rLevel][gaggle][layer-1] + remainingBytes;
								}
							} else {
								layersOffset[z][segment][rLevel][gaggle][layer] = layersOffset[z][segment][rLevel][gaggle][layer-1];
							}
						}
						layersOffset[z][segment][rLevel][gaggle][numberOfLayers] = (int) allLayers[z][segment][rLevel][gaggle].getNumBytes();
					}
				}
			}
		}
					
		
	}
	
	private void oneSingleLayer(){
		joinLayers();
		initLayersOffset();
		//initLayersLength();
		
		for(int z=0;z<zSize;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				for(int rLevel=0;rLevel<resolutionLevels[z]+1;rLevel++){
					if (rLevel==0){
						for(int gaggle=0;gaggle<initialCodedDC[z][segment].length;gaggle++){	
							layersOffset[z][segment][rLevel][gaggle][1] = (int) allLayers[z][segment][rLevel][gaggle].getNumBytes();
						}
					} else {
						if (sortingAC!=null){
							if (sortingAC[z]!=null){
								if (sortingAC[z][segment]!=null){
									for(int gaggle=0;gaggle<sortingAC[z][segment].length;gaggle++){
										layersOffset[z][segment][rLevel][gaggle][1] = (int) allLayers[z][segment][rLevel][gaggle].getNumBytes();
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void bitPlaneInterleaving(){
		joinLayers();
		
		initLayersOffset();
		initLayersLength();
		
		//Note that we first compute the sizes in bits, but before finishing we transform the values into bytes
		int availableBits= layersBits[0];
		
		int maxBitDepthRefinementDC = getMaxBitDepthRefinementDC();
		int maxBitDepthAC = getMaxBitDepthAC();
		
		int maxBitDepth = maxBitDepthRefinementDC;
		if (maxBitDepth<maxBitDepthAC){
			maxBitDepth=maxBitDepthAC;
		}
		
		int maxResolutionLevels = 0;
		for(int z=0;z<zSize;z++){
			if (maxResolutionLevels < resolutionLevels[z]){
				maxResolutionLevels = resolutionLevels[z];
			}
		}
		
		for(int bitplane = maxBitDepth-1;bitplane>=0 && availableBits>0;bitplane--){
			// from the highest to the lowest bitplane we include all the available coding passes
			// first we include all the initial DC coding stages	
			availableBits = addQuantizedDCs(bitplane,availableBits);
			
			//then we include the refinement bitplanes of DC coefficients	
			availableBits = addRefinedDCs(bitplane,availableBits);
			
			// now the coding bit depth AC must be included
			availableBits = addCodedBitDepthACBlock(bitplane,availableBits);
					
			//sorting AC
			for(int rLevel=1;rLevel<maxResolutionLevels+1;rLevel++){
				availableBits = addSortingAC(bitplane,availableBits, rLevel);
			}
			
			//refinement AC
			if (bitplane<maxBitDepthAC-1){
				for(int rLevel=1;rLevel<maxResolutionLevels+1;rLevel++){
					availableBits = addRefinementAC(bitplane,availableBits, rLevel);
				}
			}				
		}
		//finally we must convert the bits in bytes
		cropUnusedLayers();
		layersOffsetUpdateToBytes(0);
	}
	
	private void codingPassesInterleaving(){
		joinLayers();
		//int[][][][][] codingPassesLength = getCodingPassesLength();
		initLayersOffset();
		initLayersLength();
		
		//Note that we first compute the sizes in bits, but before finishing we transform the values into bytes
		int availableBits= layersBits[0];
		
		//first we include all the initial DC coding stages
		for(int z=0;z<zSize;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				for(int gaggle=0;gaggle<initialCodedDC[z][segment].length;gaggle++){
					availableBits = addStreamToLayer(availableBits, (int) initialCodedDC[z][segment][gaggle].getNumBits(),z, segment, 0, gaggle);
				}
			}
		}
		
		//then we include the refinement bitplanes of DC coefficients that are encoded before the AC coefficients
		if (availableBits>=8){
			int maxBitDepthRefinementDC = getMaxBitDepthRefinementDC();
			int maxBitDepthAC = getMaxBitDepthAC();
			for(int bitplane = maxBitDepthRefinementDC -1; bitplane>maxBitDepthAC-1 ; bitplane--){
				availableBits = addRefinedDCs(bitplane,availableBits);
			}
			
			//now the coding bit depth AC must be included
			for(int z=0;z<zSize;z++){	
				for(int segment=0;segment<blocksPerSegment[z].length;segment++){
					if (codedBitDepthACBlock[z][segment]!=null){
						for(int gaggle=0;gaggle<codedBitDepthACBlock[z][segment].length;gaggle++){
							availableBits = addStreamToLayer(availableBits, (int) codedBitDepthACBlock[z][segment][gaggle].getNumBits(),z, segment, 1, gaggle);
						}
					}
				}
			}
			int maxResolutionLevels = 0;
			for(int z=0;z<zSize;z++){
				if (maxResolutionLevels < resolutionLevels[z]){
					maxResolutionLevels = resolutionLevels[z];
				}
			}
			
			//finally sorting of AC coefficients and all the refinements (both from DC and AC coefficients) are included
			for(int bitplane=maxBitDepthAC-1;bitplane>=0 && availableBits>0;bitplane--){
				//refinement DC
				availableBits = addRefinedDCs(bitplane,availableBits);
				
				//sorting AC
				for(int rLevel=1;rLevel<maxResolutionLevels+1;rLevel++){
					availableBits = addSortingAC(bitplane,availableBits, rLevel);
				}
				
				//refinement AC
				if (bitplane<maxBitDepthAC-1){
					for(int rLevel=1;rLevel<maxResolutionLevels+1;rLevel++){
						availableBits = addRefinementAC(bitplane,availableBits, rLevel);
					}
				}
				
			}
		}
		//finally we must convert the bits in bytes
		cropUnusedLayers();
		layersOffsetUpdateToBytes(0);
	}
	
	private void cropUnusedLayers(){
		if (currentLayer<numberOfLayers-1){
			numberOfLayers = currentLayer+1;
		}
	}
	
	private void layersOffsetUpdateToBytes(int bytesToAdd){
		
		for(int z=0;z<zSize;z++){
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				for(int rLevel=0;rLevel<resolutionLevels[z]+1;rLevel++){
					if (rLevel==0){
						for(int gaggle=0;gaggle<initialCodedDC[z][segment].length;gaggle++){
							for(int layer=1;layer<numberOfLayers+1;layer++){
								layersOffset[z][segment][rLevel][gaggle][layer] = 
									layersOffset[z][segment][rLevel][gaggle][layer-1] + 
									roundBitsToBytes(layersOffset[z][segment][rLevel][gaggle][layer]) + bytesToAdd;						
								if (layersOffset[z][segment][rLevel][gaggle][layer] > allLayers[z][segment][rLevel][gaggle].getNumBytes()){
									layersOffset[z][segment][rLevel][gaggle][layer] = (int) allLayers[z][segment][rLevel][gaggle].getNumBytes();
								}
							}
						}
					} else {
						if (layersOffset[z][segment][rLevel]!=null){
							for(int gaggle=0;gaggle<layersOffset[z][segment][rLevel].length;gaggle++){
								for(int layer=1;layer<numberOfLayers+1;layer++){
									layersOffset[z][segment][rLevel][gaggle][layer] =  
										layersOffset[z][segment][rLevel][gaggle][layer-1] + 
										roundBitsToBytes(layersOffset[z][segment][rLevel][gaggle][layer]) + bytesToAdd;
									if (layersOffset[z][segment][rLevel][gaggle][layer] > allLayers[z][segment][rLevel][gaggle].getNumBytes()){
										layersOffset[z][segment][rLevel][gaggle][layer] = (int) allLayers[z][segment][rLevel][gaggle].getNumBytes();
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private int roundBitsToBytes(int bits){
		int bytes = bits/8;
		if (bits%8!=0){
			bytes++;
		} 
		return bytes;
	}
	

	private int addStreamToLayer(int availableBits, int streamLenght, int channel, int segment, int rLevel, int gaggle){
		if (availableBits>streamLenght){
			layersOffset[channel][segment][rLevel][gaggle][currentLayer+1] += streamLenght;
			availableBits -= streamLenght;
		} else {
			layersOffset[channel][segment][rLevel][gaggle][currentLayer+1] += availableBits;
			if (currentLayer < numberOfLayers-1){
				currentLayer ++;
				availableBits = addStreamToLayer(layersBits[currentLayer],streamLenght-availableBits,channel, segment, rLevel,gaggle);
			} else {
				availableBits = 0;
			}
		}
		
		return availableBits;
	}
	
	private int addQuantizedDCs(int bitplane, int availableBits){
		
		for(int z=0;z<zSize && availableBits>0;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length && availableBits>0;segment++){
				for(int gaggle=0;gaggle<initialCodedDC[z][segment].length && availableBits>0;gaggle++){
					if (bitplane == bitDepthDC[z][segment] - 1 ){
						availableBits = addStreamToLayer(availableBits, (int) initialCodedDC[z][segment][gaggle].getNumBits(),z, segment, 0, gaggle);
					}
				}
			}
		}
		
		return availableBits;
	}
	
	private int addRefinedDCs(int bitplane, int availableBits){
		
		
		for(int z=0;z<zSize && availableBits>0;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length && availableBits>0;segment++){
				for(int gaggle=0;gaggle<refinementDC[z][segment].length && availableBits>0;gaggle++){				
					if (refinementDC[z][segment][gaggle].length> bitplane){
						//this coding pass is available
						availableBits = addStreamToLayer(availableBits, (int) refinementDC[z][segment][gaggle][bitplane].getNumBits(),z, segment, 0, gaggle);
					}
				}
			}
		}
		
		return availableBits;
	}
	
	private int addCodedBitDepthACBlock(int bitplane, int availableBits){
		
		
		for(int z=0;z<zSize && availableBits>0;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length && availableBits>0;segment++){
				if (codedBitDepthACBlock[z][segment]!=null){
					if (bitplane == bitDepthAC[z][segment] - 1 ){
						for(int gaggle=0;gaggle<codedBitDepthACBlock[z][segment].length && availableBits>0;gaggle++){
							availableBits = addStreamToLayer(availableBits, (int) codedBitDepthACBlock[z][segment][gaggle].getNumBits(),z, segment, 1, gaggle);
						}
					}
				}
			}
		}
		
		return availableBits;
	}
	
	private int addSortingAC(int bitplane, int availableBits, int rLevel){
		
		for(int z=0;z<zSize  && availableBits>0;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length && availableBits>0;segment++){
				for(int gaggle=0;gaggle<sortingAC[z][segment].length && availableBits>0;gaggle++){	
					if (sortingAC[z][segment][gaggle].length> bitplane){
						if (sortingAC[z][segment][gaggle][bitplane].length>rLevel-1){
							availableBits = addStreamToLayer(availableBits, (int) sortingAC[z][segment][gaggle][bitplane][rLevel-1].getNumBits(), z, segment, rLevel, gaggle);
						}
					}
				}
			}			
		}
		
		return availableBits;
	}
	
	private int addRefinementAC(int bitplane, int availableBits, int rLevel){
		
		for(int z=0;z<zSize  && availableBits>0;z++){
			if (terRefinementAC[z]!=null){
				for(int segment=0;segment<blocksPerSegment[z].length  && availableBits>0;segment++){
					if (terRefinementAC[z][segment]!=null){
						for(int gaggle=0;gaggle<terRefinementAC[z][segment].length  && availableBits>0 ;gaggle++){
							if (terRefinementAC[z][segment][gaggle].length> bitplane){
								if (terRefinementAC[z][segment][gaggle][bitplane].length> rLevel-1){
									availableBits = addStreamToLayer(availableBits, (int) terRefinementAC[z][segment][gaggle][bitplane][rLevel-1].getNumBits(), z, segment, rLevel, gaggle);
								}
							}
						}
					}
				}
			}
		}
		
		return availableBits;
	}
	
	private int getMaxBitDepthRefinementDC(){
		int max = 0;
		for(int z=0;z<zSize;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				if (max < this.bitDepthDC[z][segment]){
					max = this.bitDepthDC[z][segment];
				}
			}
		}
		return max;
	}
	
	private int getMaxBitDepthAC(){
		int max = 0;
		for(int z=0;z<zSize;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				if (max < this.bitDepthAC[z][segment]){
					max = this.bitDepthAC[z][segment];
				}
			}
		}
		return max;
	}
	
	private void joinLayers(){
		
		for(int z=0;z<zSize;z++){	
		int encodedChannel = 0;
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				for(int rLevel=0;rLevel<resolutionLevels[z]+1;rLevel++){
					if (rLevel==0){
						for(int gaggle=0;gaggle<initialCodedDC[z][segment].length;gaggle++){
							
							if (initialCodedDC[z][segment][gaggle].getNumBits()>0){
								allLayers[z][segment][rLevel][gaggle].addBitStream(initialCodedDC[z][segment][gaggle]);
							}
							
							for(int bitplane=(refinementDC[z][segment][gaggle].length)-1;bitplane>=0;bitplane--){
								if (refinementDC[z][segment][gaggle][bitplane].getNumBits()>0){
									allLayers[z][segment][rLevel][gaggle].addBitStream(refinementDC[z][segment][gaggle][bitplane]);
								}
							}
							fillExtraBits(allLayers[z][segment][rLevel][gaggle]);	
							encodedChannel += (int) allLayers[z][segment][rLevel][gaggle].getNumBytes();
						}
					} else {
						if (sortingAC!=null){
							if (sortingAC[z]!=null){
								if (sortingAC[z][segment]!=null){
									
									for(int gaggle=0;gaggle<sortingAC[z][segment].length;gaggle++){
										if (rLevel==1){//bitDepth AC must be included in the first resolution level of AC components
											if (codedBitDepthACBlock[z][segment]!=null){
												allLayers[z][segment][rLevel][gaggle].addBitStream(codedBitDepthACBlock[z][segment][gaggle]);
											}
										}
										int gaggleBitDepth = sortingAC[z][segment][gaggle].length;
										for(int bitplane = gaggleBitDepth-1;bitplane>=0;bitplane--){
											if (sortingAC[z][segment][gaggle][bitplane]!=null){
												allLayers[z][segment][rLevel][gaggle].addBitStream(sortingAC[z][segment][gaggle][bitplane][rLevel-1]);
											}
											if (bitplane<gaggleBitDepth-1 && terRefinementAC[z][segment][gaggle][bitplane]!=null){
												allLayers[z][segment][rLevel][gaggle].addBitStream(terRefinementAC[z][segment][gaggle][bitplane][rLevel-1]);										
											}
										}
										fillExtraBits(allLayers[z][segment][rLevel][gaggle]);	
										encodedChannel += (int) allLayers[z][segment][rLevel][gaggle].getNumBytes();
									}
								}
							}
						}
					}
				}
			}
			//System.out.println("Joined channel  "+ z + ";  bytes . "+ encodedChannel);
		}
	}
	
	/**
	 * Fill the empty bits (if any) of the last byte.
	 * 
	 * @param stream
	 */
	private void fillExtraBits(BitStream stream){
		int numberExtraBits = (int) stream.getNumBits() % 8;
		if (numberExtraBits!=0){
			stream.addBits(0,8-numberExtraBits);
		}
	}
	
	//////////////////////////
	///// GET FUNCTIONS //////
	//////////////////////////
	
	public BitStream[][][][]  getAllLayers(){
		return allLayers;
	}
	
	public byte[][][][][] getByteArrays(){
		byte arrays[][][][][] = new byte[zSize][][][][];
		for(int z=0;z<zSize;z++){	
			arrays[z] = new byte[blocksPerSegment[z].length][][][];
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				arrays[z][segment] = new byte[resolutionLevels[z]+1][][];
				for(int rLevel=0;rLevel<resolutionLevels[z]+1;rLevel++){
					if (rLevel==0){
						arrays[z][segment][rLevel] = new byte[initialCodedDC[z][segment].length][];
						for(int gaggle=0;gaggle<initialCodedDC[z][segment].length;gaggle++){
							arrays[z][segment][rLevel][gaggle] = allLayers[z][segment][rLevel][gaggle].getBitStream();
						}				
					} else {
						if (sortingAC!=null){
							if (sortingAC[z]!=null){
								if (sortingAC[z][segment]!=null){
									arrays[z][segment][rLevel] = new byte[sortingAC[z][segment].length][];
									for(int gaggle=0;gaggle<sortingAC[z][segment].length;gaggle++){
										arrays[z][segment][rLevel][gaggle] = allLayers[z][segment][rLevel][gaggle].getBitStream();
									}
								}
							}
						}
					}
				}
			}
		}
		
		return arrays;
	}
	
	public int[][][][][] getLayersOffset(){
		return layersOffset;
	}
	
	public int getNumberOfLayers(){
		return numberOfLayers;
	}
}

