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
package TER.TERCommon;

public class GaggleUtilities{
	
	public static int getGagglesPerSegment(int channel, int segment, int gaggleSize, int blocksPerSegment[][]){
		int numberOfGaggles = blocksPerSegment[channel][segment];
		if (gaggleSize!=0){
			numberOfGaggles = blocksPerSegment[channel][segment] / gaggleSize;
			if ( blocksPerSegment[channel][segment]%gaggleSize != 0 ){
				numberOfGaggles++ ;
			}
		}
		return numberOfGaggles;
	}
}