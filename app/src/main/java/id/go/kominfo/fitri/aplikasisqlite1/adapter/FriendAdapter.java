package id.go.kominfo.fitri.aplikasisqlite1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import id.go.kominfo.fitri.aplikasisqlite1.R;
import id.go.kominfo.fitri.aplikasisqlite1.dao.FriendDao;
import id.go.kominfo.fitri.aplikasisqlite1.model.Friend;
public class FriendAdapter extends BaseAdapter {
    private final List<Friend> mList;
    private final Context mContext;
    public FriendAdapter(List<Friend> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return mList.size();
    }
    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if(convertView == null)
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.row_layout, viewGroup, false);
        TextView tvID = convertView.findViewById(R.id.tv_id);
        TextView tvName = convertView.findViewById(R.id.tv_name);
        TextView tvAddress = convertView.findViewById(R.id.tv_address);
        tvID.setText(String.valueOf(mList.get(i).getId()));
        tvName.setText(mList.get(i).getName());
        tvAddress.setText(mList.get(i).getAddress());
        return convertView;
    }
}
