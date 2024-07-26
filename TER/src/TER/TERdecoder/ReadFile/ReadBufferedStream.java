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
package TER.TERdecoder.ReadFile;

import GiciStream.BufferedDataInputStream;


public class ReadBufferedStream extends BufferedDataInputStream{
	
	byte bytebuffer;
	int bytePossition = -1;
	long counter = 0;
	
	public ReadBufferedStream(String inputFile) throws Exception{
		super(inputFile);
	}
	
	public ReadBufferedStream(byte source[]) throws Exception{
		super(source);
	}
	
	public boolean getBit() throws Exception {
		boolean bit;
		if (bytePossition<0){
			bytebuffer = readByte();
			//System.out.println(super.getPos());
			bytePossition = 7;
			counter++;
		} 
		
		if (( bytebuffer & (1<<bytePossition) ) ==0 ){
			bit  = false;
		} else {
			bit  = true;
		}
		bytePossition--;
		
		return bit;
	}
	
	public int getBits(int length) throws Exception{
		int bits = 0;
		for( int i=0 ; i < length; i++){
			bits = bits << 1;
			if(getBit()){
				bits++;
			}
		}
		return(bits);
	}
	
	public void clearByte(){
		bytePossition = -1;
	}
	
	public void restartCounter(){
		counter = 0;
	}
	
	public long getCounter(){
		return counter;
	}
	
	public long getTotalBytes(){
		return (super.length()+super.getPos());
	}
	
	public long getRemaniningBytes(){
		return (super.length());
	}
	
	public int getBytePossition(){
		return bytePossition;
	}
}