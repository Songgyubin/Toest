package hanium.toast.mytoast.data;


import androidx.annotation.NonNull;

import java.util.List;

import hanium.toast.mytoast.adapter.BaseExpandableRecyclerViewAdapter;

public class MyLCCompleteGroup implements BaseExpandableRecyclerViewAdapter.BaseGroupBean<MyLCCompleteChild> {

    private List<MyLCCompleteChild> mList;
    private String mName;
    private String mid;
    private String mduration;

    public MyLCCompleteGroup(@NonNull List<MyLCCompleteChild> list, @NonNull String name, String id, String duration) {
        mList = list;
        mName = name;
        mid = id;
        mduration = duration;
    }

    @Override
    public int getChildCount() {
        return mList.size();
    }

    @Override
    public boolean isExpandable() {
        return getChildCount() > 0;
    }

    public String getName() {
        return mName;
    }

    public String getId() {
        return mid;
    }

    public String getDuration() {
        return mduration;
    }
    @Override
    public MyLCCompleteChild getChildAt(int index) {
        return mList.size() <= index ? null : mList.get(index);
    }
}
