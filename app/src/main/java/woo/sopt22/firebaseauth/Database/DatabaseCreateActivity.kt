package woo.sopt22.firebaseauth.Database

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_database_create.*
import woo.sopt22.firebaseauth.R
import woo.sopt22.firebaseauth.Util.ToastMaker

class DatabaseCreateActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!){
            dataBaseCreateInputBtn->{
                createDate()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_create)

        dataBaseCreateInputBtn.setOnClickListener(this)
    }

    fun createDate(){
        var userDTO = UserDTO(editTextDatabaseName.text.toString()
                , editTextDatabaseAge.text.toString().toInt(), editTextDatabaseCity.text.toString())
        FirebaseFirestore.getInstance().collection("users").document().set(userDTO).addOnCompleteListener {
            ToastMaker.makeShortToast(this, "데이터 입력이 성공하였습니다.")
        }.addOnFailureListener { exception ->
            ToastMaker.makeShortToast(this,exception.toString())
        }
    }
}
