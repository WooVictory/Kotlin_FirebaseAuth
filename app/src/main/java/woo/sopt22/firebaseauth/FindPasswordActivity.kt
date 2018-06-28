package woo.sopt22.firebaseauth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_find_password.*
import woo.sopt22.firebaseauth.Util.ToastMaker

class FindPasswordActivity : AppCompatActivity(),View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!){
            findBtn->{
                findPassword()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)
        findBtn.setOnClickListener(this)
    }
    fun findPassword(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(editTextEmail.text.toString())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        ToastMaker.makeShortToast(this, "비밀번호 재설정 메일을 보냈습니다.")
                    } else{
                        ToastMaker.makeShortToast(this, task.exception.toString())
                    }
                }
    }

}
