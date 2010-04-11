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
package com.moneydance.modules.features.mdcsvimporter.formats;

import com.moneydance.modules.features.mdcsvimporter.TransactionReader;
import com.moneydance.apps.md.model.OnlineTxn;
import com.moneydance.modules.features.mdcsvimporter.CSVData;
import com.moneydance.util.CustomDateFormat;
import com.moneydance.util.StringUtils;
import java.io.IOException;

public class CitiBankCanadaReader extends TransactionReader {

	private static final String TRANSACTION_DATE = "transaction date";
	private static final String POSTING_DATE = "posting date";
	private static final String DESCRIPTION = "description";
	private static final String AMOUNT = "amount";
	private static final String DATE_FORMAT = "MM/DD/YYYY";
	private static final String[] SUPPORTED_DATE_FORMATS = { DATE_FORMAT };
	
	private CustomDateFormat dateFormat = new CustomDateFormat(DATE_FORMAT);

	@Override
	public boolean canParse(CSVData data) {
		
		data.reset();

		return data.nextLine() && data.nextField()
				&& TRANSACTION_DATE.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& POSTING_DATE.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& DESCRIPTION.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& AMOUNT.equals(data.getField().toLowerCase())
				&& !data.nextField();
	}

	@Override
	public String getFormatName() {
		return "CitiBank Canada";
	}

	@Override
	protected boolean parseNext(OnlineTxn txn) throws IOException {

		// empty line
		if (!reader.nextField()) {
			return false;
		}

		String transactionDateString = reader.getField();

		// skip the footer line
		if (transactionDateString.equalsIgnoreCase("Date downloaded:")) {
			return false;
		}

		reader.nextField();
		String postingDateString = reader.getField();

		reader.nextField();
		String description = reader.getField();

		reader.nextField();
		String amountString = reader.getField();
		if (amountString == null) {
			throwException("Invalid line.");
		}

		long amount = 0;
		try {
			double amountDouble = StringUtils.parseDoubleWithException(
					amountString, '.');
			amount = currency.getLongValue(amountDouble);
		} catch (Exception x) {
			throwException("Invalid amount.");
		}

		int transactionDate = dateFormat.parseInt(transactionDateString);
		int postingDate = dateFormat.parseInt(postingDateString);

		txn.setAmount(amount);
		txn.setTotalAmount(amount);
		txn.setMemo(description);
		txn.setFITxnId(postingDate + ":" + amountString + ":" + description);
		txn.setDatePostedInt(postingDate);
		txn.setDateInitiatedInt(transactionDate);
		txn.setDateAvailableInt(postingDate);

		return true;
	}

	@Override
	public String[] getSupportedDateFormats() {
		return SUPPORTED_DATE_FORMATS;
	}

	@Override
	public String getDateFormat() {
		return DATE_FORMAT;
	}

	@Override
	public void setDateFormat(String format) {
		if (!DATE_FORMAT.equals(format)) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	@Override
	protected boolean haveHeader() {
		return true;
	}

}
