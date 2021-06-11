package com.example.team3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class AlarmDBHelper(val context: Context?, val DB_NAME: String) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_VERSION = 1
        val TABLE_NAME = "alarm"
        val AID = "aid"
        val ACONTENT = "acontent"
        val AYEAR = "ayear"
        val AMONTH = "amonth"
        val ADAY = "aday"
        val AHOUR = "ahour"
        val AMINUTE = "aminute"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME(" +
                "$AID integer primary key, " + "$ACONTENT text, " +
                "$AYEAR integer, " + "$AMONTH integer, " + "$ADAY integer, " + "$AHOUR integer, " + "$AMINUTE integer);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun insertAlarm(alarm: MyAlarmData): Boolean{
        val values = ContentValues()
        values.put(AID, alarm.id)
        values.put(ACONTENT, alarm.content)
        values.put(AYEAR, alarm.year)
        values.put(AMONTH, alarm.month)
        values.put(ADAY, alarm.day)
        values.put(AHOUR, alarm.hour)
        values.put(AMINUTE, alarm.minute)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }

    fun deleteAlarm(aid: Int): Boolean {
        val strsql = "select * from $TABLE_NAME where $AID='$aid';"
        val db = writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        if (flag){
            cursor.moveToFirst()
            db.delete(TABLE_NAME,"$AID=?", arrayOf(aid.toString()))
        }
        cursor.close()
        db.close()
        return flag
    }

    fun getAllRecord(): List<MyAlarmData>{
        var allAlarms = mutableListOf<MyAlarmData>()
        var cnt = 0
        var strsql = "select * from $TABLE_NAME;"
        var db = readableDatabase
        val cursor = db.rawQuery(strsql,null)
        cursor.moveToFirst()
        val attrcount = cursor.columnCount

        if (cursor.count == 0){
            val nullList: List<MyAlarmData> = listOf(MyAlarmData(-1,"",-1,-1, -1, -1, -1))
            return nullList
        }

        do{
            val elem = arrayOfNulls<Any>(7)
            for (i in 0 until attrcount){
                elem[i] = cursor.getString(i)
            }
            allAlarms.add(cnt, MyAlarmData(elem[0].toString().toInt(),
                elem[1].toString(), elem[2].toString().toInt(), elem[3].toString().toInt(),
                elem[4].toString().toInt(), elem[5].toString().toInt(), elem[6].toString().toInt()))
            cnt++
        }while(cursor.moveToNext())

        cursor.close()
        db.close()

        return allAlarms
    }

    fun getRowCount(): Int{
        var strsql = "select * from $TABLE_NAME;"
        var db = readableDatabase
        val cursor = db.rawQuery(strsql,null)
        cursor.moveToFirst()
        return cursor.count
    }

    fun getID(year: Int, month: Int, day: Int): Int{
        val strsql = "select * from $TABLE_NAME where $AYEAR='$year' and $AMONTH='$month' and $ADAY='$day';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()
        var id = -1
        if (cursor.count!=0){
            id = cursor.getString(0).toInt()
        }
        Log.d("확인", "id: ${id}")
        cursor.close()
        db.close()
        return id
    }

    fun getAlarmData(year:Int, month:Int, day:Int) : MyAlarmData{
        val strsql = "select * from $TABLE_NAME where $AYEAR='$year' and $AMONTH='$month' and $ADAY='$day';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()
        val attrcount = cursor.columnCount
        val elem = arrayOfNulls<Any>(7)
        for (i in 0 until attrcount) {
            elem[i] = cursor.getString(i)
        }
        val alarmData = MyAlarmData(elem[0].toString().toInt(), elem[1].toString(), elem[2].toString().toInt(), elem[3].toString().toInt(),
            elem[4].toString().toInt(), elem[5].toString().toInt(), elem[6].toString().toInt())
        //Log.d("확인", "data: ${data}")
        cursor.close()
        db.close()
        return alarmData
    }
}