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

import GiciException.ErrorException;
import GiciException.ParameterException;
import GiciStream.*;
import java.io.*;

/**
 * This class writes the encoded segments into a file.
 *  
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class RecommendedOrder{
	/**
	 * This array stores for each segment and gaggle the initial coding of DC quantized components
	 */
	BitStream initialCodedDC[][] = null;
	
	/**
	 * This array stores for each segment and gaggle the encoded bit depth of AC components of each block
	 */
	BitStream codedBitDepthACBlock[][] = null;
	
	/**
	 * This array stores for each segment, gaggle and bitplane the refinement of DC components
	 */
	BitStream refinementDC[][][] = null;
	
	/**
	 * 	This array stores for each segment, gaggle, bitplane and resolution level the encoded significance of AC components of the segment
	 *  sortingAC[segment][gaggle][bitPlane][resolutionLevel]
	 */
	BitStream sortingAC[][][][] = null;
	
	/**
	 * This array stores for each segment, gaggle, bitplane, block and resolution level the refinement of AC components
	 *	refinementAC[segment][gaggle][bitPlane][block][resolutionLevel]
	 */
	BitStream refinementAC[][][][][] = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#resolutionLevels}
	 */
	int resolutionLevels;
	
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * Defintion in {@link RecommendedInterleaving#truncationPoints}
	 */
	int[] truncationPoints = null;
	
	/**
	 * Indicates if parameters in the header must be re-definied in order to improve compression performance.
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - No adjustement
	 *     <li> 1 - Adjust segByteLimit and useFill after interleaving 
	 *   </ul> 
	 */
	int[] adjustHeaderParameters = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthDC}
	 */
	int bitDepthDC[] = null;

	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthAC} 
	 */
	int bitDepthAC[] = null;
	
	/**
	 * Indicates for each segment the presence of Part 2 header
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - Part 2 header present
	 *     <li> 1 - Part 2 header absent
	 *   </ul> 
	 */
	int part2Flag[] = null;

	/**
	 * Indicates for each segment the presence of Part 3 header
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - Part 3 header present
	 *     <li> 1 - Part 3 header absent
	 *   </ul> 
	 */
	int part3Flag[] = null;
	
	/**
	 * Indicates for each segment the presence of Part 4 header
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - Part 4 header present
	 *     <li> 1 - Part 4 header absent
	 *   </ul> 
	 */
	int part4Flag[] = null;
	
	/**
	 * Defintion in {@link GiciImageExtension.ImageExtension#padRows}
	 */
	int padRows;

	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#segByteLimit}
	 */
	int segByteLimit[] = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 */
	int DCStop[] = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitPlaneStop}
	 */
	int bitPlaneStop[] = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#stageStop}
	 */
	int stageStop[] = null;
	
	/**
	 * Indicates for each segment whether fill bits will be used to produce segByteLimit bytes in a segment
	 * <p>
	 * Valid values for wl DWT levels are:<br>
	 *   <ul>
	 *     <li> 0 - no fill bits 
	 *     <li> 1 - fill bytes up to produce a segment of SegByteLimit bytes 
	 *   </ul>
	 */
	int useFill[] = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 */
	int blocksPerSegment[] = null;

	/**
	 *  Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optDCSelect}
	 */
	int optDCSelect[] = null;
	
	/**
	 *  Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optACSelect}
	 */
	int optACSelect[] = null;
	
	/**
	 * Defintion in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 */
	int WTType;
	
	/**
	 * Defintion in {@link TER.TERcoder.Coder#signedPixels}
	 */
	int signedPixels;
	
	/**
	 * Defintion in {@link TER.TERcoder.Coder#pixelBitDepth}
	 */
	int pixelBitDepth;
	
	/**
	 * Image width.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int xOriginalSize;
	
	/**
	 * Defintion in {@link GiciTransform.TransposeImage#transposeImg}
	 */
	int transposeImg;
	
	/**
	 * Indicates the coded word length for each segment
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - 8-bit word 
	 *     <li> 1 - 16-bit word
	 *     <li> 2 - 24-bit word 
	 *     <li> 3 - 32-bit word
	 *   </ul> 
	 */
	int codeWordLength[] = null; 
		
	/**
	 * Defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 */
	int customWtFlag;
	
	/**
	 * Defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 */
	float customWeight[] = null;
	
	//This values are required to be fixed in order to be compilant with the Recommendation
	int gaggleSizeDC = 16;
	int gaggleSizeAC = 16;
	
	/**
	 * Constructor of WriteSegments
	 */
	public RecommendedOrder(){

	}
	
	/**
	 * Set the parameters required to create the encoded file
	 * 
	 */
	public void setParameters(BitStream initialCodedDC[][], BitStream codedBitDepthACBlock[][], 
			BitStream refinementDC[][][], BitStream sortingAC[][][][], BitStream refinementAC[][][][][],		
			int bitDepthDC[], int bitDepthAC[], 
			int part2Flag[], int part3Flag[], int part4Flag[], 
			int padRows, 
			int segByteLimit[], int DCStop[],int bitPlaneStop[], int stageStop[], int useFill[], 
			int blocksPerSegment[], int optDCSelect[], int optACSelect[], 
			int WTType, int signedPixels, int pixelBitDepth, int xOriginalSize,
			int transposeImg, int codeWordLength[],int customWtFlag,float customWeight[],
			int truncationPoints[], int adjustHeaderParameters[],int resolutionLevels ){
	
		this.initialCodedDC = initialCodedDC;
		this.codedBitDepthACBlock = codedBitDepthACBlock;
		this.refinementDC = refinementDC;
		this.sortingAC = sortingAC;
		this.refinementAC = refinementAC;
	
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC;
		this.part2Flag = part2Flag;
		this.part3Flag = part3Flag;
		this.part4Flag = part4Flag;
		
		this.padRows = padRows;
		
		this.segByteLimit = segByteLimit;
		this.DCStop = DCStop;
		this.bitPlaneStop = bitPlaneStop;
		this.stageStop = stageStop;
		this.useFill = useFill;
		
		this.blocksPerSegment = blocksPerSegment;
		this.optDCSelect = optDCSelect;
		this.optACSelect = optACSelect;
		
		this.WTType = WTType;
		this.signedPixels = signedPixels;
		this.pixelBitDepth = pixelBitDepth;
		this.xOriginalSize = xOriginalSize;
		this.transposeImg = transposeImg;
		this.codeWordLength = codeWordLength;
		this.customWtFlag = customWtFlag;
		this.customWeight = customWeight;
		
		this.resolutionLevels = resolutionLevels;
		this.truncationPoints = truncationPoints;
		this.adjustHeaderParameters = adjustHeaderParameters;
		
		parametersSet = true;
	}
	
	/**
	 * Creates the encoded file according with the progression given by the user
	 * 
	 * @throws Exception when something goes wrong and segments cannot be written
	 */
	public void run(RandomAccessFile fileOut) throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("Segments cannot be writen if parameters are not set.");
		}
		
		
		
		int numberOfSegments =  bitDepthDC.length;
		
		for(int segmentId=0 ; segmentId < numberOfSegments ; segmentId++){
			
			int headerLength = 0;
			if (adjustHeaderParameters[segmentId]!=0){// partXHeader is modified according to the adjust given by the user
				adjustHeaderFlags(segmentId);
			}
			int endImgFlag = 0;
			if (segmentId==numberOfSegments-1){
				endImgFlag = 1;
			}
			int startImgFlag = 0;
			if (segmentId == 0){
				startImgFlag = 1;
			}
			headerLength = RecommendedHeader.getLengthRecommendedHeader(endImgFlag,
					part2Flag[segmentId],part3Flag[segmentId],part4Flag[segmentId]);
			
			
			RecommendedInterleaving is = new RecommendedInterleaving();
			is.setParameters(initialCodedDC[segmentId],codedBitDepthACBlock[segmentId],refinementDC[segmentId], 
					sortingAC[segmentId],refinementAC[segmentId], bitDepthAC[segmentId],resolutionLevels, 
					segByteLimit[segmentId], DCStop[segmentId], bitPlaneStop[segmentId], stageStop[segmentId],
					truncationPoints[segmentId], headerLength, 
					pixelBitDepth, blocksPerSegment[segmentId],
					gaggleSizeDC, gaggleSizeAC);
			BitStream encodedSegment = is.run();
			
		
			// Fill the extra bytes required to have stream with valid size according to codeWordLength
			int numberExtraBytes = (int) encodedSegment.getNumBytes()%(codeWordLength[segmentId]+1);
			if(numberExtraBytes!=0){
				numberExtraBytes = (codeWordLength[segmentId]+1) - numberExtraBytes;
				for(int k=0;k<numberExtraBytes;k++){
					encodedSegment.addByte(0);
				}
			}	
			
			if (adjustHeaderParameters[segmentId]==1){// parameters are set using the values of the encoded bit stream
				if (segByteLimit[segmentId]>headerLength + (int) encodedSegment.getNumBytes()){
					segByteLimit[segmentId] = headerLength + (int) encodedSegment.getNumBytes();
				}
				part2Flag[segmentId] = 1;
			}
			
			RecommendedHeader header = new RecommendedHeader();
			header.setParameters(bitDepthDC[segmentId], bitDepthAC[segmentId], 
					part2Flag[segmentId], part3Flag[segmentId], part4Flag[segmentId], padRows, 
					segByteLimit[segmentId], DCStop[segmentId], bitPlaneStop[segmentId], stageStop[segmentId], useFill[segmentId],
					blocksPerSegment[segmentId], optDCSelect[segmentId], optACSelect[segmentId], WTType, signedPixels,
					pixelBitDepth, xOriginalSize, transposeImg, codeWordLength[segmentId], 
					customWtFlag, customWeight, startImgFlag, endImgFlag, segmentId);
			BitStream headerSegment = header.run();
			
			// here we consider the case where some bytes must be added in order to achieve the number of bytes in the segment 
			if (useFill[segmentId]==1 && segByteLimit[segmentId]>(headerLength + (int) encodedSegment.getNumBytes())){
				int remainingBytes= segByteLimit[segmentId] - (headerLength + (int) encodedSegment.getNumBytes());
				for(int k=0;k<remainingBytes;k++){
					encodedSegment.addByte(0);
				}
			}
			
			if (segByteLimit[segmentId]>=headerLength){
				//encodedChannel.addBytes(headerSegment.getByteArray(),(int)headerSegment.getNumBytes());
				fileOut.write(headerSegment.getBitStream(),0, (int) headerSegment.getNumBytes());
				if (segByteLimit[segmentId]< headerLength + (int) encodedSegment.getNumBytes()){
					fileOut.write(encodedSegment.getBitStream(),0, segByteLimit[segmentId]-headerLength);
					//int size = segByteLimit[segmentId] - headerLength;
					//encodedChannel.addBytes(encodedSegment.getByteArray(0,size), size);
				} else {
					fileOut.write(encodedSegment.getBitStream(),0, (int) encodedSegment.getNumBytes());
					//encodedChannel.addBytes(encodedSegment.getBitStream(),(int)encodedSegment.getNumBytes());
				}
			} else {
				throw new ErrorException("\n The size of the header is greater than the size of the segment. Compression must be stopped.");
			}
			
			header = null;
			is = null;
			encodedSegment = null;
			headerSegment = null;
			
		}
		
		
		

	}
	
	/**
	 * In case that some parameters must be redefined, here we asure that the parts of the header containing such parameters will be included.
	 *   
	 * @param segmentId this integer indicates the number of the segment that is being written 
	 */
	private void adjustHeaderFlags(int segmentId){
		if (adjustHeaderParameters[segmentId]==1){// part2Flag is set to one since segByteLimit and useFill will be required
			part2Flag[segmentId] = 1;
		}
	}
}