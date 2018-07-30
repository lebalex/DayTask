package xyz.lebalex.daytask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListTaskSAdapter extends BaseAdapter {
    private final List<ListTasks> list;
    private LayoutInflater mLayoutInflater;

    public ListTaskSAdapter(Context context, List<ListTasks> list) {
        this.list = list;
/*        if(this.list.size()==0)
            this.list.add(new ListTasks("-1", context.getString(R.string.not_events)));*/
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if(view==null)
        {
            view = mLayoutInflater.inflate(R.layout.tasks_list_item, viewGroup, false);
        }
        ListTasks task = getTask(i);
        GridLayout secondPanel = (GridLayout) view.findViewById(R.id.taskPanel);
        TextView dateView = (TextView) view.findViewById(R.id.date);
        TextView title = (TextView) view.findViewById(R.id.title);


        String date = task.getDate();
        if(date==null)
            secondPanel.setBackgroundResource(R.color.colorDayAll);
        else
            secondPanel.setBackgroundResource(R.color.colorToDay);

        dateView.setText(date);
        title.setText(task.getTitle());
        return view;
    }
    private ListTasks getTask(int i)
    {
        return (ListTasks) getItem(i);
    }
}
