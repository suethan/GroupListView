package here.wait.go.gouplistview;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.widget.BaseExpandableListAdapter;

public abstract class GroupHeaderAdapter extends BaseExpandableListAdapter {
		public static final int HEADER_GONE = 0;
		public static final int HEADER_VISIBLE = 1;
		public static final int HEADER_VISIBLE_ALPHA = 2;
		/**
		 * 下面的group推header的时候
		 */
		public static final int HEADER_PUSHED_UP = 3;
		
		/**
		 * 组收起状态
		 */
		public static final int GROUP_COLLAPSE = 0;
		/**
		 * 组的展开状态
		 */
		public static final int GROUP_EXPLAND = 1;
		
		protected Map<Integer, Integer> mGroupStates = new HashMap<Integer, Integer>();
		
		private GroupListView mGroupListView;
		
		public GroupHeaderAdapter(GroupListView groupListView) {
			mGroupListView = groupListView;
		}
		
		/**
		 * 获取head的状态
		 * 
		 * @param groupPosition
		 * @param childPosition
		 * @return
		 */
		public final int getHeaderState(int groupPosition, int childPosition) {
			final int childrenCount = getChildrenCount(groupPosition);
			//子项推到顶端了,就等于下一组顶到了header
			if(childrenCount-1 == childPosition)
				return HEADER_PUSHED_UP;
			//刚好显示组
			if(-1==childPosition && !mGroupListView.isGroupExpanded(groupPosition))
				return HEADER_GONE;
			if(-1==childPosition)
				return HEADER_VISIBLE;
				
			return HEADER_VISIBLE_ALPHA;
		}
		
		/**
		 * 配置header
		 * 
		 * @param header
		 * @param groupPosition
		 * @param childPosition
		 * @param alpha
		 */
		public abstract void configureHeader(View header, int groupPosition, int childPosition, int alpha);
		
		/**
		 * 设置租按下的状态
		 * 
		 * @param groupPosition
		 * @param status
		 */
		public final void onHeadClick(int groupPosition, int status) {
			mGroupStates.put(groupPosition, status);
		}
		
		/**
		 * 获取组按下的状态
		 * 
		 * @param groupPosition
		 */
		public final int getHeaderClickState(int groupPosition) {
			if(mGroupStates.containsKey(groupPosition))
				return mGroupStates.get(groupPosition);
			return GROUP_COLLAPSE;
		}
	}