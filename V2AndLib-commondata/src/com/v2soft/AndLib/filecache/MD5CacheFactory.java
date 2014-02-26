/*
 * Copyright (C) 2014 V.Shcryabets (vshcryabets@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
