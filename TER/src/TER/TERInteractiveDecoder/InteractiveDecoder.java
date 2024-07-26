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
package TER.TERInteractiveDecoder;

import java.io.EOFException;

import GiciException.ParameterException;
import GiciImageExtension.ImageDeExtension;
import GiciTransform.InverseWaveletTransform;
import GiciTransform.LevelUnshift;
import GiciTransform.TransposeImage;
import TER.TERDefaultValues;
import TER.TERCommon.ParameterTools;
import TER.TERInteractiveDecoder.ReadStream.DecodingOptions;
import TER.TERInteractiveDecoder.ReadStream.RecommendedExtractor;
import TER.TERInteractiveDecoder.ReadStream.TERExtractor;
import TER.TERcoder.Weighting.ForwardWeighting;
import TER.TERdecoder.BPEDecoder.DecodeAvailable;
import TER.TERdecoder.BPEDecoder.ProgressionOrder0;
import TER.TERdecoder.BPEDecoder.RecommendedDecoding;
import TER.TERdecoder.ReadFile.ReadBufferedStream;
import TER.TERdecoder.Weighting.InverseWeighting;


/**
 * Main class of TER interactive decoder application. It receives all parameters, checks its validity and runs TER interactive decoder.
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 2.0
 */
public class InteractiveDecoder{
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;

	ReadBufferedStream encodedStream;	
	int inputFileType;
	float recoveredImage[][][] = null;
	
	int zSize;
	int zOriginalSize;
	int zSizeExtraction;
	int yOriginalSize;
	int ySizeExtraction;
	int xOriginalSize;
	int xSizeExtraction;
	
