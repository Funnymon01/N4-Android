package com.example.notesapplication.adapter;

import static com.example.notesapplication.fragment.AddNotesFragment.isHoveredDelete;
import static com.example.notesapplication.main.NotesActivityMain.fragmentManager;
import static com.example.notesapplication.main.NotesActivityMain.viewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapplication.databinding.CustomItemNotesBinding;
import com.example.notesapplication.main.NotesActivityMain;
import com.example.notesapplication.R;
import com.example.notesapplication.resources.ValueResources;
import com.example.notesapplication.bottomSheet.BottomSheetDialogFragmentFixNotes;
import com.example.notesapplication.model.ChildrenNoteItem;
import com.example.notesapplication.model.NoteItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NotesItemAdapter extends RecyclerView.Adapter<NotesItemAdapter.ViewHolder> implements Filterable {

    //CÁC THUỘC TÍNH
    public List<NoteItem> noteItemList;
    public List<NoteItem> noteItemListReserve = new ArrayList<>();
    Context context;
    //BIẾN CHỨA CÁC HÀM VÀ THUỘC TÍNH DÙNG CHUNG
    public static ValueResources valueResources;
    //BIẾN KIỂM TRA TRẠNG THÁI LAYOUT DELETE ĐANG ĐƯỢC SHOW HAY KHÔNG
    public static ObservableField<Boolean> isShowed = new ObservableField<>();

    //HÀM KHỞI TẠO
    public NotesItemAdapter(List<NoteItem> lists,Context context){
        valueResources = new ValueResources();
        noteItemList = lists;
        noteItemListReserve = lists;
        this.context = context;
        //SHARED PREFERENCES
        SharedPreferences data = context.getSharedPreferences("data_state",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putBoolean("fromChildrenCheckBox",false);
        editor.commit();
        isShowed.set(false);
    }

    //TẠO VIEW HOLDER
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomItemNotesBinding customItemNotesBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext())
                , R.layout.custom_item_notes,parent,false
        );
        return new ViewHolder(customItemNotesBinding,this,noteItemList);
    }


    //LẤY VÀ GÁN DỮ LIỆU LÊN CHO VIEW
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteItem noteItem = noteItemList.get(position);
        noteItem.setCustomItemNotesBinding(holder.customItemNotesBinding);
        holder.customItemNotesBinding.setNoteItem(noteItem);
        if(noteItem.getTitle().isEmpty() && noteItem.listNotes.size()==1){
            noteItem.setExpandable(true);
            noteItem.setTitle("");
            holder.customItemNotesBinding.recyclerChildrenNotes.setVisibility(View.VISIBLE);
            holder.customItemNotesBinding.textViewOnTimeNotes.setVisibility(View.GONE);
            holder.customItemNotesBinding.textViewOnTimeNotesBelow.setVisibility(View.VISIBLE);
            holder.customItemNotesBinding.recyclerChildrenNotes.setPadding(80,0,0,0);
        }
    }



    @Override
    public int getItemCount() {
        return noteItemList.size();
    }

    //FILTER : TÌM KIẾM ITEM DỰA THEO TIÊU ĐỀ HOẶC TÊN CỦA CÁC ITEM CON
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                //CHUỖI KEYWORD NGƯỜI DÙNG NHẬP VÀO
                String usersInput = charSequence.toString();
                //NẾU RỖNG THÌ TRẢ VỀ DANH SÁCH BAN ĐẦU
                if(usersInput.trim().isEmpty()){
                    noteItemList = noteItemListReserve;
                }
                //KHÔNG RỖNG THÌ BẮT ĐẦU LỌC
                else{
                    List<NoteItem> noteItemsPos = new ArrayList<>();
                    for (int i = 0 ; i < noteItemListReserve.size() ; i++){
                        NoteItem item = noteItemListReserve.get(i);
                        //NẾU TITLE CỦA ITEM ĐÓ TRÙNG VỚI KEYWORD THÌ THÊM VÀO DANH SÁCH RIÊNG
                        if(item.getTitle().toLowerCase(Locale.ROOT).contains(usersInput.toLowerCase(Locale.ROOT))){
                            noteItemsPos.add(item);
                        }
                        else{
                            //NẾU TITLE CỦA CÁC ITEM CONG ĐÓ TRÙNG VỚI KEYWORD THÌ THÊM VÀO DANH SÁCH RIÊNG
                            for (ChildrenNoteItem a : item.getListNotes()){
                                if(a.getText().toLowerCase(Locale.ROOT).contains(usersInput.toLowerCase(Locale.ROOT))){
                                    noteItemsPos.add(item);
                                }
                            }
                        }
                    }
                    noteItemList = noteItemsPos;
                }

                //TRẢ VỀ DANH SÁCH CÁC ITEMS TRÙNG VỚI KEYWORD
                FilterResults filterResults = new FilterResults();
                filterResults.values = noteItemList;

                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //GÁN DỮ LIỆU VÀ UPDATE VIEWS
                noteItemList = (List<NoteItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    //VIEW HOLDER
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        CustomItemNotesBinding customItemNotesBinding;
        boolean textChanged = false;
        NotesItemAdapter adapter;
        List<NoteItem> noteItemList;
        boolean updated = false;

        public ViewHolder(@NonNull CustomItemNotesBinding itemView,NotesItemAdapter adapter,List<NoteItem> noteItemList) {
            super(itemView.getRoot());
            this.customItemNotesBinding = itemView;
            this.adapter = adapter;
            this.noteItemList = noteItemList;
            itemView.getRoot().setOnClickListener(this);
            itemView.getRoot().setOnLongClickListener(this);
            itemView.itemNote.setOnLongClickListener(this);
            itemView.layoutSub.setOnLongClickListener(this);
            itemView.checkBoxNotes.setOnLongClickListener(this);
            itemView.textViewNotes.setOnLongClickListener(this);
            itemView.textViewOnTimeNotes.setOnLongClickListener(this);
            itemView.recyclerChildrenNotes.setOnLongClickListener(this);
            itemView.textViewOnTimeNotesBelow.setOnLongClickListener(this);

            onClickCallBack(itemView.getRoot());
        }

        //BẮT SỰ KIỆN CLICK
        @Override
        public void onClick(View view) {
            if(!isShowed.get()){
                textChanged = true;
                updated = false;
                Toast.makeText(itemView.getContext(),"PARENT CLICKED +",Toast.LENGTH_LONG).show();
                //MỞ BOTTOM SHEET CHỈNH SỬA NHIỆM VỤ
                OpenBottomDialog(view);
            }
            else{
                noteItemList.get(getAdapterPosition()).setHoveredToDelete(!noteItemList.get(getAdapterPosition()).isHoveredToDelete());
                NotesActivityMain.updateTextCountNumberItemsChecked(1);
            }
        }

        //BẮT SỰ KIỆN LONG CLICK
        @Override
        public boolean onLongClick(View view) {
            viewPager2.setUserInputEnabled(false);
            onLongClickItem();
            return true;
        }


        public void onLongClickItem(){
            if(!isShowed.get()){
                NotesActivityMain.showTabLayout(View.GONE);
                NotesActivityMain.showTopLayoutDelete(View.VISIBLE);
                NotesActivityMain.showBottomLayoutDelete(View.VISIBLE);
                NotesActivityMain.showButtonAddNotes(View.GONE);
                NotesActivityMain.resetStateExpandableAllItems();
                isShowed.set(true);
            }
            noteItemList.get(getAdapterPosition()).setHoveredToDelete(true);
            NotesActivityMain.updateTextCountNumberItemsChecked(1);
            isHoveredDelete.set(true);
        }



        //CALLBACK ON CLICK
        @SuppressLint("ClickableViewAccessibility")
        public void onClickCallBack(View view){
            customItemNotesBinding.circleButtonShowRecyclerChildren.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(customItemNotesBinding.getNoteItem().getExpandable()){
                        customItemNotesBinding.getNoteItem().setExpandable(false);
                        customItemNotesBinding.circleButtonShowRecyclerChildren.setImageResource(R.drawable.custom_background_expandable_less);
                    }
                    else{
                        customItemNotesBinding.getNoteItem().setExpandable(true);
                        customItemNotesBinding.circleButtonShowRecyclerChildren.setImageResource(R.drawable.custom_background_expandable_more);
                    }

                }
            });

            //BẮT SỰ KIỆN TEXT CHANGE CỦA ITEM MỖI KHI ĐƯỢC CHỈNH SỬA
            customItemNotesBinding.textViewNotes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.toString().isEmpty()){
                        customItemNotesBinding.textViewNotes.setText("Danh sách việc cần làm");
                        customItemNotesBinding.getNoteItem().setTitle("");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //BẮT SỰ KIỆN CHECKED CỦA CHECKBOX MỖI KHI THAY ĐỔI
            customItemNotesBinding.checkBoxNotes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    SharedPreferences sharedPreferences = compoundButton.getContext().getSharedPreferences("data_state",Context.MODE_PRIVATE);
                    boolean state = sharedPreferences.getBoolean("fromChildrenCheckBox",false);

                    customItemNotesBinding.getNoteItem().setChecked(b);

                    if(!state){
                        for (ChildrenNoteItem item : customItemNotesBinding.getNoteItem().getListNotes()){
                            if(item.isChecked() != b){
                                item.setChecked(b);
                            }
                        }
                    }

                    if(b){
                        customItemNotesBinding.textViewNotes.setTextColor(view.getContext().getResources().getColor(R.color.brown,null));
                        customItemNotesBinding.getNoteItem().setExpandable(false);
                    }
                    else{
                        customItemNotesBinding.textViewNotes.setTextColor(view.getContext().getResources().getColor(R.color.black,null));
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("fromChildrenCheckBox",false);
                    editor.commit();
                    //CHANGE NUMBER ITEM CHECKED
                    customItemNotesBinding.getNoteItem().setNumberItemCheck
                            (String.valueOf(countItemCheck(customItemNotesBinding.getNoteItem().getListNotes())));
                }
            });

            //BẮT SỰ KIỆN CHECK CỦA BUTTON CHỌN XÓA
            customItemNotesBinding.radioButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noteItemList.get(getAdapterPosition()).setHoveredToDelete(!noteItemList.get(getAdapterPosition()).isHoveredToDelete());
                    NotesActivityMain.updateTextCountNumberItemsChecked(1);
                }
            });
        }


        //HIỂN THỊ BOTTOM SHEET CHỈNH SỬA ITEMS
        public void OpenBottomDialog(View view){

            NoteItem noteItem = noteItemList.get(getAdapterPosition());
            //BOTTOM SHEET CHỈNH SỬA NHIỆM VỤ
            BottomSheetDialogFragmentFixNotes dialog = new BottomSheetDialogFragmentFixNotes(view.getContext(), noteItem);
            dialog.show(fragmentManager, dialog.getTag());
        }

        //ĐẾM SỐ ITEMS ĐƯỢC CHECKED
        public  int countItemCheck(List<ChildrenNoteItem> list){
            int a = 0;
            for (ChildrenNoteItem item : list){
                if(item.isChecked()){
                    a++;
                }
            }
            return a;
        }
    }
}
