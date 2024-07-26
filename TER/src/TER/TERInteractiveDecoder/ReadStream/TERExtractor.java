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
import TER.TERCommon.GaggleUtilities;
import TER.TERCommon.GetMax;
import TER.TERCommon.GetMin;
import TER.TERCommon.ReadPacketHeader;
import TER.TERCommon.SearchTools;
import TER.TERdecoder.ReadFile.ReadBufferedStream;
import TER.TERdecoder.ReadFile.ReadFile;

public class TERExtractor{
	
	ReadBufferedStream encodedStream;
	
	boolean parametersSet = false;
	
	int progressionOrder;
	long[][][][][] layerLocation = null;
	
	byte[][][][][] packet = null;
	
	int requiredTargetBytes;
	int zSize;
	int zSizeExtraction;
	
	int ySizeExtraction;
	int xSizeExtraction;
	
	int xSizes[];
	int xSizesExtraction[];
	
	int numLayers;
	
	int[][] blocksPerSegment = null;
	int[][] blocksPerSegmentExtraction = null;
	
	int[] WTLevels = null;
	int[] WTLevelsExtraction = null;
	
	int[] WTType = null;
	int[] WTTypeExtraction = null;
	
	int[] WTOrder = null;
	int[] WTOrderExtraction = null;
	
	int customWtFlag[] = null;
	int customWtFlagExtraction[] = null;
	float customWeight[][] = null;
	float customWeightExtraction[][] = null;
	
	int[][] gaggleSizeDC = null;
	int[][] gaggleSizeDCExtraction = null;
	
	int[][] gaggleSizeAC = null;
	int[][] gaggleSizeACExtraction = null;
	
	int[][] entropyAC = null;
	int[][] entropyACExtraction = null;
	
	int bitDepthDC[][] = null;
	int bitDepthDCExtraction[][] = null;
	int bitDepthAC[][] = null;
	int bitDepthACExtraction[][] = null;
	
	int padRows[] = null;
	int padRowsExtraction[] = null;
	
	int imageExtensionType[] = null;
	int imageExtensionTypeExtraction[] = null;
	
	int transposeImg[] = null;
	int transposeImgExtraction[] = null;
	
	int signedPixels[] = null;
	int signedPixelsExtraction[] = null;
	
	int pixelBitDepth[] = null;
	int pixelBitDepthExtraction[] = null;
	
	int imageWidth[] = null;
	int imageWidthExtraction[] = null;
	
	int imageGeometry[] = null;
	int imageGeometryExtraction[] = null;
	
	int extractionType = 0;
	int channelList[]=null;
	int yInit;
	int yLength;
	int xInit;
	int xLength;
	boolean gaggleExtraction[][][] = null;
	boolean segmentExtraction[][] = null;

	int numLayerExtraction;
	
	public TERExtractor(ReadBufferedStream encodedStream, int progressionOrder, long[][][][][] layerLocation){
		this.encodedStream = encodedStream;
		this.progressionOrder = progressionOrder;
		this.layerLocation = layerLocation;
	}
	
	
	
	public void setParameters(int zSize, int xSizes[], 
			int numLayers,
			int WTLevels[], int WTType[], int WTOrder[],
			int customWtFlag[], float customWeight[][],
			int padRows[], int imageExtensionType[], int transposeImg[],
			int signedPixels[], int pixelBitDepth[], 
			int imageWidth[], int imageGeometry[],
			int bitDepthDC[][] , int bitDepthAC[][],
			int blocksPerSegment[][], int gaggleSizeDC[][], int gaggleSizeAC[][], int[][] entropyAC,
			int extractionType, 
			int channelList[], int yInit, int yLength, int xInit, int xLength, 
			int requiredLayers, int requiredWTLevels, int requiredTargetBytes) throws ParameterException{
		
		this.zSize = zSize;
		this.xSizes = xSizes;
		this.numLayers = numLayers;
		
		this.channelList = channelList;
		
		this.WTLevels = WTLevels;
		this.WTType = WTType;
		this.WTOrder = WTOrder;
		
		this.customWtFlag = customWtFlag;
		this.customWeight = customWeight;
		
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC;
		this.blocksPerSegment = blocksPerSegment;
		this.gaggleSizeDC = gaggleSizeDC;
		this.gaggleSizeAC = gaggleSizeAC;
		this.entropyAC = entropyAC;
		checkGaggleSizes();
		
		
		this.padRows = padRows;
		this.imageExtensionType = imageExtensionType;
		this.transposeImg = transposeImg;
		this.signedPixels = signedPixels;
		this.pixelBitDepth = pixelBitDepth; 
		this.imageWidth = imageWidth;
		this.imageGeometry = imageGeometry;
		
		this.requiredTargetBytes = requiredTargetBytes;
		
		this.extractionType = extractionType;
		if (extractionType==1) {
			
			this.yInit = yInit;
			this.yLength = yLength;
			this.xInit = xInit;
			this.xLength = xLength;
			setExtractionParameters(requiredLayers,requiredWTLevels);
		}
		
		parametersSet = true;
	}
	