	int numLayers;
	int numLayersExtraction;
	
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 */
	int[] WTType = null;
	int WTTypeExtraction[] = null;
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 */
	int[] WTLevels = null;
	int WTLevelsExtraction[] = null;
	int resolutionLevels[] = null;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTOrder}
	 */
	int[] WTOrder = null;
	int WTOrderExtraction[] = null;
	/**
	 * Definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 */
	int[] customWtFlag = null; 
	int customWtFlagExtraction[] = null;
	/**
	 * Definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 */
	float[][] customWeight = null;
	float[][] customWeightExtraction = null;
	/**
	 * Definition in {@link GiciImageExtension.ImageExtension#padRows}
	 */
	int[] padRows = null;
	int[] padRowsExtraction = null;
	/**
	 * Definition in {@link TER.TERcoder.Coder#signedPixels}
	 */
	int[] signedPixels = null;
	int[] signedPixelsExtraction = null;
	/**
	 *  Definition in {@link TER.TERcoder.Coder#pixelBitDepth}
	 */
	int[] pixelBitDepth = null;
	int[] pixelBitDepthExtraction = null;
	/**
	 * Definition in {@link GiciImageExtension.ImageDeExtension#imageWidth}
	 */
	int[] imageWidth = null;
	int[] imageWidthExtraction = null;
	/**
	 * Characteristics of the output image to be saved
	 */
	int[] imageGeometry = null;
	int[] imageGeometryExtraction = null;
	/**
	 * Definition in {@link GiciTransform.TransposeImage#transposeImg}
	 */
	int[] transposeImg = null;
	int[] transposeImgExtraction = null;
	/**
	 * Definition in {@link GiciImageExtension.ImageExtension#imageExtensionType}
	 */
	int[] imageExtensionType = null;
	int[] imageExtensionTypeExtraction = null;
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
	
	int xSizes[] = null;
	int xSizesExtraction[] = null;

	int[][] blocksPerSegment = null;
	int blocksPerSegmentExtraction[][] = null;

	int[][] gaggleSizeDC = null;
	int gaggleSizeDCExtraction[][] = null;
	int[][] gaggleSizeAC = null;
	int gaggleSizeACExtraction[][] = null;
	int[][] entropyAC = null;
	int entropyACExtraction[][] = null;
	
	
	int[][] bitDepthDC = null;
	int[][] bitDepthDCExtraction = null;
	int[][] bitDepthAC = null;
	int[][] bitDepthACExtraction = null;
	
	int progressionOrder;

	
	long[][][][][] layerLocation = null;
	DecodingOptions dOptions = null;
	
	long initialStreamPosition;
	

	float gammaValue[] = null;
	int completionMode[] = null;
	boolean CVerbose[] = null;
	boolean minusHalf;
	
	int requiredLevels;
	int targetBytes;
	
	int[] channelList=null;
	int yInit;
	int yLength;
	int xInit;
	int xLength;
	int extractionType;
	
	/**
	 * Constructor of Interactive decoder
	 * 
	 * @param inputFile
	 */
	public InteractiveDecoder(String inputFile)  throws Exception{ 
		dOptions = new DecodingOptions(inputFile);
		dOptions.setParameters();
		dOptions.run();
		encodedStream = dOptions.getEncodedStream();
		inputFileType = dOptions.getInputFileType();
		
		
		if (inputFileType==1){
			initialStreamPosition = dOptions.getInitialStreamPosition();
			progressionOrder = dOptions.getTERHeader().getProgressionOrder();
			
			if (dOptions.getTERHeader().getAddType()!=0){
				throw new ParameterException("Interactive decoder does not allow yet addtype different from 0");
			}
			zSize = dOptions.getTERHeader().getZSize();
			zOriginalSize = dOptions.getTERHeader().getZOriginalSize();
			yOriginalSize = dOptions.getTERHeader().getYOriginalSize();
			xOriginalSize = dOptions.getTERHeader().getXOriginalSize();
			pixelBitDepth = dOptions.getTERHeader().getPixelBitDepth();
			signedPixels = dOptions.getTERHeader().getSignedPixels();
			WTLevels = dOptions.getTERHeader().getWTLevels();
			WTType = dOptions.getTERHeader().getWTType();
			WTOrder = dOptions.getTERHeader().getWTOrder();

			
			xSizes = dOptions.getTERHeader().getXSizes();
			imageWidth = dOptions.getTERHeader().getImageWidth();
			
			if (progressionOrder!=0){
				imageExtensionType = dOptions.getTERHeader().getImageExtensionType();
				padRows = dOptions.getTERHeader().getPadRows();
				transposeImg = dOptions.getTERHeader().getTransposeImage();
				LSComponents = dOptions.getTERHeader().getLSComponents();
				LSSubsValues = dOptions.getTERHeader().getLSSubsValues();
				LSType = dOptions.getTERHeader().getLSType();
				
				customWtFlag = dOptions.getTERHeader().getCustomWtFlag();
				customWeight = dOptions.getTERHeader().getCustomWeight();
				
				blocksPerSegment = dOptions.getTERHeader().getBlocksPerSegment();
				bitDepthDC = dOptions.getTERHeader().getBitDepthDC();
				bitDepthAC = dOptions.getTERHeader().getBitDepthAC();
				gaggleSizeDC = dOptions.getTERHeader().getGaggleSizeDC();
				gaggleSizeAC = dOptions.getTERHeader().getGaggleSizeAC();
				entropyAC = dOptions.getTERHeader().getEntropyAC();
				numLayers = dOptions.getTERHeader().getLayers();
				
				layerLocation = dOptions.getLayerLocation();
			}
			
		} else {
			initialStreamPosition = 0;
			zSize = 1;
		}
	}
	
	public void setParameters(int resolutionLevels, int numberOfLayers, int targetBytes,
			int[] channelList, int yInit, int yLength, int xInit, int xLength, int extractionType,
			float gammaValue[], int completionMode[], boolean CVerbose[], boolean minusHalf) throws ParameterException{
		
		if (resolutionLevels>0){
			this.requiredLevels = resolutionLevels -1;
		} else {
			throw new ParameterException("Resolution Levels must be greater than 0");
		}
		if (numberOfLayers>0){
			this.numLayersExtraction = numberOfLayers;
		} else {
			this.numLayersExtraction = 1;
		}
		if (targetBytes>0){
			this.targetBytes = targetBytes;
		} else {
			this.targetBytes = (int) encodedStream.getTotalBytes();
		}
		if (channelList!=null){
			if (channelList.length<=zSize){
				for(int z=0;z<channelList.length;z++){
					if (channelList[z]>=zSize){
						throw new ParameterException("You cannot demand this channel, it is not available in this image");
					}
				}
				this.channelList = channelList;
			} else {
				throw new ParameterException("You cannot decode more channels than the ones in the input image");
			}
		} else {
			this.channelList = new int[zSize];
			for(int z=0;z<zSize;z++){
				this.channelList[z] = z;
			}
		}
		
		if (yInit<0 || yInit>yOriginalSize){
			this.yInit = 0;
		} else {
			this.yInit = yInit;
		}		
		if (yLength<0 || yLength>yOriginalSize){
			this.yLength = yOriginalSize;
		} else{
			this.yLength = yLength;
		}
		
		
		if (xInit<0 || xInit>xOriginalSize){
			this.xInit = 0;
		} else {
			this.xInit = xInit;
		}
		if (xLength<0 || xLength>xOriginalSize){
			this.xLength = xOriginalSize;
		} else{
			this.xLength = xLength;
		}
		
		this.extractionType = extractionType;
		
		this.gammaValue = gammaValue;
		this.completionMode = completionMode;
		if (CVerbose!=null){
			this.CVerbose = CVerbose;
		} else {
			this.CVerbose = TERDefaultValues.CVerbose;
		}
		this.minusHalf = minusHalf;
		this.gammaValue = gammaValue;				
		this.completionMode = completionMode;
		
		parametersSet = true;
	}
	
	/**
	 * Runs the TER decoder interactive algorithm
	 * 
	 * @return the recovered image
	 * 
	 * @throws Exception  when something goes wrong and interactive decoding must be stopped
	 */
	public float[][][] run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("InteractiveDecoder cannot run if parameters are not set.");
		}
		
		recoveredImage = null;
		if (inputFileType == 0){
			encodedStream.seek(0);
			recommendedDecoding();
		} else if (inputFileType == 1){
			if (progressionOrder==0){
				encodedStream.seek(initialStreamPosition);
				progressionOrder0();
			} else {
				encodedStream.seek(initialStreamPosition);
				decodeTER();
			}
		} else{
			throw new ParameterException("Unkown file type for decoding.");
		}
		
		inverseWeighting();
		inverseDWT();
		postProcessing();
		
		return recoveredImage;
	}
	

	private void recommendedDecoding() throws Exception{
		
		this.gammaValue = ParameterTools.setParameterFloat(gammaValue,zSize,TERDefaultValues.gammaValue);				
		this.completionMode = ParameterTools.setParameterInt(completionMode,zSize,TERDefaultValues.completionMode);
		
		
		RecommendedExtractor re = new RecommendedExtractor(encodedStream);
		re.setParameters(targetBytes);
		RecommendedDecoding rec = new RecommendedDecoding();
		rec.setParameters(imageGeometry, null, gammaValue, completionMode, CVerbose, minusHalf);
		recoveredImage = new float[zSize][][];
		try{
			recoveredImage[0] = rec.run(re.run());
		} catch (EOFException e){
			//recoveredImage = rec.getRecoveredImage();
		}

		imageExtensionTypeExtraction = rec.getImageExtensionType(); 
		padRowsExtraction = rec.getPadRows();
		
		WTTypeExtraction = rec.getWTType();
		WTLevelsExtraction = rec.getWTLevels();
		WTOrderExtraction = rec.getWTOrder();
		
		signedPixelsExtraction = rec.getSignedPixels();
		pixelBitDepthExtraction = rec.getPixelBitDepth();
		transposeImgExtraction = rec.getTransposeImg();
		customWtFlagExtraction = rec.getCustomWtFlag();
		customWeightExtraction = rec.getCustomWeight();
		
		xSizesExtraction = rec.getXSize();
		imageWidthExtraction = rec.getImageWidth();
		
		zSizeExtraction = zSize;
		
		rec = null;
	}

	private void progressionOrder0() throws Exception{
		System.out.println("Not implemented yet");
		
		/*this.gammaValue = ParameterTools.setParameterFloat(gammaValue,zSize,TERDefaultValues.gammaValue);				
		this.completionMode = ParameterTools.setParameterInt(completionMode,zSize,TERDefaultValues.completionMode);
				
		RecommendedExtractor re = new RecommendedExtractor(encodedStream);
		re.setParameters(targetBytes);
		
		ProgressionOrder0  po0 =new ProgressionOrder0(zSize, yOriginalSize, xOriginalSize);
		po0.setParameters(null, gammaValue, completionMode, CVerbose, WTType, WTLevels, 
				WTOrder,null, minusHalf);
		try{
			recoveredImage = po0.run(re.run());			
		} catch (EOFException e){
			
		}

		WTTypeExtraction = WTType;
		WTLevelsExtraction = WTLevels;
		WTOrderExtraction = WTOrder;
		
		imageExtensionTypeExtraction = po0.getImageExtensionType(); 
		padRowsExtraction = po0.getPadRows();
		
		signedPixelsExtraction = po0.getSignedPixels();
		pixelBitDepthExtraction = po0.getPixelBitDepth();
		transposeImgExtraction = po0.getTransposeImg();
		customWtFlagExtraction = po0.getCustomWtFlag();
		customWeightExtraction = po0.getCustomWeight();
		
		xSizesExtraction = po0.getXSize();
		imageWidthExtraction = po0.getImageWidth();
		
		zSizeExtraction = zSize;
		
		po0=null;
		*/
		
	}
	
	
	
	
	private void decodeTER() throws Exception{
		
		
	
		TERExtractor extractor = new TERExtractor(encodedStream, progressionOrder,layerLocation);
		
		extractor.setParameters(zSize, xSizes, numLayers, 
				WTLevels, WTType, WTOrder,
				customWtFlag,customWeight,
				padRows, imageExtensionType, transposeImg,
				signedPixels, pixelBitDepth, 
				imageWidth, imageGeometry,
				bitDepthDC,bitDepthAC,
				blocksPerSegment, gaggleSizeDC,gaggleSizeAC,entropyAC,
				extractionType, channelList, yInit, yLength, xInit, xLength, 
				numLayersExtraction, requiredLevels, targetBytes);
		byte byteStream[][][][][] = extractor.run(); 
		
		DecodeAvailable decode = new DecodeAvailable(byteStream);
		setExtractionParameters(extractor);
		decode.setParameters(zSizeExtraction, ySizeExtraction,xSizeExtraction,
				imageExtensionTypeExtraction, WTTypeExtraction, WTLevelsExtraction,resolutionLevels,
				customWtFlagExtraction,customWeightExtraction,
				blocksPerSegmentExtraction,gaggleSizeDCExtraction,gaggleSizeACExtraction, entropyACExtraction,
				bitDepthDCExtraction, bitDepthACExtraction,
				completionMode, gammaValue);
		recoveredImage = decode.run();
		
		
	}
	
	private void setExtractionParameters(TERExtractor extractor) throws ParameterException{
		zSizeExtraction = extractor.getZSizeExtraction();
		ySizeExtraction = extractor.getYSizeExtraction();
		xSizeExtraction = extractor.getXSizeExtraction();
		
		WTTypeExtraction = extractor.getWTTypeExtraction();
		WTOrderExtraction = extractor.getWTOrderExtraction();
		WTLevelsExtraction = extractor.getWTLevelsExtraction();
		
		this.resolutionLevels = new int[zSizeExtraction];
		for(int z=0;z<zSizeExtraction;z++){
			resolutionLevels[z] = WTLevelsExtraction[z] + 1 ;
		}
		
		customWtFlagExtraction = extractor.getCustomWtFlagExtraction();
		customWeightExtraction = extractor.getCustomWeightExtraction();
		
		blocksPerSegmentExtraction = extractor.getBlocksPerSegmentExtraction();
		gaggleSizeDCExtraction = extractor.getGaggleSizeDCExtraction();
		gaggleSizeACExtraction = extractor.getGaggleSizeACExtraction();
		entropyACExtraction = extractor.getEntropyACExtraction();
		
		xSizesExtraction = extractor.getXSizesExtraction();

		
		padRowsExtraction = extractor.getPadRowsExtraction();
		signedPixelsExtraction = extractor.getSignedPixelsExtraction();
		pixelBitDepthExtraction = extractor.getPixelBitDepthExtraction();
		transposeImgExtraction = extractor.getTransposeImgExtraction();
		imageExtensionTypeExtraction = extractor.getImageExtensionTypeExtraction();
		imageWidthExtraction = extractor.getImageWidthExtraction();
		imageGeometryExtraction = extractor.getImageGeometryExtraction();
		
		bitDepthDCExtraction = extractor.getBitDepthDCExtraction();
		bitDepthACExtraction = extractor.getBitDepthACExtraction();
		
		this.gammaValue = ParameterTools.setParameterFloat(gammaValue,zSizeExtraction,TERDefaultValues.gammaValue);				
		this.completionMode = ParameterTools.setParameterInt(completionMode,zSizeExtraction,TERDefaultValues.completionMode);
		
	}
	
	private void inverseWeighting() throws ParameterException{
		//Weighting
		boolean needWeighting = ForwardWeighting.setWeightingNeed(customWtFlagExtraction,WTTypeExtraction,zSizeExtraction);
		if (needWeighting){
			InverseWeighting iwg = new InverseWeighting(recoveredImage);
			iwg.setParameters(customWtFlagExtraction,WTTypeExtraction,WTLevelsExtraction,customWeightExtraction);
			recoveredImage = iwg.run();
			iwg = null;
			System.gc();
			
		}
		
		//we check if all the DWT levels are performed, if not, a quantization stage must be applied
		for(int z=0;z<zSizeExtraction;z++){
			if (WTLevelsExtraction[z]!=WTLevels[channelList[z]]){
				int weigth = (int) (1<<WTLevels[channelList[z]]-WTLevelsExtraction[z]);					
				for(int y=0;y<ySizeExtraction;y++){
					for(int x=0;x<xSizeExtraction;x++){
						recoveredImage[z][y][x] = recoveredImage[z][y][x]/weigth;
					}
				}
			}
		}
	}
	
	private void inverseDWT() throws Exception{
		//Inverse Wavelet transform
		InverseWaveletTransform iwt = new InverseWaveletTransform(recoveredImage);
		iwt.setParameters(WTTypeExtraction,WTLevelsExtraction,WTOrderExtraction);
		recoveredImage = iwt.run();
		iwt = null;
		
		System.gc();
	}
	
	private void postProcessing() throws Exception{
		boolean threeLevels = true;
		for(int k=0;k<WTLevelsExtraction.length;k++){
			if(WTLevelsExtraction[k]!=3){
				threeLevels= false;
			}
		}
		if ( !threeLevels && padRowsExtraction==null ){
			if (this.imageGeometryExtraction!=null){
				padRowsExtraction = ImageDeExtension.computePadRows(zSizeExtraction,imageGeometryExtraction[1], imageExtensionTypeExtraction, WTLevelsExtraction);
			} else {//the user does not know the original size
				padRowsExtraction = ImageDeExtension.computePadRows(zSizeExtraction,recoveredImage[0].length, imageExtensionTypeExtraction, WTLevelsExtraction);
			}
		}
		boolean needDeExtension = ImageDeExtension.needDeExtension(padRowsExtraction,imageWidthExtraction,xSizesExtraction);
		//Image DeExtension
		if (needDeExtension){
			ImageDeExtension ide = new ImageDeExtension(recoveredImage);
			ide.setParameters(imageWidthExtraction,this.padRowsExtraction);
			recoveredImage = ide.run();
			//Free unused memory
			ide = null;
		}
		
		//Transpose Image
		boolean needTranspose = TransposeImage.needTranspose(transposeImgExtraction);
		if (needTranspose){
			TransposeImage ti = new TransposeImage(recoveredImage);
			ti.setParameters(transposeImgExtraction);
			recoveredImage = ti.run();
			ti = null;
		}
		
		//Level Unshift
		if (LSType!=0){
			LevelUnshift ls = new LevelUnshift(recoveredImage);
			ls.setParameters(LSType, LSComponents, LSSubsValues, pixelBitDepthExtraction);
			recoveredImage = ls.run();
			//Free unused memory
			ls = null;
		}
		
		for(int z=0; z<zSizeExtraction ; z++){
			int ySize = recoveredImage[z].length;
			int xSize = recoveredImage[z][0].length;
			
			int maxValue = (int) (1<<this.pixelBitDepthExtraction[z]) - 1;
			int minValue = 0;
			
			if (signedPixelsExtraction[z]==1){
				maxValue = (int) (1<<(this.pixelBitDepthExtraction[z]-1)) - 1;
				minValue = - (int) (1<<(this.pixelBitDepthExtraction[z]-1));
			} 
			
			for(int y=0; y<ySize ; y++){
				for(int x=0; x<xSize ; x++){
					
					if (recoveredImage[z][y][x] > maxValue){
						recoveredImage[z][y][x] = maxValue;
					} else if (recoveredImage[z][y][x] < minValue ){
						recoveredImage[z][y][x] = minValue;
					}
					recoveredImage[z][y][x]=Math.round(recoveredImage[z][y][x]);
				}
			}
		}
		
		
		if(imageGeometryExtraction==null){
			imageGeometryExtraction = new int[6];
			imageGeometryExtraction[0] = recoveredImage.length;
			imageGeometryExtraction[1] = recoveredImage[0].length;
			imageGeometryExtraction[2] = recoveredImage[0][0].length;
			int sampleType = 0;
			for(int k=0;k<pixelBitDepthExtraction.length;k++){
				if (sampleType<pixelBitDepthExtraction[k]){
					sampleType = pixelBitDepthExtraction[k];
				}
			}
			if(sampleType%8==0){
				sampleType = sampleType/8;
			} else {
				sampleType = (sampleType/8) + 1;
			}
			imageGeometryExtraction[3] = sampleType; 
			imageGeometryExtraction[4] = 0;	
		}
	}
	
	
	
	/////////////////////
	////GET FUNCTIONS////
	/////////////////////
	
	public int[] getImageGeometry(){
		return imageGeometryExtraction;
	}
	
	public long getStreamLength(){
		return encodedStream.getTotalBytes();
	}
}