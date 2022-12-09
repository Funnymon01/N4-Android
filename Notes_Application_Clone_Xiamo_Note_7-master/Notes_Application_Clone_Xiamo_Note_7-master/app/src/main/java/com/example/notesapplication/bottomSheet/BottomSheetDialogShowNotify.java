package com.example.notesapplication.bottomSheet;

import static com.example.notesapplication.fragment.AddNotesFragment.databaseSaveNoteItems;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.example.notesapplication.model.NoteItem;
import com.example.notesapplication.R;
import com.example.notesapplication.fragment.AddNotesFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

//BOTTOM SHEET SHOW THÔNG BÁO KHI MỘT NHIỆM VỤ ĐÚNG GIỜ HẸN
//NẾU APP ĐANG MỞ VÀ HIỂN THỊ THÌ SẼ HIỂN THỊ TRỰC TIẾP 1 BOTTM SHEET THÔNG BÁO
//CÒN APP BỊ KILL THÌ SẼ HIỂN THỊ THÔNG BÁO, NẾU NHẤN VÀO THÔNG BÁO THÌ SẼ HIỂN THỊ RA BOTTOM SHEET
public class BottomSheetDialogShowNotify extends BottomSheetDialogFragment {

    //THUỘC TÍNH
    String txtNotify;
    int id;

    //HÀM KHỞI TẠO
    public BottomSheetDialogShowNotify(int id,String text){
        this.id = id;
        this.txtNotify = text;
    }


    //HÀM TẠO DIALOG
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //HỦY THÔNG BÁO
        NotificationManagerCompat.from(requireContext()).cancel(id);
        TextView textViewTextNotify,buttonOkeCloseSheet;

        //INFLATE LAYOUT HIỂN THỊ KHUNG THÔNG BÁO
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_notify_notes_on_time,null,false);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(),R.style.CustomBottomSheetDialog);

        textViewTextNotify = view.findViewById(R.id.textViewNotesShow);
        textViewTextNotify.setText(txtNotify);

        buttonOkeCloseSheet = view.findViewById(R.id.buttonOkeCloseSheet);

        //BẮT SỰ KIỆN TRÊN BUTTON ĐÓNG BOTTOM SHEET
        buttonOkeCloseSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //THAY ĐỔI CHO ITEM
                NoteItem item = AddNotesFragment.findItemWithId(id);
                if(item != null){
                    item.setOverTime(true);
                }
                //THAY ĐỔI STATE Ở DATABASE
                if(databaseSaveNoteItems != null){
                    //UPDATE TRẠNG THÁI CỦA NHIỆM VỤ LÀ ĐÃ THÔNG BÁO
                    databaseSaveNoteItems.updateStateOverTimeNoteItem(id,true);
                }
                bottomSheetDialog.dismiss();
            }
        });

        //GÁN LAYOUT CHO BOTTOM SHEET
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.getBehavior().setDraggable(false);
        //TẮT CHẾ ĐỘ NHẤN BÊN NGOÀI BOTTOM SHEET SẼ ĐÓNG (ĐÓNG SAU KHI NHẤN NÚT OKE)
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        return bottomSheetDialog;
    }
}
