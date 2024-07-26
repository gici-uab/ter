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
 * This class creates a header according to the Recommendation.
 *  
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.1
 */
public class RecommendedHeader{
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * This stream contains the header of the segment
	 */
	BitStream header = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthDC}
	 */
	int bitDepthDC;

	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthAC} 
	 */
	int bitDepthAC;
	
	/**
	 * Indicates for each segment the presence of Part 2 header
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - Part 2 header absent
	 *     <li> 1 - Part 2 header present
	 *   </ul> 
	 */
	int part2Flag;

	/**
	 * Indicates for each segment the presence of Part 3 header
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - Part 3 header absent
	 *     <li> 1 - Part 3 header present
	 *   </ul> 
	 */
	int part3Flag;
	
	/**
	 * Indicates for each segment the presence of Part 4 header
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - Part 4 header absent
	 *     <li> 1 - Part 4 header present
	 *   </ul> 
	 */
	int part4Flag;
	
	/**
	 * Defintion in {@link GiciImageExtension.ImageExtension#padRows}
	 */
	int PadRows;

	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#segByteLimit}
	 */
	int segByteLimit;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 */
	int DCStop;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitPlaneStop}
	 */
	int bitPlaneStop;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#stageStop}
	 */
	int stageStop;
	
	/**
	 * Defintion in {@link RecommendedOrder#useFill}
	 */
	int useFill; 
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 */
	int blocksPerSegment;

	/**
	 *  Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optDCSelect}
	 */
	int optDCSelect;
	
	/**
	 *  Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optACSelect}
	 */
	int optACSelect;
	
	/**
	 * Defintion in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 */
	int WTType;
	
	/**
	 * Defintion in {@link TER.TERcoder.Coder#signedPixels}
	 */
	int SignedPixels;
	
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
	 * Defintion in {@link RecommendedOrder#codeWordLength}
	 */
	int codeWordLength; 
	
	/**
	 * Defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 */
	int customWtFlag;
	
	/**
	 * Defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 */
	float customWeight[] = null;
	
	/**
	 * Flags initial segment in an image (component)
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - continuation segment in image
	 *     <li> 1 - first segment in image
	 *   </ul> 
	 */
	int startImgFlag;
	
	/**
	 * Flags final segment in an image (component)
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - not the last segment in image
	 *     <li> 1 - last segment in image
	 *   </ul> 
	 */
	int endImgFlag;
	
	/**
	 * Indicates the position of the segment inside the encoded image.
	 */
	int segmentCount;
	
	/**
	 * Constructor of CreateHeader
	 *
	 */
	public RecommendedHeader(){
		
	}

	/**
	 * Set the parameters needed to create the header of the segment
	 * 
	 * @param bitDepthDC defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthDC}
	 * @param bitDepthAC defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthAC}
	 * @param part2Flag defintion in {@link #part2Flag}
	 * @param part3Flag defintion in {@link #part3Flag}
	 * @param part4Flag defintion in {@link #part4Flag}
	 * @param PadRows defintion in {@link GiciImageExtension.ImageExtension#padRows}
	 * @param segByteLimit defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#segByteLimit}
	 * @param DCStop defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 * @param bitPlaneStop defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitPlaneStop}
	 * @param stageStop defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#stageStop}
	 * @param useFill defintion in {@link RecommendedOrder#useFill}
	 * @param blocksPerSegment defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 * @param optDCSelect defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optDCSelect}
	 * @param optACSelect defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optACSelect}
	 * @param WTType defintion in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 * @param SignedPixels defintion in {@link TER.TERcoder.Coder#signedPixels}
	 * @param pixelBitDepth defintion in {@link TER.TERcoder.Coder#pixelBitDepth}
	 * @param xOriginalSize defintion in {@link #xOriginalSize}
	 * @param transposeImg defintion in {@link GiciTransform.TransposeImage#transposeImg}
	 * @param codeWordLength defintion in {@link RecommendedOrder#codeWordLength}
	 * @param customWtFlag defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 * @param customWeight defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 */
	public void setParameters(int bitDepthDC, int bitDepthAC, int part2Flag, int part3Flag, int part4Flag, 
			int PadRows, int segByteLimit, int DCStop, int bitPlaneStop, int stageStop, int useFill,
			int blocksPerSegment, int optDCSelect, int optACSelect, int WTType, int SignedPixels,
			int pixelBitDepth, int xOriginalSize, int transposeImg, int codeWordLength, 
			int customWtFlag, float customWeight[], int startImgFlag, int endImgFlag, int segmentCount){
		
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC;
		this.part2Flag = part2Flag;
		this.part3Flag = part3Flag;
		this.part4Flag = part4Flag;
		this.PadRows = PadRows;
		this.segByteLimit = segByteLimit;
		this.DCStop = DCStop;
		this.bitPlaneStop = bitPlaneStop;
		this.stageStop = stageStop;
		this.useFill = useFill;
		this.blocksPerSegment = blocksPerSegment;
		this.optDCSelect = optDCSelect;
		this.optACSelect = optACSelect;
		this.WTType = WTType;
		this.SignedPixels = SignedPixels;
		this.pixelBitDepth = pixelBitDepth;
		this.xOriginalSize = xOriginalSize;
		this.transposeImg = transposeImg;
		this.codeWordLength = codeWordLength;
		this.customWtFlag = customWtFlag;
		this.customWeight = customWeight;
		this.startImgFlag = startImgFlag;
		this.endImgFlag = endImgFlag;
		this.segmentCount = segmentCount;
		
		
		this.parametersSet = true;
	}
	
	/**
	 * Creates the header for each segment according with the recommendation of CCSDS for image data coding.
	 * 
	 * @return a bit stream containing the header of each segment
	 * 
	 * @throws Exception when something goes wrong and the creation of the header must be stopped
	 */
	public BitStream run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("CreateHeader cannot run if parameters are not set.");
		}

		header = new BitStream();
		
		putHeaderPart1A();
		
		if (endImgFlag == 1){ // PartFlag 1B is present
			putHeaderPart1B();
		} // else {PartFlag 1B is absent}
		
		if (part2Flag == 1){ // Part2Flag is present
			putHeaderPart2();
		} // else {Part2Flag is absent}
		
		if (part3Flag == 1){ // Part3Flag is present
			putHeaderPart3();
		} // else {Part2Flag is absent}
		
		if (part4Flag == 1){ // Part2Flag is present
			putHeaderPart4();
		} // else {Part2Flag is absent}
		
		
		
		
		return header;
	}

	/**
	 * Puts the part 1A of the header
	 */
	private void putHeaderPart1A(){
		header.addBits(startImgFlag,1);
		header.addBits(endImgFlag,1);
		header.addBits(segmentCount,8);
		header.addBits(bitDepthDC,5);
		header.addBits(bitDepthAC,5);
		header.addBits(0,1); //reserved for the future
		header.addBits(part2Flag,1);
		header.addBits(part3Flag,1);
		header.addBits(part4Flag,1);
	}	
	
	/**
	 * Puts the part 1B of the header
	 */
	private void putHeaderPart1B(){
		header.addBits(PadRows,3);
		header.addBits(0,5); //reserved for the future
	}
	
	/**
	 * Puts the part 2 of the header
	 */
	private void putHeaderPart2(){
		header.addBits(segByteLimit%134217728,27);
		header.addBits(DCStop,1);
		header.addBits(bitPlaneStop,5);
		header.addBits(stageStop-1,2);
		header.addBits(useFill,1);
		header.addBits(0,4); //reserved for the future
	}
	
	/**
	 * Puts the part 3 of the header
	 */
	private void putHeaderPart3(){
		header.addBits(blocksPerSegment%1048576,20);
		header.addBits(optDCSelect,1);
		header.addBits(optACSelect,1);
		header.addBits(0,2); //reserved for the future
	}
	
	/**
	 * Puts the part 4 of the header
	 */
	private void putHeaderPart4(){
		header.addBits( (WTType-3) ,1);
		header.addBits(0,2); //reserved for the future
		header.addBits(SignedPixels,1);
		header.addBits(pixelBitDepth%16,4);
		int twoPow20 = (int) 1 << 20;
		header.addBits( (int) (xOriginalSize % twoPow20) ,20);
		header.addBits(transposeImg,1);
		header.addBits(codeWordLength,2);
		header.addBits(0,1); //reserved for the future
		header.addBits(customWtFlag,1);
		if (customWtFlag == 0) {
			header.addBits(0,20);
		} else {
			for (int subband = this.customWeight.length-1; subband >=0  ; subband--){
				int exponent = (int) Math.round(Math.log(customWeight[subband])/Math.log(2));
				header.addBits(exponent,2);
			}
		}
		header.addBits(0,11); //reserved for the future
		
	}
	
	/**
	 * Computes the length of the header accoding to the given parameters
	 * 
	 * @param endImgFlag definition in {@link #endImgFlag}
	 * @param part2Flag definition in {@link #part2Flag}
	 * @param part3Flag definition in {@link #part3Flag}
	 * @param part4Flag definition in {@link #part4Flag}
	 * 
	 * @return an integer that represents the length of the segment header
	 */
	public static int getLengthRecommendedHeader(int endImgFlag, int  part2Flag, int part3Flag, int part4Flag){
		int length = 3;//sixe of the part1A header (compulsory)
		if ( endImgFlag==1 ){
			length++;
		}
		if ( part2Flag == 1 ){
			length+=5;
		}
		if ( part3Flag == 1 ){
			length+=3;
		}
		if ( part4Flag == 1 ){
			length+=8;
		}
		
		return length;
	}
}
