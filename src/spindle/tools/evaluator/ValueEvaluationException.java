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
package spindle.tools.evaluator;

import spindle.core.SpindleException;

/**
 * Signals that the value of a literal variable cannot be evaluated correctly.
 * 
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory
 * @since 2011.07.26
 * @since version 2.1.0
 */
public class ValueEvaluationException extends SpindleException {

	private static final long serialVersionUID = 1L;

	public ValueEvaluationException(String errorTag, Object... arguments) {
		this(errorTag, null, null, (Object[]) arguments);
	}

	public ValueEvaluationException(Throwable cause) {
		this(null, null, cause);
	}

	protected ValueEvaluationException(String errorTag, String message, Throwable cause, Object... arguments) {
		super(null, errorTag, message, cause, arguments);
	}

}
