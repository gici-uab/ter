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
package TER.TERcoder;

import TER.TERDefaultValues;

/**
 * This class assures that the values given by the user are compatible with the Recommendation for image data coding of the CCSDS. 
 *  
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class VerifyRecommendationParameters{
	
	public static boolean verifyRecommendationParameters(
			int[] imageExtensionType,
			int[] WTType,
			int[] WTLevels,
			int[] WTOrder,
			int[] gaggleDCSize,
			int[] gaggleACSize,
			int[] idDC, 
			int[] idAC,
			int[] entropyAC,
			int[] resolutionLevels,
			int LSType		
	){
		
		boolean verified = true;
		
		if (imageExtensionType!=null){
			if (!verifyParameter(imageExtensionType,TERDefaultValues.imageExtensionType,TERDefaultValues.imageExtensionType)){
				verified = false;
				System.out.println("The parameter given for the image extension type is out of the Recommendation");
			}
		}

		if (WTType!=null){
			if (!verifyParameter(WTType,3,4)){
				verified = false;
				System.out.println("The parameter given for the wavelet transform type is out of the Recommendation");
			}
		}
		
		if (WTLevels!=null){
			if (!verifyParameter(WTLevels,3,3)){
				verified = false;
				System.out.println("The parameter given for the wavelet transform levels is out of the Recommendation");
			}
		}
		
		if (WTOrder!=null){
			if (!verifyParameter(WTOrder,TERDefaultValues.WTOrder,TERDefaultValues.WTOrder)){
				verified = false;
				System.out.println("The parameter given for the wavelet transform levels is out of the Recommendation");
			}
		}
		
		if (gaggleDCSize!=null){
			if (!verifyParameter(gaggleDCSize,16,16)){
				verified = false;
				System.out.println("The parameter given for the DC gaggle size is out of the Recommendation");
			}
		}
		
		if (gaggleACSize!=null){
			if (!verifyParameter(gaggleACSize,16,16)){
				verified = false;
				System.out.println("The parameter given for the AC gaggle size is out of the Recommendation");
			}
		}
		
		if (idDC!=null){
			if (!verifyParameter(idDC,0,0)){
				verified = false;
				System.out.println("The parameter given for the idDC is out of the Recommendation");
			}
		}
		
		if (idAC!=null){
			if (!verifyParameter(idAC,0,0)){
				verified = false;
				System.out.println("The parameter given for the idAC is out of the Recommendation");
			}
		}
		
		if (entropyAC!=null){
			if (!verifyParameter(entropyAC,1,1)){
				verified = false;
				System.out.println("The parameter given for the entropy of AC components is out of the Recommendation");
			}
		}

		if (resolutionLevels!=null){
			if (!verifyParameter(resolutionLevels,3,3)){
				verified = false;
				System.out.println("The parameter given for the resolution levels is out of the Recommendation");
			}
		}
		
		if (LSType!=0){
			verified = false;
			System.out.println("The parameter given for the level shift is out of the Recommendation");
		}

		
		return verified;
		
	}
	
	/**
	 * Verify Parameters defined in this class
	 *
	 * @param  inputParameter definition in this class
	 * @param  minValue minimum allowed values
	 * @param  maxValue maximum allowed values
	 *
	 * @return a boolean that indicates if the parameters are allowed
	 */
	public static boolean verifyParameter(int[] inputParameter, int minValue, int maxValue){
		
		boolean verified = true; 
		
		if (inputParameter != null){			
			for(int k=0; k < inputParameter.length ; k++){
				if ( inputParameter[k] < minValue || inputParameter[k] > maxValue){
					verified = false;
				}
			}
			
		} 
		
		return verified;
		
	}
}