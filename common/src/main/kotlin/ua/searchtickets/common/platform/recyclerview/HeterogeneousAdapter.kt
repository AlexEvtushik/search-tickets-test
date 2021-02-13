package ua.searchtickets.common.platform.recyclerview

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import io.reactivex.functions.Consumer

class HeterogeneousAdapter(
    adapterDelegatesManager: AdapterDelegatesManager<List<Item>> = AdapterDelegatesManager()
) :
    ListDelegationAdapter<List<HeterogeneousAdapter.Item>>(adapterDelegatesManager),
    Consumer<List<HeterogeneousAdapter.Item>> {

    fun addDelegate(delegate: AdapterDelegate<List<Item>>) {
        delegatesManager.addDelegate(delegate)
    }

    override fun accept(newItems: List<Item>) {
        items = newItems
    }

    fun getItem(position: Int): Item? = items.getOrNull(position)

    interface Item
}

