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

import TER.TERDefaultValues;


/**
 * Arguments parser for TER coder. This class analyses a string of arguments and extract and check its validity.
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; [showArgsInfo]<br>
 * &nbsp; [get functions]<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 2.0
 */
public class ArgsParserCoder{

	private void showVersion(){
		System.out.println(" -------  help - version 2.0  ---------  ");
	}
	
	/**
	 * Arguments specificiation. The array describes argument, explain what is used and its default parameters. First index of array is argument; second specifies:<br>
	 *   <ul>
	 *     <li> 0 - short argument specification (i.e. "-nl")
	 *     <li> 1 - long argument specification (i.e. "--numberLevels")
	 *     <li> 2 - parsing specification of argument ({} indicates mandatority, [] optionality)
	 *     <li> 3 - default values
	 *     <li> 4 - mandatory argument ("1") or non mandatory argument ("0")
	 *     <li> 5 - explanation
	 *   </ul>
	 * <p>
	 * String arguments.
	 */
	String[][] argsSpecification = {
		{"-h", "--help", "", "", "0",
			"Displays this help and exits program."
		},
		{"-i", "--inputImage", "{string}", "", "1",
			"Input image. Valid formats are: pgm, ppm, pbm, jpg, tiff, png, bmp, gif, fpx. If image is raw data file extension must be \".raw\" or \".img\" and \"-g\" parameter is mandatory."
		},
		{"-g", "--imageGeometry", "{int int int int boolean}", "", "0",
			"Geometry of raw image data. Parameters are:\n    1- zSize (number of image components)\n    2- ySize (image height)\n    3- xSize (image width)\n    4- data type. Possible values are:\n \t 0- boolean (1 byte)\n \t 1- unsigned int (1 byte)\n \t 2- unsigned int (2 bytes)\n \t 3- signed int (2 bytes)\n \t 4- signed int (4 bytes)\n \t 5- signed int (8 bytes)\n \t 6- float (4 bytes)\n \t 7- double (8 bytes)\n    5- Byte order (0 if BIG ENDIAN, 1 if LITTLE ENDIAN)\n    6- 1 if 3 first components are RGB, 0 otherwise."
		},
		{"-o", "--outputFile", "{string}", "same as input with a new extension (see \"-of\" parameter)", "0",
			"Output image file name (specified without extension)."
		},
		{"-of", "--outputFileType", "{int}", TERDefaultValues.outputFileType+" ", "0",
			"File type to generate. Valid values are:\n    0- CCSDS Recommended file (.rec) \n    1- TER file (.ter)"
		},
		//Recommendation Parameters.
		{"-h2", "--part2Flag","{int[int[ int[ ...]]]}", TER.TERDefaultValues.part2Flag+" ","0",
		  "Indicates for each segment the presence of Part 2 header.\n 0 - Part 2 header absent\n 1 - Part 2 header present"
		},
		{"-h3", "--part3Flag","{int[int[ int[ ...]]]}", TER.TERDefaultValues.part3Flag+" ","0",
		  "Indicates for each segment the presence of Part 3 header.\n 0 - Part 3 header absent\n 1 - Part 3 header present"
		},
		{"-h4", "--part4Flag","{int[int[ int[ ...]]]}", TER.TERDefaultValues.part4Flag+" ","0",
		  "Indicates for each segment the presence of Part 4 header.\n 0 - Part 4 header absent\n 1 - Part 4 header present"
		},
		{"-bl", "--segByteLimit", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.segByteLimit+" ", "0",
			"Maximum number of bytes that can be used in a segment.\n The value of SegByteLimits includes bytes used for the header, and is applied to the subsequent segments until a new value is given. Value should be in the interval (0 , 134217728] and it is expressed mod(134217728)"
		},
		{"-dc", "--DCStop", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.DCStop+" ", "0",
			"Indicates whether compressed output stops after coding of quantized DC coefficients.  If only one value is specified, type will be used for all image segments."
		},
		{"-bp", "--bitPlaneStop", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.bitPlaneStop+" ", "0", 
			"Only used when DCStop equals 0. Indicates the bit plane index where the codification should stop.  If only one value is specified, type will be used for all image segments."
		},
		{"-ss", "--stageStop", "{int[ int[ int[ ...]]]}", "The same of wavelet levels plus one",  "0",
			"Indicates the stage where the codification ends.  If only one value is specified, type will be used for all image segments. If three levels of DWT are applied, then: \n    1 - stage 1 \n    2 - stage 2 \n    3 - stage 3 \n    4 - stage 4 "
		},
		{"-uf", "--useFill", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.useFill+" ", "0",
			"Specifies whether fill bits will be used to produce SegByteLimit bytes in each segment.  If only one value is specified, type will be used for all image segments."
		},
		{"-bs", "--blocksPerSegment", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.blocksPerSegment+" ", "0",
			"Indicates the number of blocks contained in a segment. If only one value is specified, type will be used for all image segments."
		},
		{"-ds", "--optDCSelect", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.optDCSelect+" ", "0",
			"Indicates the method used to select k parameter while coding DC components"
		},
		{"-as", "--optACSelect", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.optACSelect+" ", "0",
			"Indicates the method used to select k parameter while coding BitDepthAC_Block components"
		},
		{"-wt", "--WTType", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.WTType+" ", "0",
			"Discrete wavelet transform type for each image component in the spatial domain. First value is for the first component, second value for the second component and so on. If only one value is specified, wavelete transform type will be the same for all components. Valid values are:\n 0 - No wavelet transform \n 1 - Integer (Reversible) 5/3 DWT \n 2 - Real Isorange (irreversible) 9/7 DWT (JPEG2000 standard) \n 3 - Real Isonorm (irreversible) 9/7 DWT (CCSDS-Recommended) \n 4 - Integer (Reversible) 9/7M DWT (CCSDS-Recommended) \n 5 - Integer 5/3 DWT (classic construction) \n 6 - Integer 9/7 DWT (classic construction)"
		},
		{"-sp", "--signedPixels", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.signedPixels+" ", "0",
			"For each segment (entire image image in the Recommendation) specifies if the input pixels are signed or unsigned quantities. Valid values are:\n 0 - unsigned \n 1 - signed"
		},
		{"-ti", "--transposeImg", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.transposeImg+" ", "0",
			"Indicates whether the channel (entire image in the Recommendation) should be transposed after reconstruction. Valid values are:\n 0 - do not transpose image \n 1 - transpose image"
		},
		{"-cl", "--codeWordLength", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.codeWordLength+" ", "0",
			"Indicated the coded word length for each segment. Valid values are:\n 0 - 8-bit word \n 1 - 16-bit word \n 2 - 24-bit word \n 3 - 32-bit word"
		},
		{"-wg", "--customWtFlag", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.customWtFlag+" ", "0",
			"Weighting type for each component. First value is for the first component, second for the second component and so on. If only one value is specified, type will be used for all image components.\n 0 - CCSDS recommended\n 1 - Defined by the user\n 2 - No weighting"
		},
		{"-cw", "--customWeight", "{float[ float[ float[ ...]]]}", "", "0",
			"Custom weights defined by the user. For each subband of each component a weighting factor should be given for each subband. If only the number of parameters introduced is not correct the programm stops. The order of the weights is as follows: For each component :\n LL_n,HL_n,LH_n,HH_n,HL_n-1,LH_n-1,HH_n-1,...,HL_0,LH_0,HH_0."
		},
		// Extended parameters
		{"-wl", "--WTLevels", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.WTLevels+" ", "0",
			"Discrete wavelet transform levels for each image component in the spatial domain. First value is for the first component, second value for the second component and so on. If only one value is specified, wavelet transform levels will be the same for all components."
		},
		{"-ie", "--imageExtensionType","{int[ int[ int[ ...]]]}", TER.TERDefaultValues.imageExtensionType+" ", "0",
			"Indicates the kind of extension that has been applied to the each component of the image in order to make it able to be compressed using TER.  If only one value is specified, image extension type will be the same for all components. Valid values are:\n 0 - Repeating last value\n 1 - Symmetric expansion\n 2 - No extension"
		},
		{"-wo", "--WTOrder", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.WTOrder+" ", "0",
			"Order in which the Discrete wavelet transform is performed over the spatial dimentions. First value is for the first component, second value for the second component and so on. If only one value is specified, wavelet transform order will be the same for all components. \n 0 - Horizontal - Vertical \n 1 - Vertical - Horizontal \n 2 - Only horizontal"
		},
		{"-gd", "--gaggleDCSize", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.gaggleDCSize+" ", "0",
			"Size of a gaggle for DC components in each segment of the image. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the size of DC gaggles will be the same for all segments. 0 value means that all the blocks of the segment are in the same gaggle"
		},
		{"-ga", "--gaggleACSize", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.gaggleACSize+" ", "0",
			"Size of a gaggle for AC components in each segment of the image. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the size of AC gaggles will be the same for all segments. 0 value means that all the blocks of the segment are in the same gaggle"
		},
		{"-id", "--idDC", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.idDC+" ", "0",
		"Number which indicates how often the ID DC appears. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the frequency of ID DC will be the same for all segments. 0 value means that only an ID DC is used for the segment"
		},
		{"-ia", "--idAC", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.idAC+" ", "0",
		"Number which indicates how often the ID AC appears. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the frequency of ID AC will be the same for all segments. 0 value means that only an ID DC is used for the segment"
		},
		{"-ea", "--entropyAC", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.entropyAC+" ", "0",
		 "Integer array which indicates the entropy coder selected for coding AC components for each segment. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the AC entropy coder will be the same for all segments. If only one value is specified, entropy encoder for AC components will be the same for all segments. Valid values are:\n 0 -  No entropy code for AC components \n 1 - CCSDS Recommended  entropy coder\n "
		},
		{"-rl", "--resolutionLevels", "{int[ int[ int[ ...]]]}", "same of WT levels specified", "0",
		 "Highest resolution level to be coded for each image component. 0 means that only LL subband will be compressed, 1 is LL + (HL1 + LH1 + HH1), 2 is LL + (HL1 + LH1 + HH1) + (HL2 + LH2 + HH2) and so on. \n Values must be between 0 to WT levels. If a value is greater than the number of WT levels it will be understood that all resolution levels of the component are to be compressed. If only one value is specified, it will be used for all image components."
		},
		{"-pd", "--pixelBitDepth", "{int[ int[ int[ ...]]]}", " ", "0",
			"Integer array which indicates the pixel bit depth for each channel. First value is for the first channel, second value for the second channel and so on. If only one value is specified, the pixel bit depth will be the same for all channel."	
		},
		{"-vc", "--verboseComputation", "{boolean}", (TER.TERDefaultValues.CVerbose[0] ? "1": "0") + " ", "0",
			"Show some information about time and used memory for each compression stage. Value is a boolean: 0 indicates NO show and 1 indicates show."
		},
		{"-vp", "--verboseParameters", "{boolean}", (TER.TERDefaultValues.CVerbose[1] ? "1": "0") + " ", "0",
			"Show TER encoding parameters. Value is a boolean: 0 indicates NO show and 1 indicates show."
		},
		{"-bps", "--bitsPerSample", "{float[float[float[ ...]]]}", " ", "0",
			"Float array which indicates the bits per sample of each encoded segment. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the bits per sample will be the same for all segments."
		},
		{"-cf", "--compressionFactor", "{float[ float[ float[ ...]]]}", TER.TERDefaultValues.compressionFactor+" ", "0",
			"Float array which indicates the compression factor for each segment. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the compression factor will be the same for all segments."	
		},
		{"-tp", "--truncationPoints", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.truncationPoints+" ", "0",
			"For each segment specifies the available truncation points. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the truncation points will be the same for all segments. Valid values are:\n 0 - any \n 1 - end of bitplane (bitPlaneStop) \n 2 - end of resolution level (stageStop) \n 3 - end of gaggle"
		},
		{"-ap", "--adjustHeaderParameters", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.adjustHeaderParameters+" ", "0",
			"For each segment specifies the parameters that should be adjusted. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the asjusted parameters will be the same for all segments. Valid values are:\n 0 - No adjustement  \n 1 - Adjust segByteLimit and useFill after interleaving "
		},
		{"-po", "--progressionOrder", "{int}", TER.TERDefaultValues.progressionOrder+" ", "0",
			"This parameter specifies the progression order employed to create the encode image. Valid values are: \n 0 Recommended order (i.e. order defined in the CCSDS recomendation for image data coding) \n 1 LRCP Layer-Resolution-Component-Position \n 2 RLCP Resolution-Layer-Component-Position \n 3 RPCL Resolution-Position-Component-Layer \n 4 PCRL Position-Component-Resolution-Layer \n 5 CPRL Component-Position-Resolution-Layer"
		},
		{"-st", "--shiftType", "{int}", TER.TERDefaultValues.LSType+" ", "0",
			"Level shift is a preprocessing operation for unsigned channels to convert them in signed channels. It can be performed in some diferent ways:\n    0- No level shift\n    1- JPEG2000 standard level shifting (only non-negative channels)\n    2- Range center substract\n    3- Average substract\n    4- Specific values substract (see \"-sv\" parameter)"
		},
		{"-sc", "--shiftChannels", "{boolean[ boolean[ boolean[ ...]]]}", TER.TERDefaultValues.LSComponents+" ", "0",
			"Specification in which channels level shift will be applied. Each value specifies a channel (0 is the first image channel)."		
		},
		{"-sv", "--shiftValues", "{int[ int[ int[ ...]]]}", "null", "0",
			"Substracted values in each image channel. If only one is specified, the value will be used for all image channel otherwise first value is for the first component, second value is for the second component, and so on."
		},
		{"-ca", "--coefficientsApproximation", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.coefficientsApproximationTypes+" ", "0",
			"This parameter specifies the approximation to be applied to coefficients of each channel. First value is for the first channel, second value for the second channel and so on. If only one value is specified, it will be the same for all channels. Valid values are:\n 0 - Cast to integer  \n 1 - Round  \n 2 - Floor  \n 3 - Ceil "
		},
		{"-tb", "--targetBytes", "{int[ int[ int[ ...]]]}",  "", "0",
			"Number of target bytes for each compressed bitstream. Computations are done without taking into account headings, so file size will be slightly larger than this number of bytes (0 indicates that size file is not important -all compressed data will be stored-)."
		},
		{"-lc", "--layerCreationType", "{int}", TER.TERDefaultValues.layerCreationType+" ", "0",
			"This parameter specifies the method used to create the layers. Valid values are:\n 0 - One single layer is created \n 1 - Size of layers correspond to the maximum size available \n 2 - Coding Passes Interleaving  \n 3 - Bitplane Interleaving"
		},
		{"-nl", "--numberOfLayers", "{int}",  TER.TERDefaultValues.numberOfLayers+" ", "0",
			"Number of layers desired by the user. If target does not allow this number, it is possible to create a file with less layers."
		},
		{"-ls", "--layerSizeType", "{int}",  TER.TERDefaultValues.layerSizeType+" ", "0",
			"This parameter indicates the technique employed to select the size of the layers.Valid values are:\n 0 - Given by the user  \n 1 - All layers have the same length \n 2 - Each layer has half of the length that the next layer (except for the first)."
		},
		{"-lb", "--layerBytes", "{int[ int[ int[ ...]]]}",  " ", "0",
			"This parameters indicates the size of each layer. Each value specifies a layer, if there appear less values than layers demanded, the last value is taken as a default value for the rest of layers."
		},
		{"-bpppb", "--bitsPerPixelPerBand", "{float[ float[ float[ ...]]]}", " ", "0",
			"Float which indicates the bits per sample that must be used to encode the image. If more than one is specified, it will be created as many files as demandedby the user."
		},
		{"-t3", "--test3d", "{int}", " ", "0",
			"Kind of 3D Discrete wavelet transform to be applied. Valid values are:\n 0 - 2D DWT in the spatial domain  \n 1 - 1D + 2D hybrid  DWT \n 2 - 3D pyramidal DWT"
		},
		{"-swl", "--spectralWTLevels", "{int}", " ", "0",
			"Discrete wavelet transform levels to be applied in the spatial domain."
		},
		{"-swt", "--spectralWTType", "{int}", " ", "0",
			"Discrete wavelet transform type to be applied in the spatial domain."
		}
		/*,
		{"-dm", "--distortionMeasure", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.distortionMeasure+" ", "0",
			"Integer array which indicates the distortion measure selected for each segment. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the distortion measure will be the same for all segments. If only one value is specified, distortion measure will be the same for all segments. Valid values are:\n 0 - None \n 1 - L1 norm \n 2 - MSE \n "
		},
		{"-dd", "--desiredDistortion", "{float[ float[ float[ ...]]]}", TER.TERDefaultValues.desiredDistortion+" ", "0",
		"Real value which indicates the MSE that is desired for each segment. First value is for the first segment, second value for the second segment and so on. If only one value is specified, the MSE value will be the same for all segments."
		},*/
		
	};

