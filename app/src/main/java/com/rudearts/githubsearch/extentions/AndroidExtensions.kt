package com.rudearts.githubsearch.extentions

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

inline fun FragmentManager.inTransaction(block: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().block().commitNowAllowingStateLoss()
}