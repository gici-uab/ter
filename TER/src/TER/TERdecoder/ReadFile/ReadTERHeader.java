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
import TER.TERDefaultValues;
import TER.TERcoder.SegmentCoder.SegmentConstruct;

/**
 * This class receives a bit stream from which reads TER header .<br>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; set parameters<br>
 * &nbsp; run<br> 
 * &nbsp; get functions<br>  
 * 
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */

public class ReadTERHeader{
	
	ReadBufferedStream TERHeader = null;
	boolean parametersSet = false;
	boolean WTLevelsSet = false;
	boolean pixelBitDepthSet = false;
	
	int zSize;
	/**
	 * Definition in 
	 */
	int zOriginalSize;
	final int zOriginalSize_BITS = 14;

	/**
	 * Definition in 
	 */
	int yOriginalSize;
	final int yOriginalSize_BITS = 20;

	/**
	 * Definition in 
	 */
	int xOriginalSize;
	final int xOriginalSize_BITS = 20;
	
	int addType;
	final int addType_BITS = 1;
	boolean[] removedBand = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.Coder#pixelBitDepth}
	 */
	int pixelBitDepth[] = null;
	final int pixelBitDepth_BITS = 8;
	
	/** 
	 * Defintion in {@link TER.TERcoder.Coder#signedPixels}
	 */
	int signedPixels[] = null;
	final int signedPixels_BITS = 1;
	
	/**
	 * Definition in 
	 */
	int progressionOrder;
	final int progressionOrder_BITS = 3;
	
	/**
	 * Definition in 
	 */
	int layers;
	final int layers_BITS = 8;
	
	
	int WTLevels[] = null;
	int WTLevels_BITS = 4;
	int WTType[] = null;
	int WTType_BITS = 3;
	int WTOrder[] = null;
	int WTOrder_BITS = 2;
	
	int imageExtensionType[] = null;
	final int imageExtensionType_BITS = 2; 
	int padRows[] = null;
	int transposeImage[] = null;
	final int transposeImage_BITS = 1;
	int LSType;
	final int LSType_BITS = 3;
	int[] LSComponents = null;
	final int LSComponents_BITS = 1;
	int[] LSSubsValues = null;
	boolean headerMinMax = false;
	final int Values_BITS = 32;
	float minValue;
	float maxValue;
	
	int customWtFlag[] = null;
	final int customWtFlag_BITS = 1;
	float customWeight[][] = null;
	final int customWeight_BITS = 32;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 */
	int blocksPerSegment[][] = null;
	int bitDepthDC[][] = null;
	int bitDepthAC[][] = null;
	int gaggleSizeDC[][] = null;
	int gaggleSizeAC[][] = null;
	int entropyAC[][] = null;
	
	/**
	 * Constructor of ReadTERHeader. It does not receive anything. 
	 */
	public ReadTERHeader(){
		
	}
	
	/**
	 * Set the parameters needed used to read the header. 
	 * 
	 */
	public void setParameters(ReadBufferedStream TERHeader){
		
		this.TERHeader = TERHeader ;

		
		this.parametersSet = true;
	}
	
