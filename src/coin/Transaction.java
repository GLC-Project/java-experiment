package coin;

import java.math.BigInteger;

import util.BigFastList;
import util.crypto.Script;
import util.crypto.Sha256Hash;

/**
 * @author Amir Eslampanah
 * 
 */
public class Transaction {

    /**
     * These constants are a part of a scriptSig signature on the
     * this.transactionIn. They define the details of how a transaction can be
     * redeemed, specifically, they control how the hash of the transaction is
     * calculated.
     * <p/>
     * In the official client, this enum also has another flag,
     * SIGHASH_ANYONECANPAY. In this implementation, that's kept separate. Only
     * SIGHASH_ALL is actually used in the official client today. The other
     * flags exist to allow for distributed contracts.
     */
    public enum SigHash {
	ALL, // 1
	NONE, // 2
	SINGLE, // 3
    }

    /**
     * Transaction Data Format Version.
     * 
     * 4 Bytes usually
     */

    BigInteger transactionDataFormatVersion;

    /**
     * Number of Transaction this.transactionIn.
     * 
     * Variable size
     */

    BigInteger transactionInputs;

    /**
     * BigFastList of Transaction this.transactionIn.
     * 
     * Variable size
     */
    BigFastList<TransactionInput> transactionIn = new BigFastList<TransactionInput>();

    /**
     * Number of Transaction this.transactionOut.
     * 
     * Variable size
     */
    BigInteger transactionOutputs;

    /**
     * BigFastList of Transaction this.transactionOut.
     * 
     * Variable size
     */
    BigFastList<TransactionOutput> transactionOut = new BigFastList<TransactionOutput>();

    /**
     * The block number or timestamp at which this transaction is locked: Value
     * Description 0 Always locked < 500000000 Block number at which this
     * transaction is locked >= 500000000 UNIX timestamp at which this
     * transaction is locked
     * 
     * If all TxIn inputs have final (0xffffffff) sequence numbers then
     * lock_time is irrelevant. Otherwise, the transaction may not be added to a
     * block until after lock_time.
     */

    BigInteger lockTime;

    /**
     * 
     * */
    public Transaction() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param in
     */
    public void addTransactionInput(TransactionInput in) {
	this.transactionIn.add(in);
    }

    /**
     * @param out
     */
    public void addTransactionOutput(TransactionOutput out) {
	this.transactionOut.add(out);
    }

    /**
     * Calculates a signature hash, that is, a hash of a simplified form of the
     * transaction. How exactly the transaction is simplified is specified by
     * the type and anyoneCanPay parameters.
     * <p>
     * 
     * You don't normally ever need to call this yourself. It will become more
     * useful in future as the contracts features of Litecoin are developed.
     * 
     * @param inputIndex
     *            input the signature is being calculated for. Tx signatures are
     *            always relative to an input.
     * @param connectedScript
     *            the bytes that should be in the given input during signing.
     * @param type
     *            Should be SigHash.ALL
     * @param anyoneCanPay
     *            should be false.
     * @throws ScriptException
     *             if connectedScript is invalid
     */
    /*
     * public synchronized Sha256Hash hashTransactionForSignature(int
     * inputIndex, byte[] connectedScript, SigHash type, boolean anyoneCanPay)
     * throws ScriptException { return hashTransactionForSignature(inputIndex,
     * connectedScript, (byte) ((type.ordinal() + 1) | (anyoneCanPay ? 0x80 :
     * 0x00))); }
     */

