package ee.twchallenge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.exception.AuthenticationException;
import com.stripe.model.Token;


public class MainActivity extends Activity {
	
	private Button okButton;
	private EditText name, amount;
	private TextView testData;
	
	private static final String myUserId = "Andreas";
	private String nameValue;
	private Double amountValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        okButton = (Button) findViewById(R.id.okButton);
        name = (EditText) findViewById(R.id.userNameField);
        amount = (EditText) findViewById(R.id.amountField);
        testData = (TextView) findViewById(R.id.testDataField);
        
        testData.setVisibility(TextView.INVISIBLE);
        
        okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nameValue = name.getText().toString();
				try {
					amountValue = Double.valueOf(amount.getText().toString());
				} catch (NumberFormatException nfe) {
					Toast.makeText(getApplicationContext(), "Amount is not a valid number.", Toast.LENGTH_SHORT).show();
					return;
				}
				testData.setText("myUserId=" + myUserId + "&nameValue=" + nameValue + "&amountValue=" + amountValue);
				testData.setVisibility(TextView.VISIBLE);

		        new DownloadFilesTask().execute();
			}
		});
        
        Card card = new Card("4242424242424242", 12, 2016, "123");
        
        Stripe stripe = null;
		try {
			stripe = new Stripe("pk_test_6pRNASCoBOKtIshFeQd4XMUh");
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        stripe.createToken(
            card,
            new TokenCallback() {
                public void onSuccess(Token token) {
                    // Send token to your server
//                	Toast.makeText(getApplicationContext(), "JEEE", Toast.LENGTH_LONG).show();
                }
                public void onError(Exception error) {
                    // Show localized error message
                	Log.i("ERROR", error.toString());
//                    Toast.makeText(getApplicationContext(),
//                      error.toString(),
//                      Toast.LENGTH_LONG
//                    ).show();
                }
				@Override
				public void onSuccess(com.stripe.android.model.Token token) {
					// TODO Auto-generated method stub
					Log.i("ERROR", token.toString());
//					Toast.makeText(getApplicationContext(), token.toString(), Toast.LENGTH_LONG).show();
				}
            }
        );
        
    }

    
    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
        	HttpURLConnection connection;
            OutputStreamWriter request = null;

                 URL url = null;   
                 String response = null;         
                 String parameters = "name="+nameValue+"&user="+myUserId+"&amount=" + amountValue;
                 
             	Log.i("INFO", "üks");


                 try
                 {
                     url = new URL("http://endel.mt.ut.ee/getbill.php");
                     connection = (HttpURLConnection) url.openConnection();
                 	Log.i("INFO", "kaks");
                     connection.setDoOutput(true);
                     connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                     connection.setRequestMethod("POST");    

                     request = new OutputStreamWriter(connection.getOutputStream());
                     request.write(parameters);
                     request.flush();
                     request.close();            
                     String line = "";               
                     InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                     BufferedReader reader = new BufferedReader(isr);
                     StringBuilder sb = new StringBuilder();
                     while ((line = reader.readLine()) != null)
                     {
                         sb.append(line + "\n");
                     }
                     // Response from server after login process will be stored in response variable.                
                     response = sb.toString();
                     // You can perform UI operations here
                     Log.i("RESULT", response);
                     isr.close();
                     reader.close();

                 }
                 catch(Exception e)
                 {
                     // Error
                 	Log.i("ERROR", e.toString());
                 }
				return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
