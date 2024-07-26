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
package TER.TERdecoder.ReadFile;

import java.io.EOFException;

import GiciException.*;
import TER.TERCommon.GaggleUtilities;
import TER.TERCommon.GetMax;
import TER.TERCommon.ReadPacketHeader;


/**
 * This class receives a string which refers to a encoded file, opens it and store.<br>
 * 
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; set parameters<br>
 * &nbsp; run<br> 
 * 
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2  
 */
public class ReadFile{
	
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
		
	byte packet[][][][][] = null;
	int layersOffset[][][][][] = null;
	
	long initialPositionEncodedStream;	
	ReadBufferedStream encodedStream = null;
	
	ReadPacketHeader packetHeader= null;
	
	/**
	 * Constructor of the class ReadFile.
	 */
	public ReadFile(ReadBufferedStream encodedStream){
		this.encodedStream = encodedStream;
		initialPositionEncodedStream = encodedStream.getPos();
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
		
		initLayersOffset();

		parametersSet=true;
	}
	
	private void initLayersOffset(){
		layersOffset = new int[zSize][][][][];
		for(int z=0;z<zSize;z++){	
			layersOffset[z] = new int[blocksPerSegment[z].length][][][];
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				layersOffset[z][segment] = new int[WTLevels[z]+1][][];
				for(int rLevel=0;rLevel<WTLevels[z]+1;rLevel++){
					if (rLevel==0){
						int gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeDC[z][segment],blocksPerSegment);
						layersOffset[z][segment][rLevel] = new int[gaggleNum][numLayers+1];
					} else {
						int gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeAC[z][segment],blocksPerSegment);
						layersOffset[z][segment][rLevel] = new int[gaggleNum][numLayers+1];
					}
				}
			}
		}
		
	}
	
	public void run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("ReadFile cannot run if parameters are not set.");
		}
		
		if (progressionOrder==1){
			readProgression0rder1();
		} else if (progressionOrder==2){
			readProgression0rder2();
		} else if (progressionOrder==3){
			readProgression0rder3();
		} else if (progressionOrder==4){
			readProgression0rder4();
		} else if (progressionOrder==5){
			readProgression0rder5();
		} else{
			throw new WarningException("Not implemented yet!!");
		}
	}
	
	private void updateLayersOffset(){
		for(int z=0;z<zSize;z++){	
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				for(int rLevel=0;rLevel<WTLevels[z]+1;rLevel++){
					int gaggleNum = 0;
					if (rLevel==0){
						gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeDC[z][segment],blocksPerSegment);
					} else {
						gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeAC[z][segment],blocksPerSegment);				
					}
					for(int gaggle=0;gaggle<gaggleNum;gaggle++){
						for(int layer=0;layer<numLayers;layer++){
							layersOffset[z][segment][rLevel][gaggle][layer+1] += layersOffset[z][segment][rLevel][gaggle][layer];
						}
					}
				}
			}
		}
	}
	
	public void readProgression0rder1()throws Exception{
		// LRCP Layer-Resolution-Component-Position	
		
		int maxResolutionLevels = GetMax.resolutionLevels(WTLevels);	
		try{
			for(int layer = 0;layer<numLayers;layer++){
				for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){
					for(int z=0;z<zSize;z++){
						for(int segment=0;segment<blocksPerSegment[z].length;segment++){
							if (rLevel<= WTLevels[z]){
								for(int gaggle = 0;gaggle<layersOffset[z][segment][rLevel].length;gaggle++){	
									int packetLength= packetHeader.readPacketHeader();
									layersOffset[z][segment][rLevel][gaggle][layer+1] = packetLength;
									encodedStream.skipBytes(packetLength);
								}					
							}
						}
					}
				}
			}
		} catch (EOFException e){
			
		}
		
		//reset the counter of the pointer to the input stream
		encodedStream.seek(this.initialPositionEncodedStream);
		
		
		//once size is computed, we pepare the arrays and then we read the encoded stream
		updateLayersOffset();
		initPackets();
		try{	
			for(int layer=0;layer<numLayers;layer++){
				for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){
					for(int z=0;z<zSize;z++){
						for(int segment=0;segment<blocksPerSegment[z].length;segment++){
							if (rLevel<= WTLevels[z]){
								for(int gaggle = 0;gaggle<packet[z][segment][rLevel].length;gaggle++){			
									int packetLength = packetHeader.readPacketHeader();	
									if (encodedStream.getRemaniningBytes()>=packetLength){
										encodedStream.readFully(packet[z][segment][rLevel][gaggle],
												layersOffset[z][segment][rLevel][gaggle][layer],packetLength);
									} else {
										encodedStream.readFully(packet[z][segment][rLevel][gaggle],
												layersOffset[z][segment][rLevel][gaggle][layer],(int)encodedStream.getRemaniningBytes());
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

	public void readProgression0rder2()throws Exception{
		// RLCP Resolution-Layer-Component-Position
		
		int maxResolutionLevels = GetMax.resolutionLevels(WTLevels);
		try{
			for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){
				for(int layer = 0;layer<numLayers;layer++){
					for(int z=0;z<zSize;z++){
						for(int segment=0;segment<blocksPerSegment[z].length;segment++){
							if (rLevel<= WTLevels[z]){
								for(int gaggle = 0;gaggle<layersOffset[z][segment][rLevel].length;gaggle++){		
									int packetLength= packetHeader.readPacketHeader();
									layersOffset[z][segment][rLevel][gaggle][layer+1] = packetLength;
									encodedStream.skipBytes(packetLength);
								}
							}
						}
					}
				}
			}
		} catch (EOFException e){
			
		}
		//reset the counter of the pointer to the input stream
		encodedStream.seek(this.initialPositionEncodedStream);
		
		
		//once size is computed, we pepare the arrays and then we read the encoded stream
		updateLayersOffset();
		initPackets();	
		
		try{
			for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){
				for(int layer=0;layer<numLayers;layer++){
					for(int z=0;z<zSize;z++){
						for(int segment=0;segment<blocksPerSegment[z].length;segment++){
							if (rLevel<= WTLevels[z]){
								for(int gaggle = 0;gaggle<packet[z][segment][rLevel].length;gaggle++){			
									int packetLength = packetHeader.readPacketHeader();
									if (encodedStream.getRemaniningBytes()>=packetLength){
										encodedStream.readFully(packet[z][segment][rLevel][gaggle],
												layersOffset[z][segment][rLevel][gaggle][layer],packetLength);
									}else {
										encodedStream.readFully(packet[z][segment][rLevel][gaggle],
												layersOffset[z][segment][rLevel][gaggle][layer],(int)encodedStream.getRemaniningBytes());
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

	public void readProgression0rder3()throws Exception{
		// RPCL Resolution-Position-Component-Layer
		
		int maxResolutionLevels = GetMax.resolutionLevels(WTLevels);
		int maxSegmentIndex = GetMax.segmentIndex(zSize,blocksPerSegment);
		int maxGaggleIndex = GetMax.gaggleIndex(zSize,WTLevels,blocksPerSegment,layersOffset);
		
		// fisrt we compute the size required to store the information to decode the file
		try{
			for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){			
				for(int segment=0;segment<maxSegmentIndex;segment++){		
					for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){
						for(int z=0;z<zSize;z++){
							if (segment<layersOffset[z].length){
								if (rLevel<=WTLevels[z]){
									if (layersOffset[z][segment]!=null){
										if (gaggle<layersOffset[z][segment][rLevel].length){										
											for(int layer = 0;layer<numLayers;layer++){
												int packetLength= packetHeader.readPacketHeader();
												layersOffset[z][segment][rLevel][gaggle][layer+1] = packetLength;							
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
		} catch (EOFException e){
			
		}

		// reset the counter of the pointer to the input stream
		encodedStream.seek(this.initialPositionEncodedStream);

		
		//once size is computed, we pepare the arrays and then we read the encoded stream
		updateLayersOffset();
		initPackets();
		
		try{
			for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){			
				for(int segment=0;segment<maxSegmentIndex;segment++){		
					for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){
						for(int z=0;z<zSize;z++){
							if (segment<layersOffset[z].length){
								if (rLevel<=WTLevels[z]){
									if (layersOffset[z][segment][rLevel]!=null){
										if (gaggle<layersOffset[z][segment][rLevel].length){										
											for(int layer = 0;layer<numLayers;layer++){
												int packetLength = packetHeader.readPacketHeader();
												if (encodedStream.getRemaniningBytes()>=packetLength){
													encodedStream.readFully(packet[z][segment][rLevel][gaggle],
															layersOffset[z][segment][rLevel][gaggle][layer],packetLength);
												}else {
													encodedStream.readFully(packet[z][segment][rLevel][gaggle],
															layersOffset[z][segment][rLevel][gaggle][layer],(int)encodedStream.getRemaniningBytes());
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
	
	public void readProgression0rder4()throws Exception{
		// PCRL Position-Component-Resolution-Layer
		
		int maxSegmentIndex = GetMax.segmentIndex(zSize,blocksPerSegment);
		int maxGaggleIndex = GetMax.gaggleIndex(zSize,WTLevels,blocksPerSegment,layersOffset);
		try{
			for(int segment=0;segment<maxSegmentIndex;segment++){
				for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){
					for(int z=0;z<zSize;z++){			
						for(int rLevel=0;rLevel<=WTLevels[z];rLevel++){
							if (segment<layersOffset[z].length){
								for(int layer = 0;layer<numLayers;layer++){
									if (gaggle<layersOffset[z][segment][rLevel].length){
										int packetLength= packetHeader.readPacketHeader();
										layersOffset[z][segment][rLevel][gaggle][layer+1] = packetLength;
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
		// reset the counter of the pointer to the input stream
		encodedStream.seek(this.initialPositionEncodedStream);

		
		// once size is computed, we pepare the arrays and then we read the encoded stream
		updateLayersOffset();
		initPackets();
		try{
			for(int segment=0;segment<maxSegmentIndex;segment++){
				for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){
					for(int z=0;z<zSize;z++){			
						for(int rLevel=0;rLevel<=WTLevels[z];rLevel++){
							if (segment<layersOffset[z].length){
								for(int layer = 0;layer<numLayers;layer++){
									if (gaggle<layersOffset[z][segment][rLevel].length){
										int packetLength = packetHeader.readPacketHeader();
										if (encodedStream.getRemaniningBytes()>=packetLength){
											encodedStream.readFully(packet[z][segment][rLevel][gaggle],
													layersOffset[z][segment][rLevel][gaggle][layer],packetLength);
										}else {
											encodedStream.readFully(packet[z][segment][rLevel][gaggle],
													layersOffset[z][segment][rLevel][gaggle][layer],(int)encodedStream.getRemaniningBytes());
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
	
	public void readProgression0rder5()throws Exception{
		// CPRL Component-Position-Resolution-Layer
		
		int maxGaggleIndex = GetMax.gaggleIndex(zSize,WTLevels,blocksPerSegment,layersOffset);
		try{
			for(int z=0;z<zSize;z++){	
				for(int segment=0;segment<blocksPerSegment[z].length;segment++){
					for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){	
						for(int rLevel=0;rLevel<=WTLevels[z];rLevel++){
							for(int layer = 0;layer<numLayers;layer++){
								if (gaggle<layersOffset[z][segment][rLevel].length){
									int packetLength= packetHeader.readPacketHeader();
									layersOffset[z][segment][rLevel][gaggle][layer+1] = packetLength;
									encodedStream.skipBytes(packetLength);
								}
							}
						}
					}
				}
			}
		} catch (EOFException e){
			
		}
		
		// reset the counter of the pointer to the input stream
		encodedStream.seek(this.initialPositionEncodedStream);
		
		// once size is computed, we pepare the arrays and then we read the encoded stream
		updateLayersOffset();
		initPackets();
		
		try{
			for(int z=0;z<zSize;z++){	
				for(int segment=0;segment<blocksPerSegment[z].length;segment++){
					for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){	
						for(int rLevel=0;rLevel<=WTLevels[z];rLevel++){
							for(int layer = 0;layer<numLayers;layer++){
								if (gaggle<layersOffset[z][segment][rLevel].length){
									int packetLength = packetHeader.readPacketHeader();								
									if (encodedStream.getRemaniningBytes()>=packetLength){
										encodedStream.readFully(packet[z][segment][rLevel][gaggle],
												layersOffset[z][segment][rLevel][gaggle][layer],packetLength);
									}else {
										encodedStream.readFully(packet[z][segment][rLevel][gaggle],
												layersOffset[z][segment][rLevel][gaggle][layer],(int)encodedStream.getRemaniningBytes());
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
	
	private void initPackets(){
		packet = new byte[zSize][][][][];
		for(int z=0;z<zSize;z++){	
			packet[z] = new byte[blocksPerSegment[z].length][][][];
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				packet[z][segment] = new byte[WTLevels[z]+1][][];
				for(int rLevel=0;rLevel<WTLevels[z]+1;rLevel++){
					if (rLevel==0){
						int gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeDC[z][segment],blocksPerSegment);
						packet[z][segment][rLevel] = new byte[gaggleNum][];
					} else {
						int gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeAC[z][segment],blocksPerSegment);
						packet[z][segment][rLevel] = new byte[gaggleNum][];
					}
					for(int gaggle = 0;gaggle<packet[z][segment][rLevel].length;gaggle++){
						packet[z][segment][rLevel][gaggle] = new byte[layersOffset[z][segment][rLevel][gaggle][numLayers]];
					}
				}
			}
		}	
	}
	
	//////////////////////////
	///// GET FUNCTIONS //////
	//////////////////////////
	
	public byte[][][][][] getPackets(){
		return packet;
	}
	
	public int[][][][][] getLayersOffset(){
		return layersOffset;
	}
}