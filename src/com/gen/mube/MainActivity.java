package com.gen.mube;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.gen.mube.utils.NfcUtils;
import com.gen.mube.utils.Utils;
import com.gen.mube.utils.YouTubeUtils;
import com.gen.mube.utils.YouTubeUtils.YouTubeItem;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class MainActivity extends Activity {

	private NfcAdapter nfcAdapter;
	private MovieListFragment movieListFragment;
	
	private AlertDialog dialog;
	
	private NfcState nfcState = NfcState.READABLE;
	
	private HashMap<NfcState, NfcEvents> nfcEvents = new HashMap<NfcState, NfcEvents>();
	
	private static enum NfcState {
		WRITABLE, // 書き込みモード
		READABLE; // 読み込みモード
		
		// 書き込む文字列を格納する.
		private String nfcText = "";
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		nfcEvents.put(NfcState.WRITABLE, onNfcWrite);
		nfcEvents.put(NfcState.READABLE, onNfcRead);
		
		movieListFragment = new MovieListFragment();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (!NfcUtils.enableNfc(nfcAdapter)) {
			Toast.makeText(getApplicationContext(), "NFCが有効になっていません。設定からNFCを利用可能な状態にしてから再度起動してください", Toast.LENGTH_LONG).show();
			finish();
		}
		
		Intent intent 		   = new Intent(this, MainActivity.class);
		IntentFilter[] filters = new IntentFilter[]{
				new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
		};
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

		nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, NfcUtils.TECH_LIST);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		nfcAdapter.disableForegroundDispatch(this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		String action = intent.getAction();
    	if (TextUtils.isEmpty(action)) return;
    	if(!action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) return;
    	
    	// 現在の状態に応じたイベントを呼ぶ。
    	nfcEvents.get(nfcState).onDetectNfc(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String text) {
				
				movieListFragment.searchMovie(text);
				
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				
				transaction.replace(R.id.relativeMain, movieListFragment);
				transaction.addToBackStack(null);  
				transaction.commit();
				
				// 空白文字で文字を分割する。
				Utils.hideIME(MainActivity.this, searchView);
				
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0) {
				return false;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
	
	private final NfcEvents onNfcWrite  = new NfcEvents() {
		@Override
		public void onDetectNfc(Intent intent) {
			NfcUtils.writeNfcText(intent, nfcState.nfcText);

	    	nfcState = NfcState.READABLE;
	    	nfcState.nfcText = "";
	    	if (dialog != null && dialog.isShowing())
	    		dialog.dismiss();
		}
	};
	
	private final NfcEvents onNfcRead = new NfcEvents() {
		@Override
		public void onDetectNfc(Intent intent) {
			nfcState = NfcState.READABLE;
			
			String videoId = NfcUtils.getNfcText(intent);
			
			Utils.Log.d("nfcText", videoId);
			
			Intent youtubeIntent = YouTubeStandalonePlayer.createVideoIntent(MainActivity.this, YouTubeUtils.API_KEY, videoId, 0, true, true);
			startActivity(youtubeIntent);
		}
	};
	
	public void showNfcWriteDialog (YouTubeItem item) {
		dialog = new AlertDialog.Builder(MainActivity.this)
			.setTitle("書き込み")
			.setMessage("Mubeにタッチしてください")
			.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					nfcState = NfcState.READABLE;
					nfcState.nfcText = "";
				}
			})
			.show();
		dialog.setCanceledOnTouchOutside(false);
		
		Utils.Log.d("listener", "export");
		nfcState = NfcState.WRITABLE;
		nfcState.nfcText = item.getVideoId();
	}
	
	private static interface NfcEvents {
		public void onDetectNfc (Intent intent) ;
	}
}
