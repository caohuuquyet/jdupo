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
package spindle.core.dom;

/**
 * Enumerate on the types of provability level according to the conclusion types.
 * 
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory
 * @version Last modified 2012.09.18
 * @since version 2.2.1
 */
public enum ProvabilityLevel {
	// the first two items of this enumeration should not be changed as these will affect the process in the reasoning
	// engines.
	DEFINITE, DEFEASIBLE, NONE;

	public static ConclusionType getPositiveConclusionType(ProvabilityLevel provability) {
		switch (provability) {
		case DEFINITE:
			return ConclusionType.DEFINITE_PROVABLE;
		case DEFEASIBLE:
			return ConclusionType.DEFEASIBLY_PROVABLE;
		default:
			return null;
		}
	}

	public static ConclusionType getNegativeConclusionType(ProvabilityLevel provability) {
		switch (provability) {
		case DEFINITE:
			return ConclusionType.DEFINITE_NOT_PROVABLE;
		case DEFEASIBLE:
			return ConclusionType.DEFEASIBLY_NOT_PROVABLE;
		default:
			return null;
		}
	}

	public static RuleType getRuleType(ProvabilityLevel provability) {
		switch (provability) {
		case DEFINITE:
			return RuleType.STRICT;
		case DEFEASIBLE:
			return RuleType.DEFEASIBLE;
		default:
			return null;
		}
	}

}