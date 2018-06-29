package woo.sopt22.firebaseauth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_storage.*
import woo.sopt22.firebaseauth.Util.ToastMaker
import java.io.ByteArrayOutputStream

class StorageActivity : AppCompatActivity(), View.OnClickListener {


    var bitmap : Bitmap? = null

    override fun onClick(v: View?) {
        when(v!!){
            storageImageLocal->{
                image()
            }
            uploadBtn->{
                if(bitmap !=null){
                    uploadImage(bitmap!!)
                }

            }
            deleteBtn->{
                deleteImgae()
            }
        }
    }

    fun uploadImage(bitmap: Bitmap){
        var baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100,baos)
        var data = baos.toByteArray()
        FirebaseStorage.getInstance().reference.child("users")
                .child(storage_filename.text.toString()).putBytes(data)
                .addOnCompleteListener { task ->
            if(task.isSuccessful){
                ToastMaker.makeShortToast(this,"업로드에 성공하였습니다.")
                savceToUrlDatabase(task.result.downloadUrl.toString())
                // 업로드가 성공된 이미지 주소에 대한 url이 넘어오게 됩니다.

            }
        }

    }

    fun savceToUrlDatabase(url : String){
        var map = HashMap<String, Any>()
        map["profile_image_url"] = url
        FirebaseFirestore.getInstance().collection("users").document(storage_document_id.text.toString()).update(map)
    }

    fun deleteImgae(){
        FirebaseStorage.getInstance().reference.child("users").child(storage_filename.text.toString()).delete().addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                ToastMaker.makeShortToast(this,"삭제가 완료 되었습니다.")
                var map = HashMap<String, Any>()
                map["profile_image_url"] = FieldValue.delete()
                FirebaseFirestore.getInstance().collection("user").document(storage_document_id.text.toString()).update(map)
            }
        }
    }

    fun image(){
        var intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            var imageUrl = data!!.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUrl)
            storageImageLocal.setImageBitmap(bitmap)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        storageImageLocal.setOnClickListener(this)
        uploadBtn.setOnClickListener(this)
        deleteBtn.setOnClickListener(this)
    }


}
