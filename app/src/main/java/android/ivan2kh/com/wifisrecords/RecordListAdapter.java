package android.ivan2kh.com.wifisrecords;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by john on 3/16/2016.
 */
public class RecordListAdapter extends ArrayAdapter<Record> {

    protected static final String LOG_TAG = RecordListAdapter.class.getSimpleName();

    private List<Record> items;
    private int layoutResourceId;
    private Context context;

    public RecordListAdapter(Context context, int layoutResourceId, List<Record> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new RecordHolder();
        holder.Record = items.get(position);
        holder.removeRecordButton = (ImageButton)row.findViewById(R.id.record_remove);
        holder.removeRecordButton.setTag(holder.Record);

        holder.name = (TextView)row.findViewById(R.id.record_comment);
        setNameTextChangeListener(holder);
        holder.date = (TextView)row.findViewById(R.id.record_date);

        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(RecordHolder holder) {
        holder.name.setText(holder.Record.getComment());
        //holder.date.setText(String.valueOf(holder.Record.getCount()));
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String date = format.format(holder.Record.getDate());
        holder.date.setText(date);
    }

    public static class RecordHolder {
        Record Record;
        TextView name;
        TextView date;
        ImageButton removeRecordButton;
    }

    private void setNameTextChangeListener(final RecordHolder holder) {
        holder.name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.Record.setComment(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

}
