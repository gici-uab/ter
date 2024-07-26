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
 * This class receives a set of parameters from the user to compute a predefined 3d Square (dyadic) Wavelet Transform executing the HyperWaveletTransform class concordingly. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class ForwardPyramidal3D{

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
	 * Discrete wavelet transform to be applied at each dimension.
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
	 * DWT levels to apply for each dimension.
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
	 * To know the order in which the transform is going to be applied.
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
	 * Constructor, receives the original image samples.
	 *
	 * @param imageSamples definition in {@link #imageSamples}
	 */
	public ForwardPyramidal3D(float[][][] imageSamples){
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
	 * Performs the requested 3D Square (dyadic) WT and returns the result image.
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

		//Level size
		int xSubBandSize = xSize;
		int ySubBandSize = ySize;
		int zSubBandSize = zSize;

		// Checking parameters validity.
		if ( WTLevels[0] < 0 || WTLevels[1] < 0 || WTLevels[2] < 0 ){ //All values have to be no negative.
			throw new ErrorException("All the transform levels to apply must be no negative.");
		}

		//Construction of the 3D-DWT Motor.
		ForwardDWTCore hwt = new ForwardDWTCore(imageSamples);

		// Checking parameters validity.
		if ( WTLevels[0] != WTLevels[1] || WTLevels[1] != WTLevels[2] ){ //In order to apply the dyadic transform, the levels in all dimensions must be equal.
			throw new ErrorException("The transform levels to apply must be equal for every dimension.");
		}

		//Sorting cases by order
		switch( WTOrder ){
		case 0: // Vertical, Horizontal, Espectral (X,Y,Z)
			for(int currentLevel = 0; currentLevel < WTLevels[0]; currentLevel++){
				hwt.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();
				
				hwt.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();
				//Size setting for the next level
				xSubBandSize = xSubBandSize / 2 + xSubBandSize % 2;
				ySubBandSize = ySubBandSize / 2 + ySubBandSize % 2;
				zSubBandSize = zSubBandSize / 2 + zSubBandSize % 2;
			}
			break;
		case 1: // Vertical, Espectral, Horizontal (X,Z,Y)
			for(int currentLevel = 0; currentLevel < WTLevels[0]; currentLevel++){
				hwt.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();
				//Size setting for the next level
				xSubBandSize = xSubBandSize / 2 + xSubBandSize % 2;
				ySubBandSize = ySubBandSize / 2 + ySubBandSize % 2;
				zSubBandSize = zSubBandSize / 2 + zSubBandSize % 2;
			}
			break;
		case 2: // Horizontal, Vertical, Espectral,  (Y,X,Z)
			for(int currentLevel = 0; currentLevel < WTLevels[0]; currentLevel++){
				hwt.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();
				//Size setting for the next level
				xSubBandSize = xSubBandSize / 2 + xSubBandSize % 2;
				ySubBandSize = ySubBandSize / 2 + ySubBandSize % 2;
				zSubBandSize = zSubBandSize / 2 + zSubBandSize % 2;
			}
			break;
		case 3: // Horizontal, Espectral, Vertical (Y,Z,X)
			for(int currentLevel = 0; currentLevel < WTLevels[0]; currentLevel++){
				hwt.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();
				//Size setting for the next level
				xSubBandSize = xSubBandSize / 2 + xSubBandSize % 2;
				ySubBandSize = ySubBandSize / 2 + ySubBandSize % 2;
				zSubBandSize = zSubBandSize / 2 + zSubBandSize % 2;
			}
			break;
		case 4: // Espectral, Vertical, Horizontal (Z,X,Y)
			for(int currentLevel = 0; currentLevel < WTLevels[0]; currentLevel++){
				hwt.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();
				//Size setting for the next level
				xSubBandSize = xSubBandSize / 2 + xSubBandSize % 2;
				ySubBandSize = ySubBandSize / 2 + ySubBandSize % 2;
				zSubBandSize = zSubBandSize / 2 + zSubBandSize % 2;
			}
			break;
		case 5: // Espectral, Horizontal, Vertical (Z,Y,X)
			for(int currentLevel = 0; currentLevel < WTLevels[0]; currentLevel++){
				hwt.setParameters(WTTypes[2],2,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[1],1,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();

				hwt.setParameters(WTTypes[0],0,0,zSubBandSize-1,0,ySubBandSize-1,0,xSubBandSize-1);
				hwt.run();
				//Size setting for the next level
				xSubBandSize = xSubBandSize / 2 + xSubBandSize % 2;
				ySubBandSize = ySubBandSize / 2 + ySubBandSize % 2;
				zSubBandSize = zSubBandSize / 2 + zSubBandSize % 2;
			}
			break;
		}
		//Return the DWT image
		return(hwt.getImageSamples());

	}

}