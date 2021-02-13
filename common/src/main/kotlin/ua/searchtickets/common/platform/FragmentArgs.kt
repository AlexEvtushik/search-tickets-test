package ua.searchtickets.common.platform

import android.os.Bundle
import android.os.put
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T: Any> argumentNotNull(default: T? = null): ReadWriteProperty<Fragment, T> =
    object : ReadWriteProperty<Fragment, T> {
      @Suppress("UNCHECKED_CAST")
      override fun getValue(thisRef: Fragment, property: KProperty<*>): T = requireNotNull(
          thisRef.arguments
              ?.get(property.name) as? T
              ?: default
      )

      override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val args = thisRef.arguments ?: Bundle().also(thisRef::setArguments)
        val key = property.name
        args.put(key, value)
      }
    }

fun <T> argument(default: T? = null): ReadWriteProperty<Fragment, T?> =
    object : ReadWriteProperty<Fragment, T?> {
      @Suppress("UNCHECKED_CAST")
      override fun getValue(thisRef: Fragment, property: KProperty<*>): T? =
          thisRef.arguments?.get(property.name) as? T ?: default

      override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T?) {
        val args = thisRef.arguments ?: Bundle().also(thisRef::setArguments)
        val key = property.name
        value?.let { args.put(key, it) } ?: args.remove(key)
      }
    }
