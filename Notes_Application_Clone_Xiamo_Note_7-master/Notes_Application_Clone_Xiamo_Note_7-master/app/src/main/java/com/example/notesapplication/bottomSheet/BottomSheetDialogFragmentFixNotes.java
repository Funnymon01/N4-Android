package com.example.notesapplication.bottomSheet;

import static com.example.notesapplication.fragment.AddNotesFragment.calculateTimeBetweenTwoDate;
import static com.example.notesapplication.fragment.AddNotesFragment.databaseSaveNoteItems;
import static com.example.notesapplication.fragment.AddNotesFragment.editTextNotifyOnTime;
import static com.example.notesapplication.fragment.AddNotesFragment.lists;
import static com.example.notesapplication.fragment.AddNotesFragment.listsObservable;
import static com.example.notesapplication.main.NotesActivityMain.fragmentManager;
import static com.example.notesapplication.adapter.NotesItemAdapter.valueResources;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.notesapplication.databinding.CustomLayoutBottomAddNotesBinding;
import com.example.notesapplication.model.ChildrenNoteItem;
import com.example.notesapplication.model.NoteItem;
import com.example.notesapplication.R;
import com.example.notesapplication.fragment.AddNotesFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BottomSheetDialogFragmentFixNotes extends BottomSheetDialogFragment {

    //THUỘC TÍNH
    boolean updated = false;
    NoteItem noteItem;
    Context context;
    BottomSheetDialog bottomSheetDialog;
    //LAYOUT BINDING
    public static CustomLayoutBottomAddNotesBinding customLayoutBottomAddNotesBinding;

    //variables to check bottom sheet add notes open or sheet fix open
    public static boolean fromFixNotes = false;

    //HÀM KHỞI TẠO
    public BottomSheetDialogFragmentFixNotes(Context context, NoteItem noteItem){
        this.context = context;
        this.noteItem = noteItem;
    }

    //OVERRIDE HÀM TẠO DIALOG
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        fromFixNotes = true;
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialog);

        //INFLATE LAYOUT BINDING
        customLayoutBottomAddNotesBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),R.layout.custom_layout_bottom_add_notes,
                null,
                false);

        bottomSheetDialog.setContentView(customLayoutBottomAddNotesBinding.getRoot());
        Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).UpdateData();

        //noteItem.setCustomLayoutBottomAddNotesBinding(customLayoutBottomAddNotesBinding);
        Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).setObject(customLayoutBottomAddNotesBinding);

        customLayoutBottomAddNotesBinding.setNoteItem(noteItem);


        if(noteItem.listNotes.size() > 1){

            ViewGroup.LayoutParams params = customLayoutBottomAddNotesBinding.recyclerViewEditNote.getLayoutParams();
            params.height = noteItem.listNotes.size()*150;

            if(params.height >= 1560){
                params.height = 800;
            }

            customLayoutBottomAddNotesBinding.recyclerViewEditNote.setLayoutParams(params);
        }

        if(noteItem.listNotes.size() == 1 && noteItem.getTitle()==""){
            customLayoutBottomAddNotesBinding.recyclerViewEditNote.setPadding(0,0,0,0);
        }

        bottomSheetDialog.getBehavior().setDraggable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        //BẮT SỰ KIỆN CLICK TRÊN CHIP ĐẶT THỜI GIAN BÁO THỨC
        customLayoutBottomAddNotesBinding.chipAlarmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MỞ BOTTOM SHEET HẸN GIỜ BÁO THỨC
                BottomSheetDialogFragmentSetTimeNotify dialog =
                        new BottomSheetDialogFragmentSetTimeNotify(valueResources, view.getContext(), noteItem,customLayoutBottomAddNotesBinding);
                dialog.show(fragmentManager, dialog.getTag());
            }
        });
        //BẮT SỰ KIỆN CLICK TRÊN CHIP ĐẶT THỜI GIAN BÁO THỨC
        customLayoutBottomAddNotesBinding.chipAlarmText.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteItem.setTimeNotify("");
                noteItem.setDateNotify(null);
                customLayoutBottomAddNotesBinding.chipAlarmText.setText("Đặt báo thức");
            }
        });

        //BẮT SỰ KIỆN HOÀN THÀNH SỬA NHIỆM VỤ
        customLayoutBottomAddNotesBinding.buttonCompleteEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!updated){
                    CompletedEdit();
                }
            }
        });


        return bottomSheetDialog;
    }

    //HÀM THỰC THI HOÀN THÀNH SỬA NHIỆM VỤ
    @SuppressLint("NotifyDataSetChanged")
    public void CompletedEdit(){
        fromFixNotes = false;
        bottomSheetDialog.dismiss();
        noteItem.setTitle(Objects.requireNonNull(customLayoutBottomAddNotesBinding.textViewNoteTitle.getText()).toString());
        if(noteItem.getTitle().trim().isEmpty()){
            if(noteItem.getListNotes().size()==1){
                noteItem.setTitle("");
                if(!noteItem.getExpandable()){
                    noteItem.setExpandable(true);
                }
            }
            if(noteItem.getListNotes().size()!=1){
                noteItem.customItemNotesBinding.textViewNotes.setText("Danh sách việc cần làm");
            }
        }

        Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).returnTempToMainData();
        AddNotesFragment.editChildrenItemId(noteItem);
        if(noteItem.getListNotes().size() == 1 && noteItem.getListNotes().get(0).getText().isEmpty()){
            int pos = AddNotesFragment.findPositionItemWithId(noteItem.getId());
            databaseSaveNoteItems.deleteNoteItem(noteItem.getId());
            lists.remove(AddNotesFragment.findItemWithId(noteItem.getId()));
            if(pos != -1){
                Objects.requireNonNull(listsObservable.get()).notifyItemRemoved(pos);
            }
        }
        else{
            noteItem.setNumberItemCheck(String.valueOf(countItemCheck(noteItem.getListNotes())));

            if(noteItem.getDateNotify() != null){

                long timeCountDown = calculateTimeBetweenTwoDate(noteItem);

                if(timeCountDown < 0){
                    noteItem.setOverTime(true);
                }
                else{
                    noteItem.setOverTime(false);
                }
            }

            Objects.requireNonNull(noteItem.notesItemBottomSheetAdapterObservableField.get()).notifyDataSetChanged();
            Objects.requireNonNull(noteItem.noteItemChildrenAdapter.get()).notifyDataSetChanged();

            if(noteItem.getTitle().isEmpty() && noteItem.getListNotes().size() == 1 && !noteItem.getTimeNotify().isEmpty()){
                noteItem.customItemNotesBinding.recyclerChildrenNotes.setPadding(0,0,0,-20);
            }
            else if(noteItem.getTitle().isEmpty() && noteItem.getListNotes().size() == 1 && noteItem.getTimeNotify().isEmpty()){
                noteItem.customItemNotesBinding.recyclerChildrenNotes.setPadding(0,32,0,-30);
            }
            else{
                noteItem.customItemNotesBinding.recyclerChildrenNotes.setPadding(80,0,0,0);
            }

            if(!noteItem.getTimeNotify().isEmpty()){
                noteItem.setTimeNotifyNote(customLayoutBottomAddNotesBinding.chipAlarmText.getText().toString());
            }
            else{
                noteItem.setTimeNotifyNote("");
            }
            updated = true;
            //UPDATE ITEM VỪA CHỈNH SỬA Ở DATABASE
            databaseSaveNoteItems.updateNoteItem(noteItem);
            //BẮT ĐẦU CHO ITEM VỪA CHỈNH SỬA CHẠY BACKGROUD VỚI JOB SCHEDULER
            StartJobScheduler();
        }
    }

    //BẮT ĐẦU CHẠY BACKGROUND ITEM VỪA CHỈNH SỬA ĐỂ HIỂN THỊ THÔNG BÁO KHI ĐÚNG GIỜ
    private void StartJobScheduler() {

        if(noteItem.getDateNotify() != null){

            String txt = editTextNotifyOnTime(noteItem);
            Data data = new Data.Builder()
                    .putInt("id",noteItem.getId())
                    .putString("text_notify",txt)
                    .build();
            long timeCountDown = calculateTimeBetweenTwoDate(noteItem);


            if(timeCountDown > 0){
                //HIỂN THỊ THÔNG BÁO KHI ĐÚNG GIỜ SẼ XẢY RA HAI TRƯỜNG HỢP
                //TH1 : NẾU APP KHÔNG BỊ KILL VÀ VẪN ĐANG ĐƯỢC HIỂN THỊ, TỨC LÀ fragmentManager KHÁC NULL
                //
                WorkRequest workRequest;
                if(fragmentManager != null){
                    workRequest = new OneTimeWorkRequest.Builder(ShowSheetNotifyWorkerBottom.class)
                            .addTag("work_request")
                            .setInputData(data)
                            .setInitialDelay(timeCountDown-10000, TimeUnit.MILLISECONDS)
                            .build();
                }
                //TH2 : APP BỊ KILL VÀ fragmentManager LÚC NÀY LÀ NULL
                else{
                    workRequest = new OneTimeWorkRequest.Builder(ShowSheetNotifyWorker.class)
                            .addTag("work_request")
                            .setInputData(data)
                            .setInitialDelay(timeCountDown-10000, TimeUnit.MILLISECONDS)
                            .build();
                }
                //BẮT ĐẦU CHO JOB CHẠY
                WorkManager.getInstance(requireContext()).enqueue(workRequest);
            }
            else{
                //NẾU CHỈNH SỬA THỜI GIAN BÉ HƠN THỜI GIAN HIỆN TẠI THÌ
                //ITEM SẼ SET TRẠNG THÁI OVERTIMER LÀ TRUE
                //VÀ TEXT HIỂN THỊ THỜI GIAN THÔNG BÁO CHUYỂN SANG MÀU ĐỎ
                noteItem.setOverTime(true);
            }
        }
    }

    //ĐẾM SỐ ITEM ĐƯỢC CHECKED
    public  int countItemCheck(List<ChildrenNoteItem> list){
        int a = 0;
        for (ChildrenNoteItem item : list){
            if(item.isChecked()){
                a++;
            }
        }
        return a;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(!updated){
            CompletedEdit();
        }
    }

//    public boolean allItemsAreChecked(List<ChildrenNoteItem> list){
//        for (ChildrenNoteItem item : list){
//            if(!item.isChecked()){
//                return false;
//            }
//        }
//        return true;
//    }

}
