package here.wait.go.gouplistview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;


public class GroupListView extends ExpandableListView implements OnScrollListener, OnGroupClickListener{

	public GroupListView(Context context) {
		super(context);
		registerListener();
	}
	
	public GroupListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		registerListener();
	}
	
	public GroupListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		registerListener();
	}

	
	private static final int MAX_PLPHA = 150;
	private GroupHeaderAdapter mAdapter;
	/**
	 * 列表头view,mHeaderVisible=true才可见
	 */
	private View mHeaderView;
	/**
	 * 列表头是否可见
	 */
	private boolean mHeaderVisible;
	private int mHeaderWidth;
	private int mHeaderHeight;
	
	public void setHeaderView(View view) {
		mHeaderView = view;
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mHeaderView.setLayoutParams(lp);
		mHeaderView.setFadingEdgeLength(0);
		requestLayout();
	}
	
	private void registerListener() {
		setOnScrollListener(this);
		setOnGroupClickListener(this);
	}
	
	private void headerViewClick() {
		long packedPosition = getExpandableListPosition(this.getFirstVisiblePosition());
		int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
		
		if(GroupHeaderAdapter.GROUP_EXPLAND == mAdapter.getHeaderClickState(groupPosition)) {
			//收起该group
			this.collapseGroup(groupPosition);
			mAdapter.onHeadClick(groupPosition, GroupHeaderAdapter.GROUP_COLLAPSE);
		} else {
			this.expandGroup(groupPosition);
			mAdapter.onHeadClick(groupPosition, GroupHeaderAdapter.GROUP_EXPLAND);
		}
		
		this.setSelectedGroup(groupPosition);
	}
	
	private float mDownX;
	private float mDownY;
	
	/**
	 * 由于headerView是画上去的,只有在可见的情况下点击有效
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(mHeaderVisible) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX = ev.getX();
				mDownY = ev.getY();
				if(mDownX <= mHeaderWidth && mDownY <= mHeaderHeight) {
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				float x = ev.getX();
				float y = ev.getY();
				if(x <= mHeaderWidth && y <= mHeaderHeight 
						&& mDownX <= mHeaderWidth && mDownY <= mHeaderHeight) {
					if(null != mHeaderView) {
						headerViewClick();
					}
					return true;
				}
				break;
			default:
				break;
			}
		}
		
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 设置group的点击事件，自行控制group的状态
	 */
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		if(GroupHeaderAdapter.GROUP_COLLAPSE == mAdapter.getHeaderClickState(groupPosition)) {
			mAdapter.onHeadClick(groupPosition, GroupHeaderAdapter.GROUP_EXPLAND);
			parent.expandGroup(groupPosition);
			parent.setSelectedGroup(groupPosition);
		} else if (GroupHeaderAdapter.GROUP_EXPLAND == mAdapter.getHeaderClickState(groupPosition)) {
			mAdapter.onHeadClick(groupPosition, GroupHeaderAdapter.GROUP_COLLAPSE);
			parent.collapseGroup(groupPosition);
			parent.setSelectedGroup(groupPosition);
		}
		//一定要返回true,不然会有bug
		return true;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	/**
	 * 滚动的时候根据实际控制 headerView
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		final long flagPos = getExpandableListPosition(getFirstVisiblePosition());
		int groupPosition = ExpandableListView.getPackedPositionGroup(flagPos);
		int childPosition = ExpandableListView.getPackedPositionChild(flagPos);
		
		configureHeaderView(groupPosition, childPosition);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(null != mHeaderView) {
			measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
			mHeaderHeight = mHeaderView.getMeasuredHeight();
			mHeaderWidth = mHeaderView.getMeasuredWidth();
		}
	}
	
	private int mOldState = -1;
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		final long flagPostion = getExpandableListPosition(getFirstVisiblePosition());
		final int groupPos = ExpandableListView.getPackedPositionGroup(flagPostion);
		final int childPos = ExpandableListView.getPackedPositionChild(flagPostion);
		
		int state = mAdapter.getHeaderState(groupPos, groupPos);
		if(null != mHeaderView && null != mAdapter && state != mOldState) {
			mOldState = state;
			mHeaderView.layout(0, 0, mHeaderWidth, mHeaderHeight);
		}
		configureHeaderView(groupPos, childPos);
	}
	
	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = (GroupHeaderAdapter) adapter;
	}
	
	public void configureHeaderView(int groupPosition, int childPosition) {
		if(null==mHeaderView || null==mAdapter || 0==((ExpandableListAdapter)mAdapter).getGroupCount()) {
			return;
		}
		
		int state = mAdapter.getHeaderState(groupPosition, childPosition);
		
		switch (state) {
		case GroupHeaderAdapter.HEADER_GONE:
			mHeaderVisible = false;
			break;
		case GroupHeaderAdapter.HEADER_VISIBLE:
			System.out.println("HEADER_VISIBLE_ALPHA");
			mAdapter.configureHeader(mHeaderView, groupPosition,
					childPosition, 255);

			if (mHeaderView.getTop() != 0) {
				mHeaderView.layout(0, 0, mHeaderWidth, mHeaderHeight);
			}
			mHeaderVisible = true;
			break;
		case GroupHeaderAdapter.HEADER_VISIBLE_ALPHA:
			mAdapter.configureHeader(mHeaderView, groupPosition,
					childPosition, MAX_PLPHA);

			if (mHeaderView.getTop() != 0) {
				mHeaderView.layout(0, 0, mHeaderWidth, mHeaderHeight);
			}
			mHeaderVisible = true;
			break;
		case GroupHeaderAdapter.HEADER_PUSHED_UP:
			System.out.println("HEADER_PUSHED_UP");
			View firstView = getChildAt(0);
			int bottom = firstView.getBottom();
			int y = 0;
			if(bottom < mHeaderHeight) {
				y = bottom - mHeaderHeight;
			}
			
			mAdapter.configureHeader(mHeaderView, groupPosition, childPosition, 255);
			if(mHeaderView.getTop() != y) {
				System.out.println("mHeaderView.getTop() != y : " + y);
				mHeaderView.layout(0, y, mHeaderWidth, mHeaderHeight + y);
			}
			mHeaderVisible = true;
			break;
		default:
			break;
		}
	}
	
	/**
	 * 列表界面更新时调用该方法(如滚动)
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		//列表头直接描绘,不加入ViewGroup
		if(mHeaderVisible)
			drawChild(canvas, mHeaderView, getDrawingTime());
	}
}