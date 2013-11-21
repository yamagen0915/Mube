package com.gen.mube;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.gen.mube.utils.DisplayUtils;
import com.gen.mube.utils.Utils;

public class MainFragment extends Fragment {
	
	private EditText editSearch;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_main, null);
		
		ImageView imageLogo = (ImageView) view.findViewById(R.id.imageLogo);
		Bitmap bitmapLogo   = Utils.resizeBitmapFitWidth(
				/* with = */ DisplayUtils.mesureDisplayW(getActivity()),
				/* src  = */ BitmapFactory.decodeResource(getResources(), R.drawable.mube_log_with_title));
		imageLogo.setImageBitmap(bitmapLogo);
		
		editSearch = (EditText) view.findViewById(R.id.editMainSearchMovie);
		editSearch.setOnKeyListener(onSearchEnterListener);
		
		return view;
	}
	
	private final OnKeyListener onSearchEnterListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			
			if (keyCode != KeyEvent.KEYCODE_ENTER) return false;
			
			String searchWord = editSearch.getText().toString();
			editSearch.clearFocus();
			
			MovieListFragment fragment = new MovieListFragment();
			fragment.searchMovie(searchWord);
			
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.relativeMain, fragment);
			transaction.addToBackStack(null);  
			transaction.commit();
			
			return false;
		}
	};
	
}
