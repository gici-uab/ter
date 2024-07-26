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
package GiciTransform;

import GiciAnalysis.ImageStatistical;
import GiciException.*;

public class CropChannels{
	
	/**
	 * Definition in Coder
	 */
	float[][][] imageSamples = null;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	int cropType;
	boolean removeBand[] = null;
	
	/**
	 * Constructor that receives the original image samples.
	 *
	 * @param imageSamples definition in Coder
	 */
	public CropChannels(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;
	}
	
	/**
	 * Set the parameter that specifies the type of approximation
	 * 
	 * 
	 */
	public void setParameters(int cropType){
		
		this.cropType = cropType;
		
		parametersSet = true;
	}
	
	public float[][][] run() throws ErrorException {
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ErrorException("Crop channels cannot run if parameters are not set.");
		}
		float croppedImage[][][] = null;
		
		if (cropType==1){
			ImageStatistical is = new ImageStatistical(imageSamples);
			double[] energy = is.getEnergy();
			int zCount=0;
			removeBand = new boolean[energy.length];
			for(int z=0;z<energy.length;z++){
				if (energy[z]>0){
					removeBand[z] = false;
					zCount++;
				} else {
					removeBand[z] = true;
				}
			}
			
			croppedImage = new float[zCount][][];
			
			zCount=0;
			for(int z=0;z<energy.length && zCount<croppedImage.length;z++){
				if (!removeBand[z]){
					int ySize = imageSamples[z].length;
					int xSize = imageSamples[z][0].length;
					croppedImage[zCount] = new float[ySize][xSize];
					for(int y=0;y<ySize;y++){
						for(int x=0;x<xSize;x++){
							croppedImage[zCount][y][x] = imageSamples[z][y][x];
						}
					}
					zCount++;
				} 
			}
			imageSamples = null;
		} else {
			croppedImage = imageSamples;
		}
		
		return croppedImage;
	}
	
	public boolean[] getRemoveBand(){
		return removeBand;
	}
}


