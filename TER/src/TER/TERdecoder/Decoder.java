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
package TER.TERdecoder;

import GiciException.*;
import GiciImageExtension.*;
import GiciTransform.*;



import TER.TERdecoder.BPEDecoder.*;
import TER.TERdecoder.ReadFile.*;
import TER.TERDefaultValues;
import TER.TERdecoder.Weighting.*;

import TER.TERCommon.ParameterTools;
import TER.TERcoder.Weighting.*;



/**
 * Main class of TER decoder application. It receives all parameters, checks its validity and runs TER decoder.
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class Decoder{
	boolean rangeRecoveredPixels = false;
	int test3d = 0;
	int spectralWTLevels = 0;
	int spectralWTType = 0;
	
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;

	/**
	 * Recovered image samples (index meaning [z][y][x]).
	 * <p>
	 * All values allowed.
	 */
	float[][][] imageSamplesFloat = null;
	
	/**
	 * Number of image components.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int zSize;
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
	 * Input file name of encoded bitstream.
	 * <p>
	 * Valid file name (with path).
	 */
	String inputFile;
	
	/**
	 * 
	 */
	int inputFileType;
	
	/**
	 * Characteristics of the output image to be saved
	 */
	int[] imageGeometry = null;
	
	
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
	 * Definition in {@link GiciImageExtension.ImageExtension#padRows}
	 */
	int[] padRows = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#segByteLimit}
	 */
	int[] segByteLimit = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 */
	int[] DCStop = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitPlaneStop}
	 */
	int[] bitPlaneStop = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#stageStop}
	 */
	int[] stageStop = null;

	/**
	 * Definition in {@link TER.TERcoder.WriteFile.RecommendedOrder#useFill}
	 */
	int[] useFill = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 */
	int[][] blocksPerSegment = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optDCSelect}
	 */
	int[] optDCSelect = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#optACSelect}
	 */
	int[] optACSelect = null;	 
	
	/**
	 * Definition in {@link TER.TERcoder.Coder#signedPixels}
	 */
	int[] signedPixels = null;
	
	/**
	 * Definition in {@link GiciTransform.TransposeImage#transposeImg}
	 */
	int[] transposeImg = null;
	
	/**
	 * Definition in {@link TER.TERcoder.WriteFile.RecommendedOrder#codeWordLength}
	 */
	int[] codeWordLength = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#gaggleDCSize}
	 */
	//int[] gaggleDCSize = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#gaggleACSize}
	 */
	//int[] gaggleACSize = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#idDC}
	 */
	int[] idDC  = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#idAC}
	 */
	int[] idAC  = null;	
	
	/**
	 *  Definition in {@link TER.TERcoder.Coder#pixelBitDepth}
	 */
	int[] pixelBitDepth = null;

	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.EncodeGaggleAC#entropyAC}
	 */
	int[] entropyAC = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#resolutionLevels}
	 */
	int[] resolutionLevels = null;
	
	/**
	 * Definition in {@link GiciImageExtension.ImageDeExtension#imageWidth}
	 */
	int[] imageWidth = null;
	
	
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
	 * Show some information about decoding process. Each value is boolean (false means NO show, true means show).
	 * <p>
	 * Each value represents the following:<br>
	 *  <ul>
	 *    <li> 0- Information about time and used memory for each compression stage
	 *    <li> 1- TER parameters
	 *    <li> 1- TER decoder messages
	 *  </ul>
	 */
	boolean[] CVerbose = null;
	
	/**
	 * Definition in {@link TER.TERdecoder.SegmentDecoder.SegmentDecode2D#gammaValue}
	 */
	float[] gammaValue = null;
	
	boolean minusHalf;
	
	/**
	 * Definition in {@link TER.TERdecoder.SegmentDecoder.InitialDecoding#completionMode}
	 */
	int[] completionMode = null;
	
	/**
	 *  Definition in {@link TER.TERcoder.Coder#compressionFactor}
	 */
	float[] compressionFactor = null;
	
	float[] desiredDistortion;
	
	int[] distortionMeasure = null ;
	
	/**
	 * Definition in {@link GiciTransform.LevelShift#LSType}
	 */
	int LSType;

	/**
	 * Definition in {@link GiciTransform.LevelShift#LSComponents}
	 */
	boolean[] LSComponents = null;

	/**
	 * Definition in {@link GiciTransform.LevelShift#LSSubsValues}
	 */
	int[] LSSubsValues = null;
	
	
	int addType;
	boolean removedBand[] = null;
	
	boolean headerMinMax = false;
	float minValue, maxValue;
	
	/**
	 * Constructor of TERdecoder. It receives one or more encoded files.
	 * 
	 * @param inputFile this array of string contains the name (path included) of the files to be decoded
	 */
	public Decoder(String inputFile){
		
		this.inputFile = inputFile;	

		
	}
	
	/**
	 * Set the parameters used to perform the TER decoder and check validity. If a parameter is not initialized, this class initalizes it with default values.
	 * 
	 * @param imageGeometry definition in {@link #imageGeometry}
	 * @param segByteLimit definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#segByteLimit}
	 * @param DCStop definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 * @param bitPlaneStop definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitPlaneStop}
	 * @param stageStop definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#stageStop}
	 * @param pixelBitDepth definition in {@link TER.TERcoder.Coder#pixelBitDepth}
	 * @param compressionFactor definition in {@link TER.TERcoder.Coder#compressionFactor}
	 * @param gammaValue definition in {@link TER.TERdecoder.SegmentDecoder.SegmentDecode2D#gammaValue}
	 * @param minusHalf definition in {@link #minusHalf}
	 * @param completionMode definition in {@link TER.TERdecoder.SegmentDecoder.InitialDecoding#completionMode}
	 * @param CVerbose definition in {@link #CVerbose}
	 * @param test3d {@link #test3d}
	 * @param spectralWTLevels {@link #spectralWTLevels}
	 * @param spectralWTType {@link #spectralWTType}
	 * 
	 * @throws ParameterException when an invalid or unrecognized exception is detected it will be thrown
	 */
	public void setParameters(
			int[] imageGeometry,
			int[] segByteLimit,
			int[] DCStop,
			int[] bitPlaneStop,
			int[] stageStop,
			int[] pixelBitDepth,
			float[] compressionFactor,
			float[] gammaValue, boolean minusHalf,
			int[] completionMode,
			boolean[] CVerbose,
			int test3d,	int spectralWTLevels, int spectralWTType,
			boolean rangeRecoveredPixels
	) throws ParameterException{
		
		this.test3d = test3d;
		this.spectralWTLevels = spectralWTLevels;
		this.spectralWTType = spectralWTType;
		this.rangeRecoveredPixels = rangeRecoveredPixels;
		
		if (inputFile.endsWith(".rec")){
			this.inputFileType = 0;
			this.zSize = 1;
			this.zOriginalSize = 1;
		} else if (inputFile.endsWith(".ter")){
			this.inputFileType = 1;
		} else {
			//this.inputFileType = 1;
			throw new ParameterException("Unkown file type for decoding.");
		}
		
		////////////// PARAMETERS REQUIRED TO DECODE THE RECOMMENDATION /////////
		if (CVerbose!=null){
			this.CVerbose = CVerbose;
		} else {
			this.CVerbose = TERDefaultValues.CVerbose;
		}
		
		if(imageGeometry!=null){
			zOriginalSize = imageGeometry[0];
			this.imageGeometry = imageGeometry;
		} else {
			zOriginalSize = 1;
			zSize = 1;
		}
		
		if (segByteLimit!=null){
			this.segByteLimit = segByteLimit;
		}  
		
		this.gammaValue = gammaValue;
		
		this.minusHalf = minusHalf;
		
		if( !ParameterTools.verifyParameter(completionMode, zOriginalSize, 0 , Integer.MAX_VALUE) ){
			throw new ParameterException("Wrong parameters for completionMode.");
		}
		this.completionMode =completionMode;

		parametersSet = true;
	}

	
	
	
	/**
	 * Runs the TER decoder algorithm to compress the image.
	 * 
	 * @return the recovered image
	 * 
	 * @throws Exception  when something goes wrong and deconding must be stopped
	 */
	public float[][][] run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("TERDecoder cannot run if parameters are not set.");
		}
		
		if (CVerbose[0]){
			showTimeMemory("STARTING DECODING...");
		}
		
		int xSizes[] = null;
		
		ReadBufferedStream encodedStream = new ReadBufferedStream(this.inputFile);
		
		if (this.inputFileType == 0) {	
			zSize = 1;
			gammaValue = ParameterTools.setParameterFloat(gammaValue,zSize,TERDefaultValues.gammaValue);				
			completionMode = ParameterTools.setParameterInt(completionMode,zSize,TERDefaultValues.completionMode);
			
			RecommendedDecoding rec = new RecommendedDecoding();
			rec.setParameters(imageGeometry, segByteLimit, gammaValue, completionMode, CVerbose, minusHalf);
			imageSamplesFloat = new float[zSize][][];
			imageSamplesFloat[0] = rec.run(encodedStream);
			encodedStream = null;
			
			imageExtensionType = rec.getImageExtensionType(); 
			padRows = rec.getPadRows();
			
			WTType = rec.getWTType();
			WTLevels = rec.getWTLevels();
			WTOrder = rec.getWTOrder();
			resolutionLevels = rec.getWTLevels();
			codeWordLength = rec.getCodeWordLength();
			signedPixels = rec.getSignedPixels();
			pixelBitDepth = rec.getPixelBitDepth();
			transposeImg = rec.getTransposeImg();
			customWtFlag = rec.getCustomWtFlag();
			customWeight = rec.getCustomWeight();
			
			xSizes = rec.getXSize();
			imageWidth = rec.getImageWidth();
			
			rec = null;
		} else if (this.inputFileType ==1){
			
			ReadTERHeader th = new ReadTERHeader();
			th.setParameters(encodedStream);
			th.run();
			
			this.zOriginalSize = th.getZOriginalSize();
			zSize = th.getZSize();
			this.addType = th.getAddType();
			this.removedBand = th.getRemovedBand();
			
			
			this.yOriginalSize = th.getYOriginalSize();
			this.xOriginalSize = th.getXOriginalSize();
			int progressionOrder = th.getProgressionOrder(); 
			this.pixelBitDepth = th.getPixelBitDepth();
			this.signedPixels = th.getSignedPixels();
			this.WTLevels = th.getWTLevels();
			this.WTType = th.getWTType();
			this.WTOrder = th.getWTOrder();

			gammaValue = ParameterTools.setParameterFloat(gammaValue,zSize,TERDefaultValues.gammaValue);				
			completionMode = ParameterTools.setParameterInt(completionMode,zSize,TERDefaultValues.completionMode);
			
			xSizes = th.getXSizes();
			imageWidth = th.getImageWidth();
			
			if (progressionOrder == 0){
				blocksPerSegment = th.getBlocksPerSegment();
				int[][] gaggleSizeDC = th.getGaggleSizeDC();
				int[][] gaggleSizeAC = th.getGaggleSizeAC();
				int[][] entropyAC = th.getEntropyAC();

				ProgressionOrder0  po0 =new ProgressionOrder0(zSize, yOriginalSize, xOriginalSize);
				po0.setParameters(segByteLimit, gammaValue, completionMode, CVerbose,
						WTType, WTLevels, WTOrder,stageStop, minusHalf,
						blocksPerSegment,gaggleSizeDC, gaggleSizeAC, entropyAC);
				imageSamplesFloat = po0.run(encodedStream);
				
				encodedStream = null;
				
				imageExtensionType = po0.getImageExtensionType(); 
				padRows = po0.getPadRows();
				
				WTType = po0.getWTType();
				WTLevels = po0.getWTLevels();
				WTOrder = po0.getWTOrder();
				resolutionLevels = po0.getWTLevels();
				codeWordLength = po0.getCodeWordLength();
				signedPixels = po0.getSignedPixels();
				pixelBitDepth = po0.getPixelBitDepth();
				transposeImg = po0.getTransposeImg();
				customWtFlag = po0.getCustomWtFlag();
				customWeight = po0.getCustomWeight();
				
				xSizes = po0.getXSize();
				imageWidth = po0.getImageWidth();
				
				po0 = null;
				
				
			} else {
				imageExtensionType = th.getImageExtensionType();
				padRows = th.getPadRows();
				transposeImg = th.getTransposeImage();
				LSComponents = th.getLSComponents();
				LSSubsValues = th.getLSSubsValues();
				LSType = th.getLSType();
				headerMinMax = th.getHeaderMinMax(); 
				minValue = th.getMinValue();
				maxValue = th.getMaxValue();
				
				customWtFlag = th.getCustomWtFlag();
				customWeight = th.getCustomWeight();
				
				blocksPerSegment = th.getBlocksPerSegment();
				int[][] bitDepthDC = th.getBitDepthDC();
				int[][] bitDepthAC = th.getBitDepthAC();
				int[][] gaggleSizeDC = th.getGaggleSizeDC();
				int[][] gaggleSizeAC = th.getGaggleSizeAC();
				int[][] entropyAC = th.getEntropyAC();
				int layers = th.getLayers();
				
				
				
				ReadFile rf = new ReadFile(encodedStream);
				rf.setParameters(zSize, progressionOrder, layers, 
						WTLevels, blocksPerSegment, gaggleSizeDC, gaggleSizeAC);
				rf.run();
				byte byteStream[][][][][] = rf.getPackets(); 
				
				this.resolutionLevels = new int[zSize];
				for(int z=0;z<zSize;z++){
					resolutionLevels[z] = WTLevels[z] + 1 ;
				}
				
				DecodeAvailable decode = new DecodeAvailable(byteStream);
				decode.setParameters(zSize, yOriginalSize,xOriginalSize,
						imageExtensionType, WTType, WTLevels,resolutionLevels,
						customWtFlag,customWeight,
						blocksPerSegment,gaggleSizeDC,gaggleSizeAC, entropyAC,
						bitDepthDC, bitDepthAC,
						completionMode, gammaValue);
				imageSamplesFloat = decode.run();
				
				
			} 
			th = null;
		}
		encodedStream = null;
		
		System.gc();
		if (CVerbose[0]){
			showTimeMemory("BIT PLANE DECODER...");
		}
		
		//Weighting
		boolean needWeighting = ForwardWeighting.setWeightingNeed(customWtFlag,WTType,zSize);
		if (needWeighting){
			InverseWeighting iwg = new InverseWeighting(imageSamplesFloat);
			iwg.setParameters(customWtFlag,WTType,WTLevels,customWeight);
			imageSamplesFloat = iwg.run();
			iwg = null;
			System.gc();
			if (CVerbose[0]){
				showTimeMemory("WEIGHTING...");
			}
		}
		
		if (test3d==0){
//			Inverse Wavelet transform
			InverseWaveletTransform iwt = new InverseWaveletTransform(imageSamplesFloat);
			iwt.setParameters(WTType,WTLevels,WTOrder);
			imageSamplesFloat = iwt.run();
			iwt = null;
			System.gc();
			if (CVerbose[0]){
				showTimeMemory("INVERSE WAVELET TRANSFORM...");
			}
		} else if (test3d==1){
			//1D+2D non pyramidal Wavelet Transform
			int[] levels = new int[3];
			int[] types = new int[3];
			for(int k=0;k<2;k++){
				levels[k] = WTLevels[0];
				types[k] = WTType[0];
			}
			levels[2] = spectralWTLevels;
			types[2] = spectralWTType;
			InverseNonPyramidal3D iwt = new InverseNonPyramidal3D(imageSamplesFloat);
			iwt.setParameters(types,levels);
			imageSamplesFloat = iwt.run();
			iwt = null;
			//System.gc();
			if (CVerbose[0]){
				showTimeMemory("FORWARD WAVELET TRANSFORM...");
			}
		} else if (test3d==2){
			//3D pyramidal Wavelet Transform
			int[] levels = new int[3];
			int[] types = new int[3];
			for(int k=0;k<2;k++){
				levels[k] = WTLevels[0];
				types[k] = WTType[0];
			}
			levels[2] = spectralWTLevels;
			types[2] = spectralWTType;
			InversePyramidal3D iwt = new InversePyramidal3D(imageSamplesFloat);
			iwt.setParameters(types,levels);
			imageSamplesFloat = iwt.run();
			iwt = null;
			//System.gc();
			if (CVerbose[0]){
				showTimeMemory("FORWARD WAVELET TRANSFORM...");
			}
		}
		
		boolean threeLevels = true;
		for(int k=0;k<WTLevels.length;k++){
			if(WTLevels[k]!=3){
				threeLevels= false;
			}
		}
		if ( !threeLevels && padRows==null ){
			if (this.imageGeometry!=null){
				padRows = ImageDeExtension.computePadRows(zSize,imageGeometry[1], imageExtensionType, WTLevels);
			} else {//the user does not know the original size
				padRows = ImageDeExtension.computePadRows(zSize,imageSamplesFloat[0].length, imageExtensionType, WTLevels);
			}
		}
		boolean needDeExtension = ImageDeExtension.needDeExtension(padRows,imageWidth,xSizes);
		//Image DeExtension
		if (needDeExtension){
			ImageDeExtension ide = new ImageDeExtension(imageSamplesFloat);
			ide.setParameters(imageWidth,this.padRows);
			imageSamplesFloat = ide.run();
			//Free unused memory
			ide = null;
			System.gc();
			if (CVerbose[0]){
				showTimeMemory("IMAGE EXTENSION...");
			}
		}
		
		//Transpose Image
		boolean needTranspose = TransposeImage.needTranspose(this.transposeImg);
		if (needTranspose){
			TransposeImage ti = new TransposeImage(imageSamplesFloat);
			ti.setParameters(transposeImg);
			imageSamplesFloat = ti.run();
			ti = null;
			System.gc();
			if (CVerbose[0]){
				showTimeMemory("IMAGE TRANSPOSE...");
			}
		}
		
		//Level Unshift
		if (LSType!=0){
			LevelUnshift ls = new LevelUnshift(imageSamplesFloat);
			ls.setParameters(LSType, LSComponents, LSSubsValues, pixelBitDepth);
			imageSamplesFloat = ls.run();
			//Free unused memory
			ls = null;
			//Show statistics
			if (CVerbose[0]){
				showTimeMemory("LEVEL UNSHIFT");
			}
		}
		
		if (rangeRecoveredPixels){
			
			for(int z=0; z<imageSamplesFloat.length ; z++){
				int ySize = imageSamplesFloat[z].length;
				int xSize = imageSamplesFloat[z][0].length;
				
				if (!headerMinMax){
					maxValue = (int) (1<<this.pixelBitDepth[z]) - 1;
					minValue = 0;
					
					if (signedPixels[z]==1){
						maxValue = (int) (1<<(this.pixelBitDepth[z]-1)) - 1;
						minValue = - (int) (1<<(this.pixelBitDepth[z]-1));
					} 
					
				}
				
				for(int y=0; y<ySize ; y++){
					for(int x=0; x<xSize ; x++){
						
						if (imageSamplesFloat[z][y][x] > maxValue){
							imageSamplesFloat[z][y][x] = maxValue;
						} else if (imageSamplesFloat[z][y][x] < minValue ){
							imageSamplesFloat[z][y][x] = minValue;
						}
						imageSamplesFloat[z][y][x]=Math.round(imageSamplesFloat[z][y][x]);
					}
				}
			}
			
		}
		
		if (CVerbose[1]){
			 showArguments();
		}
		
		
		AddChannels ac = new AddChannels(imageSamplesFloat,zOriginalSize);
		ac.setParameters(addType,removedBand);
		imageSamplesFloat = ac.run();
		
		if(imageGeometry==null){
			imageGeometry = new int[6];
			imageGeometry[0] = imageSamplesFloat.length;
			imageGeometry[1] = imageSamplesFloat[0].length;
			imageGeometry[2] = imageSamplesFloat[0][0].length;
			int sampleType = 0;
			for(int k=0;k<pixelBitDepth.length;k++){
				if (sampleType<pixelBitDepth[k]){
					sampleType = pixelBitDepth[k];
				}
			}
			if(sampleType%8==0){
				sampleType = sampleType/8;
			} else {
				sampleType = (sampleType/8) + 1;
			}
			imageGeometry[3] = sampleType; 
			imageGeometry[4] = 0;	
		}
		return imageSamplesFloat;

		
	}

	////////////////////////////////
	/////////GET FUNCTION //////////
	////////////////////////////////
	public int[] getImageGeometry(){
		return imageGeometry;
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
		if(segByteLimit != null){
			System.out.println("SegByteLimit:");
			ParameterTools.listIntegerArray(segByteLimit);
		}
		if(DCStop != null){
			System.out.println("DCStop:");
			ParameterTools.listIntegerArray(DCStop);
		}
		
		if( bitPlaneStop != null){
			System.out.println("BitPlaneStop:");
			ParameterTools.listIntegerArray(bitPlaneStop);
		}
		if(stageStop != null){
			System.out.println("StageStop:");
			ParameterTools.listIntegerArray(stageStop);
		}
		if(useFill != null){
			System.out.println("UseFill :");
			ParameterTools.listIntegerArray(useFill);
		}
		if( blocksPerSegment != null){
			//System.out.println("BlocksPerSegment:");
			//listIntegerArray(blocksPerSegment);
		}
		if( optDCSelect != null){
			System.out.println("OptDCSelect:");
			ParameterTools.listIntegerArray(optDCSelect);
		}
		if( optACSelect != null){
			System.out.println("OptACSelect:");
			ParameterTools.listIntegerArray(optACSelect);
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
			ParameterTools.listIntegerArray(codeWordLength);
		}
		if( pixelBitDepth != null){
			System.out.println("PixelBitDepth:");
			ParameterTools.listIntegerArray(pixelBitDepth);
		}
//		if( gaggleDCSize != null){
//			System.out.println("gaggleDCSize:");
//			ParameterTools.listIntegerArray(gaggleDCSize);
//		}
//		if( gaggleACSize != null){
//			System.out.println("gaggleACSize:");
//			ParameterTools.listIntegerArray(gaggleACSize);
//		}
		if( idDC != null){
			System.out.println("idDC:");
			ParameterTools.listIntegerArray(idDC);
		}
		
		if( idAC != null){
			System.out.println("idAC:");
			ParameterTools.listIntegerArray(idAC);
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
			ParameterTools.listIntegerArray(entropyAC);
		}
		
		if( resolutionLevels != null){
			System.out.println("resolutionLevels:");
			ParameterTools.listIntegerArray(resolutionLevels);
		}
		if( this.compressionFactor != null){
			System.out.println("compressionRatio:");
			ParameterTools.listFloatArray(compressionFactor);
		}
	}

}
