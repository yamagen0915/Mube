package com.gen.mube;

import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gen.mube.ImageDownloadTask.OnImageDownloadListener;
import com.gen.mube.YouTubeSearchTask.YouTubeSearchListener;
import com.gen.mube.utils.Utils;
import com.gen.mube.utils.YouTubeUtils;
import com.gen.mube.utils.YouTubeUtils.YouTubeItem;
import com.gen.mube.utils.YouTubeUtils.YouTubeParams;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class MovieListFragment extends ListFragment {
	
	public static final String SEARCH_WORD = "searchWord";
	
	private int page = 0;
	private String[] searchWords = new String[]{};
	
	private MovieAdapter adapter;
	
	private boolean isDownloading = false;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setOnScrollListener(onScroll);
		getListView().setBackground(
				getResources().getDrawable(R.color.lite_gray));
		getListView().setDivider(
				getResources().getDrawable(R.color.white));
	}
	
	public void searchMovie(String searchWord) {
		searchWords = Utils.splitSearchWrods(searchWord);
		YouTubeParams params = new YouTubeParams.Builder(searchWords)
			.build();
		
		new YouTubeSearchTask()
			.setYouTubeSearchListener(onYouTubeSearch)
			.execute(params);
	}

	private final OnScrollListener onScroll = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			// 一番したまでスクロールした訳でないなら何もしない
			if (totalItemCount != firstVisibleItem + visibleItemCount) return;
			
			// 既に追加読み込みを始めていたら何もしない
			if (isDownloading) return;

			isDownloading = true;
			page++;

			YouTubeParams params = new YouTubeParams.Builder(searchWords)
				.page(page)
				.build();

			new YouTubeSearchTask()
				.setYouTubeSearchListener(onYouTubeSearch)
				.execute(params);
		}

	};
	
	private final YouTubeSearchListener onYouTubeSearch = new YouTubeSearchListener() {

		@Override
		public void onResult(List<YouTubeItem> results) {
			
			isDownloading = false;
			
			adapter = (MovieAdapter) getListAdapter();
			if (adapter == null) {
				adapter = new MovieAdapter(getActivity());
				setListAdapter(adapter);
			}
			
			// サムネイル画像の取得
			for (final YouTubeItem item : results) {
				adapter.add(item);
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
		
		public MovieAdapter(Context context) {
			super(context, 0);
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
