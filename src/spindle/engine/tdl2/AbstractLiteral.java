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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.Temporal;
import spindle.core.dom.TemporalStartComparator;
import spindle.engine.tdl2.impl.BasicAbstractLiteralConclusionsUpdater;
import spindle.sys.IOConstant;
import spindle.sys.message.ErrorMessage;

public class AbstractLiteral implements Map.Entry<Literal, NavigableSet<Temporal>>, Comparable<AbstractLiteral>, //
		TdlReasoningConstant, IOConstant {

	private static Temporal getTemporal(Literal literal) {
		return getTemporal(literal.getTemporal());
	}

	private static Temporal getTemporal(Temporal temporal) {
		return null == temporal ? PERSISTENT_TEMPORAL : temporal;
	}

	private static final Comparator<AbstractLiteral> comparator = new AbstractLiteralComparator();
	private static final AbstractLiteralConclusionsUpdater DEFAULT_CONCLUSIONS_UPDATER = new BasicAbstractLiteralConclusionsUpdater();

	private AbstractLiteralConclusionsUpdater conclusionsUpdater = DEFAULT_CONCLUSIONS_UPDATER;

	private Literal literal;

	private NavigableSet<Temporal> allTemporals;

	private NavigableMap<Temporal, Set<String>> newTemporalsAdded;
	private NavigableMap<Temporal, Set<String>> unprovedTemporalSegments;

	private NavigableSet<Temporal> provableTemporalSegments;

	private NavigableMap<Temporal, ConclusionType> newConclusionsDerived;
	private NavigableMap<Temporal, ConclusionType> conclusionsDerived;

	private NavigableMap<Temporal, ConclusionType> consolidatedConclusions;
	private NavigableSet<Long> positiveConclusionsStartTime, negativeConclusionsStartTime;

	private Map<Temporal, Set<Literal>> groundedLiterals;

	public AbstractLiteral(Literal literal) {
		this(literal, null);
	}

	public AbstractLiteral(Literal literal, AbstractLiteralConclusionsUpdater conclusionsUpdater) {
		setLiteral(literal);
		setConclusionsUpdater(conclusionsUpdater);

		// initialize all variables
		allTemporals = new TreeSet<Temporal>();
		newTemporalsAdded = new TreeMap<Temporal, Set<String>>();
		unprovedTemporalSegments = new TreeMap<Temporal, Set<String>>();
		// provableTemporalSegments = new TreeSet<Temporal>(TEMPORAL_START_COMPARATOR);
		provableTemporalSegments = new TreeSet<Temporal>(new TemporalStartComparator());
		newConclusionsDerived = new TreeMap<Temporal, ConclusionType>();
		conclusionsDerived = new TreeMap<Temporal, ConclusionType>();
		// consolidatedConclusions = new TreeMap<Temporal, ConclusionType>(TEMPORAL_START_COMPARATOR);
		consolidatedConclusions = new TreeMap<Temporal, ConclusionType>(new TemporalStartComparator());
		positiveConclusionsStartTime = new TreeSet<Long>();
		negativeConclusionsStartTime = new TreeSet<Long>();

		groundedLiterals = new TreeMap<Temporal, Set<Literal>>();
	}

	public void addLiteral(Literal literal, String ruleLabel) throws AbstractLiteralException {
		Temporal literalTemporal = assertLiteral(literal, "addLiteral");
		addTemporalToSet(literalTemporal, ruleLabel, newTemporalsAdded);
		if (literal.hasPredicatesGrounded()) addGroundedLiteral(literal, ruleLabel);
	}

	private void addTemporalToSet(Temporal temporal, String ruleLabel, NavigableMap<Temporal, Set<String>> temporalSet)
			throws AbstractLiteralException {
		if (null == ruleLabel || "".equals(ruleLabel)) throw new AbstractLiteralException(ErrorMessage.RULE_NULL_RULE);

		allTemporals.add(temporal);

		Set<String> ruleLabels = temporalSet.get(temporal);
		if (null == ruleLabels) {
			ruleLabels = new TreeSet<String>();
			temporalSet.put(temporal, ruleLabels);
		}
		ruleLabels.add(ruleLabel);
	}

	private void addGroundedLiteral(Literal literal, String ruleLabel) {
		Temporal literalTemporal = getTemporal(literal);
		Set<Literal> literals = groundedLiterals.get(literalTemporal);
		if (null == literals) {
			literals = new TreeSet<Literal>();
			groundedLiterals.put(literalTemporal, literals);
		}
		literals.add(literal);
	}

	public boolean addConclusionDerived(Literal literal, ConclusionType conclusionType, String ruleLabel) throws AbstractLiteralException {
		Temporal literalTemporal = assertLiteral(literal, "updateConclusionDerived");

		Set<String> ruleLabels = unprovedTemporalSegments.get(literalTemporal);
		if (null == ruleLabels){
			System.err.println("this.literal="+this.literal);
			System.err.println("unprovedTemporalSegments="+unprovedTemporalSegments);
			throw new AbstractLiteralException(ErrorMessage.ABSTRACT_LITERAL_RULE_SET_NOT_FOUND, literal);
		}
		if (!ruleLabels.remove(ruleLabel)) throw new AbstractLiteralException(ErrorMessage.ABSTRACT_LITERAL_RULE_LABEL_NOT_FOUND, literal);
		if (ruleLabels.size() == 0) unprovedTemporalSegments.remove(literalTemporal);

		ConclusionType ct = conclusionsDerived.get(literalTemporal);
		if (null != ct) {
			if (ConclusionType.isSameSide(ct, conclusionType)) return false;
			if (ct.isPositiveConclusion()) return false;
		}
		conclusionsDerived.put(literalTemporal, conclusionType);
		newConclusionsDerived.put(literalTemporal, conclusionType);

		if (conclusionType.isPositiveConclusion()) positiveConclusionsStartTime.add(literalTemporal.getStartTime());
		else negativeConclusionsStartTime.add(literalTemporal.getStartTime());

		return true;
	}

	private void updateProvableTemporalSegments() {
		if (newTemporalsAdded.size() == 0) return;

		NavigableSet<Temporal> s = new TreeSet<Temporal>(allTemporals);
		Temporal.consolidateTemporalSegments(s);

		provableTemporalSegments.clear();
		provableTemporalSegments.addAll(s);

		for (Entry<Temporal, Set<String>> entry : newTemporalsAdded.entrySet()) {
			Temporal temporal = entry.getKey();
			Set<String> ruleLabels = unprovedTemporalSegments.get(temporal);
			if (null == ruleLabels) {
				unprovedTemporalSegments.put(temporal, entry.getValue());
			} else {
				ruleLabels.addAll(entry.getValue());
			}
		}

		newTemporalsAdded.clear();
	}

	public boolean isProvable(Literal literal) throws AbstractLiteralException {
		return isProvable(assertLiteral(literal, "isProvable"));
	}

	public boolean isProvable(Temporal temporal) {
		// Temporal interval=getProvableTemporalSegment(temporal);
		// if (null!=interval){
		// System.out.println("interval="+interval);
		// try {
		// System.out.println(getTemporal(temporal)+": contains set="+
		// Temporal.extractContains(getTemporal(temporal), unprovedTemporalSegments.keySet(), interval, true));
		// } catch (TemporalException e) {
		// e.printStackTrace();
		// }
		// // System.out.println("  succeeding temporals="+ Temporal.extractTemporals(getTemporal(temporal),
		// // unprovedTemporalSegments.keySet(), interval, true, true, false));
		// }
		return null != getProvableTemporalSegment(temporal);
	}

	private Temporal assertLiteral(Literal literal, String caller) throws AbstractLiteralException {
		if (!this.literal.equals(literal, false, false))
		// if (!this.literal.equalsWithNoTemporal(literal))
			throw new AbstractLiteralException(ErrorMessage.ABSTRACT_LITERAL_LITERAL_MISMATCH, this.literal, literal, caller);
		return getTemporal(literal);
	}

	public boolean hasSameStartUnprovedLiteral(Literal literal) throws AbstractLiteralException {
		long temporalStart = assertLiteral(literal, "assertSameLiteral").getStartTime();
		for (Temporal t : unprovedTemporalSegments.keySet()) {
			long tStart = t.getStartTime();
			if (tStart < temporalStart) continue;
			if (tStart > temporalStart) return false;
			if (tStart == temporalStart) return true;
		}
		return false;
	}

	public boolean hasPositiveConclusionDerivedStart(long startTime) {
		return positiveConclusionsStartTime.contains(startTime);
	}

	public boolean hasNegativeConclusionDerivedStart(long startTime) {
		return negativeConclusionsStartTime.contains(startTime);
	}

	public NavigableSet<Temporal> getUnprovedPreceedingTemporals(Temporal temporal) {
		Temporal literalTemporal = getTemporal(temporal);
		Temporal interval = getProvableTemporalSegment(literalTemporal);
		return Temporal.extractTemporalsByStartTime(literalTemporal, unprovedTemporalSegments.keySet(), interval, true, true, false);
	}

	public boolean hasUnprovedPreceedingTemporals(Temporal temporal) {
		return getUnprovedPreceedingTemporals(temporal).size() > 0;
	}

	public NavigableSet<Temporal> getUnprovedSucceedingTemporals(Temporal temporal) {
		Temporal literalTemporal = getTemporal(temporal);
		Temporal interval = getProvableTemporalSegment(literalTemporal);
		return Temporal.extractTemporalsByStartTime(literalTemporal, unprovedTemporalSegments.keySet(), interval, false, true, true);
	}

	public boolean hasUnprovedSucceedingTemporals(Temporal temporal) {
		return getUnprovedSucceedingTemporals(temporal).size() > 0;
	}

	protected NavigableSet<Temporal> getUnprovedOverlappedTemporals(Temporal temporal) {
		Temporal literalTemporal = getTemporal(temporal);
		Temporal interval = getProvableTemporalSegment(literalTemporal);
		return Temporal.extractOverlap(literalTemporal, unprovedTemporalSegments.keySet(), interval, true);
	}

	protected boolean hasUnprovedOverlappedTemporals(Temporal temporal) {
		return getUnprovedOverlappedTemporals(temporal).size() > 0;
	}

	public NavigableSet<Temporal> getUnprovedTemporalsInInterval(Temporal temporal) {
		Temporal literalTemporal = getTemporal(temporal);
		Temporal interval = getProvableTemporalSegment(literalTemporal);
		return Temporal.extractTemporalsStartWithinTemporal(literalTemporal, unprovedTemporalSegments.keySet(), interval, true);
	}

	public boolean hasUnprovedTemporalInInterval(Temporal temporal) {
		return getUnprovedTemporalsInInterval(temporal).size() > 0;
	}

	// ==================================================
	// getters, setters and inherited methods
	// ==================================================

	/**
	 * Return the non-temporalized literal of the set.
	 */
	public Literal getLiteral() {
		return literal;
	}

	protected void setLiteral(Literal literal) {
		if (null == literal) throw new IllegalArgumentException("literal is null");
		// this.literal = literal.cloneWithNoTemporal();
		//this.literal = literal.getNoGroundedLiteralClone();
		this.literal=literal.cloneWithNoGroundedPredicates();
		this.literal.removeTemporal();
	}

	public Map<Temporal, Set<Literal>> getGroundedLiterals() {
		return groundedLiterals;
	}

	public boolean hasGroundedLiterals() {
		return groundedLiterals.size() > 0;
	}

	public boolean hasGroundedLiterals(Temporal temporal) {
		return groundedLiterals.containsKey(temporal);
	}

	// public Set<String>getGroundedLiteralsRuleLabels(Literal literal){
	// return groundedLiterals.get(literal);
	// }

	/**
	 * Return the non-temporalized literal of the set, which is the same as {@link #getLiteral}.
	 */
	@Override
	public Literal getKey() {
		return getLiteral();
	}

	/**
	 * Return the set of provable temporal segments for the literal.
	 */
	public NavigableSet<Temporal> getProvableTemporalSegments() {
		updateProvableTemporalSegments();
		return provableTemporalSegments;
	}

	public Temporal getProvableTemporalSegment(Temporal temporal) {
		updateProvableTemporalSegments();
		Temporal t = getTemporal(temporal);
		Temporal interval = provableTemporalSegments.floor(t);
		return null == interval ? null : (interval.includes(t) ? interval : null);
	}

	@Override
	public NavigableSet<Temporal> getValue() {
		// return getAllTemporalsToProve();
		return getProvableTemporalSegments();
	}

	@Override
	public NavigableSet<Temporal> setValue(NavigableSet<Temporal> value) {
		throw new IllegalArgumentException("method setValue(NavigableSet) not-yet implemented!");
	}

	public void setConclusionsUpdater(AbstractLiteralConclusionsUpdater conclusionsUpdater) {
		this.conclusionsUpdater = null == conclusionsUpdater ? DEFAULT_CONCLUSIONS_UPDATER : conclusionsUpdater;
		// if (PRINT_DETAILS) System.out.println("conclusionsUpdater=" +
		// this.conclusionsUpdater.getClass().getCanonicalName());
	}

	public NavigableSet<Temporal> getAllTemporalsToProve() {
		return allTemporals;
	}

	public boolean containsTemporalsToProve(Temporal temporal) {
		return allTemporals.contains(temporal);
	}

	public Set<Temporal> getUnprovedTemporals() {
		return unprovedTemporalSegments.keySet();
	}

	public ConclusionType getConclusionDerived(Temporal temporal) throws AbstractLiteralException {
		Temporal t = getTemporal(temporal);
		if (!allTemporals.contains(t)) throw new AbstractLiteralException("temporal " + t + " is not a provable segment.");
		return conclusionsDerived.get(temporal);
	}

	public Map<Temporal, ConclusionType> getConclusionsDerived() {
		return conclusionsDerived;
	}

	private void updateConsolidatedConclusions() throws AbstractLiteralException {
		if (!hasNewConclusionsDerived()) return;
		// if (newConclusionsDerived.size() == 0) return;

		consolidatedConclusions.clear();
		if (conclusionsDerived.size() == 1) {
			consolidatedConclusions.putAll(conclusionsDerived);
		} else {
			try {
				consolidatedConclusions.putAll(conclusionsUpdater.consolidateConclusions(conclusionsDerived));
			} catch (AbstractLiteralUpdaterException e) {
				throw new AbstractLiteralException(e);
			}
		}

		newConclusionsDerived.clear();
	}

	public boolean hasNewConclusionsDerived() {
		return newConclusionsDerived.size() > 0;
	}

	public NavigableMap<Temporal, ConclusionType> getConsolidatedConclusions() throws AbstractLiteralException {
		updateConsolidatedConclusions();
		return consolidatedConclusions;
	}

	public ConclusionType getConsolidatedConclusion(Temporal temporal) throws AbstractLiteralException {
		updateConsolidatedConclusions();
		Temporal literalTemporal = getTemporal(temporal);

		if (PRINT_DETAILS) System.out.println(literal.toString() + temporal + ":");

		Temporal provableTemporalSegment = getProvableTemporalSegment(literalTemporal);
		if (PRINT_DETAILS) System.out.println("  provableTemporalSegment=" + provableTemporalSegment);
		if (null == provableTemporalSegment) return null;

		Entry<Temporal, ConclusionType> conc = consolidatedConclusions.floorEntry(literalTemporal);
		if (PRINT_DETAILS) System.out.println("  conc=" + conc);
		if (null == conc) return null;

		Temporal concTemporal = conc.getKey();
		if (!literalTemporal.overlap(concTemporal)) return null;
		ConclusionType concConclusionType = conc.getValue();

		NavigableMap<Temporal, ConclusionType> concs = consolidatedConclusions.subMap(conc.getKey(), true,
				literalTemporal.getEndTimeAsInstance(), false);
		if (PRINT_DETAILS) System.out.println("  concs=" + concs);

		if (concs.size() == 1 && concTemporal.includes(literalTemporal) && concConclusionType.isPositiveConclusion())
			return concConclusionType;

		// NavigableSet<Temporal> overlappedUnprovedTemporals = Temporal.extractOverlap(literalTemporal,
		// unprovedTemporalSegments.keySet(),
		// provableTemporalSegment, true);
		NavigableSet<Temporal> overlappedUnprovedTemporals = getUnprovedOverlappedTemporals(literalTemporal);
		if (PRINT_DETAILS) System.out.println("  overlappedUnprovedTemporals=" + overlappedUnprovedTemporals);

		// return -ve conclusion if there is no unproved temporals overlapped with the input temporal,
		// or null if there are still some unproved temporals waiting to be proved.
		if (overlappedUnprovedTemporals.size() == 0) return ConclusionType.getNegativeConclusionType(concConclusionType);

		// return null if it is still possible to prove the time segment positively using the unproved overlapped
		// temporals and the positive conclusions derived;
		// or return -ve conclusion, otherwise.
		for (Entry<Temporal, ConclusionType> entry : concs.entrySet()) {
			if (entry.getValue().isPositiveConclusion()) {
				if (PRINT_DETAILS) System.out.println("    add " + entry.getKey() + " to the overlapped unproved temporals set");
				overlappedUnprovedTemporals.add(entry.getKey());
			}
		}

		Temporal.consolidateTemporalSegments(overlappedUnprovedTemporals);
		for (Temporal overlappedTemporal : overlappedUnprovedTemporals) {
			if (PRINT_DETAILS) System.out.printf("    check unproved overlapped temporal: %s.contains(%s)=%b\n", //
					overlappedTemporal, literalTemporal, overlappedTemporal.includes(literalTemporal));
			if (overlappedTemporal.includes(literalTemporal)) return null;
		}

		return ConclusionType.getNegativeConclusionType(concConclusionType);
	}

	@Override
	public int compareTo(AbstractLiteral literalSet) {
		return comparator.compare(this, literalSet);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractLiteral)) return false;
		return 0 == compareTo((AbstractLiteral) obj);
	}

	private String generateTemporalsString(Collection<Temporal> temporals) {
		if (temporals.size() == 0) return "";
		StringBuilder sb = new StringBuilder();
		for (Temporal temporal : temporals) {
			if (sb.length() > 0) sb.append(" ");
			sb.append(temporal);
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(literal);
		if (allTemporals.size() > 0) {
			sb.append(": provable in: ").append(generateTemporalsString(getProvableTemporalSegments()));
			sb.append(NEW_LINE_INDENTATOR).append("all temporals: ").append(generateTemporalsString(allTemporals));
		}
		if (unprovedTemporalSegments.size() > 0) {
			sb.append(NEW_LINE_INDENTATOR).append("unproved temporals:");
			// for (Entry<Temporal, Set<String>> entry : unprovedTemporalSegments.entrySet()) {
			if (PRINT_DETAILS) {
				for (Entry<Temporal, Set<String>> entry : unprovedTemporalSegments.entrySet()) {
					sb.append(NEW_LINE_INDENTATOR).append(INDENTATOR).append(entry.getKey()).append(" - ").append(entry.getValue());
				}
			} else {
				for (Entry<Temporal, Set<String>> entry : unprovedTemporalSegments.entrySet()) {
					sb.append(" ").append(entry.getKey());
				}
			}
			// }
		}
		if (conclusionsDerived.size() > 0) {
			sb.append(NEW_LINE_INDENTATOR).append("conclusions derived:");
			for (Entry<Temporal, ConclusionType> entry : conclusionsDerived.entrySet()) {
				sb.append(NEW_LINE_INDENTATOR).append(INDENTATOR).append(entry.getKey()).append(" - ").append(entry.getValue());
			}
			sb.append(NEW_LINE_INDENTATOR).append("consolidated conclusions:");
			try {
				for (Entry<Temporal, ConclusionType> entry : getConsolidatedConclusions().entrySet()) {
					sb.append(NEW_LINE_INDENTATOR).append(INDENTATOR).append(entry.getKey()).append(" - ").append(entry.getValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (positiveConclusionsStartTime.size() > 0) {
				int c = 0;
				sb.append(NEW_LINE_INDENTATOR).append("positive conclusions start time: ");
				for (Long startTime : positiveConclusionsStartTime) {
					if (c++ > 0) sb.append(",");
					sb.append(startTime);
				}
			}
			if (negativeConclusionsStartTime.size() > 0) {
				int c = 0;
				sb.append(NEW_LINE_INDENTATOR).append("negative conclusions start time: ");
				for (Long startTime : negativeConclusionsStartTime) {
					if (c++ > 0) sb.append(",");
					sb.append(startTime);
				}
			}
		}

		// System.out.println("groundedLiterals.size="+groundedLiterals.size());
		if (groundedLiterals.size() > 0) {
			sb.append(NEW_LINE_INDENTATOR).append("grounded literals:");
			for (Entry<Temporal, Set<Literal>> temporalEntry : groundedLiterals.entrySet()) {
				for (Literal groundedLiteral : temporalEntry.getValue()) {
					sb.append(NEW_LINE_INDENTATOR).append(INDENTATOR).append(groundedLiteral);
				}
			}
		}

		// if (groundedLiterals.size()>0){
		// sb.append(NEW_LINE_INDENTATOR).append("grounded literals:");
		// for (Entry<Literal,Set<String>>entry:groundedLiterals.entrySet()){
		// sb.append(NEW_LINE_INDENTATOR).append(INDENTATOR).append(entry.getKey()).append(": ");
		// int c=0;
		// for (String ruleLabel:entry.getValue()){
		// sb.append(c==0?" ":",").append(ruleLabel);
		// c++;
		// }
		// }
		// }

		return sb.toString();
	}

	// ==================================================
	// static class
	// ==================================================

	public static class AbstractLiteralComparator implements Comparator<AbstractLiteral> {

		private boolean compareLiteralOnly = false;

		public AbstractLiteralComparator() {
			this(true);
		}

		public AbstractLiteralComparator(boolean compareLiteralOnly) {
			this.compareLiteralOnly = compareLiteralOnly;
		}

		@Override
		public int compare(AbstractLiteral s1, AbstractLiteral s2) {
			if (s1 == s2) return 0;

			int c = s1.literal.compareTo(s2.literal);
			if (c != 0) return c;

			if (!compareLiteralOnly) {
				s1.updateProvableTemporalSegments();
				s2.updateProvableTemporalSegments();

				c = s1.provableTemporalSegments.size() - s2.provableTemporalSegments.size();
				if (c != 0) return c;
				Iterator<Temporal> it1 = s1.provableTemporalSegments.iterator();
				Iterator<Temporal> it2 = s2.provableTemporalSegments.iterator();
				while (it1.hasNext()) {
					Temporal t1 = it1.next();
					Temporal t2 = it2.next();
					c = t1.compareTo(t2);
					if (c != 0) return c;
				}

				c = s1.allTemporals.size() - s2.allTemporals.size();
				if (c != 0) return c;
				it1 = s1.allTemporals.iterator();
				it2 = s2.allTemporals.iterator();
				while (it1.hasNext()) {
					Temporal t1 = it1.next();
					Temporal t2 = it2.next();
					c = t1.compareTo(t2);
					if (c != 0) return c;
				}
			}
			return 0;
		}
	}

	// ==================================================
	// abstract methods
	// ==================================================
}
