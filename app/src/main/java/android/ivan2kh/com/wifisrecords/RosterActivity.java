package android.ivan2kh.com.wifisrecords;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RosterActivity extends AppCompatActivity {

    private RecordListAdapter adapter;
    static final int NEW_RECORD_REQUEST = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roster);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setupListViewAdapter();
    }

    public void removeAtomPayOnClickHandler(View v) {
        Record itemToRemove = (Record)v.getTag();
        adapter.remove(itemToRemove);
        File extDir = Environment.getExternalStoragePublicDirectory("WifiRecords");;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        String date = format.format(itemToRemove.getDate());

        String file_name = String.format(Locale.US, "%s %d %s.csv",
            date,
            itemToRemove.getCount(),
            itemToRemove.getComment());

        File file =(new File(extDir, file_name));
        file.delete();
        MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);
    }

    //2015-01-20 20:59 199 Comment.csv
    private static final Pattern fname_regex = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2} \\d{2}-\\d{2}) (\\d+) (.+)\\.csv$");
    private Record fileNameToRecord(String fname) throws PatternSyntaxException
    {
        Matcher m = fname_regex.matcher(fname);
        if (m.find()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm");
            Date date;
            try {
                date = format.parse(m.group(1));
                System.out.println("Date ->" + date);
            } catch (Exception e) {
                throw new PatternSyntaxException("fileNameToRecord exception " + fname, fname_regex.toString(), 0);
            }
            int count = Integer.parseInt(m.group(2));
            return new Record(date, count, m.group(3));
        }
        throw new PatternSyntaxException("fileNameToRecord exception " + fname, fname_regex.toString(), 0);
    }

    private void setupListViewAdapter() {
        ArrayList<Record> recordsList = new ArrayList<Record>();

        File extDir = Environment.getExternalStoragePublicDirectory("WifiRecords");;
        extDir.mkdirs();
//        MediaScannerConnection.scanFile(this, new String[]{extDir.toString()}, null, null);
//        File nomedia = new File(extDir, ".nomedia");
//        if (!nomedia.exists())
//            try {
//                nomedia.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        if(extDir != null) {
            File[] files = extDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                try {
                    Record record = fileNameToRecord(file.getName());
                    recordsList.add(record);
                } catch (PatternSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }

        adapter = new RecordListAdapter(RosterActivity.this, R.layout.record_item, recordsList);
        ListView atomPaysListView = (ListView)findViewById(R.id.records_listView);
        atomPaysListView.setAdapter(adapter);
    }

    private void setupAddPaymentButton() {
        findViewById(R.id.records_addButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //adapter.insert(new Record("", 0), 0);
            }
        });
    }

    public void onNewRecordButtonClick(View view) {
        Intent intent = new Intent(this, NewRecordActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivityForResult(intent, NEW_RECORD_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_RECORD_REQUEST) {
            if(resultCode == RESULT_OK){
                setupListViewAdapter();
//                String fname = data.getStringExtra(NewRecordActivity.FILE_NAME);
//                fname = (new File(fname)).getName();
//                adapter.insert(fileNameToRecord(fname), 0);
            } else if(resultCode == RESULT_CANCELED) {

            }

        }
    }
}
