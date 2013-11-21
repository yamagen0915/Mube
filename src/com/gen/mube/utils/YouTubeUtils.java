package com.gen.mube.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;

public class YouTubeUtils {
	
	// インスタンス化の禁止
	private YouTubeUtils () {}
	
	public static final String API_KEY = "AIzaSyDOopj-bYfvcm0xxv7CS5NyTBA56S5wles";
	
	// Data APIについては以下参照
	// https://developers.google.com/youtube/2.0/developers_guide_protocol_api_query_parameters?hl=ja
	private static final String VIDEOS_URL = "http://gdata.youtube.com/feeds/api/videos";
	
	/**
	 * 指定したワードに一致しない物を検索する場合は「-」をつける。
	 * @param searchWords
	 * @return YouTube APIのレスポンスをそのまま返す。JSON形式。
	 */
	public static String searchMovie (YouTubeParams params) {
		return sendGetRequest(VIDEOS_URL + "?" + params);
	}
	
	private static String sendGetRequest (final String urlWithParam) {
		
		Utils.Log.d("request url : "+urlWithParam);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpUriRequest request		 = new HttpGet(urlWithParam);
		try {
			HttpResponse response = httpClient.execute(request);
			if (!isHttpOk(response)) return "";
			
			return inputStreamtoString(response.getEntity().getContent());
		} catch (Exception e) {
		}
		
		return "";
	} 
	
	private static boolean isHttpOk (HttpResponse response) {
		return (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);
	}
	
	private static String inputStreamtoString (InputStream in) {
		if (in == null) return "";
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String str;
			while ((str = reader.readLine()) != null) {
				buffer.append(str);
			}
			return buffer.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * Mubeで使用するYouTubeAPIから取得したデータを扱うBeanクラス。
	 * @author yamamoto
	 *
	 */
	public static class YouTubeItem {
		
		private String videoId;
		private String title;
		private String description;
		private String author;
		private String thumbnailUrl;
		private Bitmap thumbnail;
		
		public YouTubeItem () {}
		
		public YouTubeItem(String videoId, String title, String description, String author, String thumbnailUrl) {
			this.videoId = videoId;
			this.title = title;
			this.description = description;
			this.author = author;
			this.thumbnailUrl = thumbnailUrl;
		}
		
		public String getVideoId() 		{ return videoId; }
		public String getTitle() 	 	{ return title; }
		public String getDescription() 	{ return description; }
		public String getAuthor() 		{ return author; }
		public String getThumbnailUrl() { return thumbnailUrl; }
		public Bitmap getThumbnail() 	{ return thumbnail; }
		
		public void setVideoId(String videoId) 			{ this.videoId = videoId; }
		public void setTitle(String title) 		   		{ this.title = title; }
		public void setDescription(String description) 	{ this.description = description; }
		public void setAuthor(String author) 			{ this.author = author; }
		public void setThumbnailUrl(String url)			{ this.thumbnailUrl = url; }
		public void setThumbnail(Bitmap thumbnail) 		{ this.thumbnail = thumbnail; }
	}
	
	/**
	 * YouTubeにリクエストを送るときに使うクラス。
	 * APIパラメータに対応したメンバ変数を持っている。
	 * @author yamamoto
	 *
	 */
	public static class YouTubeParams {
		
		private List<String> q;
		private String alt;
		private String orderBy;
		private int page;
		private int maxResults;
		private int version;
		
		private YouTubeParams (Builder builder) {
			this.q 		 	= builder.q;
			this.alt		= builder.alt;
			this.orderBy 	= builder.orderBy;
			this.page		= builder.page;
			this.maxResults = builder.maxResults;
			this.version	= builder.version;
		} 
		
		public List<String> getQ() { return q; }
		public String getOrderBy() { return orderBy; }
		public int getPage()       { return page; }
		public int getMaxResults() { return maxResults; }
		public int getVersion()    { return version; }
		
		@Override
		public String toString() {
			
			StringBuilder urlParams = new StringBuilder();
			urlParams.append("q").append("=").append(listToGetParams(q)).append("&");
			urlParams.append("alt").append("=").append(alt).append("&");
			urlParams.append("orderby").append("=").append(orderBy).append("&");
			
			int startIndex = (page * maxResults) + 1;
			urlParams.append("start-index").append("=").append(startIndex).append("&");
			urlParams.append("max-results").append("=").append(maxResults).append("&");
			urlParams.append("v").append("=").append(version);
			
			return urlParams.toString();
		}
		
		/**
		 * value1+value2+value3+ ・・・の形に直す
		 * @param list
		 */
		private static String listToGetParams (List<String> list) {
			StringBuilder builder = new StringBuilder();
			for (int i=0; i<list.size(); i++) {
				if (i != 0) builder.append("+");
				builder.append(list.get(i));
			}
			return builder.toString();
		}

		public static class Builder {
			
			// 必須パラメータ
			private List<String> q;
			
			// オプションパラメータ
			private String alt	   = "json";
			private String orderBy = "published";
			private int page	   = 0;
			private int maxResults = 30;
			private int version    = 2;
			
			public Builder (List<String> q) {
				this.q = q;
			}
			
			public Builder (String[] q) {
				this.q = Arrays.asList(q);
			}
			
			public Builder alt (String alt)
				{ this.alt = alt; return this; }
			public Builder orderBy (String orderBy) 	
				{ this.orderBy = orderBy; return this; }
			public Builder page (int startIndex) 
				{ this.page = startIndex; return this; }
			public Builder maxResults (int maxResults) 
				{ this.maxResults = maxResults; return this; }
			public Builder version (int version) 
				{ this.version = version; return this; }
			
			public YouTubeParams build () 
				{ return new YouTubeParams(this); }
		}
	}
	
}