	//ARGUMENTS VARIABLES
	String imageFile= null;
	String outputFile = null;
	int outputFileType = TERDefaultValues.outputFileType;
	int[] imageGeometry = null;
	int[] part2Flag = null;
	int[] part3Flag = null;
	int[] part4Flag = null;
	int[] segByteLimit = null;
	int[] DCStop = null;
	int[] bitPlaneStop = null;
	int[] stageStop = null;
	int[] useFill = null;
	int[] blocksPerSegment = null;
	int[] optDCSelect = null;
	int[] optACSelect = null;
	int[] WTType = null;
	int[] signedPixels = null;
	int[] transposeImg = null;
	int[] codeWordLength = null;
	int[] customWtFlag = null;
	float[] customWeight = null;
	int[] WTLevels = null;
	int[] imageExtensionType = null;
	int[] WTOrder = null;
	int[] gaggleDCSize= null;
	int[] gaggleACSize= null;
	int[] idDC = null;
	int[] idAC = null;
	float[] desiredDistortion = null;
	int [] entropyAC = null;
	int[] distortionMeasure = null;
	int[] resolutionLevels = null;
	float[] compressionFactor = null;
	int[] pixelBitDepth = null;
	boolean[] CVerbose = new boolean[2];
	float[] bitsPerSample = null;
	int[] truncationPoints = null;
	int[] adjustHeaderParameters = null;
	int progressionOrder = TER.TERDefaultValues.progressionOrder;
	int LSType = TER.TERDefaultValues.LSType;
	int[] LSComponents = null;
	int[] LSSubsValues = null;
	int[] coefficientsApproximation = null;
	int[]targetBytes = null;
	float[] bpppb = null;