    /**
     * This is required for signatures which use a sigHashType which cannot be
     * represented using SigHash and anyoneCanPay See transaction
     * c99c49da4c38af669dea436d3e73780dfdb6c1ecf9958baa52960e8baee30e73, which
     * has sigHashType 0
     */
    /*
     * synchronized Sha256Hash hashTransactionForSignature(int inputIndex,
     * byte[] connectedScript, byte sigHashType) throws ScriptException { //
     * TODO: This whole separate method should be un-necessary if we fix how //
     * we deserialize sighash flags.
     * 
     * // The SIGHASH flags are used in the design of contracts, please see //
     * this page for a further understanding of // the purposes of the code in
     * this method: // // https://en.litecoin.it/wiki/Contracts
     * 
     * try { // Store all the input scripts and clear them in preparation for //
     * signing. If we're signing a fresh // transaction that step isn't very
     * helpful, but it doesn't add much // cost relative to the actual // EC
     * math so we'll do it anyway. // // Also store the input sequence numbers
     * in case we are clearing // them with SigHash.NONE/SINGLE byte[][]
     * inputScripts = new byte[this.transactionIn.size()][]; long[]
     * inputSequenceNumbers = new long[this.transactionIn.size()]; for (int i =
     * 0; i < this.transactionIn.size(); i++) { inputScripts[i] =
     * this.transactionIn.get(i).getScriptBytes(); inputSequenceNumbers[i] =
     * this.transactionIn.get(i) .getSequenceNumber();
     * this.transactionIn.get(i).setScriptBytes(TransactionInput.EMPTY_ARRAY); }
     * 
     * // This step has no purpose beyond being synchronized with the //
     * reference clients bugs. OP_CODESEPARATORof executing // scripts that
     * shipped in Litecoin 0.1. // is a legacy holdover from a previous, broken
     * design // It was seriously flawed and would have let anyone take anyone
     * // elses money. Later versions switched to // the design we use today
     * where scripts are executed independently // but share a stack. This left
     * the // OP_CODESEPARATOR instruction having no purpose as it was only //
     * meant to be used internally, not actually // ever put into scripts.
     * Deleting OP_CODESEPARATOR is a step that // should never be required but
     * if we don't // do it, we could split off the main chain. connectedScript
     * = Script.removeAllInstancesOfOp(connectedScript,
     * Script.OP_CODESEPARATOR);
     * 
     * // Set the input to the script of its output. Satoshi does this but //
     * the step has no obvious purpose as // the signature covers the hash of
     * the prevout transaction which // obviously includes the output script //
     * already. Perhaps it felt safer to him in some way, or is another //
     * leftover from how the code was written. TransactionInput input =
     * this.transactionIn.get(inputIndex);
     * input.setScriptBytes(connectedScript);
     * 
     * BigFastList<TransactionOutput> outputs = this.transactionOut; if
     * ((sigHashType & 0x1f) == (SigHash.NONE.ordinal() + 1)) { // SIGHASH_NONE
     * means no outputs are signed at all - the // signature is effectively for
     * a "blank cheque". this.transactionOut = new
     * BigFastList<TransactionOutput>(0); // The signature isn't broken by new
     * versions of the transaction // issued by other parties. for (int i = 0; i
     * < this.transactionIn.size(); i++) if (i != inputIndex)
     * this.transactionIn.get(i).setSequenceNumber(0); } else if ((sigHashType &
     * 0x1f) == (SigHash.SINGLE.ordinal() + 1)) { // SIGHASH_SINGLE means only
     * sign the output at the same index // as the input (ie, my output). if
     * (inputIndex >= this.transactionOut.size()) { // The input index is beyond
     * the number of outputs, it's a // buggy signature made by a broken //
     * Litecoin implementation. The reference client also // contains a bug in
     * handling this case: // any transaction output that is signed in this case
     * will // result in both the signed output // and any future outputs to
     * this public key being // steal-able by anyone who has // the resulting
     * signature and the public key (both of which // are part of the signed tx
     * input). // Put the transaction back to how we found it. // // TODO: Only
     * allow this to happen if we are checking a // signature, not signing a
     * transactions for (int i = 0; i < transactionIn.size(); i++) {
     * transactionIn.get(i).setScriptBytes(inputScripts[i]);
     * transactionIn.get(i) .setSequenceNumber(inputSequenceNumbers[i]); }
     * this.transactionOut = outputs; // Satoshis bug is that SignatureHash was
     * supposed to return // a hash and on this codepath it // actually returns
     * the constant "1" to indicate an error, // which is never checked for.
     * Oops. return new Sha256Hash(
     * "0100000000000000000000000000000000000000000000000000000000000000"); } //
     * In SIGHASH_SINGLE the outputs after the matching input index // are
     * deleted, and the outputs before // that position are "nulled out".
     * Unintuitively, the value in a // "null" transaction is set to -1.
     * this.transactionOut = new BigFastList<TransactionOutput>(
     * this.transactionOut.subList(0, inputIndex + 1)); for (int i = 0; i <
     * inputIndex; i++) this.transactionOut.set(i, new TransactionOutput(params,
     * this, BigInteger.valueOf(-1), new byte[] {})); // The signature isn't
     * broken by new versions of the transaction // issued by other parties. for
     * (int i = 0; i < this.transactionIn.size(); i++) if (i != inputIndex)
     * this.transactionIn.get(i).setSequenceNumber(0); }
     * 
     * BigFastList<TransactionInput> inputs = this.transactionIn; if
     * ((sigHashType & 0x80) == 0x80) { // SIGHASH_ANYONECANPAY means the
     * signature in the input is not // broken by changes/additions/removals //
     * of other this.transactionIn. For example, this is useful for building //
     * assurance contracts. this.inputs = new BigFastList<TransactionInput>();
     * this.this.transactionIn.add(input); }
     * 
     * ByteArrayOutputStream bos = new ByteArrayOutputStream( length ==
     * UNKNOWN_LENGTH ? 256 : length + 4); litecoinSerialize(bos); // We also
     * have to write a hash type (sigHashType is actually an // unsigned char)
     * uint32ToByteStreamLE(0x000000ff & sigHashType, bos); // Note that this is
     * NOT reversed to ensure it will be signed // correctly. If it were to be
     * printed out // however then we would expect that it is IS reversed.
     * Sha256Hash hash = new Sha256Hash(doubleDigest(bos.toByteArray()));
     * bos.close();
     * 
     * // Put the transaction back to how we found it. this.transactionIn =
     * inputs; for (int i = 0; i < this.transactionIn.size(); i++) {
     * this.transactionIn.get(i).setScriptBytes(inputScripts[i]);
     * this.transactionIn.get(i).setSequenceNumber(inputSequenceNumbers[i]); }
     * this.transactionOut = outputs; return hash; } catch (IOException e) {
     * throw new RuntimeException(e); // Cannot happen. } }
     */

