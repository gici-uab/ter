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

import GiciException.*;

public class AddChannels{
	
	
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
	
	int addType;
	
	boolean removedBand[] = null;
	
	int zOriginalSize;
	
	/**
	 * Constructor that receives the original image samples.
	 *
	 * @param imageSamples definition in Coder
	 */
	public AddChannels(float[][][] imageSamples, int zOriginalSize){
		//Image data copy
		this.imageSamples = imageSamples;
		this.zOriginalSize = zOriginalSize;
	}
	
	public void setParameters(int addType, boolean[] removedBand){
		
		this.addType = addType;
		this.removedBand = removedBand;
		
		parametersSet = true;
	}
	
	public float[][][] run() throws ErrorException {
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ErrorException("Add channels cannot run if parameters are not set.");
		}
		float completedImage[][][] = null;
		
		if (addType==1){
			int zCount=0;
			int ySize = imageSamples[0].length;
			int xSize = imageSamples[0][0].length;
			completedImage = new float[zOriginalSize][ySize][xSize];
			for(int z=0; z<zOriginalSize;z++){
				if (removedBand[z]){
					for(int y=0;y<ySize;y++){
						for(int x=0;x<xSize;x++){
							completedImage[z][y][x] = 0;
						}
					}
				} else {
					for(int y=0;y<ySize;y++){
						for(int x=0;x<xSize;x++){
							completedImage[z][y][x] = imageSamples[zCount][y][x];
						}
					}
					zCount++;
				}
			}
			imageSamples = null;
		} else {
			completedImage = imageSamples;
		}
		
		return completedImage;
	}
}

