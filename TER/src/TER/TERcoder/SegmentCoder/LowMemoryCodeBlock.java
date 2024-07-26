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
 * This class codes as many bitplanes of a block of AC components as the user
 * requires. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; run<br>
 * &nbsp; get functions<br>
 * 
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.04
 */

public class LowMemoryCodeBlock {

	int byteBuff = 4;
	
	/**
	 * This array contains the significance of a block in a bitplane. It is structured as follows:
	 *  codedBitPlaneBlock[bit plane][resolution level][family][coding pass]
	 */
	BitStream[][][][] codedBlock = null;

	/**
	 * This array contains the contexts of the significance of a block in a bitplane. It is structured as follows:
	 *  blockBitPlaneContexts[bit plane][resolution level][family][coding pass]
	 */
	ByteStream[][][][] blockContexts = null;

	/**
	 * Determines, for each block, the number of bits needed to represent the
	 * magnitude of AC components
	 */
	int bitDepthAC_Block;

	/**
	 * Definition in {@link SegmentCode2D#resolutionLevels}
	 */
	int resolutionLevels;

	/**
	 * Indicates if the obtained code stream is going to be entropy encoded.
	 */
	boolean entropyCode;

	/**
	 * This array contains the refinement bits of the block in a bit plane. It is structured as follows:
	 * 	refinementAC[bit plane][level of resolution]
	 */
	BitStream refinementAC[][] = null;


	/**
	 * Indicate whether the descendants (all the components except the parents) are or have been significant.
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 -  Descendants are not significant
	 *     <li> 1 -  Descendant become significant at this bitplane
	 *     <li> 2 -  Descendant have been found significant in previous bitplanes
	 *   </ul>
	 */
	byte Ds;

	/**
	 * Indicate whether the descendants (all the components except the parents) are or have been significant for each of the families.
	 * That means, D[i] indicate the significance of the descendant in the family 'i'.
	 * Valid values are:<br>
	 *   <ul>
	 *     <li> 0 -  Descendants are not significant
	 *     <li> 1 -  Descendant become significant at this bitplane
	 *     <li> 2 -  Descendant have been found significant in previous bitplanes
	 *   </ul>
	 */
	byte D[] = null;

	/**
	 * For each family and generation indicates the number of bitplanes that are necessary zero due the weighting stage
	 * <p>
	 * Negative values are not allowed.
	 */
	int[] BP = null;

	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;

	/**
	 * Stores for each of the entropy coder options the number of bits required to encode the gaggle. 
	 * <p>
	 * Negative values are not allowed. 
	 */
	int codeLength[][] = null;

