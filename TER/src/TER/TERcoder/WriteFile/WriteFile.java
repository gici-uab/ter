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
import TER.TERCommon.GetMax;
import TER.TERCommon.ParameterTools;

import java.io.*;

/**
 * This class writes the encoded image into a file.
 *  
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class WriteFile{
	
	
	boolean parametersSet = false ;
	
	/**
	 * Progression order used to save the file. <br>
	 * <p>
	 * Valid values are:<br>
	 *  <ul>
	 *    <li> 0- Recommended order (i.e. order defined in the CCSDS recomendation for image data coding)
	 *    <li> 1- LRCP Layer-Resolution-Component-Position
	 *    <li> 2- RLCP Resolution-Layer-Component-Position
	 *    <li> 3- RPCL Resolution-Position-Component-Layer
	 *    <li> 4- PCRL Position-Component-Resolution-Layer
	 *    <li> 5- CPRL Component-Position-Resolution-Layer
	 *  </ul>
	 */
	int progressionOrder;
	
	String outputFile;
	int outputFileType;
	int zSize;
	int zOriginalSize;
	int yOriginalSize;
	int xOriginalSize;
	
	int cropType;
	boolean[] removeBand = null;
	
	BitStream initialCodedDC[][][] = null;
	BitStream codedBitDepthACBlock[][][] = null; 
	BitStream refinementDC[][][][] = null;
	BitStream sortingAC[][][][][] = null;
	BitStream recommendedRefinementAC[][][][][][] = null;
	BitStream terRefinementAC[][][][][] = null;
	
	int bitDepthDC[][] = null;
	int bitDepthAC[][] = null; 
	int part2Flag[][] = null;
	int part3Flag[][] = null;
	int part4Flag[][] = null;
	
	int padRows[] = null;
	int imageExtensionType[] = null;
	int transposeImage[] = null;
	int LSType;
	int LSComponents[] = null;
	int LSSubsValues[] = null;
	boolean headerMinMax = false;
	float minValue;
	float maxValue;
	
	int segByteLimit[][] = null; 
	int DCStop[][] = null;
	int bitPlaneStop[][] = null;
	int stageStop[][] = null;
	int useFill[][] = null;
	int blocksPerSegment[][] = null;
	int optDCSelect[][] = null;
	int optACSelect[][] = null;
	int WTType[] = null;
	int signedPixels[] = null;
	int pixelBitDepth[] = null;


	int codeWordLength[][] = null;
	int customWtFlag[] = null;
	float customWeight[][] = null; 
	int truncationPoints[][] = null;
	int adjustHeaderParameters[][] = null;
	int resolutionLevels[] = null;
	
	int WTLevels[] = null;
	int WTOrder[] = null;
	
	int gaggleSizeDC[][] = null	;
	int gaggleSizeAC[][] = null;	
	int idDC[][] = null	;
	int idAC[][] = null	;
	int entropyAC[][] = null;
	
	
	int numberOfLayers;
	int layerCreationType;
	int targetBytes;
	int layerSizeType;
	int layerBytes[] = null;
	
	RandomAccessFile fOut = null;
	
	/**
	 * Constructor of WriteFile
	 */
	public WriteFile(){

	}
	
	public void setParameters(
			String outputFile, int outputFileType, int progressionOrder,
			int cropType, boolean[] removeBand,int zOriginalSize,
			int zSize, int yOriginalSize,		
			BitStream initialCodedDC[][][], BitStream codedBitDepthACBlock[][][], BitStream refinementDC[][][][], 
			BitStream sortingAC[][][][][], BitStream recommendedRefinementAC[][][][][][],  BitStream terRefinementAC[][][][][],
			int bitDepthDC[][], int bitDepthAC[][], int part2Flag[][], int part3Flag[][], int part4Flag[][], 
			int imageExtensionType[], int padRows[], int transposeImage[], int LSType, 	int LSComponents[], int LSSubsValues[], 
			int segByteLimit[][], int DCStop[][],int bitPlaneStop[][], int stageStop[][], int useFill[][], 
			int blocksPerSegment[][], int optDCSelect[][], int optACSelect[][],
			int WTType[], int signedPixels[], int pixelBitDepth[], int xOriginalSize,
			int codeWordLength[][], int customWtFlag[],float customWeight[][],
			int truncationPoints[][], int adjustHeaderParameters[][],int resolutionLevels[],
			int WTLevels[], int WTOrder[],
			int gaggleSizeDC[][], int gaggleSizeAC[][], int idDC[][], int idAC[][], int entropyAC[][],
			int targetBytes, int numberOfLayers, int layerCreationType, int layerSizeType, int layerBytes[],
			boolean headerMinMax, float minValue, float maxValue
			){
		
		this.outputFileType = outputFileType;
		if (outputFileType==0){
			this.outputFile = outputFile+".rec";
		} else if (outputFileType==1){
			this.outputFile = outputFile+".ter";
		} 
		
		this.progressionOrder = progressionOrder;
		
		this.zSize = zSize;
		this.zOriginalSize = zOriginalSize;
		this.yOriginalSize = yOriginalSize;
		this.xOriginalSize = xOriginalSize;
		
		this.cropType = cropType;
		this.removeBand = removeBand;
		
		this.initialCodedDC = initialCodedDC;
		this.codedBitDepthACBlock = codedBitDepthACBlock;
		
		this.sortingAC = sortingAC;
		
		this.refinementDC = refinementDC;
		this.recommendedRefinementAC = recommendedRefinementAC;
		this.terRefinementAC = terRefinementAC;
		
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC;
		this.part2Flag = part2Flag;
		this.part3Flag = part3Flag;
		this.part4Flag = part4Flag;
		
		this.imageExtensionType = imageExtensionType;
		this.padRows = padRows;
		this.transposeImage = transposeImage;
		this.LSType = LSType;
		this.LSComponents = LSComponents;
		this.LSSubsValues = LSSubsValues;
		
		this.headerMinMax = headerMinMax;
		this.minValue = minValue;
		this.maxValue = maxValue;
		 
		this.segByteLimit = segByteLimit;
		this.DCStop = DCStop;
		this.bitPlaneStop = bitPlaneStop;
		this.stageStop = stageStop;
		this.useFill = useFill;
		 
		this.blocksPerSegment = blocksPerSegment;
		this.optDCSelect = optDCSelect;
		this.optACSelect = optACSelect;
		//this.xOriginalSize = xOriginalSize;
		this.WTType = WTType;
		this.signedPixels = signedPixels;
		this.pixelBitDepth = pixelBitDepth;
		
		this.codeWordLength = codeWordLength;
		this.customWtFlag = customWtFlag;
		this.customWeight = customWeight;
		
		this.truncationPoints = truncationPoints;
		this.adjustHeaderParameters = adjustHeaderParameters;
		this.resolutionLevels = resolutionLevels;
				
		this.WTLevels = WTLevels;
		this.WTOrder = WTOrder;
		
		
		this.gaggleSizeDC = gaggleSizeDC;
		this.gaggleSizeAC = gaggleSizeAC;
		
		this.idDC = idDC;
		this.idAC = idAC;
		this.entropyAC =  entropyAC;
		
		this.targetBytes = targetBytes;
		this.layerCreationType = layerCreationType;
		this.numberOfLayers = numberOfLayers;
		this.layerSizeType = layerSizeType;
		this.layerBytes = layerBytes;
		
		parametersSet = true;
	}
	
	/**
	 * Creates the encoded file according with the progression given by the user
	 * 
	 * @throws Exception when something goes wrong and the file cannot be written
	 */
	public void run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("File cannot be writen if parameters are not set.");
		}
		try{
			
			File newFile = new File(this.outputFile);
			if(newFile.exists()){
				newFile.delete();
				newFile.createNewFile();
			}
			
			fOut = new RandomAccessFile(newFile, "rw");
			
			if (outputFileType == 0){
				writeRecommendedFile(0);
			} else if (outputFileType == 1){
				if (progressionOrder == 0){
					 writeProgressionOrder0();
				} else {
					if (1<=progressionOrder && progressionOrder<=5){
						LayerCreation lc = new LayerCreation();
						lc.setParameters(initialCodedDC,refinementDC,
								codedBitDepthACBlock, sortingAC, terRefinementAC,
								bitDepthDC, bitDepthAC,
								zSize, blocksPerSegment, resolutionLevels, numberOfLayers, layerCreationType, 
								targetBytes,layerSizeType,layerBytes);
						lc.run();
						byte[][][][][] streams = lc.getByteArrays();
						int[][][][][] layersOffset = lc.getLayersOffset();
						
						numberOfLayers = lc.getNumberOfLayers();
						
						writeMainHeader();
						
						if (progressionOrder == 1){					
							writeProgressionOrder1(streams,layersOffset);
						} else if (progressionOrder == 2){
							writeProgressionOrder2(streams,layersOffset);
						} else if (progressionOrder == 3){
							writeProgressionOrder3(streams,layersOffset);
						} else if (progressionOrder == 4){
							writeProgressionOrder4(streams,layersOffset);
						} else if (progressionOrder == 5){
							writeProgressionOrder5(streams,layersOffset);
						}
					} else {
						throw new ParameterException("Unknown progressionOrder");
					}
				}
			} else {
				throw new ParameterException("Unknown file type.");
			}
			
			fOut.close();
			fOut = null;
		} catch(IOException e){
			throw new ErrorException(e.toString());
		}
	}
	
	public void writeRecommendedFile(int channel) throws Exception{
		RecommendedOrder ro = new RecommendedOrder();
		//The recommendation allows just one channel (gray-scaled images)
		ro.setParameters(initialCodedDC[channel], codedBitDepthACBlock[channel], 
				refinementDC[channel], sortingAC[channel], recommendedRefinementAC[channel],
				bitDepthDC[channel], bitDepthAC[channel], 
				part2Flag[channel], part3Flag[channel], part4Flag[channel], 
				padRows[channel], 
				segByteLimit[channel], DCStop[channel], bitPlaneStop[channel], stageStop[channel], useFill[channel], 
				blocksPerSegment[channel], optDCSelect[channel], optACSelect[channel], 
				WTType[channel], signedPixels[channel], pixelBitDepth[channel], xOriginalSize,
				transposeImage[channel], codeWordLength[channel], customWtFlag[channel] , customWeight[channel],
				truncationPoints[channel], adjustHeaderParameters[channel],resolutionLevels[channel]);
		
		ro.run(fOut);
		
	}
	
	public void writeProgressionOrder0() throws Exception{

		int headerLength = writeMainHeader();
		int headerBytesPerChannel = (int) Math.ceil(headerLength/zSize); 
		for(int z=0;z<zSize;z++){
			int headerBytesPerSegment = (int) Math.ceil(headerBytesPerChannel/this.blocksPerSegment[z].length);
			for(int segment = 0; segment<this.blocksPerSegment[z].length ; segment++){
				segByteLimit[z][segment] -= headerBytesPerSegment;
				adjustHeaderParameters[z][segment] = 1;
			}
			writeRecommendedFile(z);
		}
	}
	
	public void writeProgressionOrder1(byte[][][][][] streams,int[][][][][] layersOffset) throws Exception{
		//LRCP Layer-Resolution-Component-Position
		int maxResolutionLevels = GetMax.resolutionLevels(resolutionLevels);
		for(int layer = 0;layer<numberOfLayers;layer++){
			for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){
				for(int z=0;z<zSize;z++){
					for(int segment=0;segment<blocksPerSegment[z].length;segment++){
						if (rLevel<streams[z][segment].length){
							if (streams[z][segment][rLevel]!=null){
								for(int gaggle = 0;gaggle<streams[z][segment][rLevel].length;gaggle++){
									int packetLength = layersOffset[z][segment][rLevel][gaggle][layer+1] - layersOffset[z][segment][rLevel][gaggle][layer];
									writePacketHeader(packetLength);
									fOut.write(streams[z][segment][rLevel][gaggle],layersOffset[z][segment][rLevel][gaggle][layer],packetLength);
								}
							} 
						}
					}
				}
			}
		}
		
	}
	
	public void writeProgressionOrder2(byte[][][][][] streams,int[][][][][] layersOffset) throws Exception{
		//RLCP Resolution-Layer-Component-Position
		int maxResolutionLevels = GetMax.resolutionLevels(resolutionLevels);	
		for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){
			for(int layer = 0;layer<numberOfLayers;layer++){
				for(int z=0;z<zSize;z++){
					for(int segment=0;segment<blocksPerSegment[z].length;segment++){
						if (rLevel<streams[z][segment].length){
							if (streams[z][segment][rLevel]!=null){
								for(int gaggle = 0;gaggle<streams[z][segment][rLevel].length;gaggle++){
									int packetLength = layersOffset[z][segment][rLevel][gaggle][layer+1] - layersOffset[z][segment][rLevel][gaggle][layer];
									writePacketHeader(packetLength);
									fOut.write(streams[z][segment][rLevel][gaggle],layersOffset[z][segment][rLevel][gaggle][layer],packetLength);
								}
							} 
						}
					}
				}
			}
		}
		
	}
	public void writeProgressionOrder3(byte[][][][][] streams,int[][][][][] layersOffset) throws Exception{
		//RPCL Resolution-Position-Component-Layer
		int maxResolutionLevels = GetMax.resolutionLevels(resolutionLevels);
		int maxSegmentIndex = GetMax.segmentIndex(zSize,blocksPerSegment);
		int maxGaggleIndex = GetMax.gaggleIndex(zSize,resolutionLevels,blocksPerSegment,layersOffset);
		
		for(int rLevel=0;rLevel<maxResolutionLevels+1;rLevel++){
			for(int segment=0;segment<maxSegmentIndex;segment++){			
				for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){
					for(int z=0;z<zSize;z++){
						if (segment<streams[z].length){
							if (rLevel<streams[z][segment].length){
								if (streams[z][segment][rLevel]!=null){
									if (gaggle<streams[z][segment][rLevel].length){
										for(int layer = 0;layer<numberOfLayers;layer++){
											int packetLength = layersOffset[z][segment][rLevel][gaggle][layer+1] - layersOffset[z][segment][rLevel][gaggle][layer];
											writePacketHeader(packetLength);
											fOut.write(streams[z][segment][rLevel][gaggle],layersOffset[z][segment][rLevel][gaggle][layer],packetLength);
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
	
	public void writeProgressionOrder4(byte[][][][][] streams,int[][][][][] layersOffset) throws Exception{
		//PCRL Position-Component-Resolution-Layer
		int maxSegmentIndex = GetMax.segmentIndex(zSize,blocksPerSegment);
		int maxGaggleIndex = GetMax.gaggleIndex(zSize,resolutionLevels,blocksPerSegment,layersOffset);
		
		for(int segment=0;segment<maxSegmentIndex;segment++){
			for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){
				for(int z=0;z<zSize;z++){
					if (segment<streams[z].length){
						for(int rLevel=0;rLevel<streams[z][segment].length;rLevel++){
							if (gaggle<streams[z][segment][rLevel].length){
								for(int layer = 0;layer<numberOfLayers;layer++){
									int packetLength = layersOffset[z][segment][rLevel][gaggle][layer+1] - layersOffset[z][segment][rLevel][gaggle][layer];
									writePacketHeader(packetLength);
									fOut.write(streams[z][segment][rLevel][gaggle],layersOffset[z][segment][rLevel][gaggle][layer],packetLength);
								}
							}
						} 
					}
				}
			}
		}
	}
	
	public void writeProgressionOrder5(byte[][][][][] streams,int[][][][][] layersOffset) throws Exception{
		//CPRL Component-Position-Resolution-Layer
		int maxGaggleIndex = GetMax.gaggleIndex(zSize,resolutionLevels,blocksPerSegment,layersOffset);
		
		for(int z=0;z<zSize;z++){
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){
				for(int gaggle = 0;gaggle<maxGaggleIndex;gaggle++){
					for(int rLevel=0;rLevel<streams[z][segment].length;rLevel++){
						if (gaggle<streams[z][segment][rLevel].length){
							for(int layer = 0;layer<numberOfLayers;layer++){
								int packetLength = layersOffset[z][segment][rLevel][gaggle][layer+1] - layersOffset[z][segment][rLevel][gaggle][layer];
								writePacketHeader(packetLength);
								fOut.write(streams[z][segment][rLevel][gaggle],layersOffset[z][segment][rLevel][gaggle][layer],packetLength);
							}
						}
					} 
				}
			}
		}
		
	}
	
	public int writeMainHeader() throws Exception{
		TERHeader th = new TERHeader();
		
		th.setParameters(zSize,cropType,removeBand,
				zOriginalSize,yOriginalSize,xOriginalSize,progressionOrder,
				numberOfLayers,pixelBitDepth,signedPixels,
				WTLevels,WTType,WTOrder,
				imageExtensionType, padRows, transposeImage,LSType, LSComponents, LSSubsValues,
				customWtFlag, customWeight,
				blocksPerSegment,bitDepthDC, bitDepthAC, gaggleSizeDC, gaggleSizeAC, entropyAC,
				headerMinMax, minValue, maxValue);
		ByteStream mainHeader = th.run();
		int mainHeaderLength = mainHeader.getNumBytes();
		fOut.write(mainHeader.getByteStream(),0,mainHeaderLength);
		
		th = null;
		return mainHeaderLength;
	}

	private void writePacketHeader(int value) throws IOException{
		int numBytes = (value!=0) ? (int)Math.ceil(Math.log(value+1)/Math.log(2D)) : 1;
		numBytes =  ( (int) Math.ceil( numBytes/7D ) );	
		
		byte packetHeader[] = new byte[numBytes];
		int availableBits = 7*numBytes;
		int mask = 1 << (availableBits-1);
		
		for (int nbyte=0; nbyte<numBytes; nbyte++) {			
			packetHeader[nbyte] = (nbyte==(numBytes-1)) ? (byte)0x00 : (byte)0x80 ; 	// If last byte, the MSB must be 0, else 1			
			for (int bitPos=6; bitPos>=0; bitPos--) {
				if ( (value&mask) != 0) {
					packetHeader[nbyte] |= (byte)(1<<bitPos);					
				}
				mask >>= 1;
			}		
		}
		fOut.write(packetHeader,0,numBytes);
	}
}
