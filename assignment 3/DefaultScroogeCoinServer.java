package hw3;

import java.security.*;
import java.util.*;

//Scrooge creates coins by adding outputs to a transaction to his public key.
//In ScroogeCoin, Scrooge can create as many coins as he wants.
//No one else can create a coin.
//A user owns a coin if a coin is transfer to him from its current owner
public class DefaultScroogeCoinServer implements ScroogeCoinServer {

	private KeyPair scroogeKeyPair;
	private ArrayList<Transaction> ledger = new ArrayList<Transaction>();

	//Set scrooge's key pair
	@Override
	public synchronized void init(KeyPair scrooge) {
		this.scroogeKeyPair = scrooge;
//		throw new RuntimeException();
	}

	//For every 10 minute epoch, this method is called with an unordered list of proposed transactions
	// 		submitted during this epoch.
	//This method goes through the list, checking each transaction for correctness, and accepts as
	// 		many transactions as it can in a "best-effort" manner, but it does not necessarily return
	// 		the maximum number possible.
	//If the method does not accept an valid transaction, the user must try to submit the transaction
	// 		again during the next epoch.
	//Returns a list of hash pointers to transactions accepted for this epoch

	public synchronized List<HashPointer> epochHandler(List<Transaction> txs)  {
	    List<HashPointer> list = new ArrayList<HashPointer>();
		while(!txs.isEmpty()){
			List<Transaction> temp = new ArrayList<Transaction>();
			for(Transaction ts:txs){
				if(isValid(ts)){
					ledger.add(ts);
					HashPointer pointer = new HashPointer(ts.getHash(),ledger.size()-1);
					list.add(pointer);
				}else{
					temp.add(ts);
				}
			}
			if(temp.size() == txs.size()) break;
			txs = temp;
		}
		return list;		
//		throw new RuntimeException();
	}

	//Returns true if and only if transaction tx meets the following conditions:
	//CreateCoin transaction
	//	(1) no inputs
	//	(2) all outputs are given to Scrooge's public key
	//	(3) all of tx’s output values are positive
	//	(4) Scrooge's signature of the transaction is included

	//PayCoin transaction
	//	(1) all inputs claimed by tx are in the current unspent (i.e. in getUTOXs()),
	//	(2) the signatures on each input of tx are valid,
	//	(3) no UTXO is claimed multiple times by tx,
	//	(4) all of tx’s output values are positive, and
	//	(5) the sum of tx’s input values is equal to the sum of its output values;
	@Override
	public synchronized boolean isValid(Transaction tx) {
		switch (tx.getType()){
		case Create:
            //	(1) no inputs
			if(tx.numInputs()!=0)
				return false; 
			for(Transaction.Output op: tx.getOutputs()){
				//	(2) all outputs are given to Scrooge's public key
				if(op.getPublicKey()!=scroogeKeyPair.getPublic())
					return false;
			    //	(3) all of tx’s output values are positive
				if(op.getValue()<=0)
					return false;
			}
			
            //	(4) Scrooge's signature of the transaction is included
			try {
				Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
				signature.initVerify(scroogeKeyPair.getPublic());
				signature.update(tx.getRawBytes());
				
				if(!signature.verify(tx.getSignature()))
					return false;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
			
		case Pay:
			Set<UTXO> seenUTXO =getUTXOs();
			double sumInputs = 0; // input sum
			double sumOutputs = 0; // output sum
		
			for(int i=0; i< tx.numInputs(); i++) {
				Transaction.Input input =tx.getInputs().get(i);
				int indexofledger=getLedgerIndex(seenUTXO, input);
				if (indexofledger == -1)
						return false;
		
				
				Transaction.Output inandout = ledger.get(indexofledger).getOutput(input.getIndexOfTxOutput());
				sumInputs += inandout.getValue();
				PublicKey pk = inandout.getPublicKey();
				
				try{
					Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
					signature.initVerify(pk);
					signature.update(tx.getRawDataToSign(i));
					if (!signature.verify(input.getSignature())) 
						return false;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			for(Transaction.Output output: tx.getOutputs()){
	            if(output.getValue() < 0)
	                return false;
	            sumOutputs += output.getValue();
	              
	        }
			if(Math.abs(sumInputs - sumOutputs) < .000001){
				return true;
			} else {
				return false;
			}
		}
			return false;
		
//		throw new RuntimeException();
	
	}
	
	private int getLedgerIndex( Set<UTXO> seenUTXO,  Transaction.Input input) {
		for(int i=0; i<ledger.size();i++){
			if(Arrays.equals(ledger.get(i).getHash(), input.getHashOfOutputTx())){
				HashPointer hp = new HashPointer(input.getHashOfOutputTx(),i);
				UTXO checkUTXO = new UTXO(hp,input.getIndexOfTxOutput());
				if(seenUTXO.contains(checkUTXO))
					return i;
			}
		}		
		return -1;
	}

	//Returns the complete set of currently unspent transaction outputs on the ledger
	@Override
	public synchronized Set<UTXO> getUTXOs() {
		Set<UTXO> UTXO = new HashSet<UTXO>();
		for( int i=0; i<ledger.size();i++){
			Transaction tx = ledger.get(i);
			switch(tx.getType()){
			case Create:
				for ( Transaction.Output create: tx.getOutputs()){
					HashPointer createhp = new HashPointer(tx.getHash(),i);
					UTXO createUTXO = new UTXO(createhp,tx.getIndex(create));
					UTXO.add(createUTXO);
				}
				break;
				
			case Pay:
				for(int j=0; j< tx.numInputs(); j++) {
					Transaction.Input input =tx.getInputs().get(j);
					HashPointer inhp = new HashPointer(input.getHashOfOutputTx(), getLedgerIndex(UTXO, input));		
					UTXO inUTXO = new UTXO(inhp,input.getIndexOfTxOutput());
					UTXO.remove(inUTXO);
				}
				
				for(Transaction.Output output: tx.getOutputs()){
					int index = tx.getIndex(output);
					HashPointer outhp = new HashPointer(tx.getHash(),i);
					UTXO outUTXO = new UTXO(outhp,index);
					UTXO.add(outUTXO);
				}
				break;
			}	
		}
		return UTXO;
//		throw new RuntimeException();
	}
}
