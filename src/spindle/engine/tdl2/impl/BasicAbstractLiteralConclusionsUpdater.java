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
package spindle.engine.tdl2.impl;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import spindle.core.dom.ConclusionType;
import spindle.core.dom.Temporal;
import spindle.engine.tdl2.AbstractLiteralConclusionsUpdater;
import spindle.engine.tdl2.AbstractLiteralUpdaterException;

public class BasicAbstractLiteralConclusionsUpdater implements AbstractLiteralConclusionsUpdater {

	@Override
	public NavigableMap<Temporal, ConclusionType> consolidateConclusions(NavigableMap<Temporal, ConclusionType> conclusionsDerived)
			throws AbstractLiteralUpdaterException {
		NavigableSet<Temporal> pos = new TreeSet<Temporal>();
		NavigableSet<Temporal> neg = new TreeSet<Temporal>();

		Temporal currTemporal = null, lastPosTemporal = null, lastNegTemporal = null;
		ConclusionType currConclusionType = null, posConclusionType = null, negConclusionType = null;

		// join conclusion with overlapped temporals
		try {
			for (Entry<Temporal, ConclusionType> entry : conclusionsDerived.entrySet()) {
				currTemporal = entry.getKey();
				currConclusionType = entry.getValue();
				if (currConclusionType.isPositiveConclusion()) {
					if (null == lastPosTemporal) {
						lastPosTemporal = currTemporal;
						posConclusionType = currConclusionType;
					} else if (lastPosTemporal.overlapOrMeet(currTemporal)) {
						lastPosTemporal = lastPosTemporal.union(currTemporal);
					} else {
						pos.add(lastPosTemporal);
						lastPosTemporal = currTemporal;
					}
				} else {
					if (null == lastNegTemporal) {
						lastNegTemporal = currTemporal;
						negConclusionType = currConclusionType;
					} else if (lastNegTemporal.overlapOrMeet(currTemporal)) {
						lastNegTemporal = lastNegTemporal.union(currTemporal);
					} else {
						neg.add(lastNegTemporal);
						lastNegTemporal = currTemporal;
					}
				}
			}
			if (null != lastPosTemporal) pos.add(lastPosTemporal);
			if (null != lastNegTemporal) neg.add(lastNegTemporal);
		} catch (Exception e) {
			throw new AbstractLiteralUpdaterException(e);
		}

		// consolidate conclusions with different conclusion types
		NavigableMap<Temporal, ConclusionType> consolidatedConclusions = new TreeMap<Temporal, ConclusionType>();

		Iterator<Temporal> posIterator = pos.iterator();
		Iterator<Temporal> negIterator = neg.iterator();

		lastPosTemporal = posIterator.hasNext() ? posIterator.next() : null;
		lastNegTemporal = negIterator.hasNext() ? negIterator.next() : null;

		while (null != lastPosTemporal && null != lastNegTemporal) {
			// if (PRINT_DETAILS) {
			// System.out.println("lastPosTemporal=" + lastPosTemporal);
			// System.out.println("lastNegTemporal=" + lastNegTemporal);
			// }

			if (lastPosTemporal.overlap(lastNegTemporal)) {
				// if (PRINT_DETAILS) System.out.println("01");
				if (lastPosTemporal.startOnOrBefore(lastNegTemporal)) {
					// if (PRINT_DETAILS) System.out.println("02");
					consolidatedConclusions.put(lastPosTemporal, posConclusionType);
					if (lastPosTemporal.endOnOrAfter(lastNegTemporal)) {
						// if (PRINT_DETAILS) System.out.println("03");
						// skip current -ve conclusion
						lastNegTemporal = negIterator.hasNext() ? negIterator.next() : null;
					} else {
						// if (PRINT_DETAILS) System.out.println("04");
						lastNegTemporal = new Temporal(lastPosTemporal.getEndTime(), lastNegTemporal.getEndTime());
						lastPosTemporal = posIterator.hasNext() ? posIterator.next() : null;
					}
				} else {
					// if (PRINT_DETAILS) System.out.println("05");
					consolidatedConclusions.put(new Temporal(lastNegTemporal.getStartTime(), lastPosTemporal.getStartTime()),
							negConclusionType);
					if (lastPosTemporal.endOnOrAfter(lastNegTemporal)) {
						// if (PRINT_DETAILS) System.out.println("06");
						lastNegTemporal = negIterator.hasNext() ? negIterator.next() : null;
					} else {
						// if (PRINT_DETAILS) System.out.println("07");
						consolidatedConclusions.put(lastPosTemporal, posConclusionType);
						lastNegTemporal = new Temporal(lastPosTemporal.getStartTime(), lastNegTemporal.getEndTime());
					}
				}
			} else {
				// if (PRINT_DETAILS) System.out.println("11");
				if (lastPosTemporal.startBefore(lastNegTemporal)) {
					// if (PRINT_DETAILS) System.out.println("12");
					consolidatedConclusions.put(lastPosTemporal, posConclusionType);
					lastPosTemporal = posIterator.hasNext() ? posIterator.next() : null;
				} else {
					// if (PRINT_DETAILS) System.out.println("13");
					consolidatedConclusions.put(lastNegTemporal, negConclusionType);
					lastNegTemporal = negIterator.hasNext() ? negIterator.next() : null;
				}
			}
		}

		if (null != lastPosTemporal) {
			consolidatedConclusions.put(lastPosTemporal, posConclusionType);
		} else if (null != lastNegTemporal) {
			consolidatedConclusions.put(lastNegTemporal, negConclusionType);
		}

		// if (PRINT_DETAILS) {
		// System.out.println("consolidatedConclusions.lastPosTemporal=" + lastPosTemporal);
		// System.out.println("consolidatedConclusions.lastNegTemporal=" + lastNegTemporal);
		// for (Entry<Temporal, ConclusionType> entry : consolidatedConclusions.entrySet()) {
		// System.out.println("consolidatedConclusions=" + entry);
		// }
		// }

		while (posIterator.hasNext()) {
			consolidatedConclusions.put(posIterator.next(), posConclusionType);
		}
		while (negIterator.hasNext()) {
			consolidatedConclusions.put(negIterator.next(), negConclusionType);
		}

		return consolidatedConclusions;
	}
}
