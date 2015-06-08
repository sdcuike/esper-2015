/*
 * OpenCredo-Esper - simplifies adopting Esper in Java applications. 
 * Copyright (C) 2010  OpenCredo Ltd.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.doctor.esper.spring;

/**
 * @author doctor
 * 
 * @see org.opencredo.esper.EsperStatementInvalidStateException;
 *
 * @time 2015年6月8日 下午5:08:05
 */
public class EsperStatementInvalidStateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EsperStatementInvalidStateException(String message) {
		super(message);
	}
}
