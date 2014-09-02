package org.opentalking.drawskill;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.capricorn.RayMenu;

import java.io.File;
import java.util.HashMap;

import org.opentalking.drawskill.colorpicker.Picker;
import org.opentalking.drawskill.colorpicker.PickerDialog;
import org.opentalking.drawskill.style.StyleBrush;
import org.opentalking.drawskill.style.StylesFactory;
import org.opentalking.view.Fab;

public class MainActivity extends Activity {

	private final static int[] MENU_ICON = { R.drawable.brush_sketchy_button,
			R.drawable.brush_shaded_button, R.drawable.brush_fur_button,
			R.drawable.brush_web_button, R.drawable.brush_circles_button,
			R.drawable.brush_ribbon_button, R.drawable.brush_simple_button };

	private static MainActivity INSTANCE;
	private static final String PREF_OPACITY = "cur_opacity";
	private static final String PREF_STYLE = "cur_brush_type";
	private static final String PREF_COLOR = "cur_color";
	private static final String PREF_BG_COLOR = "cur_background_color";
	private static final String PREF_STROKE_WIDTH = "cur_stroke_width";
	private static final float MAX_STROKE_WIDTH = 4;
	private static final float MAX_OPACITY = 255;
	private static final String TEMP_FILE_NAME = "current_pic.png";

	public static final String PREFS_NAME = "preferences";

	private Surface surface;
	private final FileHelper fileHelper = new FileHelper(this);
	private View selectedBrushButton;
	private View backgroundPickerButton;
	private View foregroundPickerButton;

	private CountDownTimer actionBarTimer = new CountDownTimer(3000, 1000) {

		@Override
		public void onTick(long millisUntilFinished) {

		}

		@Override
		public void onFinish() {

		}
	};

	public static MainActivity getInstance() {
		return INSTANCE;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		INSTANCE = this;

		setContentView(R.layout.action_main);

		surface = (Surface) findViewById(R.id.surface);

		restoreFromPrefs();
		initButtons();
		initStyle();
		initSliders();

		RayMenu rayMenu = (RayMenu) findViewById(R.id.ray_menu);
		rayMenu.setImageItems(MENU_ICON);
		rayMenu.setOnItemClickListener(new  RayMenu.OnItemClickListener() {
			@Override
			public void onItemClick(View v, int location) {
				// TODO Auto-generated method stub
				StyleBrush styleBrush = StylesFactory.getStyle(DrawApplication.StyleButtonMap.get(location));
				getSurface().setStyle(styleBrush);
			}
		});
//		Toast.makeText(MainActivity.this, "position:" + position,Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// We are loosing action bar color on resume. So did some hack.
		switchToolbars();
		// switchToolbars();
	}

	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences preferences = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		preferences
				.edit()
				.putInt(PREF_OPACITY, surface.getOpacity())
				.putFloat(PREF_STROKE_WIDTH, surface.getStrokeWidth())
				.putInt(PREF_COLOR, surface.getPaintColor())
				.putInt(PREF_BG_COLOR, surface.getBackgroundColor())
				.putString(PREF_STYLE,
						StylesFactory.getCurrentBrushType().name()).apply();

