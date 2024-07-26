/*
 * GICI Library -
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
 * gici-info@deic.uab.es
 */
/**
 * Project: GiciStream
 *
 * Class: BufferedRandomAccess.java
 *
 * Description:
 *
 * @author Group on Interactive Coding of Images (GICI)
 *
 * Copyright (c) 2006
 */
package GiciStream;

import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class provides a wrapper for abstracting from the data source, which can
 * be a file or a buffer. The data source is choosed with the class's constructor.
 * If the data source is a file, then, data from file are buffered for a better 
 * efficiency in reading.
 * 
 * @author Group on Interactive Coding of Images (GICI)
 * @version 1.0.1 2008/04/09
 */
public class BufferedDataInputStream implements DataInput {

	/**
	 * An array of bytes used to buffer the input/output data.
	 * Elements <code>buf[0]</code>
	 * through <code>buf[count-1]</code> are the
	 * only bytes that can ever be read/write from/to the
	 * buffer;  element <code>buf[pos]</code> is
	 * the next byte to be read/write.
	 */
	private volatile byte buf[] = null;

	/**
	 * The index of the next byte to read/write from/to the buffer. This value
	 * should always be nonnegative and not shorter than <code>offset</offset>
	 * and not larger than the value of <code>count</code>. The next byte to be
	 * read/write from/to the buffer will be <code>buf[pos]</code>.
	 */
	private int pos;

	/**
	 * The index one greater than the last valid byte in the buffer. 
	 * This value should always be nonnegative
	 * and not larger than the length of <code>buf</code>.
	 * It  is one greater than the position of
	 * the last byte within <code>buf</code> that
	 * can ever be read/write from/to the buffer.
	 */
	private int count;

	/**
	 * Indicates the first available data in the buffer.
	 */
	private int offset;

	/**
	 * 
	 */
	private RandomAccessFile file = null;

	/**
	 * Indicates the offset of the file where data are from.
	 */
	private long filePos;

	/**
	 * Indicates, when data source is a file, if the End Of File mark is reached.
	 * Then, the <code>buf</code> contains the last bytes of the file.
	 */
	private boolean EOFInBuffer;

	/**
	 * It is the buffer access. Allowed values are:
	 * &nbsp "r"  read
	 * &nbsp	"rw" read & write (if file/buffer exists, removes it before
	 * &nbsp "rw+" read & write (if file/buffer exists, updates it)
	 * <p>
	 * 
	 */
	private int mode;

	/**
	 * Indicates the data source, whether data is read from file or no.
	 */
	private boolean readFromFile;

	/**
	 * It is the default size for the buffer when this was not passed
	 */
	private static int DEFAULT_BUFFER_SIZE = 1000;	// 1KB


	///////////////////////////////////////////////
	///////////////PRINCIPAL METHODS/////////////// 
	///////////////////////////////////////////////

	/**
	 * @param sz buffer size
	 */
	public  BufferedDataInputStream(int sz) {
		if (sz <= 0) {
			throw new IllegalArgumentException("Buffer size <= 0");
		}
		this.buf = new byte[sz];
		count = 0;
		offset = 0;
		filePos = 0;
		pos = 0;
		readFromFile = false;
	}

	/**
	 * 
	 * @param buf
	 */
	public BufferedDataInputStream(byte[] buf) {
		if (buf == null) {
			throw new NullPointerException();
		}		
		this.buf = buf;
		count = buf.length;
		offset = 0;
		filePos = 0;
		pos = 0;
		readFromFile = false;
	}

	/**
	 * 
	 * @param buf
	 * @param offset
	 */
	public BufferedDataInputStream(byte[] buf, int offset) {
		if (buf == null) {
			throw new NullPointerException();
		}
		this.buf = buf;
		count = this.buf.length-offset+1;
		this.offset = offset;
		filePos = 0;
		pos = offset;
		readFromFile = false;
	}

	/**
	 * 
	 * @param name
	 * @throws FileNotFoundException
	 */
	public BufferedDataInputStream(String name) throws FileNotFoundException {
		this(name != null ? new File(name) : null);
	}

