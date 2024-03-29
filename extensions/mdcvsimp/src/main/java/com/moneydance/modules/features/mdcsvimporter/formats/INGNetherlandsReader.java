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

public class INGNetherlandsReader extends TransactionReader {

	private static final String DATUM = "datum";
	private static final String NAAM_OMSCHRIJVING = "naam / omschrijving";
	private static final String REKENING = "rekening";
	private static final String TEGENREKENING = "tegenrekening";
	private static final String CODE = "code";
	private static final String AF_BIJ = "af bij";
	private static final String BEDRAG_EUR = "bedrag (eur)";
	private static final String MUTATIESORT = "mutatiesoort";
	private static final String MEDEDELINGEN = "mededelingen";
	private static final String DATE_FORMAT = "D-M-YYYY";
	private static final String[] SUPPORTED_DATE_FORMATS = { DATE_FORMAT };

	private CustomDateFormat dateFormat = new CustomDateFormat(DATE_FORMAT);

	@Override
	public boolean canParse(CSVData data) {

		data.reset();

		return data.nextLine() && data.nextField()
				&& DATUM.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& NAAM_OMSCHRIJVING.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& REKENING.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& TEGENREKENING.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& CODE.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& AF_BIJ.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& BEDRAG_EUR.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& MUTATIESORT.equals(data.getField().toLowerCase())
				&& data.nextField()
				&& MEDEDELINGEN.equals(data.getField().toLowerCase())
				&& !data.nextField();
	}

	@Override
	public String getFormatName() {
		return "ING The Netherlands";
	}

	@Override
	protected boolean parseNext(OnlineTxn txn) throws IOException {

		// empty line
		if (!reader.nextField()) {
			return false;
		}

		String datum = reader.getField();

		reader.nextField();
		String naam = reader.getField();

		reader.nextField();
		String rekening = reader.getField();

		reader.nextField();
		String tegenrekening = reader.getField();

		reader.nextField();
		String code = reader.getField();

		reader.nextField();
		String af_bij = reader.getField();

		reader.nextField();
		String bedrag = reader.getField();

		reader.nextField();
		String mutatiesort = reader.getField();

		reader.nextField();
		String mededelingen = reader.getField();
		if (mededelingen == null) {
			throwException("Invalid line.");
		}

		long amount = 0;
		try {
			double amountDouble = StringUtils.parseDoubleWithException(bedrag,
					',');
			amount = currency.getLongValue(amountDouble);
		} catch (Exception x) {
			throwException("Invalid amount.");
		}

		if (af_bij.equalsIgnoreCase("af")) {
			amount = -amount;
		} else if (af_bij.equalsIgnoreCase("bij")) {
		} else {
			throwException("Value of Af/Bij field must be 'Af' or 'Bij'.");
		}

		int date = dateFormat.parseInt(datum);

		Integer hashCode = naam.hashCode() ^ rekening.hashCode()
				^ tegenrekening.hashCode() ^ code.hashCode()
				^ af_bij.hashCode() ^ mutatiesort.hashCode()
				^ mededelingen.hashCode();

		txn.setAmount(amount);
		txn.setTotalAmount(amount);
		txn.setMemo(mededelingen);
		txn.setFITxnId(datum + ":" + bedrag + ":" + hashCode.toString());
		txn.setDatePostedInt(date);
		txn.setDateInitiatedInt(date);
		txn.setDateAvailableInt(date);
		txn.setPayeeName(naam);

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
