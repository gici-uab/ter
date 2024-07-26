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

import java.io.EOFException;

import GiciException.*;
import TER.TERdecoder.ReadFile.ReadBufferedStream;
import TER.TERdecoder.SegmentDecoder.InitialDecoding;

public class DecodeGaggleDC{
	/**
	 * To know if parameters are set.
	 * <p>
	 * True indicates that they are set otherwise false.
	 */
	boolean parametersSet = false;
	
	
	int channel, segment, gaggle, gaggleSize, gaggleSizeDC;
	
	int resolutionLevels;
	
	int initBlock;
	
	int bitDepthAC, bitDepthDC;
	
	int bp, completionMode;
	
	float gammaValue;
	
	float recoveredImage[][][] = null;
	
	ReadBufferedStream encodedStream = null;
	byte byteStream[][] = null; 
	int currentLayer;
	
	int blocksPerSegment[] = null;
	
	int DCs_2s_comp[] = null;
	int DCs[] = null;
	
	public DecodeGaggleDC(float recoveredImage[][][]){
		this.recoveredImage = recoveredImage;
	}
	
	public void setParameters(byte byteStream[],
			int channel, int segment, int gaggle,
			int gaggleSize, int gaggleSizeDC,
			int bitDepthDC, int bitDepthAC,
			int blocksPerSegment[],
			int resolutionLevels, int bp,
			int completionMode, float gammaValue) throws Exception{
		
		encodedStream = new ReadBufferedStream(byteStream);
				
		this.channel = channel;
		this.segment = segment;
		this.gaggle = gaggle;
			
		this.gaggleSize = gaggleSize;
		this.gaggleSizeDC = gaggleSizeDC;
		
		this.bitDepthDC = bitDepthDC;
		this.bitDepthAC = bitDepthAC;
				
		this.blocksPerSegment = blocksPerSegment;
		
		this.resolutionLevels = resolutionLevels;
		this.bp = bp;
		
		this.completionMode = completionMode;
		this.gammaValue = gammaValue;
		
		initBlock = 0;
		for(int k=0;k<segment;k++){
			initBlock += blocksPerSegment[k];
		}
		initBlock += (gaggle*gaggleSizeDC);
		
		parametersSet = true;	
	}
	
	public void run() throws Exception {
		//If parameters are not set run cannot be executed
		if(!parametersSet){
			throw new ParameterException("DecodeGaggleDC cannot run if parameters are not properly set.");
		}
		
		
		InitialDecoding idcDC = new InitialDecoding();
		idcDC.setParameters(gaggleSize, bitDepthDC, bitDepthAC, gaggleSizeDC, gaggleSize, bp, true, completionMode);
		DCs_2s_comp= idcDC.run(encodedStream);
		boolean endOfStream=idcDC.getAbnormalTermination();
		
		int quantizedDCBitPlanes = idcDC.getDinamicRange();
		
		DCs = new int[DCs_2s_comp.length];
		
		int threshold = ( (int) 1<<quantizedDCBitPlanes );
		int N = ((int) 1 << (bitDepthDC)); //2^(BitDepthDC)	
		int gamma = 0;
		if (quantizedDCBitPlanes>bp){
			gamma = Math.round(gammaValue*threshold);
		}
		
		for(int block=0;block<DCs_2s_comp.length;block++ ){			
			if ( DCs_2s_comp[block] < N/2 ){
				DCs[block] = DCs_2s_comp[block] + gamma; 
			} else {
				DCs[block] = DCs_2s_comp[block] - N - gamma;
			}
		}
		
		if (!endOfStream){	
			for(int bitPlane=quantizedDCBitPlanes-1;bitPlane>=bp && !endOfStream; bitPlane--){	
				try{
					refineDCs(bitPlane,bp);
				}catch (EOFException e){
					endOfStream = true;
				}
			}	
		}
		
		putDCs();
		
		parametersSet = false;//parameters must be update before running again the class
	}
	
	public void refineDCs(int bitPlane, int BPLL) throws Exception{
		int threshold = ( (int) 1<<bitPlane );//2^bitPlane
		int N = ((int) 1 << (bitDepthDC)); //2^(BitDepthDC)	
		int gamma = 0;
		if (bitPlane>bp){
			gamma = Math.round(gammaValue*threshold);
		}
		for(int block=0; block<gaggleSize; block++){
			if (encodedStream.getBit()){//the refined bit equals one
				DCs_2s_comp[block] += threshold;
			} //else { DCs_2s_comp[block] += 0; }
			if ( DCs_2s_comp[block] < N/2 ){
				DCs[block] = DCs_2s_comp[block] + gamma; 
			} else {
				DCs[block] = DCs_2s_comp[block] - N - gamma;
			}
		}
	}
	
		
	private void putDCs(){
		int xSize = recoveredImage[channel][0].length;
		int xResidualSubBandSize = ((int) xSize >> (resolutionLevels-1)) ;
		for(int block = 0;block<gaggleSize;block++){
			int x0 = (initBlock+block)%xResidualSubBandSize;
			int y0 = (initBlock+block)/xResidualSubBandSize;
			recoveredImage[channel][y0][x0] = (float) DCs[block];
		}
	}
	
	//////////////////////////////////
	///////// GET FUNCTIONS //////////
	//////////////////////////////////
	
	public float[][][] getRecoveredImage(){
		return recoveredImage;
	}

}