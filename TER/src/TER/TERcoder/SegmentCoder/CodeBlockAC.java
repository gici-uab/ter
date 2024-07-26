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
 * @version 1.2
 */

public class CodeBlockAC {
	
	/**
	 * This array contains the significance (and its contexts if needed) of a block in a bitplane. It is structured as follows:
	 *  stream[resolution level][family][coding pass]
	 */
	ByteStream[][][] sortingAC = null;
	
	/**
	 * This array contains the status of each of the components in the block. They are structured as follows:
	 * 	blockStatus[resolution level][family][y][x]
	 */
	byte[][][][] blockStatus = null;

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
	 * Indicates how the obtained code stream is going to be entropy encoded.
	 */
	int entropyCode;

	/**
	 * This array contains the refinement bits of the block in a bit plane. It is structured as follows:
	 * 	refinementAC[level of resolution]
	 */
	BitStream refinementAC[] = null;

	/**
	 * Definition in {@link SegmentCode2D#distortion}
	 */
	DistortionCompute distortion;

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
	int codeLength[] = null;

	/**
	 * Definition in {@link TER.TERcoder.Coder#imageSamplesFloat}
	 */
	float[][] imageSamples = null;

	/**
	 * Definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels} 
	 */
	int WTLevels;

	/**
	 * Indicates the position of the block inside the channel
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
	 * Indicates whether the decrease of the distortion must be computed in order to stop coding process at a given point. 
	 */
	boolean computeDistortion = false;

	/**
	 * Constructor that receives the values of the block to be further encoded.
	 * 
	 * @param imageSamples {@link TER.TERcoder.Coder#imageSamplesFloat}
	 * @param resolutionLevels {@link SegmentCode2D#resolutionLevels}
	 */
	public CodeBlockAC(float[][] imageSamples, int resolutionLevels) {

		this.imageSamples = imageSamples;
		this.resolutionLevels = resolutionLevels;
		significantPiramid = new byte[resolutionLevels][3];

	}

	
	/**
	 * Set the parameters used to code a block
	 * 
	 * @param WTLevels definition in {@link GiciTransform.ForwardWaveletTransform#WTLevels}
	 * @param resolutionLevels definition in {@link SegmentCode2D#resolutionLevels}
	 * @param blockNumber definition in {@link #blockNumber}
	 * @param blockStatus definition in {@link #blockStatus}
	 * @param bitDepthAC_Block definition in {@link #bitDepthAC_Block}
	 * @param entropyAC definition in {@link EncodeGaggleAC#entropyAC}
	 * @param BP definition in {@link #BP}
	 * @param distortion {@link #distortion}
	 * @param Ds {@link #Ds}
	 * @param D {@link #D}
	 * @param codeLength {@link #codeLength}
	 */
	public void setParameters(int WTLevels, int resolutionLevels,
			int blockNumber, byte[][][][] blockStatus,
			int bitDepthAC_Block, int entropyAC, int[] BP,
			DistortionCompute distortion, byte Ds, byte[] D, int[] codeLength) {

		this.BP = BP;
		this.bitDepthAC_Block = bitDepthAC_Block;
		this.resolutionLevels = resolutionLevels;
		this.distortion = distortion;
		this.Ds = Ds;
		this.D = D;

		this.WTLevels = WTLevels;
		this.blockNumber = blockNumber;

		int xSize = imageSamples[0].length;
		int ySize = imageSamples.length;

		int xSubBandSize = ((int) xSize >> WTLevels);
		int ySubBandSize = ((int) ySize >> WTLevels);

		int x0 = blockNumber % xSubBandSize;
		int y0 = blockNumber / xSubBandSize;

		this.xInit = new int[resolutionLevels][3];
		this.yInit = new int[resolutionLevels][3];
		for (int rLevel = 0; rLevel < resolutionLevels; rLevel++) {
			int sizeResolutionLevel = ((int) 1 << rLevel);
			xSubBandSize = ((int) xSize >> (WTLevels - rLevel));
			ySubBandSize = ((int) ySize >> (WTLevels - rLevel));
			// family 0. Corresponding to HL subband
			// (xInit,yInit) value where the block at the subband HL starts
			xInit[rLevel][0] = x0 * sizeResolutionLevel + xSubBandSize;
			yInit[rLevel][0] = y0 * sizeResolutionLevel;

			// family 1. Corresponding to LH subband
			// (xInit,yInit) value where the block at the subband LH starts
			xInit[rLevel][1] = x0 * sizeResolutionLevel;
			yInit[rLevel][1] = y0 * sizeResolutionLevel + ySubBandSize;

			// family 2. Corresponding to HH subband
			// (xInit,yInit) value where the block at the subband HH starts
			xInit[rLevel][2] = x0 * sizeResolutionLevel + xSubBandSize;
			yInit[rLevel][2] = y0 * sizeResolutionLevel + ySubBandSize;

		}


		this.entropyCode = entropyAC;

		if (blockStatus != null) {
			this.blockStatus = blockStatus;
		} else {
			initBlockStatus();
		}

		
		
		if (this.entropyCode==1){
			if (codeLength != null) {
				this.codeLength = codeLength;
			} else {
				this.codeLength = new int[9];
				for (int k = 0; k < 9; k++) {
					this.codeLength[k] = 0;
				}
			}
		}

		parametersSet = true;
	}
	
