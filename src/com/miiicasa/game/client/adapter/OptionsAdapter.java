package com.miiicasa.game.client.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miiitv.game.client.R;

public class OptionsAdapter extends BaseAdapter {

	private final static String TAG = "OptionsAdapter";
	private LayoutInflater inflater = null;
	private JSONObject optionsJSON = null;
	
	public class ViewHolder {
		
		public TextView answerTextView = null;
	}
	
	
	public OptionsAdapter(Context context, JSONObject optionsJSON) {
		this.optionsJSON = optionsJSON;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return optionsJSON.length();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.answer, parent, false);
			holder = new ViewHolder();
			holder.answerTextView = (TextView) convertView.findViewById(R.id.answer);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			holder.answerTextView.setText(optionsJSON.getString(String.valueOf(position + 1)));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
}
