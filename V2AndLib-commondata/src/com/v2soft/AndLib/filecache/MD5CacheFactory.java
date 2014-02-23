package com.v2soft.AndLib.filecache;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class MD5CacheFactory implements FileCache.NameFactory{
	@Override
	public String getLocalName(String resource) throws NoSuchAlgorithmException {
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			final byte [] pathBytes = resource.getBytes("utf-8");
			md.update(pathBytes, 0, pathBytes.length);
			final String filename = new BigInteger( 1, md.digest() ).toString( 16 );
			return filename;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
