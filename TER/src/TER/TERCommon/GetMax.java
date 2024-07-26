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

public class GetMax{
	
	public static int resolutionLevels(int resolutionLevels[]){
		int maxResolutionLevels = resolutionLevels[0];
		for(int k=1;k<resolutionLevels.length;k++){
			if (maxResolutionLevels<resolutionLevels[k]){
				maxResolutionLevels = resolutionLevels[k];
			}
		}
		return maxResolutionLevels;
	}
	
	public static int segmentIndex(int zSize, int blocksPerSegment[][]){
		int maxSegmentIndex = 0;
		for(int z=0;z<zSize;z++){
			if(maxSegmentIndex<blocksPerSegment[z].length){
				maxSegmentIndex = blocksPerSegment[z].length;
			}
		}
		return maxSegmentIndex;
	}
	
	public static int gaggleIndex(int zSize, int WTLevels[], int blocksPerSegment[][], int layersOffset[][][][][]){
		int maxGaggleIndex = 0;
		for(int z=0;z<zSize;z++){
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){			
				for(int rLevel=0;rLevel<=WTLevels[z];rLevel++){	
					if (layersOffset[z][segment][rLevel]!=null){
						if(maxGaggleIndex<layersOffset[z][segment][rLevel].length){
							maxGaggleIndex = layersOffset[z][segment][rLevel].length;
						}
					}		
				}
			}
		}
		return maxGaggleIndex;
	}
	
	public static int gaggleIndex(int zSize, int WTLevels[], int blocksPerSegment[][], long layersLocation[][][][][]){
		int maxGaggleIndex = 0;
		for(int z=0;z<zSize;z++){
			for(int segment=0;segment<blocksPerSegment[z].length;segment++){			
				for(int rLevel=0;rLevel<=WTLevels[z];rLevel++){	
					if (layersLocation[z][segment][rLevel]!=null){
						if(maxGaggleIndex<layersLocation[z][segment][rLevel].length){
							maxGaggleIndex = layersLocation[z][segment][rLevel].length;
						}
					}		
				}
			}
		}
		return maxGaggleIndex;
	}
}