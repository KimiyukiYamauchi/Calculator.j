package sample.application.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CalculatorActivity extends Activity {

	String strTemp = "";
	String strResult = "0";
	int operator = 0;

	public void numKeyOnClick(View v) {

		((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(50);

		TextView sp = (TextView) findViewById(R.id.subPanel);
		String strSp = sp.getText().toString();
		if (strSp.indexOf("=") == sp.length() - 1) {
			sp.setText("");
		}

		String strInKey = (String) ((Button) v).getText();

		if (strInKey.equals(".")) {
			if (strTemp.length() == 0) {
				strTemp = "0.";
			} else {
				if (strTemp.indexOf(".") == -1) {
					strTemp = strTemp + ".";
				}
			}
		} else {
			// if (strTemp.length() <= 9) {
			strTemp = strTemp + strInKey;
			// }
		}

		showNumber(strTemp);
	}

	private void showNumber(String strNum) {

		DecimalFormat form = new DecimalFormat("#,##0");
		Log.d(ACTIVITY_SERVICE,
				"MaximumIntegerDigits = " + form.getMaximumIntegerDigits());
		String strDecimal = "";
		String strInt = "";
		String fText = "";

		if (strNum.length() > 0) {
			int decimalPoint = strNum.indexOf(".");
			if (decimalPoint > -1) {
				strDecimal = strNum.substring(decimalPoint);
				strInt = strNum.substring(0, decimalPoint);
			} else {
				// strInt = strTemp;
				strInt = strNum;
			}
			fText = form.format(Double.parseDouble(strInt)) + strDecimal;
		} else {
			fText = "0";
		}

		Log.d(getLocalClassName(), "showNumber strNum = " + strNum);
		Log.d(getLocalClassName(), "showNumber fText = " + fText);
		((TextView) findViewById(R.id.displayPanel)).setText(fText);
	}

	public void functionKeyOnClick(View v) {

		((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(50);

		switch (v.getId()) {
		case R.id.keypadAC:
			strTemp = "";
			strResult = "0";
			operator = 0;
			((TextView) findViewById(R.id.subPanel)).setText("");
			break;
		case R.id.keypadC:
			strTemp = "";
			break;
		case R.id.keypadBS:
			if (strTemp.length() == 0) {
				return;
			} else {
				strTemp = strTemp.substring(0, strTemp.length() - 1);
			}
			break;
		case R.id.keypadCopy:
			ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			cm.setText(((TextView) findViewById(R.id.displayPanel)).getText());
			return;
		}
		showNumber(strTemp);
	}

	public void operatorKeyOnClick(View v) {

		((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(50);

		TextView sp = (TextView) findViewById(R.id.subPanel);
		String op2 = ((Button) findViewById(v.getId())).getText().toString();

		if (operator != 0) {
			String op1 = ((Button) findViewById(operator)).getText().toString();
			if (strTemp.length() > 0) {
				sp.setText(strResult + op1 + strTemp + op2);
				strResult = doCalc();
				showNumber(strResult);
			} else {
				sp.setText(strResult + op2);
			}
		} else {
			if (strTemp.length() > 0) {
				strResult = strTemp;
			}
			sp.setText(strResult + op2);
		}

		strTemp = "";

		if (v.getId() == R.id.keypadEq) {
			operator = 0;
		} else {
			operator = v.getId();
		}
	}

	private String doCalc() {
		BigDecimal bd1 = new BigDecimal(strResult);
		BigDecimal bd2 = new BigDecimal(strTemp);
		BigDecimal result = BigDecimal.ZERO;

		Log.d(getLocalClassName(), "doCalc() strResult = " + strResult);
		Log.d(getLocalClassName(), "doCalc() strTemp = " + strTemp);
		Log.d(getLocalClassName(), "doCalc() operator = " + operator);

		switch (operator) {
		case R.id.keypadAdd:
			result = bd1.add(bd2);
			break;
		case R.id.keypadSub:
			result = bd1.subtract(bd2);
			break;
		case R.id.keypadMulti:
			result = bd1.multiply(bd2);
			break;
		case R.id.keypadDiv:
			if (!bd2.equals(BigDecimal.ZERO)) {
				result = bd1.divide(bd2, 12, RoundingMode.FLOOR);
			} else {
				Toast toast = Toast.makeText(this, R.string.toast_div_by_zero,
						1000);
				toast.show();
			}
			break;
		}

		Log.d(getLocalClassName(), "doCalc() result = " + result);

		if (result.toString().indexOf(".") > 0) {
			return result.toString().replace("\\.0+$|0+$", "");
		} else {
			return result.toString();
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		readPreferences();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	void writePreferences() {
		SharedPreferences prefs = getSharedPreferences("CalcPrefs",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("strTemp", strTemp);
		editor.putString("strResult", strResult);
		editor.putInt("operator", operator);
		editor.putString("strDisplay",
				((TextView) findViewById(R.id.displayPanel)).getText()
						.toString());
		editor.putString("strSubDisplay",
				((TextView) findViewById(R.id.subPanel)).getText().toString());
		editor.commit();
	}

	void readPreferences() {
		SharedPreferences prefs = getSharedPreferences("CalcPrefs",
				MODE_PRIVATE);
		strTemp = prefs.getString("strTemp", "");
		strResult = prefs.getString("strResult", "0");
		operator = prefs.getInt("operator", 0);
		((TextView) findViewById(R.id.displayPanel)).setText(prefs.getString(
				"strDisplay", "0"));
		((TextView) findViewById(R.id.subPanel)).setText(prefs.getString(
				"strSubDisplay", "0"));
	}

	@Override
	protected void onStop() {
		super.onStop();
		writePreferences();
	}
}
