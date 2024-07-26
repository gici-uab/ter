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
import GiciAnalysis.*;


/**
 * This class receives an image and performs level shift operations. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 * &nbsp; get functions<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class LevelShift{

	/**
	 * 
	 */
	float[][][] imageSamples = null;

	/**
	 * 
	 */
	int zSize;

	/**
	 * 
	 */
	int ySize;

	/**
	 * 
	 */
	int xSize;

	/**
	 * Shift type to apply.
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - No level shift
	 *     <li> 1 - JPEG2000 standard level shifting (only non-negative components)
	 *     <li> 2 - Range center substract
	 *     <li> 3 - Average substract
	 *     <li> 4 - Specific values substract (if this case is selectioned, then is necessary to pass LSSubsValues in setParameters function)
	 *   </ul>
	 */
	int LSType;

	/**
	 * Specification of the image component to apply the level shift.
	 * <p>
	 * If true the level shift will be applied, otherwise false.
	 */
	boolean[] LSComponents = null;

	/**
	 * Substracted values of each image component.
	 * <p>
	 * If LSType == 4 then this variable cannot be null, otherwise it can be null in setParameters function
	 */
	int[] LSSubsValues = null;

	/**
	 * 
	 */
	int[] QComponentsBits = null;

	//INTERNAL VARIABLES

	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;


	/**
	 * Constructor that receives the original image samples.
	 *
	 * @param imageSamples 
	 */
	public LevelShift(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;

		//Size set
		zSize = imageSamples.length;
		ySize = imageSamples[0].length;
		xSize = imageSamples[0][0].length;
	}

	/**
	 * Set the parameters used to do the level shift operation.
	 *
	 * @param LSType definition in {@link #LSType}
	 * @param LSComponents definition in {@link #LSComponents}
	 * @param LSSubsValues definition in {@link #LSSubsValues}
	 * @param QComponentsBits 
	 */
	public void setParameters(int LSType, boolean[] LSComponents, int[] LSSubsValues, int[] QComponentsBits){
		parametersSet = true;

		//Parameters copy
		this.LSType = LSType;
		this.LSComponents = LSComponents;
		this.LSSubsValues = LSSubsValues;
		this.QComponentsBits = QComponentsBits;
	}

	/**
	 * Performs the level shift operations and returns the result image.
	 *
	 * @return the level shifted image
	 *
	 * @throws ErrorException when parameters are not set or unrecognized level shift type is passed
	 */
	public float[][][] run() throws ErrorException{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ErrorException("Parameters not set.");
		}

		if(LSType != 0){
			//Memory allocation
			if(LSSubsValues == null){
				LSSubsValues = new int[zSize];
			}

			ImageStatistical imageStatistics = null;

			//Calculus of substracted values depending on the chosen method
			switch(LSType){
			case 0: //nothing
				for(int z = 0; z < zSize; z++){
					LSSubsValues[z] = 0;
				}
				break;
			case 1: //BOI standard level shifting
				for(int z = 0; z < zSize; z++){
					LSSubsValues[z] = (int) Math.pow(2D, QComponentsBits[z] - 1);
				}
				break;
			case 2: //Range center substract
				imageStatistics = new ImageStatistical(imageSamples);
				//float[][] minMax = imageStatistics.getMinMax();
				//float[] average = imageStatistics.getAverage();
				double[] centerRange = imageStatistics.getCenterRange();
				for(int z = 0; z < zSize; z++){
					LSSubsValues[z] = (int) Math.round(centerRange[z]);
				}
				break;
			case 3: //Average substract
				imageStatistics = new ImageStatistical(imageSamples);
				//float[][] minMax = imageStatistics.getMinMax();
				double[] average = imageStatistics.getAverage();
				//float[] centerRange = imageStatistics.getCenterRange();
				for(int z = 0; z < zSize; z++){
					LSSubsValues[z] = (int) Math.round(average[z]);
				}
				break;
			case 4: //Specific values substract
				break;
			default:
				throw new ErrorException("Unrecognized level shift type.");
			}

			//Apply shift
			for(int z = 0; z < zSize; z++){
				if(LSComponents[z]){
					if(LSSubsValues[z] != 0){
						for(int y = 0; y < ySize; y++){
						for(int x = 0; x < xSize; x++){
							imageSamples[z][y][x] -= LSSubsValues[z];
						}}
					}
				}else{
					LSSubsValues[z] = 0;
				}
			}
		}

		//Return of level shifted image
		return(imageSamples);
	}

	/**
	 * @return LSSubsValues definition in {@link #LSSubsValues}
	 */
	public int[] getSubsValues(){
		return(LSSubsValues);
	}

}
