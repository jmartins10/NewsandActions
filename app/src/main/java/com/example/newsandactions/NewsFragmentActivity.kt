package com.example.newsandactions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.newsandactions.databinding.FragmentNewsActivityBinding
import com.example.newsandactions.entities.News
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("StaticFieldLeak")
open class NewsFragmentActivity : Fragment() {

    companion object SaveMe {
        lateinit var context: Context
        lateinit var latestNewsLayout: LinearLayout
        lateinit var sportNewsLayout: LinearLayout
        lateinit var economyNewsLayout: LinearLayout
        lateinit var politicsNewsLayout: LinearLayout
        lateinit var worldNewsLayout: LinearLayout
        lateinit var actionsNewsLayout: LinearLayout

        private fun dpToPx(ctx: Context, dp: Float): Int {
            return (dp * ctx.resources.displayMetrics.density).toInt()
        }

        fun createCardLayout(news: List<News?>?): LinearLayout {
            val linearLayoutReturn = LinearLayout(context)
            linearLayoutReturn.orientation = LinearLayout.VERTICAL
            linearLayoutReturn.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val pad16 = dpToPx(context, 16f)
            linearLayoutReturn.setPadding(0, 0, 0, pad16)
            linearLayoutReturn.visibility = View.VISIBLE

            news?.forEach { new ->
                if (new != null) {
                    val cardView = createCard(new)
                    linearLayoutReturn.addView(cardView)
                }
            }
            return linearLayoutReturn
        }

        fun createCard(new: News): CardView {
            val cardView = CardView(context)
            val linearLayout = LinearLayout(context)
            val title = TextView(context)
            val text = TextView(context)
            val image = ImageView(context)

            val id = View.generateViewId()
            linearLayout.id = id
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val pad12 = dpToPx(context, 12f)
            val pad8 = dpToPx(context, 8f)

            linearLayout.setPadding(pad12, pad12, pad12, pad12)
            linearLayout.visibility = View.VISIBLE

            // Image on top (Banner)
            val imgHeight = dpToPx(context, 180f)
            image.layoutParams = ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                imgHeight
            )
            image.scaleType = ImageView.ScaleType.CENTER_CROP
            image.setImageResource(R.drawable.ic_launcher_background)

            val imageUrl = new.image?.link
            if (!imageUrl.isNullOrEmpty()) {
                Thread {
                    try {
                        val connection = URL(imageUrl).openConnection() as HttpURLConnection
                        connection.connectTimeout = 3000
                        connection.readTimeout = 3000
                        connection.doInput = true
                        connection.connect()
                        val input: InputStream = connection.inputStream
                        val bitmap = BitmapFactory.decodeStream(input)
                        if (bitmap != null) {
                            Handler(Looper.getMainLooper()).post {
                                image.setImageBitmap(bitmap)
                            }
                        }
                    } catch (_: Exception) {
                        // Keep default background
                    }
                }.start()
            }
            linearLayout.addView(image)

            // Title below image
            title.layoutParams = ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            title.setPadding(0, pad8, 0, pad8)
            title.setTextColor(Color.parseColor("#111111"))
            title.textSize = 18f
            title.setTypeface(null, Typeface.BOLD)
            title.text = new.title ?: ""
            linearLayout.addView(title)

            // Text below title
            text.layoutParams = ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text.setTextColor(Color.parseColor("#555555"))
            text.textSize = 14f
            text.maxLines = 3
            text.ellipsize = TextUtils.TruncateAt.END
            text.text = new.description ?: new.content ?: ""
            linearLayout.addView(text)

            cardView.addView(linearLayout)
            cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            cardView.radius = dpToPx(context, 12f).toFloat()
            cardView.cardElevation = dpToPx(context, 6f).toFloat()

            val layoutParams = MarginLayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(pad12, pad8, pad12, pad8)
            cardView.layoutParams = layoutParams
            cardView.visibility = View.VISIBLE

            // Click listener to open detail
            cardView.setOnClickListener {
                val intent = Intent(context, NewsDetailActivity::class.java).apply {
                    putExtra("title", new.title)
                    putExtra("description", new.description)
                    putExtra("content", new.content)
                    putExtra("image_url", new.image?.link)
                    putExtra("category", new.categories?.firstOrNull() ?: "")
                }
                context.startActivity(intent)
            }

            return cardView
        }
    }

    private var _binding: FragmentNewsActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MainActivity.SaveMe2.latestNewsLayoutRestore == null) {
            latestNewsLayout = LinearLayout(requireContext())
            sportNewsLayout = LinearLayout(requireContext())
            economyNewsLayout = LinearLayout(requireContext())
            politicsNewsLayout = LinearLayout(requireContext())
            worldNewsLayout = LinearLayout(requireContext())
            actionsNewsLayout = LinearLayout(requireContext())
        } else {
            latestNewsLayout = MainActivity.SaveMe2.latestNewsLayoutRestore!!
            sportNewsLayout = MainActivity.SaveMe2.sportNewsLayoutRestore!!
            economyNewsLayout = MainActivity.SaveMe2.economyNewsLayoutRestore!!
            politicsNewsLayout = MainActivity.SaveMe2.politicsNewsLayoutRestore!!
            worldNewsLayout = MainActivity.SaveMe2.worldNewsLayoutRestore!!
            actionsNewsLayout = MainActivity.SaveMe2.actionsNewsLayoutRestore!!
        }
        MainActivity.SaveMe2.newsCtx = requireContext()
        SaveMe.context = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MainActivity.SaveMe2.newsCtx = requireContext()
        SaveMe.context = requireContext()
        _binding = FragmentNewsActivityBinding.inflate(inflater, container, false)

        val targetLayout = when (MainActivity.SaveMe2.current) {
            "Últimas" -> SaveMe.latestNewsLayout
            "Desporto" -> SaveMe.sportNewsLayout
            "Economia" -> SaveMe.economyNewsLayout
            "Política" -> SaveMe.politicsNewsLayout
            "Mundo" -> SaveMe.worldNewsLayout
            "Ações" -> SaveMe.actionsNewsLayout
            else -> SaveMe.latestNewsLayout
        }

        (targetLayout.parent as? ViewGroup)?.removeView(targetLayout)
        binding.newsLinearLayout.removeAllViews()
        binding.newsLinearLayout.addView(targetLayout)

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        MainActivity.SaveMe2.latestNewsLayoutRestore = latestNewsLayout
        MainActivity.SaveMe2.sportNewsLayoutRestore = sportNewsLayout
        MainActivity.SaveMe2.economyNewsLayoutRestore = economyNewsLayout
        MainActivity.SaveMe2.politicsNewsLayoutRestore = politicsNewsLayout
        MainActivity.SaveMe2.worldNewsLayoutRestore = worldNewsLayout
        MainActivity.SaveMe2.actionsNewsLayoutRestore = actionsNewsLayout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
