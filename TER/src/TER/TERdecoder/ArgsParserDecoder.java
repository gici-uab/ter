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


/**
 * Arguments parser for TER decoder. This class analyses a string of arguments and extract and check its validity.
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; [showArgsInfo]<br>
 * &nbsp; [get functions]<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 2.0
 */
public class ArgsParserDecoder{
	
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
		{"-o", "--outputImage", "{string}", "", "1",
			"Decoded image. Valid formats are: pgm, ppm, pbm, jpg, tiff, png, bmp, gif, fpx. If image is raw data file extension must be \".raw\" and \"-g\" parameter is mandatory."
		},
		{"-g", "--imageGeometry", "{int int int int int boolean}", "", "0",
			"Geometry of raw image data. Parameters are:\n    1- zSize (number of image components)\n    2- ySize (image height)\n    3- xSize (image width)\n    4- data type. Possible values are:\n \t 0- boolean (1 byte)\n \t 1- unsigned int (1 byte)\n \t 2- unsigned int (2 bytes)\n \t 3- signed int (2 bytes)\n \t 4- signed int (4 bytes)\n \t 5- signed int (8 bytes)\n \t 6- float (4 bytes)\n \t 7- double (8 bytes)\n    5- Byte order (0 if BIG ENDIAN, 1 if LITTLE ENDIAN)\n    6- 1 if 3 first components are RGB, 0 otherwise."
		},
		{"-i", "--inputFile", "{string}", "", "1",
			"Input encoded image file name."
		},
		//Recommendation Parameters.
		{"-bl", "--segByteLimit", "{int[ int[ int[ ...]]]}",  TER.TERDefaultValues.segByteLimit+" ", "0",
			"Maximum number of bytes that can be used in a segment.\n The value of SegByteLimits includes bytes used for the header, and is applied to the subsequent segments until a new value is given. Value should be in the interval (0 , 134217728]"
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
		{"-vc", "--verboseComputation", "{boolean}", (TER.TERDefaultValues.CVerbose[0] ? "1": "0") + " ", "0",
			"Show some information about time and used memory for each compression stage. Value is a boolean: 0 indicates NO show and 1 indicates show."
		},
		{"-vp", "--verboseParameters", "{boolean}", (TER.TERDefaultValues.CVerbose[1] ? "1": "0") + " ", "0",
			"Show TER encoding parameters. Value is a boolean: 0 indicates NO show and 1 indicates show."
		},
		{"-vm", "--verboseMessages", "{boolean}", (TER.TERDefaultValues.CVerbose[1] ? "1": "0") + " ", "0",
			"Show TER decoder messages, for instance if the file ends unexpectedly. Value is a boolean: 0 indicates NO show and 1 indicates show."
		},
		{"-gv", "--gammaValue", "{float[ float[ float[ ...]]]}", TER.TERDefaultValues.gammaValue + " ", "0",
			"Float array that indicates for each channel the way of recovering the values. First value is for the first channel, second value for the second channel and so on. If only one value is specified, it will be the same for all channels.\n This float indicates the point of the interval where values are recovered in the decoding process. When decoding a value inside an interval is possible to approximate this value to the middle value of the interval, to the low value, to 0,25 of the interval, to ... \n Example: in bit plane 3, a recovered value can be approximated to 8 (low value) or to 12 (middle value). \n Valid values are real numbers in the interval [0,1). \n For example: \n   0.0 - Lower value (gamma = 0) \n   0.5 - Middle value (gamma = 1/2) \n   0.375 - 3/8 of the threshold value (gamma = 3/8)"
		},
		{"-cm", "--completionMode", "{int[ int[ int[ ...]]]}", TER.TERDefaultValues.completionMode + " ", "0",
			"Integer array that indicates for each channel the way of completing DC values when the bitstream ends unexpectedly. First value is for the first channel, second value for the second channel and so on. If only one value is specified, it will be the same for all channels. \n Valid values are:\n 0 - Add zeros \n 1 - Repeat the middle value of the magnitude interval where the components are being decoded, i.e., (max(-xMin,xMax)+1)/2. \n 2 - Repeat the mean of the decoded values \n n - For n>2, repeat the mean value of the n-2 last recovered values. If less than n-2 values have been recovered, only the recovered values will be used. Note that for n=3 we repeat the last value."
		},
		{"-mh", "--minusHalf", "{boolean}", TER.TERDefaultValues.minusHalf + " ", "0",
			"Boolean value that indicated if the user wants to substract 0.5 to the recovered values in the bit plane encoder, as Recomendation does for the lossy case."
		},
		{"-t3", "--test3d", "{int}", " ", "0",
			"Kind of 3D Discrete wavelet transform to be applied. Valid values are:\n 0 - 2D DWT in the spatial domain  \n 1 - 1D + 2D hybrid  DWT \n 2 - 3D pyramidal DWT"
		},
		{"-swl", "--spectralWTLevels", "{int}", " ", "0",
			"Discrete wavelet transform levels to be applied in the spatial domain."
		},
		{"-swt", "--spectralWTType", "{int}", " ", "0",
			"Discrete wavelet transform type to be applied in the spatial domain."
		},
		{"-rp", "--rangeRecoveredPixels", "{boolean}", TER.TERDefaultValues.rangeRecoveredPixels + " ", "0",
			"A boolean that indicates if recovered pixels must be ranged and rounded or not."}	
	};

