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

import com.moneydance.apps.md.model.OnlineTxn;
import com.moneydance.modules.features.mdcsvimporter.CSVData;
import com.moneydance.modules.features.mdcsvimporter.TransactionReader;
import com.moneydance.util.CustomDateFormat;
import com.moneydance.util.StringUtils;
import java.io.IOException;

/**
 * @author miki
 */
public class WellsFargoReader extends TransactionReader {

	private static final String DATE_FORMAT = "MM/DD/YYYY";
	private static final String[] SUPPORTED_DATE_FORMATS = { DATE_FORMAT };

	private CustomDateFormat dateFormat = new CustomDateFormat(DATE_FORMAT);

	@Override
	public boolean canParse(CSVData data) {
		
		data.reset();

		boolean retVal = true;

		while (retVal && data.nextLine()) {

			// skip empty lines
			if (!data.nextField()) {
				continue;
			}

			String date = data.getField();
			if (!date.equals(dateFormat.format(dateFormat.parseInt(data
					.getField())))) {
				retVal = false;
				break;
			}

			if (!data.nextField()) {
				retVal = false;
				break;
			}
			try {
				StringUtils.parseDoubleWithException(data.getField(), '.');
			} catch (Exception x) {
				retVal = false;
				break;
			}

			if (!data.nextField() || !data.getField().equals("*")) {
				retVal = false;
				break;
			}

			if (!data.nextField() || !data.nextField() || data.nextField()) {
				retVal = false;
				break;
			}
		}

		return retVal;
	}

	@Override
	public String getFormatName() {
		return "Wells Fargo";
	}

	@Override
	protected boolean parseNext(OnlineTxn txn) throws IOException {

		reader.nextField();
		String dateString = reader.getField();

		// skip empty lines
		if (dateString == null || dateString.length() == 0) {
			return false;
		}

		reader.nextField();
		String amountString = reader.getField();

		// skip '*'
		reader.nextField();

		// skip unknown number
		reader.nextField();

		reader.nextField();
		String description = reader.getField();

		long amount = 0;
		try {
			double amountDouble;
			amountDouble = StringUtils.parseDoubleWithException(amountString,
					'.');
			amount = currency.getLongValue(amountDouble);
		} catch (Exception x) {
			throwException("Invalid amount.");
		}

		int date = dateFormat.parseInt(dateString);

		txn.setAmount(amount);
		txn.setTotalAmount(amount);
		txn.setMemo(description);
		txn.setFITxnId(date + ":" + currency.format(amount, '.') + ":"
				+ description);
		txn.setDatePostedInt(date);
		txn.setDateInitiatedInt(date);
		txn.setDateAvailableInt(date);

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
		return false;
	}

}
