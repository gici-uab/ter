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

public class SearchTools{
	
	public static int[] findFirst(boolean condition[][], int gaggleSize[]){
		int positions[] = new int[2];
		boolean notFound = true;
		for(int k1=0;k1<condition.length && notFound;k1++){
			for(int k2=0;k2<condition[k1].length && notFound;k2++){
				if (condition[k1][k2]){
					positions[0] = k1;
					positions[1] = (k2*gaggleSize[k1]);
					notFound = false;
				}
			}
		}
		return positions;
	}
	
	
	public static int[] findLast(boolean condition[][], int gaggleSize[]){
		int positions[] = new int[2];
		boolean notFound = true;
		for(int k1=condition.length-1;k1>=0 && notFound;k1--){
			for(int k2=condition[k1].length-1;k2>=0 && notFound;k2--){
				if (condition[k1][k2]){
					positions[0] = k1;
					positions[1] = (k2+1)*gaggleSize[k1] -1 ;
					notFound = false;
				}
			}
		}
		return positions;
	}
	
	public static int getBlockPosition(int blockCoordinates[],int blocksPerSegment[]){
		int pos=0;
		int segment = 0;	
		for(segment=0;segment<blockCoordinates[0];segment++){
			pos += blocksPerSegment[segment];
		}
		pos += (blockCoordinates[1]);
		return pos;
		
	}
}