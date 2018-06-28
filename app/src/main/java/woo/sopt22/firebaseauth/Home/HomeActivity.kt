package woo.sopt22.firebaseauth.Home

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import woo.sopt22.firebaseauth.R
import woo.sopt22.firebaseauth.Util.ToastMaker
import java.net.PasswordAuthentication

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!){
            logoutBtn->{
                // 로그아웃을 하면 페이스북 세션이 자동으로 지워지게 된다.
                LoginManager.getInstance().logOut()
                FirebaseAuth.getInstance().signOut()
                finish()
            }
            changePwBtn->{
                changPasswordDialog()
            }
            checkEmailBtn->{
                sendEmailVerification()
            }
            changeIdBtn->{
                changeIdDialog()
            }
            deleteBtn->{
                deleteId()
            }
        }
    }

    fun deleteIdDialog(){
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("알림")
        alertDialog.setMessage("삭제하시겠습니까?")
        alertDialog.setPositiveButton("확인",{dialog, which ->
            deleteId()
        })
        alertDialog.setNegativeButton("취소",{dialog, which ->

        })
        alertDialog.show()
    }

    fun deleteId(){
        FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener { task ->
            if(task.isSuccessful){
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                ToastMaker.makeShortToast(this,"아이디 삭제가 완료되습니다.")
                finish()
            } else{
                ToastMaker.makeShortToast(this,task.exception.toString())
            }
        }
    }

    fun changeIdDialog(){
        var editTextNewId = EditText(this)
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(editTextNewId)
        alertDialog.setTitle("아이디 변경")
        alertDialog.setMessage("변경하고 싶은 아이디를 입력해주세요.")
        alertDialog.setPositiveButton("확인",{dialog, which ->
            changeId(editTextNewId.text.toString())
        })
        alertDialog.setNegativeButton("취소",{dialog, which ->

        })
        alertDialog.show()

    }

    fun changeId(email : String){
        FirebaseAuth.getInstance().currentUser!!.updateEmail(email)
                .addOnCompleteListener { task ->
            if(task.isSuccessful){
                ToastMaker.makeShortToast(this,"이메일이 변경이 완룓되었습니다.")
            } else{
                ToastMaker.makeShortToast(this, task.exception.toString())
            }
        }
    }

    fun changPasswordDialog(){
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

    fun sendEmailVerification(){
        // 이메일 인증하는 버튼 활성화를 끕니다.
        if(FirebaseAuth.getInstance().currentUser!!.isEmailVerified){
            ToastMaker.makeShortToast(this, "이메일 인증이 완료되었습니다.")
            return
            // 이렇게 하게 되면 아래 코드는 무시되게 됩니다.
        }
        FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()
                .addOnCompleteListener { task ->
            if(task.isSuccessful){
                ToastMaker.makeShortToast(this, "확인 메일을 보냈습니다.")
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
        checkEmailBtn.setOnClickListener(this)
        changeIdBtn.setOnClickListener(this)
        deleteBtn.setOnClickListener(this)
    }
}
