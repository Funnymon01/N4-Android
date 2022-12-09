package com.example.notesapplication.bottomSheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.notesapplication.model.NoteItem;
import com.example.notesapplication.R;
import com.example.notesapplication.resources.ValueResources;
import com.example.notesapplication.databinding.CustomBottomSheetDateTimePickerBinding;
import com.example.notesapplication.databinding.CustomLayoutBottomAddNotesBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


//BOTTOM SHEET HIỂN THỊ ĐỂ CHỌN CÁC KHUNG THỜI GIAN THÔNG BÁO
public class BottomSheetDialogFragmentSetTimeNotify extends BottomSheetDialogFragment {

    //CÁC THUỘC TÍNH
    ValueResources valueResources;
    Context context;
    NoteItem noteItem;
    CustomBottomSheetDateTimePickerBinding customBottomSheetDateTimePickerBinding;
    CustomLayoutBottomAddNotesBinding customLayoutBottomAddNotesBinding;
    String textNotifyReturn = "";

    //HÀM KHỞI TẠO
    public BottomSheetDialogFragmentSetTimeNotify(ValueResources valueResources,Context context,NoteItem noteItem,CustomLayoutBottomAddNotesBinding customLayoutBottomAddNotesBinding){
        this.valueResources = valueResources;
        this.context = context;
        this.noteItem = noteItem;
        this.customLayoutBottomAddNotesBinding = customLayoutBottomAddNotesBinding;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialogChip = new BottomSheetDialog(context, R.style.CustomBottomSheetDialog);
        customBottomSheetDateTimePickerBinding
                = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.custom_bottom_sheet_date_time_picker,null,false);

        customBottomSheetDateTimePickerBinding.setValuesResources(valueResources);
        customBottomSheetDateTimePickerBinding.setNoteItem(noteItem);


