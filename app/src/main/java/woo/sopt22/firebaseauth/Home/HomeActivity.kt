package woo.sopt22.firebaseauth.Home

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import woo.sopt22.firebaseauth.R
import woo.sopt22.firebaseauth.Util.ToastMaker
import java.net.PasswordAuthentication

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!){
            logoutBtn->{
                FirebaseAuth.getInstance().signOut()
                finish()
            }
            changePwBtn->{
                var editTextNewPassword = EditText(this)
                editTextNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                // 입력된 글자가 자동적으로 *로 표시되도록 함
                var alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Password 변경")
                alertDialog.setMessage("변경하고 싶은 Password를 입력하세요.")
                alertDialog.setView(editTextNewPassword)
                alertDialog.setPositiveButton("변경",{dialog, which ->
                    changePassword(editTextNewPassword.text.toString())

                })
                alertDialog.setNegativeButton("취소",{dialog, which ->
                    dialog.dismiss()
                })


                alertDialog.show()
            }
        }
    }

    fun changePassword(password : String){
        FirebaseAuth.getInstance().currentUser!!.updatePassword(password)
                .addOnCompleteListener{ task ->
            if(task.isSuccessful){
                ToastMaker.makeShortToast(this, "비밀번호가 변경되었습니다.")
            } else{
                ToastMaker.makeShortToast(this, task.exception.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        logoutBtn.setOnClickListener(this)
        changePwBtn.setOnClickListener(this)
    }
}