	private void checkGaggleSizes() throws ParameterException{
		
		for(int z=0;z<channelList.length;z++){
			for(int segment=0;segment<blocksPerSegment[channelList[z]].length;segment++){
				if (gaggleSizeDC[channelList[z]][segment]!=gaggleSizeAC[channelList[z]][segment]){
					throw new ParameterException("the sizes of the gaggles do not allow interactive decoding");
				}
				if ( !(xSizes[channelList[z]]%gaggleSizeDC[channelList[z]][segment]==0 || gaggleSizeDC[channelList[z]][segment]%xSizes[channelList[z]]==0) ){
					throw new ParameterException("the sizes of the gaggles do not allow interactive decoding");
				}
			}
		}
	}
	
	private void setExtractionParameters(int requiredLayers, int requiredWTLevels) throws ParameterException{
		//first we set the number of channels to be extracted
		if (channelList!=null){
			zSizeExtraction = channelList.length;	
		} else {
			throw new ParameterException("The list of channels cannot be null");
		}
		
		//second we set numbers of layers to be extracted
		if (requiredLayers<numLayers){
			numLayerExtraction = requiredLayers;
		} else {
			numLayerExtraction = numLayers;
		}
		//third we adjust the number of WTLevels
		WTLevelsExtraction = new int[zSizeExtraction];
		WTTypeExtraction =  new int[zSizeExtraction];
		WTOrderExtraction =  new int[zSizeExtraction];
		
		customWtFlagExtraction = new int[zSizeExtraction];
		customWeightExtraction = new float[zSizeExtraction][];
		 
		
		padRowsExtraction = new int[zSizeExtraction];
		imageExtensionTypeExtraction = new int[zSizeExtraction];
		transposeImgExtraction = new int[zSizeExtraction];
		signedPixelsExtraction = new int[zSizeExtraction];
		pixelBitDepthExtraction = new int[zSizeExtraction]; 
		
				
		for(int z=0;z<zSizeExtraction;z++){
			WTLevelsExtraction[z] = WTLevels[channelList[z]];
			WTTypeExtraction[z] = WTType[channelList[z]];
			WTOrderExtraction[z] = WTOrder[channelList[z]];
			
			customWtFlagExtraction[z] = customWtFlag[channelList[z]];
			if(customWeight[channelList[z]]!=null){
				customWeightExtraction[z] = new float[customWeight[channelList[z]].length];
				for(int k=0;k<customWeight[channelList[z]].length;k++){
					customWeightExtraction[z][k] = customWeight[channelList[z]][k];
				}
			}
			
			
			padRowsExtraction[z] = padRows[channelList[z]];
			imageExtensionTypeExtraction[z] = imageExtensionType[channelList[z]];
			transposeImgExtraction[z] = transposeImg[channelList[z]];
			signedPixelsExtraction[z] = signedPixels[channelList[z]];
			pixelBitDepthExtraction[z] = pixelBitDepth[channelList[z]];
		}
		
		int minResolutionLevels = GetMin.resolutionLevels(WTLevelsExtraction);
		
		if (minResolutionLevels!=GetMax.resolutionLevels(WTLevelsExtraction)){
			System.out.println("Since each channel has a different number of WTLevels, the extractor may present some problems if you have selected a spatial region.");
		}
		if (minResolutionLevels<requiredWTLevels){
			requiredWTLevels = minResolutionLevels;	
		} 

		for(int z=0;z<zSizeExtraction;z++){
			int adjustLevels = WTLevelsExtraction[z] - requiredWTLevels;
			if (minResolutionLevels<WTLevelsExtraction[z]){
				WTLevelsExtraction[z] += WTLevelsExtraction[z] - minResolutionLevels;
			}
			WTLevelsExtraction[z] -= adjustLevels;
		}

		//now lets compute which are the segments and gaggles that must be included
		bitDepthDCExtraction = new int[zSizeExtraction][];
		bitDepthACExtraction = new int[zSizeExtraction][];
		blocksPerSegmentExtraction = new int[zSizeExtraction][];
		gaggleSizeDCExtraction = new int[zSizeExtraction][];
		gaggleSizeACExtraction = new int[zSizeExtraction][];
		entropyACExtraction = new int[zSizeExtraction][];
		segmentExtraction = new boolean[zSizeExtraction][];
		gaggleExtraction = new boolean[zSizeExtraction][][];
		for(int z=0;z<zSizeExtraction;z++){
			int initBlock=0;
			
			segmentExtraction[z] = new boolean[blocksPerSegment[channelList[z]].length];
			gaggleExtraction[z] = new boolean[blocksPerSegment[channelList[z]].length][];
			
			for(int segment=0;segment<blocksPerSegment[channelList[z]].length;segment++){
				segmentExtraction[z][segment] = false;
				int blockSide = (int) 1<<WTLevels[channelList[z]];
					
				int gaggleNum = GaggleUtilities.getGagglesPerSegment(channelList[z],segment,gaggleSizeAC[channelList[z]][segment],blocksPerSegment);
				gaggleExtraction[z][segment] = new boolean[gaggleNum];
					
				for(int gaggle=0;gaggle<gaggleNum;gaggle++){
					int pixelPosition = (gaggle*gaggleSizeAC[channelList[z]][segment] + initBlock)*blockSide;
					int x0Gaggle = pixelPosition%xSizes[channelList[z]];
					int y0Gaggle = (pixelPosition/xSizes[channelList[z]])*blockSide;
					int yGaggleLength = ((gaggleSizeAC[channelList[z]][segment]*blockSide)/xSizes[channelList[z]]) ;
					if (yGaggleLength==0){
						yGaggleLength = blockSide;
					} else {
						yGaggleLength *= blockSide;
					}
						
					int xGaggleLength = gaggleSizeAC[channelList[z]][segment]*blockSide;
					if (intersection(x0Gaggle,xGaggleLength,xInit,xLength) && intersection(y0Gaggle,yGaggleLength,yInit,yLength)){
						gaggleExtraction[z][segment][gaggle] = true;
						segmentExtraction[z][segment] = true;
						/*System.out.println("xInit: "+xInit);
						System.out.println("yInit: "+yInit);*/
					} else {
						gaggleExtraction[z][segment][gaggle] = false;
						
					}
				}
				
				initBlock += blocksPerSegment[channelList[z]][segment];
			}
			
			int numberOfSegments = getTrues(segmentExtraction[z]);
			bitDepthDCExtraction[z] = new int[numberOfSegments];
			bitDepthACExtraction[z] = new int[numberOfSegments];
			blocksPerSegmentExtraction[z] = new int[numberOfSegments];
			gaggleSizeDCExtraction[z] = new int[numberOfSegments];
			gaggleSizeACExtraction[z] = new int[numberOfSegments];
			entropyACExtraction[z] = new int[numberOfSegments];
			int segmentCount = 0;
			
			for(int segment=0;segment<blocksPerSegment[channelList[z]].length;segment++){	
				
				if(segmentExtraction[z][segment]){
					
					bitDepthDCExtraction[z][segmentCount] =  bitDepthDC[channelList[z]][segment];
					bitDepthACExtraction[z][segmentCount] =  bitDepthAC[channelList[z]][segment];
					gaggleSizeDCExtraction[z][segmentCount] =  gaggleSizeDC[channelList[z]][segment];
					gaggleSizeACExtraction[z][segmentCount] =  gaggleSizeAC[channelList[z]][segment];
					entropyACExtraction[z][segmentCount] =  entropyAC[channelList[z]][segment];
					blocksPerSegmentExtraction[z][segmentCount] = 0;
					
					int gaggleNum = GaggleUtilities.getGagglesPerSegment(channelList[z],segment,gaggleSizeAC[channelList[z]][segment],blocksPerSegment);
					for(int gaggle=0;gaggle<gaggleNum;gaggle++){
						if (gaggleExtraction[z][segment][gaggle]){
							if (gaggle!=gaggleNum-1 || blocksPerSegment[channelList[z]][segment]%gaggleSizeAC[channelList[z]][segment]==0){
								blocksPerSegmentExtraction[z][segmentCount] += gaggleSizeAC[channelList[z]][segment];
							} else {					
								blocksPerSegmentExtraction[z][segmentCount] += 
									blocksPerSegment[channelList[z]][segment]%gaggleSizeAC[channelList[z]][segment];		
							}
						}
					}
					segmentCount++;
				}
			}
		}

		int blockCoordinates[] = SearchTools.findFirst(gaggleExtraction[0],gaggleSizeAC[channelList[0]]);

		int initBlock = SearchTools.getBlockPosition(blockCoordinates,blocksPerSegment[channelList[0]]);

		blockCoordinates = SearchTools.findLast(gaggleExtraction[0],gaggleSizeAC[channelList[0]]);
		int lastBlock = SearchTools.getBlockPosition(blockCoordinates,blocksPerSegment[channelList[0]]);

		int blockSide = 1 << WTLevels[channelList[0]];

		int pixelPosition = initBlock * blockSide;
		int x0Extraction = pixelPosition%xSizes[channelList[0]];
		int y0Extraction = (pixelPosition/xSizes[channelList[0]])*blockSide;
		
		pixelPosition = lastBlock * blockSide;
		
		int x1Extraction = pixelPosition%xSizes[channelList[0]] + blockSide;
		int y1Extraction = (pixelPosition/xSizes[channelList[0]])*blockSide + blockSide;
		int resolutionReduction = 1 << (WTLevels[channelList[0]]-WTLevelsExtraction[0]);
		xSizeExtraction = (x1Extraction - x0Extraction)/resolutionReduction;
		ySizeExtraction = (y1Extraction - y0Extraction)/resolutionReduction;
		
		xSizesExtraction = new int[zSizeExtraction];
		for(int z=0;z<zSizeExtraction;z++){
			xSizesExtraction[z] = xSizeExtraction;
		}
		
	}

	
	private boolean intersection(int initGaggle, int gaggleLength, int init, int length){
		boolean intersection = false;
		if (initGaggle<init){
			if (initGaggle+gaggleLength>init){
				intersection = true;
			}// else no intersection
		} else if (initGaggle>init){
			if (init+length>initGaggle){
				intersection = true;
			}// else no intersection 
		} else if (initGaggle==init){
			intersection = true;
		}
		
		return intersection;
	}
	
