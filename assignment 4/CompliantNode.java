package hw4.node;

import hw4.Scenario;
import hw4.Transaction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CompliantNode implements Node {
	
    private final Set<Transaction> seenTxs = new HashSet<>();

    public static final NodeFactory FACTORY = new NodeFactory() {
        @Override
        public Node newNode(Scenario scenario) {
            return new CompliantNode(scenario);
        }

        @Override
        public String toString() {
            return "CompliantNode";
        }
    };

    public CompliantNode(Scenario scenario) {

    }

    @Override
    public void setTrustedNodes(boolean[] trustedNodeFlag) {

    }

    @Override
    public void setInitialTransactions(Set<Transaction> initialTransactions) {
    	seenTxs.addAll(initialTransactions);

    }

    @Override
    public Set<Transaction> getProposalsForTrustingNodes() {
        return Collections.unmodifiableSet(seenTxs);
    }

    @Override
    public void receiveFromTrustedNodes(Set<Candidate> candidates) {
        seenTxs.addAll(candidates.stream().map(c -> c.tx).collect(Collectors.toSet()));
    }
}
