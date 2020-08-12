package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.metadata.Metadata;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a proposed contract that is not yet active. It contains the contract, plus additional attributes
 * relevant to a pending proposal, such as expiration and status.
 *
 * <p>Both the Identity Provider and Application provider can have situations where a Contract may be in such a holding pattern.</p>
 *
 * <p>For the Identity Provider, this can occur when an administrator requires manual review of changes before they can take effect.
 * In this case, the Proposed Contract may exist in a queue at the Identity Provider, awaiting approval and activation.</p>
 *
 * <p>For the Application Provider, this entity may be to represent the whitelist that the FastFed Handshake specification
 * requires. In this case, the Contract Proposal represents a possible contract that the Identity Provider is permitted to activate
 * at a later time.</p>
 */
public class ContractProposal extends Metadata {

    private Contract contract;
    private Date expirationDate;
    private ContractProposalStatus status;

    /**
     * Constructs an empty Contract Proposal
     * @param configuration
     */
    public ContractProposal(FastFedConfiguration configuration) {
        super(configuration);
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public ContractProposalStatus getStatus() {
        return status;
    }

    public void setStatus(ContractProposalStatus status) {
        this.status = status;
    }

    /**
     * Constructs a Contract Proposal from a JSON-serialized representation.
     * @param configuration FastFed Configuration that controls the behavior of parsing and validation
     * @param jsonString JSON-serialized representation of the ContractProposal
     * @return
     */
    public static ContractProposal fromJson(FastFedConfiguration configuration, String jsonString) {
        Objects.requireNonNull(configuration, "configuration must not be null");
        Objects.requireNonNull(jsonString, "json must not be null");
        ContractProposal contractProposal = new ContractProposal(configuration);
        contractProposal.hydrateAndValidate(jsonString);
        return contractProposal;
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.CONTRACT_PROPOSAL);
        super.hydrateFromJson(json);

        setExpirationDate( json.getDate(JSONMember.CONTRACT_PROPOSAL_EXPIRATION));

        ContractProposalStatus status = null;
        if (json.containsValueForMember(JSONMember.CONTRACT_PROPOSAL_STATUS)) {
            try {
                status = ContractProposalStatus.valueOf(json.getString(JSONMember.CONTRACT_PROPOSAL_STATUS));
            }
            catch (IllegalArgumentException e) {
                json.getErrorAccumulator().add(
                        "Invalid value for '" + JSONMember.CONTRACT_PROPOSAL_STATUS
                        + "' (received: '" + json.getString(JSONMember.CONTRACT_PROPOSAL_STATUS) + "')"
                );
            }
        }
        setStatus(status);

        Contract contract = new Contract(getFastFedConfiguration());
        contract.hydrateFromJson( json.getObject(JSONMember.CONTRACT));
        setContract(contract);
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredObject(errorAccumulator, JSONMember.CONTRACT_PROPOSAL_EXPIRATION, expirationDate);
        validateRequiredObject(errorAccumulator, JSONMember.CONTRACT_PROPOSAL_STATUS, status);
        validateRequiredObject(errorAccumulator, JSONMember.CONTRACT, contract);
        if (contract != null) { contract.validate(errorAccumulator); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ContractProposal that = (ContractProposal) o;
        return contract.equals(that.contract) &&
                expirationDate.equals(that.expirationDate) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contract, expirationDate, status);
    }
}