	int layerCreationType = TERDefaultValues.layerCreationType;
	int numberOfLayers = TERDefaultValues.numberOfLayers;
	int layerSizeType = TERDefaultValues.layerSizeType;
	int layerBytes[] = null;
	
	int test3d = 0;
	int spectralWTLevels = 0;
	int spectralWTType = 0;
	
	 /**
	  * Class constructor that receives the arguments string and initializes all the arguments
	  * 
	  * @param args the array of strings passed at the command line
	  * 
	  * @throws Exception when an invalid parsing is detected or some problem with method invocation occurs
	  */
	public ArgsParserCoder(String[] args) throws Exception{
		int argNum = 0;
		boolean[] argsFound = new boolean[argsSpecification.length];

		//Arguments parsing
		for(int i = 0; i < argsSpecification.length; i++){
			argsFound[i] = false;
		}
		while(argNum < args.length){
			int argFound = argFind(args[argNum]);
			if(argFound != -1){
				if(!argsFound[argFound]){
					argsFound[argFound] = true;
					int argOptions = argNum + 1;
					while(argOptions < args.length){
						if(argFind(args[argOptions]) != -1){
							break;
						}else{
							argOptions++;
						}
					}
					int numOptions = argOptions - argNum;
					String[] options = new String[numOptions];
					System.arraycopy(args, argNum, options, 0, numOptions);
					argNum = argOptions;
					switch(argFound){
					case  0: //-h  --help
						showVersion();
						showArgsInfo();
						showVersion();
						System.exit(1);
						break;
					case  1: //-i  --inputImage
						imageFile = parseString(options);
						if(imageFile.endsWith(".raw")){
							argsSpecification[2][4] = "1";
						}
						break;
					case  2: //-g  --imageGeometry
						imageGeometry = parseIntegerArray(options, 6);
						break;
					case  3: //-o  --outputFile
						outputFile = parseString(options);
						break;
					case  4: //-of  --outputFileFile
						outputFileType = parseIntegerPositive(options);
						break;	
					case  5: //-h2 --Part2Flag
						part2Flag = parseIntegerArray(options);
						break;
					case  6: //-h3 --Part3Flag
						part3Flag = parseIntegerArray(options);
						break;
					case  7: //-h4 --Part4Flag
						part4Flag = parseIntegerArray(options);
						break;	
					case  8: //-bl --SegByteLimit
						segByteLimit = parseIntegerArray(options);
						break;
					case  9: //-dc --DCStop
						DCStop = parseIntegerArray(options);
						break;
					case  10: //-bp --bitPlaneStop
						bitPlaneStop = parseIntegerArray(options);
						break;
					case  11: //-ss --StageStop
						stageStop = parseIntegerArray(options);
						break;
					case  12: //-uf --UseFill
						useFill = parseIntegerArray(options);
						break;
					case  13: //-bs --BlocksPerSegment
						blocksPerSegment = parseIntegerArray(options);
						break;
					case  14: //-ds --optDCSelect
						optDCSelect = parseIntegerArray(options);
						break;
					case  15: //-as --optACSelect
						optACSelect = parseIntegerArray(options);
						break;
					case  16: //-w --WTType
						WTType = parseIntegerArray(options);
						break;
					case  17: //-sp --SignedPixels
						signedPixels = parseIntegerArray(options);
						break;	
					case  18: //-ti --TransposeImg
						transposeImg = parseIntegerArray(options);
						break;
					case  19: //-cl --CodeWordsLength
						codeWordLength = parseIntegerArray(options);
						break;
					case  20: //-wg --customWtFlag
						customWtFlag = parseIntegerArray(options);
						break;
					case  21: //-cw --customWeight
						customWeight = parseFloatArray(options);
						break;
					case  22: //-wl --WTLevels
						WTLevels = parseIntegerArray(options);
						break;
					case  23: //-ie --imageExtensionType
						 imageExtensionType = parseIntegerArray(options);
						break;	
					case  24: //-wo --WTOrder
						WTOrder = parseIntegerArray(options);
						break;
					case  25: //-gd --gaggleDCSize
						gaggleDCSize = parseIntegerArray(options);
						break;
					case  26: //-ga --gaggleACSize
						gaggleACSize = parseIntegerArray(options);
						break;
					case  27: //-id --idDC
						idDC = parseIntegerArray(options);
						break;
					case  28: //-ia --idAC
						idAC = parseIntegerArray(options);
						break;
					case  29: //-ea --entropyAC
						entropyAC = parseIntegerArray(options);
						break;
					case  30: //-rl --resolutionLevels
						resolutionLevels = parseIntegerArray(options);
						break;	
					case  31://-pd --pixelBitDepth
						pixelBitDepth = parseIntegerArray(options);
						break;
					case  32://-vc, --verboseComputation
						CVerbose[0] = parseBoolean(options);
						break;
					case  33://-vp, --verboseParameters
						CVerbose[1] = parseBoolean(options);
						break;
					case  34://-bps --bitsPerSample
						bitsPerSample = parseFloatArray(options);
						break;	
					case  35://-cf --compressionFactor
						compressionFactor = parseFloatArray(options);
						break;
					case  36: //-tp --truncationPoints
						truncationPoints = parseIntegerArray(options);
						break;	
					case  37://-ap --adjustHeaderParameters
						adjustHeaderParameters = parseIntegerArray(options);
						break;
					case  38://-po --progressionOrder
						progressionOrder = parseIntegerPositive(options);
						break;	
					case  39://-st --shiftType
						LSType = parseIntegerPositive(options);
						break;	
					case  40://-sc --shiftChannels
						 LSComponents = parseIntegerArray(options);
						break;	
					case  41://-sv --shiftValues
						 LSSubsValues = parseIntegerArray(options);
						break;	
					case  42://-ca --coefficientsApproximation
						 coefficientsApproximation = parseIntegerArray(options);
						break;	
					case  43://-tb --targetBytes
						targetBytes = parseIntegerArray(options);
						break;	
					case  44://"-lc", "--layerCreationType"
						layerCreationType = parseIntegerPositive(options);
						break;	
					case  45://"-nl", "--numberOfLayers"
						numberOfLayers = parseIntegerPositive(options);
						break;	
					case  46://"-ls", "--layerSizeType"
						layerSizeType = parseIntegerPositive(options);
						break;	
					case  47://-lb --layerBytes
						layerBytes = parseIntegerArray(options);	
					case  48://-bpppb --bitsPerPixelPerBand
						bpppb = parseFloatArray(options); 
						break;	
					case  49://-t3 --test3d
						test3d = parseIntegerPositive(options); 
						break;
					case  50://-swl --spectralWTLevels
						spectralWTLevels = parseIntegerPositive(options); 
						break;	
					case  51://-swt --spectralWTType
						spectralWTType = parseIntegerPositive(options);
						break;
					/*case  33: //-dm --distortionMeasure
						distortionMeasure = parseIntegerArray(options);
						break;				
					case  34: //-dd --desiredDistortion
						desiredDistortion = parseFloatPositiveArray(options);
						break;*/

					}
				}else{
					throw new Exception("Argument \"" + args[argNum] + "\" repeated.");
				}
			}else{
				throw new Exception("Argument \"" + args[argNum] + "\" unrecognized.");
			}
		}

		//Check mandatory arguments
		for(int i = 0; i < argsSpecification.length; i++){
			if(argsSpecification[i][4].compareTo("1") == 0){
				if(!argsFound[i]){
					throw new Exception("Argument \"" + argsSpecification[i][0] + "\" is mandatory (\"-h\" displays help).");
				}
			}
		}
	}

