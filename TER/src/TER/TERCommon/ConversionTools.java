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

/**
 * This class contain some conversion tools, they convert some file measures in the corresponding segByteLimit value 
 *  
 * @author Group on Interactive Coding of Images (GICI)
 * @version 2.0
 */
public class ConversionTools{
	
	/**
	 * Given the bits per pixel computes the number of bytes that must be used to encode each segment
	 * 
	 * @param bps float array that contains the bits per pixel of each segment
	 * @param blocksPerSegment definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels} 
	 * @param ySize image height
	 * @param xSize image width
	 * 
	 * @return an integer containing the number of bytes that must be used to encode each segment
	 */
	public static int[][] getSegByteLimitFromBPS(float[] bps, int[][] blocksPerSegment, int[] WTLevels, int ySize, int xSize, int[] pixelBitDepth){
		//This function is only designed for cases when the same number of DWT levels are applied for all the levels
		int zSize = blocksPerSegment.length;
		int[][] segByteLimit = new int[zSize][];
		float reorganizedBPP[][] = new float[zSize][];
		int readedBPP = 0;
		
		for (int z=0;z<zSize;z++){
			int segmentPerChannel = blocksPerSegment[z].length;
			segByteLimit[z] = new int[segmentPerChannel];
			reorganizedBPP[z] = new float[segmentPerChannel];
			for (int segment=0;segment<segmentPerChannel;segment++){
				if (readedBPP<bps.length){
					reorganizedBPP[z][segment] = bps[readedBPP];
				} else {
					reorganizedBPP[z][segment] = bps[bps.length - 1];
				}
				readedBPP++;
			}
			
			int sideBlockSize = (int) 1<< WTLevels[z] ;
			
			int blockNumber = 0;
			int linesToAdd = 0;
			int columnsToAdd = 0;
			
			if( ySize%sideBlockSize!=0){ 
				linesToAdd = sideBlockSize - ySize%sideBlockSize;
			}
			if( xSize%sideBlockSize!=0 ){
				columnsToAdd = sideBlockSize - xSize%sideBlockSize;
			}
			
			int yExtendedSize = ySize + linesToAdd ;
			int xExtendedSize = xSize + columnsToAdd;
			int xResidualSubbandSize = xExtendedSize / sideBlockSize;
			
			if (xSize==xExtendedSize && ySize==yExtendedSize){
				for(int segment=0;segment<segmentPerChannel;segment++){
					int pixelsPerSegment = blocksPerSegment[z][segment]*sideBlockSize*sideBlockSize;
					segByteLimit[z][segment] = Math.round(pixelsPerSegment*reorganizedBPP[z][segment]/8);
				}
			} else {
				for(int segment=0;segment<segmentPerChannel;segment++){
					int pixelsPerSegment = 0;
					for(int block=0;block<blocksPerSegment[z][segment];block++){
						int xInit = (blockNumber%xResidualSubbandSize)*sideBlockSize;
						int yInit = (blockNumber/xResidualSubbandSize)*sideBlockSize;
						int xBlockSize = sideBlockSize;
						if (xInit+sideBlockSize>xSize){
							xBlockSize = xSize - xInit;
						}
						int yBlockSize = sideBlockSize;
						if (yInit+sideBlockSize>ySize){
							yBlockSize = ySize - yInit;
						}
						pixelsPerSegment += xBlockSize*yBlockSize;
						blockNumber++;
					}
					segByteLimit[z][segment] = Math.round(pixelsPerSegment*reorganizedBPP[z][segment]/8);
				}
			}
		}
		return segByteLimit;
	}
	/**
	 * Given the compression ratio computes the number of bytes that must be used to encode each segment
	 * 
	 * @param compressionFactor float array that contains the compression factor for each segment
	 * @param pixelBitDepth pixel bit depth of the input image
	 * @param blocksPerSegment definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels} * @param blocksPerSegment
	 * @param ySize image height
	 * @param xSize image width
	 * 
	 * @return an integer containing the number of bytes that must be used to encode each segment
	 */
	public static int[][] getSegByteLimitFromCompressionFactor(float[] compressionFactor, int[] pixelBitDepth, int[][] blocksPerSegment, int[] WTLevels, int ySize, int xSize){
		// This function is only designed for cases when the same number of DWT levels are applied for all the levels
		int zSize = blocksPerSegment.length;
		int[][] segByteLimit = new int[zSize][];
		float reorganizedCF[][] = new float[zSize][];
		int readedCF = 0;
		
		for (int z=0;z<zSize;z++){
			int segmentPerChannel = blocksPerSegment[z].length;
			segByteLimit[z] = new int[segmentPerChannel];
			reorganizedCF[z] = new float[segmentPerChannel];
			for (int segment=0;segment<segmentPerChannel;segment++){
				if (readedCF<compressionFactor.length){
					reorganizedCF[z][segment] = compressionFactor[readedCF];
				} else {
					reorganizedCF[z][segment] = compressionFactor[compressionFactor.length - 1];
				}
				readedCF++;
			}
			
			int sideBlockSize = (int) 1<< WTLevels[z] ;
			int blockNumber = 0;
			
			int linesToAdd = 0;
			int columnsToAdd = 0;
			
			if( ySize%sideBlockSize!=0){ 
				linesToAdd = sideBlockSize - ySize%sideBlockSize;
			}
			if( xSize%sideBlockSize!=0 ){
				columnsToAdd = sideBlockSize - xSize%sideBlockSize;
			}
			
			int yExtendedSize = ySize + linesToAdd ;
			int xExtendedSize = xSize + columnsToAdd;
			int xResidualSubbandSize = xExtendedSize / sideBlockSize;
			
			if (xSize==xExtendedSize && ySize==yExtendedSize){
				for(int segment=0;segment<segmentPerChannel;segment++){
					int pixelsPerSegment = blocksPerSegment[z][segment]*sideBlockSize*sideBlockSize;
					float bpp = pixelBitDepth[z] / reorganizedCF[z][segment];
					segByteLimit[z][segment] = Math.round(pixelsPerSegment*bpp/8);
				}
			} else {
				for(int segment=0;segment<segmentPerChannel;segment++){
					int pixelsPerSegment = 0;
					for(int block=0;block<blocksPerSegment[z][segment];block++){
						int xInit = (blockNumber%xResidualSubbandSize)*sideBlockSize;
						int yInit = (blockNumber/xResidualSubbandSize)*sideBlockSize;
						int xBlockSize = sideBlockSize;
						if (xInit+sideBlockSize>xSize){
							xBlockSize = xSize - xInit;
						}
						int yBlockSize = sideBlockSize;
						if (yInit+sideBlockSize>ySize){
							yBlockSize = ySize - yInit;
						}
						pixelsPerSegment += xBlockSize*yBlockSize;
						blockNumber++;
					}
					float bpp = pixelBitDepth[z] / reorganizedCF[z][segment];
					segByteLimit[z][segment] = Math.round(pixelsPerSegment*bpp/8);
				}
			}
		}
		return segByteLimit;
	}

	public static int getTargetBytesFromBPP(float bpp,int ySize, int xSize){
		int targetBytes = 0;
		targetBytes = (int) (ySize*xSize*bpp/8);
		
		return targetBytes;
	}
	
	public static float getBpppbFromTargetBytes(int zSize, int ySize, int xSize, int targetBytes){
		float bpppb=0;
		
		bpppb = (float)(targetBytes*8)/ ((float) zSize*ySize*xSize);
		
		return bpppb;
	}
	
	public static int[] getTargetBytesFromBpppb(float[] bpppb, int zSize, int ySize, int xSize){
		int[] targetBytes = new int[bpppb.length];
		
		for (int k=0;k<targetBytes.length;k++){
			targetBytes[k] = (int) (zSize*ySize*xSize*bpppb[k]/8);
		}
		
		return targetBytes;
	}
}
