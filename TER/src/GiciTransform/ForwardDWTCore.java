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
 * This class receives an image and performs one level of an arbitrary defined 3D discrete wavelet transform on it. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 * &nbsp; getImageSamples<br> 
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class ForwardDWTCore{

	/**
	 * Image samples (index meaning [z][y][x]).
	 */
	float[][][] imageSamples = null;

	/**
	 * Discrete wavelet transform to be applied.
	 *<p>
	 * Valid values are:
	 *   <ul>
	 *     <li> 0 - Nothing
	 *     <li> 1 - Reversible 5/3 DWT
	 *     <li> 2 - Irreversible 9/7 DWT Isorange
	 *     <li> 3 - Irreversible 9/7 DWT Isonorm
	 *     <li> 4 - Integer (reversible) 9/7 M (CCSDS Recommended )
	 *   </ul>
	 */
	int WTTypes;

	/**
	 * Dimensional axis in which the transform is going to be applied.
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - x
	 *     <li> 1 - y
	 *     <li> 2 - z
	 *   </ul>
	 *
	 */
	int WTAxis;
	
	/**
	 * Initial coordinate of the Z axis region to be computed.
	 * <p>
	 * Negative Values not allowed
	 */
	int WTZRegionStart;
	
	/**
	 * Ending coordinate of the Z axis region to be computed.
	 * <p>
	 * Negative Values not allowed
	 */
	int WTZRegionEnd;
	
	/**
	 * Initial coordinate of the Y axis region to be computed.
	 * <p>
	 * Negative Values not allowed
	 */
	int WTYRegionStart;
	
	/**
	 * Ending coordinate of the Y axis region to be computed.
	 * <p>
	 * Negative Values not allowed
	 */
	int WTYRegionEnd;

	/**
	 * Initial coordinate of the X axis region to be computed.
	 * <p>
	 * Negative Values not allowed
	 */
	int WTXRegionStart;
	
	/**
	 * Ending coordinate of the X axis region to be computed.
	 * <p>
	 * Negative Values not allowed
	 */
	int WTXRegionEnd;

	//INTERNAL VARIABLES

	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;


	/**
	 * Constructor that receives the original image samples.
	 * @param imageSamples definition in {@link #imageSamples}
	 */
	public ForwardDWTCore(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;
	}

	/**
	 * Set the parameters used to apply the discrete wavelet transform.
	 *
	 * @param WTTypes definition in {@link #WTTypes}
	 * @param WTAxis definition in {@link #WTAxis}
	 * @param WTZRegionStart definition in {@link #WTZRegionStart}
	 * @param WTZRegionEnd definition in {@link #WTZRegionEnd}
	 * @param WTYRegionStart definition in {@link #WTYRegionStart}
	 * @param WTYRegionEnd definition in {@link #WTYRegionEnd}
	 * @param WTXRegionStart definition in {@link #WTXRegionStart}
	 * @param WTXRegionEnd definition in {@link #WTXRegionEnd}
	 */
	public void setParameters(int WTTypes, int WTAxis,int WTZRegionStart,int WTZRegionEnd,int WTYRegionStart,int WTYRegionEnd,int WTXRegionStart,int WTXRegionEnd){
		parametersSet = true;
		//Parameters copy
		this.WTTypes = WTTypes;
		this.WTAxis = WTAxis;
		this.WTZRegionStart = WTZRegionStart;
		this.WTZRegionEnd = WTZRegionEnd;
		this.WTYRegionStart = WTYRegionStart;
		this.WTYRegionEnd = WTYRegionEnd;
		this.WTXRegionStart = WTXRegionStart;
		this.WTXRegionEnd = WTXRegionEnd;
	}

	/**
	 * Used to set (or reset) the image over which the transform is going to be applied.
	 *
	 * @param imageSamples definition in {@link #imageSamples}
	 */
	public void setImageSamples(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;
	}

	/**
	 * Used to get the 3DWT transformed image.
	 *
	 * @return the DWT image
	 */
	public float[][][] getImageSamples(){
		return(imageSamples);
	}


	/**
	 * Performs the discrete wavelete transform.
	 *
	 * @throws ErrorException when parameters are not set, wavelet type is unrecognized or trying to transform over an unimplemented axis
	 */
	public void run() throws ErrorException{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ErrorException("Parameters not set.");
		}

		//Apply DWT only if is specified
		if( WTTypes != 0 ){
			//Size to transform for each axis.
			int zSize = WTZRegionEnd-WTZRegionStart+1;
			int ySize = WTYRegionEnd-WTYRegionStart+1;
			int xSize = WTXRegionEnd-WTXRegionStart+1;
			//Apply DWD
			switch(WTAxis){
			case 0: //Over the X axis.
				for( int z = 0; z < zSize; z++ ){
					for(int y = 0; y < ySize; y++){
						float currentLine[] = new float[xSize];
						for(int x = 0; x < xSize; x++){
							currentLine[x+WTXRegionStart] = imageSamples[z+WTZRegionStart][y+WTYRegionStart][x+WTXRegionStart];
						}
						currentLine = filtering(currentLine,WTTypes,WTXRegionStart);
						for(int x = 0; x < xSize; x++){
							imageSamples[z+WTZRegionStart][y+WTYRegionStart][x+WTXRegionStart] = currentLine[x+WTXRegionStart];
						}
					}
				}
				
				break;

			case 1: //Over the Y axis.
				for( int z = 0; z < zSize; z++ ){
					for(int x = 0; x < xSize; x++){
						float currentLine[] = new float[ySize];
						for(int y = 0; y < ySize; y++){
							currentLine[y+WTYRegionStart] = imageSamples[z+WTZRegionStart][y+WTYRegionStart][x+WTXRegionStart];
						}
						currentLine = filtering(currentLine,WTTypes,WTYRegionStart);
						for(int y = 0; y < ySize; y++){
							imageSamples[z+WTZRegionStart][y+WTYRegionStart][x+WTXRegionStart] = currentLine[y+WTYRegionStart];
						}
					}
				}
				
				break;

			case 2: //Over the Z axis.
				for( int y = 0; y < ySize; y++ ){
					for(int x = 0; x < xSize; x++){
						float currentLine[] = new float[zSize];
						for(int z = 0; z < zSize; z++){
							currentLine[z+WTZRegionStart] = imageSamples[z+WTZRegionStart][y+WTYRegionStart][x+WTXRegionStart];
						}
						currentLine = filtering(currentLine,WTTypes,WTZRegionStart);
						for(int z = 0; z < zSize; z++){
							imageSamples[z+WTZRegionStart][y+WTYRegionStart][x+WTXRegionStart] = currentLine[z+WTZRegionStart];
						}
					}
				}
				
				break;

			default: //Not implemented axis
				throw new ErrorException("Unimplemented Axis");
			}

		}
		//Securing the correct use of the class.
		parametersSet = false;
		
		//Image data copy
		//this.imageSamples = imageSamples;
	}

	/**
	 * This function selects the way to apply the filter depending on the phase and the size of the source.
	 *
	 * @param src a float array of the image samples
	 * @param WTTypes Filter to apply
	 * @param WTLRegionStart The starting coordinate of the original line to transform, used to determine the starting phase.
	 * @return a float array that contains the transformed sources
	 *
	 * @throws ErrorException when wavelet type is unrecognized
	 */
	private float[] filtering(float[] src, int WTTypes, int WTLRegionStart) throws ErrorException{
		float[] filtered = null;
		float[] filtered_prim =null;

		if(src.length == 1 && WTTypes != 4){
			filtered = src;
		}else{
			float[] src_prim = null;
			if(WTTypes == 4 && src.length < 6){
				src_prim = coefExpansion(src);
	
			}else{
				src_prim = src;
			}
			if(src_prim.length%2 == 0 && WTLRegionStart%2 == 0 ){
				filtered_prim = evenEvenFiltering(src_prim, WTTypes);
			}else if(src_prim.length%2 == 0 && WTLRegionStart%2 == 1 ){
				filtered_prim = evenOddFiltering(src_prim, WTTypes);
			}else if(src_prim.length%2 == 1 && WTLRegionStart%2 == 0 ){
				filtered_prim = oddEvenFiltering(src_prim, WTTypes);
			}else if(src_prim.length%2 == 1 && WTLRegionStart%2 == 1 ){
				filtered_prim = oddOddFiltering(src_prim, WTTypes);
			}else{
				filtered_prim = src_prim;
			}
			if(WTTypes == 4 && src.length < 6){
				filtered = coefUnexpansion(filtered_prim,src.length);	
			}else{
				filtered = filtered_prim;
			}
		}
		return (filtered);
	}


	/**
	 * This function applies the DWT filter to a source with even length and even phase.
	 *
	 * @param src a float array of the image samples
	 * @param WTTypes Filter to apply
	 * @return a float array that contains the transformed sources
	 *
	 * @throws ErrorException when wavelet type is unrecognized
	 */
	private float[] evenEvenFiltering(float[] src, int WTTypes) throws ErrorException{
		//Subband size
		int subbandSize = src.length;

		//Applying the filter
		switch(WTTypes){
		case 1: // 5/3 DWT
			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] - (float) (Math.floor(((src[k-1]+src[k+1])/2)));
			}
			src[subbandSize-1] = src[subbandSize-1] - (float) (Math.floor((src[subbandSize-2]+src[subbandSize-2])/2));
			src[0] = src[0] + (float) (Math.floor(((src[1]+src[1]+2)/4)));
			for (int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] + (float) (Math.floor(((src[k-1]+src[k+1]+2)/4)));
			}
			break;
		case 2: // 9/7 DWT
		case 3:
			final float alfa_97 = -1.586134342059924F;
			final float beta_97 = -0.052980118572961F;
			final float gamma_97 = 0.882911075530934F;
			final float delta_97 = 0.443506852043971F;
			final float nh_97, nl_97;
			if ( WTTypes == 2 ){// Isorange
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
			break;
		case 4:
			// Integer 9/7 M (CCSDS Recommended )
			if ( subbandSize >=6 ){
				final float alfa1 = (9F/16F);
				final float alfa2 = (1F/16F);
				final float beta = (1F/4F);

				src[1]=src[1] - (float) (Math.floor( alfa1*(src[0]+src[2])-alfa2*(src[2]+src[4])+0.5F ) );
				for (int k=3; k<subbandSize-3; k+=2){
					src[k]=src[k] - (float) (Math.floor( alfa1*(src[k-1]+src[k+1])-alfa2*(src[k-3]+src[k+3])+0.5F ) );
				}
				src[subbandSize-3]=src[subbandSize-3] - (float) (Math.floor( alfa1*(src[subbandSize-4]+src[subbandSize-2])- alfa2*(src[subbandSize-6]+src[subbandSize-2]) + 0.5F ) );
				src[subbandSize-1]=src[subbandSize-1] - (float) (Math.floor( alfa1*(src[subbandSize-2]+src[subbandSize-2])- alfa2*(src[subbandSize-4]+src[subbandSize-4]) + 0.5F ) );
				src[0]=src[0] -  (float) ( Math.floor(-beta*(src[1]+src[1])+0.5F) );
				for (int k=2; k<subbandSize; k+=2){
					src[k]=src[k] - (float) (Math.floor(-beta*(src[k-1]+src[k+1])+0.5F) );
				}
			} else {
				throw new ErrorException("Size should be greater or equal than 6 in order to perform 9/7M");
			}
			break;
		default:
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
	 * This function applies the DWT filter to a source with even length and odd phase.
	 *
	 * @param src a float array of the image samples
	 * @param WTTypes Filter to apply
	 * @return a float array that contains the transformed sources
	 *
	 * @throws ErrorException when wavelet type is unrecognized
	 */
	private float[] evenOddFiltering(float[] src, int WTTypes) throws ErrorException{
		//Subband size
		int subbandSize = src.length;
		//Appling the filter
		switch(WTTypes){
		case 1: // 5/3 DWT
			src[0] = src[0] - (float) (Math.floor(((src[1]+src[1])/2)));
			for (int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] - (float) (Math.floor(((src[k-1]+src[k+1])/2)));
			}
			
			src[subbandSize-1] = src[subbandSize-1] + (float) (Math.floor((src[subbandSize-2]+src[subbandSize-2]+2)/4));
			
			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] + (float) (Math.floor(((src[k-1]+src[k+1]+2)/4)));
			}
			break;
		
		case 2: // 9/7 DWT
		case 3:
			final float alfa_97 = -1.586134342059924F;
			final float beta_97 = -0.052980118572961F;
			final float gamma_97 = 0.882911075530934F;
			final float delta_97 = 0.443506852043971F;
			final float nh_97, nl_97;
			if ( WTTypes == 2 ){// Isorange
				nh_97 = 1.230174104914001F; //with this weights the range is mantained
				nl_97 = 1F / nh_97;
			} else {// Isonorm
				nl_97 = 1.14960430535816F; //with this weights the norm is nearly mantained
				nh_97 = -1F / nl_97;
			}
			src[0] = src[0] + alfa_97 * (src[1]+src[1]);
			for(int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] + alfa_97 * (src[k-1]+src[k+1]);
			}

			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] + beta_97 * (src[k-1]+src[k+1]);
			}
			src[subbandSize-1] = src[subbandSize-1] + beta_97 * (src[subbandSize-2]+src[subbandSize-2]);

			src[0] = src[0] + gamma_97 * (src[1]+src[1]);
			for(int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] + gamma_97 * (src[k-1]+src[k+1]);
			}

			src[subbandSize-1] = src[subbandSize-1] + delta_97 * (src[subbandSize-2]+src[subbandSize-2]);
			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] + delta_97 * (src[k-1]+src[k+1]);
			}

			for(int k = 0; k < subbandSize; k+= 2){
				src[k] = src[k] * nh_97;
				src[k+1] = src[k+1] * nl_97;
			}
			break;
		case 4:
			// Integer 9/7 M (CCSDS Recommended )
			if ( subbandSize >=6 ){
				final float alfa1 = (9F/16F);
				final float alfa2 = (1F/16F);
				final float beta = (1F/4F);	
				src[1]=src[1] - (float) (Math.floor( alfa1*(src[0]+src[2])-alfa2*(src[2]+src[4])+0.5F ) );
				for (int k=3; k<subbandSize-3; k+=2){
					src[k]=src[k] - (float) (Math.floor( alfa1*(src[k-1]+src[k+1])-alfa2*(src[k-3]+src[k+3])+0.5F ) );
				}
				src[subbandSize-3]=src[subbandSize-3] - (float) (Math.floor( alfa1*(src[subbandSize-4]+src[subbandSize-2])- alfa2*(src[subbandSize-6]+src[subbandSize-2]) + 0.5F ) );
				src[subbandSize-1]=src[subbandSize-1] - (float) (Math.floor( alfa1*(src[subbandSize-2]+src[subbandSize-2])- alfa2*(src[subbandSize-4]+src[subbandSize-4]) + 0.5F ) );
				src[0]=src[0] -  (float) ( Math.floor(-beta*(src[1]+src[1])+0.5F) );
				for (int k=2; k<subbandSize; k+=2){
					src[k]=src[k] - (float) (Math.floor(-beta*(src[k-1]+src[k+1])+0.5F) );
				}
			} else {
				throw new ErrorException("Size should be greater or equal than 6 in order to perform 9/7M");
			}
			break;
		default:
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
	 * This function applies the DWT filter to a source with odd length and even phase.
	 *
	 * @param src a float array of the image samples
	 * @param WTTypes Filter to apply
	 * @return a float array that contains the transformed sources
	 *
	 * @throws ErrorException when wavelet type is unrecognized
	 */
	private float[] oddEvenFiltering(float[] src, int WTTypes) throws ErrorException{
		//Subband size
		int subbandSize = src.length;

		//Applying the filter
		switch(WTTypes){
		case 1: // 5/3 DWT
			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] - (float) (Math.floor(((src[k-1]+src[k+1])/2)));
			}
			src[0] = src[0] + (float) (Math.floor(((src[1]+src[1]+2)/4)));
			for (int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] + (float) (Math.floor(((src[k-1]+src[k+1]+2)/4)));
			}
			src[subbandSize-1] = src[subbandSize-1] + (float) (Math.floor(((src[subbandSize-2]+src[subbandSize-2]+2)/4)));
			break;
		case 2: // 9/7 DWT
		case 3:
			final float alfa_97 = -1.586134342059924F;
			final float beta_97 = -0.052980118572961F;
			final float gamma_97 = 0.882911075530934F;
			final float delta_97 = 0.443506852043971F;
			final float nh_97, nl_97;
			if ( WTTypes == 2 ){// Isorange
				nh_97 = 1.230174104914001F; //with this weights the range is mantained
				nl_97 = 1F / nh_97;
			} else {// Isonorm
				nl_97 = 1.14960430535816F; //with this weights the norm is nearly mantained
				nh_97 = -1F / nl_97;
			}
			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] + alfa_97 * (src[k-1]+src[k+1]);
			}

			src[0] = src[0] + beta_97 * (src[1]+src[1]);
			for(int k = 2; k < subbandSize-1 ; k += 2){
				src[k] = src[k] + beta_97 * (src[k-1]+src[k+1]);
			}
			src[subbandSize-1] = src[subbandSize-1] + beta_97 * (src[subbandSize-2]+src[subbandSize-2]);

			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] + gamma_97 * (src[k-1]+src[k+1]);
			}

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
			break;
		case 4:
			// Integer 9/7 M (CCSDS Recommended )
			if ( subbandSize >=6 ){
				final float alfa1 = (9F/16F);
				final float alfa2 = (1F/16F);
				final float beta = (1F/4F);	
				src[1]=src[1] - (float) (Math.floor( alfa1*(src[0]+src[2])-alfa2*(src[2]+src[4])+0.5 ) );
				for (int k=3; k<subbandSize-3; k+=2){
					src[k]=src[k] - (float) (Math.floor( alfa1*(src[k-1]+src[k+1])-alfa2*(src[k-3]+src[k+3])+0.5 ) );
				}
				src[subbandSize-2]=src[subbandSize-2] - (float) (Math.floor( alfa1*(src[subbandSize-3]+src[subbandSize-1])- alfa2*(src[subbandSize-5]+src[subbandSize-1]) + 0.5 ) );
				src[0]=src[0] -  (float) ( Math.floor(-beta*(src[1]+src[1])+0.5) );
				for (int k=2; k<subbandSize-1; k+=2){
					src[k]=src[k] - (float) (Math.floor(-beta*(src[k-1]+src[k+1])+0.5) );
				}
				src[subbandSize-1]= src[subbandSize-1] - (float) (Math.floor(-beta*(src[subbandSize-2]+src[subbandSize-2])+0.5));
			} else {
				throw new ErrorException("Size should be greater or equal than 6 in order to perform 9/7M");
			}
			break;
		default:
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

	/**
	 * This function applies the DWT filter to a source with odd length and odd phase.
	 *
	 * @param src a float array of the image samples
	 * @param WTTypes Filter to apply
	 * @return a float array that contains the transformed sources
	 *
	 * @throws ErrorException when wavelet type is unrecognized
	 */
	private float[] oddOddFiltering(float[] src, int WTTypes) throws ErrorException{
		//Subband size
		int subbandSize = src.length;

		//Applying the filter
		switch(WTTypes){
		case 1: // 5/3 DWT
			for (int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] - (float) (Math.floor(((src[k-1]+src[k+1])/2)));
			}
			src[0] = src[0] - (float) (Math.floor(((src[1]+src[1])/2)));
			src[subbandSize-1] = src[subbandSize-1] - (float) (Math.floor(((src[subbandSize-2]+src[subbandSize-2])/2)));

			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] + (float) (Math.floor(((src[k-1]+src[k+1]+2)/4)));
			}
			break;

		case 2: // 9/7 DWT
		case 3:
			final float alfa_97 = -1.586134342059924F;
			final float beta_97 = -0.052980118572961F;
			final float gamma_97 = 0.882911075530934F;
			final float delta_97 = 0.443506852043971F;
			final float nh_97, nl_97;
			if ( WTTypes == 2 ){// Isorange
				nh_97 = 1.230174104914001F; //with this weights the range is mantained
				nl_97 = 1F / nh_97;
			} else {// Isonorm
				nl_97 = 1.14960430535816F; //with this weights the norm is nearly mantained
				nh_97 = -1F / nl_97;
			}
			for(int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] + alfa_97 * (src[k-1]+src[k+1]);
			}
			src[subbandSize-1] = src[subbandSize-1] + alfa_97 * (src[subbandSize-2]+src[subbandSize-2]);
			src[0] = src[0] + alfa_97 * (src[1]+src[1]);

			for(int k = 1; k < subbandSize-1 ; k += 2){
				src[k] = src[k] + beta_97 * (src[k-1]+src[k+1]);
			}

			for(int k = 2; k < subbandSize-1; k += 2){
				src[k] = src[k] + gamma_97 * (src[k-1]+src[k+1]);
			}
			src[subbandSize-1] = src[subbandSize-1] + gamma_97 * (src[subbandSize-2]+src[subbandSize-2]);
			src[0] = src[0] + gamma_97 * (src[1]+src[1]);

			for(int k = 1; k < subbandSize-1; k += 2){
				src[k] = src[k] + delta_97 * (src[k-1]+src[k+1]);
			}

			for(int k = 0; k < subbandSize-1; k+= 2){
				src[k] = src[k] * nh_97;
				src[k+1] = src[k+1] * nl_97;
			}
			src[subbandSize-1]=src[subbandSize-1]*nh_97;
			break;
		case 4:
			// Integer 9/7 M (CCSDS Recommended )
			if ( subbandSize >=6 ){
				final float alfa1 = (9F/16F);
				final float alfa2 = (1F/16F);
				final float beta = (1F/4F);

				src[0]= src[0] - (float) (Math.floor( alfa1*(src[1]+src[1])-alfa2*(src[3]+src[3])+0.5 ));
				src[2]= src[2] - (float) (Math.floor( alfa1*(src[1]+src[3])-alfa2*(src[1]+src[5])+0.5 ));
				for (int k=4; k<subbandSize-3; k+=2){
					src[k]=src[k] - (float) (Math.floor( alfa1*(src[k-1]+src[k+1])-alfa2*(src[k-3]+src[k+3])+0.5 ) );
				}
				src[subbandSize-3]= src[subbandSize-3] - (float) (Math.floor( alfa1*(src[subbandSize-4]+src[subbandSize-2])- alfa2*(src[subbandSize-6]+src[subbandSize-2]) + 0.5 ) );
				src[subbandSize-1]= src[subbandSize-1] - (float) (Math.floor( alfa1*(src[subbandSize-2]+src[subbandSize-2])- alfa2*(src[subbandSize-4]+src[subbandSize-4]) + 0.5 ) );
				for (int k=1; k<subbandSize; k+=2){
					src[k]=src[k] - (float) (Math.floor(-beta*(src[k-1]+src[k+1])+0.5) );
				}
			} else {
				throw new ErrorException("Size should be greater or equal than 6 in order to perform 9/7M");
			}
			break;

		default:
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
	
	/**
	 * This function expands the source array length to transform if it's length is less than 6.
	 *
	 * @param src a float array of the image samples
	 * @return an extended float array with the image samples with length size of 6.
	 * @throws ErrorException when the src length is out of the range: 1 to 5 or undefined.
	 */
	private float[] coefExpansion(float[] src) throws ErrorException{
		float[] extended = new float[6];
		//checking integrity of the src array
		if(src == null){
			throw new ErrorException("The source array is null");
		}
		switch (src.length){
			case 1:
				for (int i = 0; i < 6 ; i++){
					extended[i]=src[0];
				}
				break;
			case 2:
				for (int i = 0; i < 6 ; i+=2){
					extended[i]=src[0];
				}
				for (int i = 1; i < 6 ; i+=2){
					extended[i]=src[1];
				}
				break;
			case 3:
				for (int i = 0; i < 3 ; i++){
					extended[i]=src[i];
				}
				for (int i = 3; i < 5 ; i++){
					extended[i]=src[4-i];
				}
				extended[5]=src[1];
				break;
			case 4:
				for (int i = 0; i < 4 ; i++){
					extended[i]=src[i];
				}
				for (int i = 4; i < 6 ; i++){
					extended[i]=src[6-i];
				}
				break;
			case 5:
				for (int i = 0; i < 5 ; i++){
					extended[i]=src[i];
				}
				extended[5]=src[3];
				break;
			default:
				throw new ErrorException("The source array length is out of the range 1 - 5");
		}
	return(extended);
	}

	/**
	 * This function recovers a filtered array with original length from an expanded filtered source array.
	 *
	 * @param filt a float array of the image samples
	 * @param origSize the original length of the array
	 * @return a recovered float array with the transformed image samples with it's original length.
	 * @throws ErrorException when filt length is different of 6 or undefined and when the original length does not correspond to a valid source.
	 */
	private float[] coefUnexpansion(float[] filt,int origSize) throws ErrorException{
		float[] recovered = new float[origSize];
		//checking integrity of the filt array
		if(filt == null){
			throw new ErrorException("The filtered array is null!");
		}
		if(filt.length != 6){
			throw new ErrorException("The filtered array does not have size of 6");
		}
		switch (origSize){
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				for(int i=0; i < origSize; i++){
					recovered[i]=filt[i];
				}
				break;
			default:
				throw new ErrorException("The array original length is out of the range 1 - 5");
		}
	return(recovered);
	}

}
