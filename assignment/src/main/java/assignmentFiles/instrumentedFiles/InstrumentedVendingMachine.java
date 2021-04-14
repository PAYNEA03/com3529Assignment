package assignmentFiles.instrumentedFiles;

import java.util.TreeSet;
import java.util.Set;
import assignmentFiles.execution.*;

public class InstrumentedVendingMachine {

    private int totalCoins, currentCoins;

    private boolean allowVend;

    public VendingMachine() {
        totalCoins = 0;
        currentCoins = 0;
        allowVend = false;
    }

    public void returnCoins(Set<Integer> coveredBranches) {
        currentCoins = 0;
    }

    public void addCoin(Set<Integer> coveredBranches) {
        currentCoins++;
        if (currentCoins > 1) {
            TestDataGenerator.coveredBranch(1, coveredBranches);
            allowVend = true;
        }
    }

    public void vend(Set<Integer> coveredBranches) {
        if (allowVend) {
            TestDataGenerator.coveredBranch(2, coveredBranches);
            totalCoins += currentCoins;
            currentCoins = 0;
            allowVend = false;
        }
    }

    public static void testingGetterAndSetter(String[] paramList, Set<Integer> coveredBranches) {
        String out;
        String out2;
    }
}
