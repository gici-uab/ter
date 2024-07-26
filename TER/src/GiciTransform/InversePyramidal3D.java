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
 * This class receives a set of parameters to compute an inverse 3d Square (dyadic)Wavelet Transform executing the HyperWaveletDetransform class concordingly. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class InversePyramidal3D{

	/**
	 * Image samples (index meaning [z][y][x]).
	 */
	float[][][] imageSamples = null;

	/**
	 * Definition in {@link ForwardPyramidal3D#zSize}
	 */
	int zSize;

	/**
	 * Definition in {@link ForwardPyramidal3D#ySize}
	 */
	int ySize;

	/**
	 * Definition in {@link ForwardPyramidal3D#xSize}
	 */
	int xSize;

	/**
	 * Discrete wavelet detransform to be applied at each dimension.
	 *<p>
	 * Only the 3 first elements of the array are going to be taken, the rest is ignored.<br>
	 *   <ul>
	 *     <li> WTLevels[0] X axis
	 *     <li> WTLevels[1] Y axis
	 *     <li> WTLevels[2] Z axis
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
	public InversePyramidal3D(float[][][] imageSamples){
		//Image data copy
		this.imageSamples = imageSamples;
		zSize = imageSamples.length;
		ySize = imageSamples[0].length;
		xSize = imageSamples[0][0].length;
	}

	/**
	 * Set the parameters used to apply the discrete wavelet transform, legacy use, WTOrder is going to be set to default values.
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
		this.WTOrder = 0; //Vertical - Horizontal - Espectral
	}

	/**
	 * Set the parameters used to apply the discrete wavelet transform.
	 *
	 * @param WTTypes definition in {@link #WTTypes}
	 * @param WTLevels definition in {@link #WTLevels}
	 * @param WTOrder definition in {@link #WTOrder}
	 */
	public void setParameters(int[] WTTypes, int[] WTLevels, int WTOrder){
		parametersSet = true;
		//Parameters copy
		this.WTTypes = WTTypes;
		this.WTLevels = WTLevels;
		this.WTOrder = WTOrder;
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

		// Checking parameters validity.
		if ( WTLevels[0] != WTLevels[1] || WTLevels[1] != WTLevels[2] ){ //In order to apply the dyadic transform, the levels in all dimensions must be equal.
			throw new ErrorException("The transform levels to apply must be equal for every dimension");
		}
		//Apply DWT only if is specified
		if(WTLevels[0] > 0){
			//Sorting cases by order
			switch( WTOrder ){
			case 0: // Vertical, Horizontal, Espectral (X,Y,Z)
				for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
					//Level size
					xSubBandSize = xSubBandSizes[rLevel];
					ySubBandSize = ySubBandSizes[rLevel];
					zSubBandSize = zSubBandSizes[rLevel];
					hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

					hwd.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();
	
					hwd.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();
				}
				break;
			case 1: // Vertical, Espectral, Horizontal (X,Z,Y)
				for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
					xSubBandSize = xSubBandSizes[rLevel];
					ySubBandSize = ySubBandSizes[rLevel];
					zSubBandSize = zSubBandSizes[rLevel];
					hwd.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();
	
					hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

					hwd.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();
				}
				break;
			case 2: // Horizontal, Vertical, Espectral,  (Y,X,Z)
				for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
					xSubBandSize = xSubBandSizes[rLevel];
					ySubBandSize = ySubBandSizes[rLevel];
					zSubBandSize = zSubBandSizes[rLevel];
					hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

					hwd.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

					hwd.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

				}
				break;
			case 3: // Horizontal, Espectral, Vertical (Y,Z,X)
				for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
					xSubBandSize = xSubBandSizes[rLevel];
					ySubBandSize = ySubBandSizes[rLevel];
					zSubBandSize = zSubBandSizes[rLevel];
					hwd.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

					hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

					hwd.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();
	
				}
				break;
			case 4: // Espectral, Vertical, Horizontal (Z,X,Y)
				for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
					xSubBandSize = xSubBandSizes[rLevel];
					ySubBandSize = ySubBandSizes[rLevel];
					zSubBandSize = zSubBandSizes[rLevel];
					hwd.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

					hwd.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

					hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();
				}
				break;
			case 5: // Espectral, Horizontal, Vertical (Z,Y,X)
				for(int rLevel = 0; rLevel < WTLevels[0]; rLevel++){
					xSubBandSize = xSubBandSizes[rLevel];
					ySubBandSize = ySubBandSizes[rLevel];
					zSubBandSize = zSubBandSizes[rLevel];
					hwd.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

					hwd.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();

					hwd.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
					hwd.run();
				}
				break;
			}
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