	/**
	 * Finds the argument string in arguments specification array.
	 *
	 * @param arg argument to find out in argsSpecification
	 * @return the argument index of argsSpecification (-1 if it doesn't exist)
	 */
	int argFind(String arg){
		int argFound = 0;
		boolean found = false;

		while((argFound < argsSpecification.length) && !found){
			if((arg.compareTo(argsSpecification[argFound][0]) == 0) || (arg.compareTo(argsSpecification[argFound][1]) == 0)){
				found = true;
			}else{
				argFound++;
			}
		}
		return(found ? argFound: -1);
	}

	/**
	 * This function shows arguments information to console.
	 */
	public void showArgsInfo(){
		System.out.println("Arguments specification: ");
		for(int numArg = 0; numArg < argsSpecification.length; numArg++){
			char beginMandatory = '{', endMandatory = '}';
			if(argsSpecification[numArg][4].compareTo("0") == 0){
				//No mandatory argument
				beginMandatory = '[';
				endMandatory = ']';
			}
			System.out.print("\n" + beginMandatory + " ");
			System.out.print("{" + argsSpecification[numArg][0] + "|" + argsSpecification[numArg][1] + "} " + argsSpecification[numArg][2]);
			System.out.println(" " + endMandatory);
			System.out.println("  Explanation:\n    " + argsSpecification[numArg][5]);
			System.out.println("  Default value: " + argsSpecification[numArg][3]);
		}
	}


