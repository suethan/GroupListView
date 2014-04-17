package here.wait.go.gouplistview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends Activity {
	private GroupListView mListView;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mListView = (GroupListView) findViewById(R.id.expandable_lv);

		mListView.setHeaderView(mInflater.inflate(R.layout.group, null));
		Adapter adapter = new Adapter(mListView);
		mListView.setAdapter(adapter);

	}
	
	final class Adapter extends GroupHeaderAdapter {

		int mGroupSize = 10;
		int mChildrenSize = 10;
		private List<String> mGroup = new ArrayList<String>() {
			{
				for (int i = 0; i < mGroupSize; i++)
					add("group" + i);
			}
		};
		private List<List<String>> mChildren = new ArrayList<List<String>>() 
			{{
				for (int i = 0; i < mGroupSize; i++) {
					List<String> children = new ArrayList<String>();
					for (int j = 0; j < mChildrenSize; j++) {
						children.add("child" + j);
					}
					add(children);
				}
			}};
		
		public Adapter(GroupListView groupListView) {
			super(groupListView);
		}

		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mChildren.get(groupPosition).size();
		}

		@Override
		public String getGroup(int groupPosition) {
			return mGroup.get(groupPosition);
		}

		@Override
		public String getChild(int groupPosition, int childPosition) {
			return mChildren.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHodler holder;
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.group, null);
				holder = new GroupHodler();
				holder.text = (TextView) convertView.findViewById(R.id.tv);
				convertView.setTag(holder);
			} else {
				holder = (GroupHodler) convertView.getTag();
			}

			holder.text.setText(mGroup.get(groupPosition));

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildHodler holder;
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.child, null);
				holder = new ChildHodler();
				holder.text = (TextView) convertView.findViewById(R.id.tv);
				convertView.setTag(holder);
			} else {
				holder = (ChildHodler) convertView.getTag();
			}

			holder.text
					.setText(mChildren.get(groupPosition).get(childPosition));

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		class ChildHodler {
			TextView text;
		}

		class GroupHodler {
			TextView text;
		}



		@Override
		public void configureHeader(View header, int groupPosition,
				int childPosition, int alpha) {
			((TextView) header.findViewById(R.id.tv))
					.setText(getGroup(groupPosition));
			header.getBackground().setAlpha(alpha);
		}

	}

}
