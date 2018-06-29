package woo.sopt22.firebaseauth.Database

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_database.*
import woo.sopt22.firebaseauth.R

class DatabaseActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!){
            databaseInputBtn->{
                startActivity(Intent(this, DatabaseCreateActivity::class.java))
            }
            datebaseReadBtn->{
                startActivity(Intent(this, ReadDatabaseActivity::class.java))
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)
        databaseInputBtn.setOnClickListener(this)
        datebaseReadBtn.setOnClickListener(this)
    }
}
