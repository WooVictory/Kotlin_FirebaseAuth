package woo.sopt22.firebaseauth

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
            findPasswordBtn->{
                startActivity(Intent(this, FindPasswordActivity::class.java))
            }

        }
    }

    var authStateListener : FirebaseAuth.AuthStateListener?=null
    var callbackManager : CallbackManager?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginBtn.setOnClickListener(this)
        signBtn.setOnClickListener(this)
        findPasswordBtn.setOnClickListener(this)
        //googleLoginBtn.setOnClickListener(this)

        // 페이스북 로그인이 완료되었을 때 토큰을 받는 부
        callbackManager = CallbackManager.Factory.create()

        facebookBtn.setReadPermissions("email","public_profile")
        facebookBtn.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                /*FIXME
                * 성공된 값이 이쪽으로 넘어와서
                * 이것을 파이어베이스에 페이스북 아이디를 입력하게 됩니다.
                * */

                // 로그인 성공시 발급된 인증서

                var credentail = FacebookAuthProvider.getCredential(result!!.accessToken.token)
                FirebaseAuth.getInstance().signInWithCredential(credentail)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                ToastMaker.makeShortToast(this@MainActivity,"페이스북 연동이 성공하였습니다. ")
                            } else{
                                ToastMaker.makeShortToast(this@MainActivity,task.exception.toString())
                            }
                        }
            }

            override fun onCancel() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(error: FacebookException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })



        // 로그인 세션을 체크하는 부분입니다.
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            var user = firebaseAuth.currentUser
            // 유저 정보가 있으면 HomeActivity로 이
            if(user !=null){
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }

        // 구글 로그인 옵션을 설정한 것입니다.
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // 구글 로그인에 사용할 API 키 값
                .requestEmail() // 이메일을 요청
                .build()

        // 요청한 것을 옵션에 담아가지고 구글 로그인 클래스를 만든 것입니다.
        var googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleLoginBtn.setOnClickListener {
            // 구글 로그인 클래스에 있는 Intent를 추출해서
            // 추출한 것을 startActivityForResult로 실행한 것입니다.
            var signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent,1)

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

    override fun onPause() {
        super.onPause()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        callbackManager!!.onActivityResult(requestCode, resultCode,data)

        // 페이스북 로그인이 성공했을 경우 결과값이 이쪽으로 넘어오기 때문에(onActivityResult)
        // 넘어온 값을 callbackManager에 넘겨줘서 이 넘겨진 값을 onSuccess 부분에 값을 넘겨줍니다.



        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            // 선택한 아이디에 대한 정보를 가져옵니다.
            /*FIXME
            * task : 구글 로그인에 성공했을 때 넘어오는 토큰값을 가지고 있는 task
            * 구글 로그인에 성공했을 때 토큰 값을 넘겨주는데 이것을 담고 있는 것입니다.
            * 그리고 이것은 ApiException라는 것으로 캐스팅을 해주고
            * 캐스팅한 것을 credential [구글 로그인에 성공했다라는] (인증)로 만들어줍니다.
            * 만들어준 인증서를 Firebase에 넘겨줘야지 Firebase가 구글 아이디를 등록하게 됩니다.
            * 이렇게 넘겨줘야지 Firebase에 구글 사용자가 등록되게 됩니다.
            * */
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            var account = task.getResult(ApiException::class.java)
            var credential =  GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    ToastMaker.makeShortToast(this,"구글 아이디 연동이 완료되었습니다.")
                } else{
                    ToastMaker.makeShortToast(this, task.exception.toString())
                }
            }
        }
    }

}
