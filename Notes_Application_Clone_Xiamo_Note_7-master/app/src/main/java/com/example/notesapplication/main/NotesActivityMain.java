package com.example.notesapplication.main;

import static com.example.notesapplication.adapter.NotesItemAdapter.isShowed;
import static com.example.notesapplication.fragment.AddNotesFragment.isHoveredDelete;
import static com.example.notesapplication.fragment.AddQuoteFragment.modeDelete;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.notesapplication.R;
import com.example.notesapplication.adapter.FragmentNotesAdapter;
import com.example.notesapplication.database.DatabaseSaveNoteItems;
import com.example.notesapplication.fragment.AddNotesFragment;
import com.example.notesapplication.fragment.AddQuoteFragment;
import com.example.notesapplication.model.NoteItem;
import com.example.notesapplication.model.QuoteItem;
import com.example.notesapplication.roomdatabase.DatabaseObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 *1.CHỨC NĂNG THÊM GHI CHÚ HOẶC NHIỆM VỤ
 *-->DÒNG 224
 *KHI NHẤN VÀO floatingButtonAddNotes SẼ XẢY RA HAI TRƯỜNG HỢP
 *->TRƯỜNG HỢP 1 : TỨC LÀ VIEWPAGER2 ĐANG Ở FRAGMENT ADD QUOTES THÌ SẼ LÀ THÊM GHI CHÚ (KHÔNG CÓ THÔNG BÁO)
 *  CTRL+ NHẤN VÀO HÀM addQuoteFragment.OpenBottomSheetAddQuotes() Ở DÒNG 230 ĐỂ XEM RÕ HƠN
 *->TRƯỜNG HỢP 2 : TỨC LÀ VIEWPAGER2 ĐANG Ở FRAGMENT ADD NOTES THÌ SẼ LÀ THÊM NHIỆM VỤ (CÓ THÔNG BÁO)
 *  CTRL+ NHẤN VÀO HÀM addNotesFragment.OpenBottomSheetDialogAddNotes() Ở DÒNG 235 ĐỂ XEM RÕ HƠN
 *
 *2.CHỨC NĂNG LIỆT KÊ HIỂN THỊ GHI CHÚ, NHIỆM VỤ
 * -->ĐẦU TIÊN TA SẼ LOAD DATA (DANH SÁCH CÁC GHI CHÚ VÀ NHIỆM VỤ) TỪ Ở DƯỚI DATABASE ĐƯA LÊN NẰM Ở DÒNG 309
 * -->TA SẼ TẠO 2 ADAPTER ĐỂ HIỂN THỊ GHI CHÚ (ADAPTER TRONG AddQuotesFragment.java DÒNG 32)
 * VÀ NHIỆM VỤ (ADAPTER TRONG AddNotesFragment.java DÒNG 48)
 *ADAPTER TRONG AddQuotesFragment.java
 *      KHỞI TẠO ADAPTER Ở DÒNG 56
 *      TRONG FILE fragment_add_quote.xml ở view RecyclerView
 *      ta có 1 thuộc tính là quotesAdapter="@{addQuotesFragmentItem.quotesItemAdapterObservableField}"
 *      thuộc tính này sẽ auto set adapter cho RecyclerView, bởi vì trong file BindingUtils
 *      ta có hàm thực hiện set adapter cho recyclerview này là hàm nằm ở dòng 170
 *TƯƠNG TỰ TRONG AddNotesFragment.java
 *      KHỞI TẠO ADAPTER Ở DÒNG 75
 *      TRONG FILE fragment_add_notes.xml ở view RecyclerView
 *      ta có 1 thuộc tính là bind:setAdapter="@{addNotesFragmentItem.listsObservable}"
 *      thuộc tính này sẽ auto set adapter cho RecyclerView, bởi vì trong file BindingUtils
 *      ta có hàm thực hiện set adapter cho recyclerview này là hàm nằm ở dòng 33
 *
 *3.CHỨC NĂNG ĐẶT THỜI GIAN BÁO THỨC
 *      CHỈ ĐỔI VỚI FRAGMENT THÊM NHIỆM VỤ HÀM NÀY NẰM Ở FILE AddNotesFragment.java DÒNG 282 (CÓ GIẢI THÍCH)
 *
 *4.CHỨC NĂNG ĐÁNH DẤU GHI CHÚ ĐÃ HOÀN THÀNH
 *      TRƯỜNG HỢP : fragmentManager != null
 *          TRONG FILE : BottomSheetDialogShowNotify.java DÒNG 64 MỖI KHI ĐÚNG GIỜ THÌ SẼ
 *                      UPDATE TRẠNG THÁI ĐÁNH DẤU Ở DATABASE ĐỒNG THỜI SẼ THAY ĐỔI VIEW CỦA ITEM VỪA THÔNG BÁO
 *                      LÚC NÀY APP CHƯA BỊ KILLED BỞI NGƯỜI DÙNG
 *      TRƯỜNG HỢP : fragmentManager == null
 *          TRONG FILE ShowSheetNotifyWorker.java DÒNG 45 MỖI KHI ĐÚNG GIỜ THÌ SẼ CHỈ
 *                     UPDATE TRẠNG THÁI ĐÁNH DẤU Ở DATABASE BỞI VÌ LÚC NÀY APP ĐÃ BỊ KILLED BỞI NGƯỜI DÙNG
 *
 *5.CHỨC NĂNG XÓA CÁC GHI CHÚ HOẶC CÁC NHIỆM VỤ
 *      TRƯỜNG HỢP XÓA CÁC GHI CHÚ: NẰM Ở DÒNG 309, CTRL+ NHẤN VÀO HÀM deleteItems() ĐỂ XEM RÕ HƠN (ĐÃ GIẢI THÍCH)
 *      TRƯỜNG HỢP XÓA CÁC NHIỆM VỤ: NẰM Ở DÒNG 309, CTRL+ NHẤN VÀO HÀM deleteItems() ĐỂ XEM RÕ HƠN (ĐÃ GIẢI THÍCH)
 *6.CHỨC NĂNG TÌM KIẾM CÁC GHI CHÚ, NHIỆM VỤ
 *  Ở MỖI ADAPTER THÌ SẼ CÓ MỘT HÀM FILTER ĐỂ LỌC DỮ LIỆU, YÊU CẦU ADAPTER ĐÓ PHẢI IMPLEMENT TỪ FILTERABLE
 *      TRONG FILE QuotesItemAdapter.java SẼ LÀ TÌM KIẾM CÁC GHI CHÚ DÒNG 117
 *      TRONG FILE NotesItemAdapter.java SẼ LÀ TÌM KIẾM CÁC NHIỆM VỤ DÒNG 101
 *7.CHỨC NĂNG KILL APP VẪN HIỂN THỊ THÔNG BÁO
 *      Ở ĐÂY KHI APP BỊ KILLED THÌ TA DÙNG WorkManager ĐỂ SẮP XẾP CHO 1 JOB CHẠY BACKGROUND ĐỂ HIỆN THỊ THÔNG BÁO
 *      TA CÓ HAI JOB ĐỂ CHẠY HIỂN THỊ THÔNG BÁO ĐÓ LÀ
 *          ShowSheetNotifyWorker.java ĐƯỢC DÙNG KHI fragmentManager là NULL (VÀO CLASS ShowSheetNotifyWorker.java XEM RÕ HƠN )
 *          ShowSheetNotifyWorkerBottom.java ĐƯỢC DÙNG KHI fragmentManager KHÁC NULL (VÀO CLASS ShowSheetNotifyWorkerBottom.java XEM RÕ HƠN )
 *8.CHỨC NĂNG SỬA GHI CHÚ HOẶC NHIỆM VỤ
 *      MỖI KHI ITEMS GHI CHÚ HOẶC NHIỆM VỤ ĐƯỢC CLICK VÀO THÌ SẼ HIỂN THỊ RA BOTTOM SHEET ĐỂ CHỈNH SỬA
 *      - CHỈNH SỬA GHI CHÚ : NẰM Ở DÒNG 194 TRONG FILE QuotesItemAdapter.java  (VÀO ĐÓ ĐỌC CHI TIẾT HƠN)
 *      - CHỈNH SỬA NHIỆM VỤ : NẰM Ở DÒNG 179 TRONG FILE NotesItemAdapter.java (VÀO ĐÓ ĐỌC CHI TIẾT HƠN)
 *9.CHỨC NĂNG HIỂN THỊ TRÊN MÀN HÌNH KHÓA
 *      - ĐỂ HIỆN THỊ LAYOUT DƯỚI DẠNG THÔNG BÁO TRÊN MÀN HÌNH KHÓA THÌ LAYOUT HIỂN THỊ ĐÓ PHẢI LÀ 1 ACTIVITY
 *      - CỤ THỂ ĐỂ HIỂN THỊ Ở ĐÂY LÀ : ShowSheetBottomDialogNotifyNotes.java
 *      - CHỈNH TRONG CÀI ĐẶT CHO PHÉP APP HIỂN THỊ THÔNG BÁO TRÊN MÀN HÌNH KHÓA
 *      - LAYOUT HIỂN THỊ TRÊN MÀN HÌNH KHÓA SẼ ĐƯỢC ĐÍNH KÈM VỚI THÔNG BÁO
 *          TRONG FILE : ShowSheetNotifyWorker.java DÒNG 62, 68, 100
