package com.example.notesapplication.bottomSheet;

import static com.example.notesapplication.main.NotesActivityMain.fragmentManager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

//WORK RUN BACKGROUND
public class ShowSheetNotifyWorkerBottom extends Worker {


    public ShowSheetNotifyWorkerBottom(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    //CÔNG VIỆC CẦN THỰC HIỆN
    @NonNull
    @Override
    public Result doWork() {

        //NHẬN DỮ LIỆU
        int id = getInputData().getInt("id",0);
        String txt = getInputData().getString("text_notify");

        //KIỂM TRA XEM fragmentManager null hay khác null
        //NẾU KHÁC NULL NÓ SẼ HIỂN THỊ THÔNG BÁO NGAY
        if(fragmentManager != null){
            BottomSheetDialogShowNotify showNotify = new BottomSheetDialogShowNotify(id,txt);
            showNotify.show(fragmentManager,showNotify.getTag());
        }
        //NẾU fragmentManager null THÌ SẼ
        //ĐÓNG GÓI THÔNG TIN NHIỆM VỤ VÀ THỰC HIỆN 1 wrok request khác để hiển thị thông báo
        else{
            Data data = new Data.Builder()
                    .putInt("id",id)
                    .putString("text_notify",txt)
                    .build();
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(ShowSheetNotifyWorker.class)
                    .addTag("work_request")
                    .setInputData(data)
                    .build();
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
        }

        return Result.success();
    }
}
