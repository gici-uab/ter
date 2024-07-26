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

import java.io.EOFException;

import GiciException.ParameterException;
import GiciException.WarningException;
import TER.TERCommon.GaggleUtilities;
import TER.TERCommon.GetMax;
import TER.TERCommon.ReadPacketHeader;
import TER.TERdecoder.ReadFile.ReadBufferedStream;


public class TERIndexing{

	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	int zSize;
	
	int progressionOrder;
	int numLayers;
	
	int WTLevels[] = null;
	
	int blocksPerSegment[][] = null;
	int gaggleSizeDC[][] = null;
	int gaggleSizeAC[][] = null;
		
	long layersLocation[][][][][] = null;
	
	ReadBufferedStream encodedStream = null;
	
	ReadPacketHeader packetHeader= null;
	
	/**
	 * Constructor of the class ReadFile.
	 */
	public TERIndexing(ReadBufferedStream encodedStream){
		this.encodedStream = encodedStream;
	}
	
	public void setParameters(int zSize, int progressionOrder, 
			int numLayers,
			int WTLevels[],int blocksPerSegment[][],
			int gaggleSizeDC[][], int gaggleSizeAC[][]){
		
		this.zSize = zSize;
		
		this.progressionOrder = progressionOrder;
		this.numLayers = numLayers;
		
		this.WTLevels = WTLevels;
		this.blocksPerSegment = blocksPerSegment;
		
		this.gaggleSizeDC = gaggleSizeDC;
		this.gaggleSizeAC = gaggleSizeAC;
		
		packetHeader = new  ReadPacketHeader();
		packetHeader.setParameters(encodedStream);
		
		initLayersLocation();
		
		
		parametersSet=true;
	}

