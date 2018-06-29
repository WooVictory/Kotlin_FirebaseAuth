package woo.sopt22.firebaseauth.Database

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_read_database.*
import woo.sopt22.firebaseauth.Adapter.ReadAdapter
import woo.sopt22.firebaseauth.R

class ReadDatabaseActivity : AppCompatActivity() {

    lateinit var readDatabaseAdapter : ReadAdapter
    lateinit var userDTO: UserDTO
    var items = arrayListOf<UserDTO>()
    var realTimeItem = ArrayList<UserDTO>()
    var realTimeKeyItem = ArrayList<String>()
    var listForFilter : ArrayList<UserDTO>? = null
    var city : String?=null
    var age : String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_database)

        userDTO = UserDTO("이승우",26,"인천")
        //items = ArrayList<UserDTO>()


        //readDatabaseAdapter = ReadAdapter(items)
        recyclerviewReadDatabase.adapter = ReadAdapter(items)
        recyclerviewReadDatabase.layoutManager = LinearLayoutManager(this)

        FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener { querySnapshot ->
            for(item in querySnapshot.documents){
                var userDTO = item.toObject(UserDTO::class.java)
                // 이 부분이 약간 Retrofit 라이브러리를 사용해서 통신할 때 JSON -> GSON으로 파싱하는 것과 비슷합니다.
                items.add(userDTO)
            }
            recyclerviewReadDatabase.adapter.notifyDataSetChanged()

        }



        // 실시간으로 데이터를 전송받는 방식
        /*FIXME
        * 아래의 코드는 실시간으로 혹은 계속해서 users라는 collection을 바라보고
        * 데이터가 수정되거나 추가되거나 삭제될 때, 아래의 코드가 발생되게 됩니다.
        * clear가 없다면 데이터가 누적되는 문제가 발생합니다. clear를 넣어줘야 깨끗하게 지우고 다시 만들 수 있습니다.
        * */

        // 이렇게 밖으로 Adapter를 뺍니다.
        //readDatabaseAdapter = ReadAdapter(realTimeItem)
        recyclerviewReadDatabase_Realtime.adapter = ReadAdapter(realTimeItem)
        recyclerviewReadDatabase_Realtime.layoutManager = LinearLayoutManager(this)

/*        FirebaseFirestore.getInstance().collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            realTimeItem.clear()
            for(item in querySnapshot.documents){
                var userDTO = item.toObject(UserDTO::class.java)
                // 이 부분이 약간 Retrofit 라이브러리를 사용해서 통신할 때 JSON -> GSON으로 파싱하는 것과 비슷합니다.
                realTimeItem.add(userDTO)
            }*/

        FirebaseFirestore.getInstance().collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            for(item in querySnapshot.documentChanges){
                when(item.type){
                    // item type을 필터링함
                    // 각각의 이벤트를 처리하게 되면 시스템에 차지하는 리소스가 줄어들게 됩니다.
                    DocumentChange.Type.ADDED -> {
                        // 데이터가 추가될 때 호출이 되지만 예외적으로 처음에 시작할 때는 모든 데이터를 Added 해서 쌓아두고 시작합니다.
                        realTimeItem.add(item.document.toObject(UserDTO::class.java))
                        // Object UserDTO만 담는다.
                        realTimeKeyItem.add(item.document.id)
                        // firebase에서 각 도큐먼트의 key값만 담는 ArrayList

                        /*FIXME
                        * 문서의 이름이랑 문서의 내용이랑 각각 다른 ArrayList에 담아주는 이유는
                        * 같은 position 안에 귀속되게 하기 위함입니다.
                        *
                        * */
                    }

                    DocumentChange.Type.MODIFIED -> {
                        modifyItem(item.document.id, item.document.toObject(UserDTO::class.java))
                    }
                    DocumentChange.Type.REMOVED -> {
                        deleteItem(item.document.id)
                    }
                }
            }

            /*FIXME
            * Adapter가 이 위치에 있다면 데이터가 수정되거나 추가되거나 삭제될 때 Adapter를 계속해서 만들게 됩니다.
            * 이로 인해서 메로리 누수가 발생될 수 있습니다.
            * 그래서 이 위치가 아닌 밖으로 뺀 다음에 Adapter를 새로고침하는 코드를 넣으면 됩니다.
            * 그리고 notifyDataSetChanged가 새로고침 하는 코드
            *
            * */
            recyclerviewReadDatabase_Realtime.adapter.notifyDataSetChanged()
/*            readDatabaseAdapter = ReadAdapter(realTimeItem)
            recyclerviewReadDatabase_Realtime.adapter = readDatabaseAdapter
            recyclerviewReadDatabase_Realtime.layoutManager = LinearLayoutManager(this)*/


            read_database_spinner_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    city = parent!!.getItemAtPosition(position) as String
                    listBySpinner()
                }
            }

            read_database_spinner_age.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    age = parent!!.getItemAtPosition(position) as String
                    listBySpinner()
                }

            }


            read_database_activity_editText.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                    searchList(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

            })

        }


    }

    fun searchList(filterString : String){

        /*FIXME
        * filter 문은 listForFilter에 대한 loop문이라고 생각하면 됩니다.
        * 괄호 안에 조건문을 확인해서 조건에 맞는 아이템들을 filterList에 담습니다.
        * */
        var filterList = listForFilter!!.filter { userDTO ->
            //userDTO.name!!.contains(filterString)
            checkCharacter(userDTO.name!!, filterString)
        }

        items.clear()
        items.addAll(filterList)
        recyclerviewReadDatabase.adapter.notifyDataSetChanged()

    }

    fun checkCharacter(name : String, searchString : String) : Boolean{
        // John Sophia -> arrayOf("John","Sophia")
        var array = searchString.split(" ")
        for(item in array){
            if(name.contains(item)){
                return true
            }
        }
        return false

    }
    fun listBySpinner(){
        if(city !=null && age !=null){
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .whereEqualTo("city",city)
                    .whereGreaterThanOrEqualTo("age",age!!.toInt())
                    .get().addOnSuccessListener { querySnapshot ->
                        items.clear()
                        for(item in querySnapshot.documents){
                            var userDTO = item.toObject(UserDTO::class.java)
                            items.add(userDTO)
                        }
                        // spinner로 선택이 끝나고 나서 items가 다 만들어졌을 때 넣어줍니다.
                        // clone을 하지 않으면 items의 주소값이 복사되고, clone을 해줘야지 데이터 모두가 복사됩니다.
                        listForFilter = items.clone() as ArrayList<UserDTO>
                        recyclerviewReadDatabase.adapter.notifyDataSetChanged()
                    }.addOnFailureListener {
                        exception ->
                        println(exception.toString())
                    }
        }
    }

    fun modifyItem(modifyItem : String, userDTO: UserDTO){
        for((position, item) in realTimeKeyItem.withIndex()){
            if(item == modifyItem){
                realTimeItem[position] = userDTO
            }
        }

    }
    fun deleteItem(deleteKey : String){
        // for문에 position과 item을 같이 담을 수 있다.
        for((position,item) in realTimeKeyItem.withIndex()){
            if(deleteKey == item){
                realTimeItem.removeAt(position)
            }
        }
    }
}
