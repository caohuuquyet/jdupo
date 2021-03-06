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
package spindle.engine.tdl2;

import spindle.engine.ReasoningEngineException;

public class LiteralDashboardUtilitiesException extends ReasoningEngineException {

	private static final long serialVersionUID = 1L;

	public LiteralDashboardUtilitiesException(String errorTag) {
		this(errorTag, null, null);
	}

	public LiteralDashboardUtilitiesException(Throwable cause) {
		this(null, null, cause);
	}

	public LiteralDashboardUtilitiesException(String errorTag, Throwable cause) {
		this(errorTag, null, cause);
	}

	public LiteralDashboardUtilitiesException(String errorTag, Object... arguments) {
		this(errorTag, null, null, arguments);
	}

	public LiteralDashboardUtilitiesException(String errorTag, String message, Throwable cause, Object... arguments) {
		super(null, errorTag, message, cause, arguments);
	}


}