**/


public class NotesActivityMain extends AppCompatActivity {

    //CÁC THUỘC TÍNH
    //TAB LAYOUT
    public static TabLayout tabLayoutNote;
    //FLOATING BUTTON
    public static FloatingActionButton floatingButtonAddNotes;
    //VIEWPAGER 2
    public static ViewPager2 viewPager2;
    //DANH SÁCH CÁC FRAGMENT : FRAGMENT CHỨA GHI CHÚ VÀ FRAGMENT CHỨA DANH SÁCH CÁC NHIỆM VỤ
    private List<Fragment> fragmentList;
    //ARRAY LIST CHỨA DANH SÁCH CÁC NHIỆM VỤ (CÓ THÔNG BÁO) - FRAGMENT 2 - AddNotesFragment
    public static List<NoteItem> listNotes;
    //ARRAY LIST CHỨA DANH SÁCH CÁC GHI CHÚ (KHÔNG THÔNG BÁO) - FRAGMENT 1 - AddQuotesFragment
    public static List<QuoteItem> listQuotes;
    //ADAPTER FRAGMENT
    private FragmentNotesAdapter fragmentNotesAdapter;
    //DATABASE (SQLITE)
    DatabaseSaveNoteItems databaseSaveNoteItems;

    //LAYOUT DELETE GHI CHÚ, NHIỆM VỤ
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout layoutTopDeleteNote, layoutBottomButtonDelete;
    @SuppressLint("StaticFieldLeak")
    //NÚT CLOSE SHEET DỪNG XÓA VÀ BUTTON CHỌN TẤT CẢ GHI CHÚ HOẶC NHIỆM VỤ
    public static ImageView buttonCloseSheetDelete, buttonSelectAllItems;
    @SuppressLint("StaticFieldLeak")
    //TEXT VIEW
    public static TextView textViewNumberItemsChecked,buttonDeleteNotes;

