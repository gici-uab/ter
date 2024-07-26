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
 * This class receives an image and performs applies a discrete wavelet transform.<br>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class ForwardWaveletTransform{

		
	/**
	 * Definition in Coder
	 */
	float[][][] imageSamples = null;

	/**
	 * Definition in Coder
	 */
	int zSize;

	/**
	 * Definition in Coder
	 */
	int ySize;

	/**
	 * Definition in Coder
	 */
	int xSize;

	/**
	 * Discrete wavelet transform to be applied for each component.
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - None
	 *     <li> 1 - Reversible 5/3 DWT (JPEG2000)
	 *     <li> 2 - Real Isorange (Irreversible) 9/7 DWT (JPEG2000 standard)
	 *     <li> 3 - Real Isonorm (Irreversible) 9/7 DWT (CCSDS-Recommended)
	 *     <li> 4 - Integer (Reversible) 9/7M DWT (CCSDS-Recommended)
	 *     <li> 5 - Integer 5/3 DWT (Classic construction)
	 *     <li> 6 - Integer 9/7 DWT (Classic construction)
	 *   </ul>
	 */
	int[] WTTypes = null;

   /**
	 * DWT levels to apply for each component.
	 * <p>
	 * Negative values not allowed.
	 */
	int[] WTLevels = null;
	 
	/**
	 * To know the order of the transform in the spatial dimentions for each component
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - Horizontal - Verical
	 *     <li> 1 - Vertical - Horizontal
	 *     <li> 2 - Only horizontal
	 *   </ul>
	 * 
	 */
	int[] WTOrder = null;

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
	public ForwardWaveletTransform(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;

		//Size set
		zSize = imageSamples.length;
		
	}

	/**
	 * Set the parameters used to apply the discrete wavelet transform, the order is set and cannot be selected..
	 *
	 * @param WTTypes definition in {@link #WTTypes}
	 * @param WTLevels definition in {@link #WTLevels}
	 */
	public void setParameters(int[] WTTypes, int[] WTLevels){
		parametersSet = true;

		//Parameters copy
		this.WTTypes = WTTypes;
		this.WTLevels = WTLevels;
		this.WTOrder = new int[zSize];
		for (int z=0 ; z < zSize; z++){ //if order is not specified vertical-horizontal is set.
			// default order is the one designed for the JPEG2000 standard
			WTOrder[z] = 1; 
		}
	}

	/**
	 * Set the parameters used to apply the discrete wavelet transform when the order of the spatial dimentions can be chosen.
	 *
	 * @param WTTypes definition in {@link #WTTypes}
	 * @param WTLevels definition in {@link #WTLevels}
	 * @param WTOrder definition in {@link #WTOrder} 
	 */
	public void setParameters(int[] WTTypes, int[] WTLevels, int[] WTOrder){
		parametersSet = true;

		//Parameters copy
		this.WTTypes = WTTypes;
		this.WTLevels = WTLevels;
		this.WTOrder =WTOrder;
	}
	

	
	/**
	 * Performs the discrete wavelete transform and returns the result image.
	 *
	 * @return the DWT image
	 *
	 * @throws ErrorException when parameters are not set or wavelet type is unrecognized
	 */
	public float[][][] run() throws ErrorException{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ErrorException("Discrete wavelet transform cannot run if parameters are not set.");
		}

		//Apply DWT for each component
		for(int z = 0; z < zSize; z++){
			ySize = imageSamples[z].length;
			xSize = imageSamples[z][0].length;
			//Apply DWT only if is specified
			if((WTTypes[z] != 0) && (WTLevels[z] > 0)){

				//Level size
				int xSubBandSize = xSize;
				int ySubBandSize = ySize;

				//Apply DWT for each level
				for(int currentLevel = 0; currentLevel < WTLevels[z]; currentLevel++){

					if( WTOrder[z] == 0 || WTOrder[z] ==2 ){
						//HOR_SD
						for(int y = 0; y < ySubBandSize; y++){
							float currentRow[] = new float[xSubBandSize];
							for(int x = 0; x < xSubBandSize; x++){
								currentRow[x] = imageSamples[z][y][x];
							}
							currentRow = filtering(currentRow, z);
							for(int x = 0; x < xSubBandSize; x++){
								imageSamples[z][y][x] = currentRow[x];
							}
						}
					}
					
					if( WTOrder[z] !=2 ){
						//VER_SD
						for(int x = 0; x < xSubBandSize; x++){
							float currentColumn[] = new float[ySubBandSize];
							for(int y = 0; y < ySubBandSize; y++){
								currentColumn[y] = imageSamples[z][y][x];
							}
							currentColumn = filtering(currentColumn, z);
							for(int y = 0; y < ySubBandSize; y++){
								imageSamples[z][y][x] = currentColumn[y];
							}
						}
					}
					
					if( WTOrder[z] == 1 ){
						//HOR_SD
						for(int y = 0; y < ySubBandSize; y++){
							float currentRow[] = new float[xSubBandSize];
							for(int x = 0; x < xSubBandSize; x++){
								currentRow[x] = imageSamples[z][y][x];
							}
							currentRow = filtering(currentRow, z);
							for(int x = 0; x < xSubBandSize; x++){
								imageSamples[z][y][x] = currentRow[x];
							}
						}
					}

					//Size setting for the next level
					xSubBandSize = xSubBandSize / 2 + xSubBandSize % 2;
					if ( WTOrder[z] !=2 ) {
						ySubBandSize = ySubBandSize / 2 + ySubBandSize % 2;
					}
				}
			}
		}

		//Return the DWT image
		return(imageSamples);
	}

	/**
	 * This function selects the way to apply the filter
	 * selected depending on the size of the source
	 *
	 * @param src a float array of the image samples
	 * @param z the component determines the filter to apply
	 * @return a float array that contains the tranformed sources
	 *
	 * @throws ErrorException when wavelet type is unrecognized
	 */
	private float[] filtering(float[] src, int z) throws ErrorException{
		if(src.length==1) {
			return(src);
		}
		if(src.length%2==0) {
			return(evenFiltering(src, z));
		} else{
			return(oddFiltering(src, z));
		}
	}

	/**
	 * This function applies the DWT filter to a source with even length.
	 *
	 * @param src a float array of the image samples
	 * @param z the component determines the filter to apply
	 *
	 * @return a float array that contains the tranformed sources
	 *
	 * @throws ErrorException when wavelet type is unrecognized or size is not proper
	 */
	private float[] evenFiltering(float[] src, int z) throws ErrorException{
		//Subband size
		int subbandSize = src.length;

		//Appling the filter
		if( WTTypes[z] == 1 ){
			// Integer 5/3 DWT JPEG200
			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] - (float) (Math.floor(((src[k-1]+src[k+1])/2)));
			}
			src[subbandSize-1] = src[subbandSize-1] - (float) (Math.floor((src[subbandSize-2]+src[subbandSize-2])/2));
			src[0] = src[0] + (float) (Math.floor(((src[1]+src[1]+2)/4)));
			for (int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] + (float) (Math.floor(((src[k-1]+src[k+1]+2)/4)));
			}	
		} else if ( WTTypes[z] == 2 || WTTypes[z] == 3 )  {
			// 9/7 DWT
			final float alfa_97 = -1.586134342059924F;
			final float beta_97 = -0.052980118572961F;
			final float gamma_97 = 0.882911075530934F;
			final float delta_97 = 0.443506852043971F;
			final float nh_97, nl_97;
			if ( WTTypes[z] == 2 ){// Isorange
				nh_97 = 1.230174104914001F; //with this weights the range is mantained
				nl_97 = 1F / nh_97;
			} else {// Isonorm
				nl_97 = 1.14960430535816F; //with this weights the norm is nearly mantained
				nh_97 = -1F / nl_97;
			}

			for(int k = 1; k < subbandSize-2; k += 2){
				src[k] = src[k] + alfa_97 * (src[k-1]+src[k+1]);
			}
			src[subbandSize-1] = src[subbandSize-1] + alfa_97 * (src[subbandSize-2]+src[subbandSize-2]);

			src[0] = src[0] + beta_97 * (src[1]+src[1]);
			for(int k = 2; k < subbandSize; k += 2){
				src[k] = src[k] + beta_97 * (src[k-1]+src[k+1]);
			}

			for(int k = 1; k < subbandSize-2; k += 2){
				src[k] = src[k] + gamma_97 * (src[k-1]+src[k+1]);
			}
			src[subbandSize-1] = src[subbandSize-1] + gamma_97 * (src[subbandSize-2]+src[subbandSize-2]);

			src[0] = src[0] + delta_97 * (src[1]+src[1]);
			for(int k = 2; k < subbandSize; k += 2){
				src[k] = src[k] + delta_97 * (src[k-1]+src[k+1]);
			}

			for(int k = 0; k < subbandSize; k+= 2){
				src[k] = src[k] * nl_97;
				src[k+1] = src[k+1] * nh_97;
			}
		} else if( WTTypes[z] == 4 ){
			// Integer 9/7 M (CCSDS Recommended )
			if ( subbandSize >=6 ){
				final float alfa1 = (9F/16F);
				final float alfa2 = (1F/16F);
				final float beta = (1F/4F);
						
				src[1]=src[1] - (float) (Math.floor( alfa1*(src[0]+src[2])-alfa2*(src[2]+src[4])+0.5F ) );
				for (int k=3; k<subbandSize-3; k+=2){
					src[k]=src[k] - (float) (Math.floor( alfa1*(src[k-1]+src[k+1])-alfa2*(src[k-3]+src[k+3])+0.5F ) );
				}
				src[subbandSize-3]=src[subbandSize-3] - (float) (Math.floor( alfa1*(src[subbandSize-4]+src[subbandSize-2]) 
																								- alfa2*(src[subbandSize-6]+src[subbandSize-2]) + 0.5F ) );
				src[subbandSize-1]=src[subbandSize-1] - (float) (Math.floor( alfa1*(src[subbandSize-2]+src[subbandSize-2]) 
																								- alfa2*(src[subbandSize-4]+src[subbandSize-4]) + 0.5F ) );
				
				src[0]=src[0] -  (float) ( Math.floor(-beta*(src[1]+src[1])+0.5F) );
				for (int k=2; k<subbandSize; k+=2){
					src[k]=src[k] - (float) (Math.floor(-beta*(src[k-1]+src[k+1])+0.5F) );
				}
			} else {
				throw new ErrorException("Size should be greater or equal than 6 in order to perform 9/7M");
			}
		} else if ( WTTypes[z] == 5 || WTTypes[z] == 6 ){
			// Classical Integer to Integer DWT
			final float alfa, beta, gamma, delta;
			if( WTTypes[z] == 6 ){ // 9/7 integer version has been chosen
				alfa=-1.58615986717275F;
				beta=-0.05297864003258F;
				gamma=0.88293362717904F;
				delta=0.44350482244527F;
			} else {//it is supposed the 5/3 Le Gall filter has been chosen
				alfa=-0.5F;
				beta=0.25F;
				gamma=0.F;
				delta=0.F;
			}

			for (int k=1; k<subbandSize-2; k+=2){
				src[k]=src[k]+ (float) Math.floor(alfa*(src[k-1]+src[k+1])+0.5);
			}
			src[subbandSize-1]=src[subbandSize-1]+ (float) Math.floor(alfa*(src[subbandSize-2]+src[subbandSize-2])+0.5);

			src[0]=src[0]+  (float) Math.floor(beta*(src[1]+src[1])+0.5);
			for (int k=2; k<subbandSize; k+=2){
				src[k]=src[k]+ (float) Math.floor(beta*(src[k-1]+src[k+1])+0.5);
			}
			
			if( WTTypes[z] == 6 ){
				for (int k=1; k<subbandSize-2; k+=2){
					src[k]=src[k]+ (float) Math.floor(gamma*(src[k-1]+src[k+1])+0.5);
				}
				src[subbandSize-1]=src[subbandSize-1]+ (float) Math.floor(gamma*(src[subbandSize-2]+src[subbandSize-2])+0.5);

				src[0]=src[0]+ (float) Math.floor(delta*(src[1]+src[1])+0.5);
				for (int k=2; k<subbandSize; k+=2){
					src[k]=src[k]+ (float) Math.floor(delta*(src[k-1]+src[k+1])+0.5);
				}
			}
		} else {
			throw new ErrorException("Unrecognized wavelet transform type.");
		}

		//DE_INTERLEAVE
		int half = subbandSize / 2;
		float dst[] = new float[subbandSize];
		for(int k = 0; k < half; k++){
			dst[k] = src[2*k];
			dst[k+half] = src[2*k+1];
		}
		return(dst);

	}

	/**
	 * This function applies the DWT filter to a source with odd length.
	 *
	 * @param src a float array of the image samples
	 * @param z the component determines the filter to apply
	 *
	 * @return a float array that contains the tranformed sources
	 *
	 * @throws ErrorException when wavelet type is unrecognized or size is not proper
	 */
	private float[] oddFiltering(float[] src, int z) throws ErrorException{

		int subbandSize = src.length;

		//Appling the filter
		if( WTTypes[z] == 1 ){
			// Integer 5/3 DWT JPEG2000
			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] - (float) (Math.floor(((src[k-1]+src[k+1])/2)));
			}
			src[0] = src[0] + (float) (Math.floor(((src[1]+src[1]+2)/4)));
			for (int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] + (float) (Math.floor(((src[k-1]+src[k+1]+2)/4)));
			}
			src[subbandSize-1] = src[subbandSize-1] + (float) (Math.floor(((src[subbandSize-2]+src[subbandSize-2]+2)/4)));
	
		} else if ( WTTypes[z] == 2 || WTTypes[z] == 3 )  {
			// 9/7 DWT
			final float alfa_97 = -1.586134342059924F;
			final float beta_97 = -0.052980118572961F;
			final float gamma_97 = 0.882911075530934F;
			final float delta_97 = 0.443506852043971F;
			final float nh_97, nl_97;
			if ( WTTypes[z] == 2 ){// Isorange
				nh_97 = 1.230174104914001F; //with this weights the range is mantained
				nl_97 = 1F / nh_97;
			} else {// Isonorm
				nh_97 = 1.14960430535816F; //with this weights the norm is nearly mantained
				nl_97 = -1F / nh_97;
			}

			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] + alfa_97 * (src[k-1]+src[k+1]);
			}
			//src[subbandSize-1] = src[subbandSize-1] + alfa_97 * (src[subbandSize-2]+src[subbandSize-2]);

			src[0] = src[0] + beta_97 * (src[1]+src[1]);
			for(int k = 2; k < subbandSize-1 ; k += 2){
				src[k] = src[k] + beta_97 * (src[k-1]+src[k+1]);
			}
			src[subbandSize-1] = src[subbandSize-1] + beta_97 * (src[subbandSize-2]+src[subbandSize-2]);

			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] + gamma_97 * (src[k-1]+src[k+1]);
			}
			//src[subbandSize-1] = src[subbandSize-1] + gamma_97 * (src[subbandSize-2]+src[subbandSize-2]);

			src[0] = src[0] + delta_97 * (src[1]+src[1]);
			for(int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] + delta_97 * (src[k-1]+src[k+1]);
			}
			src[subbandSize-1] = src[subbandSize-1] + delta_97 * (src[subbandSize-2]+src[subbandSize-2]);

			for(int k = 0; k < subbandSize-1; k+= 2){
				src[k] = src[k] * nl_97;
				src[k+1] = src[k+1] * nh_97;
			}
			src[subbandSize-1]=src[subbandSize-1]*nl_97;
		} else if( WTTypes[z] == 4 ){
			// Integer 9/7 M (CCSDS Recommended )
			throw new ErrorException("Integer 9/7M CCSDS Recommended is not implemented for odd signals.!!!");
		} else if ( WTTypes[z] == 5 || WTTypes[z] == 6 ){
			// Classical Integer to Integer DWT
			final float alfa, beta, gamma, delta;
			if( WTTypes[z] == 6 ){ // 9/7 integer version has been chosen
				alfa=-1.58615986717275F;
				beta=-0.05297864003258F;
				gamma=0.88293362717904F;
				delta=0.44350482244527F;
			} else {//it is supposed the 5/3 Le Gall filter has been chosen
				alfa=-0.5F;
				beta=0.25F;
				gamma=0.F;
				delta=0.F;
			}

			for (int k=1; k<subbandSize-1; k+=2){
				src[k]=src[k]+ (float) Math.floor(alfa*(src[k-1]+src[k+1])+0.5);
			}
			//src[subbandSize-1]=src[subbandSize-1]+ (float) Math.floor(alfa*(src[subbandSize-2]+src[subbandSize-2])+0.5);

			src[0]=src[0]+  (float) Math.floor(beta*(src[1]+src[1])+0.5);
			for (int k=2; k<subbandSize-1; k+=2){
				src[k]=src[k]+ (float) Math.floor(beta*(src[k-1]+src[k+1])+0.5);
			}
			src[subbandSize-1]=src[subbandSize-1]+ (float) Math.floor(beta*(src[subbandSize-2]+src[subbandSize-2])+0.5);
			
			if( WTTypes[z] == 6 ){
				for (int k=1; k<subbandSize-1; k+=2){
					src[k]=src[k]+ (float) Math.floor(gamma*(src[k-1]+src[k+1])+0.5);
				}
				//src[subbandSize-1]=src[subbandSize-1]+ (float) Math.floor(gamma*(src[subbandSize-2]+src[subbandSize-2])+0.5);

				src[0]=src[0]+ (float) Math.floor(delta*(src[1]+src[1])+0.5);
				for (int k=2; k<subbandSize-1; k+=2){
					src[k]=src[k]+ (float) Math.floor(delta*(src[k-1]+src[k+1])+0.5);
				}
				src[subbandSize-1]=src[subbandSize-1]+ (float) Math.floor(delta*(src[subbandSize-2]+src[subbandSize-2])+0.5);

			}
		} else {
			throw new ErrorException("Unrecognized wavelet transform type.");
		}

		//DE_INTERLEAVE
		float dst[]=new float[subbandSize];
		for(int k=0;k<(subbandSize/2);k++){
			dst[k]=src[2*k];
			dst[k+(subbandSize/2)+1]=src[2*k+1];
		}
		dst[subbandSize/2]=src[subbandSize-1];

		return(dst);
	}

}