	/**
	 * Runs the block coder algorithm
	 * 
	 * @param bitPlane indicates the bitplane that must be coded
	 * 
	 * @return an array the stream of the coded block for the given bitplane structured as follows:
	 * 	 [level of resolution (parents, children,...)][subband or family][coding pass] 
	 * 
	 * @throws Exception when something goes wrong and block coding must be stopped
	 */
	public ByteStream[][][] run(int bitPlane) throws Exception {
		// If parameters are not set run cannot be executed
		if (!parametersSet) {
			throw new ParameterException(
					"CodeBlockAC cannot run if parameters are not set.");
		}

		initStreams(bitPlane);
		
		updateLeastSignificantBP(bitPlane);
		significanceCoding(bitPlane);
		refinementCoding(bitPlane);

		xInit = null;
		yInit = null;
		
		//endStreams(bitPlane);
		return this.sortingAC;
	}

	int threshold;
	/**
	 * The significance of the block is encoded for the given bitPlane. 
	 * 
	 * @param bitPlane indicates the bitplane that must be coded
	 */
	public void significanceCoding(int bitPlane) {
		int families = 3;
		threshold = ((int) 1 << bitPlane);// 2^bitPlane

		// fisrt, we look for the subbands that must be encoded and mark the
		// components
		// that become significant in this bitPlane
		int component;
		for (int rLevel = 0; rLevel < resolutionLevels; rLevel++) {
			int sizeResolutionLevel = (int) 1 << rLevel;
			for (int subband = 0; subband < families; subband++) {
				significantPiramid[rLevel][subband] = -1;
				for (int y = 0; y < sizeResolutionLevel; y++) {
					for (int x = 0; x < sizeResolutionLevel; x++) {
						if (blockStatus[rLevel][subband][y][x] == 0) {
							component = (int) imageSamples[yInit[rLevel][subband]	+ y][xInit[rLevel][subband] + x];
							if (component >= threshold || component <= -threshold) {
								// the component was not significant in previous
								// bitplanes and become
								// significant in this bitplane
								blockStatus[rLevel][subband][y][x] = 1;
								if (computeDistortion) {
									// the distortion is now updated
									int sign = 1;
									if (component < 0) {
										sign = -1;
									}
									distortion.improvedValue(0, sign* threshold, component);
								}
							}
						}
						if (blockStatus[rLevel][subband][y][x] > significantPiramid[rLevel][subband]) {
							significantPiramid[rLevel][subband] = blockStatus[rLevel][subband][y][x];
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
			if (blockStatus[0][subband][0][0] == 1 && !bitPlaneOfZeros(0, subband, bitPlane)) {
				significanceLength++;
				significance = (significance << 1) + 1;
				// now the sign is encoded
				signsLength++;
				signs = signs << 1;
				if (imageSamples[yInit[0][subband]][xInit[0][subband]] < 0) {
					signs++;
				}
			} else if (blockStatus[0][subband][0][0] == 0 && !bitPlaneOfZeros(0, subband, bitPlane)) {
				significanceLength++;
				significance = significance << 1;
			}
		}

		
		// the signs are encoded together after the significant parents
		if (significanceLength > 0) {
			updateStreams(0,0,0,significance,significanceLength,-1,false);
			
			// the signs are encoded together just after the significant parents
			if (signsLength > 0) {
				updateStreams(0,0,0,signs,signsLength,1,false);

			}
		}
		
		//parents have been encoded, here only transition words are encoded
		for (int rLevel = 1; rLevel < resolutionLevels; rLevel++) {
			// descendants are encoded
			if (rLevel == 1) {// Ds is set and encoded if needed
				if (Ds == 0 || Ds==1) {
					// There are no significant descendants
					// Ds = 0;
					//stage =-2
					updateStreams(rLevel,0,0,Ds,1,-2,false);
					if (Ds == 1) {
						// Descendants become significant in this bitplane
						Ds = 2;
					}
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
							updateStreams(rLevel,subband,0,1,1,0,false);
							if (entropyCode==1) {
								Di = (Di << 1) + 1;
								DiLength++;
							}
							
							if (!bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								codeSquare(bitPlane, rLevel, subband);
							}
							D[subband] = 2;
							
						} else if ((significantPiramid[rLevel][subband] == 0 || D[subband] == 0) && !bitPlaneOfZeros(this.resolutionLevels - 1,	subband, bitPlane)) {
							updateStreams(rLevel,subband,0,0,1,0,false);
							if (entropyCode==1) {							
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
							updateStreams(rLevel,subband,0,significantPiramid[rLevel][subband],1,0,false);
							if (entropyCode==1) {	
								Di = (Di << 1) + significantPiramid[rLevel][subband];
								DiLength++;
							}
							if (significantPiramid[rLevel][subband] > 0) {
								codeSquare(bitPlane, rLevel, subband);
							}
						}
					}
				}
				if (entropyCode==1) {
					if (DiLength == 1) {
						updateLengthEntropyEncoder(Di, (byte) 64);
					} else if (DiLength == 2) {
						updateLengthEntropyEncoder(Di, (byte) 65);// in this case the context is 65
					} else if (DiLength == 3) {
						if (rLevel == 1) {
							updateLengthEntropyEncoder(Di, (byte) 66);// in this case the context is 66 ( 000 impossible value)
						} else {
							updateLengthEntropyEncoder(Di, (byte) 67);// in this case the context is 67
						}
					}
				}
			}
		}
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

		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				int currentWord = 0;
				for (int k = 1; k < rLevel; k++) {
					int quadrantSide = (int) 1 << (rLevel - k);
					int subQuadrant = (x % (2 * quadrantSide)) / quadrantSide + 2 * ((y % (2 * quadrantSide)) / quadrantSide);
					currentWord += subQuadrant * (quadrantSide * quadrantSide)	/ 4;
				}
				if (blockStatus[rLevel][subband][y][x] == 0) {
					// the component is not significant
					initialWords[currentWord][0] = (int) initialWords[currentWord][0] << 1;
					initialWords[currentWord][1]++;
				} else if (blockStatus[rLevel][subband][y][x] == 1) {
					// the component becomes significant
					initialWords[currentWord][0] = ((int) initialWords[currentWord][0] << 1) + 1;
					initialWords[currentWord][1]++;
					// the sign is encoded
					initialWords[currentWord][2] = (int) initialWords[currentWord][2] << 1;
					initialWords[currentWord][3]++;
					// if (getComponent(rLevel,subband,y,x) < 0) {
					if (imageSamples[yInit[rLevel][subband] + y][xInit[rLevel][subband] + x] < 0) {
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
		int signifanceStage = 0;
		if (pass == rLevel){// we are coding components
			signifanceStage=-1;
		}
		if (size > 1) {
			nextWords = new int[size / 4][4];
			for (int k = 0; k < size / 4; k++) {
				nextWords[k][0] = 0;
				nextWords[k][1] = 0;
				nextWords[k][2] = 0;
				nextWords[k][3] = 0;
			}
			for (int wordIndex = 0; wordIndex < size; wordIndex++) {
				if (initialWords[wordIndex][1] > 0) {
					// the word has positive lenght
					if (initialWords[wordIndex][1] == 4) {
						// the word has four 'letters', then if the set is significant,
						// it is the first time that word is encoded and the previous quadrant
						// must have a one to signal that this is the fisrt significant bitPlane
						// otherwise the set is insignificant, then the previous subdivision
						// of the quadrant must signal the insignificance
						nextWords[wordIndex / 4][0] = nextWords[wordIndex / 4][0] << 1;
						nextWords[wordIndex / 4][1]++;
						if (initialWords[wordIndex][0] != 0) {
							// there are significant components that must be signaled in the previous quadrant
							nextWords[wordIndex / 4][0]++;
							updateStreams(rLevel,subband,pass,initialWords[wordIndex][0],initialWords[wordIndex][1],signifanceStage,true);
							
							if (initialWords[wordIndex][3] > 0) {// there are sign bits
								updateStreams(rLevel,subband,pass,initialWords[wordIndex][2],initialWords[wordIndex][3],1,false);
							}

						} // else { ... } nothing must be encoded since there are not significant components
					} else if (initialWords[wordIndex][1] > 0) {
						// the word has between one and three 'letters' the previous quadrant has been encoded in previous bitPlanes
						updateStreams(rLevel,subband,pass,initialWords[wordIndex][0],initialWords[wordIndex][1],signifanceStage,false);
						if (initialWords[wordIndex][3] > 0) {// there are sign bits
							updateStreams(rLevel,subband,pass,initialWords[wordIndex][2],initialWords[wordIndex][3],1,false);	
						}

					}
				}

			}
		} else if (initialWords[0][1] > 0) {
			boolean impossiblePattern = false;
			if (rLevel == 1) {// the word has positive lenght, here is possible to encode four zeros in the fisrt resolution Level
				impossiblePattern = false;
			} else {
				if (initialWords[0][1] == 4) {
					impossiblePattern = true;
				}
			}
			updateStreams(rLevel,subband,pass,initialWords[0][0],initialWords[0][1],signifanceStage,impossiblePattern);
			if (initialWords[0][3] > 0) {// there are sign bits
				updateStreams(rLevel,subband,pass,initialWords[0][2],initialWords[0][3],1,false);			
			}
		}
		initialWords = null;
		return nextWords;
	}

	/**
	 * Here are stored words and additional information which is needed to entropy encode each block 
	 * 
	 * @param rLevel indicates the resolution level of the subband to be encoded
	 * @param subband determines the subband once is known the resolution level
	 * @param pass indicates the coding pass that is being encoded
	 * @param word contains a word that has been encoded
	 * @param length indicates the lenght of the word to be encoded. Valid values are between 1 and 4.
	 * @param stage indicates if the word corresponds to the significance encoding, the sign encoding or the refinement.
	 *            <ul>
	 *            <li> -2- Ds coding
	 *            <li> -1- significance of components
	 *            <li> 0 - significance of a set of components
	 *            <li> 1 - sign
	 *            <li> 2 - refinement
	 *            </ul>
	 * @param impossiblePattern indicates if all zeros value if possible
	 */
	private void updateStreams(int rLevel,int subband, int pass, int word, int length, int stage, boolean impossiblePattern){
		
		if (entropyCode==0){
			if (stage==2){//refinement
				this.refinementAC[rLevel].addBit(word);
			} else {//The stage is not refinement stage
				int rLevelAux = rLevel;
				if (rLevel>0 && stage!=-2){
					rLevelAux= rLevel+1; 
				} 
				sortingAC[rLevelAux][subband][pass].addByte((byte)length);
				sortingAC[rLevelAux][subband][pass].addByte((byte)word);				
			}
		}else if (entropyCode==1) {//Raw or Recommended entropy encoder
			if (stage==2){//refinement
				this.refinementAC[rLevel].addBit(word);
			} else {//The stage is not refinement stage
				byte context = getRecommededContext(stage, length, impossiblePattern);
				int rLevelAux = rLevel;
				if (rLevel>0 && stage!=-2){
					rLevelAux= rLevel+1; 
				} 
				
				sortingAC[rLevelAux][subband][pass].addByte(context);
				sortingAC[rLevelAux][subband][pass].addByte((byte)word);
				
				if (stage<1 && length>1){
					updateLengthEntropyEncoder(word,context);
				}
			} 
		} 
	}
	

	

	/**
	 * Perform the refinement of the components of the block for the given bitPlane
	 * 
	 * @param bitPlane indicates the bitplane that is being encoded
	 */
	public void refinementCoding(int bitPlane) {
		int families = 3;
		int threshold = ((int) 1 << bitPlane);// 2^bitPlane
		this.refinementAC = new BitStream[resolutionLevels];
		int refinedValue;
		int magnitudeRefinedValue;
		int refinementBit =0;
		int refinementBitLength = 1;
		boolean impossiblePattern = false;
		int refinementStage = 2;
		int refinementPass = 0;
		for (int rLevel = 0; rLevel < resolutionLevels; rLevel++) {
			//refinementAC[rLevel] = new BitStream(byteBuff*8);
			refinementAC[rLevel] = new BitStream((rLevel+1)*8);
			int sizeResolutionLevel = (int) 1 << rLevel;

			for (int subband = 0; subband < families; subband++) {
				if (rLevel != 2) {
					for (int y = 0; y < sizeResolutionLevel; y++) {
						for (int x = 0; x < sizeResolutionLevel; x++) {
							if (blockStatus[rLevel][subband][y][x] == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								// the coefficient has been found significant in previous bitplanes, and should be refined.
								refinedValue = (int) (imageSamples[yInit[rLevel][subband]+ y][xInit[rLevel][subband] + x]);
								if (refinedValue < 0) {
									magnitudeRefinedValue = -refinedValue;
								} else {
									magnitudeRefinedValue = refinedValue;
								}
								if (( magnitudeRefinedValue & threshold) != 0) {
									refinementBit = 1;
								} else {
									refinementBit = 0;
								}
								updateStreams(rLevel,subband,refinementPass,refinementBit,refinementBitLength,refinementStage,impossiblePattern);
								
								if (computeDistortion) {
									int previousApproximation = (refinedValue / (threshold * 2)) * (threshold * 2);
									int currentApproximation = (refinedValue / threshold) * threshold;
									distortion.improvedValue( previousApproximation, currentApproximation, refinedValue);
								}
							} else if (blockStatus[rLevel][subband][y][x] == 1) {
								// the coefficient has been found significant in this bitplane, and should be refined in the
								// following bitplanes
								blockStatus[rLevel][subband][y][x] = 2;
							}
						}
					}
				} else {
					for (int y = 0; y < 2; y++) {
						for (int x = 0; x < 2; x++) {
							if (blockStatus[rLevel][subband][y][x] == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								// the coefficient has been found significant in previous bitplanes, and should be refined.
								refinedValue = (int) (imageSamples[yInit[rLevel][subband]+ y][xInit[rLevel][subband] + x]);
								if (refinedValue < 0) {
									magnitudeRefinedValue = -refinedValue;
								} else {
									magnitudeRefinedValue = refinedValue;
								}
								if (( magnitudeRefinedValue & threshold) != 0) {
									refinementBit = 1;
								} else {
									refinementBit = 0;
								}
								updateStreams(rLevel,subband,refinementPass,refinementBit,refinementBitLength,refinementStage,impossiblePattern);
								
								if (computeDistortion) {
									int previousApproximation = (refinedValue / (threshold * 2)) * (threshold * 2);
									int currentApproximation = (refinedValue / threshold) * threshold;
									distortion.improvedValue( previousApproximation, currentApproximation, refinedValue);
								}
							} else if (blockStatus[rLevel][subband][y][x] == 1) {
								// the coefficient has been found significant in this bitplane, and should be refined in the
								// following bitplanes
								blockStatus[rLevel][subband][y][x] = 2;
							}
						}
					}
					for (int y = 0; y < 2; y++) {
						for (int x = 2; x < 4; x++) {
							if (blockStatus[rLevel][subband][y][x] == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								// the coefficient has been found significant in previous bitplanes, and should be refined.
								refinedValue = (int) (imageSamples[yInit[rLevel][subband]+ y][xInit[rLevel][subband] + x]);
								if (refinedValue < 0) {
									magnitudeRefinedValue = -refinedValue;
								} else {
									magnitudeRefinedValue = refinedValue;
								}
								if (( magnitudeRefinedValue & threshold) != 0) {
									refinementBit = 1;
								} else {
									refinementBit = 0;
								}
								updateStreams(rLevel,subband,refinementPass,refinementBit,refinementBitLength,refinementStage,impossiblePattern);
								
								if (computeDistortion) {
									int previousApproximation = (refinedValue / (threshold * 2)) * (threshold * 2);
									int currentApproximation = (refinedValue / threshold) * threshold;
									distortion.improvedValue( previousApproximation, currentApproximation, refinedValue);
								}
							} else if (blockStatus[rLevel][subband][y][x] == 1) {
								// the coefficient has been found significant in this bitplane, and should be refined in the
								// following bitplanes
								blockStatus[rLevel][subband][y][x] = 2;
							}
						}
					}
					for (int y = 2; y < 4; y++) {
						for (int x = 0; x < 2; x++) {
							if (blockStatus[rLevel][subband][y][x] == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								// the coefficient has been found significant in previous bitplanes, and should be refined.
								refinedValue = (int) (imageSamples[yInit[rLevel][subband]+ y][xInit[rLevel][subband] + x]);
								if (refinedValue < 0) {
									magnitudeRefinedValue = -refinedValue;
								} else {
									magnitudeRefinedValue = refinedValue;
								}
								if (( magnitudeRefinedValue & threshold) != 0) {
									refinementBit = 1;
								} else {
									refinementBit = 0;
								}
								updateStreams(rLevel,subband,refinementPass,refinementBit,refinementBitLength,refinementStage,impossiblePattern);
								
								if (computeDistortion) {
									int previousApproximation = (refinedValue / (threshold * 2)) * (threshold * 2);
									int currentApproximation = (refinedValue / threshold) * threshold;
									distortion.improvedValue( previousApproximation, currentApproximation, refinedValue);
								}
							} else if (blockStatus[rLevel][subband][y][x] == 1) {
								// the coefficient has been found significant in this bitplane, and should be refined in the
								// following bitplanes
								blockStatus[rLevel][subband][y][x] = 2;
							}
						}
					}
					for (int y = 2; y < 4; y++) {
						for (int x = 2; x < 4; x++) {
							if (blockStatus[rLevel][subband][y][x] == 2 && !bitPlaneOfZeros(rLevel, subband, bitPlane)) {
								// the coefficient has been found significant in previous bitplanes, and should be refined.
								refinedValue = (int)(imageSamples[yInit[rLevel][subband] + y][xInit[rLevel][subband] + x]);
								if (refinedValue < 0) {
									magnitudeRefinedValue = -refinedValue;
								} else {
									magnitudeRefinedValue = refinedValue;
								}
								if (( magnitudeRefinedValue & threshold) != 0) {
									refinementBit = 1;
								} else {
									refinementBit = 0;
								}
								updateStreams(rLevel,subband,refinementPass,refinementBit,refinementBitLength,refinementStage,impossiblePattern);
								if (computeDistortion) {
									int previousApproximation = (refinedValue / (threshold * 2)) * (threshold * 2);
									int currentApproximation = (refinedValue / threshold) * threshold;
									distortion.improvedValue( previousApproximation, currentApproximation, refinedValue);
								}
							} else if (blockStatus[rLevel][subband][y][x] == 1) {
								// the coefficient has been found significant in  this bitplane, and should be refined in the
								// following bitplanes
								blockStatus[rLevel][subband][y][x] = 2;
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
	private void updateLengthEntropyEncoder(int word, byte context) {
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
			this.codeLength[0] += lengthOfTwoBitsVariableLenghtCode_Option0[symbol];
			this.codeLength[1] += 2;// uncoded option
		} else if (length == 3) {
			this.codeLength[2] += lengthOfThreeBitsVariableLenghtCode_Option0[symbol];
			this.codeLength[3] += lengthOfThreeBitsVariableLenghtCode_Option1[symbol];
			this.codeLength[4] += 3;// uncoded option
		} else if (length == 4) {
			this.codeLength[5] += lengthOfFourBitsVariableLenghtCode_Option0[symbol];
			this.codeLength[6] += lengthOfFourBitsVariableLenghtCode_Option1[symbol];
			this.codeLength[7] += lengthOfFourBitsVariableLenghtCode_Option2[symbol];
			this.codeLength[8] += 4;
		}
	}

	/**
	 * Initialize the array with contains the status of each AC component of the block
	 */
	private void initBlockStatus() {
		int families = 3;
		this.blockStatus = new byte[resolutionLevels][families][][];
		for (int rLevel = 0; rLevel < resolutionLevels; rLevel++) {
			int sizeResolutionLevel = (int) 1 << rLevel;
			for (int subband = 0; subband < families; subband++) {
				blockStatus[rLevel][subband] = new byte[sizeResolutionLevel][sizeResolutionLevel];
				for (int y = 0; y < sizeResolutionLevel; y++) {
					for (int x = 0; x < sizeResolutionLevel; x++) {
						blockStatus[rLevel][subband][y][x] = 0;
					}
				}
			}
		}
	}

	/**
	 * Initialize the bit streams and the byte streams used to store the significance block and to store the 
	 * the contexts of the encoded words to be further entropy encoded.
	 * 
	 * @param bitPlane indicates the bitplane that is going to be encoded
	 */
	private void initStreams(int bitPlane) {
		sortingAC = new ByteStream[resolutionLevels + 1][][];
		
		for (int rLevel = 0; rLevel <= resolutionLevels; rLevel++) {
			int codingACPasses = rLevel + 1;
			int families = 3;
			int rLevelAux = rLevel-1;
			if (rLevel == 0) {
				codingACPasses = 2;
				families = 1;// the three parents are encoded together
				rLevelAux = 0;
			} else if (rLevel == 1) {
				codingACPasses = 1;
				families = 1;
				rLevelAux = 1;
			} // else {... the values initially set are taken}
			sortingAC[rLevel] = new ByteStream[families][codingACPasses];
			
			for (int subband = 0; subband < families; subband++) {
				
				sortingAC[rLevel][subband] = new ByteStream[codingACPasses];
				
				
				sortingAC[rLevel][subband][0] = new ByteStream((0+1)*8);
				
				if (!bitPlaneOfZeros(rLevelAux,subband, bitPlane)){
					for (int pass = 1; pass < codingACPasses; pass++) {
						sortingAC[rLevel][subband][pass] = new ByteStream((pass+1)*8);
					}
				}
			}
		}
	}

	/**
	 * This function update the blockStatus for the values that should not be
	 * further encoded due to the weighting step for a given bitplane
	 * 
	 * @param bitPlane indicates the bitplane for which the blockStatus is updated
	 */
	public void updateLeastSignificantBP(int bitPlane) {
		for (int rLevel = 0; rLevel < resolutionLevels; rLevel++) {
			int sizeResolutionLevel = (int) 1 << rLevel;
			for (int subband = 0; subband < 3; subband++) {
				int subbandNumber = (3 * rLevel + subband) + 1;
				// residual subband is not considered while coding AC components
				if (bitPlane < BP[subbandNumber]) {
					// all the values in this are necessarily zero due to the
					// weigthing stage
					// and should not be further encoded
					for (int y = 0; y < sizeResolutionLevel; y++) {
						for (int x = 0; x < sizeResolutionLevel; x++) {
							blockStatus[rLevel][subband][y][x] = -1;
						}
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
	 *            <li> -2- Ds coding
	 *            <li> -1- significance of components
	 *            <li> 0 - significance of a set of components
	 *            <li> 1 - sign
	 *            <li> 2 - refinement
	 *            </ul>
	 * @param length indicates the lenght of the word to be encoded. Valid values are between 1 and 4.
	 * @param impossiblePattern indicates if all zeros value if possible.
	 * 
	 * @return a byte that contains the context for entropy encoding the word.
	 */
	public byte getRecommededContext(int stage, int length, boolean impossiblePattern) {
		byte context = 0;
		if (stage <= 0 ) {// this context corresponds to the significance encoding
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

	public byte[][][][] getBlockStatus() {
		return blockStatus;
	}

	public byte[] getD() {
		return D;
	}

	public byte getDs() {
		return Ds;
	}

	public BitStream[] getRefinementAC() {
		return this.refinementAC;
	}

	public int[] getCodeLength() {
		return this.codeLength;
	}

}
