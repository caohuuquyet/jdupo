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

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import spindle.sys.AppConst;
import spindle.sys.AppFeatureConst;
import spindle.sys.Messages;
import spindle.sys.message.ErrorMessage;

/**
 * DOM for representing the temporal information in a literal/rule.
 * Note that a temporal is a value pairs [s,e) representing an time interval where <i>s</i> (inclusive) and <i>e</i>
 * (non-inclusive)
 * are the start and time time of the interval, respectively.
 * A time instance in this case means that <i>s=e</i>, i.e., the start time of the interval is equal to its end time.
 * 
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory
 * @since version 2.2.1
 * @version Last modified 2012.10.11
 * @version 2012.07.11
 */
public class Temporal implements Comparable<Object>, Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	private static final Comparator<? super Temporal> TEMPORAL_COMPARATOR = new TemporalComparator();
	public static final Temporal PERSISTENT_TEMPORAL = new Temporal();

	private static final String TEMPORAL_START = String.valueOf(DomConst.Literal.TIMESTAMP_START);
	private static final String TEMPORAL_END = String.valueOf(DomConst.Literal.TIMESTAMP_END);
	private static final String TEMPORAL_SEPARATOR = String.valueOf(DomConst.Literal.LITERAL_SEPARATOR);
	private static final String TEMPORAL_POSITIVE_INFINITY = DomConst.Literal.TEMPORAL_POSITIVE_INFINITY;
	private static final String TEMPORAL_NEGATIVE_INFINITY = DomConst.Literal.TEMPORAL_NEGATIVE_INFINITY;

	public static Temporal getTemporal(Temporal temporal) {
		return null == temporal ? PERSISTENT_TEMPORAL : temporal;
	}

	public static Temporal getTemporal(Object obj) throws TemporalException {
		if (null == obj) throw new TemporalException(ErrorMessage.TEMPORAL_NULL_TEMPORAL);
		if (obj instanceof Temporal) return (Temporal) obj;
		if (obj instanceof Literal) return getTemporal(((Literal) obj).getTemporal());
		throw new TemporalException(ErrorMessage.TEMPORAL_TEMPORAL_SEGMENTS_INPUT_ERROR, obj );
	}

	public static Temporal getTemporalInstance(long t) {
		return new Temporal(t, t);
	}

	public static NavigableSet<Temporal> getTemporalSegmentsFromSet(Collection<Temporal> objects) throws TemporalException {
		TreeSet<Long> timeInstances = new TreeSet<Long>();
		TreeSet<Long> timeStamps = new TreeSet<Long>();

		Temporal temporal = null;
		for (Object obj : objects) {
			if (null == obj) continue;
			temporal = getTemporal(obj);
//			if (null == temporal) continue;
			if (temporal.isTimeInstance()) {
				timeInstances.add(temporal.startTime);
				timeStamps.add(temporal.startTime);
			} else {
				timeStamps.add(temporal.startTime);
				timeStamps.add(temporal.endTime);
			}
		}
		return generateTemporalSegments(timeStamps, timeInstances);
	}

	private static NavigableSet<Temporal> generateTemporalSegments(Collection<Long> timestamps, Collection<Long> instances) {
		NavigableSet<Temporal> segments = new TreeSet<Temporal>(new TemporalStartComparator());
		if (null == timestamps) return segments;
		if (null == instances) instances = new TreeSet<Long>();
		int c = 0;
		long timeIntervalStart = 0;
		for (Long timestamp : timestamps) {
			if (c++ > 0) segments.add(new Temporal(timeIntervalStart, timestamp));
			if (instances.contains(timestamp)) segments.add(new Temporal(timestamp, timestamp));
			timeIntervalStart = timestamp;
		}
		return segments;
	}

	public static Collection<Temporal> consolidateTemporalSegments(Collection<Temporal> temporals) {
		if (null == temporals || temporals.size() < 2) return temporals;

		Set<Temporal> origTemporals = new TreeSet<Temporal>(temporals);
		temporals.clear();

		if (origTemporals.contains(PERSISTENT_TEMPORAL)) {
			temporals.add(PERSISTENT_TEMPORAL.clone());
			return temporals;
		}

		Iterator<Temporal> it = origTemporals.iterator();
		Temporal lastTemporal = it.next();
		Temporal temporal = null;

		try {
			while ((temporal = it.next()) != null) {
				if (lastTemporal.overlapOrMeet(temporal)) {
					lastTemporal = lastTemporal.join(temporal);
				} else {
					temporals.add(lastTemporal);
					lastTemporal = temporal;
				}
			}
		} catch (Exception e) {
		}

		temporals.add(lastTemporal);

		return temporals;
	}

	private static NavigableSet<Temporal> getNavigableTemporalSet(Collection<Temporal> temporals) {
		if (temporals instanceof NavigableSet) return (NavigableSet<Temporal>) temporals;
		return new TreeSet<Temporal>(temporals);
	}

	public static NavigableSet<Temporal> extractIncludes(Temporal temporal, Collection<Temporal> temporals, //
			Temporal interval, boolean includeSameStart) {
		if (null == temporal || null == temporals) return null;

		NavigableSet<Temporal> extractedTemporals = new TreeSet<Temporal>();

		if (null == interval) interval = PERSISTENT_TEMPORAL;
		if (!temporal.overlap(interval)) return extractedTemporals;

		long temporalStart = temporal.startTime;
		boolean isTemporalInstance = temporal.isTimeInstance();

		long endTime = temporal.endTime < interval.endTime ? temporal.endTime : interval.endTime;

		for (Temporal t : getNavigableTemporalSet(temporals)) {
			long tStart = t.startTime;
			if (endTime < tStart) break;
			if (!t.during(interval) || !temporal.includes(t)) continue;
			if (tStart == temporalStart) {
				if (isTemporalInstance || includeSameStart) extractedTemporals.add(t);
			} else extractedTemporals.add(t);
		}

		return extractedTemporals;
	}

	/**
	 * Retrieve the set of temporals that are within a temporal interval and contains the temporal specified.
	 * 
	 * @param temporal
	 * @param temporals
	 * @param interval
	 * @param includeSameStart
	 * @return
	 * @throws TemporalException
	 */
	public static NavigableSet<Temporal> extractDuring(Temporal temporal, Collection<Temporal> temporals, //
			Temporal interval, boolean includeSameStart) {
		if (null == temporal || null == temporals) return null;

		NavigableSet<Temporal> extractedTemporals = new TreeSet<Temporal>();

		if (null == interval) interval = PERSISTENT_TEMPORAL;
		// if (!temporal.overlap(interval)) return extractedTemporals;
		if (!temporal.during(interval)) return extractedTemporals;

		long temporalStart = temporal.startTime;
		boolean isTemporalInstance = temporal.isTimeInstance();

		long endTime = temporal.endTime < interval.endTime ? temporal.endTime : interval.endTime;

		for (Temporal t : getNavigableTemporalSet(temporals)) {
			long tStart = t.startTime;
			if (endTime < tStart) break;
			if (!t.during(interval) || !temporal.during(t)) continue;
			if (tStart == temporalStart) {
				if (isTemporalInstance || includeSameStart) extractedTemporals.add(t);
			} else extractedTemporals.add(t);
		}

		return extractedTemporals;
	}

	/**
	 * Retrieve the set of temporals that are started within a specified interval and overlapped the temporal (partly or
	 * whole)
	 * indicated.
	 * 
	 * @param temporal
	 * @param temporals
	 * @param interval
	 * @param includeSameStart
	 * @return
	 * @throws TemporalException
	 */
	public static NavigableSet<Temporal> extractOverlap(Temporal temporal, Collection<Temporal> temporals, //
			Temporal interval, boolean includeSameStart) {
		if (null == temporal || null == temporals) return null;

		NavigableSet<Temporal> extractedTemporals = new TreeSet<Temporal>();

		if (null == interval) interval = PERSISTENT_TEMPORAL;
		if (!temporal.overlap(interval)) return extractedTemporals;

		long temporalStart = temporal.startTime;
		boolean isTemporalInstance = temporal.isTimeInstance();

		long endTime = temporal.endTime < interval.endTime ? temporal.endTime : interval.endTime;

		for (Temporal t : getNavigableTemporalSet(temporals)) {
			long tStart = t.startTime;
			if (endTime < tStart) break;
			if (!t.during(interval) || !t.overlap(temporal)) continue;
			if (tStart == temporalStart) {
				if (isTemporalInstance || includeSameStart) extractedTemporals.add(t);
			} else extractedTemporals.add(t);
		}

		return extractedTemporals;
	}

	public static NavigableSet<Temporal> extractTemporalsStartWithinTemporal(Temporal temporal, Collection<Temporal> temporals,//
			Temporal interval, boolean includeSameStart) {
		if (null == temporal || null == temporals) return null;

		NavigableSet<Temporal> extractedTemporals = new TreeSet<Temporal>();

		if (null == interval) interval = PERSISTENT_TEMPORAL;
		if (!temporal.overlap(interval)) return extractedTemporals;

		long temporalStart = temporal.startTime;
		long temporalEnd = temporal.endTime;
		boolean isTemporalInstance = temporal.isTimeInstance();

		long endTime = temporalEnd < interval.endTime ? temporalEnd : interval.endTime;

		for (Temporal t : getNavigableTemporalSet(temporals)) {
			long tStart = t.startTime;
			if (endTime < tStart) break;
			if (tStart < temporalStart || !t.during(interval)) continue;
			if (!isTemporalInstance && temporalEnd == tStart) continue;
			if (tStart == temporalStart) {
				if (isTemporalInstance || includeSameStart) extractedTemporals.add(t);
			} else extractedTemporals.add(t);
		}

		return extractedTemporals;
	}

	/**
	 * Extract the set of temporals that are started before, on, or after a particular temporal within a specified time
	 * interval.
	 * 
	 * @param temporal
	 * @param temporals
	 * @param interval
	 * @param includePreceeding
	 * @param includeSameStart
	 * @param includeSucceeding
	 * @return
	 */
	public static NavigableSet<Temporal> extractTemporalsByStartTime(Temporal temporal, Collection<Temporal> temporals, //
			Temporal interval,//
			boolean includePreceeding, boolean includeSameStart, boolean includeSucceeding) {
		if (null == temporal || null == temporals) return null;

		NavigableSet<Temporal> extractedTemporals = new TreeSet<Temporal>();

		if (null == interval) interval = PERSISTENT_TEMPORAL;
		if (!temporal.overlap(interval)) return extractedTemporals;

		long temporalStart = temporal.startTime;
		long endTime = interval.endTime;

		if (includePreceeding && includeSameStart && includeSucceeding) {
			if (PERSISTENT_TEMPORAL.equals(interval)) {
				extractedTemporals.addAll(temporals);
			} else {
				for (Temporal t : getNavigableTemporalSet(temporals)) {
					long tStart = t.startTime;
					if (tStart > endTime) break;
					if (!t.during(interval)) continue;
					extractedTemporals.add(t);
				}
			}
		} else if (!includePreceeding && includeSameStart && !includeSucceeding) {
			for (Temporal t : getNavigableTemporalSet(temporals)) {
				long tStart = t.startTime;
				if (tStart > temporalStart) break;
				if (tStart < temporalStart || !t.during(interval)) continue;
				extractedTemporals.add(t);
			}
		} else {
			for (Temporal t : getNavigableTemporalSet(temporals)) {
				long tStart = t.startTime;
				if (tStart > endTime) break;
				if (!t.during(interval)) continue;
				if (tStart == temporalStart) {
					if (includeSameStart) extractedTemporals.add(t);
				} else if (tStart < temporalStart) {
					if (includePreceeding) extractedTemporals.add(t);
				} else {
					if (includeSucceeding) extractedTemporals.add(t);
				}
			}
		}
		return extractedTemporals;
	}

	protected long startTime, endTime;

	public Temporal() {
		this(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public Temporal(long startTime) {
		this(startTime, Long.MAX_VALUE);
	}

	public Temporal(long startTime, long endTime) {
		setStartTime(startTime);
		setEndTime(endTime);
	}

	public Temporal(Temporal temporal) {
		this(temporal.startTime, temporal.endTime);
	}

	public long getStartTime() {
		return startTime;
	}

	public Temporal getStartTimeAsInstance() {
		return new Temporal(startTime, startTime);
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void removeStartTime() {
		startTime = Long.MIN_VALUE;
	}

	// public void startTimeIncrement() {
	// if (startTime < Long.MAX_VALUE) startTime++;
	// }
	//
	// public void startTimeDecrement() {
	// if (startTime > Long.MIN_VALUE) startTime--;
	// }

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		if (startTime > endTime) throw new IllegalArgumentException(Messages.getErrorMessage(ErrorMessage.TEMPORAL_STARTTIME_ENDTIME));
		if (startTime == endTime && AppFeatureConst.isIntervalBasedTemporal) this.endTime = Long.MAX_VALUE == startTime ? Long.MAX_VALUE
				: startTime + 1;
		else this.endTime = endTime;
	}

	public void removeEndTime() {
		endTime = Long.MAX_VALUE;
	}

	public Temporal getEndTimeAsInstance() {
		return new Temporal(endTime, endTime);
	}

	// public void endTimeIncrement() {
	// if (endTime < Long.MAX_VALUE) endTime++;
	// }
	//
	// public void endTimeDecrement() {
	// if (endTime > Long.MIN_VALUE) endTime--;
	// }

	public Temporal clone() {
		return new Temporal(this);
	}

	/**
	 * Check if this temporal represents a time instance (i.e., start time equals end time.)
	 * 
	 * @return true if it represents an instance of time; false otherwise.
	 */
	public boolean isTimeInstance() {
		return startTime == endTime;
	}

	public boolean startBefore(Temporal temporal) {
		return startTime < temporal.startTime;
	}

	public boolean startOnOrBefore(Temporal temporal) {
		return startTime <= temporal.startTime;
	}

	public boolean sameStart(Temporal temporal) {
		return startTime == temporal.startTime;
	}

	public boolean startOnOrAfter(Temporal temporal) {
		return temporal.startTime <= startTime;
	}

	public boolean startAfter(Temporal temporal) {
		return temporal.startTime < startTime;
	}

	public boolean endBefore(Temporal temporal) {
		return endTime < temporal.endTime;
	}

	public boolean endOnOrBefore(Temporal temporal) {
		return endTime <= temporal.endTime;
	}

	/**
	 * Determine if two temporals have the same end time.
	 * Two temporals are considered having the same end time if:
	 * (1) they are having the same time end value, and
	 * (2) either both of them are time instances or both of them are time intervals.
	 * 
	 * @param temporal Temporal to be checked.
	 * @return true if the above conditions are satisfied; false otherwise.
	 */
	public boolean sameEnd(Temporal temporal) {
		if (endTime != temporal.endTime) return false;
		boolean ins = isTimeInstance();
		boolean tins = temporal.isTimeInstance();
		if ((ins && tins) || (!ins && !tins)) return true;
		return false;
		// return sameEndTime(temporal.endTime);
		// return endTime == temporal.endTime;
	}

	public boolean endOnOrAfter(Temporal temporal) {
		return temporal.endTime <= endTime;
	}

	public boolean endAfter(Temporal temporal) {
		return temporal.endTime < endTime;
	}

	/**
	 * check if the temporal object is empty.
	 * 
	 * @return true if there is no temporal information; and false otherwise.
	 */
	public boolean hasTemporalInfo() {
		return Long.MIN_VALUE != startTime || Long.MAX_VALUE != endTime;
	}

	/**
	 * Check if the two temporals overlap each others.
	 * 
	 * @param temporal Temporal to be checked.
	 * @return true if the two temporal intersect; false otherwise.
	 */
	public boolean overlap(Temporal temporal) {
		if (null == temporal) return false;
		if (this == temporal || !hasTemporalInfo()) return true;
		if (isTimeInstance()) {
			if (temporal.isTimeInstance()) {
				return startTime == temporal.startTime;
			} else {
				if (startTime < temporal.startTime || temporal.endTime <= startTime) return false;
			}
		} else if (temporal.isTimeInstance()) {
			if (startTime > temporal.startTime || endTime <= temporal.startTime) return false;
		} else {
			if (startTime >= temporal.endTime || endTime <= temporal.startTime) return false;
		}
		return true;
	}

	// public Temporal getOverlappedInterval(Temporal temporal){
	// if (null==temporal)return clone();
	// long st=temporal.startTime>startTime?temporal.startTime:startTime;
	// long et=endTime>temporal.endTime?temporal.endTime:endTime;
	// return new Temporal(st,et);
	// }

	/**
	 * Check if the two temporals meet.
	 * 
	 * @param temporal Temporal to be checked.
	 * @return true if the start time of one is equal to the end time of the other; false otherwise.
	 */
	public boolean meet(Temporal temporal) {
		if (null == temporal) return false;
		// if (null == temporal || this == temporal) return false;
		if (isTimeInstance()) {
			if (temporal.isTimeInstance()) return startTime == temporal.startTime;
			else return startTime == temporal.endTime || startTime == temporal.startTime;
		} else {
			if (temporal.isTimeInstance()) return startTime == temporal.startTime || endTime == temporal.startTime;
		}
		return startTime == temporal.endTime || endTime == temporal.startTime;
	}

	/**
	 * Check if the two temporals are overlapped or meet each others.
	 * 
	 * @param temporal Temporal to be checked.
	 * @return true if the two temporals are overlapped or meet.
	 */

	public boolean overlapOrMeet(Temporal temporal) {
		if (null == temporal) return false;
		if (this == temporal || !hasTemporalInfo()) return true;
		if (isTimeInstance()) {
			if (temporal.isTimeInstance()) return startTime == temporal.startTime;
			else {
				if (startTime < temporal.startTime || temporal.endTime < startTime) return false;
			}
		} else {
			if (startTime > temporal.endTime || endTime < temporal.startTime) return false;
		}
		return true;
	}

	/**
	 * Check if the input temporal lies inside this temporal.
	 * 
	 * @param temporal Temporal to be checked.
	 * @return true if the input temporal is inside the interval of this temporal; false otherwise.
	 */
	public boolean includes(Temporal temporal) {
		if (null == temporal) return false;
		if (this == temporal) return true;
		if (isTimeInstance()) {
			return temporal.isTimeInstance() ? startTime == temporal.startTime : false;
		} else {
			if (temporal.isTimeInstance()) return !(startTime > temporal.startTime || temporal.endTime >= endTime);
			return !(startTime > temporal.startTime || temporal.endTime > endTime);
		}
	}

	/**
	 * Check if the temporal is included inside the specified temporal.
	 * 
	 * @param temporal Temporal to be checked.
	 * @return true if this temporal is included inside the specified temporal; false otherwise.
	 */
	public boolean during(Temporal temporal) {
		if (null == temporal) return false;
		if (this == temporal) return true;
		if (isTimeInstance()) {
			if (temporal.isTimeInstance()) return startTime == temporal.startTime;
			return temporal.startTime <= startTime && startTime <= temporal.endTime;
		} else {
			if (temporal.isTimeInstance()) return false;
			// return temporal.startTime<=startTime && endTime<=temporal.endTime ;
			return temporal.startTime <= startTime && endTime <= temporal.endTime;
			// return !(startTime < temporal.startTime || temporal.endTime < endTime);
		}

	}

	// public boolean during(Temporal temporal) {
	// if (null == temporal) return false;
	// if (this == temporal) return true;
	// if (isTimeInstance()) {
	// if (temporal.isTimeInstance()) return startTime == temporal.startTime;
	// return !(startTime < temporal.startTime || temporal.endTime <= endTime);
	// } else return !(startTime < temporal.startTime || temporal.endTime < endTime);
	// }

	/**
	 * Return the union of the two temporal.
	 * 
	 * @param temporal Temporal to be union with.
	 * @return Union of the two temporal.
	 * @throws TemporalException If the two temporal are not intersect with each other.
	 */
	public Temporal join(Temporal temporal) throws TemporalException {
		if (!overlapOrMeet(temporal))
		// if (!(overlap(temporal) || meet(temporal)))
			throw new TemporalException(ErrorMessage.TEMPORAL_NOT_INTERSECTED, new Object[] { this, temporal });
		long st = startTime > temporal.startTime ? temporal.startTime : startTime;
		long et = endTime > temporal.endTime ? endTime : temporal.endTime;
		return new Temporal(st, et);
	}

	/**
	 * Return the intersection of the two temporal.
	 * 
	 * @param temporal Temporal to be intersected with.
	 * @return Intersection of the two temporal.
	 * @throws TemporalException If the two temporal are not intersect with each other.
	 */
	public Temporal intersect(Temporal temporal) throws TemporalException {
		if (!overlap(temporal)) throw new TemporalException(ErrorMessage.TEMPORAL_NOT_INTERSECTED, new Object[] { this, temporal });
		long st = startTime > temporal.startTime ? startTime : temporal.startTime;
		long et = endTime > temporal.endTime ? temporal.endTime : endTime;
		return new Temporal(st, et);
	}

	public Temporal union(Temporal temporal) throws TemporalException {
		if (!overlapOrMeet(temporal)) throw new TemporalException(ErrorMessage.TEMPORAL_NOT_INTERSECTED, new Object[] { this, temporal });
		long st = startTime > temporal.startTime ? temporal.startTime : startTime;
		long et = temporal.endTime > endTime ? temporal.endTime : endTime;
		return new Temporal(st, et);
	}

	/**
	 * @param temporal Temporal
	 * @return Time segments of the two temporal.
	 * @throws TemporalException If the two temporal are not intersect with each other.
	 */
	public NavigableSet<Temporal> getTemporalSegments(Temporal temporal) throws TemporalException {
		if (null == temporal) throw new TemporalException(ErrorMessage.TEMPORAL_NULL_TEMPORAL);
		return getTemporalSegments(new Object[] { temporal });
	}

	/**
	 * Return a set of temporal segments that are extracted from a set of temporals or temporal literals, and
	 * are overlapped with the current temporal.
	 * 
	 * @param objects A set of temporals or temporal literals.
	 * @return A set of extracted temporal segments.
	 */
	public NavigableSet<Temporal> getTemporalSegments(Collection<?> objects) throws TemporalException {
		if (null == objects || objects.size() == 0) throw new TemporalException(ErrorMessage.TEMPORAL_NULL_TEMPORAL);
		Object[] objs = new Object[objects.size()];
		objects.toArray(objs);
		return getTemporalSegments(objs);
	}

	public NavigableSet<Temporal> getTemporalSegments(Object[] objects) throws TemporalException {
		if (null == objects || objects.length == 0) throw new TemporalException(ErrorMessage.TEMPORAL_NULL_TEMPORAL);

		TreeSet<Long> timeInstances = new TreeSet<Long>();
		TreeSet<Long> timeStamps = new TreeSet<Long>();

		if (isTimeInstance()) {
			timeInstances.add(startTime);
			timeStamps.add(startTime);
		} else {
			timeStamps.add(startTime);
			timeStamps.add(endTime);
		}

		Temporal temporal = null;
		for (Object obj : objects) {
			if (null==obj)continue;
			
			temporal = getTemporal(obj);
		//	if (null == temporal) continue;
			if (overlapOrMeet(temporal)) {
				if (temporal.isTimeInstance()) {
					timeInstances.add(temporal.startTime);
					timeStamps.add(temporal.startTime);
				} else {
					timeStamps.add(temporal.startTime);
					timeStamps.add(temporal.endTime);
				}
			} else {
				if (!AppConst.isDeploy) {
					String msg = Messages.getErrorMessage(ErrorMessage.TEMPORAL_NOT_INTERSECTED, new Object[] { this, temporal });
					System.err.println(msg);
				}
			}
		}

		return generateTemporalSegments(timeStamps, timeInstances);
	}

	@Override
	public int compareTo(Object o) {
		if (this == o) return 0;
		if (!(o instanceof Temporal)) return getClass().getName().compareTo(o.getClass().getName());

		return TEMPORAL_COMPARATOR.compare(this, (Temporal) o);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (endTime ^ (endTime >>> 32));
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (null == obj) return false;
		if (getClass() != obj.getClass()) return false;

		Temporal other = (Temporal) obj;
		if (startTime != other.startTime) return false;
		if (endTime != other.endTime) return false;
		return true;
	}

	@Override
	public String toString() {
		// if (!hasTemporalInfo()) return "";
		return TEMPORAL_START + (Long.MIN_VALUE == startTime ? TEMPORAL_NEGATIVE_INFINITY : startTime) + TEMPORAL_SEPARATOR
				+ (Long.MAX_VALUE == endTime ? TEMPORAL_POSITIVE_INFINITY : endTime) + TEMPORAL_END;
	}
}
