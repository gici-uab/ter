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
package TER.TERDisplayFrame;
import GiciException.*;
import GiciParser.*;
import TER.TERDefaultValues;

import java.lang.reflect.*;


/**
 * Arguments parser for TER DisplayFrame (extended from ArgumentsParser).
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 2.0
 */
public class DisplayFrameParser extends ArgumentsParser{

	//ARGUMENTS SPECIFICATION
	String[][] argsSpecification = {
			{"-h", "--help", "", "", "0", "1",
				"Displays this help and exits program."
			},
			{"-i", "--inputFile", "{string}", "", "1", "1",
				"Input file."
			},
			{"-rl", "--resolutionLevels", "{int}", TERDefaultValues.displayResolutionLevels+"", "0", "0",
				"Resolution Levels."
			},
			{"-nl", "--numberOfLayers", "{int}",  TER.TERDefaultValues.numberOfLayers+" ", "0", "0",
				"Number of Layers"
			},
			{"-tb", "--targetBytes", "{int}",  " ", "0", "0",
				"target bytes"
			},
			{"-cl", "--channelList", "{int[ int[ int[ ...]]]}",  " ", "0", "0",
				"list of channels that must be decoded"				
			},
			{"-y0", "--yInit", "{int}",  " ", "0", "0",
				"initial position of y dimension"				
			},
			{"-yl", "--yLength", "{int}",  " ", "0", "0",
				"length of the y dimension"				
			},
			{"-x0", "--xInit", "{int}",  " ", "0", "0",
				"initial position of x dimension"				
			},
			{"-xl", "--xLength", "{int}",  " ", "0", "0",
				"length of the x dimension"				
			},
			{"-et", "--extractionType", "{int}",  " ", "0", "0",
				"type of extraction"
			}
	};

	//ARGUMENTS VARIABLES
	String DInFile = "";
	int resolutionLevels = TERDefaultValues.displayResolutionLevels;
	int numberOfLayers =  TER.TERDefaultValues.numberOfLayers;
	int targetBytes = -1;
	int[] channelList = null;
	int yInit = -1;
	int yLength = -1;
	int xInit = -1;
	int xLength = -1;
	int extractionType = 1;
	
	/**
	 * Receives program arguments and parses it, setting to arguments variables.
	 *
	 * @param arguments the array of strings passed at the command line
	 *
	 * @throws ParameterException when an invalid parsing is detected
	 * @throws ErrorException when some problem with method invocation occurs
	 */
	public DisplayFrameParser(String[] arguments) throws ParameterException, ErrorException{
		try{
			Method m = this.getClass().getMethod("parseArgument", new Class[] {int.class, String[].class});
			parse(argsSpecification, arguments, this, m);
		}catch(NoSuchMethodException e){
			throw new ErrorException("Frame parser error invoking parse function.");
		}
	}

	/**
	 * Parse an argument using parse functions from super class and put its value/s to the desired variable. This function is called from parse function of the super class.
	 *
	 * @param argFound number of parameter (the index of the array eyeFrameArguments)
	 * @param options the command line options of the argument
	 *
	 * @throws ParameterException when some error about parameters passed (type, number of params, etc.) occurs
	 */
	public void parseArgument(int argFound, String[] options) throws ParameterException{
		switch(argFound){
		case 0: //-h  --help
			System.out.println("TER Display Frame 1.0");
			showArgsInfo();
			//showArgsInfoLatexTable();
			System.exit(0);
			break;
		case  1: //-i  --inputFile
			DInFile = parseString(options);
			break;
		case 2: //-rl --resolutionLevels
			resolutionLevels = parseIntegerPositive(options);
			break;
		case 3: //-ln --numberOfLayers
			numberOfLayers = parseIntegerPositive(options);
			break;
		case 4: //-tb --targetBytes
			targetBytes = parseIntegerPositive(options);
			break;	
		case 5: //-cl --channelList
			channelList = parseIntegerArray(options);
			break;
		case 6: //-y0 --yInit
			yInit = parseIntegerPositive(options);
			break;
		case 7: //-yl --yLength
			yLength = parseIntegerPositive(options);
			break;
		case 8: //-x0 --xInit
			xInit = parseIntegerPositive(options);
			break;
		case 9: //-xl --xLength
			xLength = parseIntegerPositive(options);
			break;
		case 10: //-et --extractionType
			extractionType = parseIntegerPositive(options);
			break;	
		}
		
	}

	//ARGUMENTS GET FUNCTIONS
	public String getDInFile(){
		return(DInFile);
	}
	public int getResolutionLevels(){
		return resolutionLevels;
	}
	public int getNumberOfLayers(){
		return numberOfLayers;
	}
	public int getTargetBytes(){
		return targetBytes;
	}
	public int[] getChannelList(){
		return channelList;
	}
	public int getYInit(){
		return yInit;
	}
	public int getYLength(){
		return yLength;
	}
	public int getXInit(){
		return xInit;
	}
	public int getXLength(){
		return xLength ;
	}
	public int getExtractionType(){
		return extractionType;
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

