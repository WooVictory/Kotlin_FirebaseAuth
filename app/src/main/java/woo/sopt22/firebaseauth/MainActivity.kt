package woo.sopt22.firebaseauth

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import woo.sopt22.firebaseauth.Home.HomeActivity
import woo.sopt22.firebaseauth.Util.ToastMaker

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!){
            loginBtn->{
                loginId()
            }
            signBtn->{
                createEmailId()
            }
        }
    }

    var authStateListener : FirebaseAuth.AuthStateListener?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginBtn.setOnClickListener(this)
        signBtn.setOnClickListener(this)

        // 로그인 세션을 체크하는 부분입니다.
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            var user = firebaseAuth.currentUser
            // 유저 정보가 있으면 HomeActivity로 이
            if(user !=null){
                startActivity(Intent(this, HomeActivity::class.java))
            }

        }
    }
    fun createEmailId(){
        /*FIXME
        * addOnCompleteListener를 통해서 결과값을 받아옵니다.
        * 람다식을 이용해서 바로 진행할 수 있도록 합니다.
        * */
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(editTextEmail.text.toString()
                , editTextPwd.text.toString()).addOnCompleteListener { task ->
            if(task.isSuccessful){
                ToastMaker.makeShortToast(this, "회원가입에 성공하셨습니다.")
            } else{
                ToastMaker.makeShortToast(this, task.exception.toString())
            }
        }

    }
    fun loginId(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(editTextEmail.text.toString()
                , editTextPwd.text.toString()).addOnCompleteListener { task ->
            if(task.isSuccessful){
                // startActivity(Intent(this, HomeActivity::class.java))
                /*FIXME
                * 이렇게 로그인 하고 나서 바로 HomeActivity로 넘어가는 것이 더 낫다고 생각할 수 있습니다.
                * 하지만, 이런 방식으로 짤 경우에는
                * */
                ToastMaker.makeShortToast(this, "로그인에 성공하셨습니다.")
            } else{
                ToastMaker.makeShortToast(this, task.exception.toString())
            }
        }
    }

    /*FIXME
    * authStateListener를 App이 가동될때 FirebaseAuth에 붙여주게 됩니다.
    * 붙여주면서 바로 로그인이 되어서 유저 정보가 있는지 체크해서 유저 정보가 있으면 바로 다음 화면으로 넘어가도록 합니다.
    * */
    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener!!)
    }

}
