//package com.v2soft.V2AndLib.demoapp.ui.activities;
//
//import java.io.IOException;
//import java.util.List;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson.JacksonFactory;
//
///**
// * 
// * @author M.Dontsov (crocotiger@gmail.com), A.Zaitsev
// * 
// */
//public class GoogleOAuthActivity extends Activity {
//	public static final String LOG_TAG = GoogleOAuthActivity.class.getSimpleName();
//
//	public static final int ID = 105;
//	public static final String EXTRA_FIRSTNAME = "firstname";
//	public static final String EXTRA_LASTNAME = "lastname";
//	public static final String EXTRA_NICKNAME = "nickname";
//	public static final String EXTRA_SEX = "sex";
//	public static final String EXTRA_AVATAR = "avatar";
//	public static final String EXTRA_ID_SOCNET = "id_socnet";
//	public static final String EXTRA_CITY = "city";
//	public static final String EXTRA_AGE = "age";
//
////	private DemoAppSettings mSettings;
////    private ProgressDialog mWaitProgressDialog;
//
//	//Private fields for G+ connection
//	private JsonFactory jsonFactory = new JacksonFactory();
//	private HttpTransport transport = new NetHttpTransport();
//	private String mApiId, mApiSecret, mApiScope;
////	private Plus plus;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
////		mSettings = ((DemoA)getApplication()).getSettings();
////        mWaitProgressDialog = new ProgressDialog(this);
////        mWaitProgressDialog.setMessage(getResources().getString(
////                R.string.messagePleaseWait));
////        mWaitProgressDialog.show();
//		signIn();
//	}
//
//	private void signIn() {
//		WebView webView = new WebView(this);
//		setContentView(webView);
//		webView.getSettings().setJavaScriptEnabled(false);
//
//		final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//				transport,
//				jsonFactory,
//				mApiId,
//				mApiSecret,
//				mApiScope)
//		.build();
//
//		webView.setWebViewClient(new WebViewClient() {
//			@Override
//			public void onPageStarted(WebView view, String url, Bitmap favicon) {
//				if (url.startsWith(ApplicationSettings.REDIRECT_URI)) {
//					try {
//						Intent res_intent = new Intent();
//						if (url.indexOf("code=") != -1) {
//							String code = url.substring(
//									ApplicationSettings.REDIRECT_URI.length() + 7,
//									url.length());
//
//							GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
//									.setRedirectUri(ApplicationSettings.REDIRECT_URI)
//									.execute();
//							Credential userCred = flow.createAndStoreCredential(tokenResponse, null);
//
//							mSettings.setGPToken(tokenResponse);
//
//							//Load user data
//							retrieveProfile(userCred);
//							//Parse user data
//							res_intent.putExtra(EXTRA_FIRSTNAME, mProfile
//									.getName().getGivenName());
//							res_intent.putExtra(EXTRA_LASTNAME, mProfile
//									.getName().getFamilyName());
////							res_intent.putExtra(EXTRA_NICKNAME,
////									mProfile.getNickname());
//							res_intent.putExtra(EXTRA_SEX, mProfile.getGender());
//							res_intent.putExtra(EXTRA_AVATAR, mProfile
//									.getImage().getUrl());
//							res_intent.putExtra(EXTRA_ID_SOCNET, mProfile.getId());
//
//							List<PlacesLived> places = mProfile.getPlacesLived();
//							String city = "";
//							if (places != null) {
//								if (!places.isEmpty()) {
//									for (PlacesLived place : places) {
//										if (place.getPrimary()) {
//											city = place.getValue();
//											break;
//										}
//									}
//								}
//							}
//							res_intent.putExtra(EXTRA_CITY, city);
//							String bDay = mProfile.getBirthday();
//							int age = 0;
//							if (bDay!=null) age = parseAge(bDay);
//							res_intent.putExtra(EXTRA_AGE, age);
//
//							setResult(Activity.RESULT_OK, res_intent);
//							view.setVisibility(View.INVISIBLE);
//							finish();
//						} else if (url.indexOf("error=") != -1) {
//							view.setVisibility(View.INVISIBLE);
//							setResult(Activity.RESULT_CANCELED);
//							finish();
//						}
//					}
//					catch (IOException e) {
//						Log.d(LOG_TAG, e.toString(), e);
//					}
//				} else {
//					super.onPageStarted(view, url, favicon);
//				}
//			}
//
//			@Override
//			public void onPageFinished(WebView view, String url) {
//                mWaitProgressDialog.dismiss();
//			}
//		});
//		String authPage = flow.newAuthorizationUrl()
//				.setRedirectUri(ApplicationSettings.REDIRECT_URI).build();
//		Log.d(LOG_TAG, "loadUrl: "+authPage);
//		webView.loadUrl(authPage);
//	}
//}
