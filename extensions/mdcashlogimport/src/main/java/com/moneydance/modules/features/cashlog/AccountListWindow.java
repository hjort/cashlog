package com.moneydance.modules.features.cashlog;

import com.moneydance.awt.*;
//import com.moneydance.apps.md.controller.Common;
import com.moneydance.apps.md.model.*;
import com.moneydance.util.CustomDateFormat;
//import com.moneydance.apps.md.controller.Util;

import java.io.*;
import java.util.Date;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * Window used for Account List interface
 * ------------------------------------------------------------------------
 */

public class AccountListWindow extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private Main extension;
	
	private JTextArea accountListArea;
	private JButton clearButton;
	private JButton closeButton;
	private JButton importButton;
	private JTextField inputArea;

	public AccountListWindow(Main extension) {
		
		super("Account List Console Cash Log");
		this.extension = extension;

		accountListArea = new JTextArea();

		RootAccount root = extension.getUnprotectedContext().getRootAccount();
		StringBuffer acctStr = new StringBuffer();
		if (root != null) {
			addSubAccounts(root, acctStr);
		}
		accountListArea.setEditable(false);
		accountListArea.setText(acctStr.toString());
		inputArea = new JTextField();
		inputArea.setEditable(true);
		clearButton = new JButton("Clear");
		closeButton = new JButton("Close");
		
		importButton = new JButton("Import");

		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		p.add(new JScrollPane(accountListArea), AwtUtil.getConstraints(0, 0, 1,
				1, 4, 1, true, true));
		p.add(Box.createVerticalStrut(8), AwtUtil.getConstraints(0, 2, 0, 0, 1,
				1, false, false));
		p.add(clearButton, AwtUtil
				.getConstraints(0, 3, 1, 0, 1, 1, false, true));
		p.add(closeButton, AwtUtil
				.getConstraints(1, 3, 1, 0, 1, 1, false, true));

		p.add(importButton);
		
		getContentPane().add(p);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		enableEvents(WindowEvent.WINDOW_CLOSING);
		closeButton.addActionListener(this);
		clearButton.addActionListener(this);

		importButton.addActionListener(this);
		
		PrintStream c = new PrintStream(new ConsoleStream());

		c.append("AccountListWindow.AccountListWindow(): " + new Date());
		
		setSize(500, 400);
		AwtUtil.centerWindow(this);
	}

	public static void addSubAccounts(Account parentAcct, StringBuffer acctStr) {
		
		int sz = parentAcct.getSubAccountCount();
		
		for (int i = 0; i < sz; i++) {
			Account acct = parentAcct.getSubAccount(i);
			acctStr.append(acct.getAccountNum());
			acctStr.append(": ");
			acctStr.append(acct.getFullAccountName());
			acctStr.append(": ");
			acctStr.append(acct.getBalance());
			acctStr.append("\n");
			addSubAccounts(acct, acctStr);
		}
	}

	public void actionPerformed(ActionEvent evt) {
		
		Object src = evt.getSource();
		
		if (src == closeButton) {
			extension.closeConsole();
		} else if (src == clearButton) {
			accountListArea.setText("");
		} else if (src == importButton) {
			importTransactions();
		}
	}

	public final void processEvent(AWTEvent evt) {
		
		if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
			extension.closeConsole();
			return;
		}
		
		if (evt.getID() == WindowEvent.WINDOW_OPENED) {
		}
		
		super.processEvent(evt);
	}

	private class ConsoleStream extends OutputStream implements Runnable {
		
		public void write(int b) throws IOException {
			accountListArea.append(String.valueOf((char) b));
			repaint();
		}

		public void write(byte[] b) throws IOException {
			accountListArea.append(new String(b));
			repaint();
		}

		public void run() {
			accountListArea.repaint();
		}
	}

	void goAway() {
		setVisible(false);
		dispose();
	}
	
	private static final String DATE_FORMAT = "MM/DD/YYYY";
	private CustomDateFormat dateFormat = new CustomDateFormat(DATE_FORMAT);

	private void importTransactions() {
		
		/*
		Transaction Date,Account ID,Description,Amount
		"04/01/2010",91,"Selling Bonus","1050.75"
		"04/02/2010",22,"Savana Grill","-30.25"
		"04/02/2010",,"Tip","-5.00"
		"04/03/2010",41,"Oil Change","-65.00"
		Summary = Count: 4, Sum: 950.50
		*/
		
		createTransaction("04/15/2010", 91, "Selling Bonus at " + new Date(), 1050.75, null);
		createTransaction("04/16/2010", 54, "Savana Grill at " + new Date(), -30.25, null);
		createTransaction("04/16/2010", -1, "Tip at " + new Date(), -5.00, null);
		createTransaction("04/17/2010", 40, "Oil Change at " + new Date(), -65.00, null);
		
		RootAccount rootAccount = extension.getUnprotectedContext().getRootAccount();
		rootAccount.refreshAccountBalances();
		
		/*
		RootAccount root = extension.getUnprotectedContext().getRootAccount();

		Account acc;
		OnlineTxnList list;
		OnlineTxn tx;
		
		acc = root.getAccountById(91);
		list = acc.getDownloadedTxns();
		
		int txDate = dateFormat.parseInt("04/17/2010");

		tx = list.newTxn();
		tx.setAmount(105075);
		tx.setTotalAmount(105075);
		tx.setMemo("Selling Bonus " + System.currentTimeMillis());
		tx.setFITxnId(txDate + ":" + System.currentTimeMillis());
		tx.setDatePostedInt(txDate);
		tx.setDateInitiatedInt(txDate);
		tx.setDateAvailableInt(txDate);
		tx.setProtocolType(OnlineTxn.PROTO_TYPE_OFX);

		list.addNewTxn(tx);

		acc.downloadedTxnsUpdated();
		root.refreshAccountBalances();
		
		System.err.println("acc: " + acc);
		System.err.println("list: " + list);
		System.err.println("tx: " + tx);
		*/
	}
	
	private void createTransaction(String date, int accountId, String payeeName, double amount, String checkNumber) {
		
		RootAccount rootAccount = extension.getUnprotectedContext().getRootAccount();
		Account internalAccount = (accountId >= 0 ? rootAccount.getAccountById(accountId) : rootAccount);
		
		try {
			
			long amountInPennies = (long) (amount * 100);
			int txDate = dateFormat.parseInt(date);
			
			if (amountInPennies < 0) amountInPennies = -amountInPennies;
			
			System.out.println("internalAccount.balanceIsNegated() = " + internalAccount.balanceIsNegated());
			System.out.println("amountInPennies = " + amountInPennies);
			
			ParentTxn parentTxn = new ParentTxn(txDate, txDate, txDate, checkNumber,
					internalAccount, payeeName, "memo: " + payeeName, -1, AbstractTxn.STATUS_UNRECONCILED);
			SplitTxn splitTxn = new SplitTxn(parentTxn, amountInPennies, amountInPennies, 1.0,
					internalAccount, parentTxn.getDescription(), -1, AbstractTxn.STATUS_UNRECONCILED);
			parentTxn.addSplit(splitTxn);
			
			System.out.println("parentTxn = " + parentTxn);
			System.out.println("splitTxn = " + splitTxn);
			
			rootAccount.getTransactionSet().addNewTxn(parentTxn);
			
			rootAccount.accountBalanceChanged(internalAccount);
			
//			rootAccount.accountModified(internalAccount);
//			rootAccount.refreshAccountBalances();
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

//	void importTx(int accId, )
	
}
