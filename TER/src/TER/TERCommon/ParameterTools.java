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
package TER.TERCommon;

import GiciException.ParameterException;

public class ParameterTools{
	/**
	 * Set the parameter containing boolean values.
	 *
	 * @param  inputParameter is an array of boolean given by the user
	 * @param  size integer that indicates the number of values to be set
	 * @param  defaultValue integer that contains default value
	 *
	 * @return a boolean array containing the required amount values to perform the coding process
	 */
	public static boolean[] setParameterBoolean(boolean[] inputParameter, int size, boolean defaultValue) throws ParameterException{
		boolean [] parameter;
		if( inputParameter != null){
			if ( inputParameter.length <= size ){
				parameter = new boolean[size];
				for(int k=0; k<inputParameter.length ; k++){//copy of common indices
					//if the number user parameters is higher than the number 
					//of parameters, last vaules given are discarded
					parameter[k] = inputParameter[k];
				}
				for(int k=inputParameter.length ; k<size ; k++){
					//in case there were not enough values in the parameters given by the  
					//user the last value is repeated as many times as necessary
					parameter[k] = inputParameter[inputParameter.length - 1 ];
				}
			} else{// the amount of parameters given by the user is greater than neeed
				// an exception is thrown
				throw new ParameterException("Too much values have been given for some of the values");
			}
		} else{//default parameters are set
			parameter = new boolean[size];
			for(int k=0; k<size ; k++){
				parameter[k] = defaultValue;
			}
		}
		
		return parameter;
		
	}
	
	/**
	 * Set the parameter.
	 *
	 * @param  inputParameter is an array of integers given by the user
	 * @param  size integer that indicates the number of values to be set
	 * @param  defaultValue integer that contains default value
	 *
	 * @return an integer array containing the required amount values to perform the coding process
	 */
	public static int[] setParameterInt(int[] inputParameter, int size, int defaultValue) throws ParameterException{
		int [] parameter;
		if( inputParameter != null){
			if ( inputParameter.length <= size ){
				parameter = new int[size];
				for(int k=0; k<inputParameter.length ; k++){//copy of common indices
					//if the number user parameters is higher than the number 
					//of parameters, last vaules given are discarded
					parameter[k] = inputParameter[k];
				}
				for(int k=inputParameter.length ; k<size ; k++){
					//in case there were not enough values in the parameters given by the  
					//user the last value is repeated as many times as necessary
					parameter[k] = inputParameter[inputParameter.length - 1 ];
				}
			} else{// the amount of parameters given by the user is greater than neeed
				// an exception is thrown
				throw new ParameterException("Too much values have been given for some of the values");
			}
		} else{//default parameters are set
			parameter = new int[size];
			for(int k=0; k<size ; k++){
				parameter[k] = defaultValue;
			}
		}
		
		return parameter;
		
	}
	
	/**
	 * Set the parameter.
	 *
	 * @param  inputParameter is an array of integers given by the user
	 * @param  size integer that indicates the number of values to be set
	 * @param  defaultValue integer that contains default value
	 *
	 * @return an integer array containing the required amount values to perform the coding process
	 */
	public static int[] setCroppedParameterInt(int[] inputParameter, int size, int defaultValue, boolean[] removedPosition) throws ParameterException{
		int [] parameter = null;
		if (removedPosition==null){
			parameter = setParameterInt(inputParameter,size,defaultValue);
		} else {
			if (removedPosition.length>size){
				parameter = new int[size];
				int zCount=0;
				for(int z=0;z<removedPosition.length && zCount<size;z++){
					if (!removedPosition[z]){
						parameter[zCount] = inputParameter[z];
						zCount++;
					}
				}
			} else {
				int newSize = removedPosition.length - getTrues(removedPosition);
				parameter = setParameterInt(inputParameter,newSize,defaultValue);
			}
		}
		
		return parameter;
		
	}
	