	/////////////////////
	//PARSING FUNCTIONS//
	/////////////////////
	//These functions receives a string array that contains in first position the argument and then their options//

	public boolean parseBoolean(String[] options) throws Exception{
		boolean value = false;

		if(options.length == 2){
			try{
				int readValue = Integer.parseInt(options[1]);
				if((readValue < 0) || (readValue > 1)){
					throw new Exception("\"" + options[1] + "\" of argument \"" + options[0] + "\" must be 0 or 1.");
				}else{
					value = readValue == 0 ? false: true;
				}
			}catch(NumberFormatException e){
				throw new Exception("\"" + options[1] + "\" of argument \"" + options[0] + "\" is not a parsable integer.");
			}
		}else{
			throw new Exception("Argument \"" + options[0] + "\" takes one option. Try \"-h\" to display help.");
		}
		return(value);
	}
	
	int parseIntegerPositive(String[] options) throws Exception{
		int value = 0;

		if(options.length == 2){
			try{
				value = Integer.parseInt(options[1]);
				if(value < 0){
					throw new Exception("\"" + options[1] + "\" of argument \"" + options[0] + "\" is must be a positive integer.");
				}
			}catch(NumberFormatException e){
				throw new Exception("\"" + options[1] + "\" of argument \"" + options[0] + "\" is not a parsable integer.");
			}
		}else{
			throw new Exception("Argument \"" + options[0] + "\" takes one option. Try \"-h\" to display help.");
		}
		return(value);
	}

