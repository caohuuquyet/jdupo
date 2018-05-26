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
package spindle.test.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Base class for {@link Combination} and {@link Permutation}.
 * 
 * @version Last modified 2013.08.04
 * @since version 1.0.0
 */
public abstract class CombinationBase {

	private final static String SEP = "-";
	private static DecimalFormat formatter = new DecimalFormat("00");
	protected int values[] = null;

	protected Set<String> resultsStr = null;
	protected int[][] results = null;

	protected void setup(final int n, final int r) {
		int formatterSize = (int) Math.ceil(Math.log10(n));
		String pattern = "";
		for (int i = 0; i < formatterSize; i++)
			pattern += "0";
		formatter = new DecimalFormat(pattern);

		values = new int[r];
		for (int i = 0; i < r; i++)
			values[i] = i;

		resultsStr = new TreeSet<String>();
		int expectedSize = calculateSize(n, r);
		// System.out.println("s="+expectedSize);
		results = new int[expectedSize][];
	}

	public List<List<Integer>> list(final int n, final int r) {
		if (r > n) throw new IllegalArgumentException("r cannot greater than n");
		// System.out.println("generateList-start, "+n+","+r);
		generateList(n, r);
		// System.out.println("generateList-end");

		List<List<Integer>> res = new Vector<List<Integer>>();
		for (int i = 0; i < results.length; i++) {
			List<Integer> l = new Vector<Integer>();
			for (int j = 0; j < r; j++) {
				l.add(results[i][j]);
			}
			res.add(l);
		}
		return res;
	}

	protected void swap(final int i, final int j) {
		int t = values[i];
		values[i] = values[j];
		values[j] = t;
	}

	public void printResult() {
		if (null == resultsStr || resultsStr.size() == 0) System.out.println("no result found!");
		System.out.println("---");
		System.out.println("resultsStr.size()=" + resultsStr.size());
		for (String s : resultsStr) {
			System.out.println(s);
		}
		System.out.println("---");
		System.out.println("results.length=" + results.length);
		for (int i = 0; i < results.length; i++) {
			for (int j = 0; j < results[i].length; j++) {
				System.out.print(((j == 0 ? "" : SEP)) + results[i][j]);
			}
			System.out.println("");
		}
	}

	public int[][] getResults() {
		return results;
	}

	protected int[] getNext(final int r) {
		results[resultsStr.size()] = Arrays.copyOf(values, r);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < r; i++)
			sb.append((i == 0) ? "" : SEP).append(formatter.format(values[i]));
		resultsStr.add(sb.toString());

		int[] resultTemp = Arrays.copyOf(values, r);

		return resultTemp;
	}

	protected abstract int[][] generateList(int n, int r);

	protected abstract int calculateSize(int n, int r);

}
