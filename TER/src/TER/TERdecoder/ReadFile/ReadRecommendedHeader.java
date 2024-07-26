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

import GiciException.*;

/**
 * This class receives a bit stream from which reads the header (compilant with the CCSDS recomendation for image data coding).<br>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; set parameters<br>
 * &nbsp; run<br> 
 * &nbsp; get functions<br>  
 * 
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class ReadRecommendedHeader{
	
	/**
	 * It is expected that the bit stream contains a header according the recommendation of CCSDS for image data coding.
	 */
	ReadBufferedStream header = null;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 *  Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthDC}
	 */	
	int bitDepthDC;
	
	/**
	 *  Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthAC}
	 */
	int bitDepthAC;
	
    /**
     * Segment counter value.
     * <p>
     * Valid values are integers between 1 and 256 encoded mod(256)
     */
	int segmentCount;
	
	/**
	 * Definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part2Flag}
	 */
	int part2Flag;
	
	/**
	 * Definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part3Flag} 
	 */
	int part3Flag;
	
	/**
	 *  Definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part4Flag}
	 */
	int part4Flag;
	
	/**
	 * Defintion in {@link GiciImageExtension.ImageExtension#padRows}
	 */
	int padRows;
	
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
	 * Defintion in {@link TER.TERcoder.WriteFile.RecommendedOrder#useFill}
	 */
	int useFill;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 */
	int blocksPerSegment;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optDCSelect}
	 */
	int optDCSelect;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optDCSelect}
	 */
	int optACSelect;
	
	/**
	 *  Definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
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
	 *  Definition in {@link GiciImageExtension.ImageDeExtension#imageWidth}
	 */
	int imageWidth;;
	
	/**
	 * Defintion in {@link GiciTransform.TransposeImage#transposeImg}
	 */
	int transposeImg;
	
	/**
	 * Defintion in {@link TER.TERcoder.WriteFile.RecommendedOrder#codeWordLength}
	 */
	int codeWordLength; 
	
	/**
	 * Defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 */
	int customWtFlag;
	
	/**
	 * efintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 */
	float customWeight[] = null;
	
	/**
	 * Flags initial segment in an image (component)
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 -  continuation segment in image
	 *     <li> 1 -  first segment in image
	 *   </ul> 
	 */	
	int startImgFlag;
	
	/**
	 * Flags final segment in an image (component)
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 -  not the last segment in image
	 *     <li> 1 -  last segment in image
	 *   </ul> 
	 */	
	int endImgFlag;
	
	/**
	 * Constructor of ReadRecommendedHeader. It does not receive anything. 
	 */
	public ReadRecommendedHeader(){
		
	}
	
	/**
	 * Set the parameters needed used to read the header, in this case, it is only necessary the bit stream containing the encoded image. 
	 * 
	 * @param header definition in 	{@link #header}
	 */
	public void setParameters(ReadBufferedStream header){
		
		this.header = header ;
		padRows = 0;
		
		this.parametersSet = true;
	}
	
	/**
	 * Read the header contained in the bit stream and store the data required for decoding purposes
	 * 
	 * @return an integer containing the size in bytes of the header
	 * 
	 * @throws Exception when the parameters are not set, the data in the bit stream is not compilant with CCSDS recommendation or the data in the bit stream cannot be read. 
	 */
	public int run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("ReadRecommendedHeader cannot run if parameters are not set.");
		}
		
		
		readHeaderPart1A();
		int headerLength = 3;
		if (endImgFlag == 1){ // PartFlag 1B is present
			readHeaderPart1B();
			headerLength += 1;
		} // else {PartFlag 1B is absent, default values are given}
			
		
		if (part2Flag == 1){ // Part2Flag is present
			readHeaderPart2();
			headerLength += 5;
		} // else {Part2Flag is absent}
		
		if (part3Flag == 1){ // Part3Flag is present
			readHeaderPart3();
			headerLength += 3;
		} // else {Part2Flag is absent}
		
		if (part4Flag == 1){ // Part2Flag is present
			readHeaderPart4();
			headerLength += 8;
		} // else {Part2Flag is absent}
		
		
		return headerLength;
	}
	
	/**
	 * Read the part 1A of the header
	 * 
	 * @throws Exception when the data in the bit stream is not compilant with CCSDS recommendation or the data in the bit stream cannot be read.
	 */
	private void readHeaderPart1A() throws Exception{
		
		this.startImgFlag = header.getBits(1) ;
		this.endImgFlag = header.getBits(1) ;
		this.segmentCount = header.getBits(8);
		bitDepthDC = header.getBits(5);
		bitDepthAC = header.getBits(5);
		int reserved = header.getBits(1);//reserved for the future
		if (reserved != 0){
			throw new ParameterException("\n This header is not compilant with the IDC Recommendation");
		}
		part2Flag = header.getBits(1);
		part3Flag = header.getBits(1);
		part4Flag = header.getBits(1);
		
		
	}
	
	/**
	 * Read the part 1B of the header
	 * 
	 * @throws Exception when the data in the bit stream is not compilant with CCSDS recommendation or the data in the bit stream cannot be read.
	 */
	private void readHeaderPart1B()  throws Exception{
		padRows  = header.getBits(3);
		int reserved = header.getBits(5); //reserved for the future
		if (reserved != 0){
			throw new ParameterException("\n This header is not compilant with the IDC Recommendation");
		}
		
	}
	
	/**
	 * Read the part 2 of the header
	 * 
	 * @throws Exception when the data in the bit stream is not compilant with CCSDS recommendation or the data in the bit stream cannot be read.
	 */
	private void readHeaderPart2()  throws Exception{
		segByteLimit = header.getBits(27);
		if (segByteLimit==0){
			segByteLimit = (int) 1<< 27;
		}
		DCStop = header.getBits(1);
		bitPlaneStop = header.getBits(5);
		stageStop = (header.getBits(2) ) +1;
		useFill = header.getBits(1);
		int reserved = header.getBits(4); //reserved for the future
		if (reserved != 0){
			throw new ParameterException("\n This header is not compilant with the IDC Recommendation");
		}
		
	}
	
	/**
	 * Read the part 3 of the header
	 * 
	 * @throws Exception when the data in the bit stream is not compilant with CCSDS recommendation or the data in the bit stream cannot be read.
	 */
	private void readHeaderPart3()  throws Exception{
		blocksPerSegment = header.getBits(20);
		optDCSelect = header.getBits(1);
		optACSelect = header.getBits(1);
		int reserved = header.getBits(2); //reserved for the future
		if (reserved != 0){
			throw new ParameterException("\n This header is not compilant with the IDC Recommendation");
		}
	}
	
	/**
	 * Read the part 4 of the header
	 * 
	 * @throws Exception when the data in the bit stream is not compilant with CCSDS recommendation or the data in the bit stream cannot be read.
	 */
	private void readHeaderPart4()  throws Exception{
		
		WTType = header.getBits(1) + 3 ;
		int reserved = header.getBits(2); //reserved for the future
		if (reserved != 0){
			throw new ParameterException("\n This header is not compilant with the IDC Recommendation");
		}
		signedPixels = header.getBits(1);
		
		pixelBitDepth = header.getBits(4);
		if ( pixelBitDepth == 0 ){
			pixelBitDepth = 16;
		}
		
		imageWidth = header.getBits(20);
		if (imageWidth == 0){
			imageWidth = (int) 1 << 20;
		}

		transposeImg = header.getBits(1);
		codeWordLength = header.getBits(2);
		reserved = header.getBits(1); //reserved for the future
		if (reserved != 0){
			throw new ParameterException("\n This header is not compilant with the IDC Recommendation");
		}
		
		customWtFlag = header.getBits(1);
		
		if (  customWtFlag == 0) {
			reserved = header.getBits(20);
		} else {
			customWeight = new float[10];
			for (int subband = 9; subband >=0 ; subband--){// 10 subbands are only allows for this kind of header
				int exponent = header.getBits(2);
				customWeight[subband] = (1 << exponent);
			}
		}
		reserved = header.getBits(11); //reserved for the future
		if (reserved != 0){
			throw new ParameterException("\n This header is not compilant with the IDC Recommendation");
		}
		

	}
	
	/////////////////////////
	////// GET FUNCTIONS ////
	/////////////////////////
	
	public int getStartImgFlag(){
		return this.startImgFlag;	
	}	

	public int getEndImgFlag(){
		return this.endImgFlag;	
	}

	public int getSegmentCount(){
		return this.segmentCount;	
	}
	
	public int getBitDepthDC(){
		return bitDepthDC;	
	}
	
	public int getBitDepthAC(){
		return bitDepthAC;	
	}
	
	public int getPart2Flag(){
		return part2Flag;	
	}
	
	public int getPart3Flag(){
		return part3Flag;	
	}
	
	public int getPart4Flag(){
		return part4Flag;	
	}
	
	public int getPadRows(){
		return padRows;	
	}
	
	public int getSegByteLimit(){
		return segByteLimit;	
	}
	
	public int getDCStop(){
		return DCStop;	
	}
	
	public int getBitPlaneStop(){
		return bitPlaneStop;	
	}
	
	public int getStageStop(){
		return stageStop;	
	}
	
	public int getUseFill(){
		return useFill;	
	}
	
	public int getBlocksPerSegment(){
		return blocksPerSegment;	
	}
	
	public int getOptDCSelect(){
		return optDCSelect;	
	}
	
	public int getOptACSelect(){
		return optACSelect;	
	}
	
	public int getWTType(){
		return WTType;	
	}
	
	public int getSignedPixels(){
		return signedPixels;	
	}
	
	public int getPixelBitDepth(){
		return pixelBitDepth;	
	}
	
	public int getImageWidth(){
		return imageWidth;	
	}

	public int getTransposeImg(){
		return transposeImg;	
	}
	
	public int getCodeWordLength(){
		return codeWordLength;	
	}

	public int getCustomWtFlag(){
		return customWtFlag;	
	}
	
	public float[] getCustomWeight(){
		return customWeight;	
	}
}