	float parseFloatPositive(String[] options) throws Exception{
		float value = 0F;

		if(options.length == 2){
			try{
				value = Float.parseFloat(options[1]);
				if(value < 0){
					throw new Exception("\"" + options[1] + "\" of argument \"" + options[0] + "\" is must be a positive float.");
				}
			}catch(NumberFormatException e){
				throw new Exception("\"" + options[1] + "\" of argument \"" + options[0] + "\" is not a parsable float.");
			}
		}else{
			throw new Exception("Argument \"" + options[0] + "\" takes one option. Try \"-h\" to display help.");
		}
		return(value);
	}

	String parseString(String[] options) throws Exception{
		String value = "";

		if(options.length == 2){
			value = options[1];
		}else{
			throw new Exception("Argument \"" + options[0] + "\" takes one option. Try \"-h\" to display help.");
		}
		return(value);
	}

	int[] parseIntegerArray(String[] options) throws Exception{
		int[] value = null;

		if(options.length >= 2){
			value = new int[options.length - 1];
			for(int numOption = 1; numOption < options.length; numOption++){
				try{
						value[numOption - 1] = Integer.parseInt(options[numOption]);
				}catch(NumberFormatException e){
					throw new Exception("\"" + options[numOption] + "\" of argument \"" + options[0] + "\" is not a parsable integer.");
				}
			}
		}else{
			throw new Exception("Argument \"" + options[0] + "\" takes one or more options. Try \"-h\" to display help.");
		}
		return(value);
	}