	//ARGUMENTS VARIABLES
	String imageFile= null;
	String inputFile = null;
	int[] imageGeometry = null;
	int[] segByteLimit = null;
	int[] DCStop = null;
	int[] bitPlaneStop = null;
	int[] stageStop = null;
	int[] distortionMeasure = null;
	float[] compressionFactor = null;
	boolean[] CVerbose = new boolean[3];
	float[] gammaValue = null;
	int[] completionMode = null;
	boolean minusHalf = TER.TERDefaultValues.minusHalf;
	boolean rangeRecoveredPixels = TER.TERDefaultValues.rangeRecoveredPixels;
	int test3d = 0;
	int spectralWTLevels = 0;
	int spectralWTType = 0;
	
	/**
	 * Class constructor that receives the arguments string and initializes all the arguments.
	 *
	 * @param args the array of strings passed at the command line
	 *
	 * @throws Exception when an invalid parsing is detected or some problem with method invocation occurs
	 */
	public ArgsParserDecoder(String[] args) throws Exception{
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
					case  1: //-o  --outputImage
						imageFile = parseString(options);
						break;
					case  2: //-g  --imageGeometry
						imageGeometry = parseIntegerArray(options, 6);
						break;
					case  3: //-i  --inputFile
						inputFile = parseString(options);
						break;
					case  4: //-bl --SegByteLimit
						segByteLimit = parseIntegerArray(options);
						break;
					case  5: //-dc --DCStop
						DCStop = parseIntegerArray(options);
						break;
					case  6: //-bp --bitPlaneStop
						bitPlaneStop = parseIntegerArray(options);
						break;
					case  7: //-ss --StageStop
						stageStop = parseIntegerArray(options);
						break;
					case  8://-vc, --verboseComputation
						CVerbose[0] = parseBoolean(options);
						break;
					case  9://-vp, --verboseParameters
						CVerbose[1] = parseBoolean(options);
						break;
					case  10://-vm, --verboseMessagess
						CVerbose[2] = parseBoolean(options);
						break;
					case  11: //-gv --gammaValue
						gammaValue = parseFloatArray(options);
						break;
					case  12: //-cm --completionMode
						completionMode = parseIntegerArray(options);
						break;
					case  13://"-mh", "--minusHalf", "{boolean}"
						minusHalf = parseBoolean(options);
						break;
					case  14://-t3 --test3d
						test3d = parseIntegerPositive(options); 
						break;	
					case  15://-swl --spectralWTLevels
						spectralWTLevels = parseIntegerPositive(options); 
						break;	
					case  16://-swt --spectralWTType
						spectralWTType = parseIntegerPositive(options);
						break;
					case  17://"-rp", "--rangeRecoveredPixels", "{boolean}"
						rangeRecoveredPixels = parseBoolean(options);
						break;	
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

	String[] parseStringArray(String[] options) throws Exception{
		String[] value = null;

		if(options.length >= 2){
			value = new String[options.length - 1];
			for(int numOption = 1; numOption < options.length; numOption++){
				try{
						value[numOption - 1] = options[numOption];
				}catch(NumberFormatException e){
					throw new Exception("\"" + options[numOption] + "\" of argument \"" + options[0] + "\" is not a parsable string.");
				}
			}
		}else{
			throw new Exception("Argument \"" + options[0] + "\" takes one or more options. Try \"-h\" to display help.");
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
	public String getInputFile(){
		return(inputFile);
	}
	public int[] getImageGeometry(){
		return(imageGeometry);
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
	public float[] getCompressionFactor(){
		return compressionFactor;
	}
	public boolean[] getCVerbose(){
		return CVerbose;
	}
	public float[] getGammaValue(){
		return gammaValue;
	}
	public int[] getCompletionMode(){
		return completionMode;
	}
	public boolean getMinusHalf(){
		return minusHalf;
	}
	public boolean getRangeRecoveredPixels(){
		return rangeRecoveredPixels;
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

