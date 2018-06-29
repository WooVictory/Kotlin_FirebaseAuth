package woo.sopt22.firebaseauth.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import woo.sopt22.firebaseauth.Database.UserDTO
import woo.sopt22.firebaseauth.R

class ReadAdapter(initItems : ArrayList<UserDTO>) : RecyclerView.Adapter<ReadAdapter.ReadViewHolder>(){

    private lateinit var items : ArrayList<UserDTO>
    init {
        items = initItems
    }
    override fun onBindViewHolder(holder: ReadViewHolder, position: Int) {
        holder.name.text = items[position].name
        holder.age.text = items[position].age.toString()
        holder.city.text = items[position].city
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadViewHolder{
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_recycler_view, parent,false)
        return ReadViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }


    inner class ReadViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var name = itemView.findViewById<TextView>(R.id.textView_name)
        var age = itemView.findViewById<TextView>(R.id.textView_age)
        var city = itemView.findViewById<TextView>(R.id.textView_city)




    }
}

