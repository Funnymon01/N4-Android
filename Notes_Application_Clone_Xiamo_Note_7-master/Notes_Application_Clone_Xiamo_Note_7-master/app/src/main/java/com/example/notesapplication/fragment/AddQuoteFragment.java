package com.example.notesapplication.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.notesapplication.R;
import com.example.notesapplication.adapter.QuotesItemAdapter;
import com.example.notesapplication.databinding.BottomSheetAddQuotesLayoutBinding;
import com.example.notesapplication.databinding.FragmentAddQuoteBinding;
import com.example.notesapplication.model.QuoteItem;
import com.example.notesapplication.roomdatabase.DatabaseObject;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AddQuoteFragment extends Fragment {

    //ADAPTER ĐỂ HIỂN THỊ DANH SÁCH CÁC GHI CHÚ
    public static ObservableField<QuotesItemAdapter> quotesItemAdapterObservableField;
    QuotesItemAdapter quotesItemAdapter;
    public static FragmentAddQuoteBinding binding;
    public static ObservableField<Integer> sizeItemsQuote;
    public static ObservableField<Boolean> modeDelete;
    List<QuoteItem> listQuotes;

    public AddQuoteFragment(List<QuoteItem> listQuotes) {
        // Required empty public constructor
        this.listQuotes = listQuotes;
        quotesItemAdapter = new QuotesItemAdapter();
        quotesItemAdapterObservableField = new ObservableField<>(quotesItemAdapter);
        modeDelete = new ObservableField<>(false);
        sizeItemsQuote = new ObservableField<>(0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.fragment_add_quote,container,false);

        binding.setAddQuotesFragmentItem(this);
        //KHỞI TẠO ADAPTER
        initItemsInRecyclerView();


        //INIT CLICK FOR SEARCH VIEW
        binding.searchViewFilterAdapter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                quotesItemAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.trim().isEmpty() ){
                    quotesItemAdapter.returnToCurrentQuoteItems();
                }
                else{
                    quotesItemAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });





        return binding.getRoot();
    }

    public void initItemsInRecyclerView(){
        quotesItemAdapter = new QuotesItemAdapter();
        quotesItemAdapter.setQuoteItemList(listQuotes);
        quotesItemAdapterObservableField.set(quotesItemAdapter);
        sizeItemsQuote.set(listQuotes.size());
    }


    //BOTTOM SHEET THÊM THÔNG BÁO KHÔNG CÓ BÁO THỨC
    public void OpenBottomSheetAddQuotes(){
        //TẠO 1 BOTTOM SHEET DIALOG
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(),R.style.CustomBottomSheetDialog);
        //KHỞI TẠO LAYOUT CỦA BOTTM SHEET THÊM GHI CHÚ (DATA BINDING / VIEW BINDING)
        BottomSheetAddQuotesLayoutBinding bottomSheetAddQuotesLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.bottom_sheet_add_quotes_layout, null, false);
        //SET VIEW VỪA KHỞI TẠO LÊN CHO BOTTOM SHEET DIALOG
        bottomSheetDialog.setContentView(bottomSheetAddQuotesLayoutBinding.getRoot());


        //BẮT SỰ KIỆN TRÊN BUTTON HOÀN THÀNH ĐỂ THÊM GHI CHÚ
        bottomSheetAddQuotesLayoutBinding.buttonCompleteEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LẤY TEXT NGƯỜI DÙNG NHẬP VÀO
                String textQuotes = Objects.requireNonNull(bottomSheetAddQuotesLayoutBinding.textInputQuotes.getText()).toString();
                //NẾU TEXT KHÁC RỖNG THÌ THÊM GHI CHÚ, KHÔNG THÌ THÔI
                if(!textQuotes.trim().isEmpty()){
                    //HÀM THÊM GHI CHÚ
                    addNewQuotesItem(textQuotes);
                    sizeItemsQuote.set(1);
                    bottomSheetDialog.dismiss();
                }
            }
        });
        bottomSheetDialog.show();
        bottomSheetAddQuotesLayoutBinding.textInputQuotes.requestFocus();
    }


    public void addNewQuotesItem(String textQuotes){
        //KHỞI TẠO 1 OBJECT LÀ QuoteItem VÀ GÁN TEXT NGƯỜI DÙNG NHẬP VÀO CHO ITEM
        QuoteItem item = new QuoteItem(textQuotes,getTimeCurrentInString(),false,false);
        //THÊM ITEM VÀO ADAPTER
        quotesItemAdapter.addNewQuotesItem(item);
        //THÊM GHI CHÚ VÀO DATABASE
        DatabaseObject.getInstance(requireContext().getApplicationContext()).roomDatabaseDao().insertQuotesItem(item);
    }

    public void returnToCurrentQuoteItems(){
        quotesItemAdapter.returnToCurrentQuoteItems();
    }

    public void resetStateAllQuoteItems(boolean bool){
        quotesItemAdapter.resetStateTo(bool);
    }

    public static String getTimeCurrentInString(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        return format.format(Calendar.getInstance().getTime());
    }


}