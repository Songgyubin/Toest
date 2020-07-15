package hanium.toast.mytoast.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hanium.toast.mytoast.R;
import hanium.toast.mytoast.data.MyLCCompleteChild;
import hanium.toast.mytoast.data.MyLCCompleteGroup;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class MyLCCompleteAdapter extends
        BaseExpandableRecyclerViewAdapter<MyLCCompleteGroup, MyLCCompleteChild, MyLCCompleteAdapter.GroupVH, MyLCCompleteAdapter.ChildVH> {

    private List<MyLCCompleteGroup> mList = new ArrayList<>();

//    public MyLCCompleteAdapter(List<MyLCCompleteGroup> list) {
//        mList = list;
//    }

    public void setItems(List<MyLCCompleteGroup> items) {
        this.mList = items;
        notifyDataSetChanged();
    }

    public List<MyLCCompleteGroup> getListGroupBean() {
        return mList;
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public MyLCCompleteGroup getGroupItem(int position) {
        return mList.get(position);
    }

    @Override
    public GroupVH onCreateGroupViewHolder(ViewGroup parent, int groupViewType) {
        return new GroupVH(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_complete_group, parent, false));
    }

    @Override
    public ChildVH onCreateChildViewHolder(ViewGroup parent, int childViewType) {
        return new ChildVH(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_complete_child, parent, false));
    }

    @Override
    public void onBindGroupViewHolder(GroupVH holder, MyLCCompleteGroup myLCCompleteGroup, boolean isExpanding) {
        holder.nameTv.setText(myLCCompleteGroup.getName());
        holder.duration_tv.setText(myLCCompleteGroup.getDuration());
        if (myLCCompleteGroup.isExpandable()) {
//            holder.foldIv.setVisibility(View.VISIBLE);
//            holder.foldIv.setImageResource(isExpanding ? R.drawable.ic_arrow_expanding : R.drawable.ic_arrow_folding);
        } else {
//            holder.foldIv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBindChildViewHolder(ChildVH holder, MyLCCompleteGroup groupBean, MyLCCompleteChild myLCCompleteChild) {
        holder.nameTv.setText(myLCCompleteChild.getName());

    }

    static class GroupVH extends BaseExpandableRecyclerViewAdapter.BaseGroupViewHolder {

        @BindView(R.id.group_item_name)
        TextView nameTv;

        @BindView(R.id.group_item_indicator)
        ImageView arrow;

        @BindView(R.id.main_duration)
        TextView duration_tv;

        GroupVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void onExpandStatusChanged(RecyclerView.Adapter relatedAdapter, boolean isExpanding) {
            if (isExpanding == true) {
                animateCollapse();
            } else {
                animateExpand();
            }
        }

        private void animateExpand() {
            RotateAnimation rotate =
                    new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }

        private void animateCollapse() {
            RotateAnimation rotate =
                    new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }
    }

    static class ChildVH extends RecyclerView.ViewHolder {

        @BindView(R.id.child_item_name)
        TextView nameTv;

        ChildVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
