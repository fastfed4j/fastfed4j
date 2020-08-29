package org.fastfed4j.test.evaluator.contract;

import org.fastfed4j.core.contract.ContractProposal;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.MetadataEvaluator;


public class ContractProposalEvaluator extends MetadataEvaluator {

    private static final ContractEvaluator contractEvaluator = new ContractEvaluator();

    public void evaluate(Operation operation, ContractProposal specimen1, ContractProposal specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "expirationDate");
        performOperation(operation, specimen1, specimen2, "status");

        contractEvaluator.evaluate(
                operation,
                specimen1.getContract(),
                specimen2 == null ? null : specimen2.getContract()
        );
    }
}
