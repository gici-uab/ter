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
package TER.TERdecoder.SegmentDecoder;

import GiciException.*;
import TER.TERdecoder.ReadFile.ReadBufferedStream;


/**
 * This class decodes a segment. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 * &nbsp; get functions<br>
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class SegmentDecode2D{
	
	/**
	 *  Bit stream which contains the information required for the decoding process.
	 */
	ReadBufferedStream encodedSegment;
	
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 */
	int WTLevels;
	
	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 */
	int WTType;
	
	/**
	 * Defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 */
	int customWtFlag; 
	
	/**
	 * Defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 */
	float[] customWeight; 
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthAC}
	 */	
	int bitDepthAC;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthACBlock}  
	 */
	int bitDepthACBlock[] = null;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthDC}
	 */	
	int bitDepthDC;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 */	
	int blocksPerSegment;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#gaggleDCSize}
	 */
	int gaggleDCSize;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#gaggleACSize}
	 */
	int gaggleACSize;	
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#idDC}
	 */
	int idDC;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#idAC}
	 */
	int idAC;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 */
	int DCStop;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitPlaneStop}
	 */
	int bitPlaneStop;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#stageStop}
	 */
	int stageStop;	
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#resolutionLevels}
	 */
	int resolutionLevels;
	
	/**
	 * Defintion in {@link TER.TERcoder.SegmentCoder.EncodeGaggleAC#entropyAC} 
	 */
	int entropyAC;
	
	/**
	 * Indicates if the bit stream containied in the decoded file has arrived to the end and decodong process must be stoped
	 */
	boolean endOfFile ;
	
	/**
	 * This array contains AC components of the recovered segment. 
	 * Usage : ACs[block][resolutionLevel][family][x][y]
	 */
	int[][][][][] ACs = null;
	
	/**
	 * This array contains DC components of the recovered segment. 
	 * Usage : DCs[block]
	 */
	int[] DCs = null;
	
	/**
	 * Defintion in {@link TER.TERdecoder.Decoder#CVerbose}
	 */
	boolean[] CVerbose=null;
	
	/**
	 * Defintion in {@link TER.TERcoder.WriteFile.RecommendedOrder#codeWordLength} 
	 */
	int codeWordLength;
	
	/**
	 * This float indicates the point of the interval where values are recovered in the decoding process. 
	 * When decoding a value inside an interval is possible to approximate this value to the 
	 * middle value of the interval, to the low value, to 0,25 of the interval, to ...
	 * Example: in bit plane 3, a recovered value can be approximated to 8 (low value) or to 12 (middle value)
	 * Valid values are real numbers in the interval [0,1). For example:<br>
	 *   <ul>
	 *     <li> 0.0 - Lower value (gamma = 0)
	 *     <li> 0.5 - Middle value (gamma = 1/2)
	 *     <li> 0.375 - 3/8 of the threshold value (gamma = 3/8) 
	 *   </ul>
	 * 
	 */
	float gammaValue;
	
	/**
	 * Definition in {@link TER.TERdecoder.SegmentDecoder.InitialDecoding#completionMode}
	 */
	int completionMode;
	
	/**
	 * Constructor that receives the coded segment (it may be void)
	 * 
	 * @param DCs defintion in {@link #DCs}
	 * @param ACs defintion in {@link #ACs}
	 * @param WTLevels defintion in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param DCStop defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#DCStop}
	 * @param blocksPerSegment defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#blocksPerSegment}
	 */
	public SegmentDecode2D(int[] DCs, int[][][][][] ACs, int WTLevels, int DCStop, int blocksPerSegment){
		this.WTLevels = WTLevels;
		this.DCStop = DCStop;
		this.blocksPerSegment = blocksPerSegment;
		
		if (DCs==null){
			this.DCs = new int[blocksPerSegment];
		} else {
			this.DCs = DCs;
		}
		
		if (ACs == null && DCStop == 0){
			//the segment for AC components is created
			int families = 3;
			this.ACs = new int[blocksPerSegment][WTLevels][families][][];
			for(int block = 0; block<blocksPerSegment; block++){
				for(int rLevel = 0; rLevel<WTLevels; rLevel++){
					int sizeResolutionLevel = (int) (1 << rLevel);
					for(int subband=0; subband<families ; subband++){
						this.ACs[block][rLevel][subband] = new int[sizeResolutionLevel][sizeResolutionLevel];
					}
				}
			}			
		} else {
			this.ACs = ACs;
		}
	}
	
	/**
	 * set Parameters required to decode the segment
	 * 
	 * @param WTType defintion in {@link GiciTransform.ForwardWaveletTransform#WTTypes}
	 * @param customWtFlag defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWtFlag}
	 * @param customWeight defintion in {@link TER.TERcoder.Weighting.ForwardWeighting#customWeight}
	 * @param gaggleDCSize defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#gaggleDCSize}
	 * @param gaggleACSize defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#gaggleACSize}
	 * @param idDC defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#idDC}
	 * @param idAC defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#idAC}
	 * @param bitPlaneStop defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitPlaneStop}
	 * @param stageStop defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#stageStop}
	 * @param bitDepthDC defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthDC}
	 * @param bitDepthAC defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#bitDepthAC}
	 * @param resolutionLevels defintion in {@link TER.TERcoder.SegmentCoder.SegmentCode2D#resolutionLevels}
	 * @param entropyAC defintion in {@link TER.TERcoder.SegmentCoder.EncodeGaggleAC#entropyAC}
	 * @param CVerbose defintion in {@link TER.TERdecoder.Decoder#CVerbose}
	 * @param codeWordLength defintion in {@link TER.TERcoder.WriteFile.RecommendedOrder#codeWordLength} 
	 * @param gammaValue definition in {@link #gammaValue}
	 * @param completionMode definition in {@link TER.TERdecoder.SegmentDecoder.InitialDecoding#completionMode}
	 */
	public void setParameters(int WTType, int customWtFlag, float[] customWeight,
			int gaggleDCSize, int gaggleACSize, int idDC, int idAC, int bitPlaneStop, int stageStop,
			int bitDepthDC, int bitDepthAC, int resolutionLevels, 
			int entropyAC, boolean[] CVerbose, int codeWordLength,
			float gammaValue, int completionMode){
		
		this.WTType = WTType;
		this.customWtFlag = customWtFlag;
		this.customWeight = customWeight;
		this.gaggleDCSize = gaggleDCSize;
		this.gaggleACSize = gaggleACSize;
		this.idDC = idDC;
		this.idAC = idAC;
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC; 
		this.bitPlaneStop = bitPlaneStop;
		this.stageStop = stageStop;
		this.resolutionLevels = resolutionLevels;
		this.entropyAC = entropyAC;
		this.CVerbose = CVerbose;
		this.codeWordLength = codeWordLength;
		this.gammaValue = gammaValue;
		this.completionMode = completionMode;
		
		parametersSet = true;
	}
	
	/**
	 * Decodes a segment according with the parameters given and with the available stream given by the user 
	 * 
	 * @param encodedSegment {@link #encodedSegment}
	 * 
	 * @throws Exception when something is wrong and decoding must be stopped
	 */
	public void run(ReadBufferedStream encodedSegment) throws Exception{
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("SegmentDecode2D cannot run if parameters are not set.");
		}
		this.encodedSegment = encodedSegment;
			
		String lastCodingPass = null;
		boolean lowerBitPlane = true;
		int BP[] = getBP();
		endOfFile = false;
		
		InitialDecoding idcDC = new InitialDecoding();
		
		idcDC.setParameters(blocksPerSegment, bitDepthDC, bitDepthAC, gaggleDCSize, idDC, BP[0], true, completionMode);
		
		int DCs_2s_comp[] = null;
		DCs_2s_comp = idcDC.run(encodedSegment);
		if (idcDC.getAbnormalTermination()){
			lowerBitPlane = false;
			endOfFile = true;
			lastCodingPass="Initial DC decoding";
		}
		
		int quantizedDCBitPlanes = idcDC.getDinamicRange();
		idcDC = null;
		if (DCs_2s_comp!=null){
			putDCs(DCs_2s_comp,quantizedDCBitPlanes,BP[0]);
		}

		
		for(int bitPlane=quantizedDCBitPlanes-1; bitPlane>bitDepthAC-1 && bitPlane>=BP[0] && !endOfFile; bitPlane--){	
			try{
				refineDCs(DCs_2s_comp, bitPlane,BP[0]);
			}catch (Exception e){
				lowerBitPlane = false;
				endOfFile = true;
				lastCodingPass="DC Refinement, before AC decoding begins, at bitplane"+bitPlane;
			}
		}
		

		if( DCStop == 0 && !endOfFile){
			InitialDecoding idcAC = new InitialDecoding();
			idcAC.setParameters(blocksPerSegment, bitDepthDC, bitDepthAC, gaggleACSize, idAC, BP[0], false,completionMode);
			
			bitDepthACBlock = idcAC.run(encodedSegment);
			if (idcAC.getAbnormalTermination()){
				lowerBitPlane = false;
				endOfFile = true;
				lastCodingPass="Decoding bit depth of AC components";
			}
			idcAC = null;
		
		}
		
		if ( DCStop==0 && !endOfFile){
			int numberOfGaggles = blocksPerSegment / gaggleACSize;
			if ( blocksPerSegment%gaggleACSize != 0 ){
				numberOfGaggles++ ;
			}
			// from the highest bitplane to the lowest bitplane, each block is encoded (if required)
			//codedBPEImage[gaggle][block][bitplane][resolutionLevel][subband][pass]
			byte segmentStatus[][][][][][] = initSegmentStatus();
			byte significantPiramid[][][][] = initSignificance();
			byte Ds[][] = initDs();
			byte D[][][] = initD();
			
			int bitPlane = 0;
			try{
				DecodeGeneration dg = null;
				RefineACs rf = null; 
				EntropyDecoderAC ed = new EntropyDecoderAC(encodedSegment, numberOfGaggles);
				for(bitPlane = bitDepthAC-1 ; bitPlane >= bitPlaneStop ; bitPlane-- ){
					if (bitPlane<quantizedDCBitPlanes && bitPlane>=BP[0]){//stage 0
						try{
							refineDCs(DCs_2s_comp, bitPlane,BP[0]);
						}
						catch (Exception e){
							lastCodingPass="DC Refinement, after decoding bit depth of AC components, at bitplane"+bitPlane;
							throw new Exception();
						}
					}
					ed.setParameters(entropyAC);
					for (int rLevel=0; rLevel<resolutionLevels; rLevel++){ //stages 1 to resolutionLevels
						if ( bitPlane<=bitPlaneStop && rLevel>=stageStop ){
							throw new Exception();
						}
						for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
							for(int block=gaggle*gaggleACSize;  block<(gaggle+1)*gaggleACSize && block<blocksPerSegment ; block++){
								if( bitDepthACBlock[block] > bitPlane ){// in other case is the bitPlane has not been encoded									
									dg = new DecodeGeneration(ed);
									dg.setParameters(ACs[block], segmentStatus[gaggle][block%gaggleACSize], 
											bitDepthACBlock[block],	BP, resolutionLevels, gaggle, gammaValue );
									try{
										dg.run(bitPlane,rLevel,rLevel,
												significantPiramid[gaggle][block%gaggleACSize], 
												Ds[gaggle][block%gaggleACSize], D[gaggle][block%gaggleACSize]);
									} catch (Exception e){
										ACs[block] = dg.getBlock();
										lastCodingPass="Decoding AC components, at bitplane "+bitPlane;
										throw new Exception();
									}
									significantPiramid[gaggle][block%gaggleACSize] = dg.getSignificantPiramid();
									segmentStatus[gaggle][block%gaggleACSize] = dg.getBlockStatus();
									Ds[gaggle][block%gaggleACSize] = dg.getDs();
									D[gaggle][block%gaggleACSize] = dg.getD();
									ACs[block] = dg.getBlock();
								}
							}
						}
					}
					if ( bitPlane<=bitPlaneStop && resolutionLevels>=stageStop ){
						throw new Exception();
					}
					for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){//stage 4
						for(int block=gaggle*gaggleACSize;  block<(gaggle+1)*gaggleACSize && block<blocksPerSegment ; block++){
							for (int rLevel=0; rLevel<resolutionLevels; rLevel++){
								
								
								if( bitDepthACBlock[block] > bitPlane ){// in other case is the bitPlane has not been encoded in previous bitPlanes
									rf = new RefineACs(ed);
									rf.setParameters(ACs[block], segmentStatus[gaggle][block%gaggleACSize], 
											bitDepthACBlock[block], BP, gaggle, gammaValue);
									try{
										rf.run(bitPlane,rLevel,rLevel);
									} catch (Exception e){
										ACs[block] = rf.getBlock();
										lastCodingPass="Refinement AC components, at bitplane"+bitPlane;
										throw new Exception();
									}
									segmentStatus[gaggle][block%gaggleACSize] = rf.getBlockStatus();
									ACs[block] = rf.getBlock();
									
								}
							}
						}
						
					}
				}
				

				
			}catch (Exception e){
				lowerBitPlane = false;
			}
			
			//remaining bits in the segments are checked
			/*int numberExtraBits = (int) (encodedSegment.getActualPosition() % 8);
			if (numberExtraBits!=0 && encodedSegment.getNumBits()>encodedSegment.getActualPosition()){
				int extraBits = encodedSegment.getBits(8-numberExtraBits);
				if (extraBits!=0){
					System.out.println("ExtraBits should have been set to zero.");
				}			
			}
			if (this.codeWordLength!=0){
				int extraBytes = (int) (encodedSegment.getActualPosition()/8)%(this.codeWordLength+1);
				
				if (extraBytes!=0){
					extraBytes = this.codeWordLength+1-extraBytes;
					extraBytes = encodedSegment.getBits(8*extraBytes);
				}
				if (extraBytes!=0){
					System.out.println("ExtraBytes should have been set to zero.");
				}
			}*/
			// remaining bits in the segments are checked
			encodedSegment.clearByte();
			// remaining bytes in the segments are checked
			if (this.codeWordLength!=0){
				int extraBytes = (int) (encodedSegment.getCounter())%(this.codeWordLength+1);
				encodedSegment.skipBytes(extraBytes);
			}
			
		}
		if(!lowerBitPlane && CVerbose[2]){
			System.out.println("\n Decoding has not arrived to the lower bitplane\n");
			System.out.println(lastCodingPass);
		}
	}

	
	/**
	 * Refine DC components
	 * 
	 * @param DCs_2s_comp integer array containing DC components in two's-complement to be refined
	 * @param bitPlane indicates the bitplane that must be refined
	 * @param BPLL defintion in {@link TER.TERcoder.SegmentCoder.InitialCoding#BPLL}
	 * 
	 * @throws Exception if the end of the bitstream is reached
	 */
	public void refineDCs(int[] DCs_2s_comp, int bitPlane, int BPLL) throws Exception{
		int threshold = ( (int) 1<<bitPlane );//2^bitPlane
		for(int block=0; block<blocksPerSegment; block++){
			if (encodedSegment.getBit()){//the refined bit equals one
				DCs_2s_comp[block] += threshold;
			} //else { DCs_2s_comp[block] += 0; }
			putDCBlock( block, DCs_2s_comp[block],bitPlane,BPLL);
		}
	}
	
	
	/**
	 * Places DC components in their position inside the segment. It also gives an approximation to the middle value
	 * 
	 * @param DCs_2s_comp integer array containing DC components in two's-complement
	 * @param bitPlane indicates the bitplane that must be refined
	 * @param BPLL defintion in {@link TER.TERcoder.SegmentCoder.InitialCoding#BPLL}
	 */
	private void putDCs(int[] DCs_2s_comp, int bitPlane, int BPLL){
		
		for(int block=0; block<blocksPerSegment ; block++){
			putDCBlock(block, DCs_2s_comp[block],bitPlane,BPLL);
		}
	}
	
	/**
	 * Receives a DC component in two's-complement and represents them with signed representation. 
	 * 
	 * @param blockNumber indicates the position of the block containing this DC inside the segment
	 * @param value contains the DC value
	 * @param bitPlane indicates the bitplane that must be refined
	 * @param BPLL defintion in {@link TER.TERcoder.SegmentCoder.InitialCoding#BPLL}
	 */
	public void putDCBlock(int blockNumber, int value, int bitPlane, int BPLL){
		int threshold = ( (int) 1<<bitPlane );//2^bitPlane
		int N = ((int) 1 << (bitDepthDC)); //2^(BitDepthDC)	
		int gamma = 0;
		if (bitPlane>BPLL){
			gamma = Math.round(gammaValue*threshold);
		} 
		
		if ( value < N/2 ){
			DCs[blockNumber] = value + gamma; 
		} else {
			DCs[blockNumber] = value - N - gamma;
		}
	}
	

	/**
	 * Initialize the array to store the status of each componet in the segment for coding purposes
	 * 
	 * @return a byte array prepared to store the status of each component of the segment
	 */
	public byte[][][][][][] initSegmentStatus(){
		int numberOfGaggles = blocksPerSegment / gaggleACSize;
		if ( blocksPerSegment%gaggleACSize != 0 ){
			numberOfGaggles++ ;
		}
		byte[][][][][][] segmentStatus = new byte[numberOfGaggles][][][][][];
		for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
			for(int block=gaggle*gaggleACSize;  block<(gaggle+1)*gaggleACSize && block<blocksPerSegment ; block++){
				int blocksInGaggle = gaggleACSize;
				if (( blocksPerSegment%gaggleACSize != 0) && (gaggle == numberOfGaggles-1) ){
					blocksInGaggle = blocksPerSegment%gaggleACSize;
				}
				segmentStatus[gaggle] = new byte[blocksInGaggle][][][][];				
			}
		}
		
		return segmentStatus;
	}

	/**
	 * Initialize the array where the significance of the descendants of each gaggle and block is stored
	 * 
	 * @return a byte array to store the significance of the descendants
	 */
	public byte[][] initDs(){
		int numberOfGaggles = blocksPerSegment / gaggleACSize;
		if ( blocksPerSegment%gaggleACSize != 0 ){
			numberOfGaggles++ ;
		}
		byte[][] Ds = new byte[numberOfGaggles][];
		for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
			int blocksInGaggle = gaggleACSize;
			if (( blocksPerSegment%gaggleACSize != 0) && (gaggle == numberOfGaggles-1) ){
				blocksInGaggle = blocksPerSegment%gaggleACSize;
			}
			Ds[gaggle] = new byte[blocksInGaggle];
			for(int block=0;  block<blocksInGaggle ; block++){
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
		int numberOfGaggles = blocksPerSegment / gaggleACSize;
		if ( blocksPerSegment%gaggleACSize != 0 ){
			numberOfGaggles++ ;
		}
		byte[][][] D = new byte[numberOfGaggles][][];
		for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
			int blocksInGaggle = gaggleACSize;
			if (( blocksPerSegment%gaggleACSize != 0) && (gaggle == numberOfGaggles-1) ){
				blocksInGaggle = blocksPerSegment%gaggleACSize;
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
	 * Initialize the array where the significance of the descendants of each generation and family of each gaggle and block is stored
	 * 
	 * @return a byte array to store the significance of each of the generation and families of the segment
	 */
	public byte[][][][] initSignificance(){
		int numberOfGaggles = blocksPerSegment / gaggleACSize;
		if ( blocksPerSegment%gaggleACSize != 0 ){
			numberOfGaggles++ ;
		}
		byte[][][][] map = new byte[numberOfGaggles][][][];
		for(int gaggle=0 ; gaggle<numberOfGaggles ; gaggle++){
			int blocksInGaggle = gaggleACSize;
			for(int block=gaggle*gaggleACSize;  block<(gaggle+1)*gaggleACSize && block<blocksPerSegment ; block++){
				
				if (( blocksPerSegment%gaggleACSize != 0) && (gaggle == numberOfGaggles-1) ){
					blocksInGaggle = blocksPerSegment%gaggleACSize;
				}
				
			}
			map[gaggle] = new byte[blocksInGaggle][resolutionLevels][3];
			for(int block=0;block<blocksInGaggle;block++){
				for(int rLevel=0;rLevel<resolutionLevels;rLevel++){
					for(int subband=0;subband<3;subband++){
						map[gaggle][block][rLevel][subband]=0;
					}
				}
			}
		}
	
		return map;
	}
	

	/////////////////////////
	//// GET FUNCTIONS //////
	/////////////////////////

	public int[] getBP(){
		
		int BP[] = new int[3*WTLevels+1];
		for(int subband = 0 ; subband < 3*WTLevels+1 ; subband++){
			if(this.customWtFlag==0 && WTType==4) {
				BP[subband] = ( WTLevels - subband/3 );
			} else if( customWtFlag == 1 ){
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

	public int[] getDCs(){
		return this.DCs;
	}
	
	public int[][][][][] getACs(){
		return this.ACs;
	}
}
