/**
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.ga.metaheuristics.mosa.comparators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;

/**
 * This class implements a <code>Comparator</code> (a method for comparing <code>Chromosomes</code> objects) 
 * based on the dominance test and considering the specified test goals only.
 * 
 * @author Annibale Panichella
 */
public class PreferenceSortingComparator<T extends Chromosome> implements Comparator<Object> {

	private final FitnessFunction<T> objective;

	private final List<T> population;

	/**
	 * Constructor
	 *
	 * @param population
	 * @param goal
	 */
	public PreferenceSortingComparator(List<T> population, FitnessFunction<T> goal) {
		this.population = new ArrayList<T>(population);
		this.objective = goal;
	}

	/**
	 * Compares two test cases focusing only on the goals in {@link MOSADominanceComparator#objectives}".
	 * 
	 * @param object1
	 *            Object representing the first test cases.
	 * @param object2
	 *            Object representing the second test cases.
	 * @return -1, or 0, or 1 if object1 dominates object2, both are non-dominated, or solution1 is dominated by solution2, respectively.
	 */
	@SuppressWarnings("unchecked")
	public int compare(Object object1, Object object2) {
		if (object1 == null)
			return 1;
		else if (object2 == null)
			return -1;

		T solution1 = (T) object1;
		T solution2 = (T) object2;

		double value1, value2;
		value1 = solution1.getFitness(this.objective);
		value2 = solution2.getFitness(this.objective);
		if (value1 < value2)
			return -1;
		else if (value1 > value2)
			return +1;
		else {
			List<T> population_without_both_solutions = new ArrayList<T>(this.population);
			population_without_both_solutions.remove(solution1);
			population_without_both_solutions.remove(solution2);

			TestSuiteChromosome solution1_suite = new TestSuiteChromosome();
			solution1_suite.addTests((Collection<TestChromosome>) population_without_both_solutions);
			solution1_suite.addTest((TestChromosome) solution1);

			TestSuiteChromosome solution2_suite = new TestSuiteChromosome();
			solution2_suite.addTests((Collection<TestChromosome>) population_without_both_solutions);
			solution2_suite.addTest((TestChromosome) solution2);

			return solution1_suite.compareSecondaryObjective(solution2_suite);
		}

	} // compare

} // PreferenceCriterion

