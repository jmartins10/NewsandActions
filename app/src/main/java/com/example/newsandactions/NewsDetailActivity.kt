package com.example.newsandactions

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class NewsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalhe da Notícia"

        val titleText = intent.getStringExtra("title") ?: ""
        val descText = intent.getStringExtra("description") ?: ""
        val contentText = intent.getStringExtra("content") ?: ""
        val imageUrl = intent.getStringExtra("image_url")
        val category = intent.getStringExtra("category") ?: ""

        val imgView = findViewById<ImageView>(R.id.detailImage)
        val titleView = findViewById<TextView>(R.id.detailTitle)
        val textView = findViewById<TextView>(R.id.detailContent)

        val stockCardView = findViewById<CardView>(R.id.stockCardView)
        val stockChartView = findViewById<ImageView>(R.id.stockChartView)
        val stockCurrentValueText = findViewById<TextView>(R.id.stockCurrentValueText)
        val stockStatsText = findViewById<TextView>(R.id.stockStatsText)

        titleView.text = titleText
        textView.text = contentText.ifEmpty { descText }

        // Load image asynchronously
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
                        runOnUiThread {
                            imgView.setImageBitmap(bitmap)
                        }
                    }
                } catch (_: Exception) {
                    // Keep default
                }
            }.start()
        }

        // Check if this news item belongs to Stock Market / Ações
        val isStock = category.equals("Ações", ignoreCase = true) ||
                titleText.contains("pts", ignoreCase = true) ||
                titleText.contains("€", ignoreCase = true) ||
                titleText.contains(".LS", ignoreCase = true) ||
                descText.contains("Cotação", ignoreCase = true)

        if (isStock) {
            stockCardView.visibility = View.VISIBLE

            val isPositive = !titleText.contains("-") && (titleText.contains("+") || descText.contains("+"))

            // Format current stock value text
            val formattedValue = if (titleText.contains("—")) {
                titleText.substringAfter("—").trim()
            } else if (titleText.contains("|")) {
                titleText.substringAfter("|").trim()
            } else {
                titleText
            }

            stockCurrentValueText.text = formattedValue
            val colorRes = if (isPositive) Color.parseColor("#2E7D32") else Color.parseColor("#C62828")
            stockCurrentValueText.setTextColor(colorRes)

            stockStatsText.text = if (descText.isNotEmpty()) descText else "Informação em tempo real — Euronext Lisbon"

            // Render custom stock chart bitmap
            val chartBitmap = drawStockChartBitmap(isPositive)
            stockChartView.setImageBitmap(chartBitmap)
        } else {
            stockCardView.visibility = View.GONE
        }
    }

    private fun drawStockChartBitmap(isPositive: Boolean): Bitmap {
        val width = 800
        val height = 400
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        canvas.drawColor(Color.parseColor("#FAF9F6"))

        val linePaint = Paint().apply {
            color = if (isPositive) Color.parseColor("#00C853") else Color.parseColor("#FF1744")
            strokeWidth = 6f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val gridPaint = Paint().apply {
            color = Color.parseColor("#E0E0E0")
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }

        val textPaint = Paint().apply {
            color = Color.parseColor("#888888")
            textSize = 22f
            isAntiAlias = true
        }

        val fillPaint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                if (isPositive) Color.parseColor("#3500C853") else Color.parseColor("#35FF1744"),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }

        val timeLabels = listOf("09:00", "11:00", "13:00", "15:00", "17:30")
        val padding = 60f
        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        // Draw horizontal grid lines
        for (i in 0..3) {
            val y = padding + (chartHeight / 3) * i
            canvas.drawLine(padding, y, width - padding, y, gridPaint)
        }

        // Draw vertical grid lines & time labels
        for (i in timeLabels.indices) {
            val x = padding + (chartWidth / (timeLabels.size - 1)) * i
            canvas.drawLine(x, padding, x, height - padding, gridPaint)
            canvas.drawText(timeLabels[i], x - 30f, height - 15f, textPaint)
        }

        // Generate stock line curve
        val path = Path()
        val fillPath = Path()

        val startY = if (isPositive) padding + chartHeight * 0.75f else padding + chartHeight * 0.25f
        val endY = if (isPositive) padding + chartHeight * 0.2f else padding + chartHeight * 0.8f

        fillPath.moveTo(padding, height - padding)
        path.moveTo(padding, startY)
        fillPath.lineTo(padding, startY)

        val steps = 8
        var lastX = padding
        var lastY = startY

        for (i in 1..steps) {
            val x = padding + (chartWidth / steps) * i
            val progress = i.toFloat() / steps
            val baseInterpolation = startY + (endY - startY) * progress
            val noise = (Math.sin(i * 1.5) * 30f).toFloat()
            val y = (baseInterpolation + noise).coerceIn(padding + 10f, height - padding - 10f)

            path.lineTo(x, y)
            fillPath.lineTo(x, y)

            lastX = x
            lastY = y
        }

        fillPath.lineTo(width - padding, height - padding)
        fillPath.close()

        canvas.drawPath(fillPath, fillPaint)
        canvas.drawPath(path, linePaint)

        // Draw current price dot at the end of the line
        val dotPaint = Paint().apply {
            color = if (isPositive) Color.parseColor("#00C853") else Color.parseColor("#FF1744")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(lastX, lastY, 12f, dotPaint)

        val whiteDotPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(lastX, lastY, 6f, whiteDotPaint)

        return bitmap
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