	/**
	 * Read the header contained in the bit stream and store the data required for decoding purposes
	 * 
	 * 
	 * @throws Exception when the parameters are not set. 
	 */
	public void run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("ReadTERHeader cannot run if parameters are not set.");
		}
		
		readImageProperties();
		
		if (progressionOrder!=0){
			readTransformParameters();
			readPreProcessingParameters();
			customWeight = new float[zSize][];
			readQuantizationParameters();
			readBitPlaneEncoderParameters();
		} else {
			readTransformParameters();
			readBitPlaneEncoderParameters();
		}
		
		TERHeader.clearByte();
	}
	
	public void readImageProperties()throws Exception{
		//zSize
		zOriginalSize = TERHeader.getBits(zOriginalSize_BITS);

		//ySize
		yOriginalSize = TERHeader.getBits(yOriginalSize_BITS);

		//xSize
		xOriginalSize = TERHeader.getBits(xOriginalSize_BITS);
		
		zSize = 0;
		//cropType
		addType = TERHeader.getBits(addType_BITS);
		if (addType!=0){
			removedBand = new boolean[zOriginalSize];
			for(int z=0;z<zOriginalSize;z++){
				removedBand[z] = TERHeader.getBit();
				if (!removedBand[z]){
					zSize++;
				}
			}
		} else {
			zSize = zOriginalSize;
		}
		
		//progressionOrder
		progressionOrder =  TERHeader.getBits(progressionOrder_BITS);
		
		if (progressionOrder!=0){
			//layers
			layers =  TERHeader.getBits(layers_BITS);
			//pixelBitDepth
			pixelBitDepth = readParameter(pixelBitDepth_BITS,zSize);
			this.pixelBitDepthSet = true;
			//signedPixels
			signedPixels = readParameter(signedPixels_BITS,zSize);
			
		}
	}
	
	public void readTransformParameters()throws Exception{
		
		WTLevels = readFlaggedParameter (TERDefaultValues.WTLevels,WTLevels_BITS,zSize);
		WTType = readFlaggedParameter (TERDefaultValues.WTType,WTType_BITS,zSize);
		WTOrder = readFlaggedParameter (TERDefaultValues.WTOrder,WTOrder_BITS,zSize);
		WTLevelsSet = true;
	}
	
	public void readPreProcessingParameters()throws Exception{
		if(!WTLevelsSet){
			throw new ParameterException("Pre-processing parameters cannot be read if the WTLevels parameter has not been previously set.");
		}
		imageExtensionType = readFlaggedParameter(TERDefaultValues.imageExtensionType,imageExtensionType_BITS,zSize);
		int padRows_BITS = 0;
		for(int z=0;z<WTLevels.length;z++){
			if(padRows_BITS<WTLevels[z]){
				padRows_BITS=WTLevels[z];
			}
		}
		padRows_BITS = (int) (1<<padRows_BITS);//2^(max(WTLevels))
		padRows = readFlaggedParameter(TERDefaultValues.padRows,padRows_BITS,zSize);
		transposeImage = readFlaggedParameter(TERDefaultValues.transposeImg,transposeImage_BITS,zSize);
		LSType =  TERHeader.getBits(LSType_BITS);
		if(LSType!=0){
			LSComponents = readFlaggedParameter(0,LSComponents_BITS,zSize);
			if (LSType==4){
				if(!pixelBitDepthSet){
					throw new ParameterException("LSSubsValues parameter cannot be read if the pixelBitDepthSet parameter has not been previously set.");
				}
				for(int z=0;z<LSComponents.length;z++){
					if(LSComponents[z]==1){
						LSSubsValues[z] = TERHeader.getBits(pixelBitDepth[z]);
					}
				}
			}
		}
		headerMinMax = TERHeader.getBit();
		if(headerMinMax){
			this.minValue = Float.intBitsToFloat(TERHeader.getBits(Values_BITS));
			this.maxValue = Float.intBitsToFloat(TERHeader.getBits(Values_BITS));
		}
	}
	
	public void readQuantizationParameters()throws Exception{
		customWtFlag = readFlaggedParameter(TERDefaultValues.customWtFlag,customWtFlag_BITS,zSize);
		
		for(int z=0;z<customWtFlag.length;z++){
			if (customWtFlag[z]==1){
				if(!WTLevelsSet){
					throw new ParameterException("customWeight parameter cannot be read if the WTLevels parameter has not been previously set.");
				}
				customWeight[z] = new float[(3*WTLevels[z])+1];
				for(int k=0;k<customWeight[z].length;k++){
					customWeight[z][k] = Float.intBitsToFloat(TERHeader.getBits(customWeight_BITS));
				}
			}
		}
	}
	
	public void readBitPlaneEncoderParameters() throws Exception{
		if(!WTLevelsSet){
			throw new ParameterException("Bit plane encoder parameters cannot be read if the WTLevels parameter has not been previously set.");
		}
		blocksPerSegment = new int[zSize][];
		bitDepthDC = new int[zSize][];
		bitDepthAC = new int[zSize][];
		gaggleSizeDC = new int[zSize][];
		gaggleSizeAC = new int[zSize][];
		entropyAC = new int[zSize][];
		
		for(int z=0;z<zSize;z++){
			
			int segmentsPerChannel = readFundamentalSequence() + 1 ;

			if (segmentsPerChannel>1){
				int blocksPerChannel = SegmentConstruct.getBlocksPerChannel((int)(1<< WTLevels[z]),xOriginalSize,yOriginalSize);
				int blocksPerSegment_BITS = (int) (Math.ceil( Math.log(1+blocksPerChannel) / Math.log(2) ) );
				blocksPerSegment[z] = readParameter(blocksPerSegment_BITS,segmentsPerChannel);
			} else {
				blocksPerSegment[z] = new int[segmentsPerChannel];
				blocksPerSegment[z][0] = SegmentConstruct.getBlocksPerChannel((int)(1<< WTLevels[z]),xOriginalSize,yOriginalSize);
				
			}
			
			int bitDepth_BITS = 0;
			if (progressionOrder!=0){ 
				bitDepth_BITS = (int) (Math.ceil( Math.log(1+( pixelBitDepth[z] + WTLevels[z] + 3 )) / Math.log(2) ));
			}
			
			int maxBlocksPerSegment = 0;
			for(int segment=0;segment<segmentsPerChannel;segment++){
				if ( maxBlocksPerSegment < blocksPerSegment[z][segment]){
					maxBlocksPerSegment = blocksPerSegment[z][segment];
				}
			}
			int gaggle_BITS = (int) (Math.ceil( Math.log(1+(maxBlocksPerSegment)) / Math.log(2) ));
			
			if (segmentsPerChannel>1){
				if (progressionOrder!=0){
					bitDepthDC[z] = readFlaggedParameter(0,bitDepth_BITS,segmentsPerChannel);
				}
				gaggleSizeDC[z] = readFlaggedParameter(TERDefaultValues.gaggleDCSize,gaggle_BITS,segmentsPerChannel);
				if (progressionOrder!=0){
					bitDepthAC[z] = readFlaggedParameter(0,bitDepth_BITS,segmentsPerChannel);
				}
				gaggleSizeAC[z] = readFlaggedParameter(TERDefaultValues.gaggleACSize,gaggle_BITS,segmentsPerChannel);
				entropyAC[z] = readFlaggedParameter(TERDefaultValues.entropyAC,1,segmentsPerChannel);
			} else {
				if (progressionOrder!=0){
					bitDepthDC[z] = readParameter(bitDepth_BITS,1);
				}
				gaggleSizeDC[z] = readParameter(gaggle_BITS,1);
				if (progressionOrder!=0){
					bitDepthAC[z] = readParameter(bitDepth_BITS,1);
				}
				gaggleSizeAC[z] = readParameter(gaggle_BITS,1);
				entropyAC[z] = readParameter(gaggle_BITS,1);
			}
		}
	}
	
	public int[] readParameter(int parameter_BITS, int maxSize) throws Exception {
		int parameter[] = new int[maxSize];
		if (maxSize==1){
			parameter[0] = TERHeader.getBits(parameter_BITS);
		} else {			
			int lastIndex = readFundamentalSequence();

			if (lastIndex>=maxSize){
				throw new WarningException("There are more parameters than maxSize. This is impossible, some error has been produced");
			} else {
				for(int k=0;k<=lastIndex;k++){
					parameter[k] = TERHeader.getBits(parameter_BITS);			
				}
				
				for(int k=lastIndex+1;k<maxSize;k++){
					parameter[k] = parameter[lastIndex];
				}
			}
			
		}
		return parameter;
	}
	
	public int[] readFlaggedParameter(int defaultValue, int parameter_BITS, int maxSize) throws Exception {
		int parameter[] = new int[maxSize];
		if (maxSize==1){
			if (TERHeader.getBit()){//the flag indicates that the parameter must be read
				parameter[0] = TERHeader.getBits(parameter_BITS);
			} else {
				parameter[0] = defaultValue;
			}
		} else {
			if (TERHeader.getBit()){//the flag indicates that the parameter must be read
				int lastIndex = readFundamentalSequence();
				
				if (lastIndex>=maxSize){
					throw new WarningException("There are more parameters than number of channels. This is impossible, some error has been produced");
				} else {
					for(int k=0;k<=lastIndex;k++){
						parameter[k] = TERHeader.getBits(parameter_BITS);					
					}
					
					for(int k=lastIndex+1;k<maxSize;k++){
						parameter[k] = parameter[lastIndex];
					}
				}
			} else {
				for(int k=0;k<maxSize;k++){
					parameter[k] = defaultValue;
				}
			}
		}
		return parameter;
	}
	
	public int readFundamentalSequence() throws Exception{
		int n=0;
		while (!TERHeader.getBit()){
			n++;
		}		
		return n;
	}
	
	//////////////////////////
	///// GET FUNCTIONS //////
	//////////////////////////
	public int getZSize(){
		return zSize;
	}
	public int getZOriginalSize(){
		return zOriginalSize;
	}
	public int getYOriginalSize(){
		return yOriginalSize;
	}
	public int getXOriginalSize(){
		return xOriginalSize;
	}
	public int getAddType(){
		return addType;
	}
	public boolean[] getRemovedBand(){
		return removedBand;
	}
	
	public int getProgressionOrder(){
		return progressionOrder;
	}

	public int getLayers(){
		return layers;
	}
	public int[] getPixelBitDepth(){
		return pixelBitDepth;
	}
	public int[] getSignedPixels(){
		return signedPixels;
	}
	
	public int[] getWTLevels(){
		return WTLevels;
	}
	public int[] getWTType(){
		return WTType;
	}
	public int[] getWTOrder(){
		return WTOrder;
	}
	
	public int[] getImageExtensionType(){
		return imageExtensionType;
	}
	public int[] getPadRows(){
		return padRows;
	}
	public int[] getTransposeImage(){
		return transposeImage;
	}
	public int getLSType(){
		return LSType;
	}
	public boolean[] getLSComponents(){
		return TERDefaultValues.integerToBooleanComponents(this.LSComponents,zSize,true);
	}
	public int[] getLSSubsValues(){
		return LSSubsValues;
	}
	
	public int[] getCustomWtFlag(){
		return customWtFlag;
	}
	public float[][] getCustomWeight(){
		return customWeight;
	}
	
	public int[][] getBlocksPerSegment(){
		return blocksPerSegment;
	}
	public int[][] getBitDepthDC(){
		return bitDepthDC;
	}
	public int[][] getBitDepthAC(){
		return bitDepthAC;
	}
	public int[][] getGaggleSizeDC(){
		return gaggleSizeDC;
	}
	public int[][] getGaggleSizeAC(){
		return gaggleSizeAC;
	}
	public int[][] getEntropyAC(){
		return entropyAC;
	}
	public int[] getXSizes(){
		int xSizes[] = new int[zSize];
		for(int z=0;z<zSize;z++){
			xSizes[z] = xOriginalSize;
			int requiredColumns = ((int) 1 << WTLevels[z]); //2^(WTLevels[z])
			if( xSizes[z]%requiredColumns!=0 ){
				xSizes[z] += requiredColumns - (xOriginalSize%requiredColumns);
			}
		}
		return xSizes;
	}
	
	public int[] getImageWidth(){
		int imageWidth[] = new int[zSize];
		for(int z=0;z<zSize;z++){
			imageWidth[z] = xOriginalSize;
		}
		return imageWidth;
	}
	
	public boolean getHeaderMinMax(){
		return headerMinMax;
	}
	public float getMinValue(){
		return minValue;
	}
	public float getMaxValue(){
		return maxValue;
	}
}