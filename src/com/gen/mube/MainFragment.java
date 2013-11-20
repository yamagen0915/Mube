package com.gen.mube;

import com.gen.mube.utils.DisplayUtils;
import com.gen.mube.utils.Utils;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
		
		Button btnSearch = (Button) view.findViewById(R.id.btnMainSearch);
		btnSearch.setOnClickListener(onSearchBtnClick);
		
		editSearch = (EditText) view.findViewById(R.id.editMainSearchMovie);
		
		return view;
	}
	
	private final OnClickListener onSearchBtnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String searchWord = editSearch.getText().toString();
			editSearch.clearFocus();
			
			MovieListFragment fragment = new MovieListFragment();
			fragment.searchMovie(searchWord);
			
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.add(R.id.relativeMain, fragment);
			transaction.addToBackStack(null);  
			transaction.commit();
		}
		
	};

}