	int[] parseIntegerArray(String[] options, int numOptions) throws Exception{
		int[] value = null;

		if(options.length == numOptions+1){
			value = new int[options.length - 1];
			for(int numOption = 1; numOption < options.length; numOption++){
				try{
						value[numOption - 1] = Integer.parseInt(options[numOption]);
				}catch(NumberFormatException e){
					throw new Exception("\"" + options[numOption] + "\" of argument \"" + options[0] + "\" is not a parsable integer.");
				}
			}
		}else{
			throw new Exception("Argument \"" + options[0] + "\" takes " + numOptions +" options. Try \"-h\" to display help.");
		}
		return(value);
	}

	float[] parseFloatPositiveArray(String[] options) throws Exception{
		float[] value = null;

		if(options.length >= 2){
			value = new float[options.length - 1];
			for(int numOption = 1; numOption < options.length; numOption++){
				try{
					value[numOption - 1] = Float.parseFloat(options[numOption]);
					if ( value[numOption - 1] < 0){
						throw new Exception("All of the arguments of " + options[0] + " is must be positive floats.");
					}
				}catch(NumberFormatException e){
					throw new Exception("\"" + options[numOption] + "\" of argument \"" + options[0] + "\" is not a parsable float.");
				}				
			}
		}else{
			throw new Exception("Argument \"" + options[0] + "\" takes one or more options. Try \"-h\" to display help.");
		}
		return(value);
	}

	float[] parseFloatArray(String[] options) throws Exception{
		float[] value = null;

		if(options.length >= 2){
			value = new float[options.length - 1];
			for(int numOption = 1; numOption < options.length; numOption++){
				try{
						value[numOption - 1] = Float.parseFloat(options[numOption]);
				}catch(NumberFormatException e){
					throw new Exception("\"" + options[numOption] + "\" of argument \"" + options[0] + "\" is not a parsable float.");
				}
			}
		}else{
			throw new Exception("Argument \"" + options[0] + "\" takes one or more options. Try \"-h\" to display help.");
		}
		return(value);
	}

	float[] parseFloatArray(String[] options, int numOptions) throws Exception{
		float[] value = null;

		if(options.length == numOptions+1){
			value = new float[options.length - 1];
			for(int numOption = 1; numOption < options.length; numOption++){
				try{
						value[numOption - 1] = Float.parseFloat(options[numOption]);
				}catch(NumberFormatException e){
					throw new Exception("\"" + options[numOption] + "\" of argument \"" + options[0] + "\" is not a parsable float.");
				}
			}
		}else{
			throw new Exception("Argument \"" + options[0] + "\" takes " + numOptions + " options. Try \"-h\" to display help.");
		}
		return(value);
	}


	///////////////////////////
	//ARGUMENTS GET FUNCTIONS//
	///////////////////////////

