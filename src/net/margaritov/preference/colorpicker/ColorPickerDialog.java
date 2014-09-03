/*
 * Copyright (C) 2010 Daniel Nilsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.margaritov.preference.colorpicker;

import org.opentalking.drawskill.R;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

public class ColorPickerDialog extends Dialog implements
		ColorPickerView.OnColorChangedListener, View.OnClickListener {

	private static final int[] COLORS = {R.color.navy,R.color.blue,R.color.aqua,R.color.teal,
		R.color.olive,R.color.green,R.color.lime,R.color.yellow,
		R.color.orange,R.color.red,R.color.maroon,R.color.fuchsia,
		R.color.purple,R.color.black,R.color.gray,R.color.silver};
	
	private ColorPickerView mColorPicker;
	private GridView normalcolors;
	private NormalColorAdapter adapter;

	private ColorPickerPanelView mOldColor;
	private ColorPickerPanelView mNewColor;

	private OnColorChangedListener mListener;

	public interface OnColorChangedListener {
		public void onColorChanged(int color);
	}

	public ColorPickerDialog(Context context, int initialColor) {
		super(context);

		init(initialColor);
	}

	private void init(int color) {
		// To fight color banding.
		getWindow().setFormat(PixelFormat.RGBA_8888);

		setUp(color);

	}

	private void setUp(int color) {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.dialog_color_picker, null);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(layout);

		mColorPicker = (ColorPickerView) layout
				.findViewById(R.id.color_picker_view);
		mOldColor = (ColorPickerPanelView) layout
				.findViewById(R.id.old_color_panel);
		mNewColor = (ColorPickerPanelView) layout
				.findViewById(R.id.new_color_panel);
		
		normalcolors = (GridView) findViewById(R.id.normalcolors);
		adapter = new NormalColorAdapter(this.getContext());
		normalcolors.setAdapter(adapter);

		((LinearLayout) mOldColor.getParent()).setPadding(0, 0,
				Math.round(mColorPicker.getDrawingOffset())*2, 0);

		mOldColor.setOnClickListener(this);
		mNewColor.setOnClickListener(this);
		mColorPicker.setOnColorChangedListener(this);
		mOldColor.setColor(color);
		mColorPicker.setColor(color, true);

	}

	@Override
	public void onColorChanged(int color) {

		mNewColor.setColor(color);

		/*
		 * if (mListener != null) { mListener.onColorChanged(color); }
		 */
	}

	public void setAlphaSliderVisible(boolean visible) {
		mColorPicker.setAlphaSliderVisible(visible);
	}

	public boolean getAlphaSliderVisible() {
		return mColorPicker.getAlphaSliderVisible();
	}

	/**
	 * Set a OnColorChangedListener to get notified when the color selected by
	 * the user has changed.
	 * 
	 * @param listener
	 */
	public void setOnColorChangedListener(OnColorChangedListener listener) {
		mListener = listener;
	}

	public int getColor() {
		return mColorPicker.getColor();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.new_color_panel) {
			if (mListener != null) {
				mListener.onColorChanged(mNewColor.getColor());
			}
		}
		dismiss();
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt("old_color", mOldColor.getColor());
		state.putInt("new_color", mNewColor.getColor());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mOldColor.setColor(savedInstanceState.getInt("old_color"));
		mColorPicker.setColor(savedInstanceState.getInt("new_color"), true);
	}
	class NormalColorAdapter extends BaseAdapter{

		private Context mContext;
		
		public NormalColorAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return COLORS.length;
		}

		@Override
		public Object getItem(int position) {
			return COLORS[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = new View(mContext);
				convertView.setMinimumWidth(50);
				convertView.setMinimumHeight(50);
			}
			convertView.setBackgroundResource(COLORS[position]);
			return convertView;
		}
		
	}
}
