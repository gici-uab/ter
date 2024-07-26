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

/**
 * This class receives an image and approximates its coefficients.<br>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.1
 */

public class CoefficientsApproximation{
	/**
	 * Definition in Coder
	 */
	float[][][] imageSamples = null;
	

	/**
	 * Approximation to be applied to coefficients of each channel.
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - Cast to integer
	 *     <li> 1 - Round
	 *     <li> 2 - Floor
	 *     <li> 3 - Ceil
	 *   </ul>
	 */
	int[] approximationTypes = null;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * Constructor that receives the original image samples.
	 *
	 * @param imageSamples definition in Coder
	 */
	public CoefficientsApproximation(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;
	}
	
	/**
	 * Set the parameter that specifies the type of approximation
	 * 
	 * @param approximationTypes definition in {@link #approximationTypes}
	 */
	public void setParameters(int[] approximationTypes){
		if (approximationTypes!=null){
			this.approximationTypes = approximationTypes;
		} else {
			this.approximationTypes = new int[imageSamples.length];
		}
		parametersSet = true;
	}
	
	/**
	 * Performs the approximation for each of the coefficients of the transformed image
	 * 
	 * @return the image with the coefficients approximated
	 * 
	 * @throws ErrorException when parameters are not set
	 */
	public float[][][] run() throws ErrorException {
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ErrorException("Coefficent approximation cannot run if parameters are not set.");
		}
		
		int zSize = this.imageSamples.length;
		for(int z=0; z<zSize ; z++){
			
				int ySize = imageSamples[z].length;
				int xSize = imageSamples[z][0].length;
				if (this.approximationTypes[z]==0){
					// values are casted to an integer value
					for(int y=0; y<ySize ; y++){
						for(int x=0; x<xSize ; x++){
							imageSamples[z][y][x]= (float) (int)(imageSamples[z][y][x]);
						}
					}
				} else if (this.approximationTypes[z]==1){
					// values are rounded to the nearest integer value
					for(int y=0; y<ySize ; y++){
						for(int x=0; x<xSize ; x++){
							imageSamples[z][y][x]= (float) Math.round(imageSamples[z][y][x]);
						}
					}
				} else if (this.approximationTypes[z]==2){
					//values are floored to the largest integer smaller than the value
					for(int y=0; y<ySize ; y++){
						for(int x=0; x<xSize ; x++){
							imageSamples[z][y][x]= (float) Math.floor(imageSamples[z][y][x]);
						}
					}	
				} else if (this.approximationTypes[z]==3){
					//values are ceiled to the smallest integer larger than the value
					for(int y=0; y<ySize ; y++){
						for(int x=0; x<xSize ; x++){
							imageSamples[z][y][x]= (float) Math.ceil(imageSamples[z][y][x]);
						}
					}	
				}
			
		}
		return imageSamples;
	}
	
	/**
	 * States if approximation is needed according to the user specifications and the requirements of the TER software 
	 * 
	 * @param approximationTypes definition in {@link #approximationTypes}
	 * 
	 * @return a boolean that indicates if the approximation is required
	 */
	public static boolean TERsetApproximationNeed(int[] approximationTypes){
		boolean required = false;
		if (approximationTypes!=null){
			for(int z=0;z<approximationTypes.length && !required;z++){
				if (approximationTypes[z]>0){
					required = true;
				}
			}
		}
		return required;
	}
}
