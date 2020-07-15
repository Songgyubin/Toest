package hanium.toast.mytoast.listeners;

import hanium.toast.mytoast.adapter.BaseExpandableRecyclerViewAdapter;
import hanium.toast.mytoast.data.MyLCCompleteChild;

public interface ExpandableRecyclerViewOnClickListener<GroupBean extends BaseExpandableRecyclerViewAdapter.BaseGroupBean, ChildBean> {

    /**
     * called when group item is long clicked
     *
     * @param groupItem
     * @return
     */
    boolean onGroupLongClicked(GroupBean groupItem);

    /**
     * called when an expandable group item is clicked
     *
     * @param groupItem
     * @param isExpand
     * @return whether intercept the click event
     */
    boolean onInterceptGroupExpandEvent(GroupBean groupItem, boolean isExpand);

    /**
     * called when an unexpandable group item is clicked
     *
     * @param groupItem
     */
    void onGroupClicked(GroupBean groupItem);

    /**
     * called when child is clicked
     *
     * @param groupItem
     * @param childItem
     */
    void onChildClicked(GroupBean groupItem, MyLCCompleteChild childItem);
}