	/**
	 * Definition in {@link TER.TERcoder.Coder#imageSamplesFloat}
	 */
	float[][][] imageSamples = null;

	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels} 
	 */
	int[] WTLevels = null;

	/**
	 * Indicates the channel where the block belongs
	 */
	int channel;

	/**
	 * Indicates the position of the gaggle inside the channel
	 */
	int blockNumber;

	/**
	 * Indicates the initial row of each of the subbands of the block inside the whole image.  
	 */
	int[][] xInit = null;

	/**
	 * Indicates the initial column of each of the subbands of the block inside the whole image.  
	 */
	int[][] yInit = null;

	/**
	 * Represents the significance of each subband of the block (significantPiramid[level of resolution][family])
	 */
	byte significantPiramid[][] = null;

	/**
	 * Constructor that receives the values of the block to be further encoded.
	 * 
	 * @param imageSamples {@link TER.TERcoder.Coder#imageSamplesFloat}
	 * @param resolutionLevels {@link SegmentCode2D#resolutionLevels}
	 */
	public LowMemoryCodeBlock(float[][][] imageSamples, int[] resolutionLevels) {

		this.imageSamples = imageSamples;
		int maxResolutionLevels = resolutionLevels[0];
		for (int k = 0; k < resolutionLevels.length; k++) {
			if (maxResolutionLevels < resolutionLevels[k]) {
				maxResolutionLevels = resolutionLevels[k];
			}
		}
		significantPiramid = new byte[maxResolutionLevels][3];

	}

	
	/**
	 * Set the parameters used to code a block
	 * 
	 * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param resolutionLevels definition in {@link SegmentCode2D#resolutionLevels}
	 * @param channel definition in {@link #channel}
	 * @param blockNumber definition in {@link #blockNumber}
	 * @param bitDepthAC_Block definition in {@link #bitDepthAC_Block}
	 * @param entropyAC definition in {@link EncodeGaggleAC#entropyAC}
	 * @param BP definition in {@link #BP}
	 */
	public void setParameters(int[] WTLevels, int resolutionLevels,	int channel, int blockNumber,
			int bitDepthAC_Block, int entropyAC, int[] BP) {

		this.BP = BP;
		this.bitDepthAC_Block = bitDepthAC_Block;
		this.resolutionLevels = resolutionLevels;

		this.WTLevels = WTLevels;
		this.blockNumber = blockNumber;
		this.channel = channel;

		int xSize = imageSamples[channel][0].length;
		int ySize = imageSamples[channel].length;

		int xSubBandSize = ((int) xSize >> WTLevels[channel]);
		int ySubBandSize = ((int) ySize >> WTLevels[channel]);

		int x0 = blockNumber % xSubBandSize;
		int y0 = blockNumber / xSubBandSize;

		this.xInit = new int[resolutionLevels][3];
		this.yInit = new int[resolutionLevels][3];
		for (int generation = 0; generation < resolutionLevels; generation++) {
			int sizeResolutionLevel = ((int) 1 << generation);
			xSubBandSize = ((int) xSize >> (WTLevels[channel] - generation));
			ySubBandSize = ((int) ySize >> (WTLevels[channel] - generation));
			// family 0. Corresponding to HL subband
			// (xInit,yInit) value where the block at the subband HL starts
			xInit[generation][0] = x0 * sizeResolutionLevel + xSubBandSize;
			yInit[generation][0] = y0 * sizeResolutionLevel;

			// family 1. Corresponding to LH subband
			// (xInit,yInit) value where the block at the subband LH starts
			xInit[generation][1] = x0 * sizeResolutionLevel;
			yInit[generation][1] = y0 * sizeResolutionLevel + ySubBandSize;

			// family 2. Corresponding to HH subband
			// (xInit,yInit) value where the block at the subband HH starts
			xInit[generation][2] = x0 * sizeResolutionLevel + xSubBandSize;
			yInit[generation][2] = y0 * sizeResolutionLevel + ySubBandSize;

		}

		if (entropyAC == 0) {// no entropy code is requires, thus no information about contexts is needed to be created
			this.entropyCode = false;
		} else {
			this.entropyCode = true;
		}

		parametersSet = true;
	}
	
	/**
	 * Runs the block coder algorithm
	 * 
	 * @param startBitPlane indicates the bitplane where the encoder must begin
	 * @param endBitPlane indicates the bitplane where the encoder must stop
	 * @param codeLength {@link #codeLength}
	 * 
	 * @throws Exception when something goes wrong and block coding must be stopped
	 */
	public void run(int startBitPlane, int endBitPlane, int[][] codeLength) throws Exception {
		// If parameters are not set run cannot be executed
		if (!parametersSet) {
			throw new ParameterException(
					"CodeBlockAC cannot run if parameters are not set.");
		}
		
		initDAndDs(startBitPlane);
		if (entropyCode) {
			this.codeLength = codeLength;
		}
		
		
		initCodedBlock(startBitPlane, endBitPlane);
		initBlockContexts(startBitPlane, endBitPlane);
		if (bitDepthAC_Block>0){
			refinementAC = new BitStream[bitDepthAC_Block-1][resolutionLevels];
		}
		this.codeLength = codeLength;
		
		for(int bitPlane=startBitPlane;bitPlane>=endBitPlane;bitPlane--){
			
			significanceCoding(bitPlane);
			
			if (bitPlane<this.bitDepthAC_Block-1){
				refinementCoding(bitPlane);
			}
			
		}
		
		this.significantPiramid = null;
		this.D = null;
		xInit = null;
		yInit = null;
	}

	private void initDAndDs(int bitPlane){
		int families = 3;
		D = new byte[families];
		
		if (bitPlane == this.bitDepthAC_Block-1){
			// this is the first bitPlane
			Ds = 0;
			for (int subband=0;subband<families;subband++){
				D[subband]=0;
			}
		} else {
			int threshold = ((int) 1 << (bitPlane));// 2^(bitPlane)
			Ds = 0;
			int componentMagnitude = 0;
			for (int subband = 0; subband < families; subband++) {
				D[subband]=0;
				for (int rLevel = 1; rLevel < resolutionLevels; rLevel++) {
					//parents are not involved in the values of Ds and D
					int sizeResolutionLevel = (int) 1 << rLevel;	
					for (int y = 0; y < sizeResolutionLevel; y++) {
						for (int x = 0; x < sizeResolutionLevel; x++) {
							componentMagnitude = Math.round(imageSamples[channel][yInit[rLevel][subband] + y][xInit[rLevel][subband] + x]);
							if (componentMagnitude<0){
								componentMagnitude = -componentMagnitude;
							}
							if (componentMagnitude>=2*threshold){//The component has been found significant in previous bitplanes
								D[subband] = 2;
								//maximum value has been found, it has no sense keep on searching
								x=sizeResolutionLevel;
								y=sizeResolutionLevel;
								rLevel=resolutionLevels;
								
							}
						}
					}
				}
				if (D[subband]>Ds){
					Ds=D[subband];
				}
			}
		}
	}
	
	private byte getStatus(int rLevel, int subband, int y, int x, int bitPlane, int threshold){
		byte status = 0;
		if (bitPlaneOfZeros(rLevel, subband, bitPlane)){
			status = -1;
		} else {
			int componentMagnitude = Math.round(imageSamples[channel][yInit[rLevel][subband] + y][xInit[rLevel][subband] + x]);
			if (componentMagnitude<0){
				componentMagnitude = -componentMagnitude;
			}
			if (componentMagnitude>=threshold){
				if (componentMagnitude < 2*threshold){
					status = 1;
				} else {
					status = 2;
				}
			} else {
				status = 0;
			}
		}
		return status;
	}
	
	/**
	 * This function indicates if the bits of the coefficients are necessarily zero due to the subband scaling operation
	 * 
	 * @param rLevel indicates the resolution level of the subband to be encoded
	 * @param subband determines the subband once is known the resolution level 
	 * @param bitPlane indicates the bitplane that is being encoded
	 * 
	 * @return a boolean that indicates if the coefficients of this subband should be taken into account for significance encoding purposes
	 */
	private boolean bitPlaneOfZeros(int rLevel, int subband, int bitPlane) {
		boolean zeros;
		int subbandNumber = (3 * rLevel) + subband + 1;
		if (BP[subbandNumber] > bitPlane) {
			zeros = true;
		} else {
			zeros = false;
		}
		return zeros;
	}

	/**
	 * The significance of the block is encoded for the given bitPlane. 
	 * 
	 * @param bitPlane indicates the bitplane that must be coded
	 */
	public void significanceCoding(int bitPlane) {
		int families = 3;
		int threshold = ((int) 1 << bitPlane);// 2^bitPlane

		// fisrt, we look for the subbands that must be encoded and signal the components
		// that become significant in this bitPlane
		//int component;
		byte status;
		
		for (int rLevel = 0; rLevel < resolutionLevels; rLevel++) {
			int sizeResolutionLevel = (int) 1 << rLevel;
			for (int subband = 0; subband < families; subband++) {
				significantPiramid[rLevel][subband] = -1;
				for (int y = 0; y < sizeResolutionLevel; y++) {
					for (int x = 0; x < sizeResolutionLevel; x++) {
						status = getStatus(rLevel,subband,y,x,bitPlane, threshold);
						if (status > significantPiramid[rLevel][subband]){
							significantPiramid[rLevel][subband] = status; 
						}
					}
				}
			}
		}

		for (int rLevel = resolutionLevels - 2; rLevel >= 1; rLevel--) {
			for (int subband = 0; subband < families; subband++) {
				if (significantPiramid[rLevel][subband] < significantPiramid[rLevel + 1][subband]
						&& !bitPlaneOfZeros(this.resolutionLevels - 1, subband,	bitPlane)) {
					significantPiramid[rLevel][subband] = significantPiramid[rLevel + 1][subband];
				}
			}
		}
		for (int subband = 0; subband < families; subband++) {
			if (significantPiramid[1][subband] > Ds) {
				Ds = significantPiramid[1][subband];
			}
		}

		// parents are encoded
		int significance = 0;
		int signs = 0;
		int signsLength = 0;
		int significanceLength = 0;
		
		for (int subband = 0; subband < families; subband++) {
			status = getStatus(0,subband,0,0,bitPlane, threshold);
			if (status == 1	&& !bitPlaneOfZeros(0, subband, bitPlane)) {
				significanceLength++;
				significance = (significance << 1) + 1;
				// now the sign is encoded
				signsLength++;
				signs = signs << 1;
				if (imageSamples[channel][yInit[0][subband]][xInit[0][subband]] < 0) {
					signs++;
				}
			} else if (status == 0 && !bitPlaneOfZeros(0, subband, bitPlane)) {
				significanceLength++;
				significance = significance << 1;
			}
		}
		// the signs are encoded together after the significant parents
		if (significanceLength > 0) {
			codedBlock[bitPlane][0][0][0].addBits(significance,
					significanceLength);
			if (entropyCode) {
				blockContexts[bitPlane][0][0][0].addByte(getContext(0,significanceLength, false));
				updateLengthEntropyEncoder(significance, getContext(0,significanceLength, false),bitPlane);
			}
		}
		// the signs are encoded together just after the significant parents
		if (signsLength > 0) {
			codedBlock[bitPlane][0][0][0].addBits(signs, signsLength);

			if (entropyCode) {
				blockContexts[bitPlane][0][0][0].addByte(getContext(1,signsLength, false));
			}
		}

		for (int rLevel = 1; rLevel < resolutionLevels; rLevel++) {
			// descendants are encoded
			if (rLevel == 1) {// Ds is set and encoded if needed
				if (Ds == 0) {
					// There are no significant descendants
					// Ds = 0;
					codedBlock[bitPlane][1][0][0].addBit(0);
					if (entropyCode) {
						blockContexts[bitPlane][1][0][0].addByte(getContext(0, 1,false));
					}
				} else if (Ds == 1) {
					// Some of the descendants become significant in this
					// bitplane
					// then Ds is set to one
					codedBlock[bitPlane][1][0][0].addBit(1);
					if (entropyCode) {
						blockContexts[bitPlane][1][0][0].addByte(getContext(0, 1,false));
					}
					Ds = 2;
				} // else { The descendants have become significant in
					// previous bitplanes and Ds equals 2;}
			}
			
			if (Ds > 0) {
				int Di = 0;
				int DiLength = 0;
				for (int subband = 0; subband < families; subband++) {
					if (rLevel == 1) {
						if ((significantPiramid[rLevel][subband] == 2 || D[subband] == 2)&& !bitPlaneOfZeros(this.resolutionLevels - 1,	subband, bitPlane)) {
							// this subband has become significant in previous bitplanes the subband must be encoded
							// since it has significant components in this or previous bitplanes, or has significant
							// descendants.
							if (!bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								codeSquare(bitPlane, rLevel, subband);
							}
							
						} else if (significantPiramid[rLevel][subband] == 1) {
							// the piramid has significant components or significant descendants it is the first time this subband is encoded
							codedBlock[bitPlane][rLevel + 1][subband][0].addBit(1);
							if (entropyCode) {
								blockContexts[bitPlane][rLevel + 1][subband][0].addByte(getContext(0, 1, false));
								Di = (Di << 1) + 1;
								DiLength++;
							}
							
							if (!bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								codeSquare(bitPlane, rLevel, subband);
							}
							D[subband] = 2;
							
						} else if ((significantPiramid[rLevel][subband] == 0 || D[subband] == 0) && !bitPlaneOfZeros(this.resolutionLevels - 1,	subband, bitPlane)) {
							codedBlock[bitPlane][rLevel + 1][subband][0].addBit(0);
							if (entropyCode) {
								blockContexts[bitPlane][rLevel + 1][subband][0].addByte(getContext(0, 1, false));
								Di = (Di << 1);
								DiLength++;
							}
						}
					} else if ((D[subband] == 2) && !bitPlaneOfZeros(this.resolutionLevels - 1,	subband, bitPlane)) {
						// The prevoius resolution level has been encoded
						if (significantPiramid[rLevel][subband] == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
							// this subband has become significant in previous bitplanes the subband must be encoded
							// since it has significant components in this or previous bitplanes, or has significant descendants.
							codeSquare(bitPlane, rLevel, subband);
						} else if ((significantPiramid[rLevel][subband] == 0 || significantPiramid[rLevel][subband] == 1) && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
							codedBlock[bitPlane][rLevel + 1][subband][0].addBit(significantPiramid[rLevel][subband]);
							if (entropyCode) {
								blockContexts[bitPlane][rLevel + 1][subband][0].addByte(getContext(0, 1, false));
								Di = (Di << 1)
								+ significantPiramid[rLevel][subband];
								DiLength++;
							}
							if (significantPiramid[rLevel][subband] > 0) {
								codeSquare(bitPlane, rLevel, subband);
							}
						}
					}
				}
				if (entropyCode) {
					if (DiLength == 1) {
						updateLengthEntropyEncoder(Di, (byte) 64, bitPlane);
					} else if (DiLength == 2) {
						updateLengthEntropyEncoder(Di, (byte) 65, bitPlane);// in this case the context is 65
					} else if (DiLength == 3) {
						if (rLevel == 1) {
							updateLengthEntropyEncoder(Di, (byte) 66, bitPlane);// in this case the context is 66 ( 000 impossible value)
						} else {
							updateLengthEntropyEncoder(Di, (byte) 67, bitPlane);// in this case the context is 67
						}
					}
				}
			}
		}
	}

	/**
	 * This function create the words needed to encode each of the squares (subbands) contained in a block.
	 * 	 
	 * @param bitPlane indicates the bitplane that is being encoded
	 * @param rLevel indicates the resolution level of the subband to be encoded
	 * @param subband determines the subband once is known the resolution level 
	 */
	private void codeSquare(int bitPlane, int rLevel, int subband) {

		int numberOfComponents = (int) (1 << (rLevel * 2)); // 2^(rLevel*2);
		int numberOfInitialWords = numberOfComponents / 4;
		int initialWords[][] = new int[numberOfInitialWords][4];
		int ySize = (int) 1 << rLevel;
		int xSize = (int) 1 << rLevel;

		// in the position [x][0] is stored the word x,
		// while in the position [x][1] is stored the lenght of the word x
		// and the position [x][2] is stored the sign of the significant pixels
		// the position [x][3] is stored the lenght of the sign bits
		for (int word = 0; word < numberOfInitialWords; word++) {
			initialWords[word][0] = 0;
			initialWords[word][1] = 0;
			initialWords[word][2] = 0;
			initialWords[word][3] = 0;
		}

		byte status = 0;
		int threshold = ((int) 1 << bitPlane);// 2^bitPlane 
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				int currentWord = 0;
				for (int k = 1; k < rLevel; k++) {
					int quadrantSide = (int) 1 << (rLevel - k);
					int subQuadrant = (x % (2 * quadrantSide)) / quadrantSide + 2 * ((y % (2 * quadrantSide)) / quadrantSide);
					currentWord += subQuadrant * (quadrantSide * quadrantSide)	/ 4;
				}
				status = getStatus(rLevel,subband,y,x,bitPlane, threshold);
				if (status == 0) {
					// the component is not significant
					initialWords[currentWord][0] = (int) initialWords[currentWord][0] << 1;
					initialWords[currentWord][1]++;
				} else if (status == 1) {
					// the component becomes significant
					initialWords[currentWord][0] = ((int) initialWords[currentWord][0] << 1) + 1;
					initialWords[currentWord][1]++;
					// the sign is encoded
					initialWords[currentWord][2] = (int) initialWords[currentWord][2] << 1;
					initialWords[currentWord][3]++;
					// if (getComponent(rLevel,subband,y,x) < 0) {
					if (imageSamples[channel][yInit[rLevel][subband] + y][xInit[rLevel][subband] + x] < 0) {
						// the component is negative
						initialWords[currentWord][2]++;
					}
				}// else {...} the coefficient has been encoded before or not
					// need to be encoded

			}
		}

		for (int pass = rLevel; pass > 0; pass--) {
			// the pass (0) where the significance of the subband and its
			// descendants is stated, has been done before
			initialWords = codeWords(initialWords, bitPlane, rLevel, subband,pass);
		}
		initialWords = null;
	}
	
	/**
	 * Given a set of words or transition words it encodes it for a given pass. It also computes the words required for the previous pass
	 * 
	 * @param initialWords contains the set of words to be encoded
	 * @param bitPlane indicates the bitplane that is being encoded
	 * @param rLevel indicates the resolution level of the subband to be encoded
	 * @param subband determines the subband once is known the resolution level
	 * @param pass indicates the coding pass that is being encoded
	 * 
	 * @return the set of words required for encoding next pass
	 */
	private int[][] codeWords(int[][] initialWords, int bitPlane, int rLevel, int subband, int pass) {
		int size = initialWords.length;
		int nextWords[][] = null;
		if (size > 1) {
			nextWords = new int[size / 4][4];
			for (int k = 0; k < size / 4; k++) {
				nextWords[k][0] = 0;
				nextWords[k][1] = 0;
				nextWords[k][2] = 0;
				nextWords[k][3] = 0;
			}
			for (int word = 0; word < size; word++) {
				if (initialWords[word][1] > 0) {
					// the word has positive lenght
					if (initialWords[word][1] == 4) {
						// the word has four 'letters', then if the set is significant,
						// it is the first time that word is encoded and the previous quadrant
						// must have a one to signal that this is the fisrt significant bitPlane
						// otherwise the set is insignificant, then the previous subdivision
						// of the quadrant must signal the insignificance
						nextWords[word / 4][0] = nextWords[word / 4][0] << 1;
						nextWords[word / 4][1]++;
						if (initialWords[word][0] != 0) {
							// there are significant components that must be signaled in the previous quadrant
							nextWords[word / 4][0]++;
							codedBlock[bitPlane][rLevel + 1][subband][pass] .addBits(initialWords[word][0], initialWords[word][1]);

							// context must be set
							if (entropyCode) {
								blockContexts[bitPlane][rLevel + 1][subband][pass] .addByte(getContext(0, initialWords[word][1], true));
								updateLengthEntropyEncoder( initialWords[word][0], getContext(0, initialWords[word][1], true), bitPlane);
							}
							if (initialWords[word][3] > 0) {// there are sign bits
								codedBlock[bitPlane][rLevel + 1][subband][pass] .addBits(initialWords[word][2], initialWords[word][3]);

								if (entropyCode) {
									blockContexts[bitPlane][rLevel + 1][subband][pass].addByte(getContext(1, initialWords[word][3], false));
								}
							}

						} // else { ... } nothing must be encoded since there are not significant components
					} else if (initialWords[word][1] > 0) {
						// the word has between one and three 'letters' the previous quadrant has been encoded in previous bitPlanes
						codedBlock[bitPlane][rLevel + 1][subband][pass].addBits(initialWords[word][0], initialWords[word][1]);

						// context must be set
						if (entropyCode) {
							blockContexts[bitPlane][rLevel + 1][subband][pass].addByte(getContext(0, initialWords[word][1], false));
							updateLengthEntropyEncoder(initialWords[word][0], getContext(0, initialWords[word][1], false), bitPlane);
						}
						if (initialWords[word][3] > 0) {// there are sign bits
							codedBlock[bitPlane][rLevel + 1][subband][pass] .addBits(initialWords[word][2], initialWords[word][3]);

							if (entropyCode) {
								blockContexts[bitPlane][rLevel + 1][subband][pass] .addByte(getContext(1, initialWords[word][3], false));
							}
						}

					}
				}

			}
		} else if (initialWords[0][1] > 0) {
			// the word has positive lenght, here is possible to encode four zeros in the fisrt resolution Level

			codedBlock[bitPlane][rLevel + 1][subband][pass].addBits(initialWords[0][0], initialWords[0][1]);
			// the context must be set
			if (entropyCode) {
				if (rLevel == 1) {
					blockContexts[bitPlane][rLevel + 1][subband][pass].addByte(getContext(0, initialWords[0][1], false));
					updateLengthEntropyEncoder(initialWords[0][0], getContext(0, initialWords[0][1], false), bitPlane);
				} else {
					if (initialWords[0][1] == 4) {
						blockContexts[bitPlane][rLevel + 1][subband][pass].addByte(getContext(0, initialWords[0][1], true));
						updateLengthEntropyEncoder(initialWords[0][0],getContext(0, initialWords[0][1], true), bitPlane);
					} else {
						blockContexts[bitPlane][rLevel + 1][subband][pass].addByte(getContext(0, initialWords[0][1],false));
						updateLengthEntropyEncoder(initialWords[0][0],getContext(0, initialWords[0][1], false), bitPlane);
					}
				}
			}
			if (initialWords[0][3] > 0) {// there are sign bits

				codedBlock[bitPlane][rLevel + 1][subband][pass].addBits(initialWords[0][2], initialWords[0][3]);
				if (entropyCode) {
					blockContexts[bitPlane][rLevel + 1][subband][pass].addByte(getContext(1, initialWords[0][3], false));
				}
			}
		}
		initialWords = null;
		return nextWords;
	}

	/**
	 * Perform the refinement of the components of the block for the given bitPlane
	 * 
	 * @param bitPlane indicates the bitplane that is being encoded
	 */
	public void refinementCoding(int bitPlane) {
		int families = 3;
		int threshold = ((int) 1 << bitPlane);// 2^bitPlane
		byte status;
		
		int refinedValue;
		for (int rLevel = 0; rLevel < resolutionLevels; rLevel++) {
			refinementAC[bitPlane][rLevel] = new BitStream(byteBuff*8);
			int sizeResolutionLevel = (int) 1 << rLevel;

			for (int subband = 0; subband < families; subband++) {
				if (rLevel != 2) {
					for (int y = 0; y < sizeResolutionLevel; y++) {
						for (int x = 0; x < sizeResolutionLevel; x++) {
							status = getStatus(rLevel,subband,y,x,bitPlane, threshold);
							if (status == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								// the coefficient has been found significant in previous bitplanes, and should be refined.

								refinedValue = Math.round(imageSamples[channel][yInit[rLevel][subband]+ y][xInit[rLevel][subband] + x]);
								if (refinedValue < 0) {
									if ((-refinedValue & threshold) != 0) {
										if (rLevel != 0) {
											this.refinementAC[bitPlane][rLevel].addBit(1);
										} else {
											this.refinementAC[bitPlane][rLevel].addBit(1);
										}
									} else {
										if (rLevel != 0) {
											this.refinementAC[bitPlane][rLevel].addBit(0);
										} else {// parents are refined together
											this.refinementAC[bitPlane][rLevel].addBit(0);
										}
									}
								} else {
									if ((refinedValue & threshold) != 0) {
										if (rLevel != 0) {
											this.refinementAC[bitPlane][rLevel].addBit(1);
										} else {
											this.refinementAC[bitPlane][rLevel].addBit(1);
										}
									} else {
										if (rLevel != 0) {
											this.refinementAC[bitPlane][rLevel].addBit(0);
										} else {// parents are refined together
											this.refinementAC[bitPlane][rLevel].addBit(0);
										}
									}
								}
							} 
						}
					}
				} else {
					for (int y = 0; y < 2; y++) {
						for (int x = 0; x < 2; x++) {
							status = getStatus(rLevel,subband,y,x,bitPlane, threshold);
							if (status == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								// the coefficient has been found significant in previous bitplanes, and should be refined.
								refinedValue = Math.round(imageSamples[channel][yInit[rLevel][subband] + y][xInit[rLevel][subband] + x]);
								if (refinedValue < 0) {
									if (((-refinedValue) & threshold) != 0) {
										this.refinementAC[bitPlane][rLevel].addBit(1);
									} else {
										this.refinementAC[bitPlane][rLevel].addBit(0);
									}
								} else {
									if ((refinedValue & threshold) != 0) {
										this.refinementAC[bitPlane][rLevel].addBit(1);
									} else {
										this.refinementAC[bitPlane][rLevel].addBit(0);
									}
								}
							} 
						}
					}
					for (int y = 0; y < 2; y++) {
						for (int x = 2; x < 4; x++) {
							status = getStatus(rLevel,subband,y,x,bitPlane, threshold);
							if (status == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								// the coefficient has been found significant in previous bitplanes, and should be refined.
								refinedValue = Math.round(imageSamples[channel][yInit[rLevel][subband] + y][xInit[rLevel][subband] + x]);
								if (refinedValue < 0) {
									if (((-refinedValue) & threshold) != 0) {
										this.refinementAC[bitPlane][rLevel].addBit(1);
									} else {
										this.refinementAC[bitPlane][rLevel].addBit(0);
									}
								} else {
									if ((refinedValue & threshold) != 0) {
										this.refinementAC[bitPlane][rLevel].addBit(1);
									} else {
										this.refinementAC[bitPlane][rLevel].addBit(0);
									}
								}
							} 
						}
					}
					for (int y = 2; y < 4; y++) {
						for (int x = 0; x < 2; x++) {
							status = getStatus(rLevel,subband,y,x,bitPlane, threshold);
							if (status == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								// the coefficient has been found significant in previous bitplanes, and should be refined.
								refinedValue = Math.round(imageSamples[channel][yInit[rLevel][subband] + y][xInit[rLevel][subband] + x]);
								if (refinedValue < 0) {
									if (((-refinedValue) & threshold) != 0) {
										this.refinementAC[bitPlane][rLevel].addBit(1);
									} else {
										this.refinementAC[bitPlane][rLevel].addBit(0);
									}
								} else {
									if ((refinedValue & threshold) != 0) {
										this.refinementAC[bitPlane][rLevel].addBit(1);
									} else {
										this.refinementAC[bitPlane][rLevel].addBit(0);
									}
								}
							} 
						}
					}
					for (int y = 2; y < 4; y++) {
						for (int x = 2; x < 4; x++) {
							status = getStatus(rLevel,subband,y,x,bitPlane, threshold);
							if (status == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								// the coefficient has been found significant in previous bitplanes, and should be refined.
								refinedValue = Math.round(imageSamples[channel][yInit[rLevel][subband] + y][xInit[rLevel][subband] + x]);
								if (refinedValue < 0) {
									if (((-refinedValue) & threshold) != 0) {
										this.refinementAC[bitPlane][rLevel].addBit(1);
									} else {
										this.refinementAC[bitPlane][rLevel].addBit(0);
									}
								} else {
									if ((refinedValue & threshold) != 0) {
										this.refinementAC[bitPlane][rLevel].addBit(1);
									} else {
										this.refinementAC[bitPlane][rLevel].addBit(0);
									}
								}
							} 
						}
					}

				}
			}
		}
	}

	/**
	 * This function computes the lenght that would use any of the entropy encoding options.
	 * 
	 * @param word contains a word that has been encoded
	 * @param context indicates the kind of word to be encoded, i.e. the lenght and the encoding pass
	 */
	private void updateLengthEntropyEncoder(int word, byte context, int bitPlane) {
		int symbol = 0;
		int length = 0;
		int PDF2MapperGeneral[] = { 0, 2, 1, 3 };
		int PDF3MapperGeneral[] = { 1, 4, 0, 5, 2, 6, 3, 7 };
		int PDF3MapperImpossibleValue[] = {/*-1*/7, 3, 0, 4, 1, 5, 2, 6 }; // 000 is impossible
		int PDF4MapperGeneral[] = { 10, 1, 3, 6, 2, 5, 9, 12, 0, 8, 7, 13, 4, 14, 11, 15 };
		int PDF4MapperImpossibleValue[] = {/*-1*/15, 1, 3, 6, 2, 5, 9, 11, 0, 8, 7, 12, 4, 13, 10, 14 }; // 0000 is impossible

		switch (context) {
		case (byte) 64: // 1-bit significance word
			symbol = word;
			length = 1;
			break;
		case (byte) 65: // 2-bit significance word
			symbol = PDF2MapperGeneral[word];
			length = 2;
			break;
		case (byte) 66: // 3-bit significance word, 000 impossible value
			symbol = PDF3MapperImpossibleValue[word];
			length = 3;
			break;
		case (byte) 67: // 3-bit significance word, general case (any value is possible)
			symbol = PDF3MapperGeneral[word];
			length = 3;
			break;
		case (byte) 68: // 4-bit significance word, 0000 impossible value
			symbol = PDF4MapperImpossibleValue[word];
			length = 4;
			break;
		case (byte) 69: // 4-bit significance word, general case (any value is possible)
			symbol = PDF4MapperGeneral[word];
			length = 4;
			break;
		default:
			symbol = word;
		}

		int lengthOfTwoBitsVariableLenghtCode_Option0[] = { 1, 2, 3, 3 };
		int lengthOfThreeBitsVariableLenghtCode_Option0[] = { 1, 2, 3, 5, 5, 5, 6, 6 };
		int lengthOfThreeBitsVariableLenghtCode_Option1[] = { 2, 2, 3, 3, 4, 4, 4, 4 };
		int lengthOfFourBitsVariableLenghtCode_Option0[] = { 1, 2, 3, 4, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8 };
		int lengthOfFourBitsVariableLenghtCode_Option1[] = { 2, 2, 3, 3, 4, 4, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7 };
		int lengthOfFourBitsVariableLenghtCode_Option2[] = { 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5 };

		if (length == 2) {
			this.codeLength[bitPlane][0] += lengthOfTwoBitsVariableLenghtCode_Option0[symbol];
			this.codeLength[bitPlane][1] += 2;// uncoded option
		} else if (length == 3) {
			this.codeLength[bitPlane][2] += lengthOfThreeBitsVariableLenghtCode_Option0[symbol];
			this.codeLength[bitPlane][3] += lengthOfThreeBitsVariableLenghtCode_Option1[symbol];
			this.codeLength[bitPlane][4] += 3;// uncoded option
		} else if (length == 4) {
			this.codeLength[bitPlane][5] += lengthOfFourBitsVariableLenghtCode_Option0[symbol];
			this.codeLength[bitPlane][6] += lengthOfFourBitsVariableLenghtCode_Option1[symbol];
			this.codeLength[bitPlane][7] += lengthOfFourBitsVariableLenghtCode_Option2[symbol];
			this.codeLength[bitPlane][8] += 4;
		}
	}



	/**
	 * Initialize the bit streams used to store the significance block
	 */
	private void initCodedBlock(int startBitPlane, int endBitPlane) {
		codedBlock = new BitStream[bitDepthAC_Block][resolutionLevels + 1][][];
		for(int bitPlane=startBitPlane;bitPlane>=endBitPlane; bitPlane--){
			for (int rLevel = 0; rLevel <= resolutionLevels; rLevel++) {
				int codingACPasses = rLevel + 1;
				int families = 3;
				if (rLevel == 0) {
					codingACPasses = 2;
					families = 1;// the three parents are encoded together
				} else if (rLevel == 1) {
					codingACPasses = 1;
					families = 1;
				} // else {... the values initially set are taken}
				codedBlock[bitPlane][rLevel] = new BitStream[families][codingACPasses];
				for (int subband = 0; subband < families; subband++) {
					codedBlock[bitPlane][rLevel][subband] = new BitStream[codingACPasses];
					for (int pass = 0; pass < codingACPasses; pass++) {
						codedBlock[bitPlane][rLevel][subband][pass] = new BitStream(byteBuff*8);
					}
				}
			}
		}
	}

	/**
	 * Initialize the byte streams used to store the contexts of the encoded words to be further entropy encoded.
	 */
	private void initBlockContexts(int startBitPlane, int endBitPlane) {
		blockContexts = new ByteStream[bitDepthAC_Block][resolutionLevels + 1][][];
		for(int bitPlane=startBitPlane;bitPlane>=endBitPlane; bitPlane--){
			for (int rLevel = 0; rLevel <= resolutionLevels; rLevel++) {
				int codingACPasses = rLevel + 1;
				int families = 3;
				if (rLevel == 0) {
					families = 1;
					codingACPasses = 2;
				} else if (rLevel == 1) {
					codingACPasses = 1;
					families = 1;
				} // else {... the values initially set are taken
				blockContexts[bitPlane][rLevel] = new ByteStream[families][codingACPasses];
				for (int subband = 0; subband < families; subband++) {
					blockContexts[bitPlane][rLevel][subband] = new ByteStream[codingACPasses];
					for (int pass = 0; pass < codingACPasses; pass++) {
						blockContexts[bitPlane][rLevel][subband][pass] = new ByteStream(byteBuff);
					}
				}
			}
		}
	}

	/**
	 * This function indicates which context correspond to each coded word given its stage, lentgh, ... 
	 * 
	 * @param stage indicates if the word corresponds to the significance encoding, the sign encoding or the refinement.
	 *            <ul>
	 *            <li> 0- significance
	 *            <li> 1- sign
	 *            <li> 2- refinement
	 *            </ul>
	 * @param length indicates the lenght of the word to be encoded. Valid values are between 1 and 4.
	 * @param impossiblePattern indicates if all zeros value if possible.
	 * 
	 * @return a byte that contains the context for entropy encoding the word.
	 */
	public byte getContext(int stage, int length, boolean impossiblePattern) {
		byte context = 0;
		if (stage == 0) {// this context corresponds to the significance encoding
			if (length == 1) {
				context = (byte) 64; // 1-bit significance word
			} else if (length == 2) {
				context = (byte) 65; // 2-bit significance word
			} else if (length == 3) {
				if (impossiblePattern) {
					context = (byte) 66; // 3-bit significance word (000 impossible value)
					// Not used due the structure used to store the coded data
				} else {
					context = (byte) 67; // 3-bit significance word (any value is possible)
				}
			} else if (length == 4) {
				if (impossiblePattern) {
					context = (byte) 68; // 4-bit significance word (0000 impossible value)
				} else {
					context = (byte) 69; // 4-bit significance word (any value is possible)
				}
			}
		} else if (stage == 1) { // this context corresponds to the sign encoding
			if (length == 1) {
				context = (byte) 70; // 1-bit sign word
			} else if (length == 2) {
				context = (byte) 71; // 2-bit sign word
			} else if (length == 3) {
				context = (byte) 72; // 3-bit sign word
			} else if (length == 4) {
				context = (byte) 73; // 4-bit sign word
			}
		} else { // this context corresponds to the refinement
			context = (byte) 74;
		}

		return context;
	}

	public BitStream[][][] getCodeBlock(int bitPlane){
		return codedBlock[bitPlane];
	}
	
	public ByteStream[][][] getBlockContexts(int bitPlane) {
		return blockContexts[bitPlane];
	}

	public BitStream[] getRefinementAC(int bitPlane) {
		return this.refinementAC[bitPlane];
	}

	public int[][] getCodeLength() {
		return codeLength;
	}

}
