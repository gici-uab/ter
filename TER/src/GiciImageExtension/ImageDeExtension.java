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
 * This class receives an image and delete the files and columns extended during compression process.<br>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; set functions<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class ImageDeExtension{

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
	 * Image width of the original image for each of the channels.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int imageWidth[] = null;
	
	/**
	 * Definition in {@link GiciImageExtension.ImageExtension#padRows}
	 */
	int padRows[] = null;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;


	/**
	 * Constructor that receives the original image samples.
	 *
	 * @param imageSamples definition in {@link #imageSamples}
	 */
	public ImageDeExtension(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;

		//Size set
		zSize = imageSamples.length;
		
		
	
	}

	
	/**
	 * Set the parameters used to invert the image extension
	 * 
	 * @param imageWidth definition in {@link #imageWidth}
	 * @param padRows definition in {@link #padRows}
	 */
	public void setParameters(int imageWidth[], int padRows[]){
		parametersSet = true;

		//Parameters copy
		this.imageWidth = imageWidth;
		if (padRows!=null){
			this.padRows = padRows;
		} else {
			this.padRows = new int[zSize];
			for(int z=0;z<zSize;z++){
				this.padRows[z]=0;
			}
		}
	}
  

	/**
	 * Inversion of the extension 
	 *
	 * @return the deExtended image
	 */
	public float[][][] run() throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("ImageDeExtension cannot run if parameters are not set.");
		}

		float[][][] cropedImage = null;
		
		
		cropedImage = new float[zSize][][];
		

		for(int z=0; z < zSize ; z++){
			int ySize = imageSamples[z].length;
			int yOriginalSize = ySize - padRows[z];
			int xOriginalSize = this.imageWidth[z];

			cropedImage[z] = new float[yOriginalSize][xOriginalSize];
			if( ySize < yOriginalSize ){//given sizes are not proper to crop the image
				throw new ParameterException("yOriginalSize parameter was not properly set.");
			}
			for(int y=0; y < yOriginalSize ; y++ ) {
				int xSize = imageSamples[z][y].length;
				if( xSize < xOriginalSize ){//given sizes are not proper to crop the image
					throw new ParameterException("xOriginalSize parameter was not properly set.");
				}
				for (int x=0; x < xOriginalSize ; x++ ){
					cropedImage[z][y][x] = imageSamples[z][y][x];
				}
			}
		}
		
		//Return the croped image
		return(cropedImage);
	}
	
	/**
	 * Determines if the image should be cropped in order to recover its initial size  
	 * 
	 * @param padRows definition in {@link #padRows} 
	 * @param imageWidth definition in {@link #imageWidth}
	 * @param xSizes indicates the size of each channel after reconstruction. 
	 * 
	 * @return a boolean that indicates if ImageDeExtension is required.  
	 */
	public static boolean needDeExtension(int[] padRows, int[] imageWidth, int[] xSizes){
		boolean neededExtension = false;
		if (xSizes!=null){
			int zSize = xSizes.length;
			if (padRows==null){
				padRows = new int[zSize];
				for(int z=0;z<zSize;z++){
					padRows[z]=0;
				}
			}
			
			for(int z=0;z<zSize;z++){
				if (padRows[z]!=0 || imageWidth[z]!=xSizes[z]){
					neededExtension = true;
				}
			}
		}
		return neededExtension;
	}

	/**
	 * Computes the number of padding rows that has been added to the original image
	 * 
	 * @param zSize definition in {@link #zSize}
	 * @param ySize image height of the original image
	 * @param imageExtensionType definition in {@link GiciImageExtension.ImageExtension#imageExtensionType}
	 * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * 
	 * @return an intger array containing the number of padding rows to delete after inverse DWT
	 */
	public static int[] computePadRows(int zSize, int ySize, int imageExtensionType[], int WTLevels[]){
		int padRows[] = new int[zSize];
		for(int z=0; z < zSize ; z++){
			int linesToAdd = 0;
			if (imageExtensionType[z]!=2 && WTLevels[z]!=0 ){ //extension is needed
				int requiredLines = ((int) 1 << WTLevels[z]); //2^(WTLevels[z]) 
				if( ySize%requiredLines!=0){ 
					linesToAdd = requiredLines - ySize%requiredLines;
				}
			}
			padRows[z] = linesToAdd; //number of padding rows added to perform the extension
		}
		return padRows;
	}
}
