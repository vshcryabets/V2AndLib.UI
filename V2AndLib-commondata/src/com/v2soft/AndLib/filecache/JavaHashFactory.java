package com.v2soft.AndLib.filecache;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class JavaHashFactory implements FileCache.NameFactory{
	@Override
	public String getLocalName(String resource) throws NoSuchAlgorithmException {
		return String.valueOf(resource.hashCode());
	}
}