    public class TransactionInput {

	/**
	 * 36 bytes
	 */
	private Outpoint previous_output;

	/**
	 * Length of the script.
	 * 
	 * Typically loaded from a var_int
	 */
	private BigInteger script_length;

	/**
	 * Computational Script for confirmation of transaction authorization
	 * 
	 * Typically stored as a uchar[]
	 */

	private Script script;

	/**
	 * Sequence number
	 * 
	 * Transaction version as defined by the sender. Intended for
	 * "replacement" of transactions when information is updated before
	 * inclusion into a block.
	 * 
	 * Typically stored as a uint32_t
	 */

	private BigInteger sequenceNum;

	/**
	 * 
	 */
	public TransactionInput() {

	}

	/**
	 * @param b
	 */
	public TransactionInput(byte[] b) {

	}

	/**
	 * @return the previous_output
	 */
	public Outpoint getPrevious_output() {
	    return this.previous_output;
	}

	/**
	 * @param previous_output1
	 *            the previous_output to set
	 */
	public void setPrevious_output(Outpoint previous_output1) {
	    this.previous_output = previous_output1;
	}

	/**
	 * @return the script_length
	 */
	public BigInteger getScript_length() {
	    return this.script_length;
	}

	/**
	 * @param script_length1
	 *            the script_length to set
	 */
	public void setScript_length(BigInteger script_length1) {
	    this.script_length = script_length1;
	}

	/**
	 * @return the script
	 */
	public Script getScript() {
	    return this.script;
	}

	/**
	 * @param script1
	 *            the script to set
	 */
	public void setScript(Script script1) {
	    this.script = script1;
	}

	/**
	 * @return the sequenceNum
	 */
	public BigInteger getSequenceNum() {
	    return this.sequenceNum;
	}

	/**
	 * @param sequenceNum1
	 *            the sequenceNum to set
	 */
	public void setSequenceNum(BigInteger sequenceNum1) {
	    this.sequenceNum = sequenceNum1;
	}

    }

    /**
     * @author A
     * 
     */
    public class Outpoint {

	/**
	 * Hash of referenced transaction
	 * 
	 * Typically char[32]
	 */
	StringBuffer hash;

	/**
	 * Index of specific output in the transaction.
	 * 
	 * Typically stored as a uint32_t
	 */
	BigInteger index;

	/**
	 * @return the hash
	 */
	public StringBuffer getHash() {
	    return this.hash;
	}

	/**
	 * @param hash
	 *            the hash to set
	 */
	public void setHash(StringBuffer hash) {
	    this.hash = hash;
	}