	public String getImageFile(){
		return(imageFile);
	}
	public String getOutputFile(){
		return(outputFile);
	}
	public int getOutputFileType(){
		return(outputFileType);
	}
	public int[] getImageGeometry(){
		return(imageGeometry);
	}
	public int[] getPart2Flag(){
		return(part2Flag);
	}
	public int[] getPart3Flag(){
		return(part3Flag);
	}
	public int[] getPart4Flag(){
		return(part4Flag);
	}
	public int[] getSegByteLimit(){
		return(segByteLimit);
	}
	public int[] getDCStop(){
		return(DCStop);
	}
	public int[] getBitPlaneStop(){
		return(bitPlaneStop);
	}
	public int[] getStageStop(){
		return(stageStop);
	}
	public int[] getUseFill(){
		return(useFill);
	}
	public int[] getBlocksPerSegment(){
		return(blocksPerSegment);
	}
	public int[] getOptDCSelect(){
		return(optDCSelect);
	}
	public int[] getOptACSelect(){
		return(optACSelect);
	}
	public int[] getWTType(){
		return(WTType);
	}
	public int[] getSignedPixels(){
		return(signedPixels);
	}
	public int[] getTransposeImg(){
		return(transposeImg);
	}
	public int[] getCodeWordLength(){
		return(codeWordLength);
	}
	public int[] getCustomWtFlag(){
		return(customWtFlag);
	}
	public float[] getCustomWeight(){
		return(customWeight);
	}
	public int[] getWTLevels(){
		return(WTLevels);
	}
	public int[] getimageExtensionType(){
		return(imageExtensionType);
	}
	public int[] getWTOrder(){
		return(WTOrder);
	}
	public int[] getGaggleDCSize(){
		return(gaggleDCSize);
	}
	public int[] getGaggleACSize(){
		return(gaggleACSize);
	}
	public int[] getIdDC(){
		return(idDC);
	}	
	public int[] getIdAC(){
		return(idAC);
	}
	public float[] getDesiredDistortion(){
		return desiredDistortion;
	}
	public int[] getEntropyAC(){
		return entropyAC;
	}
	public int[] getDistortionMeasure(){
		return distortionMeasure;
	}
	public int[] getResolutionLevels(){
		return resolutionLevels;
	}
	public float[] getCompressionFactor(){
		return compressionFactor;
	}
	public int[] getPixelBitDepth(){
		return pixelBitDepth;
	}
	public boolean[] getCVerbose(){
		return CVerbose;
	}
	public float[] getBpppb(){
		return bpppb;
	}
	public int[] getTruncationPoints(){
		return truncationPoints;
	}
	public int[] getAdjustHeaderParameters(){
		return adjustHeaderParameters;
	}
	public int getProgressionOrder(){
		return progressionOrder;
	}
	public int getLSType(){
		return LSType;
	}
	public int[] getLSComponents(){
		return LSComponents;
	}
	public int[] getLSSubsValues(){
		return LSSubsValues;
	}
	public int[] getCoefficientsApproximation(){
		return coefficientsApproximation;
	}
	public int[] getTargetBytes(){
		return targetBytes;
	}
	public int getLayerCreationType(){
		return layerCreationType;
	}
	public int getNumberOfLayers(){
		return numberOfLayers;
	}
	public int getLayerSizeType(){
		return layerSizeType;
	}
	public int[] getLayerBytes(){
		return layerBytes;
	}
	public float[] getBitsPerSample(){
		return bitsPerSample;
	}
	public int getTest3d(){
		return this.test3d;
	}
	public int getSpectralWTLevels(){
		return this.spectralWTLevels;
	}
	public int getSpectralWTType(){
		return this.spectralWTType;
	}
	
	////////////////////////////
	//// HELP DOCUMENT /////////
	////////////////////////////
	/**
	 * This function shows arguments information to console using a formatted table in latex (useful to write manuals in latex).
	 */
	public void showArgsInfoLatexTable(){
		for(int numArg = 0; numArg < argsSpecification.length; numArg++){
			System.out.println("\\begin{center}\\begin{tabular}{|rr|rlrl|}");
			String longParam = argsSpecification[numArg][1].replace("-","$-$");
			String shortParam = argsSpecification[numArg][0].replace("-","$-$");
			String paramArguments = argsSpecification[numArg][2].replace("{","$\\{$").replace("}","$\\}$").replace("[","$[$").replace("]","$]$");
			String paramMandatory;
			if(argsSpecification[numArg][4].compareTo("0") == 0){
				paramMandatory = "No";
			}else{
				paramMandatory = "Yes";
			}

			String paramExplanation = argsSpecification[numArg][5].replace("\n","\\newline").replace("\t","\\hspace*{0.5truecm}").replace("    "," ").replace("_","\\_");
			String paramDefault = argsSpecification[numArg][3];

			System.out.println("\\hline\n\\multicolumn{2}{|l|}{\\textbf{" + longParam + "}} & \\multicolumn{4}{|l|}{" + paramArguments + "} \\\\\n\\cline{3-6}");
			System.out.println("\\multicolumn{2}{|l|}{\\textbf{" + shortParam + "}} & \\emph{Mandatory:} & " + paramMandatory + " & &  \\\\\n\\hline");
			System.out.println("\\emph{Explanation:} & \\multicolumn{5}{|p{12cm}|}{" + paramExplanation + "} \\\\\n\\hline");
			System.out.println("\\emph{Default:} & \\multicolumn{5}{|p{12cm}|}{" + paramDefault + "} \\\\\n\\hline");
			System.out.println("\\end{tabular}\\end{center}");
		}
	}
}

