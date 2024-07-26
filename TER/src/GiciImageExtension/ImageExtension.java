/*
 * GICI Library -
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
 * gici-info@deic.uab.es
 */
package GiciImageExtension;

import GiciException.*;


/**
 * This class receives an image and performs an extension to the specified components.<br>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; set functions<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.1
 */
public class ImageExtension{
	
	/**
	 * Original image samples (index meaning [z][y][x]).
	 * <p>
	 * All values allowed.
	 */
	float[][][] imageSamples = null;

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
	 * Extension Type for each of the channels.
	 * <p>
	 * Following values are allowed : 0 - Repeat last value (CCSDS Recommended). 1 - Symmetric extension. 2. - No Extension. 
	 */
	int[] imageExtensionType = null;

	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 */
	int[] WTLevels = null ;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;

	/**
	 * Number of 'padding' rows to delete after inverse DWT for each component
	 * <p>
	 * Negative values are not allowed.
	 */
	int[] padRows = null;
	
	/**
	 * Constructor that receives the original image samples.
	 *
	 * @param imageSamples definition in {@link #imageSamples}
	 */
	public ImageExtension(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;

		//Size set
		zSize = imageSamples.length;
		ySize = imageSamples[0].length;
		xSize = imageSamples[0][0].length;
	}

	/**
	 * Set the parameters used to do the image extension
	 *
	 * @param imageExtensionType definition in {@link #imageExtensionType}
	 * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 */
	public void setParameters(int[] imageExtensionType, int[] WTLevels){
		parametersSet = true;

		//Parameters copy
		this.imageExtensionType = imageExtensionType;
		this.WTLevels = WTLevels;
	}
  

	/**
	 * Indicates whether image extension must be applied 
	 *
	 * @param  imageExtensionType definition in {@link #imageExtensionType}
	 * @param  WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param  xSize integer containing the width of the original image
	 * @param  ySize integer containing the height of the original image
	 * @param  zSize number of components
	 *
	 * @return a boolean that indicates if extension must be performed
	 */
	public static boolean needImageExtension(int[] imageExtensionType, int[] WTLevels,
														  int xSize, int ySize, int zSize){
		
		boolean need = false;
		for(int z=0 ; z < zSize  ; z++){
			if ( imageExtensionType[z]!=2 && WTLevels[z]!=0){
				int requiredLines = ((int) 1 << WTLevels[z]); //2^(WTLevels[z]) 
				int requiredColumns = ((int) 1 << WTLevels[z]); //2^(WTLevels[z]) 
				if( ySize%requiredLines!=0 || xSize%requiredColumns!=0 ){
					need =  true ; 
				}
			}
		}
			
		return need;
	}
	
	/**
	 * Finds the index to extend a vector according to the type selected
	 *
	 * @param indexToExtend index of the extended position
	 * @param originalSize size of the original signal
	 * @param extensionType type of extension
	 *
	 * @return an integer corresponding to the index in the original of the position that is located the value of the extended position  
	 */
	public int getExtendedIndex (int indexToExtend, int originalSize, int extensionType){
		int index = -1;
		if ( indexToExtend >=0 && indexToExtend < originalSize ){
			index = indexToExtend;
		} else if(extensionType == 0){
			index = originalSize - 1 ;
		} else if (extensionType == 1){
			index = indexToExtend;
			while ( index<0 || index >= originalSize ){
				if ( index<0 ){
					index = -index;
				} else { // index >= originalSize
					index = (2*(originalSize - 1) ) - index;
				}					
			}
		} 
		return index;
	}

	 
	/**
	 * Performs the extension desired to each component
	 * 
	 * @return a float array containing the extended image
	 * 
	 * @throws ParameterException when an invalid or unrecognized exception is detected it will be thrown
	 */
	public float[][][] run() throws ParameterException{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("ImageExtension cannot run if parameters are not set.");
		}
		this.padRows = new int[zSize];
		float[][][] extendedImage = new float[zSize][][];
		for(int z=0; z < zSize ; z++){
			int linesToAdd = 0;
			int columnsToAdd = 0;
			if (imageExtensionType[z]!=2 && WTLevels[z]!=0 ){ //extension is needed
				int requiredLines = ((int) 1 << WTLevels[z]); //2^(WTLevels[z]) 
				int requiredColumns = ((int) 1 << WTLevels[z]); //2^(WTLevels[z])
				if( ySize%requiredLines!=0){ 
					linesToAdd = requiredLines - ySize%requiredLines;
				}
				if( xSize%requiredColumns!=0 ){
					columnsToAdd = requiredColumns - xSize%requiredColumns;
				}
			}
				
			int extendedySize = ySize + linesToAdd ;
			int extendedxSize = xSize + columnsToAdd ;
			padRows[z] = linesToAdd; //number of padding rows added to perform the extension
			extendedImage[z] = new float[extendedySize][extendedxSize];
						
			//extension
			for(int y=0; y < ySize ; y++ ) {
				for (int x=0; x < xSize ; x++ ){
					extendedImage[z][y][x] = imageSamples[z][y][x];
				}
	
				//horizontal extension
				
				for(int x=xSize ; x < extendedxSize ; x++ ){
					int index = getExtendedIndex(x, xSize, imageExtensionType[z]);
					extendedImage[z][y][x] = imageSamples[z][y][index];											
				}
			}
					
			//vertical extension
			for(int y=ySize; y < extendedySize ; y++ ) {
				for(int x=0 ; x < extendedxSize ; x++ ){
					int indexX = getExtendedIndex(x, xSize, imageExtensionType[z]);
					int indexY = getExtendedIndex(y, ySize, imageExtensionType[z]);
					extendedImage[z][y][x] = imageSamples[z][indexY][indexX];											
				}
			}
		}
	
		return(extendedImage);
	}

	/**
	 * @return an integer array that indicates how many rows have been added for each channel
	 */
	public int[] getPadRows(){
		return this.padRows;
	}	
}
