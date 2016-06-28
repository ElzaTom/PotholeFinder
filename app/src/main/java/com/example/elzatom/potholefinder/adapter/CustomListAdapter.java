package com.example.elzatom.potholefinder.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.elzatom.potholefinder.R;
import com.example.elzatom.potholefinder.model.Pothole;

import java.util.List;



public class CustomListAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<Pothole> pothItems;


	public CustomListAdapter(Activity activity, List<Pothole>pothItems) {
		this.activity = activity;
		this.pothItems = pothItems;
	}

	@Override
	public int getCount() {
		return pothItems.size();
	}

	@Override
	public Object getItem(int location) {
		return pothItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.list_row, null);

		TextView title = (TextView) convertView.findViewById(R.id.title);
		TextView date = (TextView) convertView.findViewById(R.id.date);

		Pothole m = pothItems.get(position);
		title.setText(String.valueOf(m.getId()));
		date.setText(String.valueOf(m.getCreatedDate()));
		


		return convertView;
	}
	@Override
	public boolean isEnabled(int position)
	{
		return true;
	}

}