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

/**
 * DataAnotation the set of nCr combination.
 * 
 * @version Last modified 2013.08.04
 * @since version 1.0.0
 */
public class Combination extends CombinationBase {

	@Override
	protected int calculateSize(final int n, final int r) {
		int den = n - r;
		int size = 1;
		for (int i = n; i > den; i--)
			size *= i;
		for (int i = 1; i <= r; i++)
			size /= i;
		return size;
	}

	protected int[][] generateList(final int n, final int r) {
		setup(n, r);
		getNext(r);
		while (hasNext(n, r)) {
			getNext(r);
		}
		return results;
	}

	protected boolean hasNext(final int n, final int k) {
		int i = k - 1;
		++values[i];
		while (i > 0 && values[i] >= n - k + i + 1) {
			--i;
			++values[i];
		}
		if (values[0] > n - k) return false;
		for (i = i + 1; i < k; ++i) {
			values[i] = values[i - 1] + 1;
		}
		return true;
	}

}
