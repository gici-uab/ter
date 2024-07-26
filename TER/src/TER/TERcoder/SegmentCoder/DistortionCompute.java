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
package TER.TERcoder.SegmentCoder;


/**
 * This class computes the distortion . Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; initValues<br>
 * &nbsp; improvedValue<br>
 * &nbsp; get distortion<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0
 */
public class DistortionCompute{
	
	/**
	 * Indicates which distortion measure is selected by the user for controlling distortion.
	 * <br>
	 * Valid values are the following.<br>
	 *  <ul>
	 *    <li> 0- No measure of distortion is going to be controlled.
	 *    <li> 1- Measures the mean of the differences in absolute value.
	 *    <li> 2- Measures the mean of the differences in quadratic value.	 
	 *  </ul>
	 *
	 */
	int distortionMeasure;
	
	/**
	 * Indicates the difference between the original image and the reconstred according to the selected distortion.
	 * <p>
	 * Negatives values are not allowed
	 */
	long difference;
	
	/**
	 * This value represents the number of pixels controlled in the distortion measure.
	 * <p>
	 * Negatives values are not allowed
	 */
	long numberOfPixels;
	
	/**
	 * This value represents the value given to the pixels that have not received information yet.
	 */
	int defaultReconstructionValue;
	
	/**
	 * Constructor of DistortionCalculus. It receives the distortion measure and the
	 * the default value for reconstruction at the beginning of the decoding process.
	 * The distortion value is computed respect to the transformed image.
	 *
	 * @param distortionMeasure definition in {@link #distortionMeasure}
	 * @param defaultReconstructionValue definition in {@link #defaultReconstructionValue}
	 * 
	 */
	public DistortionCompute(int distortionMeasure, int defaultReconstructionValue){
		this.distortionMeasure = distortionMeasure;
		this.defaultReconstructionValue = defaultReconstructionValue;
		
		difference = 0;
		numberOfPixels = 0;
	}
	
	/**
	 * This function initializes the value of a pixel in the region of the transformed image where the distortion is computed.
	 *
	 * @param originalValue indicates the original value of a pixels in the transformed image.
	 *
	 */
	public void initNewValue(int originalValue){
		if ( distortionMeasure == 1 ){// L1 norm is required
			difference += (long) Math.abs(originalValue - defaultReconstructionValue);
		} else if ( distortionMeasure == 2 ){ //L2 norm (i.e. MSE ) is required
			difference += (long) (originalValue - defaultReconstructionValue)*(originalValue - defaultReconstructionValue);
		}
		numberOfPixels ++;
	}
	
	/**
	 * This function recomputes the distortion for a pixel whose approximation value is improved during the codification process.
	 *
	 * @param previousApproximation indicates the value previously approximated
	 * @param currentApproximation indicates the value currently appoximates
	 * @param originalValue indicates the original value
	 */
	public void improvedValue(int previousApproximation, int currentApproximation, int originalValue){
		//first is substracted the distortion given by the previous approximation and then is added the
		//distortion given by the new approximation
		if ( distortionMeasure == 1 ){// L1 norm is required
			difference -= (long) Math.abs(originalValue - previousApproximation);
			difference += (long) Math.abs(originalValue - currentApproximation);
		} else if ( distortionMeasure == 2 ){ //L2 norm (i.e. MSE ) is required
			difference -= (long) (originalValue - previousApproximation)*(originalValue - previousApproximation);
			difference += (long) (originalValue - currentApproximation)*(originalValue - currentApproximation) ;
		}		
	}
	
	/**
	 * This function computes the distortion selected by the user.
	 *
	 * @return a float value containing the value of the distortion according to the measure selected by the user.
	 */ 
	public float getDistortion(){
		float distortion = 1/0F;
		if ( distortionMeasure == 1 || distortionMeasure == 2 ){ //L1 norm or L2 norm (i.e. MSE )is required
			if ( numberOfPixels > 0 ){
				distortion = difference / (float) numberOfPixels;
			}
		} 
		return distortion;
	}
	
}
