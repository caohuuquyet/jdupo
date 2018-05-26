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

import java.util.Comparator;

/**
 * Literal comparator
 * that provides the freedom to select whether to compare the literals with or without using the temporal information.
 * 
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory
 * @since version 2.2.1
 * @version Last modified 2012.09.06
 * @see Literal
 * @see spindle.engine.tdl.LiteralDataStore
 */
public class LiteralComparator implements Comparator<Literal> {

	public static LiteralComparator getLiteralComparator() {
		return new LiteralComparator(true, true, null);
	}

	public static LiteralComparator getStartTimeLiteralComparator() {
		return new LiteralComparator(true, true, new TemporalStartComparator());
	}

	public static LiteralComparator getNoTemporalLiteralComparator() {
		return new LiteralComparator(false, true, null);
	}

	public static LiteralComparator getNoTemporalNoGroundedPredicatesLiteralComparator() {
		return new LiteralComparator(false, false, null);
	}

	public static BasicLiteralComparator getBasicLiteralComparator() {
		return new BasicLiteralComparator();
	}

	private static Comparator<Temporal> DEFAULT_TEMPORAL_COMPARATOR = new TemporalComparator();

	private boolean checkTemporal = true;
	private boolean checkPredicates = true;
	private Comparator<Temporal> temporalComparator = null;

	// private LiteralComparator(boolean checkTemporal, boolean checkPredicates) {
	// this(checkTemporal, checkPredicates, null);
	// }

	private LiteralComparator(boolean checkTemporal, boolean checkPredicates, Comparator<Temporal> temporalComparator) {
		this.checkTemporal = checkTemporal;
		this.checkPredicates = checkPredicates;
		setTemporalComparator(temporalComparator);
	}

	protected void setTemporalComparator(Comparator<Temporal> temporalComparator) {
		this.temporalComparator = temporalComparator;
	}

	@Override
	public int compare(Literal l1, Literal l2) {
		if (l1 == l2) return 0;

		int c = l1.name.compareTo(l2.name);
		if (c != 0) return c;

		if (l1.isNegation != l2.isNegation) return l1.isNegation ? Integer.MAX_VALUE : Integer.MIN_VALUE;

		// same name, negation sign
		// check mode and temporal
		c = l1.mode.compareTo(l2.mode);
		if (c != 0) return c;

		if (checkTemporal) {
			c = null == temporalComparator ? DEFAULT_TEMPORAL_COMPARATOR.compare(l1.temporal, l2.temporal) : temporalComparator.compare(
					l1.temporal, l2.temporal);
			if (c != 0) return c;
		}

		c = comparePredicates(l1, l2);
		if (c != 0) return c;
		// if (checkPredicates) {
		// c = l1.predicates.length - l2.predicates.length;
		// if (c != 0) return c;
		// for (int i = 0; i < l1.predicates.length; i++) {
		// if (!l1.isPredicateGrounded(i) && !l2.isPredicateGrounded(i)) {
		// } else if (l1.isPredicateGrounded(i) && l2.isPredicateGrounded(i)) {
		// c = l1.predicates[i].compareTo(l2.predicates[i]);
		// if (c != 0) return c;
		// } else {
		// return l1.isPredicateGrounded(i) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		// }
		// }
		// } else{
		// c = l1.predicates.length - l2.predicates.length;
		// if (c != 0) return c;
		// }
		//
		return 0;
	}

	protected int comparePredicates(Literal l1, Literal l2) {
		int c = 0;
		if (checkPredicates) {
			c = l1.predicates.length - l2.predicates.length;
			if (c != 0) return c;
			for (int i = 0; i < l1.predicates.length; i++) {
				if (!l1.isPredicateGrounded(i) && !l2.isPredicateGrounded(i)) {
				} else if (l1.isPredicateGrounded(i) && l2.isPredicateGrounded(i)) {
					c = l1.predicates[i].compareTo(l2.predicates[i]);
					if (c != 0) return c;
				} else {
					return l1.isPredicateGrounded(i) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
				}
			}
		} else {
			c = l1.predicates.length - l2.predicates.length;
			if (c != 0) return c;
		}
		return c;
	}

	public boolean isCheckTemporal() {
		return checkTemporal;
	}

	public boolean isCheckPredicates() {
		return checkPredicates;
	}

	public Comparator<Temporal> getTemporalComparator() {
		return null == temporalComparator ? DEFAULT_TEMPORAL_COMPARATOR : temporalComparator;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append(": checkTemporal(").append(checkTemporal).append("), checkPredicates(").append(checkPredicates)
				.append("), Temporal comparator=");
		sb.append(getTemporalComparator().getClass().getName());
		return sb.toString();
	}

	// --------------
	// static classes
	// --------------
	private static class BasicLiteralComparator extends LiteralComparator {
		public BasicLiteralComparator() {
			super(false, false, null);
		}

		@Override
		protected int comparePredicates(Literal l1, Literal l2) {
			return 0;
		}
	}

}
