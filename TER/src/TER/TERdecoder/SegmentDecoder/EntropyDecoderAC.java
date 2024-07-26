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

import GiciException.ParameterException;
import TER.TERdecoder.ReadFile.ReadBufferedStream;

/**
 * This class entropy decodes the word required bu the user
 * requires. Usage example:<br>
 * &nbsp; construct<br>
 * &nbsp; setParameters<br>
 * &nbsp; getWord<br>
 * 
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.2
 */
public class EntropyDecoderAC{
	
	/**
	 * Definition in {@link SegmentDecode2D#encodedSegment}
	 */
	ReadBufferedStream encodedSegment = null;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.EncodeGaggleAC#entropyAC}
	 */
	int entropyAC;

	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.EncodeGaggleAC#ids}
	 */
	int ids[][] = null;
	
	/**
	 * Indicates the number of gaggles in the segment
	 */
	int numberOfGaggles;
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.EncodeGaggleAC#noCodeId}
	 */
	int[] noCodeId = {1,3,3};
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.EncodeGaggleAC#codeIdLength}
	 */
	int[] codeIdLength = {1,2,2};
	
	/**
	 * Constructor that receives encoded bit stream
	 * 
	 * @param encodedSegment definition in {@link SegmentDecode2D#encodedSegment}
	 * @param numberOfGaggles definition in {@link #numberOfGaggles}
	 */
	public EntropyDecoderAC(ReadBufferedStream encodedSegment, int numberOfGaggles){
		this.encodedSegment = encodedSegment;
		this.numberOfGaggles = numberOfGaggles;
	}
	
	/**
	 * Set the parameters required to entropy decode the words required by the user
	 * 
	 * @param entropyAC definition in {@link TER.TERcoder.SegmentCoder.EncodeGaggleAC#entropyAC}
	 */
	public void setParameters(int entropyAC){
		this.entropyAC = entropyAC;
		this.ids = null;
		this.ids = new int[numberOfGaggles][3];
		for(int gaggle=0; gaggle<numberOfGaggles; gaggle++){
			for(int k = 0; k<3; k++){
				ids[gaggle][k] = -1;
			}
		}
		parametersSet = true;
	}
	
	/**
	 * Decodes a word according with the parameters given by the user
	 * 
	 * @param stage indicates if the word corresponds to the significance encoding, the sign encoding or the refinement.
	 *            <ul>
	 *            <li> 0- significance
	 *            <li> 1- sign
	 *            <li> 2- refinement
	 *            </ul>
	 * @param length indicates the length of the word to be decoded
	 * @param impossiblePattern indicates whether all zeros is a possible pattern
	 * @param gaggle indicates in which gaggle is located the word that is going to be decoded
	 * 
	 * @return an integer that represented the decoded word
	 * 
	 * @throws Exception when something goes wrong (tipically reaching the end of the bit stream) and decoding process must be stoped
	 */
	public int getWord(int stage, int length, boolean impossiblePattern, int gaggle) throws Exception{
		if(!parametersSet){
			throw new ParameterException("EntropyDecoder has not been initialized");
		}
		
		int word =0;
		if (entropyAC == 0){
			try{
			word = getBits(length);
			} catch(Exception e){
				throw new Exception("File finished");
			}
		} else if(entropyAC == 1){
			try{
				int symbol=0;
				if (length>=2 && stage==0){
					if(ids[gaggle][length-2]==-1){
						int idLength=codeIdLength[length-2];
						ids[gaggle][length-2] = getBits(idLength);
					}
					if (length==2){
						symbol=getTwoLengthSymbol(gaggle);
					} else if (length==3){
						symbol=getThreeLengthSymbol(gaggle);
					} else if (length==4){
						symbol= getFourLengthSymbol(gaggle);	
					}
				} else {
					symbol= getBits(length);
				}
				
				byte context = getRecommededContext(stage,length,impossiblePattern);
				word = unMapSymbol(symbol,context);
			}catch(Exception e){
				throw new Exception("File finished");
			}
		}else {
			System.out.println("\n Not implemented yet!! " + entropyAC);
			throw new Exception("File finished");
		}
		
		return word;
		
	}
	
	/**
	 * Gets a bit from the encoded bit stream
	 * 
	 * @return a bit 
	 * 
	 * @throws Exception when is not possible get a bit from the bit stream
	 */
	private boolean getBit() throws Exception{
		return encodedSegment.getBit();
	}
	
	/**
	 * Gets 'length' bit from the encoded bit stream
	 * 
	 * @param length indicates the nubmer of bits to be readed
	 * 
	 * @return an integer which represents the bits readed from the bitstream
	 * 
	 * @throws Exception when is not possible get 'length' bits from the bit stream
	 */
	private int getBits(int length) throws Exception{
		return encodedSegment.getBits(length);
	}
	
