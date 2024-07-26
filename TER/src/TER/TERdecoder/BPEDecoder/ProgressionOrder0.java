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
import GiciImageExtension.*;
import TER.TERDefaultValues;
import TER.TERdecoder.ReadFile.ReadBufferedStream;
import TER.TERdecoder.SegmentDecoder.SegmentDecode2D;
import TER.TERdecoder.ReadFile.ReadRecommendedHeader;

/**
 * This class decodes an input stream according to the Progression Order 0.<br>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; set parameters<br>
 * &nbsp; run<br> 
 * &nbsp; get functions<br>
 * 
 * @author Group on Interactive Coding of Images (GICI)
 * @version 2.02  
 */
public class ProgressionOrder0{
	int zSize, yOriginalSize, xOriginalSize;
	float recoveredSamples[][][] = null;
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = true;
	
	
	int DCStop;
	int bitPlaneStop;
	int stageStop[];
	int useFill;
	int optDCSelect;
	int optACSelect;
	
	int imageExtensionType[] = null;
	int padRows[] = null;
	int WTType[] = null;
	int WTLevels[] = null;
	int WTOrder[] = null;
	int codeWordLength[] = null;
	int signedPixels[] = null;
	int pixelBitDepth[] = null;
	int imageWidth[] = null;				
	int transposeImg[] = null;
	int customWtFlag[] = null;
	float customWeight[][] = null;
	
	int segByteLimit[] = null;
	int resolutionLevels[];   
	int idDC;
	int idAC;
	
	boolean CVerbose[] =null;
	float gammaValue[] = null;
	int completionMode[] = null;
	
	int xSize[] = null;
	
	boolean minusHalf;
	int[][] blocksPerSegment = null;
	int[][] gaggleSizeDC = null;
	int[][] gaggleSizeAC = null;
	int[][] entropyAC = null;
	
	/**
	 * Constructor that receives the size of original image
	 * 
	 */
	public ProgressionOrder0(int zSize, int yOriginalSize, int xOriginalSize){
		this.zSize = zSize;
		this.yOriginalSize = yOriginalSize;
		this.xOriginalSize = xOriginalSize;
	}
	
	public void setParameters(int[] segByteLimit, float gammaValue[], int completionMode[], boolean CVerbose[],
			int WTType[], int WTLevels[], int WTOrder[],int stageStop[], boolean minusHalf,
			int[][] blocksPerSegment, int[][] gaggleSizeDC, int[][] gaggleSizeAC, int[][] entropyAC
			)throws ParameterException{
		
		// Other Parameters
		imageExtensionType = initializeParameter(TERDefaultValues.imageExtensionType);
		padRows = initializeParameter(TERDefaultValues.padRows);
		if (WTType==null){
			this.WTType = initializeParameter(TERDefaultValues.WTType);
		} else {
			this.WTType = WTType;
		} 
		if (WTLevels==null){
			this.WTLevels = initializeParameter(TERDefaultValues.WTLevels);
			this.resolutionLevels = initializeParameter(TERDefaultValues.WTLevels);
		} else {
			this.WTLevels = WTLevels;
			this.resolutionLevels = WTLevels;
		}
		if (WTOrder==null){
			this.WTOrder = initializeParameter(TERDefaultValues.WTOrder);
		} else {
			this.WTOrder = WTOrder;
		}
		
		codeWordLength = initializeParameter(TERDefaultValues.codeWordLength);
		signedPixels = initializeParameter(TERDefaultValues.signedPixels);
		pixelBitDepth = initializeParameter(TERDefaultValues.pixelBitDepth);				
		transposeImg = initializeParameter(TERDefaultValues.transposeImg);
		customWtFlag = initializeParameter(TERDefaultValues.customWtFlag);	
		customWeight = new float[zSize][];	

		this.imageWidth = initializeParameter(xOriginalSize);

		
		
		this.segByteLimit = segByteLimit;
		this.gammaValue = gammaValue;
		this.completionMode = completionMode;
		this.CVerbose = CVerbose;
		
		// befere start decoding we must initialize default parameters
		// BPE parameteres
		DCStop = TERDefaultValues.DCStop;
		bitPlaneStop = TERDefaultValues.bitPlaneStop;
		if (stageStop!=null){
			this.stageStop = stageStop;
		} else {
			this.stageStop = new int[zSize];
			for(int z=0;z<zSize;z++){
				this.stageStop[z] = this.WTLevels[z]+1;
			}
			
		}
		useFill = TERDefaultValues.useFill;
		
		optDCSelect = TERDefaultValues.optDCSelect;
		optACSelect = TERDefaultValues.optACSelect;
		
		
		this.blocksPerSegment = blocksPerSegment;
		
		
		this.gaggleSizeDC = gaggleSizeDC;
		this.gaggleSizeAC = gaggleSizeAC;
		this.entropyAC = entropyAC;
		
		idDC = 0;
		idAC = 0;
		
		

			
			
		boolean needExtension = ImageExtension.needImageExtension(imageExtensionType,WTLevels,xOriginalSize,yOriginalSize,zSize);
		if (needExtension){		
			int linesToAdd = 0;
			int columnsToAdd = 0;
			
			int requiredLines = ((int) 1 << this.WTLevels[0]); //2^(WTLevels[z]) 
			int requiredColumns = ((int) 1 << this.WTLevels[0]); //2^(WTLevels[z])
			
			if( yOriginalSize%requiredLines!=0){ 
				linesToAdd = requiredLines - yOriginalSize%requiredLines;
			}
			if( xOriginalSize%requiredColumns!=0 ){
				columnsToAdd = requiredColumns - xOriginalSize%requiredColumns;
			}
			
			int extendedySize = yOriginalSize + linesToAdd ;
			int extendedxSize = xOriginalSize + columnsToAdd ;
			this.recoveredSamples = new float[zSize][extendedySize][extendedxSize];
			
		} else {
			this.recoveredSamples = new float[zSize][yOriginalSize][xOriginalSize];
		}
			
			
		this.minusHalf = minusHalf;
		
		parametersSet = true;
		
	}
	
