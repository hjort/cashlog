package com.moneydance.modules.features.mdcsvimporter.formats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.moneydance.apps.md.model.CurrencyTable;
import com.moneydance.apps.md.model.CurrencyType;
import com.moneydance.apps.md.model.OnlineTxn;
import com.moneydance.apps.md.model.OnlineTxnList;
import com.moneydance.apps.md.model.RootAccount;
import com.moneydance.modules.features.mdcsvimporter.CSVData;
import com.moneydance.modules.features.mdcsvimporter.CSVReader;

public class CashLogReaderTest {

	private static CashLogReader reader;
	private static String file;

	private CSVReader csvReader;
	private CSVData csvData;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		reader = new CashLogReader();
		file = CashLogReaderTest.class.getResource("/cashlog-export.csv")
				.getPath();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		reader = null;
	}

	@Before
	public void setUp() throws Exception {
		try {
			csvReader = new CSVReader(new FileReader(file));
			csvData = new CSVData(csvReader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
		csvData = null;
		csvReader.close();
		csvReader = null;
	}

	@Test
	public void testCanParse() {
		assertTrue(reader.canParse(csvData));
	}

	@Test
	public void testGetFormatName() {
		assertEquals("CashLog Application", reader.getFormatName());
	}

	@Test
	public void testHaveHeader() {

		CurrencyTable currTable = new CurrencyTable();
		CurrencyType real = new CurrencyType(5, "BRL", "Real", 1.0, 2, "R$",
				"", "", 20100410, CurrencyType.CURRTYPE_CURRENCY, currTable);

		RootAccount account = new RootAccount(real, currTable);

		try {
			reader.parse(csvData, account);
		} catch (IOException e) {
			e.printStackTrace();
		}

		OnlineTxnList onlineTxnList = account.getDownloadedTxns();
		assertEquals(4, onlineTxnList.getTxnCount());
		
		long sum = 0L;
		for (int i = 0; i < onlineTxnList.getTxnCount(); i++) {
			OnlineTxn onlineTxn = onlineTxnList.getTxn(i);
			System.out.println(onlineTxn);
//			System.out.println(onlineTxn.getAcctToAcctID());
			sum += onlineTxn.getAmount();
		}
		assertEquals(95050, sum);
		
		// assertTrue(account.isDirty());
		// account.doneLoading();
		// assertEquals(955.50, account.getBalance(), 0.01);
	}

}
