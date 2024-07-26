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

import GiciException.*;

/**
 * This class verifies and prepares the parameters in order to encode one or several segments.<br>
 * Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class SegmentConstruct{

	/**
	 * Number of channels of the image.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int zSize;
	
	/**
	 * Definition {@link SegmentCode2D#blocksPerSegment}
	 */
	int[][] blocksPerSegment = null;
	/**
	 * Stores the maximum number of blocks that can be contained in a segment
	 */
	public static int maxBlocksPerSegment =  (int) 1<< 20;//1048576; //Math.pow(2,20);
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 */
	int[] WTLevels = null ;

	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */	 
	boolean parametersSet = false;

	/**
	 * Image height.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int yOriginalSize;

	/**
	 * Image width.
	 * <p>
	 * Negative values are not allowed for this field.
	 */
	int xOriginalSize;	


	/**
	 * Constructor of the class
	 */
	 public SegmentConstruct(){
		
	}

	 /**
	  * Verify Parameters
	  * 
	  * @param Part2Flag definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part2Flag}
	  * @param Part3Flag definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part3Flag}
	  * @param Part4Flag definition in {@link TER.TERcoder.WriteFile.RecommendedHeader#part4Flag}
	  * @param segByteLimit definition in {@link SegmentCode2D#segByteLimit}
	  * @param DCStop definition in {@link SegmentCode2D#DCStop}
	  * @param bitPlaneStop definition in {@link SegmentCode2D#bitPlaneStop}
	  * @param stageStop definition in {@link SegmentCode2D#stageStop}
	  * @param UseFill definition in  {@link TER.TERcoder.WriteFile.RecommendedOrder#useFill}
	  * @param blocksPerSegment definition in {@link SegmentCode2D#blocksPerSegment}
	  * @param optDCSelect definition in {@link SegmentCode2D#optDCSelect}
	  * @param optACSelect definition in {@link SegmentCode2D#optACSelect}
	  * @param SignedPixels definition in {@link TER.TERcoder.Coder#signedPixels}
	  * @param TransposeImg definition in {@link GiciTransform.TransposeImage#transposeImg}
	  * @param CodeWordLength definition in {@link TER.TERcoder.WriteFile.RecommendedOrder#codeWordLength} 
	  * @param gaggleDCSize definition in {@link SegmentCode2D#gaggleDCSize}
	  * @param gaggleACSize definition in {@link SegmentCode2D#gaggleACSize}
	  * @param idDC definition in {@link SegmentCode2D#idDC}
	  * @param idAC definition in {@link SegmentCode2D#idAC}
	  * @param distortionMeasure definition in {@link SegmentCode2D#distortionMeasure}
	  * @param entropyAC definition in {@link SegmentCode2D#entropyAC}
	  * @param resolutionLevels definition in {@link SegmentCode2D#resolutionLevels}
	  * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	  * 
	  * @return a boolean that indicates if the parameters are allowed
	  */
	public static boolean verifyParameters(
		int[] Part2Flag, int[] Part3Flag, int[] Part4Flag, int[] segByteLimit,  int[] DCStop, int[] bitPlaneStop, 
		int[] stageStop, int[] UseFill,	int[] blocksPerSegment, int[] optDCSelect,int[] optACSelect, 
		int[] SignedPixels, int[] TransposeImg, int[] CodeWordLength, int[] gaggleDCSize, int[] gaggleACSize, 
		int[] idDC,	int[] idAC, int[] distortionMeasure, int[] entropyAC, int[] resolutionLevels, int[] WTLevels	){
		
		boolean verified = true;
		if( Part2Flag!=null ){
				for(int k=0; k<Part2Flag.length ; k++){
						if( Part2Flag[k]<0 || Part2Flag[k]>1){
								System.out.println("Invalid parameters for Part2Flag");
								verified = false;
						}
				}
		}
		if( Part3Flag!=null ){
				for(int k=0; k<Part3Flag.length ; k++){
						if( Part3Flag[k]<0 || Part3Flag[k]>1){
								System.out.println("Invalid parameters for Part3Flag");
								verified = false;
						}
				}
		}
		if( Part4Flag!=null ){
				for(int k=0; k<Part4Flag.length ; k++){
						if( Part4Flag[k]<0 || Part4Flag[k]>1){
								System.out.println("Invalid parameters for Part4Flag");
								verified = false;
						}
				}
		}
		if( segByteLimit!=null ){
				for(int k=0; k<segByteLimit.length ; k++){
						if( segByteLimit[k]<=0 ){
								System.out.println("Invalid parameters for SegByteLimit");
								verified = false;
						}
				}
		}
		if( DCStop!=null ){
				for(int k=0; k<DCStop.length ; k++){
						if( DCStop[k]<0 || DCStop[k]>1){
								System.out.println("Invalid parameters for DCStop");
								verified = false;
						}
				}
		}
		if( bitPlaneStop!=null ){
				for(int k=0; k<bitPlaneStop.length ; k++){
						if( bitPlaneStop[k]<0 ){
								System.out.println("Invalid parameters for BitPlaneStop");
								verified = false;
						}
				}
		}
	  if( stageStop!=null ){
				for(int k=0; k<stageStop.length ; k++){
						if( stageStop[k]<0 ){
								System.out.println("Invalid parameters for StageStop");
								verified = false;
						}
				}
		}
		if( UseFill!=null ){
				for(int k=0; k<UseFill.length ; k++){
						if( UseFill[k]<0 || UseFill[k]>1){
								System.out.println("Invalid parameters for UseFill");
								verified = false;
						}
				}
		}
		if( blocksPerSegment!=null ){
				for(int k=0; k<blocksPerSegment.length ; k++){
						if( blocksPerSegment[k]<1 || blocksPerSegment[k]>maxBlocksPerSegment){
								System.out.println("Invalid parameters for BlocksPerSegment");
								verified = false;
						}
				}
		}
		if( optDCSelect!=null ){
				for(int k=0; k<optDCSelect.length ; k++){
						if( optDCSelect[k]<0 || optDCSelect[k]>4){
								System.out.println("Invalid parameters for optDCSelect");
								verified = false;
						}
				}
		}		
		if( optACSelect!=null ){
				for(int k=0; k<optACSelect.length ; k++){
						if( optACSelect[k]<0 || optACSelect[k]>4){
								System.out.println("Invalid parameters for OptACSelect");
								verified = false;
						}
				}
		}
		if( SignedPixels!=null ){
				for(int k=0; k<SignedPixels.length ; k++){
						if( SignedPixels[k]<0 || SignedPixels[k]>1){
								System.out.println("Invalid parameters for SignedPixels");
								verified = false;
						}
				}
		}
		if( TransposeImg!=null ){
				for(int k=0; k<TransposeImg.length ; k++){
						if( TransposeImg[k]<0 || TransposeImg[k]>1){
								System.out.println("Invalid parameters for TransposeImg");
								verified = false;
						}
				}
		}
		if( CodeWordLength!=null ){
				for(int k=0; k<CodeWordLength.length ; k++){
						if( CodeWordLength[k]<0 || CodeWordLength[k]>4){
								System.out.println("Invalid parameters for CodeWordLength");
								verified = false;
						}
				}
		}
		if( gaggleDCSize!=null ){
				for(int k=0; k<gaggleDCSize.length ; k++){
						if( gaggleDCSize[k]<0 || gaggleDCSize[k]>maxBlocksPerSegment){
								System.out.println("Invalid parameters for gaggleDCSize");
								verified = false;
						}
				}
		}
		if( gaggleACSize!=null ){
				for(int k=0; k<gaggleACSize.length ; k++){
						if( gaggleACSize[k]<0 || gaggleACSize[k]>maxBlocksPerSegment){
								System.out.println("Invalid parameters for gaggleACSize");
								verified = false;
						}
				}
		}
		if( idDC!=null ){
				for(int k=0; k<idDC.length ; k++){
						if( idDC[k]<0 || idDC[k]>maxBlocksPerSegment){
								System.out.println("Invalid parameters for idDC");
								verified = false;
						}
				}
		}
		if( idAC!=null ){
				for(int k=0; k<idAC.length ; k++){
						if( idAC[k]<0 || idAC[k]>maxBlocksPerSegment){
								System.out.println("Invalid parameters for idAC");
								verified = false;
						}
				}
		}
		if( distortionMeasure!=null ){
				for(int k=0; k<distortionMeasure.length ; k++){
						if( distortionMeasure[k]<0 || distortionMeasure[k]>2){
								System.out.println("Invalid parameters for distortionMeasure");
								verified = false;
						}
				}
		}
		if( entropyAC!=null ){
			for(int k=0; k<entropyAC.length ; k++){
				if( entropyAC[k]<0 || entropyAC[k]>2){
					System.out.println("Invalid parameters for entropyAC");
					verified = false;
				}
			}
		}		
		if( resolutionLevels!=null ){
			int maxResolutionLevels = 0;
			for(int k=0; k<WTLevels.length ; k++){
				if (maxResolutionLevels < WTLevels[k] ){
					maxResolutionLevels = WTLevels[k];
				} 
			}
			for(int k=0; k<resolutionLevels.length ; k++){
				if( resolutionLevels[k]<0 || resolutionLevels[k]>maxResolutionLevels){
					System.out.println("Invalid parameters for resolutionLevels");
					verified = false;
				}
			}
		}
		
		
		return verified;
	}
	
	public static int[][] setGaggleSize(int[] userGaggleSize, int defaultValue, int[][] blocksPerSegment, int[] WTLevels, int xSize, int ySize, int zSize){
		int[][] gaggleSize = new int[zSize][];

		if (blocksPerSegment!=null){
			if (userGaggleSize!=null){
				int counter = 0;
				for(int z=0; z<zSize ; z++){
					gaggleSize[z] = new int[blocksPerSegment[z].length];
					int squaredBlockSize = ((int) 1 << WTLevels[z]); //2^(WTLevels[z])
					int maxBlocks =  getBlocksPerChannel(squaredBlockSize,xSize,ySize);
					for(int gaggle=0;gaggle<blocksPerSegment[z].length;gaggle++){
						int blocks = 0;
						if (counter<userGaggleSize.length){
							if (userGaggleSize[counter]>0){
								blocks = userGaggleSize[counter];
							} else {
								blocks = maxBlocks;
							}
						} else {
							if (userGaggleSize[userGaggleSize.length-1]>0){
								blocks = userGaggleSize[userGaggleSize.length-1];
							} else {
								blocks = maxBlocks;
							}
						}
						if (blocks>maxBlocks){
							blocks = maxBlocks;
						}
						gaggleSize[z][gaggle] = blocks;
						counter++;
					}
				}
			} else {//default values are set
				for(int z=0; z<zSize ; z++){
					gaggleSize[z] = new int[blocksPerSegment[z].length];
					int squaredBlockSize = ((int) 1 << WTLevels[z]); //2^(WTLevels[z])
					int blocks =  getBlocksPerChannel(squaredBlockSize,xSize,ySize);
					if (blocks>defaultValue){
						blocks = defaultValue;
					}
					for(int gaggle=0;gaggle<blocksPerSegment[z].length;gaggle++){
						gaggleSize[z][gaggle] = blocks;
					}
				}
			}
		}
		return gaggleSize;
	}
	/**
	 * Set the parameter BlocksPerSegment. This parameters should be the first to be set.
	 *
	 * @param  blocksPerSegment definition in {@link SegmentCode2D#blocksPerSegment} 
	 * @param  WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param  xSize integer containing the width of each original component
	 * @param  ySize integer containing the height of each original component
	 * @param  zSize integer containing the height of each original component
	 *
	 * @return an integer array containing the number of blocks employed to encode each segment for each channel.
	 */
	public static int[][] setBlocksPerSegment(int[] blocksPerSegment, int[] WTLevels, int xSize, int ySize, int zSize){
			
		int[][] blocksSegment = new int[zSize][];
		if( blocksPerSegment != null ){
			
			for(int z=0; z<zSize ; z++){//first, the number of needed segments for each channel is counted
				int squaredBlockSize = ((int) 1 << WTLevels[z]); //2^(WTLevels[z])
				int blocksPerChannel = getBlocksPerChannel(squaredBlockSize,xSize,ySize);
				int remainingBlocks = blocksPerChannel;
				int previousSegments = 0;
				for(int k=0;k<z;k++){
					previousSegments += blocksSegment[k].length;
				}
				int requiredSegments = 0;
				int nextSize;
				while (remainingBlocks > 0 ){
					if ( (requiredSegments + previousSegments) >= blocksPerSegment.length ){
						nextSize = blocksPerSegment[(blocksPerSegment.length)-1];
					} else {
						nextSize = blocksPerSegment[requiredSegments + previousSegments];
					}
					remainingBlocks -= nextSize;
					requiredSegments++;
				}
				blocksSegment[z] = new int[requiredSegments];
				remainingBlocks = blocksPerChannel;
				requiredSegments = 0;
				while (remainingBlocks > 0 ){
					if ( (requiredSegments + previousSegments) >= blocksPerSegment.length ){
						nextSize = blocksPerSegment[(blocksPerSegment.length)-1];
					} else {
						nextSize = blocksPerSegment[requiredSegments + previousSegments];
					}
					
					if( remainingBlocks >= nextSize){
						blocksSegment[z][requiredSegments] = nextSize;			
					} else{//if there are not enough remaining blocks in the component to create a segment
						// of the desired size, the segment is composed by the remaining blocks 	
						blocksSegment[z][requiredSegments] = remainingBlocks;
					}
					remainingBlocks -= nextSize;
					requiredSegments++;
				}
				
			}
			
		}else{//default parameters are set

			for(int z=0; z<zSize ; z++){//first, the number of needed segments for each channel is counted
				
				int squaredBlockSize = ((int) 1 << WTLevels[z]); //2^(WTLevels[z])
				int blocksPerChannel = getBlocksPerChannel(squaredBlockSize,xSize,ySize);
				if( blocksPerChannel <= maxBlocksPerSegment ){
					// only a segment is required
					blocksSegment[z] = new int[1];
					blocksSegment[z][0] = blocksPerChannel;
				}
				else{
					// more than a segment is required
					int requiredSegments = blocksPerChannel/maxBlocksPerSegment;
					if( blocksPerChannel/maxBlocksPerSegment != 0 ){
						requiredSegments++;
					}
					blocksSegment[z] = new int[requiredSegments];
					int remainingBlocks = blocksPerChannel;
					for(int k=0; k<blocksPerChannel/maxBlocksPerSegment; k++){
						blocksSegment[z][k] = maxBlocksPerSegment;
						remainingBlocks -= maxBlocksPerSegment;
					}
					if( blocksPerChannel/maxBlocksPerSegment != 0 ){
						blocksSegment[z][requiredSegments-1] = remainingBlocks ;
					}
				}
				
				
				
			}
		}
		return blocksSegment;
	}

	/**
	 * Determines the number of blocks contained in a given channel.
	 *
	 * @param  squaredBlockSize integer that indicates the horizontal (vertical) size of a block.
	 * @param  xSize integer containing the width of the channel
	 * @param  ySize integer containing the height of the channel
	 *
	 * @return an integer containing the number of blocks in a segment with such dimensions
	 */
	public static int getBlocksPerChannel(int squaredBlockSize,	int xSize, int ySize){
			
			int xBlocks;
			int yBlocks;
			
			if( xSize%squaredBlockSize == 0 ){//no extra block is required, the image width
																				//fits to construct entire blocks
				xBlocks = xSize/squaredBlockSize;
			} else { //image width does not fit to construct entire blocks
				xBlocks = xSize/squaredBlockSize + 1 ;
			}
			
			if( ySize%squaredBlockSize == 0 ){//no extra block is required, the image height
																			//fits to construct entire blocks
				yBlocks = ySize/squaredBlockSize;
			} else { //image height does not fit to construct entire blocks
				yBlocks = ySize/squaredBlockSize + 1 ;
			}
			
			return (xBlocks * yBlocks) ;
	}
	

	
	/**
	 * Set the parameters required for this class
	 * 
	 * @param blocksPerSegment definition in {@link SegmentCode2D#blocksPerSegment}
	 * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param xOriginalSize definition in {@link #xOriginalSize}
	 * @param yOriginalSize definition in {@link #yOriginalSize}
	 */
	public void setParameters(int[][] blocksPerSegment,int[] WTLevels, int xOriginalSize, int yOriginalSize){
											
		//Parameters copy
 		this.blocksPerSegment = blocksPerSegment;
		this.WTLevels = WTLevels;
		this.xOriginalSize=xOriginalSize; 
		this.yOriginalSize=yOriginalSize; 
		this.zSize = blocksPerSegment.length;
		parametersSet = true;
	}

	/**
	 * Performs the bit plane encoding and writes the encoded image to the disk
	 *  
	 * @return an array of integers which determines which blocks belong to each segment; blockInSegment[channel][segment][block]
	 * 
	 * @throws Exception when something goes wrong and the construction of the segment must be stopped
	 */
	public int[][][] run() throws ParameterException{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("IDCCoder cannot run if parameters are not set.");
		}
		
		int blockInSegment[][][] = new int[zSize][][];
		for (int z=0;z<zSize;z++){
			int segmentsPerChannel = this.blocksPerSegment[z].length;
			blockInSegment[z] = new int[segmentsPerChannel][];
			for(int segment=0;segment<segmentsPerChannel;segment++){
				blockInSegment[z][segment] = new int[blocksPerSegment[z][segment]];
			}
			int squaredBlockSize = ((int) 1 << WTLevels[z]); //2^(WTLevels[0])
			int remainingBlocksChannel = getBlocksPerChannel(squaredBlockSize,xOriginalSize,yOriginalSize);
			int nextBlock=0;
			for(int segment=0;segment<segmentsPerChannel;segment++){
				for(int block=0;block<blocksPerSegment[z][segment];block++){
					blockInSegment[z][segment][block] = nextBlock;
					remainingBlocksChannel--;
					nextBlock++;
				}	
			}
		}


		return blockInSegment;
	}	
}
