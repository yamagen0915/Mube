package com.gen.mube;

import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gen.mube.ImageDownloadTask.OnImageDownloadListener;
import com.gen.mube.YouTubeSearchTask.YouTubeSearchListener;
import com.gen.mube.utils.Utils;
import com.gen.mube.utils.YouTubeUtils;
import com.gen.mube.utils.YouTubeUtils.YouTubeItem;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class MovieListFragment extends ListFragment {
	
	public static final String SEARCH_WORD = "searchWord";
	
	public void searchMovie(String searchWord) {
		String[] searchWords = Utils.splitSearchWrods(searchWord);
		new YouTubeSearchTask()
			.setYouTubeSearchListener(onYouTubeSearch)
			.execute(searchWords);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// ListViewの背景を白くするためにこうしている。
		View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
		return view;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}
	
	private final YouTubeSearchListener onYouTubeSearch = new YouTubeSearchListener() {

		@Override
		public void onResult(List<YouTubeItem> results) {
			final MovieAdapter adapter = new MovieAdapter(getActivity(), results);
			setListAdapter(adapter);
			
			// サムネイル画像の取得
			for (final YouTubeItem item : results) {
				new ImageDownloadTask()
					.setOnImageDownloadListener(new OnImageDownloadListener() {
						@Override
						public void onDownload(Bitmap bitmap) {
							item.setThumbnail(bitmap);
							adapter.notifyDataSetChanged();
						}
					})
					.execute(item.getThumbnailUrl());
			}
		}

	};
	
	private class MovieAdapter extends ArrayAdapter<YouTubeItem> {
		
		private LayoutInflater inflater;
		
		public MovieAdapter(Context context, List<YouTubeItem> objects) {
			super(context, 0, 0, objects);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.layout_movie_list, null);
			}
			
			final YouTubeItem item = getItem(position);
			
			ImageView imageThumbnailView = (ImageView) convertView.findViewById(R.id.imageMovieListThumbnail);
			TextView textTitleView 		 = (TextView) convertView.findViewById(R.id.textMovieListTitle);
			
			imageThumbnailView.setImageBitmap(item.getThumbnail());
			textTitleView.setText(item.getTitle());
			
			ImageView imageExport = (ImageView) convertView.findViewById(R.id.imageExport);
			imageExport.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MainActivity activity = (MainActivity) getActivity();
					activity.showNfcWriteDialog(item);
				}
				
			});
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 選択された行のアイテムを取得
					YouTubeItem item = (YouTubeItem) getListAdapter().getItem(position);
					
					Intent intent = YouTubeStandalonePlayer.createVideoIntent(
							/* context		= */ getActivity(),
							/* apiKey  		= */ YouTubeUtils.API_KEY, 
							/* videoId 		= */ item.getVideoId(),
							/* timeInMills 	= */ 0,
							/* autoPlay		= */ true, 
							/* lightBoxMode = */ true);
					startActivity(intent);
				}
			});
			
			return convertView;
		}
	}

}
