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
package TER.TERcoder;

import TER.TERCommon.ConversionTools;
import TER.TERCommon.ParameterTools;
import TER.TERcoder.SegmentCoder.*;
import TER.TERcoder.Weighting.*;
import TER.TERcoder.WriteFile.*;
import TER.TERDefaultValues;


import GiciAnalysis.ImageStatistical;
import GiciException.*;
import GiciImageExtension.*;
import GiciTransform.*;
import GiciStream.*;




/**
 * Main class of TERcoder application. It receives all parameters, checks its validity and runs TER coder.
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class Coder{
	
	int test3d=0;
	int spectralWTLevels = 0;
	int spectralWTType = 0;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * Original image samples (index meaning [z][y][x]).
	 * <p>
	 * All values allowed.
	 */
	float[][][] imageSamplesFloat = null;
	
	/**
	 * Array that contains the type of each image component.
	 * <p>
	 * Only primitive type value allowed (p.e.: Byte.TYPE, Integer.TYPE, ...).
	 */
	Class[] cTypes = null;
	
	/**
	 * Indicates if the three first components of the image corresponds to a RGB model.
	 * <p>
	 * True if three first components are RGB, false otherwise.
	 */
	boolean RGBComponents;
	
	/**
	 * Number of image components.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int zSize;
	
	/**
	 * Image height.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int ySize;
	
	/**
	 * Image width.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int xSize;
	
	/**
	 * Number of image components.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int zOriginalSize;
	
	/**
	 * Image height.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int yOriginalSize;
	
	/**
	 * Image width.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int xOriginalSize;
	
	/**
	 * Input file, name of the input image
	 * <p>
	 * Valid file name (with path).
	 */
	String inputFile;
	
	/**
	 * Output file name of coded bitstream.
	 * <p>
	 * Valid file name (with path).
	 */
	String outputFile;
	
	/**
	 * 
	 */
	int outputFileType;
	
	/**
	 * Definition in {@link GiciImageExtension.ImageExtension#imageExtensionType}
	 */
	int[] imageExtensionType = null;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 */
	int[] WTType = null;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 */
	int[] WTLevels = null;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTOrder}
	 */
	int[] WTOrder = null;
	
	/**
	 * Definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 */
	int[] customWtFlag = null; 
	
	/**
	 * Definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 */
	float[][] customWeight = null; 	 
	
	/**
	 * Definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part2Flag}
	 */
	int[][] part2Flag = null;
	
	/**
	 * Definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part3Flag}
	 */
	int[][] part3Flag = null;
	
	/**
	 * Definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part4Flag}
	 */
	int[][] part4Flag = null;
	
	/**
	 * Definition in {@link GiciImageExtension.ImageExtension#padRows}
	 */
	int[] padRows = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#segByteLimit}
	 */
	int[][] segByteLimit = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 */
	int[][] DCStop = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitPlaneStop}
	 */
	int[][] bitPlaneStop = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#stageStop}
	 */
	int[][] stageStop = null;
	
	/**
	 * Definition in {@link TER.TERcoder.WriteFile.RecommendedOrder#useFill}
	 */
	int[][] useFill = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 */
	int[][] blocksPerSegment = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optDCSelect}
	 */
	int[][] optDCSelect = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optACSelect}
	 */
	int[][] optACSelect = null;	 
	
	/**
	 * Specifies whether input pixel values are signed or unsigned quantities for each component
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - Unsigned
	 *     <li> 1 - Signed
	 *   </ul> 
	 */
	int[] signedPixels = null;
	
	/**
	 * Definition in {@link GiciTransform.TransposeImage#transposeImg}
	 */
	int[] transposeImg = null;
	
	/**
	 * Definition in {@link TER.TERcoder.WriteFile.RecommendedOrder#codeWordLength}
	 */
	int[][] codeWordLength = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#gaggleDCSize}
	 */
	int[][] gaggleDCSize = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#gaggleACSize}
	 */
	int[][] gaggleACSize = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#idDC}
	 */
	int[][] idDC  = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#idAC}
	 */
	int[][] idAC  = null;	
	
	/**
	 * Specifies the input pixel bit depth for each component
	 * <p>
	 * Valid values are positive values
	 */
	int[] pixelBitDepth = null;

	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.EncodeGaggleAC#entropyAC}
	 */
	int[][] entropyAC = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#resolutionLevels}
	 */
	int[] resolutionLevels = null;

	/**
	 * Used for verbose information (time for stage).
	 * <p>
	 * 0 is initial time.
	 */
	long initStageTime = 0;

	/**
	 * Used for verbose information (total time).
	 * <p>
	 * 0 is initial time.
	 */
	long initTime = 0;
	
	/**
	 * Show some information about compression process. Each value is boolean (false means NO show, true means show).
	 * <p>
	 * Each value represents the following:<br>
	 *  <ul>
	 *    <li> 0- Information about time and used memory for each compression stage
	 *    <li> 1- TER parameters
	 *  </ul>
	 */
	boolean[] CVerbose = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.WriteFile.RecommendedInterleaving#truncationPoints}
	 */
	int[][] truncationPoints = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.WriteFile.RecommendedOrder#adjustHeaderParameters} 
	 */
	int[][] adjustHeaderParameters = null;
	
	int progressionOrder;
	
	boolean[] controlMSE;
	
	float[] desiredDistortion;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#distortion}
	 */
	int[] distortionMeasure = null ;
	
	/**
	 * Float array that contains the compression factor for each segment
	 */
	float[][] compressionFactor = null;
	
	/**
	 * Definition in {@link GiciTransform.LevelShift#LSType}
	 */
	int LSType;

	/**
	 * Definition in {@link GiciTransform.LevelShift#LSComponents}
	 */
	int[] LSComponents = null;

	/**
	 * Definition in {@link GiciTransform.LevelShift#LSSubsValues}
	 */
	int[] LSSubsValues = null;
	
	 /**
	  * Definition in {@link GiciTransform.CoefficientsApproximation#approximationTypes}
	  */
	int[] coefficientsApproximationTypes = null;
	
	int[] targetBytes = null;;
	int numberOfLayers;
	int layerCreationType;
	int layerSizeType;
	int layerBytes[] = null;
	
	int cropType;
	boolean[] removeBand= null;
	
	
	boolean headerMinMax = true;
	float minValue, maxValue;
	
	/**
	 * Constructor of TERcoder. It receives original image and some information about it.
	 *
	 * @param imageSamplesFloat definition in this class
	 * @param cTypes definition in this class
	 * @param RGBComponents definition in this class
	 */
	public Coder(float[][][] imageSamplesFloat, Class[] cTypes, boolean RGBComponents){
		//Image data copy
		this.imageSamplesFloat = imageSamplesFloat;
		this.cTypes = cTypes;
		this.RGBComponents = RGBComponents;
		
		// Size set
		zSize = imageSamplesFloat.length;
		ySize = imageSamplesFloat[0].length;
		xSize = imageSamplesFloat[0][0].length;
		
		
		zOriginalSize = imageSamplesFloat.length;
		yOriginalSize = imageSamplesFloat[0].length;
		xOriginalSize = imageSamplesFloat[0][0].length;
	}
	
		 
	/**
	 * Set the parameters used to perform the compression and check validity. If a parameter is not initialized, this class initalizes it with default values.
	 * 
	 * @param outputFile definition in {@link #outputFile}
	 * @param outputFileType definition in {@link #outputFileType}
	 * @param imageExtensionType definition in {@link GiciImageExtension.ImageExtension#imageExtensionType} 
	 * @param WTType definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param WTOrder definition in {@link GiciTransform.ForwardWaveletTransform#WTOrder}
	 * @param customWtFlag definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 * @param customWeight definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 * @param part2Flag definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part2Flag}
	 * @param part3Flag definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part3Flag}
	 * @param part4Flag definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part4Flag}
	 * @param segByteLimit definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#segByteLimit}
	 * @param DCStop definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 * @param bitPlaneStop definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitPlaneStop}
	 * @param stageStop definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#stageStop}
	 * @param useFill definition  {@link TER.TERcoder.WriteFile.RecommendedOrder#useFill}
	 * @param blocksPerSegment definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 * @param optDCSelect definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optDCSelect}
	 * @param optACSelect definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optACSelect}
	 * @param signedPixels definition in {@link #signedPixels}
	 * @param transposeImg definition in {@link GiciTransform.TransposeImage#transposeImg}
	 * @param codeWordLength definition in {@link TER.TERcoder.WriteFile.RecommendedOrder#codeWordLength}
	 * @param pixelBitDepth definition in {@link #pixelBitDepth}
	 * @param gaggleDCSize definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#gaggleDCSize}
	 * @param gaggleACSize definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#gaggleACSize}
	 * @param idDC definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#idDC}
	 * @param idAC definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#idAC}
	 * @param desiredDistortion definition in {@link #desiredDistortion}
	 * @param distortionMeasure definition in {@link #distortionMeasure}
	 * @param entropyAC definition in {@link TER.TERcoder.SegmentCoder.EncodeGaggleAC#entropyAC}
	 * @param resolutionLevels definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#resolutionLevels}
	 * @param compressionFactor definition in {@link #compressionFactor}
	 * @param CVerbose definition in {@link #CVerbose}
	 * @param truncationPoints defintion in {@link TER.TERcoder.WriteFile.RecommendedInterleaving#truncationPoints}
	 * @param adjustHeaderParameters defintion in {@link TER.TERcoder.WriteFile.RecommendedOrder#adjustHeaderParameters} 
	 * @param LSType definition in {@link GiciTransform.LevelShift#LSType}
	 * @param LSComponents definition in {@link GiciTransform.LevelShift#LSComponents}
	 * @param LSSubsValues definition in {@link GiciTransform.LevelShift#LSSubsValues}
	 * @param coefficientsApproximationTypes
	 * @param targetBytes
	 * @param numberOfLayers
	 * @param layerCreationType
	 * @param layerSizeType
	 * @param layerBytes
	 * @param bpp indicates the bits per pixel that must be employed to encode each segment
	 * @param test3d
	 * @param spectralWTLevels
	 * @param spectralWTType
	 * 
	 * 
	 * @throws ParameterException when an invalid or unrecognized exception is detected it will be thrown
	 */ 
	public void setParameters(
			String outputFile,
			int outputFileType, 
			int[] imageExtensionType,
			int[] WTType,
			int[] WTLevels,
			int[] WTOrder,
			int[] customWtFlag,
			float[] customWeight,
			int[] part2Flag,
			int[] part3Flag,
			int[] part4Flag,
			int[] segByteLimit,
			int[] DCStop,
			int[] bitPlaneStop,
			int[] stageStop,
			int[] useFill,
			int[] blocksPerSegment,
			int[] optDCSelect,
			int[] optACSelect,
			int[] signedPixels,
			int[] transposeImg,
			int[] codeWordLength,
			int[] pixelBitDepth,
			int[] gaggleDCSize,
			int[] gaggleACSize,
			int[] idDC, 
			int[] idAC,
			float[] desiredDistortion,
			int[] distortionMeasure,
			int[] entropyAC,
			int[] resolutionLevels,
			float[] compressionFactor,
			boolean[] CVerbose,
			float[] bps,
			int[] truncationPoints,
			int[] adjustHeaderParameters,
			int progressionOrder,
			int LSType,
			int[] LSComponents,
			int[] LSSubsValues,
			int[] coefficientsApproximationTypes,
			int[] targetBytes, float[] bpppb,
			int numberOfLayers, int layerCreationType,
			int layerSizeType, int layerBytes[],
			int test3d, int spectralWTLevels, int spectralWTType
	) throws ParameterException{
		///////////////////////////////////////
		CropChannels cc = new CropChannels(imageSamplesFloat);
		//this.cropType = outputFileType;
		this.cropType = 0;
		cc.setParameters(this.cropType);
		
		try{
			imageSamplesFloat = cc.run();
		} catch (ErrorException e){
			throw new ParameterException("Some problem appeared while cropping the image");
		}
		
		removeBand = cc.getRemoveBand();
		cc = null;
		//Size set
		zSize = imageSamplesFloat.length;
		ySize = imageSamplesFloat[0].length;
		xSize = imageSamplesFloat[0][0].length;
		////////////////////////////////////////////////
		
		this.test3d = test3d;
		this.spectralWTLevels = spectralWTLevels;
		this.spectralWTType = spectralWTType;
		
		parametersSet = true;
		
		if (CVerbose!=null){
			this.CVerbose = CVerbose;
		} else {
			this.CVerbose = TERDefaultValues.CVerbose;
		}
		
		//IMAGE COMPONENTS BIT DEPTH
		this.pixelBitDepth = pixelBitDepth;
		
		//OUTPUT FILE NAME AND TYPE
		this.progressionOrder = progressionOrder;
		this.outputFileType = outputFileType;
		this.outputFile = outputFile;
		if (outputFileType==0){
			//first we must berify if given parameters are possible in the Recommendation
			if (!VerifyRecommendationParameters.verifyRecommendationParameters(
					imageExtensionType,	WTType,	WTLevels, WTOrder,
					gaggleDCSize, gaggleACSize, idDC, idAC,
					entropyAC, resolutionLevels, LSType	)) {
				throw new ParameterException(" Some of the given parameters are out of the Recommendation. \n If you want to work out the Reccomendation you must use -of 1 (--outputfileType 1). \n See help for more detail about parameters");
			}
			//if parameters are possible the coding process continues
			//this.outputFile += ".rec";
			// else coding must be stopped
		} else if (outputFileType==1){
			//this.outputFile += ".ter";
			if (headerMinMax){
				ImageStatistical is = new ImageStatistical(imageSamplesFloat);
				double tmp[] = is.getTotalMinMax();
				minValue = (int) Math.round(tmp[0]);
				maxValue = (int) Math.round(tmp[1]);
				
			}
		} else {
			throw new ParameterException("Unknown file type.");
		}
		
		
		//////////////////////
		// Transpose Image ///
		//////////////////////
		this.transposeImg = ParameterTools.setParameterInt(transposeImg,zSize,TERDefaultValues.transposeImg);
		
		//////////////////////
		/// Level Shift  /////
		//////////////////////
		this.LSType = LSType;
		this.LSComponents = ParameterTools.setParameterInt(LSComponents,zSize, TERDefaultValues.LSComponents);
		this.LSSubsValues = ParameterTools.setParameterInt(LSSubsValues,zSize,0);
		
		//////////////////
		//EXTENSION TYPE//
		//////////////////
		//verify parameters
		if( !ParameterTools.verifyParameter(imageExtensionType, zSize, 0 , 2) ){
			throw new ParameterException("Wrong parameters for applying image extension.");
		}
		//set parameters
		this.imageExtensionType = ParameterTools.setParameterInt(imageExtensionType,zSize,TERDefaultValues.imageExtensionType);
		
		
		/////////////////////
		//WAVELET TRANSFORM//
		/////////////////////
		//verify parameters
		if( !ParameterTools.verifyParameter(WTType, zSize, 0 , 6) ){
			throw new ParameterException("Wrong parameters for WTType.");
		}
		if( !ParameterTools.verifyParameter(WTLevels, zSize, 0 , Integer.MAX_VALUE) ){
			throw new ParameterException("Wrong parameters for WTLevels.");
		}
		if( !ParameterTools.verifyParameter(WTOrder, zSize, 0 , 2) ){
			throw new ParameterException("Wrong parameters for WTOrder.");
		}
		
		//set parameters
		this.WTType = ParameterTools.setParameterInt(WTType,zSize,TERDefaultValues.WTType);
		this.WTLevels = ParameterTools.setParameterInt(WTLevels,zSize,TERDefaultValues.WTLevels);
		this.WTOrder = ParameterTools.setParameterInt(WTOrder,zSize,TERDefaultValues.WTOrder);

		boolean needExtension = 
			ImageExtension.needImageExtension(this.imageExtensionType,this.WTLevels,
					xSize,ySize,zSize);
		
		if ( !needExtension ) { // no extension is performed
			padRows = new int[zSize];
			for(int z=0 ; z < zSize ; z++){
				padRows[z] = 0;
			}
		}
		
		/////////////
		//WEIGHTING//
		/////////////
		//verify parameters
		if( !(ForwardWeighting.verifyParameters(customWtFlag, this.WTLevels, customWeight, zSize)) ){
			throw new ParameterException("Wrong parameters for applying weighting.");
		}	 
		//set parameters
		//The order of the next calls to functions can not be inverted, due it is
		//necessary to fix customWeight before setting customWtFlag
		this.customWeight = ForwardWeighting.setCustomWeights(customWtFlag, this.WTLevels,customWeight,zSize);
		this.customWtFlag = ParameterTools.setParameterInt(customWtFlag,zSize,TERDefaultValues.customWtFlag);
		
		
		///////////////////////////////
		// COEFFICIENT APPROXIMATION //
		///////////////////////////////
		this.coefficientsApproximationTypes = ParameterTools.setParameterInt(coefficientsApproximationTypes,zSize,TERDefaultValues.coefficientsApproximationTypes);
		
		/////////////////////
		//BIT PLANE ENCODER//
		/////////////////////
		if( !(TER.TERcoder.SegmentCoder.SegmentConstruct.verifyParameters(
				part2Flag, part3Flag, part4Flag, 
				segByteLimit, DCStop, bitPlaneStop,
				stageStop, useFill,	blocksPerSegment,
				optDCSelect, optACSelect, signedPixels,
				transposeImg, codeWordLength, 
				gaggleDCSize, gaggleACSize, idDC, idAC, 
				distortionMeasure, entropyAC, resolutionLevels, this.WTLevels) ) ){
			throw new ParameterException("Wrong parameters for applying encode transformed data.");
		}
		// the order in which the parameters are set is really important. First must be set BlocksPerSegment.
		this.blocksPerSegment = 
			TER.TERcoder.SegmentCoder.SegmentConstruct.setBlocksPerSegment(blocksPerSegment,this.WTLevels,xSize,ySize,zSize);
		
		if(this.blocksPerSegment != null){
			this.pixelBitDepth = ParameterTools.setCroppedParameterInt(pixelBitDepth,zSize,8,removeBand);
			
			if ( (segByteLimit!=null && bps!=null) || (compressionFactor!=null && bps!=null) ||  (segByteLimit!=null && compressionFactor!=null) ){
				throw new ParameterException("segByteLimit, bitsPerPixel and compressionFactor are not compatible parameters");
			}
						
			if (bps==null && compressionFactor==null){
				if (segByteLimit!=null){
					for(int k=0;k<segByteLimit.length;k++){
						if (segByteLimit[k]==0){
							segByteLimit[k] = TERDefaultValues.segByteLimit;
						}
					}
				}
				this.segByteLimit = ParameterTools.setParameterMatrix(segByteLimit,this.blocksPerSegment,TERDefaultValues.segByteLimit);
				
			} else if (compressionFactor==null && bps!=null){
				
				this.segByteLimit=ConversionTools.getSegByteLimitFromBPS(bps, this.blocksPerSegment, this.WTLevels, yOriginalSize, xOriginalSize, this.pixelBitDepth);
				
			} else if (compressionFactor!=null && pixelBitDepth!=null){
				this.segByteLimit=ConversionTools.getSegByteLimitFromCompressionFactor(compressionFactor,this.pixelBitDepth, this.blocksPerSegment, this.WTLevels, yOriginalSize, xOriginalSize);
			}
			
			this.signedPixels = ParameterTools.setCroppedParameterInt(signedPixels,zSize,TERDefaultValues.signedPixels,removeBand);
			int maxResolutionLevels = 0;
			int minResolutionLevels = this.WTLevels[0];
			for(int k=0; k<this.WTLevels.length ; k++){
				if (maxResolutionLevels < this.WTLevels[k] ){
					maxResolutionLevels = this.WTLevels[k];
				} 
				if (minResolutionLevels > this.WTLevels[k] ){
					minResolutionLevels = this.WTLevels[k];
				} 
			}
			if (resolutionLevels!=null){
				this.resolutionLevels = ParameterTools.setParameterInt(resolutionLevels, zSize,minResolutionLevels);
			} else {
				this.resolutionLevels = this.WTLevels;
			}
			this.desiredDistortion = ParameterTools.setParameterFloat(desiredDistortion,zSize,TERDefaultValues.desiredDistortion);
			this.distortionMeasure = ParameterTools.setParameterInt(distortionMeasure, zSize, TERDefaultValues.distortionMeasure);
			
			
			this.DCStop = ParameterTools.setParameterMatrix(DCStop,this.blocksPerSegment,TERDefaultValues.DCStop);
			this.bitPlaneStop = ParameterTools.setParameterMatrix(bitPlaneStop,this.blocksPerSegment,TERDefaultValues.bitPlaneStop);
			this.useFill = ParameterTools.setParameterMatrix(useFill,this.blocksPerSegment,TERDefaultValues.useFill);
			this.optDCSelect = ParameterTools.setParameterMatrix(optDCSelect,this.blocksPerSegment,TERDefaultValues.optDCSelect);
			this.optACSelect = ParameterTools.setParameterMatrix(optACSelect,this.blocksPerSegment,TERDefaultValues.optACSelect);
			
			this.codeWordLength = ParameterTools.setParameterMatrix(codeWordLength,this.blocksPerSegment,TERDefaultValues.codeWordLength);
			
			this.gaggleDCSize = 
				TER.TERcoder.SegmentCoder.SegmentConstruct.setGaggleSize(gaggleDCSize, TERDefaultValues.gaggleDCSize, 
						this.blocksPerSegment, this.WTLevels, xSize, ySize, zSize);
			this.gaggleACSize = 
				TER.TERcoder.SegmentCoder.SegmentConstruct.setGaggleSize(gaggleACSize, TERDefaultValues.gaggleACSize, 
						this.blocksPerSegment, this.WTLevels, xSize, ySize, zSize);

			if (progressionOrder >= 1 ){
				this.idDC = this.gaggleDCSize;
				this.idAC = this.gaggleACSize;
			} else {
				this.idDC = ParameterTools.setParameterMatrix(idDC,this.blocksPerSegment,TERDefaultValues.idDC);
				this.idAC = ParameterTools.setParameterMatrix(idAC,this.blocksPerSegment,TERDefaultValues.idAC);
				
			}


			this.entropyAC = ParameterTools.setParameterMatrix(entropyAC,this.blocksPerSegment, TERDefaultValues.entropyAC);

			

			this.stageStop = ParameterTools.setParameterMatrix(stageStop,this.blocksPerSegment,maxResolutionLevels+1);
			// temporal filling of values
			
			this.part2Flag = ParameterTools.setParameterMatrix(part2Flag,this.blocksPerSegment,TERDefaultValues.part2Flag);
			this.part3Flag = ParameterTools.setParameterMatrix(part3Flag,this.blocksPerSegment,TERDefaultValues.part3Flag);
			this.part4Flag = ParameterTools.setParameterMatrix(part4Flag,this.blocksPerSegment,TERDefaultValues.part4Flag);
			
			//this.progressionOrder =  setParameterMatrix(null,TERDefaultValues.progressionOrder);
			this.truncationPoints =  ParameterTools.setParameterMatrix(truncationPoints,this.blocksPerSegment,TERDefaultValues.truncationPoints);
			this.adjustHeaderParameters =  ParameterTools.setParameterMatrix(adjustHeaderParameters,this.blocksPerSegment,TERDefaultValues.adjustHeaderParameters);		
		}
		
		// In case that idDC and idAC are not multiple or gaggleDCSize and gaggleACSize,
		// then the compression parameters would not be coherent and a warning to 
		// the user will be given
		for(int z=0;z<zSize;z++){
			for(int k=0; k<this.idDC[z].length ; k++){
				if( this.idDC[z][k]%this.gaggleDCSize[z][k]!=0 || this.idAC[z][k]%this.gaggleACSize[z][k]!=0 ){
					throw new ParameterException("The parameters idDC, idAC, gaggleDCSize and gaggleACSize seems not to be properly set.");
				}
			}
		}
		if (targetBytes!=null && bpppb!=null){
			throw new ParameterException("targetBytes and bps are not compatible parameters");		
		} else if (targetBytes!=null){
			this.targetBytes = targetBytes;
		} else if (bpppb!=null){
			this.targetBytes = ConversionTools.getTargetBytesFromBpppb(bpppb,zOriginalSize,yOriginalSize,xOriginalSize);
		} else {
			// Neither targetBytes nor bpppb has been specified
			// in some parts of the algorithm we count in bits
			this.targetBytes = new int[1];
			this.targetBytes[0] = Integer.MAX_VALUE/8;
			
		}
		
		this.numberOfLayers = numberOfLayers;
		this.layerCreationType = layerCreationType;
		this.layerBytes = layerBytes;
		this.layerSizeType = layerSizeType;
		
	}
	
	
	
	/**
	 * Runs the TER coder algorithm to compress the image.
	 * 
	 * @throws Exception when something goes wrong and compression must be stopped
	 */
	public void run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("TERcoder cannot run if parameters are not set.");
		}
		if (CVerbose[0]){
			showTimeMemory("IMAGE LOAD. STARTING COMPRESSION...");
		}

		if(CVerbose[1]){
			showArguments();
		}
		//Transpose Image
		boolean needTranspose = TransposeImage.needTranspose(this.transposeImg);
		if (needTranspose){
			TransposeImage ti = new TransposeImage(imageSamplesFloat);
			ti.setParameters(transposeImg);
			imageSamplesFloat = ti.run();
			this.xOriginalSize = imageSamplesFloat[0][0].length;
			this.yOriginalSize = imageSamplesFloat[0].length;
			ti = null;
			//System.gc();
			if (CVerbose[0]){
				showTimeMemory("IMAGE TRANSPOSE...");
			}
		}
	
        //LEVEL SHIFT
		if (LSType!=0){
			LevelShift ls = new LevelShift(imageSamplesFloat);
			ls.setParameters(LSType, TERDefaultValues.integerToBooleanComponents(LSComponents,zSize,true), LSSubsValues, pixelBitDepth);
			imageSamplesFloat = ls.run();
			LSSubsValues = ls.getSubsValues();
			//Free unused memory
			ls = null;
			//Show statistics
			if (CVerbose[0]){
				showTimeMemory("LEVEL SHIFT");
			}
		}
		
		//Image Extension
		boolean needExtension = ImageExtension.needImageExtension(imageExtensionType,WTLevels,xSize,ySize,zSize);
		if ( needExtension ){
			ImageExtension ie = new ImageExtension(imageSamplesFloat);
			ie.setParameters(imageExtensionType, WTLevels);
			imageSamplesFloat = ie.run();
			padRows = ie.getPadRows();
			//Free unused memory
			ie = null;
			//System.gc();
			if (CVerbose[0]){
				showTimeMemory("IMAGE EXTENSION...");
			}
		} 
	
		if (test3d == 0){
			//Forward Wavelet Transform
			ForwardWaveletTransform fwt = new ForwardWaveletTransform(imageSamplesFloat);
			fwt.setParameters(WTType,WTLevels,WTOrder);
			imageSamplesFloat = fwt.run();
			fwt = null;
			//System.gc();
			if (CVerbose[0]){
				showTimeMemory("FORWARD WAVELET TRANSFORM...");
			}
		} else if (test3d == 1){
		// 1D+2D non pyramidal Wavelet Transform
			int[] levels = new int[3];
			int[] types = new int[3];
			for(int k=0;k<2;k++){
				levels[k] = WTLevels[0];
				types[k] = WTType[0];
			}
			levels[2] = spectralWTLevels;
			types[2] = spectralWTType;
			ForwardNonPyramidal3D fwt = new ForwardNonPyramidal3D(imageSamplesFloat);
			fwt.setParameters(types,levels);
			imageSamplesFloat = fwt.run();
			fwt = null;
			//System.gc();
			if (CVerbose[0]){
				showTimeMemory("FORWARD WAVELET TRANSFORM...");
			}
		} else if (test3d == 2){
			//3D pyramidal Wavelet Transform
			int[] levels = new int[3];
			int[] types = new int[3];
			for(int k=0;k<2;k++){
				levels[k] = WTLevels[0];
				types[k] = WTType[0];
			}
			levels[2] = spectralWTLevels;
			types[2] = spectralWTType;
			ForwardPyramidal3D fwt = new ForwardPyramidal3D(imageSamplesFloat);
			fwt.setParameters(types,levels);
			imageSamplesFloat = fwt.run();
			fwt = null;
			//System.gc();
			if (CVerbose[0]){
				showTimeMemory("FORWARD WAVELET TRANSFORM...");
			}
		}
		//System.gc();
		
		// Weighting
		boolean needWeighting = ForwardWeighting.setWeightingNeed(customWtFlag,WTType,zSize);
		if (needWeighting){
			ForwardWeighting wg = new ForwardWeighting(imageSamplesFloat);
			wg.setParameters(customWtFlag,WTType,WTLevels,customWeight);
			imageSamplesFloat = wg.run();
			wg = null;
			//System.gc();
			if (CVerbose[0]){
				showTimeMemory("WEIGHTING...");
			}
		}

		// Coefficients Approximantion
		boolean needApproximation = CoefficientsApproximation.TERsetApproximationNeed(coefficientsApproximationTypes);
		if (needApproximation){
			CoefficientsApproximation ca = new CoefficientsApproximation(imageSamplesFloat);
			ca.setParameters(coefficientsApproximationTypes);
			imageSamplesFloat=ca.run();
			if (CVerbose[0]){
				showTimeMemory("COEFFICIENTS APPROXIMATION...");
			}
		}
		
		// Bit Plane Encoder
		TER.TERcoder.SegmentCoder.SegmentConstruct sc = new TER.TERcoder.SegmentCoder.SegmentConstruct();
		sc.setParameters(blocksPerSegment, WTLevels, xOriginalSize, yOriginalSize);
		int blockInSegment[][][] = sc.run();
		sc = null;
		//System.gc();
		
		BitStream sortingAC[][][][][] = new BitStream[zSize][][][][];
		BitStream initialCodedDC[][][] = new BitStream[zSize][][];
		BitStream codedBitDepthACBlock[][][] = new BitStream[zSize][][];
		BitStream refinementDC[][][][] = new BitStream[zSize][][][];
		BitStream recommendedRefinementAC[][][][][][] = null;
		BitStream terRefinementAC[][][][][] = null;
		if (progressionOrder>=1){
			terRefinementAC = new BitStream[zSize][][][][];
		} else {
			recommendedRefinementAC = new BitStream[zSize][][][][][];
		}
		
		int bitDepthDC[][] = new int[zSize][];
		int bitDepthAC[][] = new int[zSize][];
		int bitDepthACBlock[][][][] = new int[zSize][][][];
		
		for(int z=0;z<zSize;z++){
			int segmentsPerChannel = blocksPerSegment[z].length;
			
			SegmentCode2D sc2d = new SegmentCode2D(imageSamplesFloat[z]);
			sc2d.setParameters(WTLevels[z], WTType[z], resolutionLevels[z], customWtFlag[z], customWeight[z], 
					optDCSelect[z], optACSelect[z], 
					gaggleDCSize[z], gaggleACSize[z], idDC[z], idAC[z], DCStop[z], bitPlaneStop[z], stageStop[z],
					segByteLimit[z], desiredDistortion[z], distortionMeasure[z], entropyAC[z], progressionOrder);
			sortingAC[z] = new BitStream[segmentsPerChannel][][][];
			initialCodedDC[z] = new BitStream[segmentsPerChannel][];
			codedBitDepthACBlock[z] = new BitStream[segmentsPerChannel][];
			refinementDC[z] = new BitStream[segmentsPerChannel][][];
			if (progressionOrder>=1){
				terRefinementAC[z] = new BitStream[segmentsPerChannel][][][];
			} else {
				recommendedRefinementAC[z] = new BitStream[segmentsPerChannel][][][][];
			}
			bitDepthDC[z] = new int[segmentsPerChannel];
			bitDepthAC[z] = new int[segmentsPerChannel];
			bitDepthACBlock[z] = new int[segmentsPerChannel][][];
			for(int segment=0 ; segment < segmentsPerChannel ; segment++){
				
				sc2d.run(blockInSegment[z][segment], segment);
				
				initialCodedDC[z][segment] = sc2d.getInitialCodedDC();
				refinementDC[z][segment] = sc2d.getRefinementDC();
				
				codedBitDepthACBlock[z][segment] = sc2d.getCodedBitDepthACBlock();
				
				sortingAC[z][segment] = sc2d.getSortingAC();
				if (progressionOrder>=1){
					terRefinementAC[z][segment] = sc2d.getTerRefinementAC();
				} else {
					recommendedRefinementAC[z][segment] = sc2d.getRecommendedRefinementAC();
				}
				
				bitDepthDC[z][segment] =  sc2d.getBitDepthDC();
				bitDepthAC[z][segment] =  sc2d.getBitDepthAC();
				bitDepthACBlock[z][segment] = sc2d.getBitDepthACBlock();
			}
			
			DCStop[z] = sc2d.getDCStop();
			sc2d = null;
			
			if (CVerbose[0]){
				showTimeMemory("BIT PLANE ENCODER : Band "+z+"...");
			}
			//Idea to reduce memory requirements.
			//imageSamplesFloat[z] = null;
			//System.gc();
		}
		
		this.imageSamplesFloat = null;
		//System.gc();
		
		for (int k=0;k<targetBytes.length;k++){
			String outTmp = null;
			WriteFile wf = new WriteFile();
			if (targetBytes.length>1){
				
				float bpppb = ConversionTools.getBpppbFromTargetBytes(zOriginalSize,yOriginalSize,xOriginalSize,targetBytes[k]);
				outTmp = outputFile+"-bpppb_"+getBpppbDecimals(bpppb);
				
			} else {
				outTmp = this.outputFile;
			}
			wf.setParameters(outTmp, outputFileType, progressionOrder,
					cropType,removeBand,zOriginalSize,
					zSize,yOriginalSize,		
					initialCodedDC, codedBitDepthACBlock, 
					refinementDC, sortingAC, recommendedRefinementAC, terRefinementAC,
					bitDepthDC, bitDepthAC, part2Flag, part3Flag, part4Flag, 
					imageExtensionType, padRows, transposeImg,LSType, LSComponents, LSSubsValues, 
					segByteLimit, DCStop, bitPlaneStop, stageStop, useFill, 
					blocksPerSegment, optDCSelect, optACSelect,
					WTType, signedPixels, pixelBitDepth, xOriginalSize,
					codeWordLength, customWtFlag, customWeight,
					truncationPoints, adjustHeaderParameters, resolutionLevels,
					WTLevels, WTOrder,
					gaggleDCSize,gaggleACSize,idDC,idAC,entropyAC,
					targetBytes[k], numberOfLayers, layerCreationType,
					layerSizeType,layerBytes,headerMinMax, minValue, maxValue);
			wf.run();
			
			wf = null;
			if (targetBytes.length>1){
				System.gc();
			}
			if (CVerbose[0]){
				showTimeMemory("INTERLEAVING AND SAVING FILE...");
			}
		}

	}
	
	private float getBpppbDecimals(float bpppb){
		return Math.round(bpppb*1000)/(float)1000;
	}
	//////////////////////////////////////////////
	//FUNCTIONS TO SHOW SOME VERBOSE INFORMATION//
	//////////////////////////////////////////////

	/**
	 * Show some time and memory usage statisticals.
	 *
	 * @param stage string that will be displayed
	 */
	void showTimeMemory(String stage){
		if(CVerbose[0]){
			long actualTime = System.currentTimeMillis();
			if(initTime == 0) initTime = actualTime;
			if(initStageTime == 0) initStageTime = actualTime;

			//print times are not considered
			long totalMemory = Runtime.getRuntime().totalMemory() / 1048576;
			long usedMemory =  (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
			//((float) initTime - (float) actualTime)
			String durationStage = Float.toString((actualTime - initStageTime) / 1000F) + "000";
			durationStage = durationStage.substring(0, durationStage.lastIndexOf(".") + 4);
			String duration = Float.toString((actualTime - initTime) / 1000F) + "000";
			duration = duration.substring(0, duration.lastIndexOf(".") + 4);

			System.out.println("STAGE: " + stage);
			System.out.println("  Memory (USED/TOTAL): " + usedMemory + "/" + totalMemory + " MB");
			System.out.println("  Time (USED/TOTAL)  : " + durationStage + "/" + duration + " secs");

			initStageTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * Show parameters used to encode the image
	 *
	 */
	public void showArguments(){
		
		if(imageExtensionType != null){
			System.out.println("imageExtensionType : ");
			ParameterTools.listIntegerArray(imageExtensionType);
		}
		if(padRows != null){
			System.out.println("PadRows : ");
			ParameterTools.listIntegerArray(padRows);
		}
		if(WTType != null){
			System.out.println("WTType:");
			ParameterTools.listIntegerArray(WTType);
		}
		if(WTLevels != null){
			System.out.println("WTLevels:");
			ParameterTools.listIntegerArray(WTLevels);
		}
		if(WTOrder != null){
			System.out.println("WTOrder:");
			ParameterTools.listIntegerArray(WTOrder);
		}
		if( customWtFlag != null){
			System.out.println("CustomWtFlag:");
			ParameterTools.listIntegerArray(customWtFlag);
		}
		if( customWeight != null){
			System.out.println("CustomWeight:");
			for(int i=0 ; i<customWeight.length ; i++){
				if(customWeight[i]!=null){
					ParameterTools.listFloatArray(customWeight[i]);
				}
			}
		}
		if(part2Flag!= null){
			System.out.println("Part2Flag:");
			ParameterTools.listIntegerMatrix(part2Flag);
		}
		if(part2Flag!= null){
			System.out.println("Part3Flag:");
			ParameterTools.listIntegerMatrix(part3Flag);
		}
		if(part4Flag!= null){
			System.out.println("Part4Flag:");
			ParameterTools.listIntegerMatrix(part4Flag);
		}
		if(segByteLimit != null){
			System.out.println("SegByteLimit:");
			ParameterTools.listIntegerMatrix(segByteLimit);
		}
		if(DCStop != null){
			System.out.println("DCStop:");
			ParameterTools.listIntegerMatrix(DCStop);
		}
		
		if( bitPlaneStop != null){
			System.out.println("BitPlaneStop:");
			ParameterTools.listIntegerMatrix(bitPlaneStop);
		}
		if(stageStop != null){
			System.out.println("StageStop:");
			ParameterTools.listIntegerMatrix(stageStop);
		}
		if(useFill != null){
			System.out.println("UseFill :");
			ParameterTools.listIntegerMatrix(useFill);
		}
		if( blocksPerSegment != null){
			System.out.println("BlocksPerSegment:");
			ParameterTools.listIntegerMatrix(blocksPerSegment);
		}
		if( optDCSelect != null){
			System.out.println("OptDCSelect:");
			ParameterTools.listIntegerMatrix(optDCSelect);
		}
		if( optACSelect != null){
			System.out.println("OptACSelect:");
			ParameterTools.listIntegerMatrix(optACSelect);
		} 
		if( signedPixels != null){
			System.out.println("SignedPixels:");
			ParameterTools.listIntegerArray(signedPixels);
		} 
		if( transposeImg != null){
			System.out.println("TransposeImg:");
			ParameterTools.listIntegerArray(transposeImg);
		}
		if( codeWordLength != null){
			System.out.println("CodeWordLength:");
			ParameterTools.listIntegerMatrix(codeWordLength);
		}
		if( pixelBitDepth != null){
			System.out.println("PixelBitDepth:");
			ParameterTools.listIntegerArray(pixelBitDepth);
		}
		if( gaggleDCSize != null){
			System.out.println("gaggleDCSize:");
			ParameterTools.listIntegerMatrix(gaggleDCSize);
		}
		if( gaggleACSize != null){
			System.out.println("gaggleACSize:");
			ParameterTools.listIntegerMatrix(gaggleACSize);
		}
		if( idDC != null){
			System.out.println("idDC:");
			ParameterTools.listIntegerMatrix(idDC);
		}
		
		if( idAC != null){
			System.out.println("idAC:");
			ParameterTools.listIntegerMatrix(idAC);
		}
		
		if( desiredDistortion != null){
			System.out.println("desiredDistortion:");
			ParameterTools.listFloatArray(desiredDistortion);
		}
		
		if( distortionMeasure != null){
			System.out.println("distortionMeasure:");
			ParameterTools.listIntegerArray(distortionMeasure);
		}
		
		if( entropyAC != null){
			System.out.println("entropyAC:");
			ParameterTools.listIntegerMatrix(entropyAC);
		}
		
		if( resolutionLevels != null){
			System.out.println("resolutionLevels:");
			ParameterTools.listIntegerArray(resolutionLevels);
		}
		if( this.compressionFactor != null){
			System.out.println("compressionRatio:");
			ParameterTools.listFloatMatrix(compressionFactor);
		}
	}
}
