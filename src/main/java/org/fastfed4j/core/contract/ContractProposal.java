package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
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

    /**
     * Constructs with all properties
     * @param contract contract being proposed
     * @param status status of the proposal
     * @param expirationDate expirationDate of the proposal
     */
    public ContractProposal(Contract contract, ContractProposalStatus status, Date expirationDate) {
        this(contract == null ? null : contract.getFastFedConfiguration());
        Objects.requireNonNull(contract, "contract must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(expirationDate, "expirationDate must not be null");
        setContract(contract);
        setStatus(status);
        setExpirationDate(expirationDate);
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public ContractProposal(ContractProposal other) {
        super(other);
        if (other.contract != null )
            this.contract = new Contract(other.contract);
        if (other.expirationDate != null)
            this.expirationDate = new Date(other.expirationDate.getTime());
        this.status = other.status;
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

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.CONTRACT_PROPOSAL);
        builder.putAll(super.toJson());
        builder.putAll(contract.toJson());
        builder.put(JsonMember.CONTRACT_PROPOSAL_STATUS, status.toString());
        builder.put(JsonMember.CONTRACT_PROPOSAL_EXPIRATION, expirationDate);
        return builder.build();
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
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JsonMember.CONTRACT_PROPOSAL);
        super.hydrateFromJson(json);

        setExpirationDate( json.getDate(JsonMember.CONTRACT_PROPOSAL_EXPIRATION));

        String status = json.getString(JsonMember.CONTRACT_PROPOSAL_STATUS);
        if (status != null) {
            try {
                setStatus( ContractProposalStatus.valueOf(status));
            }
            catch (IllegalArgumentException e) {
                json.getErrorAccumulator().add(
                        "Invalid value for \"" + getFullyQualifiedName(JsonMember.CONTRACT_PROPOSAL_STATUS) +
                        "\" (received: \"" + json.getString(JsonMember.CONTRACT_PROPOSAL_STATUS) + "\")"
                );
            }
        }

        JsonObject contractJson = json.getObject(JsonMember.CONTRACT);
        if (contractJson != null) {
            Contract contract = new Contract(getFastFedConfiguration());
            contract.hydrateFromJson(contractJson);
            setContract(contract);
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredObject(errorAccumulator, JsonMember.CONTRACT_PROPOSAL_EXPIRATION, expirationDate);
        validateRequiredObject(errorAccumulator, JsonMember.CONTRACT_PROPOSAL_STATUS, status);
        validateRequiredObject(errorAccumulator, JsonMember.CONTRACT, contract);
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
