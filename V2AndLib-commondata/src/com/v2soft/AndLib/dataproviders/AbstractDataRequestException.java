/*
 * Copyright (C) 2012-2014 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.dataproviders;

import java.io.Serializable;

/**
 * Data request exception
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public abstract class AbstractDataRequestException extends ITaskException implements Serializable {
	private static final long serialVersionUID = 1L;

	public AbstractDataRequestException(String message) {
		super(message);
	}
	public AbstractDataRequestException(Throwable e) {
		super(e);
	}
}