	private int getTrues(boolean condition[]){
		int count = 0;
		for(int k=0;k<condition.length;k++){
			if(condition[k]){
				count++;
			}
		}
		return count;
	}
	
	public byte[][][][][] run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("ReadFile cannot run if parameters are not set.");
		}
		
		
		if (extractionType==0){
			targetByteExtract();
		} else if (extractionType==1){
			extractSelection();
		}
		
		parametersSet = false;
		return packet;
	}
	
	
	private void targetByteExtract() throws Exception{
		
		byte byteArray[] = new byte[requiredTargetBytes];
		encodedStream.readFully(byteArray,0,requiredTargetBytes);
		ReadFile rf = new ReadFile(new ReadBufferedStream(byteArray));
		rf.setParameters(zSize, progressionOrder, numLayers, 
				WTLevels, blocksPerSegment, gaggleSizeDC, gaggleSizeAC);
		rf.run();
		
		packet = rf.getPackets();
	}
	
	private void extractSelection() throws Exception{
		
		int gaggleSize[][][][] = initGaggleSize();
		
		ReadPacketHeader packetHeader = new ReadPacketHeader();
		packetHeader.setParameters(encodedStream);
		
		int targetBytes = requiredTargetBytes;
		int maxWTLevels = GetMax.resolutionLevels(WTLevelsExtraction);
		
		try{
			
			for(int layer=0;layer<numLayerExtraction;layer++){
				for(int rLevel=0;rLevel<maxWTLevels+1 && targetBytes>0;rLevel++){
					for(int z=0;z<zSizeExtraction && targetBytes>0;z++){
						for(int segment=0;segment<blocksPerSegment[channelList[z]].length && targetBytes>0;segment++){
							int sCount=0;
							if (segmentExtraction[z][segment] && rLevel<WTLevelsExtraction[z]+1){
								int gaggleNum = 0;
								int gCount = 0;
								if (rLevel==0){
									gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeDC[z][segment],blocksPerSegment);
								} else {
									gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeAC[z][segment],blocksPerSegment);
								}
								for(int gaggle = 0;gaggle<gaggleNum && targetBytes>0;gaggle++){	
									if (gaggleExtraction[z][segment][gaggle]){
										encodedStream.seek(layerLocation[channelList[z]][segment][rLevel][gaggle][layer]);
										int packetLength = packetHeader.readPacketHeader();
										gaggleSize[z][sCount][rLevel][gCount] += packetLength;
										targetBytes -= packetLength;
										gCount++;
									}
								}
								sCount++;
							}
						}
					}
				}
			}
		} catch (EOFException e){
			
		} 
		
		targetBytes = requiredTargetBytes;
		initPackets(gaggleSize);
		gaggleSize = resetGaggleSize(gaggleSize);
		try{		
			for(int layer=0;layer<numLayerExtraction;layer++){
				for(int rLevel=0;rLevel<maxWTLevels+1 && targetBytes>0;rLevel++){
					for(int z=0;z<zSizeExtraction && targetBytes>0;z++){
						for(int segment=0;segment<blocksPerSegment[channelList[z]].length && targetBytes>0;segment++){
							int sCount=0;
							if (segmentExtraction[z][segment] && rLevel<WTLevelsExtraction[z]+1){
								int gaggleNum = 0;
								int gCount = 0;
								if (rLevel==0){
									gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeDC[z][segment],blocksPerSegment);
								} else {
									gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeAC[z][segment],blocksPerSegment);
								}
								for(int gaggle = 0;gaggle<gaggleNum && targetBytes>0;gaggle++){	
									if (gaggleExtraction[z][segment][gaggle]){
										encodedStream.seek(layerLocation[channelList[z]][segment][rLevel][gaggle][layer]);
										int packetLength = packetHeader.readPacketHeader();
										encodedStream.readFully(packet[z][sCount][rLevel][gCount],gaggleSize[z][sCount][rLevel][gCount],packetLength);
										gaggleSize[z][sCount][rLevel][gCount] += packetLength;
										targetBytes -= packetLength;
										gCount++;
									}
								}
								sCount++;
							}
						}
					}
				}
			}
		} catch (EOFException e){
			
		}
	}
	
	private int[][][][]  resetGaggleSize(int[][][][] gaggleSize){
		
		
		for(int z=0;z<zSizeExtraction;z++){	
			for(int segment=0;segment<blocksPerSegmentExtraction[z].length;segment++){			
				for(int rLevel=0;rLevel<WTLevelsExtraction[z]+1;rLevel++){
					for(int gaggle=0;gaggle<gaggleSize[z][segment][rLevel].length;gaggle++){
						gaggleSize[z][segment][rLevel][gaggle] = 0;
					}				
				}
			}
		}
		return gaggleSize;
	}

	private int[][][][]  initGaggleSize(){
		
		int gaggleSize[][][][] = new int[zSize][][][];
		for(int z=0;z<zSizeExtraction;z++){	
			gaggleSize[z] = new int[blocksPerSegmentExtraction[z].length][][];
			for(int segment=0;segment<blocksPerSegmentExtraction[z].length;segment++){
				gaggleSize[z][segment] = new int[WTLevelsExtraction[z]+1][];
				for(int rLevel=0;rLevel<WTLevelsExtraction[z]+1;rLevel++){
					int gaggleNum = 0;
					if (rLevel==0){
						gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeDCExtraction[z][segment],blocksPerSegmentExtraction);
					} else {
						gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeACExtraction[z][segment],blocksPerSegmentExtraction);
					}
					gaggleSize[z][segment][rLevel] = new int[gaggleNum];
					for(int gaggle=0;gaggle<gaggleNum;gaggle++){
						gaggleSize[z][segment][rLevel][gaggle] = 0;
					}
					
				}
			}
		}
		return gaggleSize;
	}
	
	private void initPackets(int gaggleSize[][][][]){
		packet = new byte[zSize][][][][];
		
		for(int z=0;z<zSizeExtraction;z++){	
			
			packet[z] = new byte[blocksPerSegmentExtraction[z].length][][][];
			for(int segment=0;segment<blocksPerSegmentExtraction[z].length;segment++){
				
				packet[z][segment] = new byte[WTLevelsExtraction[z]+1][][];
				for(int rLevel=0;rLevel<WTLevelsExtraction[z]+1;rLevel++){
					int gaggleNum = 0;
					if (rLevel==0){
						gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeDCExtraction[z][segment],blocksPerSegmentExtraction);
					} else {
						gaggleNum = GaggleUtilities.getGagglesPerSegment(z,segment,gaggleSizeACExtraction[z][segment],blocksPerSegmentExtraction);
					}
					packet[z][segment][rLevel] = new byte[gaggleNum][];
					for(int gaggle=0;gaggle<gaggleNum;gaggle++){
						packet[z][segment][rLevel][gaggle] = new byte[gaggleSize[z][segment][rLevel][gaggle]];
					}
					
				}
			}
		}	
	}
	
	
	///////////////////
	///GET FUNCTIONS///
	///////////////////
	public int[][] getBitDepthDCExtraction(){
		return this.bitDepthDCExtraction;
	}
	public int[][] getBitDepthACExtraction(){
		return this.bitDepthACExtraction;
	}
	public int[][] getBlocksPerSegmentExtraction(){
		return blocksPerSegmentExtraction;
	}
	public int[][] getGaggleSizeDCExtraction(){
		return gaggleSizeDCExtraction;
	}
	public int[][] getGaggleSizeACExtraction(){
		return gaggleSizeACExtraction;
	}
	public int[][] getEntropyACExtraction(){
		return entropyACExtraction;
	}
	public int[] getWTLevelsExtraction(){
		return WTLevelsExtraction;
	}
	public int[] getWTTypeExtraction(){
		return WTTypeExtraction;
	}
	public int[] getWTOrderExtraction(){
		return WTOrderExtraction;
	}
	
	public int[] getCustomWtFlagExtraction(){
		return customWtFlagExtraction;
	}
	public float[][] getCustomWeightExtraction(){
		return customWeightExtraction;
	}
	
	
	public int[] getXSizesExtraction(){
		return xSizesExtraction;
	}
	public int getZSizeExtraction(){
		return zSizeExtraction;
	}
	public int getYSizeExtraction(){
		return ySizeExtraction;
	}
	public int getXSizeExtraction(){
		return xSizeExtraction;
	}
	
	public int[] getPadRowsExtraction(){
		return padRowsExtraction;
	}
	public int[] getImageExtensionTypeExtraction(){
		return imageExtensionTypeExtraction;
	}
	public int[] getTransposeImgExtraction(){
		return transposeImgExtraction;
	}
	public int[] getSignedPixelsExtraction(){
		return signedPixelsExtraction;
	}
	public int[] getPixelBitDepthExtraction(){
		return pixelBitDepthExtraction;
	}
	public int[] getImageWidthExtraction(){
		imageWidthExtraction = this.xSizesExtraction;
		return imageWidthExtraction;
	}
	public int[] getImageGeometryExtraction(){
		if (imageGeometry!=null){
			imageGeometryExtraction = new int[6];
			imageGeometryExtraction[0] = zSizeExtraction;
			imageGeometryExtraction[1] = ySizeExtraction;
			imageGeometryExtraction[2] = xSizeExtraction;
		
			imageGeometryExtraction[3] = imageGeometry[3];
			imageGeometryExtraction[4] = imageGeometry[4];
			imageGeometryExtraction[5] = imageGeometry[5];
		}
		return imageGeometryExtraction;
	}
}