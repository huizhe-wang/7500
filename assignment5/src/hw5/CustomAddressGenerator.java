package hw5;

import org.slf4j.Logger;

import java.text.NumberFormat;
import java.util.Locale;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;

public class CustomAddressGenerator {
	private static final Logger log = LoggerFactory.getLogger(CustomAddressGenerator.class);
    private static final NetworkParameters netParams = MainNetParams.get();
    private static final int BTC_ADDRESS_MAX_LENGTH = 35;
    private static long attempts;

	/*  @param prefix	string of letters
	 *  @returns 		key whose Bitcoin address on mainnet starts with 1 followed prefix.
     * Verifies that the requested phrase represents a valid bitcoin address substring
     *
     * @param substring the requested phrase
     * @return true if the requested phrase is a valid bitcoin address substring
     */
    
    private static boolean isValidBTCAddressSubstring(final String substring) {
        boolean validity = true;

        if (!CharMatcher.JAVA_LETTER_OR_DIGIT.matchesAllOf(substring) ||
                substring.length() > BTC_ADDRESS_MAX_LENGTH ||
                CharMatcher.anyOf("OIl0").matchesAnyOf(substring)) {
            validity = false;
        }

        return validity;
    }
    
	public static ECKey get(String prefix) {
		if (isValidBTCAddressSubstring(prefix)) {
		ECKey key;

        do {
            key = new ECKey();
            attempts++;
           }  while (!(key.toAddress(netParams).toString().startsWith(prefix)));

        log.debug("Attempts made: " + NumberFormat.getNumberInstance(Locale.US).format(attempts));
        return key;
	}else{
		log.debug("Invalid number of arguments. Usage: Vanitas [phrase]");
        return null;
	}
	}
	
	public static void main(String args[]){
		ECKey key = get("1WANG");
		System.out.println("Address: " + key.toAddress(netParams));
		System.out.println("Private Key: " + key.getPrivKey());	
		}
}

