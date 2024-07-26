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
import GiciException.*;
import GiciStream.*;
import TER.TERCommon.ParameterTools;
import TER.TERcoder.SegmentCoder.SegmentConstruct;
import TER.TERDefaultValues;



/**
 * This class generates headers from the TER coder. This heading is needed to encode/decode using options that are not considered by the Recommendation. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class TERHeader{

	BitStream TERHeader = null;
	boolean parametersSet = false;

	/**
	 * All following variables have the following structure:
	 *
	 * type nameVar; //variable to be saved at the heading
	 * final type nameVar_BITS; //number of bits allowed for this variable in the heading - its range will be from 0 to 2^nameVar_BITS, otherwise a WarningException will be thrown and heading will not be generated
	 */

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

	int cropType;
	final int cropType_BITS = 1;
	boolean[] removeBand = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.Coder#pixelBitDepth}
	 */
	int pixelBitDepth[];
	final int pixelBitDepth_BITS = 8;
	
	/** 
	 * Defintion in {@link TER.TERcoder.Coder#signedPixels}
	 */
	int signedPixels[];
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
	int LSComponents[] = null;
	final int LSComponents_BITS = 1;
	int LSSubsValues[] = null;
	boolean headerMinMax;
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
	 * Constructor of TERHeader.
	 * 
	 */
	public TERHeader(){
		
	}
	
	/**  It receives the information about the compressed image needed to be put in TERHeader.
	 *
	 */
	public void setParameters(
	int zSize,
	int cropType,
	boolean[] removeBand,
	int zOriginalSize,
	int yOriginalSize,
	int xOriginalSize,
	int progressionOrder,
	int layers,
	int pixelBitDepth[],
	int signedPixels[],
	int WTLevels[],
	int WTType[],
	int WTOrder[],
	int imageExtensionType[],
	int padRows[],
	int transposeImage[],
	int LSType,
	int LSComponents[],
	int LSSubsValues[],
	int customWtFlag[],
	float customWeight[][],
	int blocksPerSegment[][],
	int bitDepthDC[][],
	int bitDepthAC[][],
	int gaggleSizeDC[][],
	int gaggleSizeAC[][],
	int entropyAC[][],
	boolean headerMinMax,
	float minValue,
	float maxValue
	){
		//Parameters copy
		this.zSize = zSize;
		this.zOriginalSize = zOriginalSize;
		this.yOriginalSize = yOriginalSize;
		this.xOriginalSize = xOriginalSize;
		
		this.cropType = cropType;
		this.removeBand = removeBand;
		
		this.progressionOrder = progressionOrder;
		
		this.layers = layers;
		this.pixelBitDepth = pixelBitDepth;
		this.signedPixels = signedPixels;
		
		this.WTLevels = WTLevels;
		this.WTType = WTType;
		this.WTOrder = WTOrder;
		
		this.imageExtensionType = imageExtensionType;
		this.padRows = padRows;
		this.transposeImage = transposeImage;
		this.LSType = LSType;
		this.LSComponents = LSComponents;
		this.LSSubsValues = LSSubsValues;
		
		this.customWtFlag = customWtFlag;
		this.customWeight = customWeight;
		
		this.blocksPerSegment = blocksPerSegment;
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC;
		this.gaggleSizeDC = gaggleSizeDC;
		this.gaggleSizeAC = gaggleSizeAC;
		this.entropyAC = entropyAC;
		this.headerMinMax = headerMinMax;
		this.minValue = minValue;
		this.maxValue = maxValue;
		
		
		parametersSet = true;
		
	}

	/**
	 * Generates the TER heading.
	 *
	 * @throws WarningException when the heading can not be generated due to some variable exceeds the maximum allowed range
	 */
	public ByteStream run() throws WarningException{
		if(!parametersSet){
			throw new WarningException("TERHeader cannot run if parameters are not set.");
		}
		TERHeader = new BitStream(32);

		addImageProperties();
		
		if (progressionOrder!=0){
			addTransformParameters();
			addPreProcessingParameters();
			addQuantizationParameters();
			addBitPlaneEncoderParameters();
		}
		
		//Change BitStream to ByteStream and return it
		fillExtraBits();
		return(new ByteStream(TERHeader.getBitStream(), (int) TERHeader.getNumBytes()));
	}

	public void addImageProperties()throws WarningException{
		//zSize
		if((zOriginalSize < 0) || (zOriginalSize >= (int)Math.pow(2, zOriginalSize_BITS))){
			throw new WarningException("Wrong zSize.");
		}
		TERHeader.addBits(zOriginalSize, zOriginalSize_BITS);

		//ySize
		if((yOriginalSize < 0) || (yOriginalSize >= (int)Math.pow(2, yOriginalSize_BITS))){
			throw new WarningException("Wrong ySize.");
		}
		TERHeader.addBits(yOriginalSize, yOriginalSize_BITS);

		//xSize
		if((xOriginalSize < 0) || (xOriginalSize >= (int)Math.pow(2, xOriginalSize_BITS))){
			throw new WarningException("Wrong xSize.");
		}
		TERHeader.addBits(xOriginalSize, xOriginalSize_BITS);
		
		//cropType
		TERHeader.addBits(cropType,cropType_BITS);
		if (cropType!=0){
			for(int z=0;z<zOriginalSize;z++){
				TERHeader.addBit(removeBand[z]);
			}
		}
		
		//progressionOrder
		if(( progressionOrder< 0) || ( progressionOrder >= (int)Math.pow(2, progressionOrder_BITS))){
			throw new WarningException("Wrong progressionOrder.");
		}
		TERHeader.addBits(progressionOrder,progressionOrder_BITS);
		
		if (progressionOrder!=0){
			//layers
			if(( layers< 0) || ( layers >= (int)Math.pow(2, layers_BITS))){
				throw new WarningException("Wrong number of layers.");
			}
			
			TERHeader.addBits(layers,layers_BITS);
			//pixelBitDepth			
			addParameter(pixelBitDepth,pixelBitDepth_BITS,zSize);
			//signedPixels
			addParameter(signedPixels,signedPixels_BITS,zSize);
		} else {
			addProgressio0Paramaters();
		}
	}
	
	public void addProgressio0Paramaters() throws WarningException{
		// transform parameters
		addTransformParameters();
		// bit plane encoder parameters
		addBitPlaneEncoderParameters();
	}
	
	public void addTransformParameters()throws WarningException{

		addFlaggedParameter(WTLevels,TERDefaultValues.WTLevels,WTLevels_BITS,zSize);
		addFlaggedParameter(WTType,TERDefaultValues.WTType,WTType_BITS,zSize);
		addFlaggedParameter(WTOrder,TERDefaultValues.WTOrder,WTOrder_BITS,zSize);
	}
	
	public void addPreProcessingParameters()throws WarningException{

		addFlaggedParameter(imageExtensionType,TERDefaultValues.imageExtensionType,imageExtensionType_BITS,zSize);
		int padRows_BITS = 0;
		for(int z=0;z<WTLevels.length;z++){
			if(padRows_BITS<WTLevels[z]){
				padRows_BITS=WTLevels[z];
			}
		}
		padRows_BITS = (int) (1<<padRows_BITS);//2^(max(WTLevels))
		addFlaggedParameter(padRows,TERDefaultValues.padRows,padRows_BITS,zSize);
		addFlaggedParameter(transposeImage,TERDefaultValues.transposeImg,transposeImage_BITS,zSize);
		if(( LSType< 0) || ( LSType >= (int)Math.pow(2, LSType_BITS))){
			throw new WarningException("Wrong LSType.");
		}
		TERHeader.addBits(LSType,LSType_BITS);
		if(LSType!=0){
			addFlaggedParameter(LSComponents,0,LSComponents_BITS,zSize);
			if (LSType==4){
				for(int z=0;z<LSComponents.length;z++){
					if(LSComponents[z]==1){
						TERHeader.addBits(LSSubsValues[z],pixelBitDepth[z]);
					}
				}
			}
		}
		TERHeader.addBit(headerMinMax);
		if(headerMinMax){
			TERHeader.addBits(Float.floatToIntBits(minValue),Values_BITS);
			TERHeader.addBits(Float.floatToIntBits(maxValue),Values_BITS);
		}
		
	}
	
	public void addQuantizationParameters() throws WarningException{
		addFlaggedParameter(customWtFlag,TERDefaultValues.customWtFlag,customWtFlag_BITS,zSize);
		for(int z=0;z<customWtFlag.length;z++){
			if (customWtFlag[z]==1){
				for(int k=0;k<customWeight[z].length;k++){
					TERHeader.addBits(Float.floatToIntBits(customWeight[z][k]),customWeight_BITS);
				}
			}
		}
	}
	
	public void addBitPlaneEncoderParameters() throws WarningException{
		
		for(int z=0;z<zSize;z++){
			int segmentsPerChannel = this.blocksPerSegment[z].length;
			addFundamentalSequence(segmentsPerChannel-1);
			int bitDepth_BITS = (int) (Math.ceil( Math.log(1+( pixelBitDepth[z] + WTLevels[z] + 3 )) / Math.log(2) ));
			int maxBlocksPerSegment = 0;
			for(int segment=0;segment<segmentsPerChannel;segment++){
				if ( maxBlocksPerSegment < blocksPerSegment[z][segment]){
					maxBlocksPerSegment = blocksPerSegment[z][segment];
				}
			}
			int gaggle_BITS = (int) (Math.ceil( Math.log(1+(maxBlocksPerSegment)) / Math.log(2) ));
			if (segmentsPerChannel>1){
				int blocksPerChannel = SegmentConstruct.getBlocksPerChannel((int)(1<< WTLevels[z]),xOriginalSize,yOriginalSize);
				int blocksPerSegment_BITS = (int) (Math.ceil( Math.log(1+blocksPerChannel) / Math.log(2) ) );
				addParameter(blocksPerSegment[z],blocksPerSegment_BITS,segmentsPerChannel);
				
				if (progressionOrder!=0){
					addFlaggedParameter(bitDepthDC[z],0,bitDepth_BITS,segmentsPerChannel);
				}
				addFlaggedParameter(gaggleSizeDC[z],TERDefaultValues.gaggleDCSize,gaggle_BITS,segmentsPerChannel);
				if (progressionOrder!=0){
					addFlaggedParameter(bitDepthAC[z],0,bitDepth_BITS,segmentsPerChannel);
				}
				addFlaggedParameter(gaggleSizeAC[z],TERDefaultValues.gaggleACSize,gaggle_BITS,segmentsPerChannel);
				addFlaggedParameter(entropyAC[z],TERDefaultValues.entropyAC,1,segmentsPerChannel);
			} else {
				if (progressionOrder!=0){
					addParameter(bitDepthDC[z],bitDepth_BITS,1);
				}
				addParameter(gaggleSizeDC[z],gaggle_BITS,1);
				if (progressionOrder!=0){
					addParameter(bitDepthAC[z],bitDepth_BITS,1);
				}
				addParameter(gaggleSizeAC[z],gaggle_BITS,1);
				addParameter(entropyAC[z],gaggle_BITS,1);
				
			}
			
		}
		
	}
	
	public void addParameter(int parameter[], int parameter_BITS, int maxSize) throws WarningException {
		if ( parameter == null) {
			throw new WarningException("Parameter cannot be null.");
		} else if (parameter.length != maxSize ) {
			throw new WarningException("Parameter size is different from maxSize.");
		} else {
			if (maxSize==1){
				TERHeader.addBits(parameter[0],parameter_BITS);
			} else {
				int lastIndex = 0;
				for(int k=1;k<maxSize;k++){
					if (parameter[k-1] != parameter[k]){
						lastIndex = k;
					}
				}
				addFundamentalSequence(lastIndex);
				for(int k=0;k<=lastIndex;k++){
					TERHeader.addBits(parameter[k],parameter_BITS);
				}
			}
		}
	}
	
	public void addFlaggedParameter(int parameter[], int defaultValue, int parameter_BITS, int maxSize) throws WarningException {
		if ( parameter == null) {
			throw new WarningException("Parameter cannot be null.");
		} else if (parameter.length != maxSize ) {
			throw new WarningException("Parameter size is different from maxSize.");
		} else {
			boolean flag = true;
			if (maxSize==1){
				if ( parameter[0] == defaultValue ){
					flag = false;					
				}
				TERHeader.addBit(flag);
				if (flag){
					TERHeader.addBits(parameter[0],parameter_BITS);
				}
			} else {
				int lastIndex = 0;
				for(int k=1;k<maxSize;k++){
					if (parameter[k-1] != parameter[k]){
						lastIndex = k;
					}
				}
				if (lastIndex == 0 && parameter[0] == defaultValue){
					flag=false;	
				}
				TERHeader.addBit(flag);
				if (flag){
					addFundamentalSequence(lastIndex);
					for(int k=0;k<=lastIndex;k++){
						TERHeader.addBits(parameter[k],parameter_BITS);
					}
				}
			}
		}
	}
	
	public void addFundamentalSequence(int n){
		for (int k=0;k<n;k++){
			TERHeader.addBit(0);
		}
		TERHeader.addBit(1);
	}
	
	/**
	 * Fill the empty bits (if any) of the last byte. 
	 */
	private void fillExtraBits(){
		int numberExtraBits = (int) TERHeader.getNumBits() % 8;
		if (numberExtraBits!=0){
			TERHeader.addBits(0,8-numberExtraBits);
		}
	}
}