package ua.searchtickets.app.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.androidx.AppNavigator
import ua.searchtickets.R

class RootNavigator(
    activity: FragmentActivity,
    containerId: Int
) : AppNavigator(activity, containerId) {
    override fun setupFragmentTransaction(
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment?
    ) {
        super.setupFragmentTransaction(fragmentTransaction, currentFragment, nextFragment)
        fragmentTransaction.setCustomAnimations(
            R.anim.fragment_fade_enter,
            R.anim.fragment_close_exit,
            R.anim.fragment_open_enter,
            R.anim.fragment_fade_exit
        )
    }
}