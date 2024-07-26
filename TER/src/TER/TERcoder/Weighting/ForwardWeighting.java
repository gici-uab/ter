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
package TER.TERcoder.Weighting;

import GiciException.*;

/**
 * This class receives an image and weights the specified components.<br>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; set functions<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class ForwardWeighting{

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
	 * Weighting type to be applied for each channel..
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - CCSDS-Recommended
	 *     <li> 1 - User defined
	 *     <li> 2 - None
	 *   </ul>
	 */  
	int[] customWtFlag = null;

	/**
	 * Contains the weights defined by the user.
	 * <p>
	 *  Any real value is allowed
	 */
	float[][] customWeight = null;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 */
	int[] WTType = null ;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 */
	int[] WTLevels = null ;
	
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
	public ForwardWeighting(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;

		//Size set
		zSize = imageSamples.length;
		}

	/**
	 * Creates a vector containing user defined weights for the weighting operation
	 *
	 * @param  CustomWtFlag definition in {@link #customWtFlag}
	 * @param  WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param  CustomWeight definition in {@link #customWeight}
	 * @param  zSize number of components
	 *
	 * @return an array of integers containing CustomWeight
	 */
	public static float[][] setCustomWeights(int[] CustomWtFlag,int[] WTLevels, float[] CustomWeight, int zSize){
		
		float[][] weight = new float[zSize][];
		if( CustomWtFlag != null ){
			for(int z=0; z<zSize; z++){
				weight[z] = null;
			}
			int counter = 0;
			int requiredFloats = 0;
			for(int z=0; z<CustomWtFlag.length ; z++){
				if (CustomWtFlag[z] == 1){
					requiredFloats = 3*WTLevels[z]+1;
					weight[z] = new float[requiredFloats];
					for(int k=0; k< requiredFloats ; k++){
						weight[z][k] = CustomWeight[counter+k];
					}
					counter += requiredFloats;
				} else {
					requiredFloats = 0;
				}
			}
			
			counter -= requiredFloats;

			for(int z=CustomWtFlag.length; z<zSize ; z++){
				if (CustomWtFlag[CustomWtFlag.length-1] == 1){
					requiredFloats = 3*WTLevels[CustomWtFlag.length-1]+1;
					weight[z] = new float[requiredFloats];
					for(int k=0; k< requiredFloats ; k++){
						weight[z][k] = CustomWeight[counter+k];
					}
				}
			}
			
 		}

		return weight;

	}
	
	/**
	 * Set the parameters used to do the normalization operation
	 *
	 * @param  customWtFlag definition in {@link #customWtFlag}
	 * @param  WTType definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 * @param  WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param  customWeight definition in {@link #customWeight}
	 *
	 */
	public void setParameters(int[] customWtFlag, int[] WTType,	int[] WTLevels, float[][] customWeight){
		parametersSet = true;

		//Parameters copy
		this.WTType = WTType;
		this.WTLevels = WTLevels;
		this.customWtFlag = customWtFlag;
		this.customWeight = customWeight;
		
		
	}

	/**
	 * return parameter CustomWtFlag defined in {@link #customWtFlag}
	 *
	 * @return an array of integers containing CustomWtFlag
	 */
	public int[] getCustomWtFlag(){
		return(this.customWtFlag);
	}
  /**
	 * return parameter CustomWeight defined in {@link #customWeight}
	 *
	 * @return an array of floats containing CustomWeight
	 */
	public float[][] getCustomWeight(){
		return(this.customWeight);
	}

	/**
	 * States if weighting is needed according to the user specifications
	 *
	 * @param  CustomWtFlag definition in {@link #customWtFlag}
	 * @param  WTType definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 * @param  zSize indicates the number of channels
	 *
	 * @return a boolean that indicates if the weighting is required
	 */	
	public static boolean setWeightingNeed(int[] CustomWtFlag, int[] WTType, int zSize){
			
		boolean required=false;
		for(int z=0 ; z < zSize ; z++){
				if( CustomWtFlag[z] == 0 && WTType[z] == 4 ){
						required=true ; 
				} else if (CustomWtFlag[z] == 1) {
						required=true ;
				}
		}
		return required;
	}

	/**
	 * Verify parameters customWtFlag and customWeight
	 *
	 * @param  CustomWtFlag definition in {@link #customWtFlag}
	 * @param  WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param  CustomWeight definition in {@link #customWeight}
	 * @param  zSize indicates the number of channels
	 *
	 * @return a boolean that indicates if the parameters are allowed
	 */
	public static boolean verifyParameters(int[] CustomWtFlag, int[] WTLevels, float[] CustomWeight, int zSize){
		
		boolean verified=true;
		if (CustomWtFlag != null){
			if ( CustomWtFlag.length > zSize ){
						System.out.println("Parameters given for CustomWtFlag are no allowed. There are too much values");
						verified = false;
			}

			int requiredFloats = 0;
			for(int z = 0 ; z < CustomWtFlag.length; z++ ){
					if (CustomWtFlag[z] == 1){ //in this case the user must provide 3*WTLevels[z]+1 weights
							requiredFloats += 3*WTLevels[z]+1;		
					}
					if ( CustomWtFlag[z]<0 || CustomWtFlag[z]>2 ){
							System.out.println("Parameters given for CustomWtFlag are no allowed. Invalid values");
							verified = false;
					}
			}

			if (CustomWtFlag[CustomWtFlag.length-1] == 1){ 
					for(int z = CustomWtFlag.length ; z < WTLevels.length; z++ ){
							//In case the weigthing type is not defined from a component on,
							//the value of the last component will be given. Then if User defined type
							//has been elected, the number of DWT should be maintained.
							if (WTLevels[CustomWtFlag.length-1] != WTLevels[z]){
									System.out.println("Parameters given for CustomWeight are no allowed. Incorrect number of values");
									verified = false;
							}		
					}
			}
		
			if ( CustomWeight!=null ){
					if(	CustomWeight.length != requiredFloats) {
							System.out.println("Parameters given for CustomWeight are no allowed. Incorrect number of values");
							verified = false;
					}
			} else if ( requiredFloats !=0 ){
						System.out.println("Parameters given for CustomWeight are no allowed. Incorrect number of values");
						verified = false;
			}
		}
		return verified;
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
					//	int exponent = ( WTLevels[channel] - subband/3 );
					//	System.out.println(Math.pow(2.,exponent) + "  " + subband);
					//	weight = (float) Math.pow(2.,exponent);
					weight = (float) ( 1 << ( WTLevels[channel] - subband/3 ) ) ;
				} else {
						weight = 1F;
				}
		} else if(customWtFlag[channel]==1){
				weight = customWeight[channel][subband];
		}
		
		return weight;
	}
	
	/**
	 * For a given channel indicates if weigthing must be applied
	 *
	 * @param channel indicates if the channel that requires the weighting factor
	 * 
	 * @return a boolean that indicates whether weighting must be applied in the given channel  
	 */
	public boolean weightingRequired(int channel){
		
		boolean required = true;
		if(customWtFlag[channel]==2){
				required = false;
		} else if(customWtFlag[channel]==0 && WTType[channel]!=4){
				required = false;
		}
											
		return required;
	}
	

	 /**
	  * Performs the weighting desired to each component
	  * 
	  * @return the weighted image
	  * 
	  * @throws ParameterException when something goes wrong and the weighting must be stopped
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
												imageSamples[z][y][x] *= weight;  
										}
								}
						}
				
						//HL subband
						weight = getWeight(currentLevel*3+1,z);
						for(int y = 0; y < ySubBandSize[currentLevel]; y++){
								for(int x = xSubBandSize[currentLevel] ; x < xSubBandSize[currentLevel+1] ; x++){
									imageSamples[z][y][x] *= weight;  
								}	
						}
				
						//LH subband
						weight = getWeight(currentLevel*3+2,z);
						for(int y = ySubBandSize[currentLevel] ; y < ySubBandSize[currentLevel+1]; y++){
								for(int x = 0; x < xSubBandSize[currentLevel] ; x++){
									imageSamples[z][y][x] *= weight;  
								}
						}
				
						//HH subband
						weight = getWeight(currentLevel*3+3,z);
						for(int y = ySubBandSize[currentLevel]; y < ySubBandSize[currentLevel+1]; y++){
								for(int x = xSubBandSize[currentLevel]; x < xSubBandSize[currentLevel+1]; x++){
										imageSamples[z][y][x] *= weight;  
								}
						}
					}
			}
		}
		//Return the weighted image
		return(imageSamples);
	}

}
