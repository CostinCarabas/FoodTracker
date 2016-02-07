package com.example.foodtracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    SharedPreferences data;
    public static String filename = "register_file";
	ArrayList<String> mobileArray = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();

		final TextView textAddFood = (TextView) findViewById(R.id.editInputFood);
		
		Button buttonAddFood = (Button) findViewById(R.id.ButtonAddFood);
		Button buttonHistory = (Button) findViewById(R.id.ButtonHistory);
		ListView listCurrentFood = (ListView) findViewById(R.id.listCurrentFood);

		mobileArray.add("Cartofi");
		mobileArray.add("Snitel cu piure");
		mobileArray.add("Pizza");
		mobileArray.add("Salata");
		mobileArray.add("Burger");
		mobileArray.add("Salata de fructe");
		mobileArray.add("Humus");
		mobileArray.add("Supa");


		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				mobileArray);

		listCurrentFood.setAdapter(adapter);
		listCurrentFood.setClickable(true);
		listCurrentFood
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						textAddFood.setText(((TextView) arg1).getText());
					}
				});

		buttonAddFood.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO send data to server
				Toast.makeText(getApplicationContext(), new SimpleDateFormat("HH_dd_yyyy").format(Calendar
						.getInstance().getTime()),
						Toast.LENGTH_SHORT).show();
				mobileArray.add(textAddFood.getText().toString());
				int id = getSharedPreferences(filename, 0).getInt("ID", -1);
				sendPostRequest(Integer.toString(id), textAddFood.getText().toString(),
						new SimpleDateFormat("HH_dd_yyyy").format(Calendar.getInstance().getTime()).toString());
			}
		});
		
		buttonHistory.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO send data to server
				Toast.makeText(getApplicationContext(), new SimpleDateFormat("HH_dd_yyyy").format(Calendar
						.getInstance().getTime()),
						Toast.LENGTH_SHORT).show();
				int id = getSharedPreferences(filename, 0).getInt("ID", -1);
				sendHistoryPostRequest(Integer.toString(id));

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void sendPostRequest(String givenId, String givenUsername, String givenPassword) {

		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {
				String paramId = params[0];
				String paramFood = params[1];
				String paramTime = params[2];

				System.out.println("*** doInBackground ** "
						+ "paramID: "+ paramId
						+" paramFood " + paramFood
						+" paramTime " + paramTime );

				HttpClient httpClient = new DefaultHttpClient();

				// In a POST request, we don't pass the values in the URL.
				// Therefore we use only the web page URL as the parameter of
				// the HttpPost argument
				HttpPost httpPost = new HttpPost(
						"http://10.0.2.2:8080/new_menu");

				// Because we are not passing values over the URL, we should
				// have a mechanism to pass the values that can be
				// uniquely separate by the other end.
				// To achieve that we use BasicNameValuePair
				// Things we need to pass with the POST request
				BasicNameValuePair idBasicNameValuePair = new BasicNameValuePair(
						"paramId", paramId);
				BasicNameValuePair foodBasicNameValuePair = new BasicNameValuePair(
						"paramFood", paramFood);
				BasicNameValuePair timeBasicNameValuePAir = new BasicNameValuePair(
						"paramTime", paramTime);

				// We add the content that we want to pass with the POST request
				// to as name-value pairs
				// Now we put those sending details to an ArrayList with type
				// safe of NameValuePair
				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(idBasicNameValuePair);
				nameValuePairList.add(foodBasicNameValuePair);
				nameValuePairList.add(timeBasicNameValuePAir);

				try {
					// UrlEncodedFormEntity is an entity composed of a list of
					// url-encoded pairs.
					// This is typically useful while sending an HTTP POST
					// request.
					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
							nameValuePairList);

					// setEntity() hands the entity (here it is
					// urlEncodedFormEntity) to the request.
					httpPost.setEntity(urlEncodedFormEntity);

					try {
						// HttpResponse is an interface just like HttpPost.
						// Therefore we can't initialize them
						HttpResponse httpResponse = httpClient
								.execute(httpPost);

						// According to the JAVA API, InputStream constructor do
						// nothing.
						// So we can't initialize InputStream although it is not
						// an interface
						InputStream inputStream = httpResponse.getEntity()
								.getContent();

						InputStreamReader inputStreamReader = new InputStreamReader(
								inputStream);

						BufferedReader bufferedReader = new BufferedReader(
								inputStreamReader);

						StringBuilder stringBuilder = new StringBuilder();

						String bufferedStrChunk = null;

						while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
							stringBuilder.append(bufferedStrChunk);
						}

						return stringBuilder.toString();

					} catch (ClientProtocolException cpe) {
						System.out
								.println("First Exception caz of HttpResponese :"
										+ cpe);
						cpe.printStackTrace();
					} catch (IOException ioe) {
						System.out
								.println("Second Exception caz of HttpResponse :"
										+ ioe);
						ioe.printStackTrace();
					}

				} catch (UnsupportedEncodingException uee) {
					System.out
							.println("An Exception given because of UrlEncodedFormEntity argument :"
									+ uee);
					uee.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				adapter.notifyDataSetChanged();
				System.out.println("!!!!!!!!!!!!" + result);

			}
		}

		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		sendPostReqAsyncTask.execute(givenId, givenUsername, givenPassword);
	}
	
	private void sendHistoryPostRequest(String givenId) {

		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {
				String paramId = params[0];

				HttpClient httpClient = new DefaultHttpClient();

				// In a POST request, we don't pass the values in the URL.
				// Therefore we use only the web page URL as the parameter of
				// the HttpPost argument
				HttpPost httpPost = new HttpPost(
						"http://10.0.2.2:8080/history");

				// Because we are not passing values over the URL, we should
				// have a mechanism to pass the values that can be
				// uniquely separate by the other end.
				// To achieve that we use BasicNameValuePair
				// Things we need to pass with the POST request
				BasicNameValuePair idBasicNameValuePair = new BasicNameValuePair(
						"paramId", paramId);

				// We add the content that we want to pass with the POST request
				// to as name-value pairs
				// Now we put those sending details to an ArrayList with type
				// safe of NameValuePair
				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(idBasicNameValuePair);

				try {
					// UrlEncodedFormEntity is an entity composed of a list of
					// url-encoded pairs.
					// This is typically useful while sending an HTTP POST
					// request.
					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
							nameValuePairList);

					// setEntity() hands the entity (here it is
					// urlEncodedFormEntity) to the request.
					httpPost.setEntity(urlEncodedFormEntity);

					try {
						// HttpResponse is an interface just like HttpPost.
						// Therefore we can't initialize them
						HttpResponse httpResponse = httpClient
								.execute(httpPost);

						// According to the JAVA API, InputStream constructor do
						// nothing.
						// So we can't initialize InputStream although it is not
						// an interface
						InputStream inputStream = httpResponse.getEntity()
								.getContent();

						InputStreamReader inputStreamReader = new InputStreamReader(
								inputStream);

						BufferedReader bufferedReader = new BufferedReader(
								inputStreamReader);

						StringBuilder stringBuilder = new StringBuilder();

						String bufferedStrChunk = null;

						while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
							stringBuilder.append(bufferedStrChunk);
						}

						return stringBuilder.toString();

					} catch (ClientProtocolException cpe) {
						System.out
								.println("First Exception caz of HttpResponese :"
										+ cpe);
						cpe.printStackTrace();
					} catch (IOException ioe) {
						System.out
								.println("Second Exception caz of HttpResponse :"
										+ ioe);
						ioe.printStackTrace();
					}

				} catch (UnsupportedEncodingException uee) {
					System.out
							.println("An Exception given because of UrlEncodedFormEntity argument :"
									+ uee);
					uee.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				adapter.notifyDataSetChanged();
		        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
		        intent.putExtra("history", result);
				System.out.println("!!!!!!!!!!!!IN MAIN" + result);
		        startActivity(intent); 

			}
		}

		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		sendPostReqAsyncTask.execute(givenId);
	}
}