	public static int getTrues(boolean condition[]){
		int count = 0;
		for(int k=0;k<condition.length;k++){
			if(condition[k]){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Set the parameter containing float values.
	 *
	 * @param  inputParameter is an array of floats given by the user
	 * @param  size integer that indicates the number of values to be set
	 * @param  defaultValue integer that contains default value
	 *
	 * @return a float array containing the required amount values to perform the coding process
	 */
	public static float[] setParameterFloat(float[] inputParameter, int size, float defaultValue) throws ParameterException{
		float [] parameter;
		if( inputParameter != null){
			if ( inputParameter.length <= size ){
				parameter = new float[size];
				for(int k=0; k<inputParameter.length ; k++){//copy of common indices
					//if the number user parameters is higher than the number 
					//of parameters, last vaules given are discarded
					parameter[k] = inputParameter[k];
				}
				for(int k=inputParameter.length ; k<size ; k++){
					//in case there were not enough values in the parameters given by the  
					//user the last value is repeated as many times as necessary
					parameter[k] = inputParameter[inputParameter.length - 1 ];
				}
			} else{// the amount of parameters given by the user is greater than neeed
				// an exception is thrown
				throw new ParameterException("Too much values have been given for some of the values");
			}
		} else{//default parameters are set
			parameter = new float[size];
			for(int k=0; k<size ; k++){
				parameter[k] = defaultValue;
			}
		}
		
		return parameter;
		
	}
	
	public static int[][] setParameterMatrix(int[] inputParameter, int blocksPerSegment[][], int defaultValue) throws ParameterException{
		int[][] parameter = null;
		if (blocksPerSegment!=null){
			int zSize = blocksPerSegment.length;
			parameter = new int[zSize][];
			int readedParameters = 0;
			for (int z=0;z<zSize;z++){
				int segmentsPerChannel = blocksPerSegment[z].length;
				parameter[z] = new int[segmentsPerChannel];
				if ( inputParameter != null){
					for(int k=0; k<segmentsPerChannel; k++){
						if (readedParameters<inputParameter.length){
							parameter[z][k] = inputParameter[readedParameters];
						} else {
							parameter[z][k] = inputParameter[inputParameter.length-1];
						}
						readedParameters++;
					}
				} else {
					for(int k=0; k<segmentsPerChannel; k++){
						parameter[z][k] = defaultValue;
					}
				}
			}
		} else {
			throw new ParameterException("blocksPerSegment must be initialized");
		}
		return parameter;
	}
	
	/**
	 * Verify Parameters defined in this class
	 *
	 * @param  inputParameter definition in this class
	 * @param  size number of components
	 * @param  minValue minimum allowed values
	 * @param  maxValue maximum allowed values
	 *
	 * @return a boolean that indicates if the parameters are allowed
	 */
	public static boolean verifyParameter(int[] inputParameter, int size, int minValue, int maxValue){
		
		boolean verified = true; 
		
		if (inputParameter != null){
			if (inputParameter.length <= size ){	
				for(int z=0; z < inputParameter.length ; z++){
					if ( inputParameter[z] < minValue || inputParameter[z] > maxValue){
						verified = false;
					}
				}
			} else {
				verified = false;
			}
		} 
		
		return verified;
		
	}
	
	/**
	 * Prints the numbers contained in an integer array
	 * 
	 * @param parameter array of integers to be printed
	 */
	public static void listIntegerArray(int[] parameter){
		if( parameter != null){
			for(int i=0 ; i<parameter.length ; i++){
				System.out.print(parameter[i] + "\t");
			}
			System.out.println(" ");
		}		
	}
	
	/**
	 * Prints the numbers contained in an integer matrix
	 * 
	 * @param parameter array of integers to be printed
	 */
	public static void listIntegerMatrix(int[][] parameter){
		if (parameter!=null){
			for(int z=0;z<parameter.length;z++){
				listIntegerArray(parameter[z]);		
			}
			System.out.println(" ");
		}
	}
	
	/**
	 * Prints the numbers contained in a float array
	 * 
	 * @param parameter array of floats to be printed
	 */
	public static void listFloatArray(float[] parameter){
		if( parameter != null){
			for(int i=0 ; i<parameter.length ; i++){
				System.out.print(parameter[i] + "\t");
			}
			System.out.println(" ");
		}		
	}	
	
	/**
	 * Prints the numbers contained in a float matrix
	 * 
	 * @param parameter array of floats to be printed
	 */
	public static void listFloatMatrix(float[][] parameter){
		if (parameter!=null){
			for(int z=0;z<parameter.length;z++){
				listFloatArray(parameter[z]);		
			}
			System.out.println(" ");
		}
		
	}
}