	/**
	 * 
	 * @param file
	 * 
	 * @throws FileNotFoundException
	 */
	public BufferedDataInputStream(File file) throws FileNotFoundException {
		this(new RandomAccessFile(file, "r"));
	}

	/**
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public BufferedDataInputStream(RandomAccessFile file) throws FileNotFoundException {		

		this.file = file;
		buf = new byte[DEFAULT_BUFFER_SIZE];
		count = 0;
		offset = 0;
		filePos = 0;
		pos = 0;
		readFromFile = true;
	}

	/**
	 * Reads the next byte of data from the input stream. The value byte is
	 * returned as an <code>int</code> in the range <code>0</code> to
	 * <code>255</code>. If no byte is available because the end of the stream
	 * has been reached, the value <code>-1</code> is returned. This method
	 * blocks until input data is available, the end of the stream is detected,
	 * or an exception is thrown.
	 *
	 * <p> A subclass must provide an implementation of this method.
	 *
	 * @return     the next byte of data, or <code>-1</code> if the end of the
	 *             stream is reached.
	 * @throws IOException 
	 * @throws EOFException 
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int read() throws EOFException, IOException {
		return (int)(read1() & 0xff);
	}

	/**
	 * See the general contract of the <code>readFully</code>
	 * method of <code>DataInput</code>.
	 * <p>
	 * Bytes
	 * for this operation are read from the contained
	 * input stream.
	 *
	 * @param      b   the buffer into which the data is read.
	 * @exception  EOFException  if this input stream reaches the end before
	 *               reading all the bytes.
	 * @exception  IOException   if an I/O error occurs.
	 * @see        java.io.FilterInputStream#in
	 */
	public void readFully(byte[] b) throws IOException {
		readFully(b, 0, b.length);		
	}

