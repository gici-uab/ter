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
 * This class receives a set of parameters to compute an inverse 3d Hybrid Rectangular/Square Wavelet Transform executing the HyperWaveletDetransform class concordingly. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class InverseNonPyramidal3D{

	/**
	 * Image samples (index meaning [z][y][x]).
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
	 * Discrete wavelet detransform to be applied at each dimension.
	 *<p>
	 * Only the 3 first elements of the array are going to be taken, the rest is ignored.<br>
	 *   <ul>
	 *     <li> WTTypes[0] X axis
	 *     <li> WTTypes[1] Y axis
	 *     <li> WTTypes[2] Z axis
	 *   </ul>
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
	int[] WTTypes = null;
	
	/**
	 * DWDt levels to apply for each dimension.
	 * <p>
	 * Negative values not allowed. Only the 3 first elements of the array are going to be taken, the rest is ignored.
	 *   <ul>
	 *     <li> WTLevels[0] X axis
	 *     <li> WTLevels[1] Y axis
	 *     <li> WTLevels[2] Z axis
	 *   </ul>
	 */
	int[] WTLevels = null;

	/**
	 * To know the order in which the detransform is going to be applied.
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - X, Y, Z
	 *     <li> 1 - X, Z, Y
	 *     <li> 2 - Y, X, Z
	 *     <li> 3 - Y, Z, X
	 *     <li> 4 - Z, X, Y
	 *     <li> 5 - Z, Y, X
	 *   </ul>
	 */
	int WTOrder;

	/**
	 * Predefined model of 3D Discrete Wavelet Detransform to perform.
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - Hybrid rectangular/square (not piramidal) - 2D + 1D
	 *     <li> 1 - Hybrid rectangular/square (not piramidal) - 1D + 2D
	 *   </ul>
	 */
	int WTModel;

	//INTERNAL VARIABLES
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;

	/**
	 * Array to store the Sub-band Sizes for every level in the X axis.
	 */
	int[] xSubBandSizes = null;

	/**
	 * Array to store the Sub-band Sizes for every level in the Y axis.
	 */
	int[] ySubBandSizes = null;

	/**
	 * Array to store the Sub-band Sizes for every level in the Z axis.
	 */
	int[] zSubBandSizes = null;


	/**
	 * Constructor, receives the image samples to detransform.
	 *
	 * @param imageSamples definition in {@link #imageSamples}
	 */
	public InverseNonPyramidal3D(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;
		zSize = imageSamples.length;
		ySize = imageSamples[0].length;
		xSize = imageSamples[0][0].length;
	}

	/**
	 * Set the parameters used to apply the discrete wavelet transform, legacy use, WTOrder and WTModel are going to be set to default values.
	 *
	 * @param WTTypes definition in {@link #WTTypes}
	 * @param WTLevels definition in {@link #WTLevels}
	 */
	public void setParameters(int[] WTTypes, int[] WTLevels){
		parametersSet = true;
		//Parameters copy
		this.WTTypes = WTTypes;
		this.WTLevels = WTLevels;
		//Seting Default Order
		this.WTOrder = 4; // Z, X, Y
		//Seting Default Model
		this.WTModel = 1; // Hybrid rectangular/square (not piramidal) - 1D + 2D
	}

	/**
	 * Set the parameters used to apply the discrete wavelet transform.
	 *
	 * @param WTTypes definition in {@link #WTTypes}
	 * @param WTLevels definition in {@link #WTLevels}
	 * @param WTOrder definition in {@link #WTOrder}
	 * @param WTModel definition in {@link #WTModel}
	 */
	public void setParameters(int[] WTTypes, int[] WTLevels, int WTOrder, int WTModel){
		parametersSet = true;
		//Parameters copy
		this.WTTypes = WTTypes;
		this.WTLevels = WTLevels;
		this.WTOrder = WTOrder;
		this.WTModel = WTModel;
	}

	/**
	 * Performs the requested Wavelet transform Model and returns the result image.
	 *
	 * @return the DWT image
	 *
	 * @throws ErrorException when parameters are not set, wavelet type is unrecognized or there is an insconsistency in the parameters.
	 */
	public float[][][] run() throws ErrorException{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ErrorException("Parameters not set.");
		}

		// Checking parameters validity.
		if ( WTLevels[0] < 0 || WTLevels[1] < 0 || WTLevels[2] < 0 ){ //All values have to be no negative.
			throw new ErrorException("All the transform levels to apply must be no negative.");
		}
		
		if ( WTLevels[0] == 0 && WTLevels[1] == 0 && WTLevels[2] == 0 ){ //Apply 3DWDt only if requested.
			return (imageSamples);
		}

		int xSubBandSize;
		int ySubBandSize;
		int zSubBandSize;
		//Level Size
		computeSubBandSizes();

		//Construction of the 3D-DWT Motor.
		InverseDWTCore hwd = new InverseDWTCore(imageSamples);

		//Deciding what to do per Model basis.
		switch( WTModel ){
		case 0: // Hybrid rectangular/square (not piramidal) - 2D + 1D
			//Sorting cases by order
			switch( WTOrder ){
			case 0: // Vertical, Horizontal, Espectral (X,Y,Z)
				// Checking parameters validity.
				if ( WTLevels[0] != WTLevels[1] ){ //In order to apply the Hybrid Transform the first 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the first two dimensions");
				}

				if ( WTLevels[2] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[2]; rLevel++){
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySize-1,0,xSize-1);
						hwd.run();
					}
				}
				
				if ( WTLevels[0] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						ySubBandSize = ySubBandSizes[rLevel];
						hwd.setParameters(WTTypes[1],1,0,zSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[0],0,0,zSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}
				break;
			case 1: // Vertical, Espectral, Horizontal (X,Z,Y)
				// Checking parameters validity.
				if ( WTLevels[0] != WTLevels[2] ){ //In order to apply the Hybrid Transform the first 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the first two dimensions");
				}
				if ( WTLevels[1] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[1]; rLevel++){
						ySubBandSize = ySubBandSizes[rLevel];
						hwd.setParameters(WTTypes[1],1,0,zSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();
					}
				}
				if ( WTLevels[0] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}
				break;
			case 2: // Horizontal, Vertical, Espectral,  (Y,X,Z)
				// Checking parameters validity.
				if ( WTLevels[1] != WTLevels[0] ){ //In order to apply the Hybrid Transform the first 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the first two dimensions");
				}
				if ( WTLevels[2] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[2]; rLevel++){
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySize-1,0,xSize-1);
						hwd.run();
					}
				}

				if ( WTLevels[1] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[1]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						ySubBandSize = ySubBandSizes[rLevel];
						hwd.setParameters(WTTypes[0],0,0,zSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[1],1,0,zSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}
				break;
			case 3: // Horizontal, Espectral, Vertical (Y,Z,X)
				// Checking parameters validity.
				if ( WTLevels[1] != WTLevels[2] ){ //In order to apply the Hybrid Transform the first 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the first two dimensions");
				}
				if ( WTLevels[0] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[0],0,0,zSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}
				if ( WTLevels[1] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[1]; rLevel++){
						ySubBandSize = ySubBandSizes[rLevel];
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();
					}
				}
				break;
			case 4: // Espectral, Vertical, Horizontal (Z,X,Y)
				// Checking parameters validity.
				if ( WTLevels[2] != WTLevels[0] ){ //In order to apply the Hybrid Transform the first 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the first two dimensions");
				}
				if ( WTLevels[1] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[1]; rLevel++){
						ySubBandSize = ySubBandSizes[rLevel];
						hwd.setParameters(WTTypes[1],1,0,zSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();
					}
				}

				if ( WTLevels[2] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[2]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}
				break;
			case 5: // Espectral, Horizontal, Vertical (Z,Y,X)
				// Checking parameters validity.
				if ( WTLevels[2] != WTLevels[1] ){ //In order to apply the Hybrid Transform the first 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the first two dimensions");
				}
				if ( WTLevels[0] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[0],0,0,zSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}

				if ( WTLevels[2] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[2]; rLevel++){
						zSubBandSize = zSubBandSizes[rLevel];
						ySubBandSize = ySubBandSizes[rLevel];
						hwd.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();
					}
				}
				break;
			}
			break;
			
		case 1: //Hybrid rectangular/square (not piramidal) - 1D + 2D
			//Sorting cases by order
			switch( WTOrder ){
			case 0: // Vertical, Horizontal, Espectral (X,Y,Z)
				// Checking parameters validity.
				if ( WTLevels[1] != WTLevels[2] ){ //In order to apply the Hybrid Transform the last 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the last two dimensions");
				}
				if ( WTLevels[1] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[1]; rLevel++){
						ySubBandSize = ySubBandSizes[rLevel];
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();
					}
				}
				if ( WTLevels[0] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[0],0,0,zSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}
				break;
			case 1: // Vertical, Espectral, Horizontal (X,Z,Y)
				// Checking parameters validity.
				if ( WTLevels[2] != WTLevels[1] ){ //In order to apply the Hybrid Transform the last 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the last two dimensions");
				}

				if ( WTLevels[2] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[2]; rLevel++){
						ySubBandSize = ySubBandSizes[rLevel];
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();
					}
				}

				if ( WTLevels[0] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[0],0,0,zSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}
				break;
			case 2: // Horizontal, Vertical, Espectral,  (Y,X,Z)
				// Checking parameters validity.
				if ( WTLevels[0] != WTLevels[2] ){ //In order to apply the Hybrid Transform the last 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the last two dimensions");
				}
				if ( WTLevels[0] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}
				if ( WTLevels[1] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[1]; rLevel++){
						ySubBandSize = ySubBandSizes[rLevel];
						hwd.setParameters(WTTypes[1],1,0,zSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();
					}
				}
				break;
			case 3: // Horizontal, Espectral, Vertical (Y,Z,X)
				// Checking parameters validity.
				if ( WTLevels[2] != WTLevels[0] ){ //In order to apply the Hybrid Transform the last 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the last two dimensions");
				}
				if ( WTLevels[2] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[2]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}
				if ( WTLevels[1] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[1]; rLevel++){
						ySubBandSize = ySubBandSizes[rLevel];
						hwd.setParameters(WTTypes[1],1,0,zSize-1,0,ySubBandSize-1,0,xSize-1);
						hwd.run();
					}
				}
				break;
			case 4: // Espectral, Vertical, Horizontal (Z,X,Y)
				// Checking parameters validity.
				if ( WTLevels[0] != WTLevels[1] ){ //In order to apply the Hybrid Transform the last 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the last two dimensions");
				}
				if ( WTLevels[0] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						ySubBandSize = ySubBandSizes[rLevel];
						hwd.setParameters(WTTypes[1],1,0,zSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[0],0,0,zSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}

				if ( WTLevels[2] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[2]; rLevel++){
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySize-1,0,xSize-1);
						hwd.run();
					}
				}
				break;
			case 5: // Espectral, Horizontal, Vertical (Z,Y,X)
				// Checking parameters validity.
				if ( WTLevels[1] != WTLevels[0] ){ //In order to apply the Hybrid Transform the last 2 dimensions needs to be equal.
					throw new ErrorException("The transform levels to apply must be equal for the last two dimensions");
				}
				if ( WTLevels[1] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[1]; rLevel++){
						xSubBandSize = xSubBandSizes[rLevel];
						ySubBandSize = ySubBandSizes[rLevel];
						hwd.setParameters(WTTypes[0],0,0,zSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
						hwd.run();

						hwd.setParameters(WTTypes[1],1,0,zSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
						hwd.run();
					}
				}

				if ( WTLevels[2] > 0 ){
					for(int rLevel = 0; rLevel < WTLevels[2]; rLevel++){
						zSubBandSize = zSubBandSizes[rLevel];
						hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySize-1,0,xSize-1);
						hwd.run();
					}
				}
				break;
			}
			break;
		}

		//Return the DWDt image
		return(hwd.getImageSamples());
	}

	/**
	 * This function compute the Sub-Band Sizes for every dimension.
	 *
	 */
	private void computeSubBandSizes(){

		xSubBandSizes = new int[ WTLevels[0] > 0  ? WTLevels[0] : Math.max(WTLevels[1],WTLevels[2])];
		ySubBandSizes = new int[ WTLevels[1] > 0  ? WTLevels[1] : Math.max(WTLevels[0],WTLevels[2])];
		zSubBandSizes = new int[ WTLevels[2] > 0  ? WTLevels[2] : Math.max(WTLevels[0],WTLevels[1])];
		
		if (WTLevels[0] > 0 ){
			xSubBandSizes[WTLevels[0]-1] = xSize;
			for(int rLevel = WTLevels[0]-2; rLevel >= 0; rLevel--){
				xSubBandSizes[rLevel] = xSubBandSizes[rLevel+1] / 2 + xSubBandSizes[rLevel+1] % 2;
			}
		}else{
			for(int rLevel = Math.max(WTLevels[1],WTLevels[2])-1; rLevel >= 0; rLevel--){
				xSubBandSizes[rLevel] = xSize;
			}
		}
		if (WTLevels[1] > 0 ){
			ySubBandSizes[WTLevels[1]-1] = ySize;
			for(int rLevel = WTLevels[1]-2; rLevel >= 0; rLevel--){
				ySubBandSizes[rLevel] = ySubBandSizes[rLevel+1] / 2 + ySubBandSizes[rLevel+1] % 2;
			}
		}else{
			for(int rLevel = Math.max(WTLevels[0],WTLevels[2])-1; rLevel >= 0; rLevel--){
				ySubBandSizes[rLevel] = ySize;
			}
		}
		if (WTLevels[2] > 0 ){
			zSubBandSizes[WTLevels[2]-1] = zSize;
			for(int rLevel = WTLevels[2]-2; rLevel >= 0; rLevel--){
				zSubBandSizes[rLevel] = zSubBandSizes[rLevel+1] / 2 + zSubBandSizes[rLevel+1] % 2;
			}
		}else{
			for(int rLevel = Math.max(WTLevels[0],WTLevels[1])-1; rLevel >= 0; rLevel--){
				zSubBandSizes[rLevel] = zSize;
			}
		}

	}


}