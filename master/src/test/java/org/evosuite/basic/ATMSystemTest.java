package org.evosuite.basic;

import com.examples.with.different.packagename.ATM;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.SystemTestBase;
import org.evosuite.continuous.persistency.CsvJUnitData;
import org.evosuite.coverage.TestFitnessFactory;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.statistics.RuntimeVariable;
import org.evosuite.strategy.TestGenerationStrategy;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ATMSystemTest  extends SystemTestBase {

    @Test
    public void testNullString() {
        EvoSuite evosuite = new EvoSuite();

        String targetClass = ATM.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;

        Properties.OUTPUT_VARIABLES = "TARGET_CLASS,criterion," +
                RuntimeVariable.Coverage.name() + "," + RuntimeVariable.Covered_Goals + "," + RuntimeVariable.Total_Goals + "," +
                RuntimeVariable.MutationScore.name() + "," + RuntimeVariable.BZUExecutionTimeCoverage + "," + RuntimeVariable.BZULengthCoverage + "," +
                RuntimeVariable.BranchCoverage;
        Properties.STATISTICS_BACKEND = Properties.StatisticsBackend.DEBUG;

        String[] command = new String[] { "-generateSuite", "-class", targetClass , "-criterion" , "branch:BZU_TIME:BZU_LENGTH"};

        Object result = evosuite.parseCommandLine(command);
        GeneticAlgorithm<?> ga = getGAFromResult(result);
        TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        int goals = 0 ;
        for(TestFitnessFactory o : TestGenerationStrategy.getFitnessFactories()){
            goals+= o.getCoverageGoals().size();
        }

        Assert.assertEquals("Wrong number of goals: ", 22, goals);
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }
}