	/**
	 * See the general contract of the <code>readFully</code>
	 * method of <code>DataInput</code>.
	 * <p>
	 * Bytes
	 * for this operation are read from the contained
	 * input stream.
	 *
	 * @param      b     the buffer into which the data is read.
	 * @param      off   the start offset of the data.
	 * @param      len   the number of bytes to read.
	 * @throws EOFException 
	 * @exception  EOFException  if this input stream reaches the end before
	 *               reading all the bytes.
	 * @exception  IOException   if an I/O error occurs.
	 * @see        java.io.FilterInputStream#in
	 */
	public void readFully(byte[] b, int off, int len) throws EOFException, IOException {	

		if (b == null) {
			throw new NullPointerException();
		} else {
			if ((off < 0) || (off > b.length) || (len < 0) ||
					((off + len) > b.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} 
		}
		
		for (int tempCount=0; len>0; ) {
			if (pos<count) {				
				tempCount = Math.min(count-pos, len);	// Read data from buffer
				System.arraycopy(buf, pos, b, off, tempCount);
				pos += tempCount;
				off += tempCount;
				len -= tempCount;				
			} else {
				if (EOFInBuffer) {
					throw new EOFException();
				} else {
					readBuffer();
				}
			}
		}		
	}

	public int skipBytes(int n) throws IOException {
		skipBytes((long)n);
		return 0;
	}

	public boolean readBoolean() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * See the general contract of the <code>readByte</code>
	 * method of <code>DataInput</code>.
	 * <p>
	 * Bytes
	 * for this operation are read from the contained stream.
	 *
	 * @return     the next byte of this input stream as a signed 8-bit
	 *             <code>byte</code>.
	 * @throws IOException 
	 * @exception  IOException   if an I/O error occurs.
	 */
	public synchronized byte readByte() throws EOFException, IOException{
		return (byte)read1();
	}

	/**
	 * Reads one input byte, zero-extends
	 * it to type <code>int</code>, and returns
	 * the result, which is therefore in the range
	 * <code>0</code>
	 * through <code>255</code>.
	 * This method is suitable for reading
	 * the byte written by the <code>writeByte</code>
	 * method of interface <code>DataOutput</code>
	 * if the argument to <code>writeByte</code>
	 * was intended to be a value in the range
	 * <code>0</code> through <code>255</code>.
	 *
	 * @return     the unsigned 8-bit value read.
	 * @exception  EOFException  if this stream reaches the end before reading
	 *               all the bytes.
	 * @exception  IOException   if an I/O error occurs.
	 */
	public int readUnsignedByte() throws EOFException, IOException {
		return readByte();		
	}

	/**
	 * Reads two input bytes and returns
	 * a <code>short</code> value. Let <code>a</code>
	 * be the first byte read and <code>b</code>
	 * be the second byte. The value
	 * returned
	 * is:
	 * <p><pre><code>(short)((a &lt;&lt; 8) | (b &amp; 0xff))
	 * </code></pre>
	 * This method
	 * is suitable for reading the bytes written
	 * by the <code>writeShort</code> method of
	 * interface <code>DataOutput</code>.
	 *
	 * @return     the 16-bit value read.
	 * @exception  EOFException  if this stream reaches the end before reading
	 *               all the bytes.
	 * @exception  IOException   if an I/O error occurs.
	 */
	public short readShort() throws EOFException, IOException {
		return (short)(
				(read1()<<8)|
				read1()
		);
	}

	public int readUnsignedShort() throws EOFException, IOException {
		return	(read1()<<8)|
		read1();
	}

	public char readChar() throws EOFException, IOException {
		return (char) (
				(read1())<<8|
				read1()
		);
	}

	public int readInt() throws EOFException, IOException {
		return	(read1()<<24)|
		(read1()<<16)|
		(read1()<<8)|
		read1();
	}

	public long readLong() throws EOFException, IOException {
		return (long)(
				((long)read1()<<56)|
				((long)read1()<<48)|
				((long)read1()<<40)|
				((long)read1()<<32)|
				((long)read1()<<24)|
				((long)read1()<<16)|
				((long)read1()<<8) |
				(long)read1()
		);
	}

	/**
	 * See the general contract of the <code>readFloat</code>
	 * method of <code>DataInput</code>.
	 * <p>
	 * Bytes
	 * for this operation are read from the contained
	 * input stream.
	 *
	 * @return     the next four bytes of this input stream, interpreted as a
	 *             <code>float</code>.
	 * @exception  EOFException  if this input stream reaches the end before
	 *               reading four bytes.
	 * @exception  IOException   if an I/O error occurs.
	 * @see        java.io.DataInputStream#readInt()
	 * @see        java.lang.Float#intBitsToFloat(int)
	 */
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	/**
	 * See the general contract of the <code>readDouble</code>
	 * method of <code>DataInput</code>.
	 * <p>
	 * Bytes
	 * for this operation are read from the contained
	 * input stream.
	 *
	 * @return     the next eight bytes of this input stream, interpreted as a
	 *             <code>double</code>.
	 * @exception  EOFException  if this input stream reaches the end before
	 *               reading eight bytes.
	 * @exception  IOException   if an I/O error occurs.
	 * @see        java.io.DataInputStream#readLong()
	 * @see        java.lang.Double#longBitsToDouble(long)
	 */
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}


	public String readLine() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String readUTF() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param pos
	 * @throws IOException
	 */
	public void seek(long pos) throws IOException {
		if (readFromFile) { // File is the data source
			filePos = pos;
			file.seek(filePos);
			count = 0;
			readBuffer();
		} else {	// Buffer is the data source
			if ((pos<offset) || (pos>count) ){
				throw new IndexOutOfBoundsException();
			} else {
				this.pos = (int)pos;
			}
		}
	}

	/**
	 * 
	 * @return a long integer that the current postion inside the file or stream
	 */
	public long getPos() {
		if (readFromFile) {
			return (long) (filePos+pos);			
		} else {
			return (long)pos;
		}
	}

