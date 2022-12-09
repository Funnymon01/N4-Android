package com.example.notesapplication.bottomSheet;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.notesapplication.R;


//ACTIVITY HIỂN THỊ THÔNG BÁO TRÊN MÀN HÌNH KHÓA
//THAY CHO BOTTOM SHEET
//BỞI VÌ, CHỈ CÓ ACTIVITY MỚI ĐƯỢC PHÉP HIỂN THỊ NHƯ 1 THÔNG BÁO TRÊN MÀN HÌNH KHÓA
public class ShowSheetBottomDialogNotifyNotes extends AppCompatActivity {

    //THUỘC TÍNH
    TextView buttonOke;
    TextView textViewTextNotify;
    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        this.getWindow().setFlags(2622464,
                2622464);
        setContentView(R.layout.activity_show_sheet_bottom_dialog_notify_notes);

        //NHẬN DỮ LIỆU THÔNG TIN CÚA THÔNG BÁO QUA INTENT
        bundle = getIntent().getBundleExtra("bundle");

        buttonOke = findViewById(R.id.buttonOkeCloseSheetActivity);
        textViewTextNotify = findViewById(R.id.textViewNotesShowActivity);

        //GET DỮ LIỆU TỪ BUNDLE
        int id = bundle.getInt("id",0);
        String txt = bundle.getString("txt","");

        //GÁN TEXT CỦA NHIỆM VỤ LÊN VIEW
        textViewTextNotify.setText(txt);
        //BẮT SỰ KIỆN TRÊN BUTTON ĐÓNG SHEET
        buttonOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //REMOVE ACTIVITI
                finishAndRemoveTask();
                //HỦY THÔNG BÁO
                NotificationManagerCompat.from(getApplicationContext()).cancel(id);
            }
        });

        boolean fromButtonSKipNotification= bundle.getBoolean("fromSkipNotification",false);

        if(fromButtonSKipNotification){
            finishAndRemoveTask();
            NotificationManagerCompat.from(getApplicationContext()).cancel(id);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        finishAndRemoveTask();
        NotificationManagerCompat.from(getApplicationContext()).cancel(bundle.getInt("id"));
    }
}