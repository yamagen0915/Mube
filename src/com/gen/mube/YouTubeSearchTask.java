package com.gen.mube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.gen.mube.interfaces.OnApiResultListener;
import com.gen.mube.utils.YouTubeUtils;
import com.gen.mube.utils.YouTubeUtils.YouTubeItem;
import com.google.android.youtube.player.internal.e;

public class YouTubeSearchTask extends AsyncTask<String, Void, List<YouTubeItem>>{
	
	private YouTubeSearchListener listener;
	
	@Override
	protected List<YouTubeItem> doInBackground(String... searchWords) {
		List<String> searchWordsList = Arrays.asList(searchWords);
		String jsonStr = YouTubeUtils.searchMovie(searchWordsList);
		
		try {
			JSONObject json   = new JSONObject(jsonStr);
			JSONObject feed	  = json.getJSONObject("feed");
			JSONArray entries = feed.getJSONArray("entry");
			
			// JSONデータから必要な情報を抽出する。
			List<YouTubeItem> youTubeItems = new ArrayList<YouTubeItem>();
			for (int i=0; i<entries.length(); i++) {
				JSONObject entry = entries.getJSONObject(i);
				youTubeItems.add(new YouTubeItem(
						getVideoIdFromEntry(entry), 
						getTitleFromEntry(entry), 
						getDescriptionFromEntry(entry), 
						getAuthorFromEntry(entry), 
						getThumbnailUrlFromEntry(entry)));
			}
			
			return youTubeItems;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 失敗したらとりあえず空の配列を返す。
		return new ArrayList<YouTubeItem>();
	}
	
	@Override
	protected void onPostExecute(List<YouTubeItem> results) {
		super.onPostExecute(results);
		
		if (listener != null) listener.onResult(results);
	}
	
	public YouTubeSearchTask setYouTubeSearchListener (YouTubeSearchListener listener) {
		this.listener = listener;
		return this;
	}
	
	private static String getTitleFromEntry (JSONObject entry) throws JSONException {
		
		if (entry.isNull("media$group")) return "";
		JSONObject group = entry.getJSONObject("media$group");
		
		if (group.isNull("media$title")) return "";
		JSONObject title = group.getJSONObject("media$title");
		
		if (title.isNull("$t")) return "";
		return title.getString("$t");
	}
	
	private static String getVideoIdFromEntry (JSONObject entry) throws JSONException {
		
		if (entry.isNull("media$group")) return "";
		JSONObject group = entry.getJSONObject("media$group");
		
		if (group.isNull("yt$videoid")) return "";
		JSONObject videoId = group.getJSONObject("yt$videoid");
		
		if (videoId.isNull("$t")) return "";
		return videoId.getString("$t");
	}
	
	private static String getDescriptionFromEntry (JSONObject entry) throws JSONException {
		
		if (entry.isNull("media$group")) return "";
		JSONObject group = entry.getJSONObject("media$group");
		
		if (group.isNull("media$description")) return "";
		JSONObject description = group.getJSONObject("media$description");
		
		if (description.isNull("$t")) return "";
		return description.getString("$t");
	}
	
	private static String getThumbnailUrlFromEntry (JSONObject entry) throws JSONException {
		
		if (entry.isNull("media$group")) return "";
		JSONObject group = entry.getJSONObject("media$group");
		
		if (group.isNull("media$thumbnail")) return "";
		JSONArray thumbnails = group.getJSONArray("media$thumbnail");
		
		if (thumbnails.length() <= 0) return "";
		JSONObject thumbnail = thumbnails.getJSONObject(0);
		
		if (thumbnail.isNull("url")) return "";
		
		return thumbnail.getString("url");
	}
	
	private static String getAuthorFromEntry (JSONObject entry) throws JSONException {
		
		if (entry.isNull("author")) return "";
		JSONArray authors = entry.getJSONArray("author");
		
		if (authors.length() <= 0) return "";
		JSONObject author = authors.getJSONObject(0);
		
		if (author.isNull("name")) return "";
		JSONObject name = author.getJSONObject("name");
		
		if (name.isNull("$t")) return "";
		return name.getString("$t");
	}
	
	public static interface YouTubeSearchListener {
		public void onResult (List<YouTubeItem> results);
	}
	
}
