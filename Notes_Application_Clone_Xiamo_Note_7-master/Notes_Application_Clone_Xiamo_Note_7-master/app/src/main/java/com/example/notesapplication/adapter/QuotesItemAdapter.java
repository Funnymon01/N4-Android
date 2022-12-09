package com.example.notesapplication.adapter;

import static com.example.notesapplication.fragment.AddNotesFragment.isHoveredDelete;
import static com.example.notesapplication.fragment.AddQuoteFragment.modeDelete;
import static com.example.notesapplication.main.NotesActivityMain.listQuotes;
import static com.example.notesapplication.main.NotesActivityMain.viewPager2;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapplication.R;
import com.example.notesapplication.databinding.BottomSheetAddQuotesLayoutBinding;
import com.example.notesapplication.databinding.QuotesItemLayoutBinding;
import com.example.notesapplication.fragment.AddQuoteFragment;
import com.example.notesapplication.main.NotesActivityMain;
import com.example.notesapplication.model.QuoteItem;
import com.example.notesapplication.roomdatabase.DatabaseObject;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuotesItemAdapter extends RecyclerView.Adapter<QuotesItemAdapter.ViewHolder> implements Filterable {

    //THUỘC TÍNH
    //DANH SÁCH CÁCH GHI CHÚ
    public  List<QuoteItem> quoteItemList;
    //DANH SÁCH CÁC GHI CHÚ BAN ĐẦU, SẼ ĐƯỢC GÁN CHO DANH SÁCH CÁC GHI CHÚ
    //MỖI KHI KẾT QUẢ FILTER TRẢ VỀ LÀ NULL
    public  List<QuoteItem> quoteItemListOld;

    //HÀM KHỞI TẠO
    public QuotesItemAdapter(){
        this.quoteItemList = new ArrayList<>();
        this.quoteItemListOld = new ArrayList<>();
    }

    //SET DANH SÁCH CÁC GHI CHÚ
    @SuppressLint("NotifyDataSetChanged")
    public void setQuoteItemList(List<QuoteItem> quoteItemList){
        this.quoteItemList = new ArrayList<>(quoteItemList);
        this.quoteItemListOld = new ArrayList<>(quoteItemList);
        notifyDataSetChanged();
    }

    //THÊM GHI CHÚ MỚI
    public void addNewQuotesItem(QuoteItem item){
        int pos = quoteItemList.size();
        quoteItemList.add(pos,item);
        quoteItemListOld.add(pos,item);
        listQuotes.add(pos,item);
        notifyItemInserted(pos);
    }

    //CLEAR DANH SÁCH CÁC GHI CHÚ
    @SuppressLint("NotifyDataSetChanged")
    public void clearQuoteItems(){
        quoteItemList.clear();
        quoteItemListOld.clear();
        notifyDataSetChanged();
    }

    //XÓA DANH SÁCH CÁC GHI CHÚ
    @SuppressLint("NotifyDataSetChanged")
    public void removeQuoteItems(List<QuoteItem> items){
        quoteItemList.removeAll(items);
        quoteItemListOld.removeAll(items);
        notifyDataSetChanged();
        for (QuoteItem item : quoteItemList){
            item.setStateCheckedToDelete(false);
            item.setStateChecked(false);
        }
        for (QuoteItem item : quoteItemListOld){
            item.setStateCheckedToDelete(false);
            item.setStateChecked(false);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void returnToCurrentQuoteItems(){
        quoteItemList = new ArrayList<>(quoteItemListOld);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QuotesItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.quotes_item_layout,parent,false);
        return new ViewHolder(binding, quoteItemList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuoteItem item = quoteItemList.get(position);
        holder.binding.setQuotes(item);
        holder.setQuoteItem(item);
    }

    @Override
    public int getItemCount() {
        return quoteItemList.size();
    }

    // HÀM FILER
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String key = charSequence.toString().trim().toLowerCase();
                List<QuoteItem> filterItemsQuote = new ArrayList<>();
                //NẾU KEYWORD CỦA NGƯỜI DÙNG TRUYỀN VÀO KHÁC RỖNG
                //THÌ TÌM KIẾM THỬ XEM TRONG DANH SÁCH GHI CHÚ CÓ KEYWORD NÀO TRÙNG
                //THÌ THÊM VÀO 1 DANH SÁCH RIÊNG
                if(!key.trim().isEmpty()){
                    for (QuoteItem item : quoteItemListOld){
                        if(item.getQuotes().toLowerCase().contains(key)){
                            filterItemsQuote.add(item);
                        }
                    }
                    //SAU ĐÓ GÁN DANH SÁCH VỪA LỌC ĐƯỢC CHO DANH SÁCH CHÍNH
                    quoteItemList = filterItemsQuote;
                }
                //NẾU KEYWORD TRUYỀN VÀO RỖNG THÌ TRẢ VỀ DANH SÁCH CÁC ITEMS BAN ĐẦU
                else{
                    quoteItemList = quoteItemListOld;
                }

                //TRẢ VỀ DANH SÁCH CÁC ITEMS TRÙNG VỚI KEYWROD
                FilterResults results = new FilterResults();
                results.values = filterItemsQuote;

                return results;
            }

            //NHẬN KẾT QUẢ TRẢ VỀ CỦA FILTER PHÍA TRÊN
            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //GÁN DANH SÁCH VÀ UPDATE LÊN VIEWS
                quoteItemList = (List<QuoteItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    //THAY ĐỔI TRẠNG THÁI ĐƯỢC CHỌN HAY KHÔNG CỦA CÁC ITEMS GHI CHÚ
    public void resetStateTo(boolean bool){
        for (QuoteItem item : quoteItemList){
            item.setStateCheckedToDelete(bool);
            item.setStateChecked(bool);
        }
    }


    //CLASS VIEW HOLDER
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        QuoteItem quoteItem;
        QuotesItemLayoutBinding binding;
        List<QuoteItem> quoteItemList;
        public ViewHolder(@NonNull QuotesItemLayoutBinding binding, List<QuoteItem> quoteItemList) {
            super(binding.getRoot());
            this.binding = binding;
            this.quoteItemList = quoteItemList;
            binding.getRoot().setOnClickListener(this);
            binding.getRoot().setOnLongClickListener(this);

            binding.radioButtonCheckDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quoteItem.setStateChecked(!quoteItem.isStateChecked());
                }
            });
        }

        public void setQuoteItem(QuoteItem item){
            this.quoteItem = item;
        }

        //ON CLICK
        @Override
        public void onClick(View view) {
            //NẾU ĐANG Ở TRẠNG THÁI XÓA
            if(modeDelete.get()){
                quoteItem.setStateChecked(!quoteItem.isStateChecked());
            }
            //NẾU ĐANG Ở TRẠNG THÁI LÀ BÌNH THƯỜNG
            else{
                //BOTTOM SHEET CHỈNH SỬA GHI CHÚ
                BottomSheetDialog bottomSheetDialogUpdateQuote = new BottomSheetDialog(view.getContext(),R.style.CustomBottomSheetDialog);
                BottomSheetAddQuotesLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(view.getContext()),R.layout.bottom_sheet_add_quotes_layout,null,false);
                bottomSheetDialogUpdateQuote.setContentView(binding.getRoot());
                binding.textInputQuotes.requestFocus();
                binding.textInputQuotes.setText(quoteItem.getQuotes());

                //BẮT SỰ KIỆN HOÀN THÀNH CHỈNH SỬA GHI CHÚ
                binding.buttonCompleteEditNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //LẤY RA ITEM ĐANG ĐƯỢC CHỈNH SỬA
                        //VÀ THAY ĐỔI DỮ LIỆU MỚI CHO NÓ
                        QuoteItem item = listQuotes.get(getAdapterPosition());
                        item.setQuotes(Objects.requireNonNull(binding.textInputQuotes.getText()).toString().trim());
                        item.setTimeSetQuotes(AddQuoteFragment.getTimeCurrentInString());
                        //ĐỒNG THỜI CŨNG PHẢI UPDATE DỮ LIỆU Ở DATABASE
                        DatabaseObject.getInstance(view.getContext()).roomDatabaseDao().updateQuoteItem(item);
                        bottomSheetDialogUpdateQuote.dismiss();
                    }
                });

                bottomSheetDialogUpdateQuote.show();
            }
        }

        //BẮT SỰ KIỆN LONG CLICK
        @Override
        public boolean onLongClick(View view) {
            viewPager2.setUserInputEnabled(false);
            modeDelete.set(true);
            resetStateTo(true);
            quoteItem.setStateCheckedToDelete(true);
            quoteItem.setStateChecked(true);

            NotesActivityMain.showTabLayout(View.GONE);
            NotesActivityMain.showTopLayoutDelete(View.VISIBLE);
            NotesActivityMain.showBottomLayoutDelete(View.VISIBLE);
            NotesActivityMain.showButtonAddNotes(View.GONE);
            NotesActivityMain.resetStateExpandableAllItems();

            return true;
        }

        //THAY ĐỔI TRẠNG THÁI DANH SÁCH CÁC GHI CHÚ
        //NẾU BIẾN TRUYỀN VÀO LÀ TRUE THÌ CHUYỂN TRẠNG THÁI CHECKED LÀ TRUE
        //VÀ NGƯỢC LẠI
        public void resetStateTo(boolean bool){
            for (QuoteItem item : quoteItemList){
                item.setStateCheckedToDelete(bool);
            }
        }

    }
}
