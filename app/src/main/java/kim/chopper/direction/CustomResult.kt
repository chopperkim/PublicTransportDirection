package kim.chopper.direction

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kim.chopper.direction.databinding.CustomResultBinding

class CustomResult @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: CustomResultBinding

    init {
        binding = CustomResultBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)
    }

    fun setRouteName(routeName: String?) {
        binding.routeTitle.text = routeName
    }

    fun setPrice(price: String?) {
        binding.priceText.text = price
    }

    fun setTime(time: String?) {
        binding.timeText.text = time
    }
}