		// wrapped to a new thread since it can be killed due to time limits for
		// onPause() method
		new Thread() {
			@Override
			public void run() {
				File tempFileName = new File(getExternalFilesDir(null),
						TEMP_FILE_NAME);
				if (null != tempFileName)
					fileHelper.saveBitmap(tempFileName);
			}
		}.run();
	}

	@Override
	protected void onResume() {
		super.onResume();
		String tempFileName = getExternalFilesDir(null) + File.separator
				+ TEMP_FILE_NAME;
		getSurface().setInitialBitmap(fileHelper.getSavedBitmap(tempFileName));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_options, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_undo).setEnabled(
				DocumentHistory.getInstance().canUndo());
		menu.findItem(R.id.menu_redo).setEnabled(
				DocumentHistory.getInstance().canRedo());
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_clear:
			getSurface().clearBitmap();
			return true;
		case R.id.menu_save:
			fileHelper.saveToSD();
			return true;
		case R.id.menu_send:
			fileHelper.share();
			return true;
		case R.id.menu_undo:
			DocumentHistory.getInstance().undo();
			invalidateOptionsMenu();
			return true;
		case R.id.menu_redo:
			DocumentHistory.getInstance().redo();
			invalidateOptionsMenu();
		default:
			return false;
		}
	}

	public Surface getSurface() {
		return surface;
	}

	public void switchToolbars() {
		View brushProperties = findViewById(R.id.brush_property);
		ActionBar actionBar = getActionBar();

		if (actionBar.isShowing()) {
			actionBar.hide();
			brushProperties.setVisibility(View.GONE);
		} else {
			actionBar.show();
			brushProperties.setVisibility(View.VISIBLE);
		}
	}

	private void initButtons() {
		backgroundPickerButton = findViewById(R.id.background_picker_button);
		backgroundPickerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				new PickerDialog(MainActivity.this,
						new Picker.OnColorChangedListener() {
							public void colorChanged(int color) {
								getSurface().setBackgroundColor(color);
								backgroundPickerButton
										.setBackgroundColor(color);
							}
						}, getSurface().getBackgroundColor()).show();
			}
		});

		foregroundPickerButton = findViewById(R.id.foreground_picker_button);
		foregroundPickerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				new PickerDialog(MainActivity.this,
						new Picker.OnColorChangedListener() {
							public void colorChanged(int color) {
								getSurface().setPaintColor(color);
								foregroundPickerButton
										.setBackgroundColor(color);
							}
						}, getSurface().getPaintColor()).show();
			}
		});

		View menuSwitcherImage = findViewById(R.id.menu_switcher);
		menuSwitcherImage.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				switchToolbars();
			}
		});
	}

	private void initSliders() {
		SeekBar opacityBar = (SeekBar) findViewById(R.id.brush_opacity_bar);
		opacityBar.setProgress((int) (Math.sqrt(surface.getOpacity()
				/ MAX_OPACITY) * 100));

		SeekBar sizeBar = (SeekBar) findViewById(R.id.brush_size_bar);
		sizeBar.setProgress((int) (surface.getStrokeWidth() / MAX_STROKE_WIDTH
				/ DrawApplication.getDensity() * 100));

		opacityBar
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar, int i,
							boolean b) {
						float opacityValue = ((float) i * i / 10000)
								* MAX_OPACITY;
						surface.setOpacity((int) opacityValue);
						foregroundPickerButton.setBackgroundColor(surface
								.getPaintColor());
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				});

		sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				surface.setStrokeWidth((float) i / 100 * MAX_STROKE_WIDTH
						* DrawApplication.getDensity());
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	private void initStyle() {
		// selectedBrushButton =
		// findViewById(DrawApplication.StyleButtonMap.get(StylesFactory
		// .getCurrentBrushType()));
		// selectedBrushButton.setSelected(true);
		backgroundPickerButton.setBackgroundColor(surface.getBackgroundColor());
		foregroundPickerButton.setBackgroundColor(surface.getPaintColor());
	}

	// private void brushButtonOnClick(int buttonRes,
	// final StylesFactory.BrushType brushType) {
	// ImageButton button = (ImageButton) findViewById(buttonRes);
	// button.setOnClickListener(new OnClickListener() {
	// public void onClick(View view) {
	// if (null != selectedBrushButton) {
	// selectedBrushButton.setSelected(false);
	// }
	// selectedBrushButton = view;
	// view.setSelected(true);
	// StyleBrush styleBrush = StylesFactory.getStyle(brushType);
	// getSurface().setStyle(styleBrush);
	// }
	// });
	// }

	private void restoreFromPrefs() {

		SharedPreferences preferences = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		surface.setOpacity(preferences.getInt(PREF_OPACITY,
				DrawController.DEFAULT_OPACITY));
		surface.setStrokeWidth(preferences.getFloat(PREF_STROKE_WIDTH,
				DrawController.DEFAULT_WIDTH));
		surface.setPaintColor(preferences.getInt(PREF_COLOR,
				DrawController.DEFAULT_COLOR));
		surface.setBackgroundColor(preferences.getInt(PREF_BG_COLOR,
				DrawController.INIT_BG_COLOR));

		String brushName = preferences.getString(PREF_STYLE, "");
		StylesFactory.BrushType brushType;
		if (brushName.length() > 0)
			brushType = StylesFactory.BrushType.valueOf(brushName);
		else
			brushType = StylesFactory.DEFAULT_BRUSH_TYPE;
		surface.setStyle(StylesFactory.getStyle(brushType));
	}

}
