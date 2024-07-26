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
package TER.TERdecoder.Weighting;

import GiciException.*;

/**
 * This class receives an image and inverse weights the specified components.<br>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; set functions<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class InverseWeighting{

	/**
	 * Image samples (index meaning [z][y][x]).
	 * <p>
	 * All values allowed.
	 */
	float[][][] imageSamples = null;

	/**
	 * Definition in {@link TER.TERcoder.Coder#zSize}
	 */
	int zSize;

	/**
	 * Definition in {@link TER.TERcoder.Coder#ySize}
	 */
	int ySize;

	/**
	 * Definition in {@link TER.TERcoder.Coder#xSize}
	 */
	int xSize;

	/**
	 * Definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 */  
	int[] customWtFlag = null;

	/**
	 * Definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 */
	float[][] customWeight = null;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 */
	int[] WTType = null ;
	
	/**
	 * Definition in  {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 */
	int[] WTLevels = null ;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;


	/**
	 * Constructor that receives the recovered image samples.
	 *
	 * @param imageSamples definition in {@link #imageSamples}
	 */
	public InverseWeighting(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;

		//Size set
		zSize = imageSamples.length;
	}



	/**
	 * Set the parameters used to do the inverse weighting operation
	 *
	 * @param  customWtFlag definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 * @param  WTType definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 * @param  WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param  customWeight definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 *
	 */
	public void setParameters(int[] customWtFlag, int[] WTType, int[] WTLevels, float[][] customWeight){
		parametersSet = true;

		//Parameters copy
		this.WTType = WTType;
		this.WTLevels = WTLevels;
		this.customWtFlag = customWtFlag;
		this.customWeight = customWeight;
		
		
	}

	
	/**
	 * return parameter customWtFlag defined in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 *
	 * @return an array of integers containing customWtFlag
	 */
	public int[] getCustomWtFlag(){
		return(this.customWtFlag);
	}
	
	/**
	 * return parameter customWeight defined in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 *
	 * @return an array of floats containing customWeight
	 */
	public float[][] getCustomWeight(){
		return(this.customWeight);
	}

	/**
	 * States if weighting is defined in needed according to the user specifications
	 *
	 * @param  customWtFlag definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 * @param  WTType definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 * @param  zSize definition in {@link TER.TERcoder.Coder#zSize}
	 *
	 * @return a boolean that indicates if the weighting is required
	 */	
	public static boolean setWeightingNeed(int[] customWtFlag, int[] WTType, int zSize){
		boolean needWeighting = false ;	
		for(int z=0 ; z < zSize && !needWeighting ; z++){
			if( customWtFlag[z] == 0 && WTType[z] == 4 ){
				needWeighting = true ; 
			} else if (customWtFlag[z] == 1) {
				needWeighting = true ;
			}
		}
		return needWeighting;
	}

	/**
	 * Finds the weight for a given subbands and channel
	 *
	 * @param subband indicates the subband that requires the weighting factor
	 * @param channel indicates the channel that requires the weighting factor
	 *
	 * @return a float corresponding to the weigth to be applied in the given subband  
	 */
	public float getWeight (int subband, int channel){
		
		float weight=1F;
		if(customWtFlag[channel]==2){
			return 1F;
		} else if(customWtFlag[channel]==0){
			if (WTType[channel]==4){
				int exponent = ( WTLevels[channel] - subband/3 );
				//	System.out.println(Math.pow(2.,exponent) + "  " + subband);
				weight = (float) Math.pow(2.,exponent);
			} else {
				weight = 1F;
			}
		} else if(customWtFlag[channel]==1){
			int index = 0;
			for(int z = 0 ; z < channel ; z++ ){
				if(customWtFlag[z]==1){
					index += 3*WTLevels[z] + 1;
				}
			}
			weight = customWeight[channel][subband];
		}
		
		return weight;
	}

	
	
	/**
	 * For a given component indicates if weigthing must be applied
	 *
	 * @param component indicates the component that requires the weighting factor
	 * 
	 * @return a boolean that indicates whether weighting must be applied in the given subband  
	 */
	public boolean weightingRequired(int component){
		
		if(customWtFlag[component]==2){
			return false;
		} else if(customWtFlag[component]==0 && WTType[component]!=4){
			return false;
		}
											
		return true;
	}
	
	/**
	 * Performs the inverse weighting desired to each component
	 *
	 * @return the inverted weighted image
	 * 
	 * @throws ParameterException when something goes wrong and the inverse weighting must be stopped
	 */
	public float[][][] run() throws ParameterException{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("ImageExtension cannot run if parameters are not correctly set.");
		}
		
		for(int z = 0; z < zSize; z++){
			ySize = imageSamples[z].length;
			xSize = imageSamples[z][0].length;
			boolean needWeighting = weightingRequired(z);
			
			if (needWeighting){
				//Level size
				int xSubBandSize[] = new int[WTLevels[z]+1];
				int ySubBandSize[] = new int[WTLevels[z]+1];

				//Size setting for all levels
				xSubBandSize[WTLevels[z]]=xSize;
				ySubBandSize[WTLevels[z]]=ySize;

				for(int k=WTLevels[z]-1;k>=0;k--){
					xSubBandSize[k] = xSubBandSize[k+1] / 2 + xSubBandSize[k+1] % 2;
					ySubBandSize[k] = ySubBandSize[k+1] / 2 + ySubBandSize[k+1] % 2;
				}
				//Apply Weigths for each subband
				for(int currentLevel = 0; currentLevel < WTLevels[z]; currentLevel++){
					float weight;
				
					//Residual Subband
					if ( currentLevel == 0 ){ 
						weight = getWeight(currentLevel*3+0,z);
						for(int y = 0; y < ySubBandSize[currentLevel]; y++){
							for(int x = 0; x < xSubBandSize[currentLevel]; x++){
								imageSamples[z][y][x] /= weight;  
							}
						}
					}
				
					//HL subband
					weight = getWeight(currentLevel*3+1,z);
					for(int y = 0; y < ySubBandSize[currentLevel]; y++){
						for(int x = xSubBandSize[currentLevel] ; x < xSubBandSize[currentLevel+1] ; x++){
							imageSamples[z][y][x] /= weight;  
						}	
					}
				
					//LH subband
					weight = getWeight(currentLevel*3+2,z);
					for(int y = ySubBandSize[currentLevel] ; y < ySubBandSize[currentLevel+1]; y++){
						for(int x = 0; x < xSubBandSize[currentLevel] ; x++){
							imageSamples[z][y][x] /= weight;  
						}
					}
				
					//HH subband
					weight = getWeight(currentLevel*3+3,z);
					for(int y = ySubBandSize[currentLevel]; y < ySubBandSize[currentLevel+1]; y++){
						for(int x = xSubBandSize[currentLevel]; x < xSubBandSize[currentLevel+1]; x++){
							imageSamples[z][y][x] /= weight;  
						}
					}
				}
			}
		}
		//Return the weighted image
		return(imageSamples);
	}

}
