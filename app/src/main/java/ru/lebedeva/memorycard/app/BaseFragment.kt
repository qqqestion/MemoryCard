package ru.lebedeva.memorycard.app

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

open class BaseFragment : Fragment() {

    fun showLoadingBar() = loadingBarVisibility(true)

    fun hideLoadingBar() = loadingBarVisibility(false)

    private fun loadingBarVisibility(isVisible: Boolean) = with(activity) {
        (this as? MainActivity)?.binding?.mainLoadingProgressBar?.isVisible = isVisible
    }

    fun snackbar(msg: String) = Snackbar.make(
        requireView(),
        msg,
        Snackbar.LENGTH_LONG
    ).show()

//    fun showErrorDialog(errorMessage: String) =
//        MaterialAlertDialogBuilder(requireContext(), R.style.TestDialogTheme)
//            .setTitle("Ошибка")
//            .setMessage(errorMessage)
//            .setPositiveButton("OK", null)
//            .create()
//            .show()
}