	/**
	 * 
	 * @param n
	 * @return a long intger that indicates the number od bits skipped
	 * @throws EOFException
	 * @throws IOException
	 */
	public long skipBytes(long n)throws IOException {

		long skippedBytes = n;

		if (n<0) {
			throw new IOException();
		}

		if (readFromFile) {
			if ( n>length() ) {
				skippedBytes = 0;
			} else {
				if ( (pos+n)<count) { //We are inside the buffer
					pos += n;					 
				} else {
					filePos+=pos+n;
					file.seek(filePos);
					count = 0;
					readBuffer();
				}
			}

		} else {
			if (((offset+n) > count) ) {
				throw new IndexOutOfBoundsException(); 
			} else {
				pos += n;
			}
		}

		return skippedBytes;		 
	}

	/**
	 * 
	 * @return an integer that indicates the capacity of the buffer
	 */
	public int getCapacity() {
		return buf.length;
	}

	/**
	 * Returns the number of bytes that can be read from this buffer without
	 * blocking. The value returned is  <code>count&nbsp;- pos</code>, which
	 * is the number of bytes remaining to be read from the input buffer.
	 *
	 * @return  the number of bytes that can be read from the buffer without
	 * 		 blocking.
	 */
	public synchronized long available() throws IOException {
		return (long)(count-pos);
	}

	/**
	 * Returns the number of bytes that can be read from this buffer, taking into
	 * account any buffering.
	 * 
	 * @return The length of the stream, in bytes, or -1 if an I/O error ocurrs.
	 **/
	public synchronized long length() {
		if (readFromFile) {
			try {
				return file.length()-(filePos + pos);
			} catch (IOException e) {
				return -1;
			}
		} else {
			return (long)(count-pos);
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		file.close();
	}

	/**
	 * For debugging purpose.
	 */
	public String toString() {
		String str = "";

		if (readFromFile) {
			str += "Data source: FILE\n";
		} else {
			str += "Data source: BUFFER\n";
		}

		str += "Length: " + count + "\n";
		str += "Offset: " + offset + "\n";
		str += "DATA: ";

		for (int i=0; i<buf.length; i++) {
			if ((0xFF&buf[i]) < 16) {
				str += "0";
			}
			str += Integer.toHexString(0xFF&buf[i]);							
		}
		str += "\n";

		return str;
	}


	/**************************************************************************/
	/**                        AUXILIARY METHODS                             **/ 
	/**************************************************************************/

	/**
	 * Reads the next byte of data from this input stream. The value 
	 * byte is returned as an <code>int</code> in the range 
	 * <code>0</code> to <code>255</code>. If no byte is available 
	 * because the end of the stream has been reached, the value 
	 * <code>-1</code> is returned. 
	 * <p>
	 * This <code>read</code> method cannot block. 
	 *
	 * @return     the next byte of this input stream as a <code>int</code>.
	 * 
	 * @exception  EOFException  if this input stream has reached the end.
	 * @exception  IOException   if an I/O error occurs.
	 */
	private final int read1() throws IOException, EOFException{

		if(pos<count){ 
			return (buf[pos++]&0xFF);
		} else if(EOFInBuffer){ // EOF is reached
			pos = count+1; // Set position to EOF
			throw new EOFException();
		} else if (readFromFile){ // End of the buffer is reached			
			readBuffer();
			return read1();
		} else {
			throw new EOFException();
			//return -1;
		}
	}

	/**
	 * 
	 * @throws EOFException
	 * @throws IOException
	 */
	private void readBuffer() throws EOFException, IOException {

		// Set new offset
		filePos += count;
		offset = 0;
		pos = 0;

		if (filePos >= file.length()) {
			throw new EOFException();
		}
		file.seek(filePos);

		// Read new data
		count = file.read(buf,0,buf.length);		

		// Check for End of File or some error
		if (count == -1) {
			count = 0;
			EOFInBuffer = true;
		} else {
			if (count < buf.length) {  // EOF is reached
				EOFInBuffer = true;
			} else {
				EOFInBuffer = false;
			}
		}		
	}

}
