package com.capricorn;


import org.opentalking.drawskill.R;
import org.opentalking.view.Fab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RayMenu extends RelativeLayout {
	
	private RayLayout mRayLayout;
	private Fab mFab;
	private OnItemClickListener itemListener;

	public RayMenu(Context context) {
		super(context);
		init(context);
	}

	public RayMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		setClipChildren(false);

		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.ray_menu, this);

		mRayLayout = (RayLayout) findViewById(R.id.item_layout);
	 	mFab = (Fab)findViewById(R.id.control_layout);
	 	int holoBlue = getResources().getColor(android.R.color.holo_blue_light);
		mFab.setFabColor(holoBlue);
	 	mFab.setFabDrawable(getResources().getDrawable(R.drawable.ic_content_new));
	 	mFab.setOnClickListener(new FabListener());
	}

	public void addItem(View item, OnClickListener listener) {
		mRayLayout.addView(item);
		item.setOnClickListener(getItemClickListener(listener));
	}

	private OnClickListener getItemClickListener(final OnClickListener listener) {
		return new OnClickListener() {

			@Override
			public void onClick(final View viewClicked) {
				Animation animation = bindItemAnimation(viewClicked, true, 400);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						postDelayed(new Runnable() {

							@Override
							public void run() {
								itemDidDisappear();
							}
						}, 0);
					}
				});

				final int itemCount = mRayLayout.getChildCount();
				for (int i = 0; i < itemCount; i++) {
					View item = mRayLayout.getChildAt(i);
					if (viewClicked != item) {
						bindItemAnimation(item, false, 300);
					}
				}
			 	mFab.showFab();
				mRayLayout.invalidate();

				if (listener != null) {
					listener.onClick(viewClicked);
				}
			}
		};
	}

	private Animation bindItemAnimation(final View child,
			final boolean isClicked, final long duration) {
		Animation animation = createItemDisapperAnimation(duration, isClicked);
		child.setAnimation(animation);

		return animation;
	}

	private void itemDidDisappear() {
		final int itemCount = mRayLayout.getChildCount();
		for (int i = 0; i < itemCount; i++) {
			View item = mRayLayout.getChildAt(i);
			item.clearAnimation();
		}

		mRayLayout.switchState(false);
	}

	private static Animation createItemDisapperAnimation(final long duration,
			final boolean isClicked) {
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(new ScaleAnimation(1.0f, isClicked ? 2.0f
				: 0.0f, 1.0f, isClicked ? 2.0f : 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f));
		animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));

		animationSet.setDuration(duration);
		animationSet.setInterpolator(new DecelerateInterpolator());
		animationSet.setFillAfter(true);

		return animationSet;
	}

	private static Animation createHintSwitchAnimation(final boolean expanded) {
		Animation animation = new RotateAnimation(expanded ? 45 : 0,
				expanded ? 0 : 45, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setStartOffset(0);
		animation.setDuration(100);
		animation.setInterpolator(new DecelerateInterpolator());
		animation.setFillAfter(true);

		return animation;
	}

	class FabListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			mFab.startAnimation(createHintSwitchAnimation(mRayLayout.isExpanded()));
			mRayLayout.switchState(true);
			mFab.hideFab();
		}
		
	}

	public void setImageItems(int[] menuIcon) {
		 int itemCount = menuIcon.length;
		
		for (int i = 0; i < itemCount; i++) {
			ImageView item = new ImageView(this.getContext());
			item.setImageResource(menuIcon[i]);
			final int location = i;
			addItem(item, new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(itemListener!=null){
						itemListener.onItemClick(v, location);
					}
				}
			});// Add a menu item
		}
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		this.itemListener = listener;
	}
	
	public interface OnItemClickListener{
		public void onItemClick(View v,int location);
	}
}
