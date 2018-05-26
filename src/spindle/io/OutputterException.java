/**
 * SPINdle (version 2.2.4)
 * Copyright (C) 2009-2014 NICTA Ltd.
 *
 * This file is part of SPINdle project.
 * 
 * SPINdle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SPINdle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPINdle.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory 
 */
package spindle.io;

import spindle.core.SpindleException;

/**
 * Signals that an attempt to export the deafeasible theory or conclusions derived has failed.
 * 
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory
 * @since version 1.0.0
 * @version Last modified 2011.07.27
 */
public class OutputterException extends SpindleException {

	private static final long serialVersionUID = 1L;

	public OutputterException(String message) {
		this(message, null);
	}

	public OutputterException(Throwable cause) {
		this(null, cause);
	}

	public OutputterException(String message, Throwable cause) {
		super(null, null, message, cause);
	}

}
