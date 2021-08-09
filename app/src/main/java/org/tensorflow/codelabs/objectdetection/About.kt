package org.tensorflow.codelabs.objectdetection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class About : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_layout)

        val fbGod: ImageView = findViewById(R.id.fb_god)
        val creator: ImageView = findViewById(R.id.creator)
        val flatIcon: CardView = findViewById(R.id.puma_cat)
        val feedback: CardView = findViewById(R.id.feedback)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = getString(R.string.About)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        fbGod.setOnClickListener {
            val uri: Uri = Uri.parse("https://github.com/FbGod")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        creator.setOnClickListener {
            val uri: Uri = Uri.parse("https://github.com/7CreAtoR7")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        flatIcon.setOnClickListener {
            val uri: Uri = Uri.parse("https://www.flaticon.com/")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        feedback.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "iljalisizki@gmail.com", null)
            )
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
            startActivity(Intent.createChooser(intent, "Select post client"))
        }

    }
}