	/**
	 * @return the index
	 */
	public BigInteger getIndex() {
	    return this.index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(BigInteger index) {
	    this.index = index;
	}

    }

    /**
     * @author A
     * 
     */
    public class TransactionOutput {

	/**
	 * Transaction Value
	 * 
	 * Typically stored as an int64_t
	 * 
	 */
	BigInteger transactionValue;

	/**
	 * Length of the pk_script
	 * 
	 * Normally stored as an var_int
	 */
	BigInteger pkScriptLength;

	/**
	 * Script
	 * 
	 * Usually contains the public key as a script setting up conditions to
	 * claim this output.
	 */

	Script pkScript;

	/**
	 * @param n
	 * @param outputAmount
	 * @param scriptBytes
	 */
	public TransactionOutput(BigInteger outputAmount, byte[] scriptBytes) {

	}

	public TransactionOutput() {
	    // TODO Auto-generated constructor stub
	}

	/**
	 * @return the transactionValue
	 */
	public BigInteger getTransactionValue() {
	    return this.transactionValue;
	}

	/**
	 * @param transactionValue
	 *            the transactionValue to set
	 */
	public void setTransactionValue(BigInteger transactionValue) {
	    this.transactionValue = transactionValue;
	}

	/**
	 * @return the pkScriptLength
	 */
	public BigInteger getPkScriptLength() {
	    return this.pkScriptLength;
	}

	/**
	 * @param pkScriptLength
	 *            the pkScriptLength to set
	 */
	public void setPkScriptLength(BigInteger pkScriptLength) {
	    this.pkScriptLength = pkScriptLength;
	}

	/**
	 * @return the pkScript
	 */
	public Script getPkScript() {
	    return this.pkScript;
	}

	/**
	 * @param pkScript
	 *            the pkScript to set
	 */
	public void setPkScript(Script pkScript) {
	    this.pkScript = pkScript;
	}

    }

    /**
     * @param index
     * @param cHECKSIGconnectedScript
     * @param b
     * @return
     */
    public Sha256Hash hashTransactionForSignature(int index,
	    byte[] cHECKSIGconnectedScript, byte b) {
	// TODO Auto-generated method stub
	return null;
    }

    /**
     * @return the transactionDataFormatVersion
     */
    public BigInteger getTransactionDataFormatVersion() {
	return this.transactionDataFormatVersion;
    }

    /**
     * @param transactionDataFormatVersion
     *            the transactionDataFormatVersion to set
     */
    public void setTransactionDataFormatVersion(
	    BigInteger transactionDataFormatVersion) {
	this.transactionDataFormatVersion = transactionDataFormatVersion;
    }

    /**
     * @return the transactionInputs
     */
    public BigInteger getTransactionInputs() {
	return this.transactionInputs;
    }

    /**
     * @param transactionInputs
     *            the transactionInputs to set
     */
    public void setTransactionInputs(BigInteger transactionInputs) {
	this.transactionInputs = transactionInputs;
    }

    /**
     * @return the transactionIn
     */
    public BigFastList<TransactionInput> getTransactionIn() {
	return this.transactionIn;
    }

    /**
     * @param transactionIn
     *            the transactionIn to set
     */
    public void setTransactionIn(BigFastList<TransactionInput> transactionIn) {
	this.transactionIn = transactionIn;
    }

    /**
     * @return the transactionOutputs
     */
    public BigInteger getTransactionOutputs() {
	return this.transactionOutputs;
    }

    /**
     * @param transactionOutputs
     *            the transactionOutputs to set
     */
    public void setTransactionOutputs(BigInteger transactionOutputs) {
	this.transactionOutputs = transactionOutputs;
    }

    /**
     * @return the transactionOut
     */
    public BigFastList<TransactionOutput> getTransactionOut() {
	return this.transactionOut;
    }

    /**
     * @param transactionOut
     *            the transactionOut to set
     */
    public void setTransactionOut(
	    BigFastList<TransactionOutput> transactionOut) {
	this.transactionOut = transactionOut;
    }

    /**
     * @return the lockTime
     */
    public BigInteger getLockTime() {
	return this.lockTime;
    }

    /**
     * @param lockTime
     *            the lockTime to set
     */
    public void setLockTime(BigInteger lockTime) {
	this.lockTime = lockTime;
    }

}
