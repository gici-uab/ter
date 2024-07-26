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
import GiciStream.*;

/**
 * This class encode the segment according to the given parameters. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 * &nbsp; get functions<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class SegmentCode2D{
	
	/**
	 * This array stores for each gaggle the initial coding of DC quantized components
	 */
	BitStream initialCodedDC[] = null;
	
	/**
	 * This array stores for each gaggle and bitplane the refinement of DC components
	 */
	BitStream refinementDC[][] = null;

	/**
	 * This array stores for each gaggle, bitplane, block and resolution level the refinement of AC components
	 *	refinementAC[gaggle][bitPlane][block][resolutionLevel]
	 */
	BitStream recommendedRefinementAC[][][][] = null;

	/**
	 * This array stores for each gaggle, bitplane, block and resolution level the refinement of AC components
	 *	refinementAC[gaggle][bitPlane][resolutionLevel]
	 */
	BitStream terRefinementAC[][][] = null;
	
	/**
	 * This array stores for each gaggle the encoded bit depth of AC components of each block
	 */
	BitStream codedBitDepthACBlock[] = null;

	/**
	 * This array stores for each gaggle, bitplane and resolution level the encoded significance of AC components of the segment
	 *  encodedSegmentAC[gaggle][bitPlane][resolutionLevel]
	 */
	BitStream sortingAC[][][] = null;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * Channel samples (index meaning [y][x]).
	 * <p>
	 * All values allowed.
	 */
	float[][] imageSamples = null;
	
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 */
	int WTLevels ;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 */
	int WTType;
	
	/**
	 * Definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 */
	int customWtFlag; 
	
	/**
	 * Definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 */
	float[] customWeight = null; 	
	
	/**
	 * This array determines which blocks belong to each segment, i.e. which is its position in the channel
	 */
	int[] blockInSegment = null ;
	
	/**
	 * This integer determines the number of bits needed to represent the magnitude of AC components in the segment.
	 * <p>
	 * Valid values are positive values
	 */	
	int bitDepthAC;
	
	/**
	 * This array determines, for each block, the number of bits needed to represent the magnitude of AC components  
	 */
	int bitDepthACBlock[] = null;
	
	/**
	 * This integer determines the number of bits needed to represent DC components in two-complement representation.
	 * <p>
	 * Valid values are positive values.
	 */ 
	int bitDepthDC;
	
	/**
	 * Indicates the number of blocks contained in each segment. 
	 * If the value exceeds the number of blocks remaining to be encoded 
	 * in a component, the number of remaining blocks will be asigned to blocksPerSegment.
	 * <p>
	 * Valid values are integers between 1 and Math.pow(2,20) encoded mod(Math.pow(2,20))
	 */
	int blocksPerSegment;
	
	/**
	 * Specifies the method employed to select value 
	 * of k parameters when coding DC quantized components 
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 -  Recommended heuristic selection
	 *     <li> 1 -  Optimum (exhaustive) selection
	 *     <li> 2 -  Cristina's heuristic selection
	 *     <li> 3 -  Fernando's heuristic selection
	 *   </ul>
	 */
	int[] optDCSelect = null;
	
	/**
	 * Specifies the method employed to select value of k parameters when coding BitDepthAC_Block 
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 -  Recommended heuristic selection
	 *     <li> 1 -  Optimum (exhaustive) selection
	 *     <li> 2 -  Cristina's heuristic selection
	 *     <li> 3 -  Fernando's heuristic selection
	 *   </ul>
	 */
	int[] optACSelect = null;
	
	/**
	 * Specifies the size of a gaggle of DC components for each segment
	 * <p>
	 * Valid values are positive values.
	 */
	int[] gaggleDCSize = null;
	
	/**
	 * Specifies the size of a gaggle of AC components for each segment
	 * <p>
	 * Valid values are positive values.
	 */
	int[] gaggleACSize = null;	
	
	/**
	 * Specifies the frequency of appareance of id DC in the segment. That means, 
	 * how often a id DC appears
	 * <p>
	 * Valid values are positive values. 0 value means that only one id DC is used 
	 * in the segment.
	 */
	int[] idDC = null;
	
	/**
	 * Specifies the frequency of appareance of id AC in the segment. That means, 
	 * how often a id AC appears
	 * <p>
	 * Valid values are positive values. 0 value means that only one id AC is used 
	 * in the segment.
	 */
	int[] idAC = null;
	
	/**
	 * Specificies the segment in the coded image;
	 */
	int segmentId;
	
	/**
	 * Indicates the maximum number of bytes that can be employed to encode a segment.  
	 * <p>
	 * Negatives values are not allowed.
	 */
	int[] segByteLimit = null;
	
	/**
	 * Indicates for each segment if codification must stop after DC initially encoding
	 * <p>
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 - No stop
	 *     <li> 1 - Stop after coding DC quantized components
	 *   </ul>
	 */
	int[] DCStop = null;
	
	/**
	 * When DCStop equals 0 indicates for each segment the bitplane where codification stops.  
	 * If the value given is greater than the number of bitplanes, then the no information from encoded bitplanes will be sent.
	 * <p>
	 * Negatives values are not allowed
	 */
	int[] bitPlaneStop = null;
	
	/**
	 * When DCStop equals 0 indicates for each segment the generation (stage) where codification stops.
	 * If more generations than the available are required, all the generations will be encoded.
	 *
	 * <p>
	 * Valid values for wl DWT levels are:<br>
	 *   <ul>
	 *     <li> 1  - all  
	 *     <li> 2  - Generation 0 (Stage 1)
	 *     <li> 3  - Generation 1 (Stage 2)
	 *     <li>  ...
	 *     <li> (wl+1)  - Generation wl (Stage wl+1)
	 *   </ul>
	 */
	int[] stageStop = null;
	
	/**
	 * Definition in {@link EncodeGaggleAC#entropyAC}
	 */
	int[] entropyAC = null;
	
	/**
	 * Indicates the number of resolution levels that must be encoded, this number can be lower that the number of WTLevels  
	 * <p>
	 * Neither negatives values nor values higher than WTLevels are allowed.
	 */
	int resolutionLevels;
	
	/**
	 * This object computes the distortion when block components are encoded.
	 */
	DistortionCompute distortion ;
	
	boolean computeDistortion = false;
	
	float desiredDistortion;
	
	int distortionMeasure;
	/**
	 * Definition in {@link TER.TERcoder.WriteFile.WriteFile#progressionOrder}
	 */
	int progressionOrder;
	
	/**
	 * Constructor that receives the transformed image 
	 *
	 * @param imageSamples definition in {@link #imageSamples}
	 *
	 */
	public SegmentCode2D(float imageSamples[][]){
		//Image data copy
		this.imageSamples = imageSamples;
		
	}
	
	
	/**
	 * Set Parameters required to encode the segments
	 *  
	 * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels} 
	 * @param WTType definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 * @param resolutionLevels definition in {@link #resolutionLevels} 
	 * @param customWtFlag definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 * @param customWeight definition in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight} 
	 * @param optDCSelect definition in {@link #optDCSelect}
	 * @param optACSelect definition in {@link #optACSelect}
	 * @param gaggleDCSize definition in {@link #gaggleDCSize}
	 * @param gaggleACSize definition in {@link #gaggleACSize} 
	 * @param idDC definition in {@link #idDC}
	 * @param idAC definition in {@link #idAC}
	 * @param DCStop definition in {@link #DCStop}
	 * @param bitPlaneStop definition in {@link #bitPlaneStop}
	 * @param stageStop definition in {@link #stageStop}
	 * @param segByteLimit definition in {@link #segByteLimit}
	 * @param desiredDistortion definition in {@link #desiredDistortion}
	 * @param distortionMeasure definition in {@link #distortion}
	 * @param entropyAC definition in {@link EncodeGaggleAC#entropyAC}
	 * @param progressionOrder definition in {@link TER.TERcoder.WriteFile.WriteFile#progressionOrder}
	 */
	public void setParameters(int WTLevels, int WTType, int resolutionLevels, int customWtFlag, float[] customWeight,
			int[] optDCSelect, int[] optACSelect, int[] gaggleDCSize, int[] gaggleACSize,
			int[] idDC, int[] idAC, int[] DCStop, int[] bitPlaneStop, int[] stageStop,
			int[] segByteLimit, float desiredDistortion, int distortionMeasure, int[] entropyAC,
			int progressionOrder){
		
		this.WTLevels = WTLevels;
		this.WTType = WTType;
		this.resolutionLevels = resolutionLevels;
		
		this.customWtFlag = customWtFlag;
		this.customWeight = customWeight;
		this.optDCSelect = optDCSelect;
		this.optACSelect = optACSelect;
		this.gaggleDCSize = gaggleDCSize;
		this.gaggleACSize = gaggleACSize;
		this.idDC = idDC;
		this.idAC = idAC;
		this.DCStop = DCStop;
		this.bitPlaneStop = bitPlaneStop;
		this.stageStop = stageStop;
		this.segByteLimit = segByteLimit;
		this.desiredDistortion = desiredDistortion;
		this.distortionMeasure = distortionMeasure;		
		this.entropyAC = entropyAC;
		this.progressionOrder = progressionOrder;
		parametersSet = true;
		
	}
	
	/**
	 * Performs the bit plane encoding and writes the coded image to an array of bits.
	 * 
	 * @param blockInSegment definition in  {@link #blockInSegment}
	 * @param segmentId definition in {@link #segmentId}
	 * 
	 * @throws Exception when something goes wrong and segment encoding must be stopped
	 */
	public void run(int blockInSegment[], int segmentId) throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("SegmentCode2D cannot run if parameters are not set.");
		}

		this.blockInSegment = blockInSegment;
		blocksPerSegment = blockInSegment.length;
		this.segmentId = segmentId;
		long remainingBits = segByteLimit[segmentId]*8;
		
		if (computeDistortion){
			distortion = new DistortionCompute(distortionMeasure,0);
		}
		setBitDepthACBlock();
		int DCs[] = getDCs();
		setBitDepthDC(DCs);	
		
		DCs = setDCs2Complement(DCs);
		
		int BP[] = getBP();
		
		InitialCoding icDC = new InitialCoding(DCs);
		icDC.setParameters(bitDepthDC, bitDepthAC, optDCSelect[segmentId], gaggleDCSize[segmentId],
				idDC[segmentId], BP[0], true, distortion);
		initialCodedDC = icDC.run();
		int quantizedDCBitPlanes = icDC.getDinamicRange();
		icDC = null;
		if (initialCodedDC !=null){
			for(int gaggle=0;gaggle<initialCodedDC.length;gaggle++){
				remainingBits -= initialCodedDC[gaggle].getNumBits();
			}
		}
		
		
		initRefinementDC(quantizedDCBitPlanes);
		
		for(int bitPlane=quantizedDCBitPlanes-1; bitPlane>bitDepthAC-1 && bitPlane>=bitPlaneStop[segmentId] && bitPlane>=BP[0] && remainingBits>0; bitPlane--){
			refineDCs(DCs, bitPlane);
			for(int gaggle=0;gaggle<refinementDC.length;gaggle++){
				if (refinementDC[gaggle][bitPlane]!=null){
					remainingBits -= refinementDC[gaggle][bitPlane].getNumBits();
				}
			}
		}
		
		if (computeDistortion){
			if ( distortion.getDistortion() < desiredDistortion ){
				DCStop[segmentId] = 1;
			}
		}
		
		if ( DCStop[segmentId]==0 && remainingBits>0){
			// bit depth for each block is encoded
			InitialCoding icAC = new InitialCoding(bitDepthACBlock);
			icAC.setParameters(bitDepthDC, bitDepthAC, optACSelect[segmentId], gaggleACSize[segmentId],
					idAC[segmentId], BP[0], false, distortion);
			codedBitDepthACBlock = icAC.run();		
			icAC = null;
			
			if (codedBitDepthACBlock !=null){
				for(int gaggle=0;gaggle<codedBitDepthACBlock.length;gaggle++){
					remainingBits -= codedBitDepthACBlock[gaggle].getNumBits();
				}
			}
			
			int numberOfGaggles = blocksPerSegment / gaggleACSize[segmentId];
			if ( blocksPerSegment%gaggleACSize[segmentId] != 0 ){
				numberOfGaggles++ ;
			}
			
			// from the highest bitplane to the lowest bitplane, each block is encoded (if required)
			
			
			byte segmentStatus[][][][][][] = initSegmentStatus(); 
			byte Ds[][] = initDs();
			byte D[][][] = initD();
			initRefinementAC();
			
			sortingAC = new BitStream[numberOfGaggles][bitDepthAC][];
			EncodeGaggleAC entropyGaggle= new EncodeGaggleAC();
			
			CodeBlockAC codeACs = new CodeBlockAC(imageSamples,resolutionLevels);
			for(int bitplane = bitDepthAC-1 ; bitplane >= bitPlaneStop[segmentId] && remainingBits>0; bitplane-- ){
				if (bitplane>=BP[0] && bitplane<quantizedDCBitPlanes){
					refineDCs(DCs, bitplane);
					for(int gaggle=0;gaggle<refinementDC.length;gaggle++){
						if (refinementDC[gaggle][bitplane]!=null){
							remainingBits -= refinementDC[gaggle][bitplane].getNumBits();
						}
					}
				}
				for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
					ByteStream sortingACGaggle[][][][] = new ByteStream[gaggleACSize[segmentId]][][][];
					int codeLengthGaggle[] = null;

					BitStream refinementGaggle[][] = null;
					if 	(progressionOrder>=1){				
						refinementGaggle = new BitStream[gaggleACSize[segmentId]][];
					}
					for(int block=gaggle*gaggleACSize[segmentId];  block<(gaggle+1)*gaggleACSize[segmentId] && block<blocksPerSegment ; block++){
						if( bitDepthACBlock[block] > bitplane ){// in other case is not needed to encode the bitPlane
							int blockInGaggle = block % gaggleACSize[segmentId];
							
							codeACs.setParameters(WTLevels, resolutionLevels, blockInSegment[block],
									segmentStatus[gaggle][blockInGaggle],bitDepthACBlock[block],
									entropyAC[segmentId],BP,distortion, 
									Ds[gaggle][blockInGaggle], D[gaggle][blockInGaggle], codeLengthGaggle);
							sortingACGaggle[blockInGaggle] = codeACs.run(bitplane);
							
							segmentStatus[gaggle][blockInGaggle] = codeACs.getBlockStatus();
							Ds[gaggle][blockInGaggle] = codeACs.getDs();
							D[gaggle][blockInGaggle] = codeACs.getD();
							if (bitplane<bitDepthAC-1){
								if (progressionOrder>=1){
									refinementGaggle[blockInGaggle] = codeACs.getRefinementAC();
								} else {
									recommendedRefinementAC[gaggle][bitplane][blockInGaggle] = codeACs.getRefinementAC();
								}
							}
							
							if(entropyAC[segmentId]==1){
								codeLengthGaggle = codeACs.getCodeLength();							
							} 
							
						}
					}
					if (this.entropyAC[segmentId]<2){
						//Here is entropy encoded the gaggle
						entropyGaggle.setParameters(sortingACGaggle,
								entropyAC[segmentId],codeLengthGaggle,resolutionLevels);
						sortingAC[gaggle][bitplane]=entropyGaggle.run();
						
						for (int rLevel =0; rLevel<sortingAC[gaggle][bitplane].length ; rLevel++){
							remainingBits -= sortingAC[gaggle][bitplane][rLevel].getNumBits();
						}
						//memory is freed
						
					} 
					// here is interleaved the refinement bits of the gaggle (if required)
					if (bitplane<bitDepthAC-1 && progressionOrder>=1){
							for(int rLevel=0;rLevel<resolutionLevels;rLevel++){
								terRefinementAC[gaggle][bitplane][rLevel] = new BitStream((rLevel+1)*8*gaggleACSize[segmentId]);
								for(int block=0;block<refinementGaggle.length;block++){
									if (refinementGaggle[block]!=null){
										terRefinementAC[gaggle][bitplane][rLevel].addBitStream(refinementGaggle[block][rLevel]);
									}
								}		
							}					
					}
					refinementGaggle = null;
					codeLengthGaggle = null;
					sortingACGaggle = null;
					
				}
				
			}
			segmentStatus = null;
			D = null;
			Ds = null;
			codeACs = null;
			entropyGaggle = null;
			
			
		} else {
			codedBitDepthACBlock = null ;
		}	
		BP = null;
		distortion = null;
		DCs = null;
		//System.gc();

	}

	/**
	 * Refine DC components for a given bitplane
	 * 
	 * @param DCs array containing DC components in two's-complement
	 * @param bitPlane indicates the bitplane that must be refined
	 */
	public void refineDCs(int[] DCs, int bitPlane){
		int threshold = ( (int) 1<<bitPlane );//2^bitPlane
		int gaggleSize = gaggleDCSize[segmentId];
		for(int block=0; block<blocksPerSegment; block++){
			if ( (DCs[block] & threshold) != 0 ){
				refinementDC[block/gaggleSize][bitPlane].addBit(1);
			} else {
				refinementDC[block/gaggleSize][bitPlane].addBit(0);	
			}	
			if (computeDistortion){
				int previousApproximation = ( (int) (DCs[block]>>(bitPlane+1) )<<(bitPlane+1));
				int currentApproximation = ( (int) (DCs[block]>>bitPlane )<< bitPlane  );
				
				distortion.improvedValue(previousApproximation, currentApproximation, DCs[block]);
			}
		}
	}
	
	/**
	 * Initialize the bit streams required to store the refinement of DC components
	 * 
	 * @param q indicates the bitplane where DC refinement must begin
	 */
	public void initRefinementDC(int q){
		int numberOfGaggles = blocksPerSegment / gaggleDCSize[segmentId];
		if ( blocksPerSegment%gaggleDCSize[segmentId] != 0 ){
			numberOfGaggles++ ;
		}
		refinementDC = new BitStream[numberOfGaggles][];
		for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
			int bitBuff = (this.gaggleDCSize[segmentId]+1)*8;
			refinementDC[gaggle] = new BitStream[q];
			for(int bitPlane=0;bitPlane<q;bitPlane++){
				refinementDC[gaggle][bitPlane] = new BitStream(bitBuff);
			}
		}
	}

	/**
	 * Initialize the bit streams required to store the refinement of AC components
	 */
	public void initRefinementAC(){
		if (bitDepthAC-1>0){
			int numberOfGaggles = blocksPerSegment / gaggleACSize[segmentId];
			if ( blocksPerSegment%gaggleACSize[segmentId] != 0 ){
				numberOfGaggles++ ;
			}
			if (progressionOrder>=1){
				terRefinementAC = new BitStream[numberOfGaggles][][];
				for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
					terRefinementAC[gaggle] = new BitStream[bitDepthAC-1][resolutionLevels];
				}
			} else {
				recommendedRefinementAC = new BitStream[numberOfGaggles][][][];
				for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
					int blocksInGaggle = gaggleACSize[segmentId];
					if (( blocksPerSegment%gaggleACSize[segmentId] != 0) && (gaggle == numberOfGaggles-1) ){
						blocksInGaggle = blocksPerSegment%gaggleACSize[segmentId];
					}				
					recommendedRefinementAC[gaggle] = new BitStream[bitDepthAC-1][blocksInGaggle][];
				}
			}
		}
	}
	
	/**
	 * Initialize the array to store the status of each componet in the segment for coding purposes
	 * 
	 * @return a byte array prepared to store the status of each component of the segment
	 */
	public byte[][][][][][] initSegmentStatus(){
		int numberOfGaggles = blocksPerSegment / gaggleACSize[segmentId];
		if ( blocksPerSegment%gaggleACSize[segmentId] != 0 ){
			numberOfGaggles++ ;
		}
		byte[][][][][][] segmentStatus = new byte[numberOfGaggles][][][][][];
		for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
			int blocksInGaggle = gaggleACSize[segmentId];
			for(int block=gaggle*gaggleACSize[segmentId];  block<(gaggle+1)*gaggleACSize[segmentId] && block<blocksPerSegment ; block++){
				
				if (( blocksPerSegment%gaggleACSize[segmentId] != 0) && (gaggle == numberOfGaggles-1) ){
					blocksInGaggle = blocksPerSegment%gaggleACSize[segmentId];
				}
								
			}
			segmentStatus[gaggle] = new byte[blocksInGaggle][][][][];
		}
		
		return segmentStatus;
	}

	/**
	 * Initialize the array where the significance of the descendants of each gaggle and block is stored
	 * 
	 * @return a byte array to store the significance of the descendants
	 */
	public byte[][] initDs(){
		int numberOfGaggles = blocksPerSegment / gaggleACSize[segmentId];
		if ( blocksPerSegment%gaggleACSize[segmentId] != 0 ){
			numberOfGaggles++ ;
		}
		byte[][] Ds = new byte[numberOfGaggles][];
		for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
			int blocksInGaggle = gaggleACSize[segmentId];
			if (( blocksPerSegment%gaggleACSize[segmentId] != 0) && (gaggle == numberOfGaggles-1) ){
				blocksInGaggle = blocksPerSegment%gaggleACSize[segmentId];
			}
			Ds[gaggle] = new byte[blocksInGaggle];				
			
			for(int block=0;block<blocksInGaggle;block++){
				Ds[gaggle][block] = 0;
			}
		}
		
		return Ds;
	}
	
	/**
	 * Initialize the array where the significance of the descendants of each family of each gaggle and block is stored
	 * 
	 * @return a byte array to store the significance of the descendants of each family
	 */
	public byte[][][] initD(){
		int numberOfGaggles = blocksPerSegment / gaggleACSize[segmentId];
		if ( blocksPerSegment%gaggleACSize[segmentId] != 0 ){
			numberOfGaggles++ ;
		}
		byte[][][] D = new byte[numberOfGaggles][][];
		for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
			int blocksInGaggle = gaggleACSize[segmentId];
			if (( blocksPerSegment%gaggleACSize[segmentId] != 0) && (gaggle == numberOfGaggles-1) ){
				blocksInGaggle = blocksPerSegment%gaggleACSize[segmentId];
			}
			D[gaggle] = new byte[blocksInGaggle][3];				
			for(int block=0;block<blocksInGaggle;block++){
				for(int subband=0; subband<3;subband++){
					D[gaggle][block][subband] = 0;
				}
			}
		}
		
		return D;
	}
	
	/**
	 * Select DC components of a given block inside the transformed image and returns them.
	 *
	 * @return an integer array represents the DC in the block selected.
	 *
	 */
	public int[] getDCs(){
		int DCs[] = new int[blocksPerSegment];
		for(int block=0; block<blocksPerSegment ; block++){
			DCs[block] = getDCBlock( blockInSegment[block]);
		}
		return DCs;
	}
	
	/**
	 * Select a DC component of a given block inside the transformed image and returns it.
	 *
	 * @param blockNumber integer that indicates the number of the block in the transformed image
	 *
	 * @return an integer which represents the DC in the block selected.
	 */
	public int getDCBlock(int blockNumber){
		int xSize = imageSamples[0].length;
		int xResidualSubBandSize = ((int) xSize >> WTLevels) ;		
		int x = blockNumber%xResidualSubBandSize;
		int y = blockNumber/xResidualSubBandSize;
		
		int DC= (int) (imageSamples[y][x]) ;
		
		return DC;
	}
	
	/**
	 * Set the number of bits required to encode DC components in 2's complement representation
	 * 
	 * @param DCs array containing DC components of the segment
	 */
	public void setBitDepthDC(int DCs[]){
		int max = 0;
		for( int k = 0 ; k < blocksPerSegment; k++ ){
			int requiredBits = getBitsRequiredTwosComplement(DCs[k]);
			if( requiredBits > max ){
				max = requiredBits;
			}
		}
		bitDepthDC = max ; 
	}
	
	/**
	 * Set DC components in 2's complement representation
	 * 
	 * @param DCs array containing DC components of the segment
	 * 
	 * @return integer array containing DC components represented in two's-complement.
	 */
	public int[] setDCs2Complement(int DCs[]){
		int N = ((int) 1 << (bitDepthDC)); //2^(BitDepthDC)
		
		for( int k = 0 ; k < blocksPerSegment; k++ ){
			if(DCs[k] < 0){
				DCs[k] = N + DCs[k];
			} 
			
		}
		return DCs;	
	}

	
	/**
	 * Get the number of bits required to encode a number in 2's complement representation 
	 *
	 * @param n an integer to be represneted in 2's complement
	 * 
	 * @return an integer that represents the number of bits required to encode the input 
	 *     		in 2's complement 	 
	 */	
	public int getBitsRequiredTwosComplement(int n){
		if ( n < 0 ){
			return ( (int) (1+ Math.ceil( Math.log( Math.abs(n) ) / Math.log(2) ) ) );
		} else { //n>=0
			return ( (int) (1+ Math.ceil( Math.log(1+n) / Math.log(2) ) ) );
		}
	}
	
	/**
	 * Set the bit depth to represent the magnitude of AC components in all blocks of the segment.
	 * 
	 * @throws Exception when the dimensions of the image may cause problems
	 */
	public void setBitDepthACBlock() throws Exception{
		bitDepthACBlock = new int[blockInSegment.length];
		//first the maximum magnitude of AC components is found
		
		float component = 0;
		for(int block=0; block<blockInSegment.length ; block++){
			component = 0;
			
			int blockNumber = this.blockInSegment[block];
			if ( WTLevels != 0 ){
				int xSize = imageSamples[0].length;
				int ySize = imageSamples.length;
				int maxResolutionLevels = resolutionLevels;
				if ( maxResolutionLevels > WTLevels ){//check if there are enough levels of WT
					maxResolutionLevels = WTLevels;
				}
				int squaredBlockSize = ((int) 1 << maxResolutionLevels); //2^(maxResolutionLevels)
				
				
				if(	xSize%squaredBlockSize != 0 || ySize%squaredBlockSize != 0 ){
					//size of the transformed image may produce problemms.
					//Since they are not solved an exception is thrown.
					throw new Exception("Bit Plane Encoder cannot run with this image dimensions");
				} 
				
				int xSubBandSize = ((int) xSize >> WTLevels ) ;
				int ySubBandSize = ((int) ySize >> WTLevels ) ;
				
				int x0 = blockNumber%xSubBandSize;
				int y0 = blockNumber/xSubBandSize;
				
				//Generation 0 to WTLevels-1
				for( int rLevel=0 ; rLevel<maxResolutionLevels ; rLevel++){
					
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
							if (component < Math.abs(imageSamples[yInit+y][xInit+x])){
								component = Math.abs(imageSamples[yInit+y][xInit+x]);
							}
						}
					}
					
					//family 1. Corresponding to LH subband
					xInit = x0*sizeResolutionLevel ;
					yInit = y0*sizeResolutionLevel + ySubBandSize;
					//(xInit,yInit) value where the block at the subband LH starts
					for(int y=0; y<sizeResolutionLevel ; y++){
						for(int x=0; x<sizeResolutionLevel ; x++){
							if (component < Math.abs(imageSamples[yInit+y][xInit+x])){
								component = Math.abs(imageSamples[yInit+y][xInit+x]);
							}
						}
					}
					
					//family 2. Corresponding to HH subband
					xInit = x0*sizeResolutionLevel + xSubBandSize;
					yInit = y0*sizeResolutionLevel + ySubBandSize;
					//(x0,y0) value where the block at the subband HH starts
					for(int y=0; y<sizeResolutionLevel ; y++){
						for(int x=0; x<sizeResolutionLevel ; x++){
							if (component < Math.abs(imageSamples[yInit+y][xInit+x])){
								component = Math.abs(imageSamples[yInit+y][xInit+x]);
							}
						}
					}
				}
			}
			bitDepthACBlock[block] = (int)(component);
		}
		//once the maximum magnitude of each block is known, the bits required to encode such magnitudes is found.
		//Moreover, the bits needed to encode AC magnitudes in the segment is found
		bitDepthAC = 0;
		for(int block=0; block<blockInSegment.length ; block++){
			bitDepthACBlock[block] =  (int) (Math.ceil( Math.log(1+bitDepthACBlock[block]) / Math.log(2) ) );
			if ( bitDepthAC < bitDepthACBlock[block] ){
				bitDepthAC = bitDepthACBlock[block];
			}
		}
	}
	
	
	/**
	 * Creates an integer array that determines for each subband the number of bitplanes that are necessary zero due the weighting stage 
	 * 
	 * @return an integer array that contains for each family and generation the number of bitplanes that are necessary zero due the weighting stage
	 * 
	 * @throws ParameterException when the parameters are not compatible
	 */
	public int[] getBP() throws ParameterException{
		
		int waveletType = WTType;
		int weightFlag = customWtFlag;
		
		int BP[] = new int[3*WTLevels+1];
		for(int subband = 0 ; subband < 3*WTLevels+1 ; subband++){
			if(weightFlag==0 && waveletType==4) {
				BP[subband] = ( WTLevels - subband/3 );
			} else if( weightFlag == 1){
				//BP[subband] = 0;
				//this point must be improved. There should be a way to find whether the 
				//user-given weights produce zeros in the leasts significant bitplanes.
				BP[subband] = (int) Math.round(Math.log(customWeight[subband])/Math.log(2));
			} else {
				BP[subband] = 0;
			}
			
		}
		return BP;
	}
	
	/////////////////////////////
	//// GET FUNCTIONS //////////
	/////////////////////////////
	
	public BitStream[] getInitialCodedDC(){
		return initialCodedDC;
	}
	
	public BitStream[][] getRefinementDC(){
		return refinementDC;
	}
	
	public BitStream[] getCodedBitDepthACBlock(){
		return codedBitDepthACBlock;
	}	
	
	public int getBitDepthDC(){
		return bitDepthDC;
	}
	
	public int getBitDepthAC(){
		return bitDepthAC;
	}	
	
	public BitStream[][][][] getRecommendedRefinementAC() {
		return this.recommendedRefinementAC;
	}
	
	public BitStream[][][] getTerRefinementAC() {
		return this.terRefinementAC;
	}
	
	public int[][] getBitDepthACBlock(){
		int numberOfGaggles = blocksPerSegment / gaggleACSize[segmentId];
		if ( blocksPerSegment%gaggleACSize[segmentId] != 0 ){
			numberOfGaggles++ ;
		}
		int bitDepthACGaggleBlock[][] = new int[numberOfGaggles][];
		for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
			int blocksInGaggle = gaggleACSize[segmentId];
			for(int block=gaggle*gaggleACSize[segmentId];  block<(gaggle+1)*gaggleACSize[segmentId] && block<blocksPerSegment ; block++){
				if (( blocksPerSegment%gaggleACSize[segmentId] != 0) && (gaggle == numberOfGaggles-1) ){
					blocksInGaggle = blocksPerSegment%gaggleACSize[segmentId];
				}
			}
			bitDepthACGaggleBlock[gaggle] = new int[blocksInGaggle];
			for(int block=0;  block<blocksInGaggle ; block++){
				bitDepthACGaggleBlock[gaggle][block] = bitDepthACBlock[block + gaggle*gaggleACSize[segmentId]];
			}
		}
		
		return bitDepthACGaggleBlock;
	}
 
	public int[] getDCStop(){
		return DCStop;
	}	
	
	public BitStream[][][] getSortingAC(){
		return this.sortingAC;
	}
	
}

