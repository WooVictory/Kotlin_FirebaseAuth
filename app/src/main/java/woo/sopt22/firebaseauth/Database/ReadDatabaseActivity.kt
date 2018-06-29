package woo.sopt22.firebaseauth.Database

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_read_database.*
import woo.sopt22.firebaseauth.Adapter.ReadAdapter
import woo.sopt22.firebaseauth.R

class ReadDatabaseActivity : AppCompatActivity() {

    lateinit var readDatabaseAdapter : ReadAdapter
    lateinit var userDTO: UserDTO
    lateinit var items : ArrayList<UserDTO>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_database)

        userDTO = UserDTO("이승우",26,"인천")
        items = ArrayList<UserDTO>()

        FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener { querySnapshot ->
            for(item in querySnapshot.documents){
                var userDTO = item.toObject(UserDTO::class.java)
                // 이 부분이 약간 Retrofit 라이브러리를 사용해서 통신할 때 JSON -> GSON으로 파싱하는 것과 비슷합니다.
                items.add(userDTO)
            }

            readDatabaseAdapter = ReadAdapter(items)
            recyclerviewReadDatabase.adapter = readDatabaseAdapter
            recyclerviewReadDatabase.layoutManager = LinearLayoutManager(this)
        }



    }
}
