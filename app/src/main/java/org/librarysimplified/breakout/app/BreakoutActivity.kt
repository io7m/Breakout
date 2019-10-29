package org.librarysimplified.breakout.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BreakoutActivity : AppCompatActivity() {

  private lateinit var breakoutView: BreakoutView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    this.setTheme(R.style.Blank)
    this.setContentView(R.layout.main)

    this.breakoutView = this.findViewById(R.id.breakoutView)
  }
}