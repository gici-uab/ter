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
package TER.TERdecoder.BPEDecoder;

import GiciException.*;

/**
 * This class locates the decoded blocks inside the recovered image. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 * &nbsp; get functions<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class AssembleSegments{
	
	/**
	 * Contains the image to which the segments are assembled
	 */
	float[][][] recoveredImage = null;
	
	/**
	 * Indicates the number of blocks that have been added to the recoveredImage
	 */
	int numberOfBlocks[] = null;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * Indicates the position of the block in the recovered image
	 * locationOfBlocks[block] contains the possition of the block
	 */
	int[] locationOfBlocks = null;
	
	/**
	 * Definition in {@link TER.TERdecoder.SegmentDecoder.SegmentDecode2D#DCs}
	 */
	int[] DCs = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthDC}
	 */
	int bitDepthDC;
	
	/**
	 * Definition in {@link TER.TERdecoder.SegmentDecoder.SegmentDecode2D#ACs} 
	 */
	int[][][][][] ACs = null;
	
	/**
	 * This indicates indicates the image width (xSize) of the channel.
	 * This size must differ from the image of the recovered image after inverse wavelet transform 
	 */
	int xSize;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 */
	int blocksPerSegment;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 */
	int WTLevels;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#resolutionLevels}
	 */
	int resolutionLevels;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 */
	int DCStop;
	
	int zSize;
	int channel;
	
	boolean minusHalf = true;
	
	/**
	 * Constructor of the AssembleSegments class. 
	 * Receives the image where blocks are added and the number of blocks that are contained in it.
	 * 
	 * @param initialImage contains the image where blocks are added
	 * @param numberOfInitialBlocks indicates the number of blocks in the initial image, where new blocks are added
	 */
	public AssembleSegments(float[][][] initialImage,int numberOfInitialBlocks[], int zSize){
		
		this.zSize = zSize;
		if (numberOfInitialBlocks!=null){
			this.numberOfBlocks = numberOfInitialBlocks;
		} else {
			this.numberOfBlocks = new int[zSize];
			for(int z=0;z<zSize;z++){
				this.numberOfBlocks[z]=0;	
			}
		}
		
		if(initialImage!=null){
			recoveredImage = initialImage;
		} else {
			recoveredImage = new float[zSize][][];
		}
	}
	
	/**
	 * Set the parameters required to assemble the blocks.
	 * 
	 * @param DCs definition in {@link TER.TERdecoder.SegmentDecoder.SegmentDecode2D#DCs}
	 * @param bitDepthDC definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthDC}
	 * @param ACs definition in {@link TER.TERdecoder.SegmentDecoder.SegmentDecode2D#ACs} 
	 * @param xSize definition in {@link #xSize}
	 * @param locationOfBlocks definition in {@link #locationOfBlocks}
	 * @param blocksPerSegment definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param resolutionLevels definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#resolutionLevels}
	 * @param DCStop definition in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 * 
	 * @throws ParameterException when parameters given are not properly
	 */
	public void setParameters(int[] DCs, int bitDepthDC, int[][][][][] ACs, 
			int xSize, int[] locationOfBlocks, int channel, int blocksPerSegment, 
			int WTLevels, int resolutionLevels, int DCStop, boolean minusHalf) throws ParameterException{
		
		parametersSet = true;
		
		
		
		this.DCs = DCs;
		this.blocksPerSegment = blocksPerSegment;
		this.xSize = xSize;
		this.WTLevels = WTLevels;
		this.resolutionLevels = resolutionLevels;
		this.bitDepthDC = bitDepthDC;
		this.DCStop = DCStop;
		
		this.minusHalf = minusHalf;
		
		this.channel = channel; // it is very important to set the variable channel before setting locationOfBlocks
		if (locationOfBlocks == null){//Recommended order is set
			initLocationOfBlocks();
		} else {
			this.locationOfBlocks = locationOfBlocks;
		}
		
		//Now lets verify that parameteres are feasible
		if (DCs.length!=blocksPerSegment){
			parametersSet = false;
		}
		
		if (ACs!=null){
			this.ACs = ACs;
			if (ACs.length!=blocksPerSegment){
				parametersSet = false;
			}
		} else {
			this.ACs = new int[blocksPerSegment][][][][];
		}
		if (this.recoveredImage!=null){
			if(this.recoveredImage.length!=zSize){
				parametersSet = false;
			} else{
				for(int z=0;z<zSize;z++){
					if(this.recoveredImage[z]!=null){
						if(this.recoveredImage[z][0]!=null){
							if(this.recoveredImage[z][0].length!=xSize){
								parametersSet = false;
							}
						}
					}
				}
			}
		}
		
		
		if(!parametersSet){
			throw new ParameterException("Segment cannot be added to the image if parameters are not properly set.");
		}
		
	}
	
	/**
	 * Put the segment into the recovered image, if it is required, the image size is modified to assemble the segment.
	 * 
	 * @throws ParameterException if parameters are not set correctly.
	 */
	public void run() throws ParameterException{
		if(!parametersSet){
			throw new ParameterException("Segment cannot be added to the image if parameters are not set.");
		}
		
		//Construction of an image big enough to place the given segment
		resizeImage();
		
		//Here we put each of the blocks in the image
		for(int block=0;block<blocksPerSegment; block++){
			putBlockInSegment(block,channel,locationOfBlocks[block]); 
		}
		// Now the number of blocks in the segment is updated
		numberOfBlocks[channel] += blocksPerSegment;
		
		
		//For using this method again it is necessary to set the parameters again. 
		parametersSet = false;
	}
	
	/**
	 * Compute the number of rows and columns required to add the segment, resize if needed and reallocate the blocks previously decoded
	 */
	private void resizeImage(){
		int yInitialSize;
		// This part must be improved for images with blocks recovered in order different from the one in the recommendation
		
		if (this.recoveredImage[channel]==null){
			yInitialSize = 0;
		} else {
			yInitialSize = recoveredImage[channel].length;
		}
		
		//computation of the amount blocks in the segment
		int pixelsPerBlock = (int) 1 << (WTLevels*2);//(2^(2*WTLevels[channel]))
		int blocksAvailable = (xSize*yInitialSize)/pixelsPerBlock;
		int emptyBlocks = blocksAvailable - this.numberOfBlocks[channel]; 
		if (emptyBlocks<blocksPerSegment){
			int blocksToAdd = blocksPerSegment - emptyBlocks;
			int sizeSideBlock = (int) 1 << (WTLevels);//((2*WTLevels[channel]))
			
			int rowsToAdd = (blocksToAdd*sizeSideBlock) / xSize;
			
			if ( (blocksToAdd*sizeSideBlock) %xSize !=0){
				rowsToAdd++;
			}
			rowsToAdd *= sizeSideBlock;
			
			int yExtendedSize = yInitialSize + rowsToAdd;
			
			float[][] extendedChannel = new float[yExtendedSize][xSize];
			
			
			//DCs are copied
			int xSubbandSize = ((int) xSize >> (WTLevels) ) ;
			int ySubbandSizeInitial = ((int) yInitialSize >> (WTLevels) ) ;
			for(int y=0;y<ySubbandSizeInitial;y++){
				for(int x=0;x<xSubbandSize;x++){
					extendedChannel[y][x] = this.recoveredImage[channel][y][x];
				}
			}
			if (DCStop==0){
				//ACs are copied
				for (int rLevel=0; rLevel<resolutionLevels; rLevel++){
					
					xSubbandSize = ((int) xSize >> (WTLevels -rLevel) ) ;
					ySubbandSizeInitial = ((int) yInitialSize >> (WTLevels -rLevel) ) ;
					int ySubbandSizeExtended = ((int) yExtendedSize >> (WTLevels -rLevel) ) ;
					int y0Initial, y0Extended, x0;
					//family 0
					x0=xSubbandSize;
					y0Initial=0;
					y0Extended=0;
					for(int y=0;y<ySubbandSizeInitial;y++){
						for(int x=x0;x<xSubbandSize+x0;x++){
							extendedChannel[y+y0Extended][x] = this.recoveredImage[channel][y+y0Initial][x];
						}
					}
					
					//family 1
					x0=0;
					y0Initial=ySubbandSizeInitial;
					y0Extended=ySubbandSizeExtended;
					for(int y=0;y<ySubbandSizeInitial;y++){
						for(int x=x0;x<xSubbandSize+x0;x++){
							extendedChannel[y+y0Extended][x] = this.recoveredImage[channel][y+y0Initial][x];
						}
					}
					
					//family 2
					x0=xSubbandSize;
					y0Initial=ySubbandSizeInitial;
					y0Extended=ySubbandSizeExtended;
					for(int y=0;y<ySubbandSizeInitial;y++){
						for(int x=x0;x<xSubbandSize+x0;x++){
							extendedChannel[y+y0Extended][x] = this.recoveredImage[channel][y+y0Initial][x];
						}
					}
				}
				
			}
			recoveredImage[channel] = null;
			recoveredImage[channel] = extendedChannel;
		} // else {} Nothing shoulb be done. Blocks to be added fit in the previously reserved array
		
		
	}
	
	/**
	 * Put the components in a block inside the transformed image.
	 * 
	 * @param block indicates the number of block inside the segment
	 * @param channel indicates the channel where the block belongs
	 * @param blockNumber indicates the number of the segment inside the image
	 * 
	 * @throws ParameterException when parameters are not compatible with adding the block
	 */

	public void putBlockInSegment(int block, int channel, int blockNumber) throws ParameterException{
		
		// first DC is placed
		int xSize = recoveredImage[channel][0].length;
		int xResidualSubBandSize = ((int) xSize >> WTLevels) ;
		int x0 = blockNumber%xResidualSubBandSize;
		int y0 = blockNumber/xResidualSubBandSize;
		recoveredImage[channel][y0][x0] = (float) DCs[block];
		if (this.minusHalf){
			if (DCs[block]>0){
				recoveredImage[channel][y0][x0] -= 0.5F;
			} else if (DCs[block]<0){
				recoveredImage[channel][y0][x0] += 0.5F;
			}
		}
		
		// Then AC components are placed
		if ( WTLevels != 0 && DCStop==0){
			
			
			int ySize = recoveredImage[channel].length;
			
			int squaredBlockSize = ((int) 1 << resolutionLevels); //2^(maxResolutionLevels)

			if(	xSize%squaredBlockSize != 0 || ySize%squaredBlockSize != 0 ){
				//size of the transformed image may produce problemms.
				//Since they are not solved an exception is thrown.
				throw new ParameterException("Bit Plane decoder cannot run with this image dimensions at the channel"+channel);
			} 
			
			int xSubBandSize = ((int) xSize >> WTLevels ) ;
			int ySubBandSize = ((int) ySize >> WTLevels ) ;
			
			x0 = blockNumber%xSubBandSize;
			y0 = blockNumber/xSubBandSize;
			
			//Generation 0 to WTLevels-1
			for( int rLevel=0 ; rLevel<resolutionLevels ; rLevel++){
				int sizeResolutionLevel = ((int) 1 << rLevel ) ;
				xSubBandSize = ((int) xSize >> (WTLevels - rLevel ) ) ;
				ySubBandSize = ((int) ySize >> (WTLevels - rLevel ) ) ;
				
				int xInit;
				int yInit;
				//family 0. Corresponding to HL subband
				xInit = x0*sizeResolutionLevel + xSubBandSize;
				yInit = y0*sizeResolutionLevel;
				//(xInit,yInit) value where the block at the subband HL starts
				for(int y=0; y<sizeResolutionLevel ; y++){
					for(int x=0; x<sizeResolutionLevel ; x++){
						recoveredImage[channel][yInit+y][xInit+x] = ACs[block][rLevel][0][y][x];
						if (this.minusHalf){
							if (ACs[block][rLevel][0][y][x]>0){
								recoveredImage[channel][yInit+y][xInit+x] -= 0.5F;
							} else if (ACs[block][rLevel][0][y][x]<0){
								recoveredImage[channel][yInit+y][xInit+x] += 0.5F;
							}
						}
					}
				}
				
				//family 1. Corresponding to LH subband
				xInit = x0*sizeResolutionLevel ;
				yInit = y0*sizeResolutionLevel + ySubBandSize;
				//(xInit,yInit) value where the block at the subband LH starts
				for(int y=0; y<sizeResolutionLevel ; y++){
					for(int x=0; x<sizeResolutionLevel ; x++){
						recoveredImage[channel][yInit+y][xInit+x] = ACs[block][rLevel][1][y][x];
						if (this.minusHalf){
							if (ACs[block][rLevel][1][y][x]>0){
								recoveredImage[channel][yInit+y][xInit+x] -= 0.5F;
							} else if (ACs[block][rLevel][1][y][x]<0){
								recoveredImage[channel][yInit+y][xInit+x] += 0.5F;
							}
						}
					}
				}
				
				//family 2. Corresponding to HH subband
				xInit = x0*sizeResolutionLevel + xSubBandSize;
				yInit = y0*sizeResolutionLevel + ySubBandSize;
				//(x0,y0) value where the block at the subband HH starts
				for(int y=0; y<sizeResolutionLevel ; y++){
					for(int x=0; x<sizeResolutionLevel ; x++){
						recoveredImage[channel][yInit+y][xInit+x] = ACs[block][rLevel][2][y][x];
						if (this.minusHalf){
							if (ACs[block][rLevel][2][y][x]>0){
								recoveredImage[channel][yInit+y][xInit+x] -= 0.5F;
							} else if (ACs[block][rLevel][2][y][x]<0){
								recoveredImage[channel][yInit+y][xInit+x] += 0.5F;
							}
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * In the case that the order of the blocks in the recovered image is not given, the default order is set.
	 */
	private void initLocationOfBlocks(){
		locationOfBlocks = new int[blocksPerSegment];
		for(int block=0; block<blocksPerSegment; block++){
			//the position [block] contains the position where the block is located inside the channel
			//Default position is given by the Recommendation
			locationOfBlocks[block] = numberOfBlocks[channel] + block;
		}
	}
	

	/////////////////////////
	//// GET FUNCTIONS //////
	/////////////////////////

	public float[][][] getRecoveredImage(){
		return recoveredImage;
	}
	
	public int[] getNumberOfBlocks(){
		return this.numberOfBlocks;
	}
}