	public float[][][] run(ReadBufferedStream encodedStream) throws Exception {
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("Progression Order 0 cannot run if parameters are not properly set.");
		}
		
		
		
		AssembleSegments as = new AssembleSegments(recoveredSamples,null,zSize);
		int DCs[] = null;
		int ACs[][][][][] = null;
		
		int headerSegByteLimit = TERDefaultValues.segByteLimit;
		int userSegByteLimit = TERDefaultValues.segByteLimit;

		
		xSize = new int[zSize];
		for(int z=0;z<zSize;z++){
			int endImgFlag = 0;
			int segmentId = 0;
			
			while(endImgFlag!=1 && encodedStream.getRemaniningBytes()>0){
				
				encodedStream.restartCounter();
				ReadRecommendedHeader rh = new ReadRecommendedHeader();
				rh.setParameters(encodedStream);
				
				int headerLength = rh.run();

				int part2Flag = rh.getPart2Flag();
				int part3Flag = rh.getPart3Flag();
				int part4Flag = rh.getPart4Flag();
				endImgFlag = rh.getEndImgFlag();
				int bitDepthDC = rh.getBitDepthDC();
				int bitDepthAC = rh.getBitDepthAC();
				if (endImgFlag==1 && this.WTLevels[z] == 3){	
					this.padRows[z] = rh.getPadRows();
				}
				
				if (part2Flag==1){
					headerSegByteLimit = rh.getSegByteLimit();
					DCStop = rh.getDCStop();
					bitPlaneStop = rh.getBitPlaneStop();
					//stageStop = rh.getStageStop();
					useFill = rh.getUseFill();
				}
				if (part3Flag==1){
					blocksPerSegment[z][segmentId] = rh.getBlocksPerSegment();
					optDCSelect = rh.getOptDCSelect();
					optACSelect = rh.getOptACSelect();
				}
				if (part4Flag==1){
					/*if (WTType!=null) {
						WTType[z] = rh.getWTType();
					}*/
					this.codeWordLength[z] = rh.getCodeWordLength();
					this.signedPixels[z] = rh.getSignedPixels();
					this.pixelBitDepth[z] = rh.getPixelBitDepth();
					this.imageWidth[z] = rh.getImageWidth();				
					this.transposeImg[z] = rh.getTransposeImg();
					this.customWtFlag[z] = rh.getCustomWtFlag();
					if (this.customWtFlag[z]==1){
						if (this.WTLevels[z] == 3 ){
							this.customWeight[z] = rh.getCustomWeight();
						} else {
							throw new ParameterException("Recommended header only allows weights if 3 DWT levels have been applied.");
						}
					}
				}
				
				xSize[z] = imageWidth[z];
				int requiredColumns = ((int) 1 << WTLevels[z]); //2^(WTLevels[z])
				if( xSize[z]%requiredColumns!=0 ){
					xSize[z] += requiredColumns - (imageWidth[z]%requiredColumns);
				}
				
				SegmentDecode2D sd2d = new SegmentDecode2D(null,null,WTLevels[z],
						DCStop,blocksPerSegment[z][segmentId]);
				
				sd2d.setParameters(WTType[z], customWtFlag[z], customWeight[z],
						gaggleSizeDC[z][segmentId], gaggleSizeAC[z][segmentId], idDC, idAC,
						bitPlaneStop, stageStop[z],bitDepthDC, bitDepthAC, 
						resolutionLevels[z], entropyAC[z][segmentId], CVerbose, codeWordLength[z],
						gammaValue[z],completionMode[z]);
						
				//int segmentSize = segByteLimit[channel];
				int segmentSize = headerSegByteLimit;
				
				//The size of the segment is set according to the value in part2Header.
				if (segByteLimit!=null){
					if(segmentId<segByteLimit.length){
						userSegByteLimit = segByteLimit[segmentId];
					} else {
						userSegByteLimit = segByteLimit[segByteLimit.length-1];
					}
					if (part2Flag==0){
						// If part2Header is absent and the user gives some parameters, it is used the size given by the user.
						segmentSize = userSegByteLimit;
					}
				} 
				
				if (segmentSize > encodedStream.length() + headerLength){
					segmentSize = (int) encodedStream.length() + headerLength;
				}
				
				if (segmentSize > headerLength){				
					
					int bytesToDecode = segmentSize;
					
					// If the user gives a number of bytes per segment, it is supposed that the user only wants to decode up to 
					// this number of bytes.
					if (segByteLimit!=null){
						if (useFill == 1){
							if (userSegByteLimit>headerSegByteLimit){
								bytesToDecode = headerSegByteLimit;
								System.out.println("WARNING: The number of bytes asked for the user for the segment number "+segmentId+" is "+userSegByteLimit+", which is higher than the number of bytes available for this segment, only "+headerSegByteLimit+" bytes have been used");
							} else {
								if (segmentSize>=userSegByteLimit){
									bytesToDecode = userSegByteLimit;
								} else {
									bytesToDecode = segmentSize;
								}
							}
						} else {
							throw new WarningException("It is not possible to partially decode a segment if the last byte is unkown, it may cause problems to decode the next segment.");
						}
					}
					//byte[] segmentBytes = encodedStream.getByteArray(headerLength,bytesToDecode-1);
					
					byte[] segmentBytes = new byte[bytesToDecode-headerLength];				
					encodedStream.readFully(segmentBytes);
					
					ReadBufferedStream codedSegment = new ReadBufferedStream(segmentBytes);
					sd2d.run(codedSegment);				
					if (useFill == 1){
						codedSegment = null;
						segmentBytes = null;
					} else {
						if (codedSegment.length()>0){
							if (encodedStream.length()>0){
								// In this case we allocate all the bytes in an array in order to construct a new encodedStream
								// joining the bytes remaining in the previous segment plus the new bytes in the input stream
								byte[] codedSegmentRemainingBytes = new byte[(int) codedSegment.length()];
								byte[] encodedStreamRemainingBytes = new byte[(int) encodedStream.length()];
								byte[] remainingBytes = new byte[(int) ( codedSegment.length() + encodedStream.length() ) ];
								codedSegment.readFully(codedSegmentRemainingBytes);							
								encodedStream.readFully(encodedStreamRemainingBytes);
								
								
								for(int k=0;k<codedSegmentRemainingBytes.length;k++){
									remainingBytes[k] = codedSegmentRemainingBytes[k];
								}
								
								for(int k=0;k<encodedStreamRemainingBytes.length;k++){
									remainingBytes[k+codedSegmentRemainingBytes.length] = encodedStreamRemainingBytes[k];
								}
								
								codedSegmentRemainingBytes = null;
								encodedStreamRemainingBytes = null;
								encodedStream = null;
								encodedStream = new ReadBufferedStream(remainingBytes);
								
							} else {
								encodedStream = null;
								encodedStream = codedSegment;
							}
						}
					}

				}

				
				DCs = sd2d.getDCs();
				ACs = sd2d.getACs();
				
				
				
				as.setParameters(DCs, bitDepthDC, ACs, xSize[z], null, z, blocksPerSegment[z][segmentId]
				                ,WTLevels[z],resolutionLevels[z],DCStop, minusHalf);
				as.run();
				
				sd2d = null;
				DCs = null;
				ACs = null;
				segmentId++;
				
			}
			
		}
		recoveredSamples = as.getRecoveredImage();
		