	private void initLayersLocation(){
		layersLocation = new long[zSize][][][][];
		for(int z=0;z<zSize;z++){	
			layersLocation[z] = new long[blocksPerSegment[z].length][][][];
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				layersLocation[z][segment] = new long[WTLevels[z]+1][][];
				for(int rLevel=0;rLevel<WTLevels[z]+1;rLevel++){
					if (rLevel==0){
						int gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeDC[z][segment],blocksPerSegment);
						layersLocation[z][segment][rLevel] = new long[gaggleNum][numLayers];
					} else {
						int gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeAC[z][segment],blocksPerSegment);
						layersLocation[z][segment][rLevel] = new long[gaggleNum][numLayers];
					}
				}
			}
		}
	}
	
	public long[][][][][] run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("ReadFile cannot run if parameters are not set.");
		}
		
		if (progressionOrder==1){
			indexProgression0rder1();
		} else if (progressionOrder==2){
			indexProgression0rder2();
		} else if (progressionOrder==3){
			indexProgression0rder3();
		} else if (progressionOrder==4){
			indexProgression0rder4();
		} else if (progressionOrder==5){
			indexProgression0rder5();
		} else{
			throw new WarningException("Not implemented yet!!");
		}
		return layersLocation;
	}
	
	public void indexProgression0rder1()throws Exception{
		// LRCP Layer-Resolution-Component-Position	
		
		int maxResolutionLevels = GetMax.resolutionLevels(WTLevels);	
		try{
			for(int layer = 0;layer<numLayers;layer++){
				for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){
					for(int z=0;z<zSize;z++){
						for(int segment=0;segment<blocksPerSegment[z].length;segment++){
							if (rLevel<= WTLevels[z]){
								for(int gaggle = 0;gaggle<layersLocation[z][segment][rLevel].length;gaggle++){
									layersLocation[z][segment][rLevel][gaggle][layer] = encodedStream.getPos();
									int packetLength= packetHeader.readPacketHeader();
									if (encodedStream.getRemaniningBytes()>packetLength){
										encodedStream.skipBytes(packetLength);
									}
								}					
							}
						}
					}
				}
			}
		} catch (EOFException e){
			
		}
	}
	
	public void indexProgression0rder2()throws Exception{
		// RLCP Resolution-Layer-Component-Position
		
		int maxResolutionLevels = GetMax.resolutionLevels(WTLevels);
		try{
			for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){
				for(int layer = 0;layer<numLayers;layer++){
					for(int z=0;z<zSize;z++){
						for(int segment=0;segment<blocksPerSegment[z].length;segment++){
							if (rLevel<= WTLevels[z]){
								for(int gaggle = 0;gaggle<layersLocation[z][segment][rLevel].length;gaggle++){		
									layersLocation[z][segment][rLevel][gaggle][layer] = encodedStream.getPos();
									int packetLength= packetHeader.readPacketHeader();
									if (encodedStream.getRemaniningBytes()>packetLength){
										encodedStream.skipBytes(packetLength);
									}
								}
							}
						}
					}
				}
			}
		} catch (EOFException e){
			
		}
	}
	
	public void indexProgression0rder3()throws Exception{
		// RPCL Resolution-Position-Component-Layer
		
		int maxResolutionLevels = GetMax.resolutionLevels(WTLevels);
		int maxSegmentIndex = GetMax.segmentIndex(zSize,blocksPerSegment);
		int maxGaggleIndex = GetMax.gaggleIndex(zSize,WTLevels,blocksPerSegment,layersLocation);
		
		// fisrt we compute the size required to store the information to decode the file
		try{
			for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){			
				
				for(int segment=0;segment<maxSegmentIndex;segment++){		
					for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){
						for(int z=0;z<zSize;z++){
							if (segment<layersLocation[z].length){
								if (rLevel<=WTLevels[z]){
									if (layersLocation[z][segment]!=null){
										if (gaggle<layersLocation[z][segment][rLevel].length){										
											for(int layer = 0;layer<numLayers;layer++){
												layersLocation[z][segment][rLevel][gaggle][layer] = encodedStream.getPos();
												int packetLength= packetHeader.readPacketHeader();
												if (encodedStream.getRemaniningBytes()>packetLength){
													encodedStream.skipBytes(packetLength);
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
		} catch (EOFException e){
			
		}
	}
	
	public void indexProgression0rder4()throws Exception{
		// PCRL Position-Component-Resolution-Layer
		
		int maxSegmentIndex = GetMax.segmentIndex(zSize,blocksPerSegment);
		int maxGaggleIndex = GetMax.gaggleIndex(zSize,WTLevels,blocksPerSegment,layersLocation);
		
		try{
			for(int segment=0;segment<maxSegmentIndex;segment++){
				for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){
					for(int z=0;z<zSize;z++){			
						for(int rLevel=0;rLevel<=WTLevels[z];rLevel++){
							if (segment<layersLocation[z].length){
								for(int layer = 0;layer<numLayers;layer++){
									if (gaggle<layersLocation[z][segment][rLevel].length){
										layersLocation[z][segment][rLevel][gaggle][layer] = encodedStream.getPos();
										int packetLength= packetHeader.readPacketHeader();
										if (encodedStream.getRemaniningBytes()>packetLength){
											encodedStream.skipBytes(packetLength);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (EOFException e){
			
		}
	}
	
	public void indexProgression0rder5()throws Exception{
		// CPRL Component-Position-Resolution-Layer
		
		int maxGaggleIndex = GetMax.gaggleIndex(zSize,WTLevels,blocksPerSegment,layersLocation);
		
		try{
			for(int z=0;z<zSize;z++){	
				for(int segment=0;segment<blocksPerSegment[z].length;segment++){
					for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){	
						for(int rLevel=0;rLevel<=WTLevels[z];rLevel++){
							for(int layer = 0;layer<numLayers;layer++){
								if (gaggle<layersLocation[z][segment][rLevel].length){
									layersLocation[z][segment][rLevel][gaggle][layer] = encodedStream.getPos();
									int packetLength= packetHeader.readPacketHeader();
									if (encodedStream.getRemaniningBytes()>packetLength){
										encodedStream.skipBytes(packetLength);
									}
								}
							}
						}
					}
				}
			}
		} catch (EOFException e){
			
		}
	}
}