package com.paypay.challenge.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq

@RunWith(AndroidJUnit4::class)
class DefaultStorageTest {
    private val context = mock<Context>()

    private val sharedPreferences = mock<SharedPreferences>()

    @Before
    fun setup() {
        whenever(context.getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE))).thenReturn(sharedPreferences)
    }

    @Test
    fun testGetWithDefaultValue() {
        val boolValue = true
        val intValue = 200
        val floatValue = 300F
        val longValue = 100L
        val stringValue = "test string"

        whenever(sharedPreferences.getBoolean("bool_key1", boolValue)).thenReturn(boolValue)
        whenever(sharedPreferences.getInt("int_key1",intValue)).thenReturn(intValue)
        whenever(sharedPreferences.getFloat("float_key1",floatValue)).thenReturn(floatValue)
        whenever(sharedPreferences.getLong("long_key1",longValue)).thenReturn(longValue)
        whenever(sharedPreferences.getString("string_key1", stringValue)).thenReturn(stringValue)

        val defaultStorage = DefaultStorage(context)
        assertThat(defaultStorage.get("bool_key1", boolValue)).isTrue()
        assertThat(defaultStorage.get("int_key1", intValue)).isEqualTo(intValue)
        assertThat(defaultStorage.get("float_key1", floatValue)).isEqualTo(floatValue)
        assertThat(defaultStorage.get("long_key1", longValue)).isEqualTo(longValue)
        assertThat(defaultStorage.get("string_key1", stringValue)).isEqualTo(stringValue)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testGet_withUnsupportedType() {
        val defaultStorage = DefaultStorage(context)
        defaultStorage.get("key1,", listOf<String>())
    }

    @Test
    fun testGet_withNoDefaultValue() {
        val entries = mapOf("key1" to "test", "key2" to 89F)
        whenever(sharedPreferences.contains("key1")).thenReturn(true)
        whenever(sharedPreferences.all).thenReturn(entries)
        val defaultStorage = DefaultStorage(context)
        assertThat(defaultStorage.get<String>("key1")).isEqualTo("test")
        assertThat(defaultStorage.get<String>("key2")).isNull()
        assertThat(defaultStorage.get<String>("key3")).isNull()
    }

    @Test
    fun testGetAll() {
        val entries = mapOf("key1" to "test", "key2" to 89F)
        whenever(sharedPreferences.all).thenReturn(entries)
        val defaultStorage = DefaultStorage(context)
        assertThat(defaultStorage.getAll()).isEqualTo(entries)
    }

    @Test
    fun testSet() {
        val editor = mock<SharedPreferences.Editor>()
        whenever(sharedPreferences.edit()).thenReturn(editor)
        val defaultStorage = DefaultStorage(context)
        defaultStorage.set("key1", true)
        defaultStorage.set("key2", 200)
        defaultStorage.set("key3", 300F)
        defaultStorage.set("key4", 400L)
        defaultStorage.set("key5", "test")
        defaultStorage.set("key6", setOf("test", "test2"))

        verify(editor).putBoolean("key1", true)
        verify(editor).putInt("key2", 200)
        verify(editor).putFloat("key3", 300F)
        verify(editor).putLong("key4", 400L)
        verify(editor).putString("key5", "test")
        verify(editor).putStringSet("key6", setOf("test", "test2"))
        verify(editor, times(6)).commit()
    }

    @Test
    fun testRemove() {
        val editor = mock<SharedPreferences.Editor>()
        whenever(sharedPreferences.edit()).thenReturn(editor)
        val defaultStorage = DefaultStorage(context)
        defaultStorage.remove("key")
        verify(editor).remove("key")
        verify(editor).commit()
    }
}