		return recoveredSamples;
	}
	/**
	 * Initialize the parameter.
	 * @param  defaultValue integer that contains default value
	 *
	 * @return an integer array containing the required amount values to perform the coding process
	 */
	private int[] initializeParameter(int defaultValue) throws ParameterException{
		int [] parameter = new int[zSize];
		parameter[0] = defaultValue;
		
		return parameter;
		
	}
	
	/// GET FUNCTIONS ///
	public int[] getImageExtensionType(){
		return imageExtensionType;
	}
	public int[] getPadRows() {
		return padRows;
	}
	public int[] getWTType(){
		return WTType;
	}
	public int[] getWTLevels(){
		return WTLevels;
	}	
	public int[] getWTOrder(){
		return WTOrder;
	}
	public int[] getCodeWordLength(){
		return codeWordLength;
	}
	public int[] getSignedPixels(){
		return signedPixels;
	}
	public int[] getPixelBitDepth(){
		return pixelBitDepth;
	}
	public int[] getTransposeImg(){
		return transposeImg;
	}
	public int[] getCustomWtFlag(){
		return customWtFlag;
	}
	public float[][] getCustomWeight(){
		return customWeight;
	}
	public int[] getXSize(){
		return xSize;
	}
	public int[] getImageWidth(){
		return  imageWidth;
	}
	public float[][][] getRecoveredImage(){
		return recoveredSamples;
	}
}