    //FRAGMENT NHIỆM VỤ
    AddNotesFragment addNotesFragment;
    //FRAGMENT GHI CHÚ
    AddQuoteFragment addQuoteFragment;
    //FRAGMENT MANAGER
    public static FragmentManager fragmentManager;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.notes_activity_main);
            //ÁNH XẠ VIEW
            fragmentManager = getSupportFragmentManager();
            tabLayoutNote = findViewById(R.id.tabLayoutNotes);
            floatingButtonAddNotes = findViewById(R.id.floatingButtonAddNote);
            viewPager2 = findViewById(R.id.viewPagerNotes);

            layoutTopDeleteNote = findViewById(R.id.layoutTopDeleteNote);
            layoutBottomButtonDelete = findViewById(R.id.layoutBottomButtonDelete);
            buttonCloseSheetDelete = findViewById(R.id.buttonCloseSheetDelete);
            buttonSelectAllItems = findViewById(R.id.buttonSelectAllItems);
            textViewNumberItemsChecked = findViewById(R.id.textViewNumberItemsChecked);
            buttonDeleteNotes = findViewById(R.id.buttonDeleteNotes);

            //SET CÁC THUỘC TÍNH TAB LAYOUT
            tabLayoutNote.setTabRippleColor(null);
            tabLayoutNote.setTabGravity(TabLayout.GRAVITY_CENTER);
            tabLayoutNote.setTabMode(TabLayout.MODE_FIXED);
            tabLayoutNote.setSelectedTabIndicatorColor(android.R.color.transparent);

            //KHỞI TẠO VÀ LOAD DATABASE TỪ SQLITE NẾU CÓ DATA
            InitListNoteItemsAndQuotesItems();
            //KHỞI TẠO CÁC FRAGMENT (FRAGMENT THÊM GHI CHÚ VÀ THÊM NHIỆM VỤ)
            InitListsFragment();

            //TẠO ADAPTER FRAGMENT
            fragmentNotesAdapter = new FragmentNotesAdapter(this, fragmentList);
            //SET ADAPTER FRAGMENT CHO VIEW PAGER2
            viewPager2.setAdapter(fragmentNotesAdapter);

            //TAB MEDICATOR - ÁNH XẠ TỪNG TAB VỚI FRAGMENT PHÙ HỢP
            //TAB 1 - FRAGMENT THÊM GHI CHÚ (FRAGMENT ADD QUOTES)
            //TAB 2 - FRAGMENT THÊM NHIỆM VỤ (FRAGMENT ADD NOTES)
            new TabLayoutMediator(tabLayoutNote, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    //SWITCH CASE TAB
                    switch (position){
                        //TAB 1 - FRAGMENT ADD QUOTES
                        case 0:
                            tab.setIcon(R.drawable.ic_quote);
                            break;
                        case 1:
                        //TAB 2 - FRAGMENT ADD NOTES
                            tab.setIcon(R.drawable.ic_note_add);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + position);
                    }
                }
            }).attach();

            //
            tabLayoutNote.getTabAt(0).getIcon().setTint(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_orange_dark));

            //TAB LAYOUT LISTENER
            tabLayoutNote.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //THAY ĐỔI MÀU SẮC CỦA TAB ĐƯỢC CHỌN
                    Objects.requireNonNull(tab.getIcon()).setTint(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_orange_dark));
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    //THAY ĐỔI MÀU SẮC CỦA TAB ĐƯỢC KHÔNG ĐƯỢC CHỌN
                    Objects.requireNonNull(tab.getIcon()).setTint(ContextCompat.getColor(getApplicationContext(),R.color.brown));
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            //BẮT SỰ KIỆN CỦA FLOATING BUTTON THÊM GHI CHÚ HOẶC NHIỆM VỤ
            //NÓ SẼ DỰA VÀO ĐANG Ở TAB HIỆN TẠI NÀO ĐỂ THÊM GHI CHÚ HOẶC NHIỆM VỤ
            //NẾU ĐANG Ở TAB 1 - FRAGMENT ADD QUOTES THÌ SẼ THÊM GHI CHÚ
            //NẾU ĐANG Ở TAB 2 - FRAGMENT ADD NOTES THÌ SẼ THÊM NHIỆM VỤ
            floatingButtonAddNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TAB THÊM GHI CHÚ
                    if(viewPager2.getCurrentItem() == 0){
                        //MỞ BOTTOM SHEET THÊM GHI CHÚ
                        addQuoteFragment.OpenBottomSheetAddQuotes();
                    }
                    //TAB THÊM NHIỆM VỤ
                    else{
                        //MỞ BOTTOM SHEET THÊM NHIỆM VỤ
                        addNotesFragment.OpenBottomSheetDialogAddNotes();
                    }
                }
            });


            //BẮT SỰ KIỆN BUTTON ĐÓNG SHEET XÓA
            //KHÔNG XÓA ITEM NỮA
            buttonCloseSheetDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //DISABLE CÁC EVENT SWIPE (LƯỚT SANG TRÁI PHẢI ......) CỦA NGƯỜI DÙNG
                    viewPager2.setUserInputEnabled(true);
                    //LẤY VỊ TRÍ HIỆN TẠI CỦA TAB HIỆN TẠI - 0 NẾU ĐANG Ở TAB THÊM GHI CHÚ - 1 NẾU ĐANG Ở TAB THÊM NHIỆM VỤ
                    int pos = viewPager2.getCurrentItem();
                    if(pos == 0){
                        //TẮT TRẠNG THÁI XÓA
                        modeDelete.set(false);
                        //TRẢ CÁC TRẠNG THÁI CỦA CÁC ITEM BỊ CHECK LÀ XÓA VỀ TRẠNG THÁI FALSE (KHÔNG XÓA NỮA)
                        addQuoteFragment.resetStateAllQuoteItems(false);
                    }
                    else{
                        //TẮT TRẠNG THÁI XÓA
                        isHoveredDelete.set(false);
                        //TRẢ CÁC TRẠNG THÁI CỦA CÁC ITEM BỊ CHECK LÀ XÓA VỀ TRẠNG THÁI FALSE (KHÔNG XÓA NỮA)
                        resetStateDeleteAllItems(false,pos);
                        isShowed.set(false);
                        resetStateExpandableOriginal();
                    }
                    //ẨN HIỆN CÁC LAYOUT
                    showTabLayout(View.VISIBLE);
                    showTopLayoutDelete(View.GONE);
                    showBottomLayoutDelete(View.GONE);
                    showButtonAddNotes(View.VISIBLE);
                }
            });


            //BẮT SỰ KIỆN CHỌN BUTTON CHỌN TẤT CẢ THÔNG BÁO
            buttonSelectAllItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int posViewPager = viewPager2.getCurrentItem();

                    //LẤY VỊ TRÍ HIỆN TẠI CỦA VIEWPAGER2
                    int posCurrentPager = viewPager2.getCurrentItem();
                    //KIỂM TRA TẤT CẢ CÁC ITEM ĐƯỢC CHỌN HAY KHÔNG
                    //NẾU TẤT CẢ ĐƯỢC CHỌN THÌ LẦN NHẤN VÀO TIẾP THEO SẼ CHUYỂN
                    //TRẠNG THÁI ĐƯỢC CHỌN SANG KHÔNG ĐƯỢC CHỌN
                    if(allItemsIsChecked(posCurrentPager)){
                        Log.i("AAA","ALLL FALSEEEEEEEEEEEE");
                        resetStateDeleteAllItems(false,posCurrentPager);
                        textViewNumberItemsChecked.post(new Runnable() {
                            @Override
                            public void run() {
                                textViewNumberItemsChecked.setText("Chọn mục");
                            }
                        });
                        disableClickedForButtonDelete(true);
                    }
                    //VÀ NGƯỢC LẠI
                    //NẾU TẤT CẢ CÁC ITEM CHƯA ĐƯỢC CHỌN THÌ CHỌN TẤT CẢ ITEMS
                    else{
                        Log.i("AAA","TRUEEEEEEEEEEEEEEEEE");
                        resetStateDeleteAllItems(true,posCurrentPager);
                        updateTextCountNumberItemsChecked(posCurrentPager);
                    }
                }
            });

            //BẮT SỰ KIỆN BUTTON XÓA CÁC ITEM ĐƯỢC CHỌN
            buttonDeleteNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItems();
                    resetStateExpandableOriginal();
                    isShowed.set(false);
                    showTopLayoutDelete(View.GONE);
                    showBottomLayoutDelete(View.GONE);
                    showTabLayout(View.VISIBLE);
                    showButtonAddNotes(View.VISIBLE);
                    isHoveredDelete.set(false);
                    viewPager2.setUserInputEnabled(true);
                }
            });

    }

    //KHỞI TẠO CÁC FRAGMENT
    public void InitListsFragment(){
        fragmentList = new ArrayList<>();
        addNotesFragment = new AddNotesFragment(listNotes,getApplicationContext());
        addQuoteFragment = new AddQuoteFragment(listQuotes);
        fragmentList.add(addQuoteFragment);
        fragmentList.add(addNotesFragment);
    }

    //LẤY DANH SÁCH CÁC NHIỆM VỤ TỪ DATABASE
    public void InitListNoteItemsFromDatabase(){
        listNotes = databaseSaveNoteItems.getListNoteItemsReturn();
    }

    //LẤY DANH SÁCH CÁC GHI CHÚ TỪ DATABASE
    public void InitListQuoteItemsFromDatabase(){
        listQuotes = DatabaseObject.getInstance(this).roomDatabaseDao().getQuoteItems();
    }

    //RETRIEVE DATA FROM DATABASE
    public void InitListNoteItemsAndQuotesItems(){
        listNotes = new ArrayList<>();
        listQuotes = new ArrayList<>();
        databaseSaveNoteItems = new DatabaseSaveNoteItems(getApplicationContext());
        InitListNoteItemsFromDatabase();
        InitListQuoteItemsFromDatabase();
    }

    //LAYOUT TOP DELETE (HIỂN THỊ MỖI KHI ITEM ĐƯỢC LONG CLICK)
    public static void showTopLayoutDelete(int visibility){
        layoutTopDeleteNote.post(new Runnable() {
            @Override
            public void run() {
                layoutTopDeleteNote.setVisibility(visibility);
            }
        });
    }

    //LAYOUT BOTOTM DELETE (HIỂN THỊ MỖI KHI ITEM ĐƯỢC LONG CLICK)
    public static void showBottomLayoutDelete(int visibility){
        layoutBottomButtonDelete.post(new Runnable() {
            @Override
            public void run() {
                layoutBottomButtonDelete.setVisibility(visibility);
            }
        });
    }

    //HIỆN THỊ HOẶC ẨN TAB LAYOUT
    public static void showTabLayout(int visibility){
        tabLayoutNote.post(new Runnable() {
            @Override
            public void run() {
                tabLayoutNote.setVisibility(visibility);
            }
        });
    }

    //HIỂN THỊ HOẶC ẨN BUTTON THÊM
    public static void showButtonAddNotes(int visibility){
        floatingButtonAddNotes.post(new Runnable() {
            @Override
            public void run() {
                floatingButtonAddNotes.setVisibility(visibility);
            }
        });
    }

    //DISABLE CLICK CỦA BUTTON XÓA
    public static void disableClickedForButtonDelete(boolean disableState){
        buttonDeleteNotes.post(new Runnable() {
            @SuppressLint("UseCompatTextViewDrawableApis")
            @Override
            public void run() {
                buttonDeleteNotes.setClickable(!disableState);
                buttonDeleteNotes.setTextColor(disableState ? Color.argb(100,152,152,152) : Color.BLACK);
                buttonDeleteNotes.setCompoundDrawableTintList(ColorStateList.valueOf(disableState ? Color.argb(100,152,152,152) : Color.BLACK));
            }
        });
    }

    public boolean checkLayoutDeleteIsShow(){
        return layoutTopDeleteNote.getVisibility() == View.VISIBLE;
    }

    //BẮT SỰ KIỆN BACK PRESSED CỦA NGƯỜI DÙNG
    @Override
    public void onBackPressed() {
        viewPager2.setUserInputEnabled(true);
        int posViewPager = viewPager2.getCurrentItem();
        if(posViewPager == 0){
            modeDelete.set(false);
            addQuoteFragment.returnToCurrentQuoteItems();
            addQuoteFragment.resetStateAllQuoteItems(false);
            if(checkLayoutDeleteIsShow()){
                showTabLayout(View.VISIBLE);
                showTopLayoutDelete(View.GONE);
                showBottomLayoutDelete(View.GONE);
                showButtonAddNotes(View.VISIBLE);
            }
        }
        else{
            if(checkLayoutDeleteIsShow()){
                isHoveredDelete.set(false);
                resetStateDeleteAllItems(false,posViewPager);
                showTabLayout(View.VISIBLE);
                showTopLayoutDelete(View.GONE);
                showBottomLayoutDelete(View.GONE);
                showButtonAddNotes(View.VISIBLE);
                isShowed.set(false);

                //////////RESET STATE OF NOTE ITEMS BACK TO ORIGINALS
                resetStateExpandableOriginal();
            }
            else{
                super.onBackPressed();
            }
        }
    }

    //THAY ĐỔI TRẠNG THÁI CỦA CÁC ITEM ĐƯỢC CHECKED SANG CHECKED HOẶC KHÔNG CHECKED
    public void resetStateDeleteAllItems(boolean state, int pos){

        if(pos == 0){
            for (QuoteItem item : listQuotes){
                //item.setStateCheckedToDelete(state);
                item.setStateChecked(state);
            }
        }
        else{
            for (int i = 0; i < listNotes.size(); i++) {
                listNotes.get(i).setHoveredToDelete(state);
            }
        }
    }

    //CHANGE ALL STATE EXPANDABLE OF ITEMS TO FALSE
    public static void resetStateExpandableAllItems(){
        for (NoteItem item : listNotes){
            item.setTempExpandable(item.getExpandable());
            item.setExpandable(false);
        }
    }

    public void resetStateExpandableOriginal(){
        for (NoteItem item : listNotes){
            item.setExpandable(item.isTempExpandable());
        }
    }

    //KIỂM TRA TOÀN BỘ ITEM ĐƯỢC CHECKED HAY KHÔNG
    public boolean allItemsIsChecked(int pos){

        if(pos == 0){
            for (QuoteItem item : listQuotes){
                if(!item.isStateChecked()){
                    return false;
                }
            }
            return true;
        }

        for (NoteItem item :listNotes){
            if(!item.isHoveredToDelete()){
                return false;
            }
        }
        return true;
    }


    //DELETE CÁC ITEMS
    @SuppressLint("NotifyDataSetChanged")
    public void deleteItems(){
        List<NoteItem> listItemsDelete = new ArrayList<>();
        List<QuoteItem> listQuoteItemsDelete = new ArrayList<>();
        int posCurrentPager = viewPager2.getCurrentItem();
        //KIỂM TRA CÓ PHẢI TẤT CẢ CÁC ITEM ĐƯỢC CHỌN KHÔNG
        //TÙY VÀO ĐANG Ở FRAGMENT NÀO
        //NẾU TẤT CẢ ĐƯỢC CHECKED THÌ THỰC HIỆN LỆNH TRONG IF
        //NGƯỢC LẠI THỰC HIỆN LỆNH TRONG ELSE
        if(allItemsIsChecked(posCurrentPager)){
            //NẾU ĐANG Ở FRAGMENT ADD QUOTES THÌ SẼ XÓA GHI CHÚ
            if(posCurrentPager == 0){
                modeDelete.set(false);
                listQuotes.clear();
                //CLEAR CÁC VIEWS
                Objects.requireNonNull(AddQuoteFragment.quotesItemAdapterObservableField.get()).clearQuoteItems();
                //XÓA CÁC ITEM ĐƯỢC CHECK Ở DATABASE
                DatabaseObject.getInstance(this).roomDatabaseDao().deleteAllQuoteItems();
                AddQuoteFragment.binding.icEmptyNotes.post(new Runnable() {
                    @Override
                    public void run() {
                        AddQuoteFragment.binding.icEmptyNotes.setVisibility(View.VISIBLE);
                    }
                });
                AddQuoteFragment.binding.textViewNull.post(new Runnable() {
                    @Override
                    public void run() {
                        AddQuoteFragment.binding.textViewNull.setVisibility(View.VISIBLE);
                    }
                });
            }
            //NẾU ĐANG Ở FRAGMENT ADD NOTES THÌ SẼ XÓA GHI CHÚ
            else{
                listNotes.clear();
                //XÓA CÁC ITEM ĐƯỢC CHECK Ở DATABASE
                databaseSaveNoteItems.deleteAllItems();
                Objects.requireNonNull(AddNotesFragment.listsObservable.get()).notifyDataSetChanged();
                AddNotesFragment.fragmentAddNotesBinding.icEmptyNotes.post(new Runnable() {
                    @Override
                    public void run() {
                        AddNotesFragment.fragmentAddNotesBinding.icEmptyNotes.setVisibility(View.VISIBLE);
                    }
                });
                AddNotesFragment.fragmentAddNotesBinding.textViewNull.post(new Runnable() {
                    @Override
                    public void run() {
                        AddNotesFragment.fragmentAddNotesBinding.textViewNull.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
        //NẾU KHÔNG PHẢI CÁC ITEMS ĐỀU ĐƯỢC CHECKED
        else{
            //NẾU ĐANG Ở FRAGMENT ADD QUOTES THÌ SẼ XÓA GHI CHÚ
            if(posCurrentPager == 0){
                int size = listQuotes.size();
                //DÙNG VÒNG LẶP KIỂM TRA CÁC ITEM
                for (int i = 0; i < listQuotes.size(); ++i){
                    QuoteItem item = listQuotes.get(i);
                    //NẾU ITEM NÀO BỊ CHECKED
                    if(item.isStateChecked()){
                        listQuoteItemsDelete.add(item);
                        //THÌ SẼ BỊ XÓA KHỎI DATABASE
                        DatabaseObject.getInstance(this).roomDatabaseDao().deleteQuoteItem(item);
                    }
                }
                listQuotes.removeAll(listQuoteItemsDelete);
                Objects.requireNonNull(AddQuoteFragment.quotesItemAdapterObservableField.get()).removeQuoteItems(listQuoteItemsDelete);
                modeDelete.set(false);
                for (QuoteItem item : listQuotes){
                    item.setStateCheckedToDelete(false);
                    item.setStateChecked(false);
                }
            }
            //NẾU ĐANG Ở FRAGMENT ADD QUOTES THÌ SẼ XÓA NHIỆM VỤ
            else{
                int size = listNotes.size();
                for (int i = 0; i < listNotes.size(); ++i){
                    //DÙNG VÒNG LẶP KIỂM TRA ITEMS NÀO ĐƯỢC CHECKED
                    NoteItem item = listNotes.get(i);
                    if(item.isHoveredToDelete()){
                        listItemsDelete.add(item);
                        //XÓA ITEM Ở DATABASE NẾU NÓ ĐƯỢC CHECKED
                        databaseSaveNoteItems.deleteNoteItem(item.getId());
                    }
                }
                listNotes.removeAll(listItemsDelete);
                Objects.requireNonNull(AddNotesFragment.listsObservable.get()).notifyItemRangeRemoved(0,size);
            }
        }
    }

    //ĐẾM SỐ LƯỢNG CÁC ITEM ĐƯỢC CHECKED
    public static int countItemsChecked(int pos){
        int i = 0;
        if(pos == 0){
            for (QuoteItem item : listQuotes){
                if(item.isStateChecked()){
                    i++;
                }
            }
        }
        else{
            for (NoteItem item : listNotes){
                if(item.isHoveredToDelete()){
                    i++;
                }
            }
        }
        return  i;
    }

    //UPDATE TEXT HIỂN THỊ SỐ CÁC ITEMS ĐƯỢC CHECKED
    public static void updateTextCountNumberItemsChecked(int pos){
        textViewNumberItemsChecked.post(new Runnable() {
            @Override
            public void run() {
                int count = countItemsChecked(pos);
                if(count != 0){
                    disableClickedForButtonDelete(false);
                }
                else {
                    disableClickedForButtonDelete(true);
                }
                textViewNumberItemsChecked.setText(count > 0 ? "Đã chọn "+String.valueOf(countItemsChecked(pos))+" mục" : "Chọn mục");
            }
        });
    }



    //LƯU TRẠNG THÁI CHỈNH SỬA CỦA TỪNG ITEM VÀO DATABASE
    public void saveTheLastStateOfListNoteItemsToDatabase(){
        if(fragmentList.size() != 0){
            for (NoteItem item : listNotes){
                if(isShowed.get()){
                    item.setExpandable(item.isTempExpandable());
                }
                databaseSaveNoteItems.updateNoteItemEnd(item);
            }
        }
    }

    //SAVE THE LAST STATE OF NOTE ITEMS TO DATABASE WHEN FRAGMENT PAUSED
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("AAA","ON PAUSEEEEEEEEEEEEEEEEEEE");
        saveTheLastStateOfListNoteItemsToDatabase();
    }
}