        //MỖI KHI SET THỜI GIAN THÔNG BÁO THÌ ITEM ĐƯỢC CHỌN SẼ ĐƯỢC GỬI QUA SHEET NÀY
        //QUA HÀM KHỞI TẠO
        //NẾU ITEM ĐƯỢC CHỌN CHƯA ĐƯỢC SET THỜI GIAN
        //THÌ MẶC ĐỊNH SẼ HIỂN THỊ NGÀY VÀ GIỜ LẤY DỰA THEO THỜI GIAN THỰC CỦA HỆ THỐNG
        if(noteItem.getTimeNotify().isEmpty()){
            Calendar calendar = Calendar.getInstance();
            //LẤY THỜI THỰC HỆ THỐNG CHUYỂN SANG CHUỖI STRING
            customBottomSheetDateTimePickerBinding.textViewDate.setText(
                    valueResources.convertDateToDateString(calendar.getTime())+" "
            );
            //SET THỜI GIAN THỰC LÊN VIEW
            customBottomSheetDateTimePickerBinding.textViewTime.setText(" "+
                    calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
        }
        else{
            //TRƯỜNG HỢP ITEM ĐÃ ĐƯỢC SET THỜI GIAN
            //THÌ TA SẼ LẤY THỜI GIAN ĐÃ SET ĐÓ
            //SET LÊN CÁC VIEW HIỂN THỊ THỜI GIAN ĐÃ SET ĐÓ

            String currentStringDate = valueResources.convertDateToStringFormatStandard(noteItem.getDateNotify());
            int index = valueResources.dateFormatStandardList.indexOf(currentStringDate);

            int year = Integer.parseInt(valueResources.dateFormatStandardList.get(index).split("/")[2]);

            if(year == Calendar.getInstance().get(Calendar.YEAR)){
                customBottomSheetDateTimePickerBinding.textViewDate.setText(valueResources.getItemListDateString(index)+" ");
            }
            else{
                customBottomSheetDateTimePickerBinding.textViewDate.setText(
                        valueResources.getItemListDateString(index) +", "+year+" ");
            }

            customBottomSheetDateTimePickerBinding.textViewTime.setText(" "+noteItem.getTimeNotify());
        }

        //SET VIEW
        bottomSheetDialogChip.setContentView(customBottomSheetDateTimePickerBinding.getRoot());
        //DISABLE DRAG CỦA SHEET
        bottomSheetDialogChip.getBehavior().setDraggable(false);

        bottomSheetDialogChip.show();

        //BẮT SỰ KIỆN BUTTON HỦY ĐẶT HẸN GIỜ THÔNG BÁO
        customBottomSheetDateTimePickerBinding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialogChip.dismiss();
            }
        });


        //BẮT SỰ KIỆN BUTTON ĐỒNG Ý HẸN GIỜ THÔNG BÁO
        customBottomSheetDateTimePickerBinding.buttonOk.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                bottomSheetDialogChip.dismiss();
                //LẤY RA THỜI GIAN MÀ NGƯỜI DÙNG ĐÃ CHỌN
                //NGÀY
                int datePos = customBottomSheetDateTimePickerBinding.numberPicker.getValue();
                //GIỜ
                int hoursValuePos = customBottomSheetDateTimePickerBinding.numberPickerHours.getValue();
                //PHÚT
                int minutesValuePos = customBottomSheetDateTimePickerBinding.numberPickerMinutes.getValue();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM");

                String dateTxt = valueResources.getItemDateFormatStandard(datePos);

                //SET THỜI GIAN ĐÃ CHỌN LÊN CHIP TEXT
                if(Integer.parseInt(dateTxt.split("/")[2]) == Calendar.getInstance().get(Calendar.YEAR)){
                    customLayoutBottomAddNotesBinding.chipAlarmText.setText(simpleDateFormat.format(valueResources.getItemListDateDate(datePos))+" "+(hoursValuePos < 10 ? "0"+hoursValuePos : hoursValuePos)+":"+(minutesValuePos < 10 ? "0"+minutesValuePos : minutesValuePos));
                }
                else{
                    customLayoutBottomAddNotesBinding.chipAlarmText.setText(simpleDateFormat.format(valueResources.getItemListDateDate(datePos))+"/"+dateTxt.split("/")[2]+" "+(hoursValuePos < 10 ? "0"+hoursValuePos : hoursValuePos)+":"+(minutesValuePos < 10 ? "0"+minutesValuePos : minutesValuePos));
                }

                //SET THỜI GIAN HẸN THÔNG BÁO
                noteItem.setDateNotify(valueResources.getItemListDateDate(datePos));
                noteItem.setTimeNotify(String.valueOf((hoursValuePos<10?"0"+hoursValuePos:hoursValuePos)+":"+(minutesValuePos<10?"0"+minutesValuePos:minutesValuePos)));

            }
        });

        //BẮT SỰ KIỆN SCROLL TRÊN PICKER CHỌN NGÀY
        customBottomSheetDateTimePickerBinding.numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldPos, int newPos) {

                //MỖI KHI THAY ĐỔI THÌ SẼ LẤY THỜI GIAN UP LÊN VIEW ĐỂ HIỆN THỊ
                //NGÀY MÀ NGƯỜI DÙNG ĐÃ CHỌN
                Date date = valueResources.getItemListDateDate(newPos);
                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                customBottomSheetDateTimePickerBinding.textViewDate.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        customBottomSheetDateTimePickerBinding.textViewDate.setText(
                                valueResources.getItemListDateString(numberPicker.getValue())
                                        +
                                        ((simpleDateFormat.format(date).equals(String.valueOf(calendar.get(Calendar.YEAR)))) ? "" : ", "+simpleDateFormat.format(date))+" ");
                    }
                });
            }
        });

        //BẮT SỰ KIỆN SCROLL TRÊN PICKER CHỌN GIỜ
        customBottomSheetDateTimePickerBinding.numberPickerHours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldPos, int newPos) {
                //MỖI KHI THAY ĐỔI THÌ SẼ UP LÊN TEXT HIỂN THỊ GIỜ MÀ NGƯỜI DÙNG ĐÃ THAY ĐỔI
                String text = customBottomSheetDateTimePickerBinding.textViewTime.getText().toString();
                customBottomSheetDateTimePickerBinding.textViewTime.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        customBottomSheetDateTimePickerBinding.textViewTime
                                .setText(newPos+":"+text.split(":")[1]);
                    }
                });
            }
        });

        //BẮT SỰ KIỆN SCROLL TRÊN PICKER CHỌN PHÚT
        customBottomSheetDateTimePickerBinding.numberPickerMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldPos, int newPos) {
                //MỖI KHI THAY ĐỔI THÌ SẼ UP LÊN TEXT HIỂN THỊ PHÚT MÀ NGƯỜI DÙNG ĐÃ THAY ĐỔI
                String text = customBottomSheetDateTimePickerBinding.textViewTime.getText().toString();
                customBottomSheetDateTimePickerBinding.textViewTime.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        customBottomSheetDateTimePickerBinding.textViewTime
                                .setText(text.split(":")[0]+":"+(newPos < 10 ? "0"+newPos : newPos));
                    }
                });
            }
        });
        return  bottomSheetDialogChip;
    }
}
