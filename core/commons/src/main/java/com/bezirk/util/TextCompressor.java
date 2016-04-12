package com.bezirk.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextCompressor {
	
	public static final Logger log = LoggerFactory.getLogger(TextCompressor.class);

	/**
	 * Uses a gzip compression to compress the String, a better option for a lengthier string. But the smaller string will have a space overhead. and returns a compressed byte[]
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static byte[] compress(byte[] str) {
		ByteArrayOutputStream obj=new ByteArrayOutputStream();
		if (str == null || str.length == 0) {
			return str;
		}
		try {
			GZIPOutputStream gzip = new GZIPOutputStream(obj);
			gzip.write(str);
			gzip.close();
		} catch (IOException e) {
			log.error("Exception while compressing the bytes", e);
			
		}
		return obj.toByteArray();
	}

	/**
	 * decompress the byte[] to the String format.
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String decompress(byte[] str){
		if (str == null || str.length == 0) {
			return Arrays.toString(str);
		}
		String outStr = "";
		try{
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str));
			BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
			String line;
			while ((line=bf.readLine())!=null) {
				outStr += line;
			}
		}catch(Exception e){
			log.error("Exception while decompressing the bytes", e);
		}
		return outStr;
	}
	
	
	/**
	 * compresses the input byte[] using deflater feature.
	 * @param bytes
	 * @return
	 */
	public static byte[] compressByteArrayUsingDeflater(byte[] bytes){
        
        ByteArrayOutputStream baos = null;
        Deflater dfl = new Deflater();
        dfl.setLevel(Deflater.BEST_COMPRESSION);
        dfl.setInput(bytes);
        dfl.finish();
        baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4*1024];
        try{
            while(!dfl.finished()){
                int size = dfl.deflate(tmp);
                baos.write(tmp, 0, size);
            }
        } catch (Exception ex){
        	log.error("Exception while compress Byte ArrayUsingDeflater", ex);
        } finally {
            try{
                if(baos != null) baos.close();
            } catch(Exception ex){
            	log.error("Exception while closing ByteArrayOutputStream", ex);
            }
        }
         
        return baos.toByteArray();
    }

}
