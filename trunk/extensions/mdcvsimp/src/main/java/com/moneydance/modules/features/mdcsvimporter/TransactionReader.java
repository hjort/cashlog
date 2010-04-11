/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.moneydance.modules.features.mdcsvimporter;

import com.moneydance.apps.md.model.Account;
import com.moneydance.apps.md.model.CurrencyType;
import com.moneydance.apps.md.model.OnlineTxn;
import com.moneydance.apps.md.model.OnlineTxnList;
import com.moneydance.modules.features.mdcsvimporter.formats.CashLogReader;
import com.moneydance.modules.features.mdcsvimporter.formats.CitiBankCanadaReader;
import com.moneydance.modules.features.mdcsvimporter.formats.INGNetherlandsReader;
import com.moneydance.modules.features.mdcsvimporter.formats.SimpleCreditDebitReader;
import com.moneydance.modules.features.mdcsvimporter.formats.WellsFargoReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author miki
 */
public abstract class TransactionReader {

	private static CitiBankCanadaReader citiBankCanadaReader = new CitiBankCanadaReader();
	private static INGNetherlandsReader ingNetherlandsReader = new INGNetherlandsReader();
	private static SimpleCreditDebitReader simpleCreditDebitReader = new SimpleCreditDebitReader();
	private static WellsFargoReader wellsFargoReader = new WellsFargoReader();
	private static CashLogReader cashLogReader = new CashLogReader();

	protected CSVData reader;
	protected Account account;
	protected OnlineTxnList transactionList;
	protected CurrencyType currency;

	protected abstract boolean canParse(CSVData data);

	protected abstract boolean parseNext(OnlineTxn txn) throws IOException;

	public abstract String getFormatName();

	public abstract String[] getSupportedDateFormats();

	public abstract String getDateFormat();

	public abstract void setDateFormat(String format);

	protected final void throwException(String message) throws IOException {
		throw new IOException(message);
	}

	public final void parse(CSVData reader, Account account) throws IOException {
		
		this.reader = reader;
		this.account = account;
		this.transactionList = account.getDownloadedTxns();
		this.currency = account.getCurrencyType();

		reader.reset();
		
		// skip the header
		if (haveHeader()) {
			reader.nextLine();
		}

		while (reader.nextLine()) {
			OnlineTxn txn = transactionList.newTxn();
			if (parseNext(txn)) {
				txn.setProtocolType(OnlineTxn.PROTO_TYPE_OFX);
				if (account.balanceIsNegated()) {
					txn.setAmount(-txn.getAmount());
					txn.setTotalAmount(-txn.getAmount());
				}
				transactionList.addNewTxn(txn);
			}
		}
	}

	public static TransactionReader[] getCompatibleReaders(CSVData data) {
		
		ArrayList<TransactionReader> formats = new ArrayList<TransactionReader>();

		if (citiBankCanadaReader.canParse(data)) {
			formats.add(citiBankCanadaReader);
		}

		if (ingNetherlandsReader.canParse(data)) {
			formats.add(ingNetherlandsReader);
		}

		if (simpleCreditDebitReader.canParse(data)) {
			formats.add(simpleCreditDebitReader);
		}

		if (wellsFargoReader.canParse(data)) {
			formats.add(wellsFargoReader);
		}
		
		if (cashLogReader.canParse(data)) {
			formats.add(cashLogReader);
		}

		TransactionReader[] retVal = new TransactionReader[formats.size()];
		formats.toArray(retVal);
		return retVal;
	}

	@Override
	public String toString() {
		return getFormatName();
	}

	protected abstract boolean haveHeader();

}