	/**
	 * Decodes the symbols with length 2
	 * 
	 * @param gaggle indicates in which gaggle is located the word that is going to be decoded
	 * 
	 * @return an integer representing the decoded symbol 
	 * 
	 * @throws Exception when the bit stream is finished before decoding the symbol
	 */
	private int getTwoLengthSymbol(int gaggle) throws Exception{
		int symbol;
		if(noCodeId[0]==ids[gaggle][0]){
			symbol= getBits(2);
		} else {
			symbol = 0;
			for(;symbol<3 && !getBit() ;symbol++);
		}
		return symbol;
	}
	
	/**
	 * Decodes the symbols with length 3
	 * 
	 * @param gaggle indicates in which gaggle is located the word that is going to be decoded
	 * 
	 * @return an integer representing the decoded symbol 
	 * 
	 * @throws Exception when the bit stream is finished before decoding the symbol
	 */
	private int getThreeLengthSymbol(int gaggle) throws Exception{
		int symbol=0;
		if(noCodeId[1]==ids[gaggle][1]){
			symbol= getBits(3);	
		} else if (ids[gaggle][1] == 0) {
			symbol = 0;
			if (getBit()){
				symbol = 0;
			} else if (getBit()){
				symbol = 1;
			} else if (getBit()){
				symbol = 2;
			} else {
				int bits = getBits(2);
				if (bits==0){
					symbol = 3;
				} else if (bits==1){
					symbol = 4;
				} else if (bits==2){
					symbol = 5;
				} else {
					if (getBit()){ //the bit equals 1
						symbol = 7;
					} else {
						symbol = 6;
					}
				}
			}			
		} else if (ids[gaggle][1] == 1){
			symbol = 0;
			int bits = getBits(2);
			if ( bits==2 ){
				symbol = 0;
			} else if ( bits==3){
				symbol = 1;
			} else if ( bits==1 ){
				if (getBit()){ //the bit equals 1
					symbol = 3;
				} else {
					symbol = 2;
				}
			} else if ( bits==0 ){
				bits = getBits(2);
				if ( bits==2 ){
					symbol = 4;
				} else if ( bits==3 ){
					symbol = 5;
				} else if ( bits==0 ){
					symbol = 6;
				} else if ( bits==1 ){
					symbol = 7;
				}
			}
		}
		return symbol;
	}
	
	/**
	 * Decodes the symbols with length 4
	 * 
	 * @param gaggle indicates in which gaggle is located the word that is going to be decoded
	 * 
	 * @return an integer representing the decoded symbol 
	 * 
	 * @throws Exception when the bit stream is finished before decoding the symbol
	 */
	private int getFourLengthSymbol(int gaggle) throws Exception{
		int symbol=0;
		if(noCodeId[2]==ids[gaggle][2]){
			symbol= getBits(4);	
		} else if (ids[gaggle][2] == 0) {
			if (getBit()){
				symbol = 0;
			} else if (getBit()){
				symbol = 1;
			} else if (getBit()){
				symbol = 2;
			} else if (getBit()){
				symbol = 3;
			} else {
				if (!getBit()){//gets 0
					int bits = getBits(2);
					if (bits==0){
						symbol = 4;
					} else if (bits==1){
						symbol = 5;
					} else if (bits==2){
						symbol = 6;
					} else if (bits==3){
						symbol = 7;
					}  // i.e. symbol = 4 + bits;
				} else {//gets 1
					int bits = getBits(3);
					if (bits==0){
						symbol = 8;
					} else if (bits==1){
						symbol = 9;
					} else if (bits==2){
						symbol = 10;
					} else if (bits==3){
						symbol = 11;
					} else if (bits==4){
						symbol = 12;
					} else if (bits==5){
						symbol = 13;
					} else if (bits==6){
						symbol = 14;
					} else if (bits==7){
						symbol = 15;
					} // i.e. symbol = 8 + bits; 
				}
			}
		} else if (ids[gaggle][2] == 1){
			int bits = getBits(2);
			if ( bits==2 ){
				symbol = 0;
			} else if ( bits==3 ){
				symbol = 1;
			} else if ( bits==1 ){
				if (!getBit()){//gets 0
					symbol = 2;
				} else {//gets 1
					symbol = 3;
				}
			} else if ( bits==0 ){
				bits = getBits(2);
				if ( bits==2 ){
					symbol = 4;
				} else if ( bits==3){
					symbol = 5;
				} else if ( bits==0 ){
					bits = getBits(2);
					if ( bits==0 ){
						symbol = 6;
					} else if ( bits==1 ){
						symbol = 7;
					} else if ( bits==2 ){
						symbol = 8;
					} else if ( bits==3 ){
						symbol = 9;
					} 
				} else if ( bits==1 ){
					bits = getBits(2);
					if ( bits==0 ){
						symbol = 10;
					} else if ( bits==1 ){
						symbol = 11;
					} else if ( bits==2 ){
						if (!getBit()){//get 0
							symbol = 12;
						} else { //get 1
							symbol = 13;
						}
					} else if ( bits==3 ){
						if (!getBit()){//get 0
							symbol = 14;
						} else { //get 1
							symbol = 15;
						}
					}
				}
			}
		} else if (ids[gaggle][2] == 2){
			int bits = getBits(3);
			if (bits ==4){
				symbol = 0;
			} else if (bits ==5){
				symbol = 1;
			} else if (bits ==6){
				symbol = 2;
			} else if (bits ==7){
				symbol = 3;
			} else if (bits ==2){
				if (!getBit()){//get 0
					symbol = 4;
				} else {//get 1
					symbol = 5;
				}
			} else if (bits ==3){
				if (!getBit()){//get 0
					symbol = 6;
				} else {//get 1
					symbol = 7;
				}				
			} else if (bits ==1){
				bits = getBits(2);
				if (bits==0){
					symbol = 8;
				} else if (bits==1){
					symbol = 9;
				} else if (bits==2){
					symbol = 10;
				} else if (bits==3){
					symbol = 11;
				}//i.e. 8+bits
			} else if (bits ==0){
				bits = getBits(2);
				if (bits==0){
					symbol = 12;
				} else if (bits==1){
					symbol = 13;
				} else if (bits==2){
					symbol = 14;
				} else if (bits==3){
					symbol = 15;
				}//i.e. 12+bits
			} 
		}
		return symbol;
	}
	
