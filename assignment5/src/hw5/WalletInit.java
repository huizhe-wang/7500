package hw5;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class WalletInit {
    public static final String WALLETS_DIR = "/Users/dominator/Documents/7500/assignment5";

    private final WalletAppKit kit;
    private final NetworkParameters parameters;
    protected final static Logger LOGGER = LoggerFactory.getLogger(WalletInit.class);

    public WalletInit(NetworkParameters parameters, String walletName) {
        if (WALLETS_DIR == null) {
            LOGGER.info("Failed: Directory for wallet has not been set.");
            System.exit(1);
        }
        File walletDir = new File(WALLETS_DIR + File.separator + walletName);

        this.parameters = parameters;
        kit = new WalletAppKit(parameters, walletDir, walletName);

        LOGGER.info("Starting to sync blockchain. This might take a few minutes");
        kit.setAutoSave(true);
        kit.startAsync();
        kit.awaitRunning();
        kit.wallet().addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                LOGGER.info("TRANSACTION CONFIDENCE: " + tx.getHashAsString() + " " + tx.getConfidence() + " numBroadcastPeers=" + tx.getConfidence().numBroadcastPeers());
            }
        });
        kit.wallet().allowSpendingUnconfirmedTransactions();
        LOGGER.info("Synced blockchain.");
        LOGGER.info("You've got {} in your pocket", kit.wallet().getBalance().toFriendlyString());
        for (String dn : new String [] {"eligius.st", "test-insight.bitpay.com", "insight.bitpay.com"}) {
            try {
                kit.peerGroup().addAddress(InetAddress.getByName(dn));
            } catch (UnknownHostException ex) {
                LOGGER.warn("Failed to add a peer group: " + dn, ex);
            }
        }
        for (String ip : new String[] {
                "5.95.80.47",
                "5.135.158.68",
                "136.243.23.208",
                "37.233.101.121",
                "60.205.93.37",
                "192.99.15.220",
                "144.76.136.19",
                "37.97.208.110",
                "138.68.105.134",
                "178.62.149.93",
                "82.145.59.46",
                "109.255.194.6",
                "192.34.56.173",
                "188.166.174.222",
                "195.154.69.36",
                "46.101.173.25",
                "27.255.83.134",
                "47.89.54.17",
                "103.208.86.208",
                "88.198.141.189",
                "91.121.201.39",
                "51.254.45.3",
                "136.243.139.96"}) {
            try {
                kit.peerGroup().addAddress(InetAddress.getByName(ip));
            } catch (UnknownHostException ex) {
                LOGGER.warn("Failed to add a peer group: " + ip, ex);
            }
        }
        LOGGER.info("Successfully initialized/loaded wallet: " + walletDir);
        Address current = kit.wallet().currentReceiveAddress();
        LOGGER.info("Current receive: " + current.toBase58());
        LOGGER.info("All watched addresses: " + kit.wallet().getWatchedAddresses());
        for (Address addr : kit.wallet().getWatchedAddresses()) {
            LOGGER.info(addr.toBase58());
        }
    }

    public void monitor() {

        while (true) {
            LOGGER.info("PRINTING ALL TRANSASCTIONS: " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now()));
            for (WalletTransaction t : kit.wallet().getWalletTransactions()) {
                System.out.println(t.getTransaction().getHashAsString() + " " + t.getTransaction().getUpdateTime() + " " + t.getTransaction().getConfidence());
            }

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public WalletAppKit getKit() {
        return kit;
    }

    public Wallet getWallet() {
        return kit.wallet();
    }

}
