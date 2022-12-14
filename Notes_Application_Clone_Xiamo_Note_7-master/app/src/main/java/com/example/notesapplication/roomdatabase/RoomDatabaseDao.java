package com.example.notesapplication.roomdatabase;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notesapplication.model.QuoteItem;
import com.example.notesapplication.utils.Utils;

import java.util.List;

@Dao
public interface RoomDatabaseDao {

    //THÊM GHI CHÚ VÀO ROOM
    //INSERT QUOTE ITEM TO DATABASE
    @Insert
    void insertQuotesItem(QuoteItem item);

    //GET DANH SÁCH CÁC GHI CHÚ
    //GET LIST QUOTE ITEMS FROM DATABASE
    @Query("SELECT * FROM "+ Utils.TABLE_NAME)
    List<QuoteItem> getQuoteItems();

    //XÓA 1 GHI CHÚ
    //DELETE QUOTE ITEMS FROM DATABASE
    @Delete
    void deleteQuoteItem(QuoteItem item);

    //XÓA TẤT CẢ CÁC GHI CHÚ
    //DELETE ALL QUOTE ITEMS
    @Query("DELETE FROM "+Utils.TABLE_NAME)
    void deleteAllQuoteItems();

    //UPDATE THÔNG TIN CỦA GHI CHÚ
    //UPDATE QUOTE ITEM IN DATABASE
    @Update
    void updateQuoteItem(QuoteItem item);

    //UPDATE THÔNG TIN GHI CHÚ DỰA VÀO CÁC FIELD (TRƯỜNG DỮ LIỆU)
    //UPDATE QUOTE ITEM BY FIELD
    @Query("UPDATE "+Utils.TABLE_NAME+" SET QUOTES= :newText, TIME =:time WHERE QUOTES=:oldText")
    void updateQuoteItemByField(String oldText, String newText,String time);
}
