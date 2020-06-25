package layout

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.*
import io.wookey.wallet.R
import io.wookey.wallet.support.extensions.screenWidth
import io.wookey.wallet.support.extensions.sharedPreferences
import kotlinx.android.synthetic.main.dialog_picker.*
import java.util.*

class PickerDialog : DialogFragment() {

    private var cancelListener: (() -> Unit)? = null
    private var confirmListener: ((String) -> Unit)? = null

    private lateinit var data: List<String>

    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_picker, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val window = dialog?.window
        window?.let {
            val attributes = it.attributes.apply {
                gravity = Gravity.BOTTOM
                width = screenWidth()
            }
            it.attributes = attributes
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.decorView.setPadding(0, 0, 0, 0)
            it.setLayout(attributes.width, WindowManager.LayoutParams.WRAP_CONTENT)
            it.setWindowAnimations(R.style.BottomDialog_Animation)
        }

        val list = mutableListOf<String>()
        data.forEachIndexed { i, s ->
            list.add(s.toUpperCase(Locale.CHINA))
            if (s == sharedPreferences().getString("currentCurrency", "usd")) {
                position = i
            }
        }
        loopView.setItems(list)
        loopView.setInitPosition(position)
        loopView.setListener {
            Log.e("position: ", it.toString())
            position = it
        }

        confirm.setOnClickListener { _ ->
            confirmListener?.invoke(data[position])
            hide()
        }

        cancel.setOnClickListener {
            cancelListener?.invoke()
            hide()
        }
    }

    fun hide() {
        val activity = activity
        if (activity != null && !activity.isFinishing && !activity.isDestroyed) {
            dismiss()
        }
    }

    companion object {
        private const val TAG = "PickerDialog"
        fun newInstance(): PickerDialog {
            val fragment = PickerDialog()
            return fragment
        }

        fun display(
            fm: FragmentManager,
            data: List<String>,
            cancelListener: (() -> Unit)? = null,
            confirmListener: ((String) -> Unit)? = null
        ) {
            val ft = fm.beginTransaction()
            val prev = fm.findFragmentByTag(TAG)
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            newInstance().apply {
                this.data = data
                this.cancelListener = cancelListener
                this.confirmListener = confirmListener
            }.show(ft, TAG)
        }
    }
}