	/**
	 * Definition in {@link TER.TERcoder.SegmentCoder.CodeBlockAC#getRecommededContext(int, int, boolean)}
	 */
	public byte getRecommededContext(int stage, int length, boolean impossiblePattern) {
		byte context = 0;
		if (stage == 0){//this context corresponds to the significance encoding  
			if (length == 1){
				context = (byte) 64; // 1-bit significance word
			} else if( length == 2){
				context = (byte) 65; // 2-bit significance word
			} else if ( length == 3){
				if (impossiblePattern){
					context = (byte) 66; // 3-bit significance word (000 impossible value) 
					// Not used due the structure used to store the coded data 
				} else {
					context = (byte) 67; // 3-bit significance word (any value is possible)
				}
			} else if ( length == 4){
				if (impossiblePattern){
					context = (byte) 68; // 4-bit significance word (0000 impossible value)
				} else {
					context = (byte) 69; // 4-bit significance word (any value is possible)
				}
			}
		} else if (stage == 1){ // this context corresponds to the sign encoding
			if (length == 1){
				context = (byte) 70; // 1-bit sign word
			} else if( length == 2){
				context = (byte) 71; // 2-bit sign word
			} else if ( length == 3){
				context = (byte) 72; // 3-bit sign word
			} else if ( length == 4){
				context = (byte) 73; // 4-bit sign word
			}
		} else { // this context corresponds to the refinement
			context = (byte) 74;
		}
		return context;
	}

	/**
	 * Map symbols to words
	 * 
	 * @param symbol indicates the symbol to be mapped
	 * @param context indicates the kind of word to be decoded, i.e. the lenght and the encoding pass
	 * 
	 * @return an integer that represents the decoded word
	 */
	private int unMapSymbol(int symbol, byte context){
		int word = 0;
		int iPDF2MapperGeneral[] = {0,2,1,3};//{0,2,1,3};
		int iPDF3MapperGeneral[] = {2,0,4,6,1,3,5,7};//{1,4,0,5,2,6,3,7};
		int iPDF3MapperImpossibleValue[] = {2,4,6,1,3,5,7,0};//{/*-1*/7,3,0,4,1,5,2,6}; // 000 is impossible
		int iPDF4MapperGeneral[] = {8,1,4,2,12,5,3,10,9,6,0,14,7,11,13,15};//{10,1,3,6,2,5,9,12,0,8,7,13,4,14,11,15};
		int iPDF4MapperImpossibleValue[] = {8,1,4,2,12,5,3,10,9,6,14,7,11,13,15,0};//{/*-1*/15,1,3,6,2,5,9,11,0,8,7,12,4,13,10,14}; // 0000 is impossible
		
		switch(context){
		case (byte) 64: // 1-bit significance word
			word = symbol;
		break;
		case (byte) 65: // 2-bit significance word
			word=iPDF2MapperGeneral[symbol];
		break;
		case (byte) 66: // 3-bit significance word, 000 impossible value
			word=iPDF3MapperImpossibleValue[symbol];	
		break;		
		case (byte) 67: // 3-bit significance word, general case (any value is possible)
			word=iPDF3MapperGeneral[symbol];	
		break;
		case (byte) 68: // 4-bit significance word, 0000 impossible value
			word=iPDF4MapperImpossibleValue[symbol];	
		break;		
		case (byte) 69: // 4-bit significance word, general case (any value is possible)
			word=iPDF4MapperGeneral[symbol];	
		break;	
		default :
			word=symbol;
		}
